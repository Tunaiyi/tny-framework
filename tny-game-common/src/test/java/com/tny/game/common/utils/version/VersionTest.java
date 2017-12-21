package com.tny.game.common.utils.version;

import org.apache.commons.lang3.*;
import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by Kun Yang on 2017/12/20.
 */
public class VersionTest {

    private static final String VERSION_WORD = "10.10.1.2";

    @Test
    public void of() {
        Version.of(VERSION_WORD);
        Version.of(VERSION_WORD, ".");
    }

    @Test(expected = NullPointerException.class)
    public void of1Null() {
        Version.of(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void of1Blank() {
        Version.of("  ");
    }

    @Test(expected = NullPointerException.class)
    public void of2Null() {
        Version.of("null", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void of2Blank() {
        Version.of("null", "   ");
    }

    @Test
    public void getFullVersion() {
        Version version = Version.of(VERSION_WORD);
        assertEquals(version.getFullVersion(), VERSION_WORD);
    }

    @Test
    public void getSubVersions() {
        Version version = Version.of(VERSION_WORD);
        assertArrayEquals(version.getSubVersions(), StringUtils.split(VERSION_WORD, "."));
    }

    @Test
    public void getSubVersion() {
        Version version = Version.of(VERSION_WORD);
        String[] vers = StringUtils.split(VERSION_WORD, ".");
        assertEquals(version.getSubVersion(-1), vers[0]);
        assertEquals(version.getSubVersion(0), vers[0]);
        assertEquals(version.getSubVersion(1), vers[1]);
        assertEquals(version.getSubVersion(2), vers[2]);
        assertEquals(version.getSubVersion(3), vers[3]);
        assertEquals(version.getSubVersion(4), vers[3]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSubVersions1() {
        Version version = Version.of(VERSION_WORD);
        String[] vers = StringUtils.split(VERSION_WORD, ".");
        assertArrayEquals(version.getSubVersions(-1, 2), Arrays.copyOfRange(vers, 0, 2));
        assertArrayEquals(version.getSubVersions(0, 2), Arrays.copyOfRange(vers, 0, 2));
        assertArrayEquals(version.getSubVersions(1, 2), Arrays.copyOfRange(vers, 1, 2));
        assertArrayEquals(version.getSubVersions(2, 2), Arrays.copyOfRange(vers, 2, 2));
        assertArrayEquals(version.getSubVersions(2, 3), Arrays.copyOfRange(vers, 2, 3));
        assertArrayEquals(version.getSubVersions(3, 3), Arrays.copyOfRange(vers, 3, 3));
        assertArrayEquals(version.getSubVersions(4, 3), Arrays.copyOfRange(vers, 3, 3));
    }

    @Test
    public void getSubVersionLength() {
        Version version = Version.of(VERSION_WORD);
        String[] vers = StringUtils.split(VERSION_WORD, ".");
        assertEquals(version.getSubVersionsLength(), vers.length);
        version = Version.of("eee");
        assertEquals(version.getSubVersionsLength(), 1);
        version = Version.of("eee.222");
        assertEquals(version.getSubVersionsLength(), 2);
    }

    @Test
    public void compareTo() {
        Version value = Version.of("10.10.10");
        assertEquals(value.compareTo(Version.of("10.10.10")), 0);
        assertEquals(value.compareTo(Version.of("9.10.10")), 1);
        assertEquals(value.compareTo(Version.of("10.9.10")), 1);
        assertEquals(value.compareTo(Version.of("10.10.9")), 1);
        assertEquals(value.compareTo(Version.of("a.10.10")), 1);
        assertEquals(value.compareTo(Version.of("10.a.10")), 1);
        assertEquals(value.compareTo(Version.of("10.10.a")), 1);
        assertEquals(value.compareTo(Version.of("11.10.10")), -1);
        assertEquals(value.compareTo(Version.of("10.11.10")), -1);
        assertEquals(value.compareTo(Version.of("10.10.11")), -1);
        assertEquals(value.compareTo(Version.of("10.10.10.1")), -1);
        assertEquals(value.compareTo(Version.of("10.10.10.a")), -1);
        assertEquals(Version.of("10.10.10").compareTo(value), 0);
        assertEquals(Version.of("9.10.10").compareTo(value), -1);
        assertEquals(Version.of("10.9.10").compareTo(value), -1);
        assertEquals(Version.of("10.10.9").compareTo(value), -1);
        assertEquals(Version.of("a.10.10").compareTo(value), -1);
        assertEquals(Version.of("10.a.10").compareTo(value), -1);
        assertEquals(Version.of("10.10.a").compareTo(value), -1);
        assertEquals(Version.of("11.10.10").compareTo(value), 1);
        assertEquals(Version.of("10.11.10").compareTo(value), 1);
        assertEquals(Version.of("10.10.11").compareTo(value), 1);
        assertEquals(Version.of("10.10.10.1").compareTo(value), 1);
        assertEquals(Version.of("10.10.10.a").compareTo(value), 1);
    }
}