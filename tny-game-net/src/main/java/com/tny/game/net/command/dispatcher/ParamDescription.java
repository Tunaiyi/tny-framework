package com.tny.game.net.command.dispatcher;

import com.tny.game.common.number.*;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.expr.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2022/1/7 5:52 PM
 */
class ParamDescription {

    /* body 为List时 索引 */
    private int index;

    /* body 为Map时 key */
    private String key;

    private ExprHolder formula;

    private ParamMode mode = ParamMode.NONE;

    /* 参数类型 */
    private final Class<?> paramClass;

    private boolean require;

    private final AnnotationHolder annotationHolder;

    private final MethodControllerHolder method;

    ParamDescription(MethodControllerHolder method, Class<?> paramClass, List<Annotation> paramAnnotations,
            LocalNum<Integer> indexCounter,
            ExprHolderFactory exprHolderFactory) {
        this.method = method;
        this.paramClass = paramClass;
        this.annotationHolder = new AnnotationHolder(paramAnnotations);
        this.require = true;
        this.mode = ParamMode.NONE;
        if (NetBootstrapSetting.class.isAssignableFrom(this.paramClass)) {
            this.mode = ParamMode.SETTING;
        } else if (paramClass == Endpoint.class) {
            this.mode = ParamMode.ENDPOINT;
        } else if (paramClass == Session.class) {
            this.mode = ParamMode.SESSION;
        } else if (paramClass == Tunnel.class) {
            this.mode = ParamMode.TUNNEL;
        } else if (paramClass == RelayTunnel.class) {
            this.mode = ParamMode.RELAY_TUNNEL;
        } else if (paramClass == Message.class) {
            this.mode = ParamMode.MESSAGE;
        } else if (ResultCode.class.isAssignableFrom(this.paramClass)) {
            this.mode = ParamMode.CODE;
        } else {
            if (this.annotationHolder.isEmpty()) {
                this.index = indexCounter.intValue();
                indexCounter.add(1);
                this.mode = ParamMode.INDEX_PARAM;
            } else {
                for (Class<?> annotationClass : this.annotationHolder.getAnnotationClasses()) {
                    if (annotationClass == MsgBody.class) {
                        MsgBody body = this.annotationHolder.getAnnotation(MsgBody.class);
                        this.mode = ParamMode.BODY;
                        this.require = body.require();
                    } else if (annotationClass == MsgParam.class) {
                        /* 参数注解 */
                        MsgParam msgParam = this.annotationHolder.getAnnotation(MsgParam.class);
                        this.require = msgParam.require();
                        if (StringUtils.isNoneBlank(msgParam.value())) {
                            this.key = msgParam.value();
                            this.formula = exprHolderFactory.create("_body." + this.key.trim());
                            this.mode = ParamMode.KEY_PARAM;
                        } else {
                            if (msgParam.index() >= 0) {
                                this.index = msgParam.index();
                            } else {
                                this.index = indexCounter.intValue();
                                indexCounter.add(1);
                            }
                            this.mode = ParamMode.INDEX_PARAM;
                        }
                    } else if (annotationClass == UserId.class) {
                        this.mode = ParamMode.UserID;
                    } else if (annotationClass == MsgCode.class) {
                        if (paramClass == Integer.class || paramClass == int.class) {
                            this.mode = ParamMode.CODE_NUM;
                        } else if (ResultCode.class.isAssignableFrom(this.paramClass)) {
                            this.mode = ParamMode.CODE;
                        } else {
                            throw new IllegalArgumentException(format("{} 类型参数只能是 {} {} {}, 无法为 {}",
                                    MsgCode.class, Integer.class, int.class, ResultCode.class));
                        }
                    } else {
                        this.index = indexCounter.intValue();
                        indexCounter.add(1);
                        this.mode = ParamMode.INDEX_PARAM;
                    }
                }
            }
        }
    }

    AnnotationHolder getAnnotationHolder() {
        return annotationHolder;
    }

    List<Annotation> getAnnotations() {
        return annotationHolder.getAnnotations();
    }

    Object getValue(NetTunnel<?> tunnel, Message message, Object body) throws CommandException {
        boolean require = this.require;
        if (body == null) {
            body = message.bodyAs(Object.class);
        }
        MessageHead head = message.getHead();
        Object value = null;
        switch (this.mode) {
            case MESSAGE:
                value = message;
                break;
            case ENDPOINT: {
                value = tunnel.getEndpoint();
                break;
            }
            case SESSION: {
                value = tunnel.getEndpoint();
                if (value instanceof Session) {
                    break;
                }
                throw new NullPointerException(format("{} session is null", tunnel));
            }
            case CLIENT: {
                value = tunnel.getEndpoint();
                if (value instanceof Client) {
                    break;
                }
                throw new NullPointerException(format("{} session is null", tunnel));
            }
            case TUNNEL:
                value = tunnel;
                break;
            case RELAY_TUNNEL:
                if (tunnel instanceof RelayTunnel) {
                    value = tunnel;
                } else {
                    throw new CommandException(NetResultCode.SERVER_EXECUTE_EXCEPTION, format("{} 并非可转发通道 RelayTunnel", tunnel));
                }
                break;
            case SETTING:
                NetworkContext context = tunnel.getContext();
                value = context.getSetting();
                break;
            case BODY:
                value = body;
                break;
            case UserID:
                value = tunnel.getUserId();
                break;
            case INDEX_PARAM:
                try {
                    if (body == null) {
                        if (require) {
                            throw new NullPointerException(format("{} 收到消息体为 null"));
                        }
                        break;
                    }
                    if (body instanceof List) {
                        value = ((List<?>)body).get(this.index);
                    } else if (body.getClass().isArray()) {
                        value = Array.get(body, this.index);
                    } else {
                        throw new CommandException(NetResultCode.SERVER_EXECUTE_EXCEPTION,
                                format("{} 收到消息体为 {}, 不可通过index获取", this.method, body.getClass()));
                    }
                } catch (CommandException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new CommandException(NetResultCode.SERVER_EXECUTE_EXCEPTION, format("{} 调用异常", this.method), e);
                }
                break;
            case KEY_PARAM:
                if (body == null) {
                    if (require) {
                        throw new NullPointerException(format("{} 收到消息体为 null"));
                    } else {
                        break;
                    }
                }
                if (body instanceof Map) {
                    value = ((Map<?, ?>)body).get(this.key);
                } else {
                    value = this.formula.createExpr()
                            .put("_body", body)
                            .execute(this.paramClass);
                }
                break;
            case CODE:
                value = ResultCodes.of(head.getCode());
                break;
            case CODE_NUM:
                value = head.getCode();
                break;
        }
        if (value != null && !this.paramClass.isInstance(value)) {
            value = ObjectAide.convertTo(value, this.paramClass);
        }
        return value;
    }

}
