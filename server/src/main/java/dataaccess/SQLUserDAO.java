package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class SQLUserDAO implements UserDAO {
    private final Connection connection;

    public SQLUserDAO() throws DataAccessException {
        this.connection = DatabaseManager.getConnection();
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(u.password(), BCrypt.gensalt());
        String sql = "INSERT INTO User (username, password, email) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, u.username());
            stmt.setString(2, hashedPassword); // Store hashed password
            stmt.setString(3, u.email());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create user: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM User WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UserData(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve user: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void clearAllUserData() throws DataAccessException {
        String sql = "DELETE FROM User";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear user data: " + e.getMessage());
        }
    }

    private String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }
}
