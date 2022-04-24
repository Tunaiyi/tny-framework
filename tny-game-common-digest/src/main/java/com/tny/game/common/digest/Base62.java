package com.tny.game.common.digest;

import java.util.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-21 13:40
 */
public class Base62 {

    private static final String CODES =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final char CODEFLAG = '9';

    private static StringBuilder out = new StringBuilder();

    private static Map<Character, Integer> CODE_MAP = new HashMap<>();

    private static void Append(int b) {
        if (b < 61) {
            out.append(CODES.charAt(b));
        } else {

            out.append(CODEFLAG);

            out.append(CODES.charAt(b - 61));
        }
    }

    public static String base62Encode(byte[] in) {

        // Reset output StringBuilder
        out.setLength(0);

        //
        int b;

        // Loop with 3 bytes as a group
        for (int i = 0; i < in.length; i += 3) {

            // #1 char
            b = (in[i] & 0xFC) >> 2;
            Append(b);

            b = (in[i] & 0x03) << 4;
            if (i + 1 < in.length) {

                // #2 char
                b |= (in[i + 1] & 0xF0) >> 4;
                Append(b);

                b = (in[i + 1] & 0x0F) << 2;
                if (i + 2 < in.length) {

                    // #3 char
                    b |= (in[i + 2] & 0xC0) >> 6;
                    Append(b);

                    // #4 char
                    b = in[i + 2] & 0x3F;
                    Append(b);

                } else {
                    // #3 char, last char
                    Append(b);
                }
            } else {
                // #2 char, last char
                Append(b);
            }
        }

        return out.toString();
    }

    public static byte[] base62Decode(char[] inChars) {

        // Map for special code followed by CODEFLAG '9' and its code index
        CODE_MAP.put('A', 61);
        CODE_MAP.put('B', 62);
        CODE_MAP.put('C', 63);

        ArrayList<Byte> decodedList = new ArrayList<>();

        // 6 bits bytes
        int[] unit = new int[4];

        int inputLen = inChars.length;

        // char counter
        int n = 0;

        // unit counter
        int m = 0;

        // regular char
        char ch1 = 0;

        // special char
        char ch2 = 0;

        Byte b = 0;

        while (n < inputLen) {
            ch1 = inChars[n];
            if (ch1 != CODEFLAG) {
                // regular code
                unit[m] = CODES.indexOf(ch1);
                m++;
                n++;
            } else {
                n++;
                if (n < inputLen) {
                    ch2 = inChars[n];
                    if (ch2 != CODEFLAG) {
                        // special code index 61, 62, 63
                        unit[m] = CODE_MAP.get(ch2);
                        m++;
                        n++;
                    }
                }
            }

            // Add regular bytes with 3 bytes group composed from 4 units with 6 bits.
            if (m == 4) {
                b = (byte)((unit[0] << 2) | (unit[1] >> 4));
                decodedList.add(b);
                b = (byte)((unit[1] << 4) | (unit[2] >> 2));
                decodedList.add(b);
                b = (byte)((unit[2] << 6) | unit[3]);
                decodedList.add(b);

                // Reset unit counter
                m = 0;
            }
        }

        // Add tail bytes group less than 4 units
        if (m != 0) {
            if (m == 1) {
                b = (byte)((unit[0] << 2));
                decodedList.add(b);
            } else if (m == 2) {
                b = (byte)((unit[0] << 2) | (unit[1] >> 4));
                decodedList.add(b);
            } else if (m == 3) {
                b = (byte)((unit[0] << 2) | (unit[1] >> 4));
                decodedList.add(b);
                b = (byte)((unit[1] << 4) | (unit[2] >> 2));
                decodedList.add(b);
            }
        }

        Byte[] decodedObj = decodedList.toArray(new Byte[0]);
        byte[] decoded = new byte[decodedObj.length];

        // Convert object Byte array to primitive byte array
        for (int i = 0; i < decodedObj.length; i++) {
            decoded[i] = decodedObj[i];

        }
        return decoded;
    }

}
