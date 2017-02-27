package com.tny.game.annotation;

import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.Session;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
 * @see Request
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
    boolean auth() default true;

    /**
     * 消息校驗標記
     * <p>
     * <p>
     * true為需要做消息校驗,false為不需要做消息校驗,默認為true<br>
     *
     * @return
     */
    boolean check() default true;

    /**
     * 处理协议号列表, 默认处理所有
     * <p>
     * <p>
     * 被Controller标记的类的模塊ID,與Request的Module相对应,默认-1代表无效<br>
     * 被Controller标记的方法的业务方法ID,與Request的Protocol相对应,默认-1代表无效<br>
     *
     * @return
     * @see Request
     */
    int value();

    /**
     * 模块/业务方法名称
     * <p>
     * <p>
     * 被Controller标记的类的模塊名稱,與Request的Module相对应,默認為類名<br>
     * 被Controller标记的方法的业务方法名稱,與Request的Protocol相对应,默認為方法名<br>
     *
     * @return
     * @see Request
     */
    String name() default "";

    /**
     * 用户组名称
     * <p>
     * <p>
     * 当userType System<br>
     *
     * @return
     */
    String[] userGroup() default {Session.DEFAULT_USER_GROUP};

    /**
     * 程序类型(服务器类型)
     *
     * @return 程序类型
     */
    String[] appType() default {};

    /**
     * @return 可处理的消息码, 默认为全部
     */
    int[] codes() default {};

    /**
     * 是否要检测请求超时
     *
     * @return 检测true 不检测false
     */
    boolean timeOut() default true;

    /**
     * 获取请求的生命周期(毫秒)<br>
     *
     * @return <= 0 为无限制
     */
    long requestLife() default 60000L;

}