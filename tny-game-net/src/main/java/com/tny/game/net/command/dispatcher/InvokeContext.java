package com.tny.game.net.command.dispatcher;

/**
 * Created by Kun Yang on 2017/5/26.
 */
public class InvokeContext extends CommandContext {

    protected String name;

    protected MethodControllerHolder controller;

    public InvokeContext(MethodControllerHolder controller) {
        super(controller.getName());
        this.controller = controller;
    }

    public MethodControllerHolder getController() {
        return controller;
    }


    public String getName() {
        MethodControllerHolder controller = this.controller;
        // if (controller == null) {
        //     MessageHead head = message.getHead();
        //     return this.name = String.valueOf(head.getId());
        // } else {
        return this.name = controller.getControllerClass() + "." + controller.getName();
        // }
    }

}
