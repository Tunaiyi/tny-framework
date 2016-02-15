package com.tny.game.actor.task;

import com.tny.game.base.item.Do;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by Kun Yang on 16/1/22.
 */
abstract class DefaultTaskStage<R> extends BaseTaskStage<R> {

    protected TaskFragment<Object, R> fragment;

    @SuppressWarnings("unchecked")
    public DefaultTaskStage(CommonTaskStage head, TaskFragment<?, R> fragment) {
        super(head);
        this.fragment = (TaskFragment<Object, R>) fragment;
    }

    @Override
    public TaskFragment<?, ?> getTaskFragment() {
        return fragment;
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        VoidTaskStage stage = Stages.supply(() -> {

            String name = "1232222";
            System.out.println("supplier " + name);
            return name;

        }).thenApply((value) -> {

            Integer id = Integer.parseInt(value);
            System.out.println("thenApply " + id);
            return System.currentTimeMillis() + 3000;

        }).joinApply((time) -> Stages.awaitSupply(() -> {

            if (System.currentTimeMillis() > time) {
                System.out.println("时间已到达");
                return Do.succ(time);
            } else {
                return Do.fail();
            }

        }, Duration.ofSeconds(10))).thenAccept((value) -> {
            System.out.println("thenAccept " + new Date(value));
        }).awaitRun(Stages.time(Duration.ofSeconds(3)))
                .thenRun(() -> System.out.println("OK"));

        while (!stage.isFinalDone()) {
            stage.start();
        }

        if (stage.isFinalFailed()) {
            System.out.println("stage.isFinalFailed() = " + stage.isFinalFailed());
            stage.getFinalCause().printStackTrace();
        }
    }

}
