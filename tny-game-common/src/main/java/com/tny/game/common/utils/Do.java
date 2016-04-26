package com.tny.game.common.utils;

import com.tny.game.common.ExceptionUtils;

/**
 * 做完的结果
 *
 * @author KGTny
 */
public class Do {

    private Do() {
    }

    private static Done<?> FAILED = new DefaultDone<>();

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @param value
     * @return
     */
    public static <M, MC extends M> Done<M> succ(MC value) {
        ExceptionUtils.checkNotNull(value == null, "Done.value is null");
        return new DefaultDone<>(value);
    }

    public static <M, MC extends M> Done<M> succNullable(MC value) {
        return new DefaultDone<>(value);
    }

    @SuppressWarnings("unchecked")
    public static <M> Done<M> fail() {
        return (Done<M>) FAILED;
    }

    private static class DefaultDone<M> extends Done<M> {

        private boolean done;
        private M returnValue;


        private DefaultDone() {
            this.done = false;
            this.returnValue = null;
        }

        private DefaultDone(M returnValue) {
            this.done = true;
            this.returnValue = returnValue;
        }

        /**
         * 是否成功 code == ItemResultCode.SUCCESS
         *
         * @return
         */
        public boolean isSuccess() {
            return done;
        }

        /**
         * 是否有结果值呈现
         *
         * @return
         */
        public boolean isPresent() {
            return this.returnValue != null;
        }

        /**
         * 获取返回结果
         *
         * @return
         */
        public M get() {
            return this.returnValue;
        }

        @Override
        public String toString() {
            return "DefaultDone{" +
                    "done=" + done +
                    ", returnValue=" + returnValue +
                    '}';
        }
    }


}
