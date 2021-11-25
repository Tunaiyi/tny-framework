package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/19 12:29 下午
 */
public interface ObjectMapperCustomizer {

	void customize(ObjectMapper mapper);

}
