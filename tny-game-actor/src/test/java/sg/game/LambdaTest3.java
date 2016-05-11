package sg.game;

import java.util.function.Consumer;

/**
 * Created by Kun Yang on 16/2/21.
 */
public class LambdaTest3 {

    static Consumer<String> temp = null;

    int value = 1;

    private static void test1() {
        Consumer<String> ld = System.out::println;
        ld.accept("abc");
    }

    private static void test2() {
        Consumer<String> ld = (s) -> System.out.println(s);
        ld.accept("abc");
    }

    private static void test(Consumer<Integer> consumer) {
        consumer.accept(100);
    }

    public void runTest() {
        test(TestConsumer.consumer::test);

        test(TestConsumer::testStatic);

        test((s) -> TestConsumer.consumer.test(s));

        int value = 1;
        test((s) -> s += value);

        test((s) -> s += this.value);

    }

    public static void main(String[] args) {
        System.out.println("end");
    }

}
