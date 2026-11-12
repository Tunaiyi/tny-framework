/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    int line() default 0;

    /**
     * @return 默认处理所有
     */
    MessageMode[] modes() default {};

}