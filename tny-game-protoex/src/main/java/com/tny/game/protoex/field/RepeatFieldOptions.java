package com.tny.game.protoex.field;

/**
 * repeat类型element编码方式
 *
 * @param <M>
 * @author KGTny
 */
public interface RepeatFieldOptions<M> extends FieldOptions<M> {

    /**
     * element编码方式
     *
     * @return
     */
    FieldOptions<?> getElementOptions();

}
