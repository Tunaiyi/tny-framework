package com.tny.game.common.result;

import com.tny.game.common.utils.*;
import org.apache.commons.lang3.EnumUtils;

import java.util.*;

public class ResultCodes {

	private static final Map<Integer, ResultCode> codeMap = new HashMap<>();

	public static ResultCode of(int id) {
		return codeMap.get(id);
	}

	public static <E extends Enum<E> & ResultCode> void registerClass(Class<E> codeClass) {
		EnumUtils.getEnumList(codeClass).forEach(ResultCodes::registerCode);
	}

	public static void registerCode(ResultCode code) {
		ResultCode old = codeMap.put(code.getCode(), code);
		if (old != null && old != code) {
			IllegalArgumentException e = new IllegalArgumentException(StringAide.format("{}.{} 与 {}.{} id 都为 {}",
					code.getClass(), code, old.getClass(), old, old.getCode()));
			e.printStackTrace();
			throw e;
		}
	}

	public static boolean isSuccess(int code) {
		return code == ResultCode.SUCCESS_CODE;
	}

	public static boolean isSuccess(ResultCode code) {
		return code.getCode() == ResultCode.SUCCESS_CODE;
	}

}
