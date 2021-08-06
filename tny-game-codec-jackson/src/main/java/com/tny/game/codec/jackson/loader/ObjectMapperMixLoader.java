package com.tny.game.codec.jackson.loader;

import com.tny.game.codec.jackson.mapper.annotation.*;
import com.tny.game.common.enums.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;

import static com.tny.game.codec.jackson.mapper.ObjectMapperFactory.*;

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
	static ClassSelector mixEnumIdentifiableSelector() {
		return ClassSelector.create()
				.addFilter(SubOfClassFilter.ofInclude(EnumIdentifiable.class))
				.setHandler(createHandler((module, classes) -> classes.stream()
						.filter(Class::isEnum)
						.forEach(enumClass -> {
							module.setMixInAnnotation(enumClass, EnumIdentifiableMix.class);
						})));
	}
	//    @ClassSelectorProvider
	//    static ClassSelector ResultCodeClassesSelector() {
	//        return ClassSelector.instance()
	//                .addFilter(SubOfClassFilter.ofInclude(ResultCode.class))
	//                .setHandler(createHandler((module, classes) -> classes.forEach(
	//                        codeClass -> LOGGER.info("ResultCodeLoader : {}", codeClass))
	//                ));
	//    }

}
