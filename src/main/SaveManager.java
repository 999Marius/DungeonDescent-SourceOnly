package main;

import enemies.Enemy;
import entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {
    private static SaveManager instance = new SaveManager();
    private Connection connection;
    private static final String DB_PATH = "jdbc:sqlite:res/game_saves.sqlite";
    private static final int MAX_SAVES = 10;

    private SaveManager() {
        connectToDatabase();
    }

    private void  connectToDatabase(){
        try{
            connection = DriverManager.getConnection(DB_PATH);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static SaveManager getInstance(){
        if(instance == null){
            instance = new SaveManager();
        }
        return instance;
    }

    public int createSaveSlot(String saveName){
        maintainSaveLimit();
        String sql = "INSERT INTO save_slots(save_name) VALUES(?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            pstmt.setString(1, saveName);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();

            if(rs.next()){
                return rs.getInt(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    public void saveGameState(int slotId, GamePanel gamePanel){
        try{
            connection.setAutoCommit(false);
            savePlayerState(slotId, gamePanel.getPlayer());
            saveEnemyState(slotId, gamePanel.enemyManager.getEnemies());
            saveGameProgress(slotId, gamePanel);
            saveLevelState(slotId, gamePanel);
            connection.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void savePlayerState(int slotId, Player player){
        String sql = "INSERT OR REPLACE INTO player_state(slot_id, worldX, worldY, health, direction) VALUES(?, ?, ?, ?, ?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, slotId);
            pstmt.setInt(2, player.worldX);
            pstmt.setInt(3, player.worldY);
            pstmt.setInt(4, player.getCurrentHealth());
            pstmt.setString(5, player.direction);
            pstmt.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void saveEnemyState(int slotId, List<Enemy> enemies){
        String sql = "INSERT OR REPLACE INTO enemy_state(slot_id, enemy_id, type, worldX, worldY, health) VALUES(?, ?, ?, ?, ?, ?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            for(int i = 0; i < enemies.size(); i++){
                Enemy enemy = enemies.get(i);
                int typeId = getEnemyTypeId(enemy.getEnemyType());
                pstmt.setInt(1, slotId);
                pstmt.setInt(2, i);
                pstmt.setInt(3, typeId);
                pstmt.setInt(4, enemy.worldX);
                pstmt.setInt(5, enemy.worldY);
                pstmt.setInt(6, enemy.getCurrentHealth());
                pstmt.executeUpdate();
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
    private int getEnemyTypeId(String typeName){
        String sql = "SELECT type_id FROM enemy_types WHERE type_name = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, typeName);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getInt("type_id");
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    public void saveLevelEnemyConfig(int levelId, int enemyTypeId, int minSpawn, int maxSpawn, float spawnRate){
        String sql = "INSERT OR REPLACE INTO level_enemy_config VALUES(?, ?, ?, ?, ?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, levelId);
            pstmt.setInt(2, enemyTypeId);
            pstmt.setInt(3, minSpawn);
            pstmt.setInt(4, maxSpawn);
            pstmt.setFloat(5, spawnRate);
            pstmt.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void saveLevelEnemyPlacement(int levelId, int enemyTypeId, int x, int y){
        String sql = "INSERT INTO level_enemy_placement VALUES(?, ?, ?, ?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, levelId);
            pstmt.setInt(2, enemyTypeId);
            pstmt.setInt(3, x);
            pstmt.setInt(4, y);
            pstmt.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void saveGameProgress(int slotId, GamePanel gamePanel){
        String sql = "INSERT OR REPLACE INTO game_progress VALUES(?, ?, ?, ?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            int currentLevel = gamePanel.mapManager.getCurrentMap().getLevelNumber();
            pstmt.setInt(1, slotId);
            pstmt.setInt(2, gamePanel.mapManager.getCurrentMap().getLevelNumber());
            pstmt.setInt(3, gamePanel.killTracker.getTotalKills());
            pstmt.setInt(4, gamePanel.killTracker.getKillsForLevel(currentLevel));
            pstmt.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void saveLevelState(int slotId, GamePanel gamePanel){
        String sql = "INSERT OR REPLACE INTO level_state VALUES(?, ?, ?, ?)";
        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            int currentLevel = gamePanel.mapManager.getCurrentMap().getLevelNumber();
            pstmt.setInt(1, slotId);
            pstmt.setInt(2, currentLevel);
            pstmt.setBoolean(3, gamePanel.getLevelExit().isActive());
            pstmt.setBoolean(4, gamePanel.killTracker.isLevelComplete(currentLevel));
            pstmt.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public List<SaveSlot> getAllSaveSlots() {
        List<SaveSlot> saveSlots = new ArrayList<>();
        String sql = "SELECT * FROM save_slots ORDER BY timestamp DESC";

        try(Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while(rs.next()) {
                int id = rs.getInt("slot_id");
                String name = rs.getString("save_name");
                String timestamp = rs.getString("timestamp");
                saveSlots.add(new SaveSlot(id, name, timestamp));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return saveSlots;
    }

    public static class SaveSlot{
        public final int id;
        public final String name;
        public final String timestamp;

        public SaveSlot(int id, String name, String timestamp){
            this.id = id;
            this.name = name;
            this.timestamp = timestamp;
        }
    }

    public ResultSet loadPlayerState(int slotId) throws SQLException {
        String sql = "SELECT * FROM player_state WHERE slot_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, slotId);
        return pstmt.executeQuery();
    }

    public ResultSet loadEnemyState(int slotId) throws SQLException {
        String sql = """
        SELECT e.*, t.type_name 
        FROM enemy_state e 
        JOIN enemy_types t ON e.type = t.type_id 
        WHERE e.slot_id = ?
    """;
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, slotId);
        return pstmt.executeQuery();
    }

    public ResultSet loadGameProgress(int slotId) throws SQLException {
        String sql = "SELECT * FROM game_progress WHERE slot_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, slotId);
        return pstmt.executeQuery();
    }

    public ResultSet loadLevelState(int slotId) throws SQLException {
        String sql = "SELECT * FROM level_state WHERE slot_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, slotId);
        return pstmt.executeQuery();
    }

    private void maintainSaveLimit() {
        String countSql = "SELECT COUNT(*) as count FROM save_slots";
        String getOldestSql = "SELECT slot_id FROM save_slots ORDER BY timestamp ASC LIMIT ?";

        try(Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(countSql)) {

            if(rs.next()) {
                int currentSaves = rs.getInt("count");
                if(currentSaves >= MAX_SAVES) {
                    connection.setAutoCommit(false);  // Start transaction

                    // Get the slot_ids to delete
                    int toDelete = currentSaves - MAX_SAVES + 1;
                    PreparedStatement pstmt = connection.prepareStatement(getOldestSql);
                    pstmt.setInt(1, toDelete);
                    ResultSet slotsToDelete = pstmt.executeQuery();

                    while(slotsToDelete.next()) {
                        int slotId = slotsToDelete.getInt("slot_id");
                        // Delete from all related tables
                        stmt.executeUpdate("DELETE FROM enemy_state WHERE slot_id = " + slotId);
                        stmt.executeUpdate("DELETE FROM player_state WHERE slot_id = " + slotId);
                        stmt.executeUpdate("DELETE FROM game_progress WHERE slot_id = " + slotId);
                        stmt.executeUpdate("DELETE FROM level_state WHERE slot_id = " + slotId);
                        stmt.executeUpdate("DELETE FROM save_slots WHERE slot_id = " + slotId);
                    }

                    connection.commit();
                }
            }
        } catch(SQLException e) {
            try {
                if(connection != null) connection.rollback();
            } catch(SQLException re) {
                re.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void deleteAllSaves() {
        try {
            connection.setAutoCommit(false);  // Start transaction
            Statement stmt = connection.createStatement();

            String[] deleteQueries = {
                    "DELETE FROM enemy_state",
                    "DELETE FROM player_state",
                    "DELETE FROM game_progress",
                    "DELETE FROM level_state",
                    "DELETE FROM save_slots"
            };

            for(String sql : deleteQueries) {
                stmt.executeUpdate(sql);
            }

            connection.commit();
        } catch(SQLException e) {
            try {
                connection.rollback();
            } catch(SQLException re) {
                re.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
