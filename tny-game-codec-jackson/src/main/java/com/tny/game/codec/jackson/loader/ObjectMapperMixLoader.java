package com.tny.game.codec.jackson.loader;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tny.game.codec.jackson.mapper.annotation.*;
import com.tny.game.common.enums.*;
import com.tny.game.common.reflect.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.tny.game.codec.jackson.mapper.AutoRegisterModuleClassesHandler.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-05 13:08
 */
public class ObjectMapperMixLoader {

	@ClassSelectorProvider
	static ClassSelector autoMixClassesSelector() {
		return ClassSelector.create()
				.addFilter(AnnotationClassFilter.ofInclude(JsonAutoMixClasses.class))
				.setHandler(createHandler((module, classes) -> classes.forEach(mix -> {
							JsonAutoMixClasses mixClasses = mix.getAnnotation(JsonAutoMixClasses.class);
							for (Class<?> mixClass : mixClasses.value()) {
								module.setMixInAnnotation(mixClass, mix);
							}
						})
				));
	}

	@ClassSelectorProvider
	static ClassSelector mixEnumerableSelector() {
		return ClassSelector.create()
				.addFilter(SubOfClassFilter.ofInclude(Enumerable.class))
				.setHandler(createHandler((module, classes) -> classes.stream()
						.filter(Class::isEnum)
						.forEach(enumClass -> module.setMixInAnnotation(enumClass, EnumerableMix.class))));
	}

	@ClassSelectorProvider
	static ClassSelector jsonSubTypeSelector() {
		return ClassSelector.create()
				.addFilter(AnnotationClassFilter.ofInclude(JsonRegisterSubType.class, JsonTypeName.class))
				.setHandler(createHandler(ObjectMapperMixLoader::handleSubClasses));
	}

	public static void handleSubClasses(SimpleModule module, Collection<Class<?>> classes) {
		Set<Class<?>> baseClasses = new HashSet<>();
		Map<Class<?>, String> subClasses = new HashMap<>();
		for (Class<?> clazz : classes) {
			JsonRegisterSubType subType = clazz.getAnnotation(JsonRegisterSubType.class);
			JsonTypeName typeName = clazz.getAnnotation(JsonTypeName.class);
			if (subType == null && typeName == null) {
				continue;
			}
			if (typeName != null) {
				subClasses.put(clazz, typeName.value());
			}
			if (subType != null) {
				if (StringUtils.isBlank(subType.prefix())) {
					subClasses.put(clazz, subType.value());
				} else {
					subClasses.put(clazz, subType.prefix() + subType.link() + subType.value());
				}
			}
			Set<Class<?>> deepClasses = ReflectAide.getDeepClasses(clazz.getSuperclass());
			for (Class<?> baseClass : deepClasses) {
				if (baseClass.getAnnotation(JsonTypeInfo.class) != null) {
					baseClasses.add(baseClass);
				}
			}
		}
		baseClasses.forEach(module::registerSubtypes);
		subClasses.forEach((sub, value) -> module.registerSubtypes(new NamedType(sub, value)));
	}

}
