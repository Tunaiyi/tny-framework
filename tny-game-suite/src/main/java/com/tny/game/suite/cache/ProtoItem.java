package com.tny.game.suite.cache;

import com.tny.game.basics.item.*;
import com.tny.game.cache.*;
import org.slf4j.*;

import java.io.*;
import java.sql.*;

public class ProtoItem implements Blob {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProtoItem.class);

    private byte[] item;

    private Blob blob;

    private Object object;

    private Integer number;

    private int state;

    public ProtoItem(byte[] item, Item<?> object, Integer number, int state) {
        this.item = item;
        this.object = object;
        this.number = number;
        this.state = state;
    }

    public byte[] getItem() {
        return this.item;
    }

    public Integer getNumber() {
        return this.number;
    }

    public Object getObject() {
        return this.object;
    }

    private Blob blob() throws SQLException {
        if (blob != null) {
            return blob;
        }
        return blob = new NoCopyBytesBlob(this.item);
    }

    @Override
    public long length() throws SQLException {
        return blob().length();
    }

    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        return blob().getBytes(pos, length);
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        return blob().getBinaryStream();
    }

    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        return blob().position(pattern, start);
    }

    @Override
    public long position(Blob pattern, long start) throws SQLException {
        return blob().position(pattern, start);
    }

    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        return blob().setBytes(pos, bytes);
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        return blob().setBytes(pos, bytes, offset, len);
    }

    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        return blob().setBinaryStream(pos);
    }

    @Override
    public void truncate(long len) throws SQLException {
        blob().truncate(len);
    }

    @Override
    public void free() throws SQLException {
        blob().free();
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        return blob().getBinaryStream(pos, length);
    }

}
