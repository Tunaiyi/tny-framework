/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.demo.core.common.dto;

import com.baidu.bjf.remoting.protobuf.annotation.*;
import com.tny.game.codec.typeprotobuf.annotation.*;
import com.tny.game.demo.core.server.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/8 4:51 下午
 */

@DTODoc("玩家DTO")
@ProtoEx(1000_03_00)
@ProtobufClass
@TypeProtobuf(1000_03_00)
public class PlayerDTO {

    @ProtoExField(1)
    @Protobuf(order = 1)
    private long id;

    @ProtoExField(2)
    @Protobuf(order = 2)
    private String name;

    @ProtoExField(3)
    @Protobuf(order = 3)
    private int age;

    public PlayerDTO() {
    }

    public PlayerDTO(DemoPlayer player) {
        this.id = player.getId();
        this.name = player.getName();
        this.age = player.getAge();
    }

    public PlayerDTO(long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public long getId() {
        return id;
    }

    public PlayerDTO setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PlayerDTO setName(String name) {
        this.name = name;
        return this;
    }

    public int getAge() {
        return age;
    }

    public PlayerDTO setAge(int age) {
        this.age = age;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("age", age)
                .toString();
    }

}
