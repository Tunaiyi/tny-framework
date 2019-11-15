package com.tny.game.cache.mysql;

import com.tny.game.cache.*;
import org.slf4j.*;

import javax.sql.rowset.serial.*;
import java.io.*;
import java.sql.*;

public class DBCacheItem<T> extends RawCacheItem<T, Blob> {

    private static final long serialVersionUID = 1L;

    private final static Logger logger = LoggerFactory.getLogger(DBCacheClient.class);

    private static final int OBJECT = 0;
    private static final int BYTE_ARRAY = 1;

    private String key;
    private int flags;
    private Blob data;
    private long expire;
    private long version;
    private long saveAt;

    private volatile Object dataObject;

    protected DBCacheItem(CacheItem<?> item) {
        this(item.getKey(), item.getData(), item.getVersion(), item.getExpire());
    }

    protected DBCacheItem(String key, Object data, long version, long expire) {
        this.key = key;
        this.version = version;
        this.expire = expire <= 0L ? 0L : System.currentTimeMillis() + expire;
        this.saveAt = System.currentTimeMillis();
        this.format(data);
    }

    public DBCacheItem() {
    }

    protected void format(Object data) {
        if (data != null) {
            byte[] bytes = null;
            if (data instanceof byte[]) {
                bytes = (byte[]) data;
                this.flags = BYTE_ARRAY;
            } else {
                bytes = this.object2Bytes(data);
                this.flags = OBJECT;
            }
            try {
                if (bytes != null) {
                    this.data = new SerialBlob(bytes);
                } else {
                    logger.warn("{} date is null", this.key);
                }
            } catch (SerialException e) {
                logger.error("new SerialBlob exception", e);
            } catch (SQLException e) {
                logger.error("new SerialBlob exception", e);
            }
        }
    }

    @Override
    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public Blob getData() {
        return this.data;
    }

    public Object getObject() {
        if (this.data == null)
            return null;
        if (this.dataObject != null)
            return this.dataObject;
        synchronized (this) {
            if (this.dataObject != null)
                return this.dataObject;
            if (this.flags == OBJECT) {
                this.dataObject = this.bytes2Object();
            } else {
                try {
                    this.dataObject = this.data.getBytes(1, (int) this.data.length());
                } catch (SQLException e) {
                    logger.error("SerialBlob getBytes exception", e);
                }
            }
            return this.dataObject;
        }
    }

    protected void setData(Blob data) {
        this.data = data;
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    protected void setVersion(long version) {
        this.version = version;
    }

    @Override
    public long getExpire() {
        return this.expire;
    }

    public int getFlags() {
        return this.flags;
    }

    public long getSaveAt() {
        return this.saveAt;
    }

    protected void setFlags(int flags) {
        this.flags = flags;
    }

    protected void setSaveAt(long saveAt) {
        this.saveAt = saveAt;
    }

    protected void setExpire(long expire) {
        this.expire = expire;
    }

    public int getHash() {
        return this.key.hashCode();
    }

    protected void setHash(int set) {
    }

    public boolean isItemExpire() {
        return this.expire != 0 && System.currentTimeMillis() > this.expire;
    }

    private byte[] object2Bytes(Object object) {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutputStream objectOut = new ObjectOutputStream(byteOut)) {
            objectOut.writeObject(object);
            return byteOut.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object bytes2Object() {
        if (this.data == null)
            return null;
        try (ObjectInputStream objectIn = new ObjectInputStream(this.data.getBinaryStream())) {
            Object result = objectIn.readObject();
            return result;
        } catch (Exception e) {
            logger.error("item2Object", e);
            return null;
        }
    }

}
