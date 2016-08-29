package com.tny.game.web.converter;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * Created by Kun Yang on 16/8/23.
 */
@SuppressWarnings("rawtypes")
public class MapFormHttpMessageConverter implements HttpMessageConverter<Map<String, ?>> {

    private static final byte[] BOUNDARY_CHARS =
            new byte[]{'-', '_', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
                    'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A',
                    'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                    'V', 'W', 'X', 'Y', 'Z'};

    private final Random rnd = new Random();

    private Charset charset = Charset.forName("UTF-8");

    private List<MediaType> supportedMediaTypes = new ArrayList<>();

    private List<HttpMessageConverter<?>> partConverters = new ArrayList<>();

    public MapFormHttpMessageConverter() {
        this.supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        this.supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
        this.supportedMediaTypes.add(new MediaType("application", "x-tar"));

        this.partConverters.add(new ByteArrayHttpMessageConverter());
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        this.partConverters.add(stringHttpMessageConverter);
        this.partConverters.add(new ResourceHttpMessageConverter());
    }

    /**
     * Set the message body converters to use. These converters are used to convert objects to MIME parts.
     */
    public final void setPartConverters(List<HttpMessageConverter<?>> partConverters) {
        Assert.notEmpty(partConverters, "'partConverters' must not be empty");
        this.partConverters = partConverters;
    }

    /**
     * Add a message body converter. Such a converters is used to convert objects to MIME parts.
     */
    public final void addPartConverter(HttpMessageConverter<?> partConverter) {
        Assert.notNull(partConverter, "'partConverter' must not be NULL");
        this.partConverters.add(partConverter);
    }

    /**
     * Sets the character set used for writing form data.
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        if (!Map.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (mediaType == null) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            // we can't read multipart
            if (!supportedMediaType.equals(MediaType.MULTIPART_FORM_DATA) &&
                    supportedMediaType.includes(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        if (!MultiValueMap.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (mediaType == null || MediaType.ALL.equals(mediaType)) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.isCompatibleWith(mediaType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set the list of {@link MediaType} objects supported by this converter.
     */
    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        this.supportedMediaTypes = supportedMediaTypes;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList(this.supportedMediaTypes);
    }

    @Override
    public Map<String, ?> read(Class<? extends Map<String, ?>> clazz,
                               HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

        MediaType contentType = inputMessage.getHeaders().getContentType();
        Charset charset = contentType.getCharSet() != null ? contentType.getCharSet() : this.charset;
        String body = StreamUtils.copyToString(inputMessage.getBody(), charset);

        String[] pairs = StringUtils.tokenizeToStringArray(body, "&");

        if (MultiValueMap.class.isAssignableFrom(clazz)) {
            MultiValueMap<String, String> result = new LinkedMultiValueMap<>(pairs.length);
            for (String pair : pairs) {
                int idx = pair.indexOf('=');
                if (idx == -1) {
                    result.add(URLDecoder.decode(pair, charset.name()), null);
                } else {
                    String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
                    String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
                    result.add(name, value);
                }
            }
            return result;
        } else {
            Map<String, String> result = new HashMap<>(pairs.length);
            for (String pair : pairs) {
                int idx = pair.indexOf('=');
                if (idx == -1) {
                    String name = URLDecoder.decode(pair, charset.name());
                    if (!StringUtils.isEmpty(name))
                        result.put(name, null);
                } else {
                    String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
                    String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
                    if (!StringUtils.isEmpty(name)) {
                        result.put(name, value);
                    }
                }
            }
            return result;
        }
    }

