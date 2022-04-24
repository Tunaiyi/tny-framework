package com.tny.game.common.context;

import java.util.*;
import java.util.function.Supplier;

class EmptyAttributes implements Attributes {

    public EmptyAttributes() {
        super();
    }

    @Override
    public <T> T getAttribute(AttrKey<? extends T> key) {
        return null;
    }

    @Override
    public <T> T getAttribute(AttrKey<? extends T> key, T defaultValue) {
        return defaultValue;
    }

    @Override
    public <T> T computeIfAbsent(AttrKey<? extends T> key, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T computeIfAbsent(AttrKey<? extends T> key, Supplier<T> supplier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T setIfAbsent(AttrKey<? extends T> key, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T removeAttribute(AttrKey<? extends T> key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void setAttribute(AttrKey<? extends T> key, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(Map<AttrKey<?>, ?> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(AttrEntry<?> entry) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(Collection<AttrEntry<?>> entries) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void setAttribute(AttrEntry<?>... entries) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void removeAttribute(Collection<AttrKey<?>> keys) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Map<AttrKey<?>, Object> getAttributeMap() {
        return Collections.emptyMap();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void clearAttribute() {
        throw new UnsupportedOperationException();
    }

}
