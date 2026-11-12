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
            Protocol other = (Protocol) obj;
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
