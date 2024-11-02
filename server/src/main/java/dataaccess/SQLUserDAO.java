package dataaccess;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO {

    public void createUser(UserData u) throws DataAccessException {
        String sql = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.username());
            stmt.setString(2, u.password());
            stmt.setString(3, u.email());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error inserting user: " + e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM Users WHERE username = ?";
        UserData user = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String password = rs.getString("password");
                String email = rs.getString("email");
                user = new UserData(username, password, email);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error finding user: " + e.getMessage());
        }

        return user;
    }

    public void clearAllUserData() throws DataAccessException {
        String sql = "DELETE FROM Users";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error clearing user data: " + e.getMessage());
        }
    }




}
