package com.tny.game.expr.groovy

import com.tny.game.common.formula.MathEx

rand = MathEx.&rand;

def nowMethod(int value) {
    rand(value)
};

print nowMethod(200);