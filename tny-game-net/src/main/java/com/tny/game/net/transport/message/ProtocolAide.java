package com.tny.game.net.transport.message;

public class ProtocolAide {

    public static final DefaultProtocol PUSH = new DefaultProtocol(-1);

    protected static class DefaultProtocol implements Protocol {

        private int protocol;

        private DefaultProtocol(int protocol) {
            this.protocol = protocol;
        }

        @Override
        public int getNumber() {
            return this.protocol;
        }

        public void setProtocol(int protocol) {
            this.protocol = protocol;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + protocol;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Protocol other = (Protocol) obj;
            return protocol == other.getNumber();
        }

    }

    public static Protocol protocol(int protocol) {
        return new DefaultProtocol(protocol);
    }

}
