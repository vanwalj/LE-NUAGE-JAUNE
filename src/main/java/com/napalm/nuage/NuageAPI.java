package com.napalm.nuage;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.appengine.api.users.User;

import javax.inject.Named;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;


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
    public String helloWorld()
    {
        return "hello world";
    }

    @ApiMethod(name = "helloWorld2", path = "helloworld2", httpMethod = ApiMethod.HttpMethod.GET)
    public String helloWorld2(User user) throws ForbiddenException {
        return "hello world 2";
    }

    @ApiMethod(name = "signUp", path = "signup", httpMethod = ApiMethod.HttpMethod.POST)
    public NuageUser signUp(User user) throws ForbiddenException {
        if (NuageUser.getNuageUserWithEmail(user.getEmail()) != null) {
            throw new com.google.api.server.spi.response.ForbiddenException("User " + user.getEmail() + " is already registered");
        }
        NuageUser newUser = null;
        newUser = new NuageUser(user.getEmail(), new ArrayList<String>(), new BigInteger(130, new SecureRandom()).toString());
        newUser.store();
        return newUser;
    }

    @ApiMethod(name = "logIn", path = "login", httpMethod = ApiMethod.HttpMethod.POST)
    public NuageUser logIn(User user) throws ForbiddenException {
        NuageUser currentUser = NuageUser.getNuageUserWithEmail(user.getEmail());
        if (currentUser == null) {
            signUp(user);
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

}
