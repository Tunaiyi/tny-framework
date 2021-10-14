package com.tny.game.demo.core.server;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.codec.annotation.*;
import com.tny.game.codec.protobuf.*;
import com.tny.game.data.annotation.*;
import com.tny.game.redisson.annotation.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/8 10:47 上午
 */
@RedisObject
@ProtobufClass
@Codecable(ProtobufMimeType.PROTOBUF)
@EntityObject
@Document
public class DemoPlayer {

	@Id
	@EntityId
	@Protobuf(order = 1)
	private long id;

	@Protobuf(order = 2)
	private String name;

	@Protobuf(order = 3)
	private int age;

	public DemoPlayer() {
	}

	public DemoPlayer(long id, String name, int age) {
		this.id = id;
		this.name = name;
		this.age = age;
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

}
