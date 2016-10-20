package com.tny.game.doc.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.tny.game.LogUtils;
import com.tny.game.doc.holder.DTODocHolder;
import org.apache.commons.lang3.StringUtils;

@XStreamAlias("dtoImport")
public class PushDTOInfo implements Comparable<PushDTOInfo> {

    @XStreamAsAttribute
    private String className;

    @XStreamAsAttribute
    private String des;

    @XStreamAsAttribute
    private String text;

    @XStreamAsAttribute
    private String packageName;

    @XStreamAsAttribute
    private String handlerName;

    public PushDTOInfo(Class<?> clazz) {
        super();
        DTODocHolder holder = DTODocHolder.create(clazz);
        this.packageName = clazz.getPackage().getName();
        this.className = clazz.getSimpleName();
        this.des = holder.getDTODoc().value();
        this.text = holder.getDTODoc().text();
        if (StringUtils.isBlank(this.text))
            this.text = this.des;
        this.handlerName = LogUtils.format("public function $send{}$S(dto : {}):void{}", clazz.getSimpleName(), clazz.getSimpleName());
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getDes() {
        return des;
    }

    public String getHandlerName() {
        return handlerName;
    }

    @Override
    public int compareTo(PushDTOInfo other) {
        int value = this.packageName.compareTo(other.packageName);
        if (value == 0)
            return this.className.compareTo(other.className);
        return value;
    }

}
