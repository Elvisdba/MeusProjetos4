package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.*;
import br.com.rtools.associativo.db.*;
import br.com.rtools.financeiro.Evt;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.db.ServicosDB;
import br.com.rtools.financeiro.db.ServicosDBToplink;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.SalvarAcumuladoDB;
import br.com.rtools.utilitarios.SalvarAcumuladoDBToplink;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class CaravanaBean implements Serializable {

    private Caravana caravana;
    private Servicos servicos;
    private EventoServico eventoServico;
    private EventoServicoValor eventoServicoValor;
    private String msgConfirma;
    private Integer idDescricaoEvento;
    private Integer idGrupoEvento;
    private Integer idServicos;
    private Integer idIndex;
    private Integer idIndexServicos;
    private String valor;
    private List<DataObject> listaServicosAdd;
    private List<Caravana> listaCaravana;
    private boolean habilitado;

    private final List<SelectItem> listaDescricaoEvento = new ArrayList();
    private final List<SelectItem> listaGrupoEvento = new ArrayList();
    private final List<SelectItem> listaServicos = new ArrayList();

    @PostConstruct
    public void init() {
        caravana = new Caravana();
        servicos = new Servicos();
        eventoServico = new EventoServico();
        eventoServicoValor = new EventoServicoValor();
        msgConfirma = "";
        idDescricaoEvento = 0;
        idGrupoEvento = 0;
        idServicos = 0;
        idIndex = -1;
        idIndexServicos = -1;
        valor = "0.0";
        listaServicosAdd = new ArrayList();
        listaCaravana = new ArrayList();
        habilitado = true;

        loadListaServicos();
        loadListaGrupoEvento();
        loadListaDescricaoEvento();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("caravanaBean");
    }
    
    public void loadListaServicos() {
        listaServicos.clear();
        
        ServicosDB db = new ServicosDBToplink();
        List<Servicos> select = db.pesquisaTodos(138);
        for (int i = 0; i < select.size(); i++) {
            listaServicos.add(
                    new SelectItem(
                            i,
                            select.get(i).getDescricao(),
                            Integer.toString(select.get(i).getId())
                    )
            );
        }
    }    

    public void loadListaGrupoEvento() {
        listaGrupoEvento.clear();
        SalvarAcumuladoDB sadb = new SalvarAcumuladoDBToplink();
        List<GrupoEvento> select = (List<GrupoEvento>) sadb.listaObjeto("GrupoEvento");
        for (int i = 0; i < select.size(); i++) {
            listaGrupoEvento.add(
                    new SelectItem(
                            i,
                            select.get(i).getDescricao(),
                            Integer.toString(select.get(i).getId())
                    )
            );
        }
    }

    public void loadListaDescricaoEvento() {
        listaDescricaoEvento.clear();

        if (listaGrupoEvento.isEmpty()) {
            return;
        }

//        DescricaoEventoDB db = new DescricaoEventoDBToplink();
//        List<DescricaoEvento> select = db.pesquisaDescricaoPorGrupo(Integer.parseInt(listaGrupoEvento.get(idGrupoEvento).getDescription()));
        
        DescricaoEventoDB db = new DescricaoEventoDBToplink();
        List<DescricaoEvento> select = db.pesquisaDescricaoPorGrupo(2);
        for (int i = 0; i < select.size(); i++) {
            listaDescricaoEvento.add(
                    new SelectItem(
                            i,
                            select.get(i).getDescricao(),
                            Integer.toString(select.get(i).getId())
                    )
            );
        }
    }

    public Boolean validaSalvar(){
        if (listaDescricaoEvento.isEmpty()) {
            msgConfirma = "Cadastrar descrição de eventos!";
            return false;
        }
        
        if (caravana.getDataSaida().isEmpty()){
            msgConfirma = "Digite uma Data de Saída!";
            return false;
        }
        
        if (caravana.getHoraSaida().isEmpty()){
            msgConfirma = "Digite um Horário de Saída!";
            return false;
        }        
        
        if (caravana.getDataChegada().isEmpty()){
            msgConfirma = "Digite uma Data de Chegada!";
            return false;
        }
        
        if (caravana.getHoraChegada().isEmpty()){
            msgConfirma = "Digite um Horário de Chegada!";
            return false;
        }
        
        if (caravana.getDataRetorno().isEmpty()){
            msgConfirma = "Digite uma Data de Retorno!";
            return false;
        }
        
        if (caravana.getHoraRetorno().isEmpty()){
            msgConfirma = "Digite um Horário de Retorno!";
            return false;
        }
        
        return true;
    }
    
    
    public String salvar() {
        if (!validaSalvar()){
            GenericaMensagem.warn("Atenção", msgConfirma);
            return null;
        }
        
        SalvarAcumuladoDB sv = new SalvarAcumuladoDBToplink();
        AEvento aEvento = new AEvento();
        sv.abrirTransacao();
        
        DescricaoEvento de = (DescricaoEvento) sv.find(new DescricaoEvento(), Integer.parseInt(getListaDescricaoEvento().get(idDescricaoEvento).getDescription()));
        if (caravana.getId() == -1) {
            Evt evt = new Evt();
            if (!sv.inserirObjeto(evt)) {
                msgConfirma = "Erro ao salvar EVT!";
                GenericaMensagem.warn("Erro", msgConfirma);
                sv.desfazerTransacao();
                return null;
            }
            caravana.setEvt(evt);
            
            aEvento.setDescricaoEvento(de);
            if (!sv.inserirObjeto(aEvento)) {
                msgConfirma = "Erro ao salvar Evento!";
                GenericaMensagem.warn("Erro", msgConfirma);
                sv.desfazerTransacao();
                return null;
            }

            caravana.setaEvento(aEvento);
            if (!sv.inserirObjeto(caravana)) {
                msgConfirma = "Erro ao salvar caravana!";
                GenericaMensagem.warn("Erro", msgConfirma);
                sv.desfazerTransacao();
                return null;
            } else {
                msgConfirma = "Caravana salva com sucesso!";
                GenericaMensagem.info("Sucesso", msgConfirma);
                sv.comitarTransacao();
                return null;
            }
        } else {
            if (caravana.getEvt() == null){
                Evt evt = new Evt();
                if (!sv.inserirObjeto(evt)) {
                    msgConfirma = "Erro ao salvar EVT!";
                    GenericaMensagem.warn("Erro", msgConfirma);
                    sv.desfazerTransacao();
                    return null;
                }
                caravana.setEvt(evt);
            }
            aEvento = (AEvento) sv.find(new AEvento(), caravana.getaEvento().getId());
            aEvento.setDescricaoEvento(de);
            if (!sv.alterarObjeto(aEvento)) {
                msgConfirma = "Erro ao atualizar Evento!";
                GenericaMensagem.warn("Erro", msgConfirma);
                sv.desfazerTransacao();
                return null;
            }
            caravana.setaEvento(aEvento);
            if (!sv.alterarObjeto(caravana)) {
                msgConfirma = "Erro ao atulizar caravana!";
                GenericaMensagem.warn("Erro", msgConfirma);
                sv.desfazerTransacao();
                return null;
            } else {
                msgConfirma = "Caravana atualizada com sucesso!";
                GenericaMensagem.info("Sucesso", msgConfirma);
                sv.comitarTransacao();
                return null;
            }
        }
    }

    public String adicionarServico() {
        if (getListaServicos().isEmpty()) {
            msgConfirma = "Não existe nenhum serviço para ser adicionado!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }
        
        if (caravana.getId() == -1){
            msgConfirma = "Salve a Caravana antes de adicionar serviços!";
            GenericaMensagem.warn("Atenção", msgConfirma);
            return null;
        }
        
        SalvarAcumuladoDB sv = new SalvarAcumuladoDBToplink();
        sv.abrirTransacao();
//        if (caravana.getId() == -1) {
//            AEvento aEvento = new AEvento();
//            aEvento.setDescricaoEvento((DescricaoEvento) sv.find(new DescricaoEvento(), Integer.parseInt(getListaDescricaoEvento().get(idDescricaoEvento).getDescription())));
//            if (!sv.inserirObjeto(aEvento)) {
//                msgConfirma = "Erro ao salvar Evento!";
//                GenericaMensagem.warn("Erro", msgConfirma);
//                sv.desfazerTransacao();
//                return null;
//            }
//
//            caravana.setaEvento(aEvento);
//            if (!sv.inserirObjeto(caravana)) {
//                msgConfirma = "Erro ao salvar caravana!";
//                GenericaMensagem.warn("Erro", msgConfirma);
//                sv.desfazerTransacao();
//                return null;
//            }
//        }

        servicos = new Servicos();
        servicos = (Servicos) sv.find(new Servicos(), Integer.parseInt(getListaServicos().get(idServicos).getDescription()));
        float vl = 0;
        eventoServico.setaEvento(caravana.getaEvento());
        eventoServico.setServicos(servicos);
        if (!sv.inserirObjeto(eventoServico)) {
            msgConfirma = "Erro ao inserir Evento Serviço!";
            GenericaMensagem.warn("Erro", msgConfirma);
            sv.desfazerTransacao();
            return null;
        }
        eventoServicoValor.setEventoServico(eventoServico);
        vl = Float.valueOf(valor);
        eventoServicoValor.setValor(vl);
        if (!sv.inserirObjeto(eventoServicoValor)) {
            msgConfirma = "Erro ao inserir Evento Serviço Valor!";
            GenericaMensagem.warn("Erro", msgConfirma);
            sv.desfazerTransacao();
            return null;
        }

        sv.comitarTransacao();
        msgConfirma = "Serviço adicionado!";
        GenericaMensagem.info("Sucesso", msgConfirma);
        if (eventoServico.isIndividual()) {
            listaServicosAdd.add(new DataObject(servicos, eventoServico, eventoServicoValor, eventoServico.isIndividual(), valor, "<< Sim >>"));
        } else {
            listaServicosAdd.add(new DataObject(servicos, eventoServico, eventoServicoValor, eventoServico.isIndividual(), valor, "<< Não >>"));
        }
        eventoServico = new EventoServico();
        eventoServicoValor = new EventoServicoValor();
        valor = "0";
        return null;
    }

    public String excluir() {
        SalvarAcumuladoDB sv = new SalvarAcumuladoDBToplink();
        sv.abrirTransacao();
        if (caravana.getId() != -1) {
            caravana = (Caravana) sv.find(caravana);
            AEvento aEvento = (AEvento) sv.find(caravana.getaEvento());
            if (!listaServicosAdd.isEmpty()) {
                DataObject dtObj = null;
                for (DataObject listaServicosAdd1 : listaServicosAdd) {
                    dtObj = listaServicosAdd1;
                    if (!excluirServicos(sv, dtObj)) {
                        sv.desfazerTransacao();
                        msgConfirma = "Erro ao excluir lista de Serviços!";
                        GenericaMensagem.warn("Erro", msgConfirma);
                        return null;
                    }
                }
            }
            if (!sv.deletarObjeto(caravana)) {
                msgConfirma = "Erro ao excluír caravana!";
                GenericaMensagem.warn("Erro", msgConfirma);
                sv.desfazerTransacao();
                return null;
            }

            if (!sv.deletarObjeto(aEvento)) {
                msgConfirma = "Erro ao excluir Evento!";
                GenericaMensagem.warn("Erro", msgConfirma);
                sv.desfazerTransacao();
                return null;
            } else {
                sv.comitarTransacao();
                msgConfirma = "Caravana excluído com sucesso!";
                caravana = new Caravana();
                GenericaMensagem.info("Sucesso", msgConfirma);
                return null;
            }
        } else {
            msgConfirma = "Pesquise uma caravana antes de excluir!";
            GenericaMensagem.warn("Erro", msgConfirma);
            sv.desfazerTransacao();
            return null;
        }
    }

    public String excluirServicos(DataObject dob) {
        SalvarAcumuladoDB sv = new SalvarAcumuladoDBToplink();
        sv.abrirTransacao();
        DataObject dtObj = dob;//(DataObject) listaServicosAdd.get(idIndexServicos);
        if (excluirServicos(sv, dtObj)) {
            sv.comitarTransacao();
        } else {
            sv.desfazerTransacao();
        }
        return null;
    }

    public boolean excluirServicos(SalvarAcumuladoDB sv, DataObject dtObj) {
        eventoServico = (EventoServico) sv.find(new EventoServico(), ((EventoServico) dtObj.getArgumento1()).getId());
        eventoServicoValor = (EventoServicoValor) sv.find(new EventoServicoValor(), ((EventoServicoValor) dtObj.getArgumento2()).getId());
        if (!sv.deletarObjeto(eventoServicoValor)) {
            msgConfirma = "Erro ao Excluir evento serviço valor!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return false;
        }

        if (!sv.deletarObjeto(eventoServico)) {
            msgConfirma = "Erro ao Excluir evento serviço!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return false;
        } else {
            msgConfirma = "Serviço excluido!";
            GenericaMensagem.info("Sucesso", msgConfirma);
            eventoServico = new EventoServico();
            eventoServicoValor = new EventoServicoValor();
            return true;
        }
    }

    public String novo() {
        GenericaSessao.put("caravanaBean", new CaravanaBean());
        return "caravana";
    }

    public String editar(Caravana car) {
        caravana = car;//(Caravana) listaCaravana.get(idIndex);
        GenericaSessao.put("linkClicado", true);
        for (int i = 0; i < getListaGrupoEvento().size(); i++) {
            if (Integer.parseInt(getListaGrupoEvento().get(i).getDescription()) == caravana.getaEvento().getDescricaoEvento().getGrupoEvento().getId()) {
                idGrupoEvento = i;
            }
        }
        for (int i = 0; i < getListaDescricaoEvento().size(); i++) {
            if (Integer.parseInt(getListaDescricaoEvento().get(i).getDescription()) == caravana.getaEvento().getDescricaoEvento().getId()) {
                idDescricaoEvento = i;
            }
        }
        return "caravana";
    }

    public List<SelectItem> getListaDescricaoEvento() {
        return listaDescricaoEvento;
    }

    public List<SelectItem> getListaGrupoEvento() {
        return listaGrupoEvento;
    }

    public List<SelectItem> getListaServicos() {
        return listaServicos;
    }

    public List<DataObject> getListaServicosAdd() {
        EventoServicoDB dbE = new EventoServicoDBToplink();
        EventoServicoValorDB dbEv = new EventoServicoValorDBToplink();
        if (caravana.getId() != -1) {
            listaServicosAdd.clear();
            List<EventoServico> evs;
            EventoServicoValor ev;
            evs = dbE.listaEventoServico(caravana.getaEvento().getId());
            for (int i = 0; i < evs.size(); i++) {
                ev = dbEv.pesquisaEventoServicoValor(evs.get(i).getId());
                if (evs.get(i).isIndividual()) {
                    listaServicosAdd.add(new DataObject(evs.get(i).getServicos(), evs.get(i), ev, evs.get(i).isIndividual(), Moeda.converteR$Float(ev.getValor()), "<< Sim >>"));
                } else {
                    listaServicosAdd.add(new DataObject(evs.get(i).getServicos(), evs.get(i), ev, evs.get(i).isIndividual(), Moeda.converteR$Float(ev.getValor()), "<< Não >>"));
                }
            }
        }
        return listaServicosAdd;
    }

    public void setListaServicosAdd(List listaServicosAdd) {
        this.listaServicosAdd = listaServicosAdd;
    }

    public void refreshForm() {
    }

    public Caravana getCaravana() {
        return caravana;
    }

    public void setCaravana(Caravana caravana) {
        this.caravana = caravana;
    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

    public EventoServico getEventoServico() {
        return eventoServico;
    }

    public void setEventoServico(EventoServico eventoServico) {
        this.eventoServico = eventoServico;
    }

    public EventoServicoValor getEventoServicoValor() {
        return eventoServicoValor;
    }

    public void setEventoServicoValor(EventoServicoValor eventoServicoValor) {
        this.eventoServicoValor = eventoServicoValor;
    }

    public String getValor() {
        return Moeda.converteR$(valor);
    }

    public void setValor(String valor) {
        this.valor = Moeda.substituiVirgula(valor);
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public List<Caravana> getListaCaravana() {
        if (listaCaravana.isEmpty()) {
            SalvarAcumuladoDB sadb = new SalvarAcumuladoDBToplink();
            listaCaravana = (List<Caravana>) sadb.listaObjeto("Caravana");
        }
        return listaCaravana;
    }

    public void setListaCaravana(List<Caravana> listaCaravana) {
        this.listaCaravana = listaCaravana;
    }

    public Integer getIdDescricaoEvento() {
        return idDescricaoEvento;
    }

    public void setIdDescricaoEvento(Integer idDescricaoEvento) {
        this.idDescricaoEvento = idDescricaoEvento;
    }

    public Integer getIdGrupoEvento() {
        return idGrupoEvento;
    }

    public void setIdGrupoEvento(Integer idGrupoEvento) {
        this.idGrupoEvento = idGrupoEvento;
    }

    public Integer getIdServicos() {
        return idServicos;
    }

    public void setIdServicos(Integer idServicos) {
        this.idServicos = idServicos;
    }

    public Integer getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(Integer idIndex) {
        this.idIndex = idIndex;
    }

    public Integer getIdIndexServicos() {
        return idIndexServicos;
    }

    public void setIdIndexServicos(Integer idIndexServicos) {
        this.idIndexServicos = idIndexServicos;
    }
}
