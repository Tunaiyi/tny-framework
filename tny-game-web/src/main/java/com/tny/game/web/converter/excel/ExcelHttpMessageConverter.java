package com.tny.game.web.converter.excel;

import org.apache.poi.ss.usermodel.*;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.util.StreamUtils;

import java.io.*;

public class ExcelHttpMessageConverter extends AbstractHttpMessageConverter<Workbook> {

    /**
     * Creates a new instance of the {@code ByteArrayHttpMessageConverter}.
     */
    public ExcelHttpMessageConverter() {
        super(new MediaType("application", "octet-stream"));
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Workbook.class.isAssignableFrom(clazz);
    }

    @Override
    protected Long getContentLength(Workbook bytes, MediaType contentType) {
        return null;
    }

    @Override
    protected void writeInternal(Workbook book, HttpOutputMessage outputMessage) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(1024 * 1024);
        book.write(stream);
        int length = stream.size();
        outputMessage.getHeaders().setContentLength(length);
        stream.writeTo(outputMessage.getBody());
    }

    @Override
    protected Workbook readInternal(Class<? extends Workbook> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        long contentLength = inputMessage.getHeaders().getContentLength();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(contentLength >= 0 ? (int)contentLength : StreamUtils.BUFFER_SIZE);
        StreamUtils.copy(inputMessage.getBody(), bos);
        return WorkbookFactory.create(inputMessage.getBody());
    }

}
