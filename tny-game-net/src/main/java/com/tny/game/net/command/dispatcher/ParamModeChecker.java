package com.tny.game.net.command.dispatcher;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/29 03:29
 **/
public interface ParamModeChecker {

    boolean isMode(Class<?> paramClass, AnnotationHolder annotationHolder);

}
