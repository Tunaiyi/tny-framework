package com.tny.game.suite.cache;

import com.google.protobuf.Message;
import com.tny.game.common.utils.Logs;
import com.tny.game.common.collection.CopyOnWriteMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Blob;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Kun Yang on 2017/1/18.
 */
public class ProtobufTableMapper {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProtobufTableMapper.class);

    private static Map<String, ProtobufTableMapper> tableMapperMap = new CopyOnWriteMap<>();

    private String table;

    private ProtoCacheFormatter<?, ? extends Message> formatter;

    public static Optional<ProtobufTableMapper> mapper(String table) {
        return Optional.ofNullable(tableMapperMap.get(table));
    }

    public static ProtobufTableMapper loadOrCreate(String table, ProtoCacheFormatter<?, ? extends Message> formatter) {
        ProtobufTableMapper mapper = new ProtobufTableMapper(table, formatter);
        ProtobufTableMapper old = tableMapperMap.putIfAbsent(mapper.getTable(), mapper);
        if (old != null && old.getFormatter() != formatter) {
            throw new IllegalArgumentException(
                    Logs.format("存在两个相同的表 {} 对应不同的formatter {} 与 {}", table, formatter.getClass(), old.getFormatter().getClass()));
        }
        return old != null ? old : mapper;
    }

    private ProtobufTableMapper(String table, ProtoCacheFormatter<?, ? extends Message> formatter) {
        this.table = table;
        this.formatter = formatter;
    }

    public String getTable() {
        return table;
    }

    public ProtoCacheFormatter<?, ? extends Message> getFormatter() {
        return formatter;
    }

    public Message parser(Blob data) {
        try {
            return this.formatter.bytes2Proto(data.getBytes(1L, (int) data.length()));
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }
}
