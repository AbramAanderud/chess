package serverfacade;


import com.google.gson.Gson;
import dataaccess.DataAccessException;
import requests.*;
import result.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;
    private String authToken;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass!=null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request!=null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    public void setLastStoredAuth(String auth) {
        authToken = auth;
    }

    public RegisterResult register(RegisterRequest r) throws DataAccessException, ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, r, RegisterResult.class, null);
    }

    public LoginResult login(LoginRequest l) throws DataAccessException, ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, l, LoginResult.class, null);
    }

    public LogoutResult logout(LogoutRequest l) throws DataAccessException, ResponseException {
        var path = "/session";
        return this.makeRequest("DELETE", path, l, LogoutResult.class, authToken);
    }

    public ListResult listGames(ListRequest l) throws DataAccessException, ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, l, ListResult.class, authToken);
    }

    public JoinResult joinGame(JoinRequest j) throws DataAccessException, ResponseException {
        var path = "/game";
        return this.makeRequest("PUT", path, j, JoinResult.class, authToken);
    }

    public CreateGameResult createGame(CreateGameRequest g) throws DataAccessException, ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, g, CreateGameResult.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(!method.equals("GET"));

            if (authToken!=null) {
                http.setRequestProperty("Authorization", authToken);
            }

            if (request!=null && !method.equals("GET")) {
                writeBody(request, http);
            }

            http.connect();
            throwIfNotSuccessful(http);

            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100==2;
    }

}
