package sg.game;

import com.tny.game.common.RunningChecker;

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
        RunningChecker.doStart("call at first");
        consumer = TestConsumer.consumer::test;
        test(TestConsumer.consumer::test);
        RunningChecker.endPrint("call at first");

        RunningChecker.doStart("call testLD");
        for (int i = 0; i < time; i++) {
            testLD();
        }
        RunningChecker.endPrint("call testLD");

        RunningChecker.doStart("call testOJ");
        for (int i = 0; i < time; i++) {
            testOJ();
        }
        RunningChecker.endPrint("call testOJ");

        RunningChecker.doStart("call ref Object.method");
        for (int i = 0; i < time; i++) {
            test(TestConsumer.consumer::test);
        }
        RunningChecker.endPrint("call ref Object.method");

        RunningChecker.doStart("call ref this.method");
        for (int i = 0; i < time; i++) {
            test(this::test);
        }
        RunningChecker.endPrint("call ref this.method");

        RunningChecker.doStart("call ref Class.StaticMethod");
        for (int i = 0; i < time; i++) {
            test(TestConsumer::testStatic);
        }
        RunningChecker.endPrint("call ref Class.StaticMethod");

        consumer = TestConsumer.consumer::test;
        RunningChecker.doStart("call copy ref Object.method");
        for (int i = 0; i < time; i++) {
            test(consumer);
        }
        RunningChecker.endPrint("call copy ref Object.method");


        RunningChecker.doStart("call lambda");
        for (int i = 0; i < time; i++) {
            test((s) -> TestConsumer.consumer.test(s));
        }
        RunningChecker.endPrint("call lambda");

        final int value = 1;
        RunningChecker.doStart("call lambda with local");
        for (int i = 0; i < time; i++) {
            test((s) -> s = value + 1);
        }
        RunningChecker.endPrint("call lambda with local");

        RunningChecker.doStart("call lambda with this");
        for (int i = 0; i < time; i++) {
            test((s) -> s = this.fieldValue + 1);
        }
        RunningChecker.endPrint("call lambda with this");

        consumer = (s) -> TestConsumer.consumer.test(s);
        RunningChecker.doStart("call copy lambda");
        for (int i = 0; i < time; i++) {
            test(consumer);
        }
        RunningChecker.endPrint("call copy lambda");


        RunningChecker.doStart("call inner class");
        for (int i = 0; i < time; i++) {
            test(new Consumer<Integer>() {

                @Override
                public void accept(Integer integer) {
                    integer = integer + 1;
                }
            });
        }
        RunningChecker.endPrint("call inner class");


        Consumer<Integer> cons = new Consumer<Integer>() {

            @Override
            public void accept(Integer integer) {
                integer = integer + 1;
            }
        };
        RunningChecker.doStart("call copy inner class");
        for (int i = 0; i < time; i++) {
            test(cons);
        }
        RunningChecker.endPrint("call copy inner class");


        RunningChecker.doStart("call inner class with local");
        for (int i = 0; i < time; i++) {
            test(new Consumer<Integer>() {

                @Override
                public void accept(Integer integer) {
                    integer = value + 1;
                }
            });
        }
        RunningChecker.endPrint("call inner class with local");

        RunningChecker.doStart("call inner class with this");
        for (int i = 0; i < time; i++) {
            test(new Consumer<Integer>() {

                @Override
                public void accept(Integer integer) {
                    integer = fieldValue + 1;
                }
            });
        }
        RunningChecker.endPrint("call inner class with this");

    }

    public static void main(String[] args) {
        System.out.println("First ================================== First");
        new LambdaTest().runTest();
        System.out.println("Second ================================== Second");
        new LambdaTest().runTest();
        System.out.println("end");
    }

}
