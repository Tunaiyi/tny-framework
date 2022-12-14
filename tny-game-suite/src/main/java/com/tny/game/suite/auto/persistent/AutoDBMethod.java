package com.tny.game.suite.auto.persistent;

import com.tny.game.suite.auto.*;
import com.tny.game.suite.auto.persistent.annotation.*;

import java.lang.reflect.Method;

/**
 * Created by Kun Yang on 16/1/28.
 */
public class AutoDBMethod extends AutoMethod<AutoDB, AutoDBReturn, AutoDBParam> {

    protected AutoDBMethod(Method method) {
        super(method, AutoDB.class, AutoDBReturn.class, AutoDBParam.class);
    }

    @Override
    public boolean isHandleInvoke() {
        return super.isHandleInvoke() && this.autoInvoke.value();
    }

}
