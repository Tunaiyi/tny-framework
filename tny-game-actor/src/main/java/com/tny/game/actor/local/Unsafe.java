package com.tny.game.actor.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("restriction")
class Unsafer {

	private static final Logger LOGGER = LoggerFactory.getLogger(Unsafer.class);
	private static final sun.misc.Unsafe UNSAFE = initUnsafe();

	private static sun.misc.Unsafe initUnsafe() {
		try {
			java.lang.reflect.Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return (sun.misc.Unsafe) f.get(null);
		} catch (Exception e) {
		}
		return sun.misc.Unsafe.getUnsafe();
	}

	static {
		initUnsafe();
		try {
			// http://www.mail-archive.com/jdk6-dev@openjdk.java.net/msg00698.html
			try {
				List<Class<?>> paramClasses = Arrays.asList(Object.class, long.class, Object.class, long.class, long.class);
				UNSAFE.getClass().getDeclaredMethod("copyMemory", paramClasses.toArray(new Class<?>[paramClasses.size()]));
				LOGGER.debug("sun.misc.Unsafe.copyMemory: available");
			} catch (NoSuchMethodError | NoSuchMethodException t) {
				LOGGER.debug("sun.misc.Unsafe.copyMemory: unavailable");
				throw t;
			}

		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}

	public static Unsafe unsafe() {
		return UNSAFE;
	}

}
