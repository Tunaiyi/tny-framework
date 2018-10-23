package com.tny.game.net.base;

import com.tny.game.common.result.ResultCode;
import com.tny.game.common.utils.DoneResult;
import com.tny.game.net.command.CommandResult;
import com.tny.game.net.message.Protocol;

/**
 * @author KGTny
 * @ClassName: ResultFactory
 * @Description: 响应结果工厂
 * @date 2011-9-19 上午10:56:55
 * <p>
 * 创建响应结果工厂
 * <p>
 * 提供创建响应结果的方法<br>
 */
public class ResultFactory {

    /**
     * 成功不返回数据
     */
    public final static CommandResult NONE = new CommandResultImpl(NetResultCode.SUCCESS, null);

    /**
     * 成功返回数据
     */
    public final static CommandResult SUCC = new CommandResultImpl(NetResultCode.SUCCESS, null);

    /**
     * 创建成功响应结果
     * <p>
     * <p>
     * 创建成功响应结果<br>
     *
     * @param message 信息体
     * @return 返回响应结果
     */
    public static CommandResult success(Object message) {
        return new CommandResultImpl(NetResultCode.SUCCESS, message);
    }

    public static CommandResult result(ResultCode code) {
        if (code.isSuccess())
            return success();
        else
            return fail(code);
    }


    /**
     * 创建成功响应结果
     * <p>
     * <p>
     * 创建成功响应结果<br>
     *
     * @return 返回响应结果
     */
    public static CommandResult success() {
        return SUCC;
    }

    /**
     * 创建成功不返回的响应结果
     * <p>
     * <p>
     * 创建成功响应结果<br>
     *
     * @return 返回响应结果
     */
    public static CommandResult none() {
        return NONE;
    }

    public static CommandResult create(ResultCode code, Object message) {
        return new CommandResultImpl(code, message);
    }

    /**
     * 创建失败响应结果
     * <p>
     * <p>
     * 创建失败响应结果<br>
     *
     * @param code    结果码
     * @param message 消息体
     * @return 返回响应结果
     */
    public static CommandResult fail(ResultCode code, Object message) {
        return new CommandResultImpl(code, message);
    }

    /**
     * 创建失败响应结果
     * <p>
     * <p>
     * 创建失败响应结果<br>
     *
     * @param code 结果码
     * @return 返回响应结果
     */
    public static CommandResult fail(ResultCode code) {
        return new CommandResultImpl(code, null);
    }

    /**
     * 根据done创建响应结果
     *
     * @param done 错误结果
     * @return 返回响应结果
     */
    public static CommandResult fail(DoneResult<?> done) {
        return fail(done.getCode(), null);
    }


    //TODO Protocol 设置
    private static class CommandResultImpl implements CommandResult {

        private final ResultCode resultCode;
        private final Object body;
        private Protocol protocol;

        private CommandResultImpl(ResultCode code) {
            super();
            this.resultCode = code;
            this.body = null;
        }

        private CommandResultImpl(ResultCode code, Object message) {
            super();
            this.resultCode = code;
            this.body = message;
        }

        //		private CommandResultImpl(int code, Object body) {
        //			super();
        //			this.resultCode = code;
        //			this.body = body;
        //		}

        @Override
        public ResultCode getResultCode() {
            return this.resultCode;
        }

        @Override
        public Object getBody() {
            return this.body;
        }

        @Override
        public Protocol getProtocol() {
            return protocol;
        }

    }

}
