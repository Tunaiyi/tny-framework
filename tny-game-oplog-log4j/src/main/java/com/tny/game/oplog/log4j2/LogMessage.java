package com.tny.game.oplog.log4j2;

import com.tny.game.oplog.Loggable;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.util.Strings;

public class LogMessage extends MapMessage {

    private static final long serialVersionUID = 1L;

    private Loggable log;

    public LogMessage(Loggable log) {
        this.log = log;
        this.put("sid", Integer.toString(log.getServerID()));
    }

    public Loggable getLog() {
        return this.log;
    }

    @Override
    public String getFormattedMessage() {
        return Strings.EMPTY;
    }

    public int getServerID() {
        return log.getServerID();
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
