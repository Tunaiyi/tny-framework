package com.tny.game.basics.item.loader.jackson;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.tny.game.expr.*;
import org.junit.platform.commons.util.*;
import org.slf4j.*;

import java.io.IOException;

public class ExprHolderDeserializer extends JsonDeserializer<ExprHolder> {

	private static final Logger LOG = LoggerFactory.getLogger(ExprHolderDeserializer.class);

	private final ExprHolderFactory exprHolderFactory;

	public ExprHolderDeserializer(ExprHolderFactory exprHolderFactory) {
		this.exprHolderFactory = exprHolderFactory;
	}

	@Override
	public ExprHolder deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String expr = p.getValueAsString();
		try {
			if (StringUtils.isBlank(expr)) {
				return null;
			}
			return exprHolderFactory.create(expr);
		} catch (Throwable e) {
			LOG.error("{}", expr, e);
			throw e;
		}
	}

}