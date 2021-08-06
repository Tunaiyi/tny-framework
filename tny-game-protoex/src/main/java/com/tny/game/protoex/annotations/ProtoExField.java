package com.tny.game.protoex.annotations;

import com.tny.game.doc.annotation.*;

import java.lang.annotation.*;

/**
 * 标记自定义ProtoEx类型字段
 * <p>
 * 若字段为Repeat(Collection)类型时还需要标记 @ProtoExElement
 * 若字段为Map类型时还需要标记 @ProtoExEntry
 *
 * @author KGTny
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@DocAnnotationClass
public @interface ProtoExField {

	/**
	 * ProtoEx类型字段ID 取值方位 1 - 536870911
	 *
	 * @return
	 */
	int value();

	/**
	 * 配置字段编码方式
	 *
	 * @return
	 */
	ProtoExConf conf() default @ProtoExConf;

}
