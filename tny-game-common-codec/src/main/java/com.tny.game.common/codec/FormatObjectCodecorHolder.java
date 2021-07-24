package com.tny.game.common.codec;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/10/9 8:35 下午
 */
public class FormatObjectCodecorHolder {

    private String format;

    private ObjectCodec<?> codecor;

    public FormatObjectCodecorHolder(String format, ObjectCodec<?> codecor) {
        this.format = format;
        this.codecor = codecor;
    }

    public String getFormat() {
        return this.format;
    }

    public ObjectCodec<?> getCodecor() {
        return this.codecor;
    }

}
