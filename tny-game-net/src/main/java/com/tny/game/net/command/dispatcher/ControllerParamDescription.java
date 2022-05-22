package com.tny.game.net.command.dispatcher;

import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.List;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.command.dispatcher.ParamMode.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2022/1/7 5:52 PM
 */
class ControllerParamDescription {

    /* body 为List时 索引 */
    private int index;

    private ParamMode mode;

    private String headerKey;

    /* 参数类型 */
    private final Class<?> paramClass;

    private boolean require;

    private final AnnotationHolder annotationHolder;

    private final MethodControllerHolder method;

    ControllerParamDescription(MethodControllerHolder method, Class<?> paramClass, List<Annotation> paramAnnotations,
            ParamIndexCreator indexCreator) {
        this.method = method;
        this.paramClass = paramClass;
        this.annotationHolder = new AnnotationHolder(paramAnnotations);
        this.require = true;
        this.mode = NONE;
        RpcOptional optional = this.annotationHolder.getAnnotation(RpcOptional.class);
        if (optional != null) {
            this.require = false;
        }
        if (NetBootstrapSetting.class.isAssignableFrom(this.paramClass)) {
            this.mode = SETTING;
        } else if (Session.class.isAssignableFrom(paramClass)) {
            this.mode = SESSION;
        } else if (Client.class.isAssignableFrom(paramClass)) {
            this.mode = CLIENT;
        } else if (Endpoint.class.isAssignableFrom(paramClass)) {
            this.mode = ENDPOINT;
        } else if (Tunnel.class.isAssignableFrom(paramClass)) {
            this.mode = TUNNEL;
        } else if (paramClass == Message.class) {
            this.mode = MESSAGE;
        } else if (ResultCode.class.isAssignableFrom(this.paramClass)) {
            this.mode = CODE;
        } else if (MessageHeader.class.isAssignableFrom(this.paramClass)) {
            this.mode = HEADER;
            RpcHeader header = this.annotationHolder.getAnnotation(RpcHeader.class);
            if (header != null) {
                this.headerKey = header.value();
            }
        } else {
            for (Class<?> annotationClass : this.annotationHolder.getAnnotationClasses()) {
                if (annotationClass == RpcBody.class) {
                    this.mode = BODY;
                } else if (annotationClass == RpcParam.class) {
                    /* 参数注解 */
                    RpcParam msgParam = this.annotationHolder.getAnnotation(RpcParam.class);
                    if (msgParam.value() >= 0) {
                        this.index = indexCreator.use(msgParam.value());
                    } else {
                        this.index = indexCreator.peek();
                    }
                    this.mode = PARAM;
                } else if (annotationClass == UserId.class) {
                    this.mode = USER_ID;
                } else if (annotationClass == RpcCode.class) {
                    if (paramClass == Integer.class || paramClass == int.class) {
                        this.mode = CODE_NUM;
                    } else if (ResultCode.class.isAssignableFrom(this.paramClass)) {
                        this.mode = CODE;
                    } else {
                        throw new IllegalArgumentException(format("{} 类型参数只能是 {} {} {}, 无法为 {}",
                                RpcCode.class, Integer.class, int.class, ResultCode.class));
                    }
                } else if (annotationClass == RpcFrom.class) {
                    if (Messager.class.isAssignableFrom(paramClass)) {
                        this.mode = SENDER;
                    }
                    if (RpcServicer.class.isAssignableFrom(paramClass)) {
                        this.mode = FROM_SERVICE;
                    }
                } else if (annotationClass == RpcTo.class) {
                    if (Messager.class.isAssignableFrom(paramClass)) {
                        this.mode = RECEIVER;
                    }
                    if (RpcServicer.class.isAssignableFrom(paramClass)) {
                        this.mode = TO_SERVICE;
                    }
                }
            }
            if (this.mode == NONE && require) {
                if (method.getMessageMode() == MessageMode.REQUEST) {
                    this.index = indexCreator.peek();
                    this.mode = PARAM;
                } else {
                    this.mode = BODY;
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
        NetworkContext context = tunnel.getContext();
        MessageHead head = message.getHead();
        Object value = null;
        switch (this.mode) {
            case NONE:
                break;
            case MESSAGE:
                value = message;
                break;
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
            case ENDPOINT: {
                value = tunnel.getEndpoint();
                break;
            }
            case TUNNEL:
                if (this.paramClass.isInstance(tunnel)) {
                    value = tunnel;
                } else {
                    throw new CommandException(NetResultCode.SERVER_EXECUTE_EXCEPTION, format("{} 并非可转发通道 RelayTunnel", tunnel));
                }
                break;
            case SETTING:
                value = context.getSetting();
                break;
            case BODY:
                value = body;
                break;
            case USER_ID:
                value = tunnel.getUserId();
                break;
            case HEADER:
                if (StringUtils.isNoneBlank(headerKey)) {
                    value = message.getHeader(headerKey, as(this.paramClass));
                } else {
                    List<MessageHeader<?>> headers = message.getHeaders(as(this.paramClass));
                    if (headers.size() == 1) {
                        value = headers.get(0);
                    }
                    if (headers.size() > 1) {
                        throw new CommandException(NetResultCode.SERVER_EXECUTE_EXCEPTION,
                                format("{} 类型的MessageHeader多于一个 {}, 必须通过 MsgHeader 指定 key", this.paramClass, headers));
                    }
                }
                break;
            case PARAM:
                try {
                    if (body != null) {
                        if (body instanceof List) {
                            value = ((List<?>)body).get(this.index);
                        } else if (body.getClass().isArray()) {
                            value = Array.get(body, this.index);
                        } else {
                            throw new CommandException(NetResultCode.SERVER_EXECUTE_EXCEPTION,
                                    format("{} 收到消息体为 {}, 不可通过index获取", this.method, body.getClass()));
                        }
                    }
                } catch (CommandException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new CommandException(NetResultCode.SERVER_EXECUTE_EXCEPTION, format("{} 调用异常", this.method), e);
                }
                break;
            case CODE:
                value = ResultCodes.of(head.getCode());
                break;
            case CODE_NUM:
                value = head.getCode();
                break;
            case FROM_SERVICE: {
                RpcForwardHeader forwardHeader = head.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
                if (forwardHeader != null) {
                    value = forwardHeader.getFrom();
                }
                break;
            }
            case TO_SERVICE: {
                RpcForwardHeader forwardHeader = head.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
                if (forwardHeader != null) {
                    value = forwardHeader.getTo();
                }
                break;
            }
            case SENDER: {
                RpcForwardHeader forwardHeader = head.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
                if (forwardHeader != null) {
                    ForwardMessager sender = forwardHeader.getSender();
                    value = context.getMessagerFactory().createMessager(sender);
                }
                break;
            }
            case RECEIVER: {
                RpcForwardHeader forwardHeader = head.getHeader(MessageHeaderConstants.RPC_FORWARD_HEADER);
                if (forwardHeader != null) {
                    ForwardMessager receiver = forwardHeader.getReceiver();
                    value = context.getMessagerFactory().createMessager(receiver);
                }
                break;
            }
        }
        if (require && value == null) {
            throw new CommandException(NetResultCode.SERVER_EXECUTE_EXCEPTION,
                    format("{} 第 {} 个参数不可为 null", this.method, index));
        }
        if (value != null && !this.paramClass.isInstance(value)) {
            value = ObjectAide.convertTo(value, this.paramClass);
        }
        return value;
    }

}
