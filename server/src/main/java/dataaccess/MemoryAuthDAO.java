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

    public void clearAllAuthData() {
        authData.clear();
    }
}
