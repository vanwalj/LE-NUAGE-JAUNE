package com.napalm.nuage;

import com.google.appengine.api.datastore.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Valentin on 13/12/14.
 */
public class NuageUser {

    private String email;

    private String APIKey;

    private List<String> CORSList = null;

    private Long usageSave;

    public NuageUser(String email, List<String> CORSList, String APIKey, Long usageSave)
    {
        this.CORSList = CORSList;
        this.email = email;
        this.APIKey = APIKey;
        this.usageSave = usageSave;
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
            foundUser = new NuageUser(email, CORSList, (String) user.getProperty("APIKey"), (Long) user.getProperty("usageSave"));
        } catch (EntityNotFoundException e) {
        }
        return foundUser;
    }

    public void store()
    {
        update();
        sendWelcomeMail();
    }

    private void update()
    {
        Entity userEntity = new Entity("NuageUser", this.getEmail());

        userEntity.setProperty("APIKey", this.getAPIKey());
        userEntity.setProperty("usageSave", this.getUsageSave());
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

    private void sendWelcomeMail()
    {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = "Welcome to VATmess " + this.getEmail() + "," + '\r' + '\n' + "Here is your API Key : " + this.getAPIKey() + '\r' + '\n' + "Please keep a safe copy of it.";

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("vatmess@gmail.com", "VATmess"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(this.getEmail(), this.getEmail()));
            msg.setSubject("Your VATmess account has been activated");
            msg.setText(msgBody);
            Transport.send(msg);

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void sendResetAPIKeyMail()
    {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = "As you requested, your API key has been reset." + '\r' + '\n' + "Here is your new API Key : " + this.getAPIKey() + '\r' + '\n' + "Please keep a safe copy of it.";

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("vatmess@gmail.com", "VATmess"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(this.getEmail(), this.getEmail()));
            msg.setSubject("Your VATmess API key has been reset");
            msg.setText(msgBody);
            Transport.send(msg);

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
        addStoredKey(CORS);
        update();
    }

    public void deleteCors(String CORS)
    {
        this.setUsageSave(this.getUsageSave() + getStoredUsageForKey(CORS));
        deleteStoredKey(CORS);
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
        this.setUsageSave(this.getUsageSave() + getStoredUsageForKey(this.getAPIKey()));
        deleteStoredKey(this.getAPIKey());
        this.setAPIKey(new BigInteger(130, new SecureRandom()).toString());
        addStoredKey(this.getAPIKey());
        update();
        sendResetAPIKeyMail();
    }

    public Long getUsageSave() {
        return usageSave;
    }

    public void setUsageSave(Long usageSave) {
        this.usageSave = usageSave;
    }

    public APIUsage getCurrentUsage()
    {
        Long totalCounter = new Long(0);
        totalCounter += this.getUsageSave();
        totalCounter += getStoredUsage();
        return new APIUsage(totalCounter);
    }

    private Long getStoredUsage()
    {
        Long result = new Long(0);
        result += getStoredUsageForKey(this.getAPIKey());
        for (String CORS : this.getCORSList())
        {
            result += getStoredUsageForKey(CORS);
        }
        return result;
    }

    private Long getStoredUsageForKey(String key)
    {
        Long result = new Long(DatastoreManager.getInstance().getJedis().get(key));
        return result;
    }

    private void deleteStoredKey(String key)
    {
        DatastoreManager.getInstance().getJedis().del(key);
    }

    private void addStoredKey(String key)
    {
        DatastoreManager.getInstance().getJedis().set(key, "0");
    }
}
