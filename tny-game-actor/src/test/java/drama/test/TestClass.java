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

package drama.test;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TestClass {

    private static class Person {

        private int id;

        public Person(int id) {
            this.id = id;
        }

        public boolean isDouble() {
            return this.id % 2 == 0;
        }

        @Override
        public String toString() {
            return "Person{" +
                   "id=" + id +
                   '}';
        }

    }

    @Test
    public void testLambda() {
        List<Person> persons = Lists.newArrayList(
                new Person(1),
                new Person(2),
                new Person(3),
                new Person(4),
                new Person(5),
                new Person(6),
                new Person(7)
        );
        List<Person> newPersons = persons.stream()
                .filter(Person::isDouble)
                .collect(Collectors.toList());
        System.out.println(persons);
        System.out.println(newPersons);
    }

    public static String ELEMENT_REGEX_STR = "(?:[-\\w:@&=+,.!~*'_;]|%\\p{XDigit}{2})(?:[-\\w:@&=+,.!~*'$_;]|%\\p{XDigit}{2})*";

    public static Pattern ELEMENT_REGEX = Pattern.compile(ELEMENT_REGEX_STR);

    private boolean match(String value) {
        System.out.print(value + " = ");
        return ELEMENT_REGEX.matcher(value).matches();
    }

    @Test
    public void testRegPath() {
        System.out.println(match("abc"));
        System.out.println(match("hh://ddddss"));
        System.out.println(match("/ddddss"));
        System.out.println(match(":ddd+-=dss"));
        System.out.println(match("http://example.org/absolute/URI/with/absolute/path/to/resource.txt"));
        System.out.println(match("ftp://example.org/resource.txt"));
        System.out.println(match("urn:issn:1535-3613"));
        System.out.println(match("../abc"));
        System.out.println(match("./abc"));
    }

    @Test
    public void testDuration() {
        Duration duration = Duration.of(1200, ChronoUnit.MILLIS);
        System.out.println(duration.toMillis());
        System.out.println(duration.getSeconds());
    }

}


