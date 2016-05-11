package sg.game;

import java.util.function.Consumer;

/**
 * Created by Kun Yang on 16/2/21.
 */
public class LambdaTest2 {

    static Consumer<String> temp = null;

    private static void test1() {
        Consumer<String> ld = System.out::println;
        ld.accept("abc");
    }

    private static void test2() {
        Consumer<String> ld = (s) -> System.out.println(s);
        ld.accept("abc");
    }

    public static void main(String[] args) {
        test1();
        test2();
    }

}
