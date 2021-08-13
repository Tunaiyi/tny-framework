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
	 * 转播连接打开
	 */
	public static final byte RELAY_PACKET_TYPE_PIPE_OPEN = (byte)1;

	/**
	 * 转播连接关闭
	 */
	public static final byte RELAY_PACKET_TYPE_PIPE_CLOSE = (byte)2;

	/**
	 * 转播连接ping
	 */
	public static final byte RELAY_PACKET_TYPE_PIPE_PING = (byte)3;

	/**
	 * 转播连接ping
	 */
	public static final byte RELAY_PACKET_TYPE_PIPE_PONG = (byte)4;

	/**
	 * 转播数据包类型标识位
	 */
	public static final byte RELAY_PACKET_FOR_TUBULE = 1 << 4;

	/**
	 * 转播通道连接
	 */
	public static final byte RELAY_PACKET_TYPE_TUBULE_CONNECT = RELAY_PACKET_FOR_TUBULE | (byte)1;

	/**
	 * 转播通道断开
	 */
	public static final byte RELAY_PACKET_TYPE_TUBULE_DISCONNECT = RELAY_PACKET_FOR_TUBULE | (byte)2;

	/**
	 * 转播通道连接确认
	 */
	public static final byte RELAY_PACKET_TYPE_TUBULE_CONNECTED = RELAY_PACKET_FOR_TUBULE | (byte)3;

	/**
	 * 转播通道断开确认
	 */
	public static final byte RELAY_PACKET_TYPE_TUBULE_DISCONNECTED = RELAY_PACKET_FOR_TUBULE | (byte)4;

	/**
	 * 转播通道转发
	 */
	public static final byte RELAY_PACKET_TYPE_TUBULE_MESSAGE = RELAY_PACKET_FOR_TUBULE | (byte)5;

	/**
	 * 是否是文件头
	 *
	 * @param magics
	 * @return
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
