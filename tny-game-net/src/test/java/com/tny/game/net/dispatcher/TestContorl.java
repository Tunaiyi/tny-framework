package com.tny.game.net.dispatcher;

import com.tny.game.annotation.Controller;
import com.tny.game.annotation.Plugin;
import com.tny.game.net.base.ResultFactory;

import static org.junit.Assert.assertEquals;

@Controller()
@Plugin(before = {TestPluginBefore.class}, after = {TestPluginAfter.class})
public class TestContorl {

    protected static final int test1 = 10001;
    protected static final int test2 = 10002;
    protected static final int test3 = 10003;
    protected static final int test4 = 10004;
    protected static final int nomalUnlogin = 10005;
    protected static final int nomalLogin = 10006;
    protected static final int systemLogin = 10007;
    protected static final int login = 10008;

    @Controller(value = test1)
    public CommandResult test1(Request request, long id) {
        System.out.println("Ok " + id);
        assertEquals(id, 171772272);
        return ResultFactory.success("ok");
    }

    @Controller(value = test2, userGroup = {"Tom"})
    public CommandResult test2(Request request, long id) {
        System.out.println("Ok " + id);
        assertEquals(id, 171772272);
        return ResultFactory.success("ok");
    }

    @Controller(value = test3, userGroup = {"loginSystem"})
    public CommandResult test3(Request request, long id) {
        System.out.println("Ok " + id);
        assertEquals(id, 171772272);
        return ResultFactory.success("ok");
    }

    @Controller(value = test4)
    public CommandResult test4(Request request, long id) {
        throw new NullPointerException("Null");
    }

    @Controller(value = nomalUnlogin)
    public CommandResult nomalUnlogin(Request request, long id, int number) {
        System.out.println("Ok " + id);
        assertEquals(id, 171772272);
        assertEquals(number, 1);
        return ResultFactory.success("ok");
    }

    @Controller(value = nomalLogin)
    public CommandResult nomalLogin(Request request, long id, int number) {
        System.out.println("Ok " + id);
        assertEquals(id, 171772272);
        assertEquals(number, 2);
        return ResultFactory.success("ok");
    }

    @Controller(value = systemLogin, userGroup = {"loginSystem"})
    public CommandResult systemLogin(Request request, long id, int number) {
        System.out.println("Ok " + id);
        assertEquals(id, 171772272);
        assertEquals(number, 3);
        return ResultFactory.success("ok");
    }

    @Controller(value = login)
    public CommandResult login(Request request, Long id, String string, String group, String ip) {
        System.out.println("Ok " + id);
        return ResultFactory.success("ok");
    }

}
