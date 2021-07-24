package com.tny.game.common.codec;

import org.springframework.util.MimeType;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/8/19 6:24 下午
 */
public interface ObjectCodecorFactory {

    Collection<MimeType> getMediaTypes();

    <T> ObjectCodec<T> createCodecor(Type clazz);

}
