package br.com.rtools.utilitarios;

import br.com.rtools.endereco.Bairro;
import br.com.rtools.endereco.CepAlias;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.endereco.DescricaoEndereco;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.endereco.Logradouro;
import br.com.rtools.endereco.dao.BairroDao;
import br.com.rtools.endereco.dao.DescricaoEnderecoDao;
import br.com.rtools.endereco.dao.LogradouroDao;
import br.com.rtools.endereco.dao.CidadeDao;
import br.com.rtools.endereco.dao.EnderecoDao;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class CEPService {

    private String cep = "";
    private String cepMemoria = "";
    private Endereco endereco = new Endereco();

    /**
     * http://www.republicavirtual.com.br/cep/exemplos.php
     */
    public void procurar() {
        procurar(null);
    }

    public void procurar(Dao dao) {
        if (cep.isEmpty()) {
            return;
        }
        EnderecoDao enderecoDB = new EnderecoDao();
        CidadeDao cidadeDB = new CidadeDao();
        List<Endereco> listaEnderecos = (List<Endereco>) enderecoDB.pesquisaEnderecoCep(cep);
        if (listaEnderecos == null || listaEnderecos.isEmpty()) {
            if (cepMemoria.equals(cep)) {
                return;
            }
            if (cepMemoria.equals("")) {
                cepMemoria = cep;
            }
            cep = cep.replace("-", "");
            String urlString = "http://cep.republicavirtual.com.br/web_cep.php?cep=" + cep + "&formato=query_string";
            // os parametros a serem enviados
            Properties parameters = new Properties();
            parameters.setProperty("cep", cep);
            parameters.setProperty("formato", "xml");
            Iterator i = parameters.keySet().iterator();
            int counter = 0;
            while (i.hasNext()) {
                String name = (String) i.next();
                String value = parameters.getProperty(name);
                urlString += (++counter == 1 ? "?" : "&") + name + "=" + value;
            }
            Boolean autocommit = false;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Request-Method", "GET");
                connection.setDoInput(true);
                connection.setDoOutput(false);
                connection.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder newData = new StringBuilder();
                String s = "";
                while (null != ((s = br.readLine()))) {
                    newData.append(s);
                }
                br.close();
                XStream xstream = new XStream(new DomDriver());
                Annotations.configureAliases(xstream, CepAlias.class);
                xstream.alias("webservicecep", CepAlias.class);
                CepAlias cepAlias = (CepAlias) xstream.fromXML(newData.toString());
                if (dao == null) {
                    autocommit = true;
                    dao = new Dao();
                }
                Cidade cidade = cidadeDB.pesquisaCidadePorEstadoCidade(cepAlias.getUf(), cepAlias.getCidade());
                if (cidade == null) {
                    return;
                }
                LogradouroDao logradouroDao = new LogradouroDao();
                Logradouro logradouro = logradouroDao.pesquisaLogradouroPorDescricao(cepAlias.getTipo_logradouro());
                if (logradouro == null) {
                    if (!cepAlias.getTipo_logradouro().isEmpty()) {
                        logradouro = new Logradouro();
                        logradouro.setDescricao(cepAlias.getTipo_logradouro());
                        dao.save(logradouro, autocommit);
                    }
                }
                BairroDao bairroDao = new BairroDao();
                Bairro bairro = bairroDao.find(cepAlias.getBairro());
                if (bairro == null) {
                    if (!cepAlias.getBairro().isEmpty()) {
                        bairro = new Bairro();
                        bairro.setDescricao(cepAlias.getBairro());
                        dao.save(bairro, autocommit);
                    }
                }
                DescricaoEnderecoDao descricaoEnderecoDao = new DescricaoEnderecoDao();
                DescricaoEndereco descricaoEndereco = descricaoEnderecoDao.find(cepAlias.getLogradouro());
                if (descricaoEndereco == null) {
                    if (!cepAlias.getLogradouro().isEmpty()) {
                        descricaoEndereco = new DescricaoEndereco();
                        descricaoEndereco.setDescricao(cepAlias.getLogradouro());
                        dao.save(descricaoEndereco, autocommit);
                    }
                }
                if (!cep.isEmpty() && bairro != null && logradouro != null && descricaoEndereco != null) {
                    endereco = new Endereco();
                    endereco.setCep(cep);
                    endereco.setBairro(bairro);
                    endereco.setCidade(cidade);
                    endereco.setDescricaoEndereco(descricaoEndereco);
                    endereco.setLogradouro(logradouro);
                    List list = enderecoDB.pesquisaEndereco(
                            endereco.getLogradouro().getId(),
                            endereco.getDescricaoEndereco().getId(),
                            endereco.getBairro().getId(),
                            endereco.getCidade().getId()
                    );
                    if (list.isEmpty()) {
                        dao.save(endereco, autocommit);
                    } else {
                        endereco = new Endereco();
                        list.clear();
                    }
                }
            } catch (IOException e) {
            }
        } else {
            endereco = new Endereco();
            if (listaEnderecos.size() == 1) {
                endereco = listaEnderecos.get(0);
            } else {
                endereco.setBairro(listaEnderecos.get(0).getBairro());
            }
        }
        cep = "";
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCepMemoria() {
        return cepMemoria;
    }

    public void setCepMemoria(String cepMemoria) {
        this.cepMemoria = cepMemoria;
    }
}
