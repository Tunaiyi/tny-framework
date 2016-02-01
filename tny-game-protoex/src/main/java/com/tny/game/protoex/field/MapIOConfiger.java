package com.tny.game.protoex.field;

/**
 * Map键值对编码方式配置
 *
 * @param <M>
 * @author KGTny
 */
public interface MapIOConfiger<M> extends IOConfiger<M> {

    /**
     * key编码方式
     *
     * @return
     */
    public IOConfiger<?> getKeyConfiger();

    /**
     * value编码方式
     *
     * @return
     */
    public IOConfiger<?> getValueConfiger();

}
