package com.tny.game.doc.controller;

import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;

public class OperationParamDescription extends ParamDescription {

    public OperationParamDescription() {
    }

    public OperationParamDescription(DocParam docParam) {
        super(docParam);
    }

    public OperationParamDescription(DocParam docParam, TypeFormatter typeFormatter) {
        super(docParam, typeFormatter);
    }

}
