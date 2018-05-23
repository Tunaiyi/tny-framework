package com.tny.game;

import com.tny.game.common.RunningChecker;

import javax.script.*;

public class JSTest {

    public static void main(String[] args) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        String script = "function test(){var data = 2000;  return data + 12;} test();";
        Compilable compilable = (Compilable) engine;
        CompiledScript compiledFun = compilable.compile(script);
        compiledFun.eval();
        RunningChecker.start("function");
        int time = 1000;
        for (int index = 0; index < time; index++)
            compiledFun.eval();
        RunningChecker.endPrint("function", "function end");

        script = "var data = 2000; data + 12";
        CompiledScript compiledNoFun = compilable.compile(script);
        compiledNoFun.eval();
        RunningChecker.start("nofunction");
        for (int index = 0; index < time; index++)
            compiledNoFun.eval();
        RunningChecker.endPrint("nofunction end");

        // Map<String, Object> map = new HashMap<String, Object>();
        //         // map.put("name", "abc");

        // script = "data.name";
        // FormulaHolder formulaHolder = MvelFormulaFactory.create(script, FormulaType.EXPRESSION);
        // Formula formula = formulaHolder.createFormula();
        // System.out.println(formula.put("data", map).execute(Object.class));
        // RunningChecker.start("mvel");
        // for (int index = 0; index < time; index++)
        //     formula.execute(Number.class);
        // RunningChecker.endPrint("mvel end");
    }
}
