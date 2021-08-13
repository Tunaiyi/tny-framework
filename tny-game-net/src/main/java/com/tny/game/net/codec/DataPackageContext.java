package com.tny.game.net.codec;

import com.tny.game.common.utils.*;
import com.tny.game.net.exception.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;

/**
 * 网络包计步器
 * Created by Kun Yang on 2018/8/9.
 */
public class DataPackageContext {

	// 随机字节
	private final byte[] randomBytes;

	// 随机器
	private final Random random;

	// 访问 accessKeyBytes
	private final byte[] accessKeyBytes;

	// 访问 accessKey
	private final String accessKey;

	// 访问 Id
	private final long accessId;

	// 序号累计器
	private int packetNumber;

	// 当前随机值
	private int packetCode;

	// 客户端Time
	private long remoteTime;

	// 服务端Time
	private long localTime;

	private final DataPackCodecOptions config;

	public DataPackageContext(long accessId, DataPackCodecOptions config) {
		this.config = config;
		this.accessId = accessId;
		String key = accessId + config.getSecurityKeys(accessId);
		this.random = new Random(HashAide.djbStringHash32(key));
		this.randomBytes = DigestUtils.sha512(key);
		this.accessKeyBytes = DigestUtils.md5(key);
		this.accessKey = Hex.encodeHexString(this.accessKeyBytes);
	}

	public long getAccessId() {
		return this.accessId;
	}

	public int getPacketNumber() {
		return this.packetNumber;
	}

	public int getPacketCode() {
		return this.packetCode;
	}

	public byte[] getPackSecurityKey() {
		return config.getSecurityKeyBytes(packetNumber);
	}

	public void checkPacketTime(long time) {
		if (time <= 0) {
			throw CodecException.causeTimeout("illegal package time {}", time);
		}
		if (this.remoteTime == 0) {
			this.remoteTime = time;
			this.localTime = System.currentTimeMillis();
		}
		long timeout = this.config.getPacketTimeout();
		if (timeout <= 0) {
			return;
		}
		long remoteTime = this.remoteTime + (System.currentTimeMillis() - this.localTime);
		if (Math.abs(remoteTime - time) > timeout) {
			throw CodecException.causeTimeout("remote time is {}, packet time is {}, already timeout", remoteTime, time);
		}
	}

	/**
	 * 移动到下一个消息包
	 *
	 * @return 返回移动后的包 number
	 */
	public int nextNumber() {
		if (this.random != null) {
			this.packetCode = this.random.nextInt(Integer.MAX_VALUE);
		}
		return ++this.packetNumber;
	}

	/**
	 * 将计步器移动到第 number 个包
	 *
	 * @param number 目标number
	 */
	public void goToAndCheck(int number) {
		if (this.packetNumber >= number) {
			throw CodecException.causeDecode("id " + number + " is handled!");
		}
		long maxSkipNumber = this.config.getSkipNumberStep();
		if (number - this.packetNumber > maxSkipNumber) {
			throw CodecException.causeDecode("id " + number + " is illegal!");
		}
		while (this.packetNumber < number) {
			if (this.random != null) {
				this.packetCode = this.random.nextInt(Integer.MAX_VALUE);
			}
			this.packetNumber++;
		}
	}

	/**
	 * 获取随机字节数
	 *
	 * @param randNum 随机码
	 * @param length  获取字节长度
	 * @return 返回随机字节
	 */
	public byte[] getRandomBytes(int randNum, int length) {
		byte[] randomBytes = this.randomBytes;
		if (randomBytes.length == 0) {
			return randomBytes;
		}
		if (length > randomBytes.length / 2) {
			length = randomBytes.length / 2;
		}
		randNum = randNum % length;
		return Arrays.copyOfRange(randomBytes, randNum, randNum + length - 1);
	}

	public byte[] getAccessKeyBytes() {
		return this.accessKeyBytes;
	}

	public String getAccessKey() {
		return this.accessKey;
	}

}
