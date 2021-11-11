package com.tny.game.net.annotation;

import com.tny.game.net.message.*;

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
 *            @Controller(check = true)
 * 			public class ControllerTest {
 *
 *              public List<Integer> getPlayersIdList(){ // doSometing }
 *
 *                @Controller(check = false, name="playerId")
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
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Rpc {

	/**
	 * 处理协议号
	 * <p>
	 * <p>
	 * 被Controller标记的类的模塊ID <br>
	 * 被Controller标记的方法的业务方法ID <br>
	 *
	 * @return 处理协议号
	 */
	int value();

	/**
	 * @return 线路id
	 */
	int[] line() default {};

	/**
	 * @return 默认处理所有
	 */
	MessageMode[] modes() default {};

}