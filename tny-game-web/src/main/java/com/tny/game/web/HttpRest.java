package com.tny.game.web;

import com.google.common.collect.ImmutableMap;
import com.tny.game.web.converter.*;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.*;

import java.net.*;
import java.util.*;

public class HttpRest {

    private RestTemplate restTemplate;

    public HttpRest() {
        this.restTemplate = new RestTemplate();
    }

    public HttpRest(ClientHttpRequestFactory requestFactory) {
        this.restTemplate = new RestTemplate(requestFactory);
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setConverters(List<HttpMessageConverter<?>> converters) {
        this.restTemplate.setMessageConverters(converters);
    }

    public void setInterceptors(List<ClientHttpRequestInterceptor> interceptors) {
        this.restTemplate.setInterceptors(interceptors);
    }

    public void setErrorHandler(ResponseErrorHandler errorHandler) {
        this.restTemplate.setErrorHandler(errorHandler);
    }

    public <B> B get(String url, Class<B> bodyClass) {
        ResponseEntity<B> value = getEntity(url, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> getEntity(String url, Class<B> bodyClass) {
        return this.getEntity(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, ImmutableMap.of(), bodyClass);
    }

    public <B> B get(String url, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = getEntity(url, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> getEntity(String url, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.getEntity(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, urlVars, bodyClass);
    }

    public <E, B> B get(String url, E entity, Class<B> bodyClass) {
        ResponseEntity<B> value = getEntity(url, entity, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> getEntity(String url, E entity, Class<B> bodyClass) {
        return this.getEntity(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, entity, ImmutableMap.of(), bodyClass);
    }

    public <E, B> B get(String url, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = getEntity(url, entity, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> getEntity(String url, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.getEntity(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, entity, urlVars, bodyClass);
    }

    public <B> B get(String url, MediaType accept, MediaType contentType, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = getEntity(url, accept, contentType, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> getEntity(String url, MediaType accept, MediaType contentType, Map<String, String> urlVars, Class<B> bodyClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(accept));
        headers.setContentType(contentType);
        return this.getEntity(url, headers, urlVars, bodyClass);
    }

    public <E, B> B get(String url, MediaType accept, MediaType contentType, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = getEntity(url, accept, contentType, entity, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> getEntity(String url, MediaType accept, MediaType contentType, E entity, Map<String, String> urlVars,
            Class<B> bodyClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(accept));
        headers.setContentType(contentType);
        return this.getEntity(url, headers, entity, urlVars, bodyClass);
    }

    public <E, B> B get(String url, HttpHeaders headers, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = this.getEntity(url, headers, entity, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> getEntity(String url, HttpHeaders headers, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.getEntity(url, new HttpEntity<>(entity, headers), urlVars, bodyClass);
    }

    public <B> B get(String url, HttpHeaders headers, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = this.getEntity(url, headers, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> getEntity(String url, HttpHeaders headers, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.getEntity(url, new HttpEntity<>(headers), urlVars, bodyClass);
    }

    public <E, B> B get(String url, HttpEntity<E> entity, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = this.getEntity(url, entity, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> getEntity(String url, HttpEntity<E> entity, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.restTemplate.exchange(url, HttpMethod.GET, entity, bodyClass, urlVars);
    }

    public <B> B post(String url, Class<B> bodyClass) {
        ResponseEntity<B> value = postEntity(url, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> postEntity(String url, Class<B> bodyClass) {
        return this.postEntity(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, ImmutableMap.of(), bodyClass);
    }

    public <B> B post(String url, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = postEntity(url, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> postEntity(String url, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.postEntity(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, urlVars, bodyClass);
    }

    public <E, B> B post(String url, E entity, Class<B> bodyClass) {
        ResponseEntity<B> value = postEntity(url, entity, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> postEntity(String url, E entity, Class<B> bodyClass) {
        return this.postEntity(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, entity, ImmutableMap.of(), bodyClass);
    }

    public <E, B> B post(String url, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = postEntity(url, entity, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> postEntity(String url, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.postEntity(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, entity, urlVars, bodyClass);
    }

    public <B> B post(String url, MediaType accept, MediaType contentType, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = postEntity(url, accept, contentType, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> postEntity(String url, MediaType accept, MediaType contentType, Map<String, String> urlVars, Class<B> bodyClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(accept));
        headers.setContentType(contentType);
        return this.postEntity(url, headers, urlVars, bodyClass);
    }

    public <E, B> B post(String url, MediaType accept, MediaType contentType, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = postEntity(url, accept, contentType, entity, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> postEntity(String url, MediaType accept, MediaType contentType, E entity, Map<String, String> urlVars,
            Class<B> bodyClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(accept));
        headers.setContentType(contentType);
        return this.postEntity(url, headers, entity, urlVars, bodyClass);
    }

    public <E, B> B post(String url, HttpHeaders headers, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = this.postEntity(url, headers, entity, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> postEntity(String url, HttpHeaders headers, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.postEntity(url, new HttpEntity<>(entity, headers), urlVars, bodyClass);
    }

    public <B> B post(String url, HttpHeaders headers, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = this.postEntity(url, headers, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> postEntity(String url, HttpHeaders headers, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.postEntity(url, new HttpEntity<>(headers), urlVars, bodyClass);
    }

    public <E, B> B post(String url, HttpEntity<E> entity, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = this.postEntity(url, entity, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> postEntity(String url, HttpEntity<E> entity, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.restTemplate.postForEntity(url, entity, bodyClass, urlVars);
    }

    public <B> B put(String url, Class<B> bodyClass) {
        ResponseEntity<B> value = putEntity(url, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> putEntity(String url, Class<B> bodyClass) {
        return this.putEntity(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, ImmutableMap.of(), bodyClass);
    }

    public <B> B put(String url, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = putEntity(url, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> putEntity(String url, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.putEntity(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, urlVars, bodyClass);
    }

    public <E, B> B put(String url, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = putEntity(url, entity, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> putEntity(String url, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.putEntity(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, entity, urlVars, bodyClass);
    }

    public <E, B> B put(String url, E entity, Class<B> bodyClass) {
        ResponseEntity<B> value = putEntity(url, entity, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> putEntity(String url, E entity, Class<B> bodyClass) {
        return this.putEntity(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, entity, ImmutableMap.of(), bodyClass);
    }

    public <B> B put(String url, MediaType accept, MediaType contentType, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = putEntity(url, accept, contentType, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> putEntity(String url, MediaType accept, MediaType contentType, Map<String, String> urlVars, Class<B> bodyClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(accept));
        headers.setContentType(contentType);
        return this.putEntity(url, headers, urlVars, bodyClass);
    }

    public <E, B> B put(String url, MediaType accept, MediaType contentType, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = putEntity(url, accept, contentType, entity, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> putEntity(String url, MediaType accept, MediaType contentType, E entity, Map<String, String> urlVars,
            Class<B> bodyClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(accept));
        headers.setContentType(contentType);
        return this.putEntity(url, headers, entity, urlVars, bodyClass);
    }

    public <E, B> B put(String url, HttpHeaders headers, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = this.putEntity(url, headers, entity, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> putEntity(String url, HttpHeaders headers, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.putEntity(url, new HttpEntity<>(entity, headers), urlVars, bodyClass);
    }

    public <B> B put(String url, HttpHeaders headers, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = this.putEntity(url, headers, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> putEntity(String url, HttpHeaders headers, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.putEntity(url, new HttpEntity<>(headers), urlVars, bodyClass);
    }

    public <E, B> B put(String url, HttpEntity<E> entity, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = this.putEntity(url, entity, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> putEntity(String url, HttpEntity<E> entity, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.restTemplate.exchange(url, HttpMethod.PUT, entity, bodyClass, urlVars);
    }

    public <B> B del(String url, Class<B> bodyClass) {
        ResponseEntity<B> value = delEntity(url, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> delEntity(String url, Class<B> bodyClass) {
        return this.delEntity(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, ImmutableMap.of(), bodyClass);
    }

    public <B> B del(String url, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = delEntity(url, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> delEntity(String url, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.delEntity(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, urlVars, bodyClass);
    }

    public <B> B del(String url, MediaType accept, MediaType contentType, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = delEntity(url, accept, contentType, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> delEntity(String url, MediaType accept, MediaType contentType, Map<String, String> urlVars, Class<B> bodyClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(accept));
        headers.setContentType(contentType);
        return this.delEntity(url, headers, urlVars, bodyClass);
    }

    public <B> B del(String url, HttpHeaders headers, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = this.delEntity(url, headers, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <B> ResponseEntity<B> delEntity(String url, HttpHeaders headers, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), bodyClass, urlVars);
    }

    public <E, B> B exchange(String url, HttpMethod method, HttpHeaders headers, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = this.exchangeEntity(url, method, headers, entity, urlVars, bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> exchangeEntity(String url, HttpMethod method, HttpHeaders headers, E entity, Map<String, String> urlVars,
            Class<B> bodyClass) {
        return this.restTemplate.exchange(url, method, new HttpEntity<>(entity, headers), bodyClass, urlVars);
    }

    public <E, B> B exchange(String url, HttpMethod method, HttpHeaders headers, E entity, Class<B> bodyClass) {
        ResponseEntity<B> value = this.exchangeEntity(url, method, headers, entity, ImmutableMap.of(), bodyClass);
        if (value == null) {
            return null;
        }
        return value.getBody();
    }

    public <E, B> ResponseEntity<B> exchangeEntity(String url, HttpMethod method, HttpHeaders headers, E entity, Class<B> bodyClass) {
        return this.restTemplate.exchange(url, method, new HttpEntity<>(entity, headers), bodyClass, ImmutableMap.of());
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public static String getLocalAddress() {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ip;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getLocalAddress());
        HttpClientConfiguration clientConfiguration = new HttpClientConfiguration();
        HttpRest client = new HttpRest();
        client.setRestTemplate(clientConfiguration.restTemplate(clientConfiguration.requestFactory()));
        client.getRestTemplate().setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders().setDate(System.currentTimeMillis());
            System.out.println(request.getHeaders().getDate());
            System.out.println(request.getURI());
            return execution.execute(request, body);
        }));
        System.out.println(
                client.get("http://192.168.1.113:13600/api/data/{table}/{playerId}", ImmutableMap.of("table", "CPlayer", "playerId", "1000014548"),
                        String.class));
        System.out.println(client.post("http://192.168.1.153:13600/api/script", "System.out.println(\"OK\")", ImmutableMap.of(), String.class));
    }

}
