package com.tny.game.basics.loader;

import com.tny.game.basics.item.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 读取 tny-factory.properties 配置中
 * com.tny.game.loader.EnumLoader 配置相关的枚举会提前读取
 * Created by Kun Yang on 16/9/9.
 */
public final class ItemModelClassLoader {

	private static final Set<Class<?>> CLASSES = new ConcurrentHashSet<>();

	private ItemModelClassLoader() {
	}

	@ClassSelectorProvider
	public static ClassSelector controllerSelector() {
		return ClassSelector.create()
				.addFilter(SubOfClassFilter.ofInclude(ItemModel.class))
				.setHandler(CLASSES::addAll);
	}

	public static Set<Class<? extends ItemModel>> getClasses() {
		return as(Collections.unmodifiableSet(CLASSES));
	}

}
