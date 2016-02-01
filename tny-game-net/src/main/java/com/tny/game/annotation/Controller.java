package com.tny.game.annotation;

import com.tny.game.net.dispatcher.Session;

import java.lang.annotation.*;

/**
 * @author KGTny
 * @ClassName: Controller
 * @Description: 标注处理模块
 * @date 2011-8-16 上午11:01:28
 * <p>
 * <p>
 * 被Controller标记的类作为业务类进行解析<br>
 * 被Controller标记的方法的业务方法名稱,與Request的Protocol相对应,默認為方法名<br>
 * <p>
 * <pre>
 * 			@Controller(check = true)
 * 			public class ControllerTest {
 *
 *              public List<Integer> getPlayersIdList(){ // doSometing }
 *
 * 				@Controller(check = false, name="playerId")
 * 				public Integer getPlayerIdList(){
 *                   // doSometing
 *              }
 *
 *           }
 * </pre>
 * <p>
 * Request.getModule() 等于ControllerTest Request.getProtocol()
 * 等于getPlayersIdList 系统会调用getPlayersIdList,并且进行信息校验
 * <p>
 * Request.getModule() 等于ControllerTest Request.getProtocol() 等于playerId
 * 系统会调用getPlayerIdList,但不会进行信息校验
 * @see com.tny.game.net.dispatcher.Request
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Controller {

    /**
     * 身份認證標記
     * <p>
     * <p>
     * 此操作是否需要登录认证.<br>
     * true为需要身份认证 false为不需要身份认证,默认为false<br>
     *
     * @return
     */
    public boolean auth() default true;

    /**
     * 消息校驗標記
     * <p>
     * <p>
     * true為需要做消息校驗,false為不需要做消息校驗,默認為true<br>
     *
     * @return
     */
    public boolean check() default true;

    /**
     * 模块/业务方法名称 优先级比name高
     * <p>
     * <p>
     * 被Controller标记的类的模塊ID,與Request的Module相对应,默认-1代表无效<br>
     * 被Controller标记的方法的业务方法ID,與Request的Protocol相对应,默认-1代表无效<br>
     *
     * @return
     * @see com.tny.game.net.dispatcher.Request
     */
    public int value() default 0;

    /**
     * 模块/业务方法名称
     * <p>
     * <p>
     * 被Controller标记的类的模塊名稱,與Request的Module相对应,默認為類名<br>
     * 被Controller标记的方法的业务方法名稱,與Request的Protocol相对应,默認為方法名<br>
     *
     * @return
     * @see com.tny.game.net.dispatcher.Request
     */
    public String name() default "";

    //	/**
    //	 * 用户类型
    //	 * <p>
    //	 *
    //	 * 该控制器调用的用户类型<br>
    //	 *
    //	 * @return
    //	 */
    //	public UserType userType() default UserType.User;

    /**
     * 用户组名称
     * <p>
     * <p>
     * 当userType System<br>
     *
     * @return
     */
    public String[] userGroup() default {Session.DEFAULT_USER_GROUP};

    /**
     * 服务器类型
     *
     * @return 服务器类型
     */
    public String[] serverType() default {};

    /**
     * 是否要检测请求超时
     *
     * @return 检测true 不检测false
     */
    public boolean timeOut() default true;

    /**
     * 获取请求的生命周期(毫秒)<br>
     *
     * @return <= 0 为无限制
     */
    public long requestLife() default 60000L;

}