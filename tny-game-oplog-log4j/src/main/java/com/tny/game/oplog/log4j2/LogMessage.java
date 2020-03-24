package com.tny.game.oplog.log4j2;

import com.tny.game.oplog.*;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDateTime;

public class LogMessage extends MapMessage {

    private static final long serialVersionUID = 1L;

    private Log log;

    public LogMessage(Log log) {
        this.log = log;
        LocalDateTime dateTime = LocalDateTime.from(log.getLogAt());
        this.put("sid", Integer.toString(log.getServerId()));
        this.put("lyyyy", String.valueOf(dateTime.getYear()));
        this.put("lMM", String.valueOf(dateTime.getMonthValue()));
        this.put("ldd", String.valueOf(dateTime.getDayOfMonth()));
        this.put("lHH", String.valueOf(dateTime.getHour()));
        this.put("lmm", String.valueOf(dateTime.getMinute()));
        this.put("lss", String.valueOf(dateTime.getSecond()));
        this.put("lSSS", String.valueOf(dateTime.getNano() / 1000));
    }

    public Log getLog() {
        return this.log;
    }

    @Override
    public String getFormattedMessage() {
        return Strings.EMPTY;
    }

    public int getServerId() {
        return this.log.getServerId();
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
