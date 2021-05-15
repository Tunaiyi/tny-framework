package com.tny.game.net.command.dispatcher;

/**
 * Created by Kun Yang on 2017/5/26.
 */
public class ControllerMessageCommandContext extends MessageCommandContext {

    protected MethodControllerHolder controller;

    public ControllerMessageCommandContext(MethodControllerHolder controller) {
        super(controller.getName());
        this.controller = controller;
    }

    public MethodControllerHolder getController() {
        return this.controller;
    }

    @Override
    public String getName() {
        return this.controller.getName();
    }

}
