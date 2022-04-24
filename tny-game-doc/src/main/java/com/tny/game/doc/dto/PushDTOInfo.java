package com.tny.game.doc.dto;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.doc.holder.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import static com.tny.game.common.utils.StringAide.*;

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

    public static <C extends Annotation, F extends Annotation> PushDTOInfo create(Class<?> clazz,
            Class<C> classAnnotation, Function<C, Object> classIdGetter,
            Class<F> fieldAnnotation, Function<F, Object> fieldIdGetter) {
        DTODocHolder holder = DTODocHolder.create(clazz, classAnnotation, classIdGetter, fieldAnnotation, fieldIdGetter);
        if (holder == null) {
            return null;
        }
        return new PushDTOInfo(clazz, holder);
    }

    public PushDTOInfo(Class<?> clazz, DTODocHolder holder) {
        super();
        this.packageName = clazz.getPackage().getName();
        this.className = clazz.getSimpleName();
        this.des = holder.getDTODoc().value();
        this.text = holder.getDTODoc().text();
        if (StringUtils.isBlank(this.text)) {
            this.text = this.des;
        }
        this.handlerName = format("public function $send{}$S(dto : {}):void{}", clazz.getSimpleName(), clazz.getSimpleName());
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
        if (value == 0) {
            return this.className.compareTo(other.className);
        }
        return value;
    }

}
