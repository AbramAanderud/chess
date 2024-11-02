package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {
    private final Connection connection;

    public SQLAuthDAO() throws DataAccessException {
        this.connection = DatabaseManager.getConnection();
    }

    public String createAuth(String username) throws DataAccessException {
        String newAuthToken = UUID.randomUUID().toString();
        String sql = "INSERT INTO authDataTable (authtoken, username) VALUES (?, ?)";

        try (PreparedStatement prepstmt = connection.prepareStatement(sql)) {
            prepstmt.setString(1, newAuthToken);
            prepstmt.setString(2, username);
            prepstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create auth token: " + e.getMessage());
        }

        return newAuthToken;
    }

    public boolean isValidAuth(String authToken) throws DataAccessException {
        String sql = "SELECT COUNT(*) FROM authDataTable WHERE authtoken = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, authToken);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check auth token validity: " + e.getMessage());
        }
        return false;
    }

    public String getUsernameByAuth(String authToken) throws DataAccessException {
        String sql = "SELECT username FROM authDataTable WHERE authtoken = ?";
        try (PreparedStatement prepstmt = connection.prepareStatement(sql)) {
            prepstmt.setString(1, authToken);
            ResultSet rs = prepstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve username by auth token: " + e.getMessage());
        }
        return null;
    }

    public void deleteAuthByAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM authDataTable WHERE authtoken = ?";
        try (PreparedStatement prepstmt = connection.prepareStatement(sql)) {
            prepstmt.setString(1, authToken);
            prepstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete auth token: " + e.getMessage());
        }
    }

    public void clearAllAuthData() throws DataAccessException {
        String sql = "DELETE FROM authDataTable";
        try (PreparedStatement prepstmt = connection.prepareStatement(sql)) {
            prepstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear all auth data: " + e.getMessage());
        }
    }
}
