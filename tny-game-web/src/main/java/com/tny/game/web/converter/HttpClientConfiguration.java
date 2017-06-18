package com.tny.game.web.converter;

import com.tny.game.common.thread.CoreThreadFactory;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.DefaultCookieSpec;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@Profile({"web.client"})
public class HttpClientConfiguration {

    private static final String HTTP_PROXY_HOST = "tny.common.http_client.proxy.host";
    private static final String HTTP_PROXY_PORT = "tny.common.http_client.proxy.port";

    private ScheduledExecutorService idleConnectionMonitor = Executors.newSingleThreadScheduledExecutor(
            new CoreThreadFactory("IdleConnectionMonitor", true));

    private ConnectionKeepAliveStrategy keepAliveStrategy = (response, context) -> {
        HeaderElementIterator it = new BasicHeaderElementIterator(
                response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
            HeaderElement he = it.nextElement();
            String param = he.getName();
            String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
                try {
                    return Long.parseLong(value) * 1000;
                } catch (NumberFormatException ignore) {
                }
            }
        }
        return 30 * 1000;
    };

    private SSLContext newSSLContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        // set up a TrustManager that trusts everything
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }

        }}, new SecureRandom());
        return sslContext;
    }

    private static class EasyCookieSpec extends DefaultCookieSpec {
        @Override
        public void validate(Cookie arg0, CookieOrigin arg1) throws MalformedCookieException {
            //allow all cookies
        }
    }

    private static class EasySpecProvider implements CookieSpecProvider {
        @Override
        public CookieSpec create(HttpContext context) {
            return new EasyCookieSpec();
        }
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory requestFactory() throws Exception {
        SSLContext sslContext = newSSLContext();
        PlainConnectionSocketFactory httpSocketFactory = new PlainConnectionSocketFactory();
        SSLConnectionSocketFactory httpsSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", httpSocketFactory)
                .register("https", httpsSocketFactory)
                .build();
        final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        cm.setMaxTotal(300);
        cm.setDefaultMaxPerRoute(300);

        Registry<CookieSpecProvider> cookieRegistry = RegistryBuilder.<CookieSpecProvider>create()
                .register("easy", new EasySpecProvider())
                .build();

        CookieStore cookieStore = new BasicCookieStore();

        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec("easy")
                .setConnectionRequestTimeout(30 * 1000)
                .setSocketTimeout(30 * 1000)
                .setConnectTimeout(30 * 1000)
                .build();
        HttpClientBuilder builder = HttpClients.custom();
        String host = System.getProperty(HTTP_PROXY_HOST);
        String port = System.getProperty(HTTP_PROXY_PORT);
        if (host != null && port != null) {
            builder.setProxy(new HttpHost(host, Integer.parseInt(port)));
        }

        final CloseableHttpClient httpClient = builder
                .setConnectionTimeToLive(10, TimeUnit.MINUTES)
                .setConnectionManager(cm)
                .setSSLContext(sslContext)
                .setDefaultCookieStore(cookieStore)
                .setDefaultCookieSpecRegistry(cookieRegistry)
                .setDefaultRequestConfig(requestConfig)
                .setKeepAliveStrategy(this.keepAliveStrategy)
                .build();
        this.idleConnectionMonitor.schedule(new Runnable() {

            @Override
            public void run() {
                try {
                    cm.closeExpiredConnections();
                    cm.closeIdleConnections(30, TimeUnit.SECONDS);
                } finally {
                    HttpClientConfiguration.this.idleConnectionMonitor.schedule(this, 5, TimeUnit.SECONDS);
                }
            }

        }, 5, TimeUnit.SECONDS);
        return new HttpComponentsClientHttpRequestFactory(httpClient) {
            @Override
            protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
                if (HttpMethod.DELETE == httpMethod) {
                    return new HttpDelete(uri);
                }
                return super.createHttpUriRequest(httpMethod, uri);
            }
        };
    }


    @Bean(name = "restTemplate", autowire = Autowire.NO)
    public RestTemplate restTemplate(@Autowired HttpComponentsClientHttpRequestFactory factory) throws Exception {
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }
}
