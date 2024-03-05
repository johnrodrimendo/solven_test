package com.affirm.common.util;

import org.crazycake.shiro.BaseRedisManager;
import org.crazycake.shiro.IRedisManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;

public class CustomRedisManager extends BaseRedisManager implements IRedisManager {

    private String host;
    private int port = 6379;
    private int timeout = 2000;
    private String password;
    private int database = 0;
    private JedisPool jedisPool;

    private void init() {
        synchronized (this) {
            if (this.jedisPool == null) {
                this.jedisPool = new JedisPool(this.getJedisPoolConfig(), this.host, this.port, this.timeout, this.password, this.database);
            }
        }
    }

    protected Jedis getJedis() {
        if (this.jedisPool == null) {
            this.init();
        }

        return this.jedisPool.getResource();
    }

    public byte[] get(byte[] key) {
        long startTime = new Date().getTime();
        byte[] result = super.get(key);
//        System.out.println("REDIS GET [" + new String(key) + "] -> " + (new Date().getTime() - startTime));
        return result;
    }

    public byte[] set(byte[] key, byte[] value, int exipreTime) {
        long startTime = new Date().getTime();
        byte[] result = super.set(key, value, exipreTime);
//        System.out.println("REDIS SET [" + new String(key) + "] -> " + (new Date().getTime() - startTime));
        return result;
    }


    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
