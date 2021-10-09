package com.tny.game.suite.auto.snapshot;

import java.lang.reflect.Method;

@FunctionalInterface
public interface AutoSnapMethodFactory {

	AutoSnapMethod<?> create(Method method);

}
