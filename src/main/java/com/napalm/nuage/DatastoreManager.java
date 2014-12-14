package com.napalm.nuage;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import redis.clients.jedis.Jedis;

/**
 * Created by Valentin on 13/12/14.
 */
public class DatastoreManager {
    private static DatastoreManager ourInstance = new DatastoreManager();

    public static DatastoreManager getInstance() {
        return ourInstance;
    }

    private DatastoreService datastore = null;
    private Jedis jedis = null;

    private DatastoreManager() {
        this.datastore = DatastoreServiceFactory.getDatastoreService();
        this.jedis = new Jedis("130.211.109.210");
    }

    public DatastoreService getDatastore()
    {
        return datastore;
    }

    public Jedis getJedis() {
        return jedis;
    }

    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }
}
