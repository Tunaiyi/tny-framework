package com.tny.game.net.relay;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 7:35 下午
 */
public class RelayCodecConstants {

	/**
	 * 转播包开头标识
	 */
	public static final byte[] RELAY_MAGIC = "rpk".getBytes();

	/**
	 * 转播包参数长度占用字节数 4
	 */
	public static final int RELAY_PACKET_ARGUMENTS_LENGTH_BYTES_SIZE = 4;

	/**
	 * 转播包类型字节长度
	 */
	public static final int RELAY_PACKET_OPTION_BYTES_SIZE = 1;

	/**
	 * 转发包头部标识位(MAGIC + 长度)
	 */
	public static final int RELAY_PACKET_HEAD_LENGTH_BYTES_SIZE = RELAY_MAGIC.length + RELAY_PACKET_ARGUMENTS_LENGTH_BYTES_SIZE;

	/**
	 * 转播包类型
	 */
	public static final int RELAY_PACKET_TYPE_BIT_SIZE = 5;

	/**
	 * 转播包类型
	 */
	public static final byte RELAY_PACKET_TYPE_MASK = ~(-1 << RELAY_PACKET_TYPE_BIT_SIZE);

	/**
	 * 转播连接打开发起
	 */
	public static final byte RELAY_PACKET_TYPE_LINK_OPENING = (byte)1;

	/**
	 * 转播连接打开成功
	 */
	public static final byte RELAY_PACKET_TYPE_LINK_OPENED = (byte)2;

	/**
	 * 转播连接关闭
	 */
	public static final byte RELAY_PACKET_TYPE_LINK_CLOSE = (byte)3;

	/**
	 * 转播连接ping
	 */
	public static final byte RELAY_PACKET_TYPE_LINK_PING = (byte)5;

	/**
	 * 转播连接ping
	 */
	public static final byte RELAY_PACKET_TYPE_LINK_PONG = (byte)6;

	/**
	 * 转播数据包类型标识位
	 */
	public static final byte RELAY_PACKET_FOR_TUNNEL = 1 << 4;

	/**
	 * 转播通道连接发起
	 */
	public static final byte RELAY_PACKET_TYPE_TUNNEL_CONNECT = RELAY_PACKET_FOR_TUNNEL | (byte)1;

	/**
	 * 转播通道连接成功
	 */
	public static final byte RELAY_PACKET_TYPE_TUNNEL_CONNECTED = RELAY_PACKET_FOR_TUNNEL | (byte)2;

	/**
	 * 转播通道断开
	 */
	public static final byte RELAY_PACKET_TYPE_TUNNEL_DISCONNECT = RELAY_PACKET_FOR_TUNNEL | (byte)3;

	//	/**
	//	 * 转播通道连接确认
	//	 */
	//	public static final byte RELAY_PACKET_TYPE_TUNNEL_CONNECTED = RELAY_PACKET_FOR_TUNNEL | (byte)3;
	//
	//	/**
	//	 * 转播通道断开确认
	//	 */
	//	public static final byte RELAY_PACKET_TYPE_TUNNEL_DISCONNECTED = RELAY_PACKET_FOR_TUNNEL | (byte)4;

	/**
	 * 转播通道转发
	 */
	public static final byte RELAY_PACKET_TYPE_TUNNEL_RELAY = RELAY_PACKET_FOR_TUNNEL | (byte)5;

	/**
	 * 是否是文件头
	 */
	public static boolean isMagic(final byte[] magics) {
		for (int index = 0; index < magics.length; index++) {
			if (RELAY_MAGIC[index] != magics[index]) {
				return false;
			}
		}
		return true;
	}

}
