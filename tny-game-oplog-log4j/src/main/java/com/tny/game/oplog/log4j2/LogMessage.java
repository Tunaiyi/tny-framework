package com.tny.game.oplog.log4j2;

import com.tny.game.oplog.Log;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.DateTime;

public class LogMessage extends MapMessage {

    private static final long serialVersionUID = 1L;

    private Log log;

    public LogMessage(Log log) {
        this.log = log;
        DateTime dateTime = log.getLogAt();
        this.put("sid", Integer.toString(log.getServerID()));
        this.put("lyyyy", String.valueOf(dateTime.getYear()));
        this.put("lMM", String.valueOf(dateTime.getMonthOfYear()));
        this.put("ldd", String.valueOf(dateTime.getDayOfMonth()));
        this.put("lHH", String.valueOf(dateTime.getHourOfDay()));
        this.put("lmm", String.valueOf(dateTime.getMinuteOfHour()));
        this.put("lss", String.valueOf(dateTime.getSecondOfDay()));
        this.put("lSSS", String.valueOf(dateTime.getMillisOfSecond()));
    }

    public Log getLog() {
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
