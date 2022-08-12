/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.message;

public class Protocols {

    public static final int PUSH_ID = 0;

    public static final DefaultProtocol PUSH = new DefaultProtocol(PUSH_ID, 0);

    protected static class DefaultProtocol implements Protocol {

        private int id;

        private int line = 0;

        private DefaultProtocol(int id, int line) {
            this.id = id;
            this.line = line;
        }

        @Override
        public int getProtocolId() {
            return this.id;
        }

        @Override
        public int getLine() {
            return this.line;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.id;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Protocol other = (Protocol)obj;
            return this.id == other.getProtocolId();
        }

    }

    public static Protocol protocol(int protocol) {
        return new DefaultProtocol(protocol, 0);
    }

    public static Protocol protocol(int protocol, int line) {
        return new DefaultProtocol(protocol, line);
    }

}
