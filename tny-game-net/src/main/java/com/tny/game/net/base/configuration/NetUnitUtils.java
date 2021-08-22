package com.tny.game.net.base.configuration;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/18 2:33 下午
 */
public final class NetUnitUtils {

	public static final String DEFAULT_HEAD = "default";

	private NetUnitUtils() {
	}

	public static String defaultName(Class<?> clazz) {
		return DEFAULT_HEAD + clazz.getSimpleName();
	}

	public static String unitName(String key, Class<?> clazz) {
		return key + clazz.getSimpleName();
	}

}
