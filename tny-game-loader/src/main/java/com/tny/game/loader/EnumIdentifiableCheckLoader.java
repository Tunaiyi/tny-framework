package com.tny.game.loader;

import com.tny.game.common.enums.*;
import com.tny.game.common.utils.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 3:41 下午
 */
public class EnumIdentifiableCheckLoader {

	public static final Logger LOGGER = LoggerFactory.getLogger(EnumIdentifiableCheckLoader.class);

	@ClassSelectorProvider
	@SuppressWarnings("unchecked")
	static <E extends Enum<E> & EnumIdentifiable<?>> ClassSelector enumIdentifiableSelector() {
		return ClassSelector.create()
				.addFilter(SubOfClassFilter.ofInclude(EnumIdentifiable.class))
				.setHandler((classes) -> classes.forEach(codeClass -> {
					if (codeClass.isEnum()) {
						Map<Object, E> map = new HashMap<>();
						List<E> enumList = EnumUtils.getEnumList((Class<E>)codeClass);
						enumList.forEach(e -> check(map, e));
					}
					LOGGER.info("EnumIdentifiableCheckLoader : {}", codeClass);
				}));
	}

	private static <E extends Enum<E> & EnumIdentifiable<?>> void check(Map<Object, E> map, E value) {
		E old = map.putIfAbsent(value.getId(), value);
		if (old != null && !Objects.equals(old, value)) {
			Asserts.throwBy(IllegalArgumentException::new,
					"Enum Class {}, ids of members {} and {} are both {}", value.getClass(), old, value, value.getId());
		}
	}

}
