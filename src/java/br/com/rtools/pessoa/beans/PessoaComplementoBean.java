package br.com.rtools.pessoa.beans;

import br.com.rtools.pessoa.*;
import br.com.rtools.pessoa.dao.PessoaDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class PessoaComplementoBean extends PesquisarProfissaoBean implements Serializable {

    private PessoaComplemento pessoaComplemento = new PessoaComplemento();
    private Pessoa pessoa = new Pessoa();
    private Pessoa responsavel = new Pessoa();
    private Registro registro = new Registro();
    private int diaVencimento = 0;
    private List<SelectItem> listaDataVencimento = new ArrayList();

    public String update(Integer pessoa_id) {
        if (pessoa_id != -1) {
            Dao dao = new Dao();
            pessoaComplemento.setPessoa((Pessoa) dao.find(new Pessoa(), (int) pessoa_id));
            pessoaComplemento.setNrDiaVencimento(diaVencimento);

            if (responsavel != null && responsavel.getId() != -1) {
                pessoaComplemento.setResponsavel(responsavel);
            } else {
                pessoaComplemento.setResponsavel(null);
            }

            dao.openTransaction();
            if (pessoaComplemento.getId() == -1) {
                if (dao.save(pessoaComplemento)) {
                    dao.commit();
                    GenericaMensagem.info("Sucesso", "Pessoa Complemento salva!");
                } else {
                    dao.rollback();
                    GenericaMensagem.error("Atenção", "Erro ao salvar Pessoa Complemento!");
                }
            } else if (dao.update(pessoaComplemento)) {
                dao.commit();
                GenericaMensagem.info("Sucesso", "Pessoa Complemento atualizada!");
            } else {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao Atualizar Pessoa Complemento!");
            }
        }
        Rotina r = new Rotina().get();
        if (r.getId() == 71) {
            ((FisicaBean) GenericaSessao.getObject("fisicaBean")).setPessoaComplemento(pessoaComplemento);
            PF.update("form_pessoa_fisica:id_msg_aviso_block");
        }
        if (r.getId() == 82) {
            ((JuridicaBean) GenericaSessao.getObject("juridicaBean")).setPessoaComplemento(pessoaComplemento);
            PF.update("formPessoaJuridica:id_msg_aviso_block");
        }
        return null;
    }

    public List<SelectItem> getListaDataVencimento() {
        if (listaDataVencimento.isEmpty()) {
            for (int i = 1; i <= 31; i++) {
                listaDataVencimento.add(new SelectItem(Integer.toString(i)));
            }
        }
        return listaDataVencimento;
    }

    public void setListaDataVencimento(List<SelectItem> listaDataVencimento) {
        this.listaDataVencimento = listaDataVencimento;
    }

    public PessoaComplemento getPessoaComplemento() {
        return pessoaComplemento;
    }

    public void setPessoaComplemento(PessoaComplemento pessoaComplemento) {
        this.pessoaComplemento = pessoaComplemento;
    }

    public String pessoaComplementoPesquisaPessoa(Integer idPessoa) {
        try {
            PessoaDao pessoaDB = new PessoaDao();
            pessoaComplemento = pessoaDB.pesquisaPessoaComplementoPorPessoa(idPessoa);
            if (pessoaComplemento.getId() == -1) {
                diaVencimento = getRegistro().getFinDiaVencimentoCobranca();
            } else {
                diaVencimento = pessoaComplemento.getNrDiaVencimento();
            }            
        } catch (Exception e)  {
            
        }
        return null;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public int getDiaVencimento() {
        return diaVencimento;
    }

    public void setDiaVencimento(int diaVencimento) {
        this.diaVencimento = diaVencimento;
    }

    public Registro getRegistro() {
        if (registro == null || registro.getId() == -1) {
            registro = (Registro) Registro.get();
        }
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public Pessoa getResponsavel() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            responsavel = (Pessoa) GenericaSessao.getObject("pessoaPesquisa");
            GenericaSessao.remove("pessoaPesquisa");
        }
        return responsavel;
    }

    public void setResponsavel(Pessoa responsavel) {
        this.responsavel = responsavel;
    }
}
