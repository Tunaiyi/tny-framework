/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package cywl.proto.test;

import com.google.protobuf.InvalidProtocolBufferException;

public class TestMain {

    public static void main(String[] args) throws InvalidProtocolBufferException {
        //		List<Integer> value = new LinkedList<Integer>();
        //		value.add(1);
        //		value.add(2);
        //		value.add(3);
        //		value.add(4);
        //		//		for (Integer v1 : value) {
        //		//			for (Integer v2 : value) {
        //		//				System.out.println(v1 + " " + v2);
        //		//			}
        //		//		}
        //		for (Iterator<?> iterator = value.iterator(); iterator.hasNext();) {
        //			Integer v1 = (Integer) iterator.next();
        //			for (Iterator<?> iterator2 = value.iterator(); iterator2.hasNext();) {
        //				Integer v2 = (Integer) iterator2.next();
        //				System.out.println(v1 + " " + v2);
        //			}
        //		}
        //		//		 << 1 >>1
        //		//		System.out.println(Integer.MAX_VALUE << 1 >>> 1);

        //		ProtoExOutputStream outputStream = new ProtoExOutputStream(10);
        //		outputStream.writeInt(-1);
        //		byte[] test = outputStream.toByteArray();
        //
        //		ProtoExInputStream reader = new ProtoExInputStream(test);
        //		System.out.println(reader.readInt());
        //
        //		Goods equip = new Goods("八丈蛇矛", "张飞神武器八丈蛇矛");
        //		PersonDTO person1 = new PersonDTO();
        //		person1.setName("Tom");
        //		person1.setAge(14);
        //		person1.setEquip(equip);
        //		person1.addFriend(1001);
        //		person1.addFriend(1002);
        //		person1.addFriend(1003);
        //		person1.setNoteList(Arrays.asList(new String[] { "abc", "def", "ghi" }));
        //		person1.addGoods(new Goods("经验卡", "+经验"));
        //		person1.addGoods(new Goods("体力卡", "+体力"));
        //
        //		ProtoExSchema<PersonDTO> protoSchema = RuntimeSchema.getProtoSchema(PersonDTO.class);
        //		outputStream = new ProtoExOutputStream(256, 256);
        //		outputStream.writeMessage(person1);
        //		byte[] data = outputStream.toByteArray();
        //
        //		PersonProto proto = PersonProto.parseFrom(data);
        //		//		data = proto.toByteArray();
        //		//		proto = PersonProto.parseFrom(data);
        //		PersonDTO person2 = new PersonDTO();
        //		person2.setName(proto.getName());
        //		person2.setAge(proto.getAge());
        //		person2.addFriend(proto.getFriendIDList());
        //		GoodsProto equipProto = proto.getEquip();
        //		person2.setEquip(new Goods(equipProto.getName(), equipProto.getDesc()));
        //		person2.setFloatTest(proto.getFloatTest());
        //		person2.setDoubleTest(proto.getDoubleTest());
        //		person2.setFalseTest(proto.getFalseTest());
        //		person2.setTrueTest(proto.getTrueTest());
        //		person2.setNoteList(proto.getNoteListList());
        //		person2.setParamBytes(proto.getParamBytes().toByteArray());
        //		for (GoodsProto goodsProto : proto.getGoodsListList())
        //			person2.addGoods(new Goods(goodsProto.getName(), goodsProto.getDesc()));
        //		System.out.println("person1 = " + person1);
        //		System.out.println("person2 = " + person2);
        //		reader = new ProtoExInputStream(data);
        //		TODO
        //		PersonDTO person3 = protoSchema.readRoot(reader);
        //		System.out.println("person3 = " + person3);
    }

}
