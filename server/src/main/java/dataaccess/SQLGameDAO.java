package dataaccess;

import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {
    private final Connection connection;
    private final Gson gson = new Gson();

    public SQLGameDAO() throws DataAccessException {
        this.connection = DatabaseManager.getConnection();
    }

    @Override
    public int createGame(GameData g) throws DataAccessException {
        String sql = "INSERT INTO gameDataTable (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        String jsonGameBoard = gson.toJson(g.game());

        try (PreparedStatement prepstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            prepstmt.setString(1, g.whiteUsername());
            prepstmt.setString(2, g.blackUsername());
            prepstmt.setString(3, g.gameName());
            prepstmt.setString(4, jsonGameBoard);

            prepstmt.executeUpdate();
            try (ResultSet generatedKeys = prepstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DataAccessException("Failed to make game and get ID");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create game: " + e.getMessage());
        }
    }


    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM gameDataTable WHERE gameID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return readGame(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve game: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM gameDataTable";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                games.add(readGame(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list games: " + e.getMessage());
        }

        return games;
    }

    @Override
    public void clearAllGameData() throws DataAccessException {
        String sql = "DELETE FROM gameDataTable";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear game data: " + e.getMessage());
        }
    }

    public void joinGameRequest(Integer gameID, String username, String color) throws DataAccessException {
        if (gameID==null || username==null || color==null) {
            throw new DataAccessException("GameID, username, and color must not be null.");
        }

        String updateStatement;
        if (color.equalsIgnoreCase("WHITE")) {
            updateStatement = "UPDATE Game SET whiteUsername = ? WHERE gameID = ?";
        } else if (color.equalsIgnoreCase("BLACK")) {
            updateStatement = "UPDATE Game SET blackUsername = ? WHERE gameID = ?";
        } else {
            throw new DataAccessException("Invalid color provided.");
        }

        try (PreparedStatement ps = connection.prepareStatement(updateStatement)) {
            ps.setString(1, username);
            ps.setInt(2, gameID);
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated==0) {
                throw new DataAccessException("No game found with the provided game ID.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating the game: " + e.getMessage());
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String gameJson = rs.getString("game");

        return gson.fromJson(gameJson, GameData.class);
    }
}
