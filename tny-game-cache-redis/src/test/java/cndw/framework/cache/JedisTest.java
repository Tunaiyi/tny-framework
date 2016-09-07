package cndw.framework.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Pool;

import java.util.stream.IntStream;

public class JedisTest {

    public static void main(String[] args) {
        StringBuffer in = new StringBuffer();
        IntStream.range(0, 1024).forEach(i -> in.append("a"));
        try (Pool<Jedis> dataSource = new JedisPool("127.0.0.1")) {
            try (Jedis jedis = dataSource.getResource()) {
                System.out.println(jedis.del("test"));
                // RBtlRpt:10015704484dec1
                jedis.hset("ltest", "test", in.toString());
                String value = jedis.hget("ltest", "test");
                System.out.println(value);
                System.out.println(value.length());
            } catch (JedisConnectionException e) {
                e.printStackTrace();
            }
        }
    }
}
