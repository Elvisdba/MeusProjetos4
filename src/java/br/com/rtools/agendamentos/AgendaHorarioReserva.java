package br.com.rtools.agendamentos;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ag_horario_reserva",
        uniqueConstraints = @UniqueConstraint(columnNames = {"dt_expiracao", "id_horario", "id_pessoa_reserva"})
)
public class AgendaHorarioReserva implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_expiracao", length = 5, nullable = false)
    private Date dtExpiracao;
    @JoinColumn(name = "id_horario", referencedColumnName = "id", nullable = false)
    @ManyToOne()
    private AgendaHorarios horario;
    @JoinColumn(name = "id_pessoa_reserva", referencedColumnName = "id", nullable = false)
    @ManyToOne()
    private Pessoa pessoaReserva;

    public AgendaHorarioReserva() {
        this.id = null;
        this.dtExpiracao = new Date();
        this.horario = null;
        try {
            this.pessoaReserva = ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getPessoa();
        } catch (Exception e1) {
            try {
                this.pessoaReserva = ((Pessoa) GenericaSessao.getObject("sessaoUsuarioAcessoWeb"));
            } catch (Exception e2) {
                this.pessoaReserva = null;
            }
        }
    }

    public AgendaHorarioReserva(Integer id, Date dtExpiracao, AgendaHorarios horario, Pessoa pessoaReserva) {
        this.id = id;
        this.dtExpiracao = dtExpiracao;
        this.horario = null;
        this.pessoaReserva = pessoaReserva;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDtExpiracao() {
        return dtExpiracao;
    }

    public void setDtExpiracao(Date dtExpiracao) {
        this.dtExpiracao = dtExpiracao;
    }

    public AgendaHorarios getHorario() {
        return horario;
    }

    public void setHorario(AgendaHorarios horario) {
        this.horario = horario;
    }

    public Pessoa getPessoa() {
        return pessoaReserva;
    }

    public void setPessoa(Pessoa pessoaReserva) {
        this.pessoaReserva = pessoaReserva;
    }

}
