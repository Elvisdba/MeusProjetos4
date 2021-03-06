package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.ConvencaoPeriodo;
import br.com.rtools.arrecadacao.Oposicao;
import br.com.rtools.arrecadacao.OposicaoPessoa;
import br.com.rtools.arrecadacao.dao.ConvencaoPeriodoDao;
import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.associativo.MatriculaSocios;
import br.com.rtools.associativo.SMotivoInativacao;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.financeiro.dao.ServicoPessoaDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaDao;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Mask;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.ValidaDocumentos;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class OposicaoBean implements Serializable {

    private Oposicao oposicao;
    private OposicaoPessoa oposicaoPessoa;
    private ConvencaoPeriodo convencaoPeriodo;
    private List<ConvencaoPeriodo> listaConvencaoPeriodos;
    private String message;
    private String mensagemEmpresa;
    private String sexo;
    private String valorPesquisa;
    private boolean desabilitaPessoa;
    private List<Oposicao> listaOposicaos;
    private String porPesquisa;
    private String descricaoPesquisa;
    private String comoPesquisa;
    private Socios socios;
    private boolean removeFiltro;

    @PostConstruct
    public void init() {
        oposicao = new Oposicao();
        oposicaoPessoa = new OposicaoPessoa();
        convencaoPeriodo = new ConvencaoPeriodo();
        listaConvencaoPeriodos = new ArrayList();
        message = "";
        mensagemEmpresa = "";
        sexo = "M";
        valorPesquisa = "";
        desabilitaPessoa = false;
        listaOposicaos = new ArrayList();
        porPesquisa = "todos";
        descricaoPesquisa = "";
        comoPesquisa = "";
        socios = new Socios();
        removeFiltro = false;
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("oposicaoBean");
        GenericaSessao.remove("oposicaoPesquisa");
        GenericaSessao.remove("juridicaPesquisa");
        GenericaSessao.remove("removeFiltro");
    }

    public void clear() {
        oposicao = new Oposicao();
        oposicaoPessoa = new OposicaoPessoa();
        convencaoPeriodo = new ConvencaoPeriodo();
        listaConvencaoPeriodos.clear();
        listaOposicaos.clear();
        sexo = "";
        porPesquisa = "todos";
        message = "";
        setMensagemEmpresa("");
        setDesabilitaPessoa(false);
        valorPesquisa = "";
        setSexo("M");
    }

    public void removerEndereco() {
        oposicao.getOposicaoPessoa().setEndereco(null);
    }

    public void outraEmpresa() {
        listaConvencaoPeriodos = new ArrayList();
        String cpf = oposicao.getOposicaoPessoa().getCpf();
        oposicao = new Oposicao();
        oposicaoPessoa = new OposicaoPessoa();
        oposicao.getOposicaoPessoa().setCpf(cpf);
        consultaPessoa(true);
        desabilitaPessoa = false;
        convencaoPeriodo = new ConvencaoPeriodo();
    }

    public void save() {
        if (oposicao.getJuridica().getId() == -1) {
            message = "Informar a pessoa jurídica!";
            return;
        }
        if (oposicao.getOposicaoPessoa().getNome().isEmpty()) {
            message = "Informar o nome da pessoa!";
            return;
        }
        JuridicaDao db = new JuridicaDao();
        List<List> lista_inativa = db.listaJuridicaContribuinte(oposicao.getJuridica().getId());

        if (lista_inativa.isEmpty()) {
            message = "Empresa não é contribuinte!";
            return;
        }

        if (lista_inativa.get(0).get(11) != null) {
            message = "Empresa Inativa!";
            return;
        }
        if (oposicao.getConvencaoPeriodo().getId() == -1) {
            message = "Empresa fora do período de Convenção!";
            return;
        }
        if (oposicao.getConvencaoPeriodo().getId() == -1) {
            message = "Empresa fora do período de Convenção!";
            return;
        }
        if (oposicao.getEmissao().isEmpty()) {
            message = "Campo emissão não pode estar vazio!";
            return;
        }
//        if (socios.getId() != -1) {
//            if (socios.getServicoPessoa().isAtivo()) {
//                message = "CPF cadastrado como sócio, inative para salvar oposição!";
//                return;
//            }
//        }
        Dao dao = new Dao();
        String beforeUpdate = "";
        Oposicao o = (Oposicao) dao.find(oposicao);
        if (oposicao.getId() != -1) {
            beforeUpdate = ""
                    + "ID: " + o.getId()
                    + " - Pessoa (Oposição Pessoa): (" + o.getOposicaoPessoa().getId() + ") " + o.getOposicaoPessoa().getNome()
                    + " - Jurídica: (" + o.getJuridica().getPessoa().getId() + ") " + o.getJuridica().getPessoa().getNome()
                    + " - Convençao Período: (" + o.getConvencaoPeriodo().getId() + ") "
                    + " [" + o.getConvencaoPeriodo().getConvencao().getDescricao()
                    + " - " + o.getConvencaoPeriodo().getGrupoCidade().getDescricao()
                    + " - Ref: " + o.getConvencaoPeriodo().getReferenciaInicial()
                    + " - " + o.getConvencaoPeriodo().getReferenciaFinal()
                    + " ]";
        }

        if (oposicao.getOposicaoPessoa().getCpf().equals("___.___.___-__") || oposicao.getOposicaoPessoa().getCpf().equals("")) {
            message = "Informar o CPF!";
            return;
        }
        if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(oposicao.getOposicaoPessoa().getCpf()))) {
            message = "CPF inválido!";
            return;
        }

        if (!saveOposicaoPessoa()) {
            message = "Falha ao salvar oposição pessoa!";
            return;
        }

        NovoLog novoLog = new NovoLog();
        dao.openTransaction();
        if (oposicao.getId() == -1) {
            OposicaoDao oposicaoDao = new OposicaoDao();
            if (oposicaoDao.validaOposicao(oposicao)) {
                message = "Oposição já cadastrada para essa pessoa para a convenção vigente!";
                return;
            }
            if (dao.save(oposicao)) {
                novoLog.save(""
                        + "ID: " + oposicao.getId()
                        + " - Pessoa (Oposição Pessoa): (" + oposicao.getOposicaoPessoa().getId() + ") " + oposicao.getOposicaoPessoa().getNome()
                        + " - Jurídica: (" + oposicao.getJuridica().getPessoa().getId() + ") " + oposicao.getJuridica().getPessoa().getNome()
                        + " - Convençao Período: (" + oposicao.getConvencaoPeriodo().getId() + ") "
                        + " [" + oposicao.getConvencaoPeriodo().getConvencao().getDescricao()
                        + " - " + oposicao.getConvencaoPeriodo().getGrupoCidade().getDescricao()
                        + " - Ref: " + oposicao.getConvencaoPeriodo().getReferenciaInicial()
                        + " - " + oposicao.getConvencaoPeriodo().getReferenciaFinal()
                        + " ]"
                );

                if (!inativarSocioOposicao(dao)) {
                    message = "Erro ao Inativar Sócio";
                    dao.rollback();
                    return;
                }

                dao.commit();
                clear();
                setMessage("Registro salvo com sucesso.");
            } else {
                dao.rollback();
                clear();
                message = "Falha ao salvar este registro!";
            }
        } else if (dao.update(oposicao)) {
            novoLog.update(beforeUpdate, ""
                    + "ID: " + oposicao.getId()
                    + " - Pessoa (Oposição Pessoa): (" + oposicao.getOposicaoPessoa().getId() + ") " + oposicao.getOposicaoPessoa().getNome()
                    + " - Jurídica: (" + oposicao.getJuridica().getPessoa().getId() + ") " + oposicao.getJuridica().getPessoa().getNome()
                    + " - Convençao Período: (" + oposicao.getConvencaoPeriodo().getId() + ") "
                    + " [" + oposicao.getConvencaoPeriodo().getConvencao().getDescricao()
                    + " - " + oposicao.getConvencaoPeriodo().getGrupoCidade().getDescricao()
                    + " - Ref: " + oposicao.getConvencaoPeriodo().getReferenciaInicial()
                    + " - " + oposicao.getConvencaoPeriodo().getReferenciaFinal()
                    + " ]"
            );

            if (!inativarSocioOposicao(dao)) {
                message = "Erro ao Inativar Sócio";
                dao.rollback();
                return;
            }

            dao.commit();
            clear();
            setMessage("Registro atualizado com sucesso.");
        } else {
            dao.rollback();
            clear();
            message = "Falha ao excluir este registro!";
        }
    }

    public boolean inativarSocioOposicao(Dao dao) {
        try {
            PessoaDao db = new PessoaDao();
            Pessoa p = db.pessoaDocumento(oposicao.getOposicaoPessoa().getCpf());
            if (p == null) {
                return true;
            }

            ConfiguracaoSocial cs = (ConfiguracaoSocial) new Dao().find(new ConfiguracaoSocial(), 1);
            if (cs.getInativaOposicao()) {
                ServicoPessoaDao sp_dao = new ServicoPessoaDao();
                if (p.getSocios() != null && p.getSocios().getId() != -1) {
                    // SE A PESSOA DA OPOSIÇÃO NÃO FOR TITULAR
                    if (p.getId() != p.getSocios().getMatriculaSocios().getTitular().getId()) {
                        return true;
                    }

                    List<ServicoPessoa> list_sp = sp_dao.listaTodosServicoPessoaPorTitular(p.getSocios().getMatriculaSocios().getTitular().getId());

                    for (ServicoPessoa sp : list_sp) {
                        sp.setAtivo(false);
                        if (!dao.update(sp)) {
                            GenericaMensagem.error("Error", "Não foi possível alterar Serviço Pessoa!");
                            return false;
                        }
                    }

                    MatriculaSocios ms = p.getSocios().getMatriculaSocios();

                    ms.setDtInativo(DataHoje.dataHoje());
                    ms.setMotivoInativacao((SMotivoInativacao) dao.find(new SMotivoInativacao(), 3));

                    if (!dao.update(ms)) {
                        GenericaMensagem.error("Error", "Não foi possível alterar Matrícula Sócios!");
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public String edit(Oposicao o) {
        Dao dao = new Dao();
        setDesabilitaPessoa(true);
        oposicao = (Oposicao) dao.rebind(o);
        sexo = oposicao.getOposicaoPessoa().getSexo();
        GenericaSessao.put("oposicaoPesquisa", oposicao);
        GenericaSessao.put("linkClicado", true);
        String urlRetorno = "oposicao";
        if (GenericaSessao.exists("urlRetorno")) {
            GenericaSessao.put("oposicaoPesquisaPor", porPesquisa);
            urlRetorno = GenericaSessao.getString("urlRetorno");
        }
        return urlRetorno;
    }

    public boolean saveOposicaoPessoa() {
        oposicao.getOposicaoPessoa().setSexo(sexo);

        Dao dao = new Dao();
        dao.openTransaction();
        OposicaoDao oposicaoDao = new OposicaoDao();
        OposicaoPessoa op = oposicaoDao.pesquisaOposicaoPessoa(oposicao.getOposicaoPessoa().getCpf(), oposicao.getOposicaoPessoa().getRg());
        if (op.getId() == -1) {
            if (oposicao.getOposicaoPessoa().getId() == -1) {
                if (dao.save(oposicao.getOposicaoPessoa())) {
                    listaConvencaoPeriodos.clear();
                    dao.commit();
                    return true;
                } else {
                    dao.rollback();
                    return false;
                }
            }
        } else if (op.getCpf().equals(oposicao.getOposicaoPessoa().getCpf())
                && op.getRg().equals(oposicao.getOposicaoPessoa().getRg())
                && op.getNome().equals(oposicao.getOposicaoPessoa().getNome())
                && op.getSexo().equals(oposicao.getOposicaoPessoa().getSexo())
                && op.getEmail1().equals(oposicao.getOposicaoPessoa().getEmail1())
                && op.getTelefone1().equals(oposicao.getOposicaoPessoa().getTelefone1())
                && op.getTelefone2().equals(oposicao.getOposicaoPessoa().getTelefone2())
                && op.getDataNascimentoString().equals(oposicao.getOposicaoPessoa().getDataNascimentoString())) {
            return true;
        } else if (oposicao.getOposicaoPessoa().getId() != -1) {
            oposicao.getOposicaoPessoa().setId(op.getId());
            if (dao.update(oposicao.getOposicaoPessoa())) {
                dao.commit();
                return true;
            } else {
                dao.rollback();
                return false;
            }
        }
        return true;
    }

    public void delete() {
        delete(0);
    }

    public void delete(int id) {
        Dao dao = new Dao();
        NovoLog novoLog = new NovoLog();
        if (id > 0) {
            oposicao = (Oposicao) dao.find(new Oposicao(), id);
        } else {
            oposicao = (Oposicao) dao.find(oposicao);
        }
        if (oposicao.getId() != -1) {
            dao.openTransaction();
            if (dao.delete(oposicao)) {
                novoLog.delete(""
                        + "ID: " + oposicao.getId()
                        + " - Pessoa (Oposição Pessoa): (" + oposicao.getOposicaoPessoa().getId() + ") " + oposicao.getOposicaoPessoa().getNome()
                        + " - Jurídica: (" + oposicao.getJuridica().getPessoa().getId() + ") " + oposicao.getJuridica().getPessoa().getNome()
                        + " - Convençao Período: (" + oposicao.getConvencaoPeriodo().getId() + ") "
                        + " - [" + oposicao.getConvencaoPeriodo().getConvencao().getDescricao()
                        + " - " + oposicao.getConvencaoPeriodo().getGrupoCidade().getDescricao()
                        + " - Ref: " + oposicao.getConvencaoPeriodo().getReferenciaInicial()
                        + " - " + oposicao.getConvencaoPeriodo().getReferenciaFinal()
                        + " ]"
                );
                dao.commit();
                clear();
                setMessage("Registro excluído com sucesso");
            } else {
                dao.rollback();
                clear();
                message = "Falha na exclusão do registro!";
            }
        }
    }

    public void inativar() {
        Dao dao = new Dao();
        NovoLog novoLog = new NovoLog();
        dao.openTransaction();

        oposicao.setDtInativacao(DataHoje.dataHoje());
        if (dao.update(oposicao)) {
            novoLog.update(""
                    + "ID: " + oposicao.getId()
                    + " - Pessoa (Oposição Pessoa): (" + oposicao.getOposicaoPessoa().getId() + ") " + oposicao.getOposicaoPessoa().getNome()
                    + " - Jurídica: (" + oposicao.getJuridica().getPessoa().getId() + ") " + oposicao.getJuridica().getPessoa().getNome()
                    + " - Convençao Período: (" + oposicao.getConvencaoPeriodo().getId() + ") "
                    + " - [" + oposicao.getConvencaoPeriodo().getConvencao().getDescricao()
                    + " - " + oposicao.getConvencaoPeriodo().getGrupoCidade().getDescricao()
                    + " - Ref: " + oposicao.getConvencaoPeriodo().getReferenciaInicial()
                    + " - " + oposicao.getConvencaoPeriodo().getReferenciaFinal()
                    + " ]"
                    + " - Data Inativação: ( NULL ) "
                    + " - Usuário: ( NULL ) ",
                    ""
                    + "ID: " + oposicao.getId()
                    + " - Pessoa (Oposição Pessoa): (" + oposicao.getOposicaoPessoa().getId() + ") " + oposicao.getOposicaoPessoa().getNome()
                    + " - Jurídica: (" + oposicao.getJuridica().getPessoa().getId() + ") " + oposicao.getJuridica().getPessoa().getNome()
                    + " - Convençao Período: (" + oposicao.getConvencaoPeriodo().getId() + ") "
                    + " - [" + oposicao.getConvencaoPeriodo().getConvencao().getDescricao()
                    + " - " + oposicao.getConvencaoPeriodo().getGrupoCidade().getDescricao()
                    + " - Ref: " + oposicao.getConvencaoPeriodo().getReferenciaInicial()
                    + " - " + oposicao.getConvencaoPeriodo().getReferenciaFinal()
                    + " ]"
                    + " - Data Inativação: ( " + oposicao.getInativacaoString() + " ) "
                    + " - Usuário: ( " + Usuario.getUsuario().getPessoa().getNome() + " ) "
            );
            dao.commit();
            clear();
            setMessage("Registro inativado com sucesso");
        } else {
            dao.rollback();
            clear();
            message = "Falha ao inativar!";
        }
    }

    public void consultaPessoa() {
        consultaPessoa(false);
    }

    public void consultaPessoa(Boolean ignoraPessoaEmpresa) {
        boolean isVerificaDocumento = false;
        if (oposicao.getOposicaoPessoa().getCpf().equals("___.___.___-__") || oposicao.getOposicaoPessoa().getCpf().equals("")) {
            isVerificaDocumento = true;
        }
        if (!oposicao.getOposicaoPessoa().getRg().equals("")) {
            isVerificaDocumento = false;
        }
        if (isVerificaDocumento == true) {
            return;
        }
        if (oposicao.getOposicaoPessoa().getId() != -1) {
            return;
        }
        if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(oposicao.getOposicaoPessoa().getCpf()))) {
            //RequestContext.getCurrentInstance().execute("PF('input_cpf').focus();");
            GenericaMensagem.warn("Validação", "CPF inválido!");
            return;
        }
        OposicaoDao oposicaoDao = new OposicaoDao();
        PessoaEmpresa pessoaEmpresa = new PessoaEmpresa();
        if (ignoraPessoaEmpresa == null || !ignoraPessoaEmpresa) {
            pessoaEmpresa = oposicaoDao.pesquisaPessoaFisicaEmpresa(oposicao.getOposicaoPessoa().getCpf(), oposicao.getOposicaoPessoa().getRg());
        }
        PessoaDao dbp = new PessoaDao();
        Pessoa p = dbp.pessoaDocumento(oposicao.getOposicaoPessoa().getCpf());
        if (pessoaEmpresa.getId() != -1 && (ignoraPessoaEmpresa == null || !ignoraPessoaEmpresa)) {
            if (oposicao.getJuridica().getId() == -1) {
                oposicao.setJuridica(pessoaEmpresa.getJuridica());
            }
            setDesabilitaPessoa(true);
            oposicaoPessoa = oposicaoDao.pesquisaOposicaoPessoa(oposicao.getOposicaoPessoa().getCpf(), oposicao.getOposicaoPessoa().getRg());
            if (oposicaoPessoa.getId() != -1) {
                oposicao.setOposicaoPessoa(oposicaoPessoa);
            } else {
                oposicao.getOposicaoPessoa().setNome(pessoaEmpresa.getFisica().getPessoa().getNome());
                oposicao.getOposicaoPessoa().setRg(pessoaEmpresa.getFisica().getRg());
                oposicao.getOposicaoPessoa().setCpf(pessoaEmpresa.getFisica().getPessoa().getDocumento());
                oposicao.getOposicaoPessoa().setSexo(pessoaEmpresa.getFisica().getSexo());
                sexo = pessoaEmpresa.getFisica().getSexo();
            }
        } else if (p != null) {
            oposicaoPessoa = oposicaoDao.pesquisaOposicaoPessoa(oposicao.getOposicaoPessoa().getCpf(), oposicao.getOposicaoPessoa().getRg());
            if (oposicaoPessoa.getId() != -1) {
                oposicao.setOposicaoPessoa(oposicaoPessoa);
            } else {
                oposicao.getOposicaoPessoa().setNome(p.getNome());
                oposicao.getOposicaoPessoa().setRg(p.getFisica().getRg());
                oposicao.getOposicaoPessoa().setCpf(p.getDocumento());
                oposicao.getOposicaoPessoa().setSexo(p.getFisica().getSexo());
                sexo = p.getFisica().getSexo();
                if (oposicao.getJuridica().getId() != -1 && oposicao.getJuridica().getDtFechamento() == null) {
                    setDesabilitaPessoa(true);
                }
            }
        } else {
            oposicaoPessoa = oposicaoDao.pesquisaOposicaoPessoa(oposicao.getOposicaoPessoa().getCpf(), oposicao.getOposicaoPessoa().getRg());
            if (oposicaoPessoa.getId() != -1) {
                oposicao.setOposicaoPessoa(oposicaoPessoa);
            } else {
                if (oposicao.getJuridica().getId() != -1 && oposicao.getJuridica().getDtFechamento() == null) {
                    setDesabilitaPessoa(true);
                }
                oposicao.getOposicaoPessoa().setSexo(sexo);
            }
        }
        listaConvencaoPeriodos.clear();
        convencaoPeriodoConvencaoGrupoCidade();
        SociosDao db = new SociosDao();

        if (p != null) {
            ConfiguracaoSocial cs = (ConfiguracaoSocial) new Dao().find(new ConfiguracaoSocial(), 1);
            if (cs.getInativaOposicao()) {
                socios = p.getSocios();//db.pesquisaSocioPorPessoa(pessoaEmpresa.getFisica().getPessoa().getId());            
                if (socios != null && socios.getId() != -1) {
                    // SE A PESSOA DA OPOSIÇÃO FOR TITULAR
                    if (p.getId() == socios.getMatriculaSocios().getTitular().getId()) {
                        if (socios.getServicoPessoa().isAtivo()) {
                            GenericaMensagem.error("Validação", "CPF cadastrado como sócio, ao salvar será inativado Automáticamente!");
                        }
                    }
                }

                db.getEntityManager().close();
            }
        }
    }

    public void convencaoPeriodoConvencaoGrupoCidade() {
        if (oposicao.getJuridica().getId() != -1) {
            if (oposicao.getConvencaoPeriodo().getId() == -1) {
                if (!oposicao.getEmissao().isEmpty()) {
                    OposicaoDao oposicaoDao = new OposicaoDao();
                    List list = oposicaoDao.pesquisaPessoaConvencaoGrupoCidade(oposicao.getJuridica().getId());
                    ConvencaoPeriodoDao di = new ConvencaoPeriodoDao();
                    if (!list.isEmpty()) {
                        String referencia_hifen = DataHoje.livre(oposicao.getDtEmissao(), "YYYY-MM");
                        convencaoPeriodo = di.convencaoPeriodoConvencaoGrupoCidade((Integer) list.get(0), (Integer) list.get(1), referencia_hifen);
                        if (convencaoPeriodo.getId() == -1) {
                            convencaoPeriodo = new ConvencaoPeriodo();
                        } else {
                            oposicao.setConvencaoPeriodo(convencaoPeriodo);
                        }
                    }
                } else {
                    convencaoPeriodo = oposicao.getConvencaoPeriodo();
                }
            } else {
                convencaoPeriodo = oposicao.getConvencaoPeriodo();
            }
        }
    }

    public Oposicao getOposicao() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            oposicao.setJuridica((Juridica) GenericaSessao.getObject("juridicaPesquisa", true));
            mensagemEmpresa = "";
            if (oposicao.getJuridica().getDtFechamento() != null) {
                mensagemEmpresa = "Empresa está inativa!";
                return null;
            }
            listaConvencaoPeriodos.clear();
            convencaoPeriodoConvencaoGrupoCidade();
        }

        if (GenericaSessao.exists("oposicaoPesquisa")) {
            setDesabilitaPessoa(true);
            oposicao = (Oposicao) GenericaSessao.getObject("oposicaoPesquisa", true);
            listaConvencaoPeriodos.clear();
            convencaoPeriodoConvencaoGrupoCidade();
        }

        if (GenericaSessao.exists("enderecoPesquisa")) {
            oposicao.getOposicaoPessoa().setEndereco((Endereco) GenericaSessao.getObject("enderecoPesquisa", true));
        }
        return oposicao;
    }

    public void setOposicao(Oposicao oposicao) {
        this.oposicao = oposicao;
    }

    public OposicaoPessoa getOposicaoPessoa() {
        return oposicaoPessoa;
    }

    public void setOposicaoPessoa(OposicaoPessoa oposicaoPessoa) {
        this.oposicaoPessoa = oposicaoPessoa;
    }

    public ConvencaoPeriodo getConvencaoPeriodo() {
        return convencaoPeriodo;
    }

    public void setConvencaoPeriodo(ConvencaoPeriodo convencaoPeriodo) {
        this.convencaoPeriodo = convencaoPeriodo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMensagemEmpresa(String mensagemEmpresa) {
        this.mensagemEmpresa = mensagemEmpresa;
    }

    public String getMsgEmpresa() {
        return mensagemEmpresa;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getValorPesquisa() {
        return valorPesquisa;
    }

    public void setValorPesquisa(String valorPesquisa) {
        this.valorPesquisa = valorPesquisa;
    }

    public boolean isDesabilitaPessoa() {
        return desabilitaPessoa;
    }

    public void setDesabilitaPessoa(boolean desabilitaPessoa) {
        this.desabilitaPessoa = desabilitaPessoa;
    }

    public List<Oposicao> getListaOposicaos() {
        OposicaoDao oposicaoDao = new OposicaoDao();
        if (listaOposicaos.isEmpty()) {
            if (removeFiltro) {
                if (porPesquisa.equals("todos")) {
                    return new ArrayList();
                }
            }
            listaOposicaos = oposicaoDao.pesquisaOposicao(descricaoPesquisa, porPesquisa, comoPesquisa);
        }
        return listaOposicaos;
    }

    public void setListaOposicaos(List<Oposicao> listaOposicaos) {
        this.listaOposicaos = listaOposicaos;
    }

    public void acaoPesquisaInicial() {
        listaOposicaos.clear();
        setComoPesquisa("Inicial");
    }

    public void acaoPesquisaParcial() {
        listaOposicaos.clear();
        setComoPesquisa("Parcial");
    }

    public String getPorPesquisa() {
        if (porPesquisa.equals("todos")) {
            descricaoPesquisa = "";
        }
        return porPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }

    public String getComoPesquisa() {
        return comoPesquisa;
    }

    public void setComoPesquisa(String comoPesquisa) {
        this.comoPesquisa = comoPesquisa;
    }

    public String getDescricaoPesquisa() {
        return descricaoPesquisa;
    }

    public void setDescricaoPesquisa(String descricaoPesquisa) {
        this.descricaoPesquisa = descricaoPesquisa;
    }

    public String getMascara() {
        return Mask.getMascaraPesquisa(porPesquisa, true);
    }

    public boolean isRemoveFiltro() {
        if (GenericaSessao.exists("removeFiltro")) {
            removeFiltro = GenericaSessao.getBoolean("removeFiltro", true);
        }
        return removeFiltro;
    }

    public void setRemoveFiltro(boolean removeFiltro) {
        this.removeFiltro = removeFiltro;
    }

    public String pessoaSocio(String cpf) {
        PessoaDao pessoaDB = new PessoaDao();
        Pessoa p = pessoaDB.pessoaDocumento(cpf);
        if (p != null) {
            SociosDao sociosDB = new SociosDao();
            Socios s = sociosDB.pesquisaSocioPorPessoa(p.getId());
            if (s.getId() != -1) {
                if (s.getServicoPessoa().isAtivo()) {
                    return "Sócio";
                }
            }
        }
        return "";
    }

    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                oposicao.setConvencaoPeriodo(new ConvencaoPeriodo());
                convencaoPeriodoConvencaoGrupoCidade();
                break;
            default:
                break;
        }
    }
}
