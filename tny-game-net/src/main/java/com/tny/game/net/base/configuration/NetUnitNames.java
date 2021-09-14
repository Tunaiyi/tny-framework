package com.tny.game.net.base.configuration;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/18 2:33 下午
 */
public final class NetUnitNames {

	public static final String DEFAULT_HEAD = "default";

	private NetUnitNames() {
	}

	public static String defaultName(Class<?> clazz) {
		return DEFAULT_HEAD + clazz.getSimpleName();
	}

	public static String lowerCamelName(Class<?> clazz) {
		String name = clazz.getSimpleName();
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	public static String lowerCamelName(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	public static String unitName(String key, Class<?> clazz) {
		return key + clazz.getSimpleName();
	}

}
