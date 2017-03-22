package com.tny.game.net.message.defalut;

import com.tny.game.net.message.AbstractNetMessageBuilder;

/**
 * Created by Kun Yang on 2017/3/21.
 */
public class DefaultMessageBuilder<UID> extends AbstractNetMessageBuilder<UID, DefaultMessage<UID>> {

    protected DefaultMessageBuilder() {
        super(DefaultMessage::new);
    }

    public DefaultMessageBuilder<UID> setGroup(String group) {
        getMessage().setGroup(group);
        return this;
    }

    public DefaultMessageBuilder<UID> setUid(UID uid) {
        getMessage().setUid(uid);
        return this;
    }

    @Override
    protected void doBuild(DefaultMessage<UID> request) {

    }
}
