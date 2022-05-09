package com.tny.game.demo.core.server;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.codec.annotation.*;
import com.tny.game.codec.protobuf.*;
import com.tny.game.common.digest.binary.*;
import com.tny.game.data.annotation.*;
import com.tny.game.redisson.annotation.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/8 10:47 上午
 */
@RedisObject
@ProtobufClass
@Codable(ProtobufMimeType.PROTOBUF)
@EntityObject
@Document
public class DemoPlayer {

    @Id
    @EntityKey
    @Protobuf(order = 1)
    private long id;

    @Protobuf(order = 2)
    private String name;

    @Protobuf(order = 3)
    private int age;

    @Indexed
    private ObjectId objectId;

    public DemoPlayer() {
    }

    private static final AtomicInteger number = new AtomicInteger();

    public DemoPlayer(long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;

        byte[] objId = new byte[12];
        BytesAide.long2Bytes(Long.MAX_VALUE - number.decrementAndGet(), objId, 0);
        BytesAide.int2Bytes(Integer.MAX_VALUE - number.decrementAndGet(), objId, 8);
        this.objectId = new ObjectId(objId);
    }

    public long getId() {
        return id;
    }

    public DemoPlayer setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public DemoPlayer setName(String name) {
        this.name = name;
        return this;
    }

    public int getAge() {
        return age;
    }

    public DemoPlayer setAge(int age) {
        this.age = age;
        return this;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public DemoPlayer setObjectId(ObjectId objectId) {
        this.objectId = objectId;
        return this;
    }

    public static void main(String[] args) {
        byte[] objId = new byte[12];
        BytesAide.long2Bytes(Long.MAX_VALUE, objId, 0);
        BytesAide.int2Bytes(Integer.MAX_VALUE, objId, 8);
        ObjectId objectId = new ObjectId(objId);
        objId = objectId.toByteArray();
        long number = BytesAide.bytes2Long(objId, 0);
        long value = BytesAide.bytes2Long(objId, 8);
        System.out.println(number);
        System.out.println(Long.MAX_VALUE);
        System.out.println(value);
        System.out.println(Integer.MAX_VALUE);

        System.out.println(objectId.getTimestamp());
        System.out.println(objectId.toHexString());
    }

}
