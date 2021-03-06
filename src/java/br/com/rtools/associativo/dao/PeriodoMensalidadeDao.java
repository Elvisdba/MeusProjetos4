package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.PeriodoMensalidade;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class PeriodoMensalidadeDao extends DB {

    public List<PeriodoMensalidade> findAllByPeriodo(Integer periodo_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT PM FROM PeriodoMensalidade AS PM WHERE PM.periodo.id = :periodo_id");
            query.setParameter("periodo_id", periodo_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public PeriodoMensalidade findByPeriodo(Integer periodo_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT PM FROM PeriodoMensalidade AS PM WHERE PM.periodo.id = :periodo_id");
            query.setParameter("periodo_id", periodo_id);
            return (PeriodoMensalidade) query.getSingleResult();
        } catch (Exception e) {
            return null; 
        }
    }
}
