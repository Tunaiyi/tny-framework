package com.tny.game.oplog.log4j2;

import com.tny.game.oplog.Loggable;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.Strings;

public class LogMessage implements Message {

    private static final long serialVersionUID = 1L;

    private Loggable log;

    public LogMessage(Loggable log) {
        this.log = log;
    }

    public Loggable getLog() {
        return this.log;
    }

    @Override
    public String getFormattedMessage() {
        return Strings.EMPTY;
    }

    @Override
    public String getFormat() {
        return Strings.EMPTY;
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }

    @Override
    public Throwable getThrowable() {
        return null;
    }

}
