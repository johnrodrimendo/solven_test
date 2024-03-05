package com.affirm.test;

import com.affirm.client.model.LoggedUserEmployer;
import com.google.gson.Gson;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.io.Serializable;
import java.net.URI;

public class RedisTest {

    public static void main(String[] args) throws Exception {
        RedisTest test = new RedisTest();
        Session session = test.doReadSession("b25092d6-4d90-4c88-a449-2542adea1dce");

        if (session != null) {
            SimplePrincipalCollection simplePrincipalCollection = (SimplePrincipalCollection) session.getAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY");
            if (simplePrincipalCollection != null) {
                LoggedUserEmployer loggedUserEmployer = (LoggedUserEmployer) simplePrincipalCollection.getPrimaryPrincipal();
                if (loggedUserEmployer != null) {
                    System.out.println("" + new Gson().toJson(loggedUserEmployer));
                    System.out.println("last access: " + session.getLastAccessTime());
                    System.out.println("Active company: " + loggedUserEmployer.getActiveCompany().getId() + " - " + loggedUserEmployer.getActiveCompany().getName());
                } else
                    System.out.println("No hay logged user");
            } else
                System.out.println("No hay simpleprincipal");
        } else
            System.out.println("No hay sesion");
    }


    JedisPool pool;

    {
        //RedisCloud
        try {
            URI redisUri = new URI(System.getenv("REDIS_CLOUD_URL"));

            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(1);
            pool = new JedisPool(poolConfig,
                    redisUri.getHost(),
                    redisUri.getPort(),
                    Protocol.DEFAULT_TIMEOUT,
                    redisUri.getUserInfo().split(":", 2)[1]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //AMAZON
        //JedisPool pool = new JedisPool(new JedisPoolConfig(), "35.162.234.94", 6379, 5000);//AMAZON
    }


    protected Session doReadSession(Serializable sessionid) {
        try {
            long time = System.nanoTime();
            byte[] object = readFromRedis(SerializationUtils.serialize(sessionid));
            System.out.println("SESSION DAO READ -> " + ((System.nanoTime() - time) / 1000000));
            if (object == null) {
                return null;
            }
            return (Session) SerializationUtils.deserialize(object);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private byte[] readFromRedis(byte[] key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.get(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
