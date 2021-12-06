package com.tny.game.basics.item.annotation;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.mapper.*;

import java.lang.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/12/3 2:30 上午
 */

@Inherited
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonProperty
@JsonDeserialize(using = ItemJsonDeserializer.class)
@JsonSerialize(using = ItemJsonSerializer.class)
@Documented
public @interface JsonItemFormat {

	Class<? extends Manager<?>> value();

}
