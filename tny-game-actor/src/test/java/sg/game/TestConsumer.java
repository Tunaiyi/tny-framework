package sg.game;

/**
 * Created by Kun Yang on 16/2/21.
 */
public class TestConsumer {

    public static TestConsumer consumer = new TestConsumer();

    public void test(int test) {
        test = test + 1;
    }

    public static void testStatic(int test) {
        test = test + 1;
    }

}
