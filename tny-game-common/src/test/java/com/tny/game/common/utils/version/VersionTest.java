package com.tny.game.common.utils.version;

import org.apache.commons.lang3.*;
import org.junit.*;

import java.util.*;

import static com.tny.game.common.utils.version.Version.*;
import static org.junit.Assert.*;

/**
 * Created by Kun Yang on 2017/12/20.
 */
public class VersionTest {

    private static final String VERSION_WORD = "10.10.1.2";

    @Test
    public void testOf() {
        of(VERSION_WORD);
        of(VERSION_WORD, ".");
    }

    @Test(expected = NullPointerException.class)
    public void of1Null() {
        of(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void of1Blank() {
        of("  ");
    }

    @Test(expected = NullPointerException.class)
    public void of2Null() {
        of("null", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void of2Blank() {
        of("null", "   ");
    }

    @Test
    public void getFullVersion() {
        Version version = of(VERSION_WORD);
        assertEquals(version.getFullVersion(), VERSION_WORD);
    }

    @Test
    public void getSubVersions() {
        Version version = of(VERSION_WORD);
        assertArrayEquals(version.getSubVersions(), StringUtils.split(VERSION_WORD, "."));
    }

    @Test
    public void getSubVersion() {
        Version version = of(VERSION_WORD);
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
        Version version = of(VERSION_WORD);
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
        Version version = of(VERSION_WORD);
        String[] vers = StringUtils.split(VERSION_WORD, ".");
        assertEquals(version.getSubVersionsLength(), vers.length);
        version = of("eee");
        assertEquals(version.getSubVersionsLength(), 1);
        version = of("eee.222");
        assertEquals(version.getSubVersionsLength(), 2);
    }

    @Test
    public void compareTo() {
        Version value = of("10.10.10");
        assertEquals(value.compareTo(of("10.10.10")), 0);
        assertEquals(value.compareTo(of("9.10.10")), 1);
        assertEquals(value.compareTo(of("10.9.10")), 1);
        assertEquals(value.compareTo(of("10.10.9")), 1);
        assertEquals(value.compareTo(of("a.10.10")), 1);
        assertEquals(value.compareTo(of("10.a.10")), 1);
        assertEquals(value.compareTo(of("10.10.a")), 1);
        assertEquals(value.compareTo(of("11.10.10")), -1);
        assertEquals(value.compareTo(of("10.11.10")), -1);
        assertEquals(value.compareTo(of("10.10.11")), -1);
        assertEquals(value.compareTo(of("10.10.10.1")), -1);
        assertEquals(value.compareTo(of("10.10.10.a")), -1);
        assertEquals(of("10.10.10").compareTo(value), 0);
        assertEquals(of("9.10.10").compareTo(value), -1);
        assertEquals(of("10.9.10").compareTo(value), -1);
        assertEquals(of("10.10.9").compareTo(value), -1);
        assertEquals(of("a.10.10").compareTo(value), -1);
        assertEquals(of("10.a.10").compareTo(value), -1);
        assertEquals(of("10.10.a").compareTo(value), -1);
        assertEquals(of("11.10.10").compareTo(value), 1);
        assertEquals(of("10.11.10").compareTo(value), 1);
        assertEquals(of("10.10.11").compareTo(value), 1);
        assertEquals(of("10.10.10.1").compareTo(value), 1);
        assertEquals(of("10.10.10.a").compareTo(value), 1);
    }


    @Test
    public void greaterThan() {
        Version value = of("10.10.10");
        assertFalse(value.greaterThan(of("10.10.10")));
        assertTrue(value.greaterThan(of("9.10.10")));
        assertTrue(value.greaterThan(of("10.9.10")));
        assertTrue(value.greaterThan(of("10.10.9")));
        assertTrue(value.greaterThan(of("a.10.10")));
        assertTrue(value.greaterThan(of("10.a.10")));
        assertTrue(value.greaterThan(of("10.10.a")));
        assertFalse(value.greaterThan(of("11.10.10")));
        assertFalse(value.greaterThan(of("10.11.10")));
        assertFalse(value.greaterThan(of("10.10.11")));
        assertFalse(value.greaterThan(of("10.10.10.1")));
        assertFalse(value.greaterThan(of("10.10.10.a")));
        assertFalse(of("10.10.10").greaterThan(value));
        assertFalse(of("9.10.10").greaterThan(value));
        assertFalse(of("10.9.10").greaterThan(value));
        assertFalse(of("10.10.9").greaterThan(value));
        assertFalse(of("a.10.10").greaterThan(value));
        assertFalse(of("10.a.10").greaterThan(value));
        assertFalse(of("10.10.a").greaterThan(value));
        assertTrue(of("11.10.10").greaterThan(value));
        assertTrue(of("10.11.10").greaterThan(value));
        assertTrue(of("10.10.11").greaterThan(value));
        assertTrue(of("10.10.10.1").greaterThan(value));
        assertTrue(of("10.10.10.a").greaterThan(value));
    }

    @Test
    public void greaterEqualsThan() {
        Version value = of("10.10.10");
        assertTrue(value.greaterEqualsThan(of("10.10.10")));
        assertTrue(value.greaterEqualsThan(of("9.10.10")));
        assertTrue(value.greaterEqualsThan(of("10.9.10")));
        assertTrue(value.greaterEqualsThan(of("10.10.9")));
        assertTrue(value.greaterEqualsThan(of("a.10.10")));
        assertTrue(value.greaterEqualsThan(of("10.a.10")));
        assertTrue(value.greaterEqualsThan(of("10.10.a")));
        assertFalse(value.greaterEqualsThan(of("11.10.10")));
        assertFalse(value.greaterEqualsThan(of("10.11.10")));
        assertFalse(value.greaterEqualsThan(of("10.10.11")));
        assertFalse(value.greaterEqualsThan(of("10.10.10.1")));
        assertFalse(value.greaterEqualsThan(of("10.10.10.a")));
        assertTrue(of("10.10.10").greaterEqualsThan(value));
        assertFalse(of("9.10.10").greaterEqualsThan(value));
        assertFalse(of("10.9.10").greaterEqualsThan(value));
        assertFalse(of("10.10.9").greaterEqualsThan(value));
        assertFalse(of("a.10.10").greaterEqualsThan(value));
        assertFalse(of("10.a.10").greaterEqualsThan(value));
        assertFalse(of("10.10.a").greaterEqualsThan(value));
        assertTrue(of("11.10.10").greaterEqualsThan(value));
        assertTrue(of("10.11.10").greaterEqualsThan(value));
        assertTrue(of("10.10.11").greaterEqualsThan(value));
        assertTrue(of("10.10.10.1").greaterEqualsThan(value));
        assertTrue(of("10.10.10.a").greaterEqualsThan(value));
    }

    @Test
    public void lessThan() {
        Version value = of("10.10.10");
        assertFalse(value.lessThan(of("10.10.10")));
        assertFalse(value.lessThan(of("9.10.10")));
        assertFalse(value.lessThan(of("10.9.10")));
        assertFalse(value.lessThan(of("10.10.9")));
        assertFalse(value.lessThan(of("a.10.10")));
        assertFalse(value.lessThan(of("10.a.10")));
        assertFalse(value.lessThan(of("10.10.a")));
        assertTrue(value.lessThan(of("11.10.10")));
        assertTrue(value.lessThan(of("10.11.10")));
        assertTrue(value.lessThan(of("10.10.11")));
        assertTrue(value.lessThan(of("10.10.10.1")));
        assertTrue(value.lessThan(of("10.10.10.a")));
        assertFalse(of("10.10.10").lessThan(value));
        assertTrue(of("9.10.10").lessThan(value));
        assertTrue(of("10.9.10").lessThan(value));
        assertTrue(of("10.10.9").lessThan(value));
        assertTrue(of("a.10.10").lessThan(value));
        assertTrue(of("10.a.10").lessThan(value));
        assertTrue(of("10.10.a").lessThan(value));
        assertFalse(of("11.10.10").lessThan(value));
        assertFalse(of("10.11.10").lessThan(value));
        assertFalse(of("10.10.11").lessThan(value));
        assertFalse(of("10.10.10.1").lessThan(value));
        assertFalse(of("10.10.10.a").lessThan(value));
    }

    @Test
    public void lessEqualsThan() {

        Version value = of("10.10.10");
        assertTrue(value.lessEqualsThan(of("10.10.10")));
        assertFalse(value.lessEqualsThan(of("9.10.10")));
        assertFalse(value.lessEqualsThan(of("10.9.10")));
        assertFalse(value.lessEqualsThan(of("10.10.9")));
        assertFalse(value.lessEqualsThan(of("a.10.10")));
        assertFalse(value.lessEqualsThan(of("10.a.10")));
        assertFalse(value.lessEqualsThan(of("10.10.a")));
        assertTrue(value.lessEqualsThan(of("11.10.10")));
        assertTrue(value.lessEqualsThan(of("10.11.10")));
        assertTrue(value.lessEqualsThan(of("10.10.11")));
        assertTrue(value.lessEqualsThan(of("10.10.10.1")));
        assertTrue(value.lessEqualsThan(of("10.10.10.a")));
        assertTrue(of("10.10.10").lessEqualsThan(value));
        assertTrue(of("9.10.10").lessEqualsThan(value));
        assertTrue(of("10.9.10").lessEqualsThan(value));
        assertTrue(of("10.10.9").lessEqualsThan(value));
        assertTrue(of("a.10.10").lessEqualsThan(value));
        assertTrue(of("10.a.10").lessEqualsThan(value));
        assertTrue(of("10.10.a").lessEqualsThan(value));
        assertFalse(of("11.10.10").lessEqualsThan(value));
        assertFalse(of("10.11.10").lessEqualsThan(value));
        assertFalse(of("10.10.11").lessEqualsThan(value));
        assertFalse(of("10.10.10.1").lessEqualsThan(value));
        assertFalse(of("10.10.10.a").lessEqualsThan(value));

    }

    @Test
    public void equals() {

        Version value = of("10.10.10");
        assertTrue(value.equals(of("10.10.10")));
        assertFalse(value.equals(of("9.10.10")));
        assertFalse(value.equals(of("10.9.10")));
        assertFalse(value.equals(of("10.10.9")));
        assertFalse(value.equals(of("a.10.10")));
        assertFalse(value.equals(of("10.a.10")));
        assertFalse(value.equals(of("10.10.a")));
        assertFalse(value.equals(of("11.10.10")));
        assertFalse(value.equals(of("10.11.10")));
        assertFalse(value.equals(of("10.10.11")));
        assertFalse(value.equals(of("10.10.10.1")));
        assertFalse(value.equals(of("10.10.10.a")));
        assertTrue(of("10.10.10").equals(value));
        assertFalse(of("9.10.10").equals(value));
        assertFalse(of("10.9.10").equals(value));
        assertFalse(of("10.10.9").equals(value));
        assertFalse(of("a.10.10").equals(value));
        assertFalse(of("10.a.10").equals(value));
        assertFalse(of("10.10.a").equals(value));
        assertFalse(of("11.10.10").equals(value));
        assertFalse(of("10.11.10").equals(value));
        assertFalse(of("10.10.11").equals(value));
        assertFalse(of("10.10.10.1").equals(value));
        assertFalse(of("10.10.10.a").equals(value));
    }
}