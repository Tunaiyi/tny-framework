package test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadTest {

    static Thread thread;

    static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) throws InterruptedException {

        final Future<?> future = executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    System.out.println("sleep start");
                    thread = Thread.currentThread();
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Thread.sleep(1000);

        executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    future.cancel(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            System.out.println("wait");
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        executorService.shutdown();

    }
}
