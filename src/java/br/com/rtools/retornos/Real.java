package br.com.rtools.retornos;

import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.ArquivoRetorno;
import java.util.ArrayList;
import java.util.List;

public class Real extends ArquivoRetorno {

    public Real(ContaCobranca contaCobranca) {
        super(contaCobranca);
    }

    @Override
    public List<ObjetoRetorno> sicob(boolean baixar, String host) {
        List<ObjetoRetorno> lista_objeto_retorno = new ArrayList();
//        host = host + "/pendentes/";
//        pasta = host;
//
//        File fl = new File(host);
//        File listFile[] = fl.listFiles();
//        if (listFile != null) {
//            int qntRetornos = listFile.length;
//            for (int u = 0; u < qntRetornos; u++) {
//                try {
//                    FileReader reader = new FileReader(host + listFile[u].getName());
//                    BufferedReader buffReader = new BufferedReader(reader);
//                    List lista = new Vector();
//                    while ((linha = buffReader.readLine()) != null) {
//                        lista.add(linha);
//                    }
//
//                    reader.close();
//                    buffReader.close();
//                    int i = 0;
//                    while (i < lista.size()) {
//                        if (i < 1) {
//                            cnpj = ((String) lista.get(i)).substring(18, 32);
//                            codigoCedente = ((String) lista.get(i)).substring(63, 70);
//                        }
//                        if (((String) lista.get(i)).substring(13, 14).equals("T")) {
//                            nossoNumero = ((String) lista.get(i)).substring(44, 57).trim();
//                            dataVencimento = ((String) lista.get(i)).substring(73, 81).trim();
//                            //valorTaxa      = ((String) lista.get(i)).substring(198, 213); // NAO SEI
//                            valorTaxa = "0";
//                        }
//                        try {
//                            int con = Integer.parseInt(dataVencimento);
//                            if (con == 0) {
//                                dataVencimento = "11111111";
//                            }
//                        } catch (Exception e) {
//                        }
//                        i++;
//                        if (i < lista.size() && ((String) lista.get(i)).substring(13, 14).equals("U")) {
//                            valorPago = ((String) lista.get(i)).substring(77, 92);
//                            dataPagamento = ((String) lista.get(i)).substring(137, 145);
//
//                            listaRetorno.add(
//                                    new GenericaRetorno(
//                                            cnpj, //1 ENTIDADE
//                                            codigoCedente, //2 NESTE CASO SICAS
//                                            nossoNumero, //3
//                                            valorPago, //4
//                                            valorTaxa, //5
//                                            "",//valorCredito,   //6
//                                            dataPagamento, //7
//                                            dataVencimento,//dataVencimento, //8
//                                            "", //9 ACRESCIMO
//                                            "", //10 VALOR DESCONTO
//                                            "", //11 VALOR ABATIMENTO
//                                            "", //12 VALOR REPASSE ...(valorPago - valorCredito)
//                                            pasta, // 13 NOME DA PASTA
//                                            listFile[u].getName(), //14 NOME DO ARQUIVO
//                                            "", //15 DATA CREDITO
//                                            "", // 16 SEQUENCIAL DO ARQUIVO
//                                            null
//                                    )
//                            );
//                            i++;
//                        }
//                    }
//                } catch (Exception e) {
//                    
//                }
//            }
//        }
        return lista_objeto_retorno;
    }

    @Override
    public List<ObjetoRetorno> sindical(boolean baixar, String host) {
        return new ArrayList();
    }

    @Override
    public List<ObjetoRetorno> sigCB(boolean baixar, String host) {
        return new ArrayList();
    }

    @Override
    public String darBaixaSindical(String caminho, Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }

    @Override
    public String darBaixaSigCB(String caminho, Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }

    @Override
    public String darBaixaSigCBSocial(String caminho, Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }

    @Override
    public String darBaixaSicob(String caminho, Usuario usuario) {
        String mensagem = super.baixarArquivo(this.sicob(true, caminho), caminho, usuario);
        return mensagem;
    }

    @Override
    public String darBaixaSicobSocial(String caminho, Usuario usuario) {
        String mensagem = super.baixarArquivoSocial(this.sicob(true, caminho), caminho, usuario);
        return mensagem;
    }

    @Override
    public String darBaixaPadrao(Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }
}
