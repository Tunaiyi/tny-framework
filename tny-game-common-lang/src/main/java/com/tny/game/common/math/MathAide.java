package com.tny.game.common.math;

import com.google.common.collect.Range;
import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import static com.tny.game.common.number.NumberAide.*;

public class MathAide {

    public static final Logger LOGGER = LoggerFactory.getLogger(MathAide.class);

    /**
     * 转Int
     *
     * @param number
     * @return
     */
    public static Integer toInt(final Number number) {
        return number.intValue();
    }

    /**
     * 转Long
     *
     * @param number
     * @return
     */
    public static Long toLong(final Number number) {
        return number.longValue();
    }

    /**
     * 转Double
     *
     * @param number
     * @return
     */
    public static Double toDouble(final Number number) {
        return number.doubleValue();
    }

    /**
     * 转Float
     *
     * @param number
     * @return
     */
    public static Float toFloat(final Number number) {
        return number.floatValue();
    }

    /**
     * 转Byte
     *
     * @param number
     * @return
     */
    public static Byte toByte(final Number number) {
        return number.byteValue();
    }

    /**
     * a 的 b 次幂
     *
     * @param a
     * @param b
     * @return
     */
    public static int toPow(int a, int b) {
        return (int) Math.pow(a, b);
    }

    /**
     * 开平方根
     *
     * @param a
     * @return
     */
    public static int toSqrt(int a) {
        return (int) Math.sqrt(a);
    }

    /**
     * 获取 0 到 number - 1 的随机数
     *
     * @param number
     * @return
     */
    public static int rand(final int number) {
        return ThreadLocalRandom.current().nextInt(number);
    }

    /**
     * @return 随机 from 到 to 范围的随机数
     */
    public static int rand(final int from, final int to) {
        if (to < from)
            throw new IllegalArgumentException(StringAide.format("to {} < from {}", to, from));
        return MathAide.rand((to - from) + 1) + from;
    }

    /**
     * 抽签 (权重)
     * 参数: [权重1, 返回值1, 权重2, 返回值2, .... 权重n, 返回值n] 如[1,"a",2,"b"]
     * 在 权重为1的"a" 和 权重为2的"b" 的范围内进行抽签
     * 返回抽中的"a"或"b"
     *
     * @param randomItemList 内容
     * @return
     */
    public static <V> V lot(List<?> randomItemList) {
        List<RandomObject<V>> itemList = new ArrayList<>();
        int number = 0;
        for (int index = 0; index < randomItemList.size(); index = index + 2) {
            Integer value = (Integer) randomItemList.get(index);
            V object = ObjectAide.as(randomItemList.get(index + 1));
            number += value;
            itemList.add(new RandomObject<>(object, number));
        }
        int value = ThreadLocalRandom.current().nextInt(number);
        for (RandomObject<V> item : itemList) {
            if (value < item.getValue())
                return item.getObject();
        }
        return null;
    }

    public static <V> List<V> randObjects(int times, List<RandomObject<V>> items) {
        return randObjects(items.get(items.size() - 1).getValue(), times, items, null);
    }

    public static <V> List<V> randObjects(int times, List<RandomObject<V>> items, V defItem) {
        return randObjects(items.get(items.size() - 1).getValue(), times, items, defItem);
    }

    public static <V> List<V> randObjects(int number, int times, List<RandomObject<V>> items) {
        return randObjects(number, times, items, null);
    }

    public static <V> List<V> randObjects(int number, int times, List<RandomObject<V>> items, V defItem) {
        List<V> list = new ArrayList<>();
        for (int index = 0; index < times; index++) {
            int value = ThreadLocalRandom.current().nextInt(number);
            for (RandomObject<V> item : items) {
                if (value < item.getValue()) {
                    list.add(item.getObject());
                }
            }
            list.add(defItem);
        }
        return list;
    }

    public static <V> V randObject(List<RandomObject<V>> items) {
        return randObject(items.get(items.size() - 1).getValue(), items);
    }

    public static <V> V randObject(List<RandomObject<V>> items, V defItem) {
        return randObject(items.get(items.size() - 1).getValue(), items, defItem);
    }

    public static <V> V randObject(int number, List<RandomObject<V>> items) {
        return randObject(number, items, null);
    }

    public static <V> V randObject(int number, List<RandomObject<V>> items, V defItem) {
        int value = ThreadLocalRandom.current().nextInt(number);
        for (RandomObject<V> item : items) {
            if (value < item.getValue())
                return item.getObject();
        }
        return defItem;
    }

    public static <V> List<RandomObject<V>> weights2RandomItems(List<?> randomItemList) {
        List<RandomObject<V>> itemList = new ArrayList<>();
        int number = 0;
        for (int index = 0; index < randomItemList.size(); index = index + 2) {
            Integer value = (Integer) randomItemList.get(index);
            V object = ObjectAide.as(randomItemList.get(index + 1));
            number += value;
            itemList.add(new RandomObject<>(object, number));
        }
        return itemList;
    }


