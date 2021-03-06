package br.com.rtools.sistema;

import br.com.rtools.pessoa.Juridica;
import br.com.rtools.utilitarios.DataHoje;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "sis_configuracao")
@NamedQuery(name = "Configuracao.pesquisaID", query = "SELECT C FROM Configuracao c WHERE C.id = :pid")
public class Configuracao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_nome_cliente", length = 300)
    private String nomeCliente;
    @Column(name = "ds_persistence", length = 200)
    private String persistence;
    @Column(name = "ds_caminho_sistema", length = 200)
    private String caminhoSistema;
    @Column(name = "ds_identifica", length = 100, unique = true)
    private String identifica;
    @JoinColumn(name = "id_juridica", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Juridica juridica;
    @Column(name = "nr_acesso")
    private Integer acessos;
    @Column(name = "dt_cadastro")
    @Temporal(TemporalType.DATE)
    private Date dtCadastro;
    @Column(name = "is_ativo")
    private Boolean ativo;
    @Column(name = "ds_host", length = 300)
    private String host;
    @Column(name = "ds_senha", length = 300)
    private String senha;

    public Configuracao() {
        this.id = null;
        this.nomeCliente = "";
        this.persistence = "";
        this.caminhoSistema = "";
        this.identifica = "";
        this.juridica = new Juridica();
        this.acessos = 0;
        this.dtCadastro = DataHoje.dataHoje();
        this.ativo = true;
        this.host = "";
        this.senha = "";
    }

    public Configuracao(Integer id, String nomeCliente, String persistence, String caminhoSistema, String identifica, Juridica juridica, Integer acessos, String cadastro, Boolean ativo, String host, String senha) {
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.persistence = persistence;
        this.caminhoSistema = caminhoSistema;
        this.identifica = identifica;
        this.juridica = juridica;
        this.acessos = acessos;
        this.dtCadastro = DataHoje.converte(cadastro);
        this.ativo = ativo;
        this.host = "";
        this.senha = "";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getPersistence() {
        return persistence;
    }

    public void setPersistence(String persistence) {
        this.persistence = persistence;
    }

    public String getCaminhoSistema() {
        return caminhoSistema;
    }

    public void setCaminhoSistema(String caminhoSistema) {
        this.caminhoSistema = caminhoSistema;
    }

    public String getIdentifica() {
        return identifica;
    }

    public void setIdentifica(String identifica) {
        this.identifica = identifica;
    }

    public Juridica getJuridica() {
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public Integer getAcessos() {
        return acessos;
    }

    public void setAcessos(Integer acessos) {
        this.acessos = acessos;
    }

    public Date getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(Date cadastro) {
        this.dtCadastro = cadastro;
    }

    public String getCadastro() {
        return DataHoje.converteData(dtCadastro);
    }

    public void setCadastro(String cadastro) {
        this.dtCadastro = DataHoje.converte(cadastro);
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return "Configuracao{" + "id=" + id + ", nomeCliente=" + nomeCliente + ", persistence=" + persistence + ", caminhoSistema=" + caminhoSistema + ", identifica=" + identifica + ", juridica=" + juridica + ", acessos=" + acessos + ", dtCadastro=" + dtCadastro + ", ativo=" + ativo + ", host=" + host + ", senha=" + senha + '}';
    }

}
