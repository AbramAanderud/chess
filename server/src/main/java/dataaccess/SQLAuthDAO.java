package dataaccess;

import model.AuthData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public String createAuth(String username) {
        String newAuthToken = UUID.randomUUID().toString();
        String sql = "INSERT INTO auth (auth_token, username) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newAuthToken);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            e.printStackTrace();
        }

        return newAuthToken;
    }

    @Override
    public boolean isValidAuth(String authToken) {
        String sql = "SELECT COUNT(*) FROM auth WHERE auth_token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, authToken);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getUsernameByAuth(String authToken) {
        String sql = "SELECT username FROM auth WHERE auth_token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, authToken);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteAuthByAuth(String authToken) {
        String sql = "DELETE FROM auth WHERE auth_token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, authToken);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearAllAuthData() {
        String sql = "DELETE FROM auth";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
