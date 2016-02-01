package com.tny.game.http;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;
import java.util.Map.Entry;

public class Http {

    /**
     * @uml.property name="logger"
     * @uml.associationEnd multiplicity="(1 1)"
     */
    private Logger logger = LoggerFactory.getLogger(Http.class);

    private static HttpClient httpClient;

    /**
     * @uml.property name="method"
     * @uml.associationEnd
     */
    private HttpRequestBase method;

    private HttpEntity httpEntity;

    /**
     * @uml.property name="paramMap"
     * @uml.associationEnd multiplicity="(0 -1)" elementType="java.lang.Object" qualifier="key:java.lang.String java.lang.Object"
     */
    private Map<String, Object> paramMap = new TreeMap<String, Object>();

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // 将最大连接数增加到200
        cm.setMaxTotal(1000);
        // 将每个路由基础的连接增加到20
        cm.setDefaultMaxPerRoute(100);
        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
    }

    public static Http get(String url) {
        Http http = new Http();
        http.method = new HttpGet(url);
        return http;
    }

    public static Http post(String url) {
        Http http = new Http();
        http.method = new HttpPost(url);
        return http;
    }

    public static Http put(String url) {
        Http http = new Http();
        http.method = new HttpPut(url);
        return http;
    }

    public static Http delete(String url) {
        Http http = new Http();
        http.method = new HttpDelete(url);
        return http;
    }

    public Http setRequestHeader(String key, String value) {
        this.method.setHeader(key, value);
        return this;
    }

    public Http setRequestHeader(Map<String, String> requsetHead) {
        for (Entry<String, String> entry : requsetHead.entrySet())
            this.method.setHeader(entry.getKey(), entry.getValue());
        return this;
    }

    public Http setRequestHeader(Header header) {
        this.method.setHeader(header);
        return this;
    }

    public Http setRequestHeaders(Header[] header) {
        this.method.setHeaders(header);
        return this;
    }

    public Http setRequestHeaders(Collection<Header> headerColl) {
        for (Header header : headerColl)
            this.method.setHeader(header);
        return this;
    }

    public Http setParams(String key, Object... value) {
        this.paramMap.put(key, value);
        return this;
    }

    public Http setParams(Map<String, ?> param) {
        for (Entry<String, ?> entry : param.entrySet())
            this.setParams(entry.getKey(), entry.getValue());
        return this;
    }

    public URI getURI() {
        System.out.println(this.method.getRequestLine());
        return this.method.getURI();
    }

    private void setParam() throws UnsupportedEncodingException {
        if (!(this.method instanceof HttpEntityEnclosingRequestBase))
            return;
        if (this.httpEntity != null) {
            ((HttpEntityEnclosingRequestBase) this.method).setEntity(this.httpEntity);
        } else if (!this.paramMap.isEmpty()) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (Entry<String, Object> entry : this.paramMap.entrySet()) {
                Object value = entry.getValue();
                Object[] values = null;
                if (value.getClass().isArray()) {
                    values = (Object[]) value;
                } else {
                    values = new Object[]{value};
                }
                for (Object v : values) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), v.toString()));
                }
            }
            ((HttpPost) this.method).setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        }

    }

    public Http setEntity(HttpEntity entity) {
        this.httpEntity = entity;
        return this;
    }

    public HttpResponse requset() {
        try {
            this.setParam();
            return httpClient.execute(this.method);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String requestString() {
        try {
            this.setParam();
            return httpClient.execute(this.method, new BasicResponseHandler());
        } catch (Exception e) {
            this.logger.error("http requset exception", e);
        }
        return null;
    }

    // ip=192.168.21.179&uid=&ports=3100&dir=develop
    public static void main(String[] args) {
        /*
         * System.out.println(Http.get("http://192.168.14.112/redalert")
		 * .setParams("ip", "192.168.21.179")
		 * .setParams("uid", "3100")
		 * .setParams("dir", "develop")
		 * .getURI());
		 * System.out
		 * .println(Http
		 * .get("http://192.168.14.112/redalert/?ip=192.168.21.179&uid=&ports=3100&dir=develop")
		 * .setParams("ip", "192.168.21.179")
		 * .setParams("uid", "3100").setParams("dir", "develop")
		 * .requestString());
		 */

        System.out
                .println(Http
                        .post("http://localhost:8080/server/replace")
                        .setParams("serverID", 111, 2222)
                        .setParams("base64Content", "gggg", "kkk")
                        .requestString());
    }

}
