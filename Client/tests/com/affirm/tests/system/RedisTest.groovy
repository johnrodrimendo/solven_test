package com.affirm.tests.system

import groovy.transform.CompileStatic
import org.apache.commons.lang3.SerializationUtils
import org.apache.shiro.session.Session
import org.junit.jupiter.api.Test
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.Protocol

@CompileStatic
class RedisTest {

    @Test
    void getRedisSession() throws Exception {
        URI redisUri = new URI("redis://redis-dev-solven-entity-9297331:NpPK9dPbmVjy63yJP9IQTDapH711g0wZ@redis-14438.c85.us-east-1-2.ec2.cloud.redislabs.com:14438");
        String key = "shiro:session:8c17223d-85b6-4b4e-8a86-7e94add39b9f";

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(25);
        JedisPool pool = new JedisPool(poolConfig,
                redisUri.getHost(),
                redisUri.getPort(),
                Protocol.DEFAULT_TIMEOUT,
                redisUri.getUserInfo().split(":", 2)[1]);
        Jedis jedis = null;
        try {
            jedis = pool.getResource()
            byte[] object = jedis.get(key.getBytes("UTF-8"));
            if (object != null) {
                Session session = (Session) SerializationUtils.deserialize(object);
                System.out.println(session);
            }
            jedis.close()
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
