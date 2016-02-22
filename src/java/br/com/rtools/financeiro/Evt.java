package br.com.rtools.financeiro;

import br.com.rtools.financeiro.db.LoteDBToplink;
import br.com.rtools.financeiro.db.MovimentoDBToplink;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "fin_evt")
@NamedQuery(name = "Evt.pesquisaID", query = "select e from Evt e where e.id=:pid")
public class Evt implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_descricao", nullable = true)
    private String descricao;

    public Evt() {
        this.id = -1;
        this.descricao = "";
    }

    public Evt(int id) {
        this.id = id;
        this.descricao = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "Evt{" + "id=" + id + ", descricao=" + descricao + '}';
    }

    public Lote getLote() {
        return new LoteDBToplink().pesquisaLotePorEvt(this);
    }

    public List<Movimento> getMovimento() {
        return new MovimentoDBToplink().listaMovimentosDoLote(getLote().getId());
    }

}
