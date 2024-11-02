package dataaccess;

import chess.ChessGame;
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
        String jsonGame = gson.toJson(g.game());

        System.out.println("game name to be made " + g.gameName());

        try (PreparedStatement prepstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            prepstmt.setString(1, g.whiteUsername());
            prepstmt.setString(2, g.blackUsername());
            prepstmt.setString(3, g.gameName());
            prepstmt.setString(4, jsonGame);

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
        String resetIdSql = "ALTER TABLE gameDataTable AUTO_INCREMENT = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             PreparedStatement resetStmt = connection.prepareStatement(resetIdSql)) {
            stmt.executeUpdate();
            resetStmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear game data: " + e.getMessage());
        }
    }

    public void joinGameRequest(Integer gameID, String username, String color) throws DataAccessException {
        String updateStatement;
        if (color.equals("WHITE")) {
            updateStatement = "UPDATE gameDataTable SET whiteUsername = ? WHERE gameID = ?";
        } else if (color.equals("BLACK")) {
            updateStatement = "UPDATE gameDataTable SET blackUsername = ? WHERE gameID = ?";
        } else {
            throw new DataAccessException("Invalid color");
        }

        try (PreparedStatement ps = connection.prepareStatement(updateStatement)) {
            ps.setString(1, username);
            ps.setInt(2, gameID);
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated==0) {
                throw new DataAccessException("No game with that game ID");
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
        ChessGame game = gson.fromJson(gameJson, ChessGame.class);

        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }
}
