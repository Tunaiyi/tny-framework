package com.tny.game.protoex.field;

/**
 * repeat类型element编码方式
 *
 * @param <M>
 * @author KGTny
 */
public interface RepeatIOConfiger<M> extends IOConfiger<M> {

    /**
     * element编码方式
     *
     * @return
     */
    public IOConfiger<?> getElementConfiger();

}
