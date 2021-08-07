package com.tny.game.base.item.probability;

import com.tny.game.common.utils.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;

import java.lang.reflect.Modifier;
import java.util.Collection;

/**
 * Created by Kun Yang on 16/9/9.
 */
public final class RandomCreatorLoader {

	private RandomCreatorLoader() {
	}

	@ClassSelectorProvider
	public static ClassSelector selector() {
		return ClassSelector.create()
				.addFilter(SubOfClassFilter.ofInclude(RandomCreatorFactory.class))
				.setHandler(RandomCreatorLoader::handle);
	}

	private static void handle(Collection<Class<?>> classes) {
		for (Class<?> clazz : classes) {
			if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) || RandomCreators.isDefault(clazz)) {
				continue;
			}
			try {
				RandomCreatorFactory factory = (RandomCreatorFactory)clazz.newInstance();
				factory.registerSelf();
			} catch (Exception e) {
				Asserts.throwWith(IllegalArgumentException::new, e, "创建 {} 异常", clazz);
			}
		}
	}

}
