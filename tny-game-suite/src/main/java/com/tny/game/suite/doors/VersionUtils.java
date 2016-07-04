package com.tny.game.suite.doors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class VersionUtils {

	public static boolean check(String version) {
		return NumberUtils.isDigits(StringUtils.replace(version, ".", ""));
	}

	public static int[] version2Ints(String version) {
		String[] versions = StringUtils.split(version, ".");
		int[] verInts = new int[versions.length];
		for (int index = 0; index < versions.length; index++) {
			verInts[index] = NumberUtils.toInt(versions[index]);
		}
		return verInts;
	}
}
