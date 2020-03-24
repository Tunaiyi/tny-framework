package com.tny.game.expr.groovy

import com.tny.game.common.math.MathAide

rand = MathAide.&rand;

def nowMethod(int value) {
    rand(value)
};

print nowMethod(200);