package com.tny.game.data.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/19 12:29 下午
 */
public interface JsonEntityConverterMapperCustomizer {

	void customize(ObjectMapper mapper);

}
