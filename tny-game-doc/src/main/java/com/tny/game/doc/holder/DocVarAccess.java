package com.tny.game.doc.holder;

import com.tny.game.doc.annotation.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/18 21:09
 **/
public interface DocVarAccess {

    VarDoc getVarDoc();

    String getDocText();

    String getDocDesc();

    Class<?> getDocType();

    String getDocTypeName();

    String getDocExample();

    Class<?> getVarClass();

    String getVarClassName();

}
