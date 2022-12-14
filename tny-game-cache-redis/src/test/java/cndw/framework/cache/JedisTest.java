package cndw.framework.cache;

import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.util.Pool;

import java.net.*;
import java.util.stream.IntStream;

public class JedisTest {

    public static void main(String[] args) {
        try (Pool<Jedis> dataSource = new JedisPool(new URI("127.0.0.1"))) {
            try (Jedis jedis = dataSource.getResource()) {
                System.out.println(jedis.del("stat:100:user_retain"));
                // RBtlRpt:10015704484dec1
                IntStream.range(0, 10).forEach(i -> jedis.hset("test:" + i, "test", i + ""));
                IntStream.range(0, 10).forEach(i -> System.out.println(jedis.hget("test:" + i, "test")));
                System.out.println(jedis.del(jedis.keys("test:*").toArray(new String[0])));
                IntStream.range(0, 10).forEach(i -> System.out.println(jedis.hget("test:" + i, "test")));
            } catch (JedisConnectionException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
