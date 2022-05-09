package com.tny.game.net.message;

/**
 * Rpc转发HeaderBuilder
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 02:37
 **/
public abstract class MessageHeaderBuilder<H extends MessageHeader<?>> {

    private H header;

    public MessageHeaderBuilder() {
    }

    public boolean isHasHeader() {
        return header != null;
    }

    protected H header() {
        if (header == null) {
            header = create();
        }
        return header;
    }

    protected abstract H create();

    public H build() {
        H header = this.header;
        this.header = null;
        return header;
    }

}
