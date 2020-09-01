package com.tny.game.common.reflect;

public class FormulaTest {

    // @Test
    // public void testNomal() {
    //     int data = 0;
    //     long time = System.currentTimeMillis();
    //     System.out.println();
    //     for (int i = 0; i < 1000000; i++) {
    //         FPlayer player = new FPlayer(i);
    //         data = player.getLevel() * 30 / 20 + player.getLevel() * 31;
    //     }
    //     System.out.println(data + "  " + (System.currentTimeMillis() - time));
    // }
    //
    // @Test
    // public void testFomual() {
    //     FormulaHolder formulaHolder = MvelFormulaFactory.create("player.int * 30 / 20 + player.int * 31", FormulaType.EXPRESSION);
    //     int data = 0;
    //     long time = System.currentTimeMillis();
    //     for (int i = 0; i < 1000000; i++) {
    //         data = formulaHolder.createFormula().put("player", new FPlayer(i)).execute(Integer.class);
    //     }
    //     System.out.println(data + "  " + (System.currentTimeMillis() - time));
    // }
}
