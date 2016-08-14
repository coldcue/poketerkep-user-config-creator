package com.botcrator.guerillamail;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class GuerillaMailClient {
    private final Logger logger = Logger.getLogger(GuerillaMailClient.class.getSimpleName());

    private CloseableHttpClient httpClient;
    private RequestConfig requestConfig;
    private String ip;

    public GuerillaMailClient(String ip, InetSocketAddress proxy) throws Exception {
        this.ip = ip;

        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create().register("http", new MyConnectionSocketFactory(proxy)).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
        HttpClientContext clientContext = HttpClientContext.create();
        clientContext.setAttribute("socks.address", proxy);

        httpClient = HttpClients.custom()
                .disableAuthCaching()
                .disableContentCompression()
                .disableAutomaticRetries()
                .disableRedirectHandling()
                .setConnectionManager(cm).build();

        requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.BEST_MATCH)
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        //Init
        String get_email_address = sendRequest("get_email_address", false);

        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        Map map = mapper.readValue(get_email_address, Map.class);

        logger.info("Default email address is: " + map.get("email_addr"));
    }

    public GuerillaMailClient() throws Exception {

        HttpClientContext clientContext = HttpClientContext.create();

        httpClient = HttpClients.custom()
                .disableAuthCaching()
                .disableContentCompression()
                .disableAutomaticRetries()
                .disableRedirectHandling()
                .build();

        requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.BEST_MATCH)
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        //Init
        String get_email_address = sendRequest("get_email_address", false);

        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        Map map = mapper.readValue(get_email_address, Map.class);

        logger.info("Default email address is: " + map.get("email_addr"));
    }

    public void setEmailUser(String emailUser) throws Exception {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("email_user", emailUser));
        params.add(new BasicNameValuePair("lang", "en"));

        String set_email_user = sendRequest("set_email_user", params, true);

        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        Map map = mapper.readValue(set_email_user, Map.class);

        logger.info("Email address is now: " + map.get("email_addr"));
    }

    public int checkTwitchEmail() throws Exception {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("offset", "0"));

        String check_mail = sendRequest("get_email_list", params, false);
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        Map map = mapper.readValue(check_mail, Map.class);

        //noinspection unchecked
        ArrayList<LinkedHashMap<String, String>> list = (ArrayList<LinkedHashMap<String, String>>) map.get("list");

        for (LinkedHashMap<String, String> email : list) {
            if (Pattern.matches("^.*Twitch.*", email.get("mail_subject"))) {
                logger.info("Twitch e-mail has arrived!");
                return Integer.parseInt(email.get("mail_id"));
            }
        }

        logger.info("E-mail hasn't arrived yet...");
        return 0;
    }

    public String fetchEmail(int id) throws Exception {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("email_id", String.valueOf(id)));

        String fetch_email = sendRequest("fetch_email", params, false);

        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        Map map = mapper.readValue(fetch_email, Map.class);

        return (String) map.get("mail_body");
    }

    private String sendRequest(String function, boolean post) throws Exception {
        return sendRequest(function, new ArrayList<>(), post);
    }

    private String sendRequest(String function, List<NameValuePair> params, boolean post) throws Exception {
        params.add(new BasicNameValuePair("f", function));
        params.add(new BasicNameValuePair("ip", ip));
        params.add(new BasicNameValuePair("agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:28.0) Gecko/20100101 Firefox/28.0"));

        String asd = URLEncodedUtils.format(params, "UTF-8");
        String uri = "http://api.guerrillamail.com/ajax.php?" + asd;

        CloseableHttpResponse httpResponse;
        if (post) {
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setConfig(requestConfig);
            httpResponse = httpClient.execute(httpPost);
        } else {
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setConfig(requestConfig);
            httpResponse = httpClient.execute(httpGet);
        }

        InputStream content = httpResponse.getEntity().getContent();

        String body = IOUtils.toString(content);
        httpResponse.close();
        if (Pattern.compile(".*captcha.*", Pattern.DOTALL).matcher(body).matches()) {
            throw new Exception("GuerillaMail Captcha detected");
        }
        return body;
    }

    public void close() throws IOException {
        httpClient.close();
    }

    static class MyConnectionSocketFactory implements ConnectionSocketFactory {

        private final InetSocketAddress proxyAddress;

        public MyConnectionSocketFactory(InetSocketAddress proxy) {
            this.proxyAddress = proxy;
        }

        @Override
        public Socket createSocket(final HttpContext context) throws IOException {
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, proxyAddress);
            return new Socket(proxy);
        }

        @Override
        public Socket connectSocket(
                final int connectTimeout,
                final Socket socket,
                final HttpHost host,
                final InetSocketAddress remoteAddress,
                final InetSocketAddress localAddress,
                final HttpContext context) throws IOException {
            Socket sock;
            if (socket != null) {
                sock = socket;
            } else {
                sock = createSocket(context);
            }
            if (localAddress != null) {
                sock.bind(localAddress);
            }
            try {
                sock.connect(remoteAddress, connectTimeout);
            } catch (SocketTimeoutException ex) {
                throw new ConnectTimeoutException(ex, host, remoteAddress.getAddress());
            }
            return sock;
        }

    }
}
