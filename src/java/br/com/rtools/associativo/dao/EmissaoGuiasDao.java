package br.com.rtools.associativo.dao;

import br.com.rtools.financeiro.Guia;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class EmissaoGuiasDao extends DB {

    public List listaMovimentosDevedores(int id_pessoa) {
        try {
            String textqry
                    = " SELECT * \n"
                    + "   FROM fin_movimento m "
                    + "  WHERE dt_vencimento < now() \n"
                    + "    AND m.id_pessoa = " + id_pessoa
                    + "    AND m.id_baixa is null \n "
                    + "    AND m.is_ativo = true \n "
                    + "    AND m.id_servicos NOT IN (SELECT sr.id_servicos FROM fin_servico_rotina sr WHERE id_rotina = 4)";

            Query qry = getEntityManager().createNativeQuery(textqry);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Guia> findByLote(Integer lote_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT G FROM Guia G WHERE G.lote.id = :lote_id");
            query.setParameter("lote_id", lote_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
