package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private static MemoryAuthDAO instance;
    private final Collection<AuthData> authData = new ArrayList<>();

    private MemoryAuthDAO() { }

    public static MemoryAuthDAO getInstance() {
        if (instance == null) {
            instance = new MemoryAuthDAO();
        }
        return instance;
    }

    public String createAuth(String username) {
        String newAuthToken = UUID.randomUUID().toString();
        AuthData newAuthData = new AuthData(newAuthToken, username);
        authData.add(newAuthData);
        return newAuthToken;
    }

    public boolean isValidAuth(String authToken) {
        for(AuthData currData : authData) {
            if(currData.authToken().equals(authToken)) {
                return true;
            }
        }
        return false;
    }

    public AuthData getAuth(String username) {
        if(authData.isEmpty()) {
            return null;
        }
        for(AuthData currData : authData) {
            if(currData.username().equals(username)) {
                return currData;
            }
        }
        return null;
    }

    public void deleteAuth(String username) {
        if(authData.isEmpty()) {
            return;
        }
        authData.removeIf(currData -> currData.username().equals(username));
    }

    public String getUsernameByAuth(String authToken) {
        if(authData.isEmpty()) {
            return null;
        }
        for(AuthData currData : authData) {
            if(currData.authToken().equals(authToken)) {
                return currData.username();
            }
        }
        return null;
    }


    public void deleteAuthByAuth(String authToken) {
        if(authData.isEmpty()) {
            return;
        }
        authData.removeIf(currData -> currData.authToken().equals(authToken));
    }

    public void clearAllAuthData() {
        authData.clear();
    }
}
