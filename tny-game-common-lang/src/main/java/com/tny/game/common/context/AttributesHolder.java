package com.tny.game.common.context;

/**
 * @author KGTny
 * @ClassName: Attributes
 * @Description: 属性对象接口
 * @date 2011-9-21 ����10:48:40
 * <p>
 * 属性对象接口
 * <p>
 * <br>
 */
public class AttributesHolder {

    private volatile transient Attributes attributes;

    private final Object holderLock = new Object();

    public Attributes attributes() {
        if (this.attributes != null) {
            return this.attributes;
        }
        synchronized (this.holderLock) {
            if (this.attributes != null) {
                return this.attributes;
            }
            return this.attributes = ContextAttributes.create();
        }
    }

}
