package dataaccess;

import java.sql.*;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {
    private final Connection connection;

    public SQLAuthDAO() throws DataAccessException {
        this.connection = DatabaseManager.getConnection();
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        String newAuthToken = UUID.randomUUID().toString();
        String sql = "INSERT INTO authDataTable (authtoken, username) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newAuthToken);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create auth token: " + e.getMessage());
        }

        return newAuthToken;
    }

    @Override
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

    @Override
    public String getUsernameByAuth(String authToken) throws DataAccessException {
        String sql = "SELECT username FROM authDataTable WHERE authtoken = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, authToken);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve username by auth token: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuthByAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM authDataTable WHERE authtoken = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, authToken);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete auth token: " + e.getMessage());
        }
    }

    @Override
    public void clearAllAuthData() throws DataAccessException {
        String sql = "DELETE FROM authDataTable";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear all auth data: " + e.getMessage());
        }
    }
}
