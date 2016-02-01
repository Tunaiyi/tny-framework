package cndw.framework.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Pool;

public class JedisTest {

    public static void main(String[] args) {
        try (Pool<Jedis> dataSource = new JedisPool("127.0.0.1")) {
            try (Jedis jedis = dataSource.getResource()) {
                System.out.println(jedis.del("test"));
                jedis.set("test", "test");
                System.out.println(jedis.del("test"));
                while (true) {
                    System.out.println(jedis.del("test"));
                    System.out.println(jedis.set("test".getBytes(), "value1".getBytes(), "NX".getBytes(), "PX".getBytes(), 1));
                    Thread.sleep(1000);
                }
            } catch (JedisConnectionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