    /**
     * 随机获取对象
     * 参数: [100:"a", 200:"b"]
     * 抽中"a"的范围为 0-99
     * 抽中"b"的范围为 100-199
     * 返回抽中的"a"或"b" 若无对象抽中返回 null
     *
     * @param number         随机范围
     * @param randomItemList 随机内容
     * @return
     */
    public static <V> V rand(final int number, List<Object> randomItemList) {
        return rand(number, randomItemList, null);
    }

    /**
     * 随机获取对象
     * 参数: [100:"a", 200:"b"]
     * 抽中"a"的范围为 0-99
     * 抽中"b"的范围为 100-199
     * 返回抽中的"a"或"b" 若无对象抽中返回 defaultObject
     *
     * @param number         随机范围
     * @param randomItemList 随机内容
     * @param defaultObject  随机不到对象 返回的默认值
     * @return
     */
    public static <V> V rand(final int number, List<Object> randomItemList, V defaultObject) {
        List<RandomObject<V>> itemList = new ArrayList<>();
        for (int index = 0; index < randomItemList.size(); index = index + 2) {
            Integer value = (Integer) randomItemList.get(index);
            V object = ObjectAide.as(randomItemList.get(index + 1));
            itemList.add(new RandomObject<>(object, value));
        }
        Collections.sort(itemList);
        int value = ThreadLocalRandom.current().nextInt(number);
        for (RandomObject<V> item : itemList) {
            if (value < item.getValue()) {
                return item.getObject();
            }
        }
        return defaultObject;
    }

    /**
     * 随机获取对象(存在Key重复问题)
     * 参数: [100:"a", 200:"b"]
     * 抽中"a"的范围为 0-99
     * 抽中"b"的范围为 100-199
     * 返回抽中的"a"或"b" 若无对象抽中返回 null
     *
     * @param number    随机范围
     * @param randomMap 随机内容
     * @return
     */
    public static <V> V rand(final int number, Map<Integer, V> randomMap) {
        return rand(number, randomMap, null);
    }

    /**
     * 随机获取对象(存在Key重复问题)
     * 参数: [100:"a", 200:"b"]
     * 抽中"a"的范围为 0-99
     * 抽中"b"的范围为 100-199
     * 返回抽中的"a"或"b" 若无对象抽中返回 defaultObject
     *
     * @param number        随机范围
     * @param randomMap     随机内容
     * @param defaultObject 随机不到对象 返回的默认值
     * @return
     */
    public static <V> V rand(final int number, Map<Integer, V> randomMap, V defaultObject) {
        int value = ThreadLocalRandom.current().nextInt(number);
        SortedMap<Integer, V> sortedMap = new TreeMap<>(randomMap);
        for (Entry<Integer, V> entry : sortedMap.entrySet()) {
            if (value < entry.getKey()) {
                return entry.getValue();
            }
        }
        return defaultObject;
    }

    /**
     * 限制time次出num个数量的随机器
     *
     * @param time        监控次数
     * @param num         监控次数出现的num
     * @param prob        随机概率
     * @param currentTime 当前次数
     * @param currentNum  当前出现数量
     * @return 是否出现
     */
    public static boolean randLimited(int time, int num, int prob, int currentTime, int currentNum) {
        return randLimited(time, num, prob, 0, currentTime, currentNum);
    }

    /**
     * 限制time次出num个数量的随机器
     *
     * @param time          监控次数
     * @param num           监控次数出现的num
     * @param timesProbsMap 随机概率列表
     * @param currentTime   当前次数
     * @param currentNum    当前出现数量
     * @return 是否出现
     */
    public static boolean randLimited(int time, int num, Map<Integer, Integer> timesProbsMap, int currentTime, int currentNum) {
        return randLimited(time, num, timesProbsMap, 0, currentTime, currentNum);
    }

