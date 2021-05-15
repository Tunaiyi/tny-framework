package sg.game;

import com.tny.game.common.runtime.*;

import java.util.function.Consumer;

/**
 * Created by Kun Yang on 16/2/21.
 */
public class LambdaTest {

    int fieldValue = 1;

    static Consumer<Integer> temp = new Consumer<Integer>() {

        @Override
        public void accept(Integer integer) {
            integer = integer + 1;
        }

    };

    private static void test1() {
        Consumer<Integer> ld = System.out::println;
        ld.accept(100);
    }

    private static void testLD() {
        Consumer<Integer> ld = (s) -> s = s + 1;
        ld.accept(100);
    }

    private static void testOJ() {
        temp.accept(100);
    }

    public void test(int test) {
        test = test + 1;
    }

    private static void test(Consumer<Integer> consumer) {
        consumer.accept(100);
    }

    public void runTest() {
        int time = 1000000;
        Consumer<Integer> consumer = null;
        RunChecker.trace("call at first");
        consumer = TestConsumer.consumer::test;
        test(TestConsumer.consumer::test);
        RunChecker.end("call at first");

        RunChecker.trace("call testLD");
        for (int i = 0; i < time; i++) {
            testLD();
        }
        RunChecker.end("call testLD");

        RunChecker.trace("call testOJ");
        for (int i = 0; i < time; i++) {
            testOJ();
        }
        RunChecker.end("call testOJ");

        RunChecker.trace("call ref Object.method");
        for (int i = 0; i < time; i++) {
            test(TestConsumer.consumer::test);
        }
        RunChecker.end("call ref Object.method");

        RunChecker.trace("call ref this.method");
        for (int i = 0; i < time; i++) {
            test(this::test);
        }
        RunChecker.end("call ref this.method");

        RunChecker.trace("call ref Class.StaticMethod");
        for (int i = 0; i < time; i++) {
            test(TestConsumer::testStatic);
        }
        RunChecker.end("call ref Class.StaticMethod");

        consumer = TestConsumer.consumer::test;
        RunChecker.trace("call copy ref Object.method");
        for (int i = 0; i < time; i++) {
            test(consumer);
        }
        RunChecker.end("call copy ref Object.method");

        RunChecker.trace("call lambda");
        for (int i = 0; i < time; i++) {
            test((s) -> TestConsumer.consumer.test(s));
        }
        RunChecker.end("call lambda");

        final int value = 1;
        RunChecker.trace("call lambda with local");
        for (int i = 0; i < time; i++) {
            test((s) -> s = value + 1);
        }
        RunChecker.end("call lambda with local");

        RunChecker.trace("call lambda with this");
        for (int i = 0; i < time; i++) {
            test((s) -> s = this.fieldValue + 1);
        }
        RunChecker.end("call lambda with this");

        consumer = (s) -> TestConsumer.consumer.test(s);
        RunChecker.trace("call copy lambda");
        for (int i = 0; i < time; i++) {
            test(consumer);
        }
        RunChecker.end("call copy lambda");

        RunChecker.trace("call inner class");
        for (int i = 0; i < time; i++) {
            test(new Consumer<Integer>() {

                @Override
                public void accept(Integer integer) {
                    integer = integer + 1;
                }
            });
        }
        RunChecker.end("call inner class");

        Consumer<Integer> cons = new Consumer<Integer>() {

            @Override
            public void accept(Integer integer) {
                integer = integer + 1;
            }
        };
        RunChecker.trace("call copy inner class");
        for (int i = 0; i < time; i++) {
            test(cons);
        }
        RunChecker.end("call copy inner class");

        RunChecker.trace("call inner class with local");
        for (int i = 0; i < time; i++) {
            test(new Consumer<Integer>() {

                @Override
                public void accept(Integer integer) {
                    integer = value + 1;
                }
            });
        }
        RunChecker.end("call inner class with local");

        RunChecker.trace("call inner class with this");
        for (int i = 0; i < time; i++) {
            test(new Consumer<Integer>() {

                @Override
                public void accept(Integer integer) {
                    integer = LambdaTest.this.fieldValue + 1;
                }
            });
        }
        RunChecker.end("call inner class with this");

    }

    public static void main(String[] args) {
        System.out.println("First ================================== First");
        new LambdaTest().runTest();
        System.out.println("Second ================================== Second");
        new LambdaTest().runTest();
        System.out.println("end");
    }

}