    private MultiValueMap<String, ?> map2MultiValueMap(Map<String, ?> map) {
        MultiValueMap<String, Object> multiMap = new LinkedMultiValueMap<>();
        for (Entry<String, ?> entry : map.entrySet()) {
            multiMap.add(entry.getKey(), entry.getValue());
        }
        return multiMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(Map<String, ?> map, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        if (!(map instanceof MultiValueMap)) {
            map = map2MultiValueMap(map);
        }
        if (!isMultipart(map, contentType)) {
            writeForm((MultiValueMap<String, String>) map, contentType, outputMessage);
        } else {
            writeMultipart((MultiValueMap<String, Object>) map, outputMessage);
        }
    }

    private boolean isMultipart(Map<String, ?> map, MediaType contentType) {
        if (contentType != null) {
            return MediaType.MULTIPART_FORM_DATA.equals(contentType);
        }
        for (String name : map.keySet()) {
            Object part = map.get(name);
            if (part instanceof Iterable) {
                for (Object value : (Iterable<?>) part) {
                    if (value != null && !(value instanceof String)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void writeForm(MultiValueMap<String, String> form, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException {
        Charset charset;
        if (contentType != null) {
            outputMessage.getHeaders().setContentType(contentType);
            charset = contentType.getCharSet() != null ? contentType.getCharSet() : this.charset;
        } else {
            outputMessage.getHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            charset = this.charset;
        }
        StringBuilder builder = new StringBuilder();
        for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext(); ) {
            String name = nameIterator.next();
            for (Iterator<String> valueIterator = form.get(name).iterator(); valueIterator.hasNext(); ) {
                String value = valueIterator.next();
                builder.append(URLEncoder.encode(name, charset.name()));
                if (value != null) {
                    builder.append('=');
                    builder.append(URLEncoder.encode(value, charset.name()));
                    if (valueIterator.hasNext()) {
                        builder.append('&');
                    }
                }
            }
            if (nameIterator.hasNext()) {
                builder.append('&');
            }
        }
        byte[] bytes = builder.toString().getBytes(charset.name());
        outputMessage.getHeaders().setContentLength(bytes.length);
        StreamUtils.copy(bytes, outputMessage.getBody());
    }

    private void writeMultipart(MultiValueMap<String, Object> parts, HttpOutputMessage outputMessage)
            throws IOException {
        byte[] boundary = generateMultipartBoundary();

        Map<String, String> parameters = Collections.singletonMap("boundary", new String(boundary, "US-ASCII"));
        MediaType contentType = new MediaType(MediaType.MULTIPART_FORM_DATA, parameters);
        outputMessage.getHeaders().setContentType(contentType);

        writeParts(outputMessage.getBody(), parts, boundary);
        writeEnd(boundary, outputMessage.getBody());
    }

    private void writeParts(OutputStream os, MultiValueMap<String, Object> parts, byte[] boundary) throws IOException {
        for (Map.Entry<String, List<Object>> entry : parts.entrySet()) {
            String name = entry.getKey();
            for (Object part : entry.getValue()) {
                if (part != null) {
                    writeBoundary(boundary, os);
                    HttpEntity entity = getEntity(part);
                    writePart(name, entity, os);
                    writeNewLine(os);
                }
            }
        }
    }

    private void writeBoundary(byte[] boundary, OutputStream os) throws IOException {
        os.write('-');
        os.write('-');
        os.write(boundary);
        writeNewLine(os);
    }

    @SuppressWarnings("unchecked")
    private HttpEntity getEntity(Object part) {
        if (part instanceof HttpEntity) {
            return (HttpEntity) part;
        } else {
            return new HttpEntity(part);
        }
    }

    @SuppressWarnings("unchecked")
    private void writePart(String name, HttpEntity partEntity, OutputStream os) throws IOException {
        Object partBody = partEntity.getBody();
        Class<?> partType = partBody.getClass();
        HttpHeaders partHeaders = partEntity.getHeaders();
        MediaType partContentType = partHeaders.getContentType();
        for (HttpMessageConverter messageConverter : partConverters) {
            if (messageConverter.canWrite(partType, partContentType)) {
                HttpOutputMessage multipartOutputMessage = new MultipartHttpOutputMessage(os);
                multipartOutputMessage.getHeaders().setContentDispositionFormData(name, getFilename(partBody));
                if (!partHeaders.isEmpty()) {
                    multipartOutputMessage.getHeaders().putAll(partHeaders);
                }
                messageConverter.write(partBody, partContentType, multipartOutputMessage);
                return;
            }
        }
        throw new HttpMessageNotWritableException(
                "Could not write request: no suitable HttpMessageConverter found for request type [" +
                        partType.getName() + "]");
    }

    private void writeEnd(byte[] boundary, OutputStream os) throws IOException {
        os.write('-');
        os.write('-');
        os.write(boundary);
        os.write('-');
        os.write('-');
        writeNewLine(os);
    }

    private void writeNewLine(OutputStream os) throws IOException {
        os.write('\r');
        os.write('\n');
    }

    /**
     * Generate a multipart boundary.
     * <p>The default implementation returns a random boundary.
     * Can be overridden in subclasses.
     */
    protected byte[] generateMultipartBoundary() {
        byte[] boundary = new byte[rnd.nextInt(11) + 30];
        for (int i = 0; i < boundary.length; i++) {
            boundary[i] = BOUNDARY_CHARS[rnd.nextInt(BOUNDARY_CHARS.length)];
        }
        return boundary;
    }

    /**
     * Return the filename of the given multipart part. This value will be used for the
     * {@code Content-Disposition} header.
     * <p>The default implementation returns {@link Resource#getFilename()} if the part is a
     * {@code Resource}, and {@code null} in other cases. Can be overridden in subclasses.
     *
     * @param part the part to determine the file name for
     * @return the filename, or {@code null} if not known
     */
    protected String getFilename(Object part) {
        if (part instanceof Resource) {
            Resource resource = (Resource) part;
            return resource.getFilename();
        } else {
            return null;
        }
    }

    /**
     * Implementation of {@link org.springframework.http.HttpOutputMessage} used for writing multipart data.
     */
    private class MultipartHttpOutputMessage implements HttpOutputMessage {

        private final HttpHeaders headers = new HttpHeaders();

        private final OutputStream os;

        private boolean headersWritten = false;

        public MultipartHttpOutputMessage(OutputStream os) {
            this.os = os;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headersWritten ? HttpHeaders.readOnlyHttpHeaders(headers) : this.headers;
        }

        @Override
        public OutputStream getBody() throws IOException {
            writeHeaders();
            return this.os;
        }

        private void writeHeaders() throws IOException {
            if (!this.headersWritten) {
                for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                    byte[] headerName = getAsciiBytes(entry.getKey());
                    for (String headerValueString : entry.getValue()) {
                        byte[] headerValue = getAsciiBytes(headerValueString);
                        os.write(headerName);
                        os.write(':');
                        os.write(' ');
                        os.write(headerValue);
                        writeNewLine(os);
                    }
                }
                writeNewLine(os);
                this.headersWritten = true;
            }
        }

        protected byte[] getAsciiBytes(String name) {
            try {
                return name.getBytes("US-ASCII");
            } catch (UnsupportedEncodingException ex) {
                // should not happen, US-ASCII is always supported
                throw new IllegalStateException(ex);
            }
        }
    }

}
