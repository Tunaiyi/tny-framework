package com.tny.game.common.reflect.aop.annotation;

import java.lang.reflect.*;

public enum Privileges {

    ALL {
        @Override
        public boolean check(Method method) {
            return true;
        }
    },

    PUBLIC {
        @Override
        public boolean check(Method method) {
            return Modifier.isPublic(method.getModifiers());
        }
    },

    PROTECTED {
        @Override
        public boolean check(Method method) {
            return Modifier.isProtected(method.getModifiers());
        }
    },

    PRIVATE {
        @Override
        public boolean check(Method method) {
            return Modifier.isPrivate(method.getModifiers());
        }
    };

    public abstract boolean check(Method method);

}
