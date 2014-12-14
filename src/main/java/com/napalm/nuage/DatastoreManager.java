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

    private DatastoreManager() {
        this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public DatastoreService getDatastore()
    {
        return datastore;
    }
}
