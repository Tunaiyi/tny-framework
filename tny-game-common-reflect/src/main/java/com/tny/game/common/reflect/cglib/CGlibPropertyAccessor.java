package com.tny.game.common.reflect.cglib;

import com.tny.game.common.reflect.*;

import java.lang.reflect.*;

public class CGlibPropertyAccessor implements PropertyAccessor {

	private String name;

	private MethodAccessor reader;

	private MethodAccessor writer;

	private Class<?> type;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isReadable() {
		return this.reader != null;
	}

	@Override
	public boolean isWritable() {
		return this.writer != null;
	}

	@Override
	public Class<?> getPropertyType() {
		return this.type;
	}

	@Override
	public Type getGenericType() {
		if (this.reader != null) {
			return this.reader.getJavaMethod().getGenericReturnType();
		} else if (this.writer != null) {
			return this.writer.getJavaMethod().getGenericParameterTypes()[0];
		}
		return null;
	}

	@Override
	public Object getPropertyValue(Object instance) throws InvocationTargetException {
		if (instance == null) {
			return null;
		}
		if (this.reader == null) {
			throw new UnsupportedOperationException(instance.getClass() + "不支持 [" + this.name + "] 属性 Getter 方法");
		}
		return this.reader.invoke(instance, new Object[]{this.type});
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected void setReader(MethodAccessor reader) {
		this.reader = reader;
	}

	protected void setWriter(MethodAccessor writer) {
		this.writer = writer;
	}

	protected void setType(Class<?> type) {
		this.type = type;
	}

	@Override
	public void setPropertyValue(Object instance, Object value) throws InvocationTargetException {
		if (instance == null) {
			return;
		}
		if (this.writer == null) {
			throw new UnsupportedOperationException(instance.getClass() + "不支持 [" + this.name + "] 属性 Setter 方法");
		}
		this.writer.invoke(instance, new Object[]{value});
	}

}
