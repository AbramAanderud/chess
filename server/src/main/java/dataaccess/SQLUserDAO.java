package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {
    private final Connection connection;

    public SQLUserDAO(Connection connection) throws DataAccessException {
        this.connection = connection;
    }

    public void createUser(UserData u) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(u.password(), BCrypt.gensalt());
        String sql = "INSERT INTO userDataTable (username, password, email) VALUES (?, ?, ?)";

        try (PreparedStatement prepstmt = connection.prepareStatement(sql)) {
            prepstmt.setString(1, u.username());
            prepstmt.setString(2, hashedPassword);
            prepstmt.setString(3, u.email());
            prepstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create user " + e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM userDataTable WHERE username = ?";
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
            throw new DataAccessException("Failed to get user by username " + e.getMessage());
        }
        return null;
    }

    public void clearAllUserData() throws DataAccessException {
        String sql = "DELETE FROM userDataTable";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear " + e.getMessage());
        }
    }

}
