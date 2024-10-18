package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class MemoryAuthDAO {
    Collection<AuthData> authData = new ArrayList<>();

    public void createAuth(String username) {
        String newAuthToken = UUID.randomUUID().toString();
        AuthData newAuthData = new AuthData(newAuthToken, username);
        authData.add(newAuthData);
    }

    public AuthData getAuth(String username) {
        if(authData.isEmpty()) {
            System.out.println("authData is empty");
            return null;
        }
        for(AuthData currData : authData) {
            if(currData.username().equals(username)) {
                return currData;
            }
        }
        System.out.println("didn't find username to get auth from");
        return null;
    }

    public void deleteAuth(String username) {
        if(authData.isEmpty()) {
            System.out.println("data is empty when trying to delete");
            return;
        }
        authData.removeIf(currData -> currData.username().equals(username));
    }
}
