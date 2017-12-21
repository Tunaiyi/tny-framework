package com.tny.game.common.utils.version;

import com.tny.game.common.formula.*;
import com.tny.game.common.utils.*;
import org.apache.commons.lang3.*;
import org.apache.commons.lang3.math.*;

import java.util.*;

/**
 * Created by Kun Yang on 2017/12/20.
 */
public class Version implements Comparable<Version> {

    private static final String DEFAULT_SEPARATOR = ".";

    private String fullVersion;

    private String[] subVersions;

    public static Version of(String version) {
        return of(version, DEFAULT_SEPARATOR);
    }

    public static Version of(String version, String separator) {
        return new Version(version, separator);
    }

    private Version(String version, String separator) {
        Throws.checkNotNull(version, "version is null");
        Throws.checkNotNull(separator, "separator is null");
        Throws.checkArgument(StringUtils.isNoneBlank(version), "version mush not blank");
        Throws.checkArgument(StringUtils.isNoneBlank(separator), "separator mush not blank");
        this.fullVersion = version;
        this.subVersions = StringUtils.split(version, separator);
    }

    public String getFullVersion() {
        return fullVersion;
    }

    public String[] getSubVersions() {
        return Arrays.copyOf(subVersions, subVersions.length);
    }

    public String getSubVersion(int index) {
        return this.subVersions[MathEx.clamp(index, 0, subVersions.length - 1)];
    }

    public String[] getSubVersions(int subLength) {
        return Arrays.copyOf(subVersions, Math.min(subLength, subVersions.length - 1));
    }

    public String[] getSubVersions(int from, int to) {
        return Arrays.copyOfRange(subVersions, Math.max(from, 0), Math.min(to, subVersions.length - 1));
    }

    public int getSubVersionsLength() {
        return this.subVersions.length;
    }

    private static final Comparator<String> comparator = (one, other) -> {
        one = format(one);
        other = format(other);
        boolean oneDgt = NumberUtils.isDigits(one);
        boolean otherDgt = NumberUtils.isDigits(other);
        if (oneDgt != otherDgt)
            return oneDgt ? 1 : -1;
        int lengthCompare = one.length() - other.length();
        if (lengthCompare != 0)
            return lengthCompare;
        return one.compareTo(other);
    };


    @Override
    public int compareTo(Version other) {
        int length = Math.min(this.subVersions.length, other.subVersions.length);
        for (int index = 0; index < length; index++) {
            String thisSubVer = this.subVersions[index];
            String otherSubVer = other.subVersions[index];
            int subCpResult = comparator.compare(thisSubVer, otherSubVer);
            if (subCpResult != 0)
                return subCpResult;
        }
        return this.subVersions.length - other.subVersions.length;
    }

    private static String format(String value) {
        for (int index = 0; index < value.length(); index++) {
            char c = value.charAt(index);
            if (c != '0') {
                if (index == 0)
                    return value;
                else
                    return value.substring(index);
            }
        }
        return "0";
    }

}
