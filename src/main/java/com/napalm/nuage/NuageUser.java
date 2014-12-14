package com.napalm.nuage;

import com.google.appengine.api.datastore.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valentin on 13/12/14.
 */
public class NuageUser {

    private String email;

    private String APIKey;

    private List<String> CORSList = null;

    public NuageUser(String email, List<String> CORSList, String APIKey)
    {
        this.CORSList = CORSList;
        this.email = email;
        this.APIKey = APIKey;
    }

    public String getEmail()
    {
        return this.email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public static NuageUser getNuageUserWithEmail(String email) {
        NuageUser foundUser = null;
        DatastoreService datastore = DatastoreManager.getInstance().getDatastore();
        Entity user = null;
        try {
            user = datastore.get(KeyFactory.createKey("NuageUser", email));
            Query CORSQuery = new Query("CORS").setAncestor(user.getKey());
            List<String> CORSList = new ArrayList<>();
            for (Entity CORS : datastore.prepare(CORSQuery).asList(FetchOptions.Builder.withDefaults()))
            {
                CORSList.add(CORS.getKey().getName());
            }
            foundUser = new NuageUser(email, CORSList, (String) user.getProperty("APIKey"));
        } catch (EntityNotFoundException e) {
        }
        return foundUser;
    }

    public void store()
    {
        Entity userEntity = new Entity("NuageUser", this.getEmail());

        userEntity.setProperty("APIKey", this.getAPIKey());
        DatastoreService datastore = DatastoreManager.getInstance().getDatastore();
        datastore.put(userEntity);

        List<Entity> CORSEntitiesList = new ArrayList<>();
        for (String CORS : this.CORSList)
        {
            Entity CORSEntity = new Entity("CORS", CORS, userEntity.getKey());
            CORSEntitiesList.add(CORSEntity);
        }
        datastore.put(CORSEntitiesList);
    }

    public String getAPIKey() {
        return APIKey;
    }

    public void setAPIKey(String APIKey) {
        this.APIKey = APIKey;
    }

    public List<String> getCORSList() {
        return CORSList;
    }

    public void addCors(String CORS)
    {
        this.CORSList.add(CORS);
        store();
    }

    public void deleteCors(String CORS)
    {
        this.CORSList.remove(CORS);
        DatastoreService datastore = DatastoreManager.getInstance().getDatastore();
        try {
            Entity user = datastore.get(KeyFactory.createKey("NuageUser", email));
            datastore.delete(KeyFactory.createKey(user.getKey(), "CORS", CORS));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setCORSList(List<String> CORSList) {
        this.CORSList = CORSList;
    }

    public void resetAPIKey()
    {

    }
}
