package com.tny.game.actor.stage;

/**
 * Created by Kun Yang on 16/1/22.
 */
class AwaysStage extends BaseStage<Void> {

    protected TaskFragment<Object, Object> fragment;

    @SuppressWarnings("unchecked")
    public AwaysStage(CommonStage head, TaskFragment<?, ?> fragment) {
        super(head);
        this.fragment = (TaskFragment<Object, Object>) fragment;
    }

    @Override
    public TaskFragment<?, ?> getTaskFragment() {
        return fragment;
    }

}