    /**
     * 限制time次出num个数量的随机器
     *
     * @param time          监控次数
     * @param num           监控次数出现的num
     * @param timesProbsMap n次的概率列表 {3:30, 9:20, 20:10000} 没伦监控第n次概率
     * @param extra         额外次数 够监控次数后的额外次数
     * @param currentTime   当前次数
     * @param currentNum    当前出现数量
     * @return 是否出现
     */
    public static boolean randLimited(int time, int num, Map<Integer, Integer> timesProbsMap, int extra, int currentTime, int currentNum) {
        int certainly = checkCertainly(time, num, extra, currentTime, currentNum);
        boolean drop;
        if (certainly == 0) {
            int moreTime = currentTime % time;
            Integer prob = null;
            SortedMap<Integer, Integer> sortedMap;
            if (timesProbsMap instanceof SortedMap)
                sortedMap = (SortedMap<Integer, Integer>) timesProbsMap;
            else
                sortedMap = new TreeMap<>(timesProbsMap);
            for (Entry<Integer, Integer> entry : sortedMap.entrySet()) {
                if (moreTime < entry.getKey()) {
                    prob = entry.getValue();
                    break;
                }
            }
            if (prob == null)
                prob = sortedMap.lastKey();
            int randValue = rand(0, 10000);
            drop = randValue < prob;
        } else {
            drop = certainly > 0;
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("随机结果 : " + drop);
        return drop;
    }

    /**
     * 限制time次出num个数量的随机器
     *
     * @param time        监控次数
     * @param num         监控次数出现的num
     * @param extra       额外次数 够监控次数后的额外次数
     * @param currentTime 当前次数
     * @param currentNum  当前出现数量
     * @return 是否出现
     */
    private static int checkCertainly(int time, int num, int extra, int currentTime, int currentNum) {
        int validNum = ((currentTime / time) + 1) * num;
        int moreTime = currentTime % time;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("当前已获取 = {}; 当前次数 ={}; 允许掉落数量 = {}; 周期当前次数 = {};",
                    currentNum, currentTime, validNum, moreTime);
        }
        if (currentNum < validNum) {
            if (moreTime >= (time - num)) {
                return 1;
            }
        } else if (currentNum >= validNum + extra) {
            return -1;
        }
        return 0;
    }

    /**
     * 限制time次出num个数量的随机器
     *
     * @param time        监控次数
     * @param num         监控次数出现的num
     * @param prob        随机概率
     * @param extra       额外次数 够监控次数后的额外次数
     * @param currentTime 当前次数
     * @param currentNum  当前出现数量
     * @return 是否出现
     */
    public static boolean randLimited(int time, int num, int prob, int extra, int currentTime, int currentNum) {
        int certainly = checkCertainly(time, num, extra, currentTime, currentNum);
        boolean drop;
        if (certainly == 0) {
            int randValue = rand(0, 10000);
            drop = randValue < prob;
        } else {
            drop = certainly > 0;
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("随机结果 : " + drop);
        return drop;
    }

    /**
     * 随机字符串
     *
     * @param src
     * @param length
     * @return
     */
    public static String randKey(String src, int length) {
        StringBuilder builder = new StringBuilder(length);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int index = 0; index < length; index++) {
            int srcIndex = random.nextInt(src.length());
            builder.append(src.charAt(srcIndex));
        }
        return builder.toString();
    }

    /**
     * 获取value取值范围的有效数 最大值maxValue, 最小值minValue
     *
     * @param value    取值数
     * @param minValue 最小范围
     * @param maxValue 最大范围
     * @return 返回有效数
     */
    public static int clamp(int value, int minValue, int maxValue) {
        return Math.min(Math.max(minValue, value), maxValue);
    }

    /**
     * 获取value取值范围的有效数 最大值maxValue, 最小值minValue
     *
     * @param value    取值数
     * @param minValue 最小范围
     * @param maxValue 最大范围
     * @return 返回有效数
     */
    public static long clamp(long value, long minValue, long maxValue) {
        return Math.min(Math.max(minValue, value), maxValue);
    }

    /**
     * 获取value取值范围的有效数 最大值maxValue, 最小值minValue
     *
     * @param value    取值数
     * @param minValue 最小范围
     * @param maxValue 最大范围
     * @return 返回有效数
     */
    public static float clamp(float value, float minValue, float maxValue) {
        return Math.min(Math.max(minValue, value), maxValue);
    }

    /**
     * 获取value取值范围的有效数 最大值maxValue, 最小值minValue
     *
     * @param value    取值数
     * @param minValue 最小范围
     * @param maxValue 最大范围
     * @return 返回有效数
     */
    public static double clamp(double value, double minValue, double maxValue) {
        return Math.min(Math.max(minValue, value), maxValue);
    }

    /**
     * 获取value取值范围的有效数 最大值maxValue, 最小值minValue
     *
     * @param value    取值数
     * @param minValue 最小范围
     * @param maxValue 最大范围
     * @return 返回有效数
     */
    public static Number clamp(Number value, Number minValue, Number maxValue) {
        return min(max(value, minValue), maxValue);
    }

    /**
     * 获取value取值范围的有效数 最大值maxValue, 最小值minValue
     *
     * @param range 方位
     * @param value 取值数
     * @return 返回有效数
     */
    public static <T extends Comparable<T>> T clamp(Range<T> range, T value) {
        if (range.contains(value))
            return value;
        return value.compareTo(range.lowerEndpoint()) < 0 ? range.lowerEndpoint() : range.upperEndpoint();
    }

    /**
     * 随机times次数, 有一定概率命中
     *
     * @param prob  概率
     * @param times 做多少次
     * @return 返回命中的次数
     */
    public static int randHitTimes(int prob, int times) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int done = 0;
        for (int time = 0; time < times; time++) {
            if (random.nextInt(10000) < prob)
                done++;
        }
        return done;
    }

}
