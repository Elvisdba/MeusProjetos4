package br.com.rtools.sistema.beans;

import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.seguranca.dao.RotinaDao;
import br.com.rtools.seguranca.dao.UsuarioDao;
import br.com.rtools.sistema.SisAutorizacoes;
import br.com.rtools.sistema.SisAutorizacoesTipo;
import br.com.rtools.sistema.dao.SisAutorizacoesDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Messages;
import br.com.rtools.utilitarios.WSSocket;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class SisAutorizacoesBean implements Serializable {

    private SisAutorizacoes sisAutorizacoes;
    private List<SisAutorizacoes> listSisAutorizacoes;
    private String refusedMotive;
    private String filter;
    private String status;
    private String value;
    private List<SelectItem> listTipoAutorizacao;
    private List<SelectItem> listGestores;
    private List<SelectItem> listOperadores;
    private List<SelectItem> listRotinas;
    private Integer idTipoAutorizacao;
    private Integer idGestor;
    private Integer idOperador;
    private Integer idRotina;

    public SisAutorizacoesBean() {
        refusedMotive = "";
        status = "aberto";
        filter = "";
        value = "";
        loadListTipoAutorizacao();
        loadListSisAutorizacoes();
        loadListOperadores();
        loadListRotinas();
        loadListGestores();
    }

    public void accept(SisAutorizacoes sa) {
        SisAutorizacoesDao sad = new SisAutorizacoesDao();
        sa.setGestor(Usuario.getUsuario());
        sa.setDtAutorizacao(new Date());
        sa.setHoraAutorizacao(DataHoje.horaMinuto());
        sa.setAutorizado(true);
        Dao dao = new Dao();
        dao.openTransaction();
        if (!sad.execute(dao, sa)) {
            dao.rollback();
            Messages.warn("Erro", "AO REALIZAR AUTORIZAÇÃO!");
            return;
        }
        Rotina r = new Rotina();
        List<SisAutorizacoes> list = sad.findByAutorizacao(sa.getId());
        for (int i = 0; i < list.size(); i++) {
            if (!sad.execute(dao, list.get(i))) {
                dao.rollback();
                Messages.warn("Erro", "AO REALIZAR AUTORIZAÇÃO!");
                return;
            }
            list.get(i).setGestor(sa.getGestor());
            list.get(i).setDtAutorizacao(sa.getDtAutorizacao());
            list.get(i).setHoraAutorizacao(DataHoje.horaMinuto());
            list.get(i).setAutorizado(true);
            if (!dao.update(list.get(i))) {
                Messages.warn("Erro", "AO REALIZAR AUTORIZAÇÃO!");
                return;
            }
        }
        if (!new Dao().update(sa, true)) {
            dao.rollback();
            Messages.warn("Erro", "AO REALIZAR AUTORIZAÇÃO!");
            return;
        }
        dao.commit();
        Messages.info("Sucesso", "SOLICITAÇÃO AUTORIZADA");
        loadListSisAutorizacoes();
        if (sa.getTabela().equals("pes_pessoa")) {
            new Dao().refresh(sa.getPessoa());
        }
        complete(sa);
    }

    public void refused(SisAutorizacoes sa) {
        sisAutorizacoes = sa;
        loadListSisAutorizacoes();
    }

    public void refused() {
        if (refusedMotive.isEmpty()) {
            Messages.warn("Validação", "INFORMAR O MOTIVO DA RECUSA!");
            return;
        }
        if (refusedMotive.length() < 5) {
            Messages.warn("Validação", "INFORMAR O MOTIVO VÁLIDO!");
            return;
        }
        sisAutorizacoes.setGestor(Usuario.getUsuario());
        sisAutorizacoes.setDtAutorizacao(new Date());
        sisAutorizacoes.setHoraAutorizacao(DataHoje.horaMinuto());
        sisAutorizacoes.setAutorizado(false);
        sisAutorizacoes.setMotivoRecusa(refusedMotive);
        if (!new Dao().update(sisAutorizacoes, true)) {
            Messages.warn("Erro", "AO REALIZAR AUTORIZAÇÃO!");
            return;
        }
        complete(sisAutorizacoes);
        sisAutorizacoes = new SisAutorizacoes();
        Messages.info("Sucesso", "SOLICITAÇÃO RECUSADA");
        refusedMotive = "";
        loadListSisAutorizacoes();
    }

    public SisAutorizacoes getSisAutorizacoes() {
        return sisAutorizacoes;
    }

    public void setSisAutorizacoes(SisAutorizacoes sisAutorizacoes) {
        this.sisAutorizacoes = sisAutorizacoes;
    }

    public List<SisAutorizacoes> getListSisAutorizacoes() {
        return listSisAutorizacoes;
    }

    public void setListSisAutorizacoes(List<SisAutorizacoes> listSisAutorizacoes) {
        this.listSisAutorizacoes = listSisAutorizacoes;
    }

    public String getRefusedMotive() {
        return refusedMotive;
    }

    public void setRefusedMotive(String refusedMotive) {
        this.refusedMotive = refusedMotive;
    }

    public final void loadListSisAutorizacoes() {
        listSisAutorizacoes = new ArrayList();
        switch (filter) {
            case "rotina":
                value = Integer.toString(idRotina);
                break;
            case "operador":
                value = Integer.toString(idOperador);
                break;
            case "gestor":
                value = Integer.toString(idGestor);
                break;
            case "pessoa":
                value = value;
                break;
            case "data":
                value = value;
                break;
            default:
                break;
        }
        listSisAutorizacoes = new SisAutorizacoesDao().findAll(idTipoAutorizacao, status, filter, value, "");
    }

    public void listener() {
        idGestor = null;
        idOperador = null;
        idRotina = null;
        value = "";
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<SelectItem> getListGestores() {
        return listGestores;
    }

    public void setListGestores(List<SelectItem> listGestores) {
        this.listGestores = listGestores;
    }

    public List<SelectItem> getListOperadores() {
        return listOperadores;
    }

    public void setListOperadores(List<SelectItem> listOperadores) {
        this.listOperadores = listOperadores;
    }

    public List<SelectItem> getListRotinas() {
        return listRotinas;
    }

    public void setListRotinas(List<SelectItem> listRotinas) {
        this.listRotinas = listRotinas;
    }

    public final void loadListGestores() {
        listGestores = new ArrayList();
        List<Usuario> list = new UsuarioDao().findByTabela("sis_autorizacoes", "id_gestor");
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idGestor = list.get(i).getId();
            }
            listGestores.add(new SelectItem(list.get(i).getId(), list.get(i).getPessoa().getNome()));
        }
    }

    public final void loadListOperadores() {
        listOperadores = new ArrayList();
        List<Usuario> list = new UsuarioDao().findByTabela("sis_autorizacoes", "id_operador");
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idOperador = list.get(i).getId();
            }
            listOperadores.add(new SelectItem(list.get(i).getId(), list.get(i).getPessoa().getNome()));
        }

    }

    public final void loadListTipoAutorizacao() {
        listTipoAutorizacao = new ArrayList();
        idTipoAutorizacao = -1;
        List<SisAutorizacoesTipo> list = new Dao().list(new SisAutorizacoesTipo());
        listTipoAutorizacao.add(new SelectItem(-1, "-- TODOS -- "));
        for (int i = 0; i < list.size(); i++) {
            listTipoAutorizacao.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public final void loadListRotinas() {
        listRotinas = new ArrayList();
        List<Rotina> list = new RotinaDao().findByTabela("sis_autorizacoes");
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idRotina = list.get(i).getId();
            }
            listRotinas.add(new SelectItem(list.get(i).getId(), list.get(i).getRotina()));
        }
    }

    public Integer getIdGestor() {
        return idGestor;
    }

    public void setIdGestor(Integer idGestor) {
        this.idGestor = idGestor;
    }

    public Integer getIdOperador() {
        return idOperador;
    }

    public void setIdOperador(Integer idOperador) {
        this.idOperador = idOperador;
    }

    public Integer getIdRotina() {
        return idRotina;
    }

    public void setIdRotina(Integer idRotina) {
        this.idRotina = idRotina;
    }

    public List<SelectItem> getListTipoAutorizacao() {
        return listTipoAutorizacao;
    }

    public void setListTipoAutorizacao(List<SelectItem> listTipoAutorizacao) {
        this.listTipoAutorizacao = listTipoAutorizacao;
    }

    public Integer getIdTipoAutorizacao() {
        return idTipoAutorizacao;
    }

    public void setIdTipoAutorizacao(Integer idTipoAutorizacao) {
        this.idTipoAutorizacao = idTipoAutorizacao;
    }

    public void complete(SisAutorizacoes s) {
        try {
            if (s.getRotina().getId() == 82) {
                String ws = "client:" + ControleUsuarioBean.getCliente().toLowerCase() + ",pf:id:" + s.getPessoa().getJuridica().getId();
                if (s.getAutorizado()) {
                    WSSocket.send(ws, "true");
                } else {
                    WSSocket.send(ws, "false");
                }
            } else if (s.getRotina().getId() == 71) {
                String ws = "client:" + ControleUsuarioBean.getCliente().toLowerCase() + ",pf:id:" + s.getPessoa().getFisica().getId();
                if (s.getAutorizado()) {
                    WSSocket.send(ws, "true");
                } else {
                    WSSocket.send(ws, "false");
                }
            }
        } catch (Exception e) {

        }
    }

}
