package com.tny.game.net.message;

public class Protocols {

    public static final DefaultProtocol PUSH = new DefaultProtocol(0, 0);

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
