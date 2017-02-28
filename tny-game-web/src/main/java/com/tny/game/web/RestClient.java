package com.tny.game.web;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RestClient {

    private RestTemplate restTemplate;

    public RestClient() {
        this.restTemplate = new RestTemplate();
    }

    public RestClient(ClientHttpRequestFactory requestFactory) {
        this.restTemplate = new RestTemplate(requestFactory);
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setConverterList(List<HttpMessageConverter<?>> converters) {
        this.restTemplate.setMessageConverters(converters);
    }

    public void setErrorHandler(ResponseErrorHandler errorHandler) {
        this.restTemplate.setErrorHandler(errorHandler);
    }

    // MediaType
    public <E, B> B execute(String url, HttpMethod method, MediaType mediaType, E entity, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.execute(url, method, mediaType, mediaType, entity, urlVars, bodyClass);
    }

    public <E, B> B execute(String url, HttpMethod method, MediaType mediaType, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.execute(url, method, mediaType, mediaType, null, urlVars, bodyClass);
    }

    public <E, B> B execute(String url, HttpMethod method, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.execute(url, method, new HttpHeaders(), null, urlVars, bodyClass);
    }

    public <E, B> B execute(String url, HttpMethod method, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.execute(url, method, new HttpHeaders(), null, urlVars, bodyClass);
    }

    public <E, B> B execute(String url, HttpMethod method, E entity, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.execute(url, method, new HttpHeaders(), entity, urlVars, bodyClass);
    }

    public <E, B> B execute(String url, HttpMethod method, MediaType mediaType, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.execute(url, method, mediaType, mediaType, null, urlVars, bodyClass);
    }

    public <E, B> B execute(String url, HttpMethod method, MediaType mediaType, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(mediaType));
        headers.setContentType(mediaType);
        return this.execute(url, method, new HttpEntity<Object>(entity, headers), urlVars, bodyClass);
    }

    // MediaType
    public <E, B> B execute(String url, HttpMethod method, MediaType accept, MediaType contentType, E entity, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.execute(url, method, accept, contentType, entity, urlVars, bodyClass);
    }

    public <E, B> B execute(String url, HttpMethod method, MediaType accept, MediaType contentType, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.execute(url, method, accept, contentType, null, urlVars, bodyClass);
    }

    public <E, B> B execute(String url, HttpMethod method, MediaType accept, MediaType contentType, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.execute(url, method, accept, contentType, null, urlVars, bodyClass);
    }

    public <E, B> B execute(String url, HttpMethod method, MediaType accept, MediaType contentType, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(accept));
        headers.setContentType(contentType);
        return this.execute(url, method, new HttpEntity<Object>(entity, headers), urlVars, bodyClass);
    }

    // HttpHeaders entity urlVars

    public <E, B> B execute(String url, HttpMethod method, HttpHeaders headers, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.execute(url, method, headers, null, urlVars, bodyClass);
    }

    public <E, B> B execute(String url, HttpMethod method, HttpHeaders headers, E entity, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.execute(url, method, headers, entity, urlVars, bodyClass);
    }

    public <E, B> B execute(String url, HttpMethod method, HttpHeaders headers, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.execute(url, method, headers, null, urlVars, bodyClass);
    }

    public <E, B> B execute(String url, HttpMethod method, HttpHeaders headers, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.execute(url, method, new HttpEntity<Object>(entity, headers), urlVars, bodyClass);
    }

    // HttpEntity

    public <E, B> B execute(String url, HttpMethod method, HttpEntity<E> entity, Class<B> bodyClass) {
        ResponseEntity<B> value = this.restTemplate.exchange(url, method, entity, bodyClass, Collections.emptyMap());
        if (value == null)
            return null;
        return value.getBody();
    }

    public <E, B> B execute(String url, HttpMethod method, HttpEntity<E> entity, Map<String, String> urlVars, Class<B> bodyClass) {
        ResponseEntity<B> value = this.restTemplate.exchange(url, method, entity, bodyClass, urlVars);
        if (value == null)
            return null;
        return value.getBody();
    }

    // MediaType
    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, MediaType mediaType, E entity, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.exchange(url, method, mediaType, mediaType, entity, urlVars, bodyClass);
    }

    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, MediaType mediaType, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.exchange(url, method, mediaType, mediaType, null, urlVars, bodyClass);
    }

    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.exchange(url, method, new HttpHeaders(), null, urlVars, bodyClass);
    }

    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.exchange(url, method, new HttpHeaders(), null, urlVars, bodyClass);
    }

    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, E entity, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.exchange(url, method, new HttpHeaders(), entity, urlVars, bodyClass);
    }

    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, MediaType mediaType, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.exchange(url, method, mediaType, mediaType, null, urlVars, bodyClass);
    }

    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, MediaType mediaType, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(mediaType));
        headers.setContentType(mediaType);
        return this.exchange(url, method, new HttpEntity<Object>(entity, headers), urlVars, bodyClass);
    }

    // MediaType
    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, MediaType accept, MediaType contentType, E entity, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.exchange(url, method, accept, contentType, entity, urlVars, bodyClass);
    }

    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, MediaType accept, MediaType contentType, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.exchange(url, method, accept, contentType, null, urlVars, bodyClass);
    }

    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, MediaType accept, MediaType contentType, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.exchange(url, method, accept, contentType, null, urlVars, bodyClass);
    }

    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, MediaType accept, MediaType contentType, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(accept));
        headers.setContentType(contentType);
        return this.exchange(url, method, new HttpEntity<Object>(entity, headers), urlVars, bodyClass);
    }

    // HttpHeaders entity urlVars

    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, HttpHeaders headers, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.exchange(url, method, headers, null, urlVars, bodyClass);
    }

    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, HttpHeaders headers, E entity, Class<B> bodyClass) {
        Map<String, String> urlVars = Collections.emptyMap();
        return this.exchange(url, method, headers, entity, urlVars, bodyClass);
    }

    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, HttpHeaders headers, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.exchange(url, method, headers, null, urlVars, bodyClass);
    }

    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, HttpHeaders headers, E entity, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.exchange(url, method, new HttpEntity<Object>(entity, headers), urlVars, bodyClass);
    }

    // HttpEntity

    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, HttpEntity<E> entity, Class<B> bodyClass) {
        return this.restTemplate.exchange(url, method, entity, bodyClass, Collections.emptyMap());
    }

    public <E, B> ResponseEntity<B> exchange(String url, HttpMethod method, HttpEntity<E> entity, Map<String, String> urlVars, Class<B> bodyClass) {
        return this.restTemplate.exchange(url, method, entity, bodyClass, urlVars);
    }


}
