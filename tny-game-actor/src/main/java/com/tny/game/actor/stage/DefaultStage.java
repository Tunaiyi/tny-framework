package com.tny.game.actor.stage;

/**
 * 默认阶段实现
 * Created by Kun Yang on 16/1/22.
 */
abstract class DefaultStage<R> extends BaseStage<R> {

    protected Fragment<Object, R> fragment;

    @SuppressWarnings("unchecked")
    public DefaultStage(Object name, Fragment<?, R> fragment) {
        super(name);
        this.fragment = (Fragment<Object, R>)fragment;
    }

    @Override
    public Fragment<?, ?> getFragment() {
        return fragment;
    }

    //    public static void main(String[] args) throws ExecutionException, InterruptedException {
    //        VoidTaskStage stage = Stages.supply(() -> {
    //
    //            String name = "1232222";
    //            System.out.println("supplier " + name);
    //            return name;
    //
    //        }).then((value) -> {
    //
    //            Integer id = Integer.parseInt(value);
    //            System.out.println("thenApply " + id);
    //            return System.currentTimeMillis() + 3000;
    //
    //        }).join((time) -> Stages.waitFor(() -> {
    //
    //            if (System.currentTimeMillis() > time) {
    //                System.out.println("时间已到达");
    //                return Do.succ(time);
    //            } else {
    //                return Do.fail();
    //            }
    //
    //        }, Duration.ofSeconds(10))).then((value) -> {
    //            System.out.println("thenAccept " + new Date(value));
    //        }).waitUntil(Stages.time(Duration.ofSeconds(3)))
    //                .then(() -> System.out.println("OK"));
    //
    //        while (!stage.isDone()) {
    //            StageUtils.run(stage);
    //        }
    //
    //        if (stage.isFailed()) {
    //            System.out.println("stage.isFinalFailed() = " + stage.isFailed());
    //            stage.getCause().printStackTrace();
    //        }
    //    }

}
