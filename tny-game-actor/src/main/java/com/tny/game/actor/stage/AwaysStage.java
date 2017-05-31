package com.tny.game.actor.stage;

/**
 * Created by Kun Yang on 16/1/22.
 */
class AwaysStage extends BaseStage<Void> {

    protected Fragment<Object, Object> fragment;

    @SuppressWarnings("unchecked")
    public AwaysStage(Object name, Fragment<?, ?> fragment) {
        super(name);
        this.fragment = (Fragment<Object, Object>) fragment;
    }

    @Override
    public Fragment<?, ?> getFragment() {
        return fragment;
    }

}
