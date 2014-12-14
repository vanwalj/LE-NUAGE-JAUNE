package com.napalm.nuage;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.appengine.api.users.User;
import redis.clients.jedis.Jedis;

import javax.inject.Named;
import javax.xml.transform.Source;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


/**
 * Add your first API methods in this class, or you may create another class. In that case, please
 * update your web.xml accordingly.
 */
@Api(
        name = "nuageAPI",
        version = "v1",
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constants.WEB_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID}
)
public class NuageAPI {

    @ApiMethod(name = "helloWorld", path = "helloworld", httpMethod = ApiMethod.HttpMethod.GET)
    public HelloWorld helloWorld()
    {
        return new HelloWorld("hello world");
    }

    @ApiMethod(name = "helloWorld2", path = "helloworld2", httpMethod = ApiMethod.HttpMethod.GET)
    public HelloWorld helloWorld2(User user) throws ForbiddenException {
        return new HelloWorld("hello world " + user.getEmail());
    }

    @ApiMethod(name = "signUp", path = "signup", httpMethod = ApiMethod.HttpMethod.POST)
    public NuageUser signUp(User user) throws ForbiddenException {
        if (NuageUser.getNuageUserWithEmail(user.getEmail()) != null) {
            throw new com.google.api.server.spi.response.ForbiddenException("User " + user.getEmail() + " is already registered");
        }
        NuageUser newUser = null;
        newUser = new NuageUser(user.getEmail(), new ArrayList<String>(), new BigInteger(130, new SecureRandom()).toString(), new Long(0));
        newUser.store();
        return newUser;
    }

    @ApiMethod(name = "logIn", path = "login", httpMethod = ApiMethod.HttpMethod.POST)
    public NuageUser logIn(User user) throws ForbiddenException {
        NuageUser currentUser = NuageUser.getNuageUserWithEmail(user.getEmail());
        if (currentUser == null) {
            currentUser = signUp(user);
        }
        return currentUser;
    }

    @ApiMethod(name = "user", path = "user", httpMethod = ApiMethod.HttpMethod.GET)
    public NuageUser user(User user) throws ForbiddenException {
        return logIn(user);
    }

    @ApiMethod(name = "addCors", path = "user/cors/add", httpMethod = ApiMethod.HttpMethod.POST)
    public void addCors(User user, @Named("cors") String CORS) throws ForbiddenException {
        NuageUser currentUser = NuageUser.getNuageUserWithEmail(user.getEmail());
        if (currentUser == null) {
            throw new com.google.api.server.spi.response.ForbiddenException("User " + user.getEmail() + " is not registered");
        }
        currentUser.addCors(CORS);
    }

    @ApiMethod(name = "deleteCors", path = "user/cors/delete", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void deleteCors(User user, @Named("cors") String CORS) throws ForbiddenException {
        NuageUser currentUser = NuageUser.getNuageUserWithEmail(user.getEmail());
        if (currentUser == null) {
            throw new com.google.api.server.spi.response.ForbiddenException("User " + user.getEmail() + " is not registered");
        }
        currentUser.deleteCors(CORS);
    }

    @ApiMethod(name = "resetAPIKey", path = "user/key/reset", httpMethod = ApiMethod.HttpMethod.POST)
    public void resetAPIKey(User user) throws ForbiddenException {
        NuageUser currentUser = NuageUser.getNuageUserWithEmail(user.getEmail());
        if (currentUser == null) {
            throw new com.google.api.server.spi.response.ForbiddenException("User " + user.getEmail() + " is not registered");
        }
        currentUser.resetAPIKey();
    }

    @ApiMethod(name = "apiUsage", path = "user/usage", httpMethod = ApiMethod.HttpMethod.GET)
    public APIUsage apiUsage(User user) throws ForbiddenException
    {
        NuageUser currentUser = NuageUser.getNuageUserWithEmail(user.getEmail());
        if (currentUser == null) {
            throw new com.google.api.server.spi.response.ForbiddenException("User " + user.getEmail() + " is not registered");
        }
        return currentUser.currentUsage();
    }

    @ApiMethod(name = "detailedUsage", path = "user/detailedUsage", httpMethod = ApiMethod.HttpMethod.GET)
    public List<SourceUsage> detailedUsage(User user) throws ForbiddenException {
        List<SourceUsage> detailedUsageList = new ArrayList<>();
        NuageUser currentUser = NuageUser.getNuageUserWithEmail(user.getEmail());
        if (currentUser == null) {
            throw new com.google.api.server.spi.response.ForbiddenException("User " + user.getEmail() + " is not registered");
        }
        detailedUsageList.add(new SourceUsage(currentUser.getAPIKey(), currentUser.storedUsageForKey(currentUser.getAPIKey())));
        detailedUsageList.add(new SourceUsage("Deleted sources", currentUser.getUsageSave()));
        for (String CORS : currentUser.getCORSList())
        {
            detailedUsageList.add(new SourceUsage(CORS, currentUser.storedUsageForKey(CORS)));
        }
        return detailedUsageList;
    }

}
