package br.com.rtools.financeiro.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.financeiro.ServicoValor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

public class ServicoValorDao extends DB {

    public List pesquisaServicoValor(int idServico) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select p "
                    + "  from ServicoValor p "
                    + " where p.servicos.id = :pid");
            qry.setParameter("pid", idServico);
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }
    
    public ServicoValor pesquisaServicoValorPorPessoaFaixaEtaria(int idServico, int idPessoa) {
        ServicoValor servicoValor = new ServicoValor();
        try {
            String queryString = ""
                    + "        SELECT sv.*                                                                                "
                    + "          FROM fin_servicos s                                                                       "
                    + "   INNER JOIN fin_servico_valor sv ON (sv.id_servico = s.id)                                        "
                    + "   INNER JOIN pes_fisica fis ON (fis.id_pessoa = " + idPessoa + ")                                      "
                    + "          AND extract(YEAR FROM AGE(fis.dt_nascimento)) BETWEEN sv.nr_idade_ini AND sv.nr_idade_fim "
                    + "        WHERE s.id = " + idServico;
            Query query = getEntityManager().createNativeQuery(queryString, ServicoValor.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (ServicoValor) list.get(0);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return new ServicoValor();
    }

    public ServicoValor pesquisaServicoValorPorIdade(int idServico, int idade) {
        try {
            String queryString = ""
                    + "        SELECT sv.*                                                 "
                    + "          FROM fin_servicos s                                        "
                    + "   INNER JOIN fin_servico_valor sv ON (sv.id_servico = s.id)         "
                    + "        WHERE s.id = " + idServico
                    + "          AND " + idade + " BETWEEN sv.nr_idade_ini AND sv.nr_idade_fim ";
            Query query = getEntityManager().createNativeQuery(queryString, ServicoValor.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (ServicoValor) list.get(0);
            }
        } catch (NumberFormatException e) {
            e.getMessage();
        }
        return new ServicoValor();
    }

    public double pesquisaMaiorResponsavel(int idPessoa) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "            SELECT extract(year from age(dt_nascimento))           "
                    + "              FROM pes_fisica pf,                                  "
                    + "                   pes_pessoa p,                                   "
                    + "                   pes_pessoa_endereco pe                          "
                    + "             WHERE extract(year from age(dt_nascimento)) > 18      "
                    + "               AND pf.id_pessoa = p.id                             "
                    + "               AND pe.id_pessoa = p.id                             "
                    + "               AND p.id_tipo_documento = 1                         "
                    + "               AND pe.id_tipo_endereco = 3                         "
                    + "               AND p.id = " + idPessoa + "                         ");
            Vector vector = (Vector) qry.getSingleResult();
            return (new BigDecimal((Double) vector.get(0))).doubleValue();
        } catch (Exception e) {
            e.getMessage();
        }
        return 0;
    }

}
