/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.version;

import com.tny.game.common.math.*;
import com.tny.game.common.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

/**
 * Created by Kun Yang on 2017/12/20.
 */
public class Version implements Comparable<Version> {

    private static final String DEFAULT_SEPARATOR = ".";

    private final String fullVersion;

    private final String[] subVersions;

    public static Version of(String version) {
        return of(version, DEFAULT_SEPARATOR);
    }

    public static Version of(String version, String separator) {
        return new Version(version, separator);
    }

    private Version(String version, String separator) {
        Asserts.checkNotNull(version, "version is null");
        Asserts.checkNotNull(separator, "separator is null");
        Asserts.checkArgument(StringUtils.isNoneBlank(version), "version mush not blank");
        Asserts.checkArgument(StringUtils.isNoneBlank(separator), "separator mush not blank");
        this.fullVersion = version;
        this.subVersions = StringUtils.split(version, separator);
    }

    public String getFullVersion() {
        return this.fullVersion;
    }

    public String[] getSubVersions() {
        return Arrays.copyOf(this.subVersions, this.subVersions.length);
    }

    public String getSubVersion(int index) {
        return this.subVersions[MathAide.clamp(index, 0, this.subVersions.length - 1)];
    }

    public String[] getSubVersions(int subLength) {
        return Arrays.copyOf(this.subVersions, Math.min(subLength, this.subVersions.length - 1));
    }

    public String[] getSubVersions(int from, int to) {
        return Arrays.copyOfRange(this.subVersions, Math.max(from, 0), Math.min(to, this.subVersions.length - 1));
    }

    public int getSubVersionsLength() {
        return this.subVersions.length;
    }

    public boolean greaterThan(Version version) {
        return this.compareTo(version) > 0;
    }

    public boolean greaterEqualsThan(Version version) {
        return this.compareTo(version) >= 0;
    }

    public boolean lessThan(Version version) {
        return this.compareTo(version) < 0;
    }

    public boolean lessEqualsThan(Version version) {
        return this.compareTo(version) <= 0;
    }

    private static final Comparator<String> comparator = (one, other) -> {
        one = format(one);
        other = format(other);
        boolean oneDgt = NumberUtils.isDigits(one);
        boolean otherDgt = NumberUtils.isDigits(other);
        if (oneDgt != otherDgt) {
            return oneDgt ? 1 : -1;
        }
        int lengthCompare = one.length() - other.length();
        if (lengthCompare != 0) {
            return lengthCompare;
        }
        return one.compareTo(other);
    };

    @Override
    public int compareTo(Version other) {
        if (this.fullVersion.equals(other.getFullVersion())) {
            return 0;
        }
        int length = Math.min(this.subVersions.length, other.subVersions.length);
        for (int index = 0; index < length; index++) {
            String thisSubVer = this.subVersions[index];
            String otherSubVer = other.subVersions[index];
            int subCpResult = comparator.compare(thisSubVer, otherSubVer);
            if (subCpResult != 0) {
                return subCpResult;
            }
        }
        return this.subVersions.length - other.subVersions.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Version)) {
            return false;
        }
        Version version = (Version) o;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(getSubVersions(), version.getSubVersions());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getSubVersions());
    }

    private static String format(String value) {
        for (int index = 0; index < value.length(); index++) {
            char c = value.charAt(index);
            if (c != '0') {
                if (index == 0) {
                    return value;
                } else {
                    return value.substring(index);
                }
            }
        }
        return "0";
    }

    @Override
    public String toString() {
        return "Version{" + "fullVersion='" + this.fullVersion + '\'' + '}';
    }

}
