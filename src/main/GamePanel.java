package main;

import collisions.Collisions;
import enemies.Enemy;
import enemies.EnemyManager;
import entity.KillTracker;
import entity.Player;
import map.LevelExit;
import map.LevelTransitionManager;
import map.MapManager;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {
    //Screen settings
    public final int originalTileSize = 16;
    final int scale = 4;

    public final int tileSize = originalTileSize * scale; //64x64
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // pixels: 1024
    public final int screenHeight = tileSize * maxScreenRow; // pixels: 768

    //FPS
    int fps = 60;

    //menu buttons
    public Rectangle resumeButton;
    public Rectangle savesButton;
    public Rectangle saveGameButton;
    public Rectangle exitButton;
    protected Rectangle restartButton;
    protected Rectangle quitButton;
    //save
    public List<Rectangle> saveSlotButtons = new ArrayList<>();
    private int selectedSaveSlot = -1;


    public enum GameState{
        PLAY,
        PAUSE,
        MENU,
        SAVES_MENU,
        GAME_OVER
    }

    public GameState currentState = GameState.PLAY;

    // UI Colors
    private final Color PAUSE_OVERLAY = new Color(0, 0, 0, 100);
    private final Color MENU_BACKGROUND = new Color(0, 0, 0, 200);
    private final Color MENU_TEXT = Color.WHITE;
    public Font MENU_FONT;
    public KeyHandler keyH = new KeyHandler();

    public Thread gameThread;
    public KillTracker killTracker;
    public boolean debugMode = true;  // Set to true to see hitbox
    public MapManager mapManager = new MapManager(this);
    private LevelExit levelExit;

    public Collisions collisionChecker = new Collisions(mapManager.getCurrentMap(), tileSize);
    public EnemyManager enemyManager = new EnemyManager(this, collisionChecker);
    Player player = new Player(this, keyH, collisionChecker, enemyManager);
    public LevelTransitionManager transitionManager = new LevelTransitionManager();

    public GamePanel(){
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.addMouseListener(new MouseHandler(this));
        killTracker = new KillTracker(this);
        levelExit = new LevelExit(0, tileSize);

        //for level 0 beacaue we init the mapmanager with level 0 so nextlevel will never get to 0
        enemyManager.spawnRandomEnemies(1, 1000, 2000, 1000, 2000);
        killTracker.setTotalEnemiesPerLevel(0, enemyManager.getEnemies().size());

        try{
            MENU_FONT = Font.createFont(Font.TRUETYPE_FONT, new File("res/font/DungeonFont.ttf")).deriveFont(Font.PLAIN, 32);
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Font not loaded: " + e.getMessage());
        }
    }

    public void startGame(){
        if(gameThread == null){
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / fps; // 1 second = 1,000,000,000 nanoseconds
                                                         // 60 fps
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();

                delta--;
            }
        }
    }
    public void update(){
        transitionManager.update();
        if(keyH.escPressed && !keyH.escWasPressed){
            toggleGameState();
            keyH.escWasPressed = true;
        }
        if(!keyH.escPressed){
            keyH.escWasPressed = false;
        }

        if(player.isDead() && currentState != GameState.GAME_OVER){
            currentState = GameState.GAME_OVER;
        }

        if(currentState == GameState.PLAY){
            checkLevelProgress();
            int currentLevel = mapManager.getCurrentMap().getLevelNumber();

            if(killTracker.isLevelComplete(currentLevel)){
                mapManager.getCurrentMap().getLevelExit().setActive(true);

                if(mapManager.getCurrentMap().getLevelExit().isPlayerAtExit(player.worldX, player.worldY)){
                    loadNextLevel();
                }
            }

            mapManager.getCurrentMap().getLevelExit().update();
            player.update();
            enemyManager.update();
        }
    }

    public void loadNextLevel(){
        if(!transitionManager.isTransitioning){
            transitionManager.startTransition();
        } else if(transitionManager.shouldLoadLevel()){
            int nextLevel = mapManager.getCurrentMap().getLevelNumber() + 1;
            mapManager.setCurrentMap(nextLevel);
            enemyManager.getEnemies().clear();
            collisionChecker = new Collisions(mapManager.getCurrentMap(), tileSize);
            player.setCollisionChecker(collisionChecker);
            enemyManager.setCollisionChecker(collisionChecker);

            levelExit = new LevelExit(nextLevel, tileSize);
            switch(nextLevel){
                case 0 -> {
                    player.worldX = 1280;
                    player.worldY = 1024;
                    enemyManager.spawnEnemiesForLevel(nextLevel);
                }
                case 1 -> {
                    player.worldX = 832;
                    player.worldY = 512;
                    enemyManager.spawnEnemiesForLevel(nextLevel);
                }
                case 2-> {
                    player.worldX = 1220;
                    player.worldY = 1308;
                    enemyManager.spawnEnemiesForLevel(nextLevel);
                }
            }
            killTracker.setTotalEnemiesPerLevel(nextLevel, enemyManager.getEnemies().size());
        }
    }
    public void loadGame(int slotId) {
        SaveManager saveManager = SaveManager.getInstance();

        try {
            // load game progress first to set correct level
            ResultSet progressRs = saveManager.loadGameProgress(slotId);
            if(progressRs.next()) {
                // get and set the level number
                int levelNumber = progressRs.getInt("level_number");
                mapManager.setCurrentMap(levelNumber);

                collisionChecker = new Collisions(mapManager.getCurrentMap(), tileSize);
                player.setCollisionChecker(collisionChecker);
                enemyManager.setCollisionChecker(collisionChecker);

                // reset kill tracker completely before loading save data
                killTracker = new KillTracker(this);
                killTracker.setTotalKills(progressRs.getInt("total_kills"));
                killTracker.setTotalEnemiesPerLevel(levelNumber, enemyManager.getEnemiesForLevel(levelNumber));


                // clear enemies
                enemyManager.getEnemies().clear();

                // Load player state
                ResultSet playerRs = saveManager.loadPlayerState(slotId);
                if(playerRs.next()) {
                    player.worldX = playerRs.getInt("worldX");
                    player.worldY = playerRs.getInt("worldY");
                    player.setCurrentHealth(playerRs.getInt("health"));
                    player.direction = playerRs.getString("direction");
                }

                // Load enemies
                ResultSet enemyRs = saveManager.loadEnemyState(slotId);
                enemyManager.getEnemies().clear();

                int aliveCount = 0;
                while(enemyRs.next()) {
                    int health = enemyRs.getInt("health");
                    if(health > 0) {
                        String enemyType = enemyRs.getString("type_name");
                        int x = enemyRs.getInt("worldX");
                        int y = enemyRs.getInt("worldY");
                        Enemy enemy = enemyManager.spawnEnemy(enemyType, x, y);
                        if(enemy != null) {
                            System.out.println("Setting enemy health to: " + health);
                            enemy.setCurrentHealth(health);
                            System.out.println("Enemy health after set: " + enemy.getCurrentHealth());
                            aliveCount++;
                        }
                    }
                }

                //set kills for this level based on alive enemies
                int totalEnemies = enemyManager.getEnemiesForLevel(levelNumber);
                int killedEnemies = totalEnemies - aliveCount;
                killTracker.setKillsForLevel(levelNumber, killedEnemies);
                System.out.println("After loading enemies, count: " + enemyManager.getEnemies().size());
                //load level state
                ResultSet levelRs = saveManager.loadLevelState(slotId);
                if(levelRs.next()) {
                    boolean exitActive = levelRs.getBoolean("exit_status");
                    mapManager.getCurrentMap().getLevelExit().setActive(exitActive);
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    public void checkLevelProgress(){
        int currentLevel = mapManager.getCurrentMap().getLevelNumber();

        if(killTracker.isLevelComplete(currentLevel)){

            levelExit.setActive(true);
            if(levelExit.isPlayerAtExit(player.worldX, player.worldY)){
                loadNextLevel();
            }
        }
    }
    private void toggleGameState(){
        if(currentState == GameState.PLAY){
            currentState = GameState.PAUSE;
        }else if(currentState == GameState.PAUSE){
            currentState = GameState.PLAY;
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        mapManager.draw(g2, player.worldX, player.worldY);

        int cameraX = screenWidth/2 - player.worldX - tileSize/2;;
        int cameraY = screenHeight/2 - player.worldY;

        enemyManager.draw(g2, cameraX, cameraY);
        player.drawHealthBar(g2);
        player.draw(g2, cameraX, cameraY);
        killTracker.drawKillCounters(g2);
        if(currentState == GameState.PAUSE){
            drawPauseScreen(g2);
        }else if(currentState == GameState.SAVES_MENU){
            drawSavesMenu(g2);
        }else  if(currentState == GameState.GAME_OVER){
            drawGameOverScreen(g2);
        }
        transitionManager.draw(g2, screenWidth, screenHeight);

        int level = mapManager.getCurrentMap().getLevelNumber();
        if (killTracker.isLevelComplete(level)) {
            drawLevelCompleteMessage(g2);
        }

        g2.dispose();
    }

    private void updateButtonBounds(int x, int y, int width, int height, String text){
        switch (text){
            case "Resume" -> resumeButton = new Rectangle(x, y-10, width, height);
            case "Load Save" -> savesButton = new Rectangle(x, y-10, width, height);
            case "Exit" -> exitButton = new Rectangle(x, y-10, width, height);
            case "Save Game" -> saveGameButton = new Rectangle(x, y-10, width, height);
        }
    }

    private boolean isTextHovered(Rectangle buttonArea){
        Point mousePos = getMousePosition();
        return mousePos !=null && buttonArea.contains(mousePos);
    }
    public void drawSavesMenu(Graphics2D g2){
        g2.setColor(MENU_BACKGROUND);
        g2.fillRect(0, 0, screenWidth/2, screenHeight); // Left side menu

        List<SaveManager.SaveSlot> saves = SaveManager.getInstance().getAllSaveSlots();
        saveSlotButtons.clear();

        g2.setFont(MENU_FONT);
        int buttonHeight = 50;
        int padding = 20;
        // Draw save slots
        for(int i = 0; i < saves.size(); i++){
            SaveManager.SaveSlot save = saves.get(i);
            int y = padding + i * (buttonHeight + 10);

            Rectangle button = new Rectangle(padding, y, screenWidth/2 - 2*padding, buttonHeight);
            saveSlotButtons.add(button);

            if(isTextHovered(button)){
                g2.setColor(Color.GRAY);
            } else {
                g2.setColor(Color.WHITE);
            }

            g2.drawString(save.name, button.x + 10, button.y + 35);
            g2.drawString(save.timestamp, button.x + 10, button.y + 60);
        }

        // draw back button
        g2.setColor(Color.WHITE);
        g2.drawString("Back", padding, screenHeight - padding);
    }

    private void drawPauseScreen(Graphics2D g2){
        g2.setColor(PAUSE_OVERLAY);
        g2.fillRect(0, 0, screenWidth, screenHeight);

        //pause menu
        g2.setColor(MENU_BACKGROUND);
        int menuWidth = 300;
        int menuHeight = 400;

        int menuX =(screenWidth - menuWidth)/2;
        int menuY =(screenHeight - menuHeight)/2;

        g2.setFont(MENU_FONT);
        g2.setColor(MENU_TEXT);

        String[] menuItems = {"PAUSED", "Resume", "Load Save", "Save Game", "Exit"};
        int textY = menuY + 80;

        for(String menuItem : menuItems){
            int textX = menuX + (menuWidth - g2.getFontMetrics().stringWidth(menuItem)) / 2;

            if(!menuItem.equals("PAUSED")){
                updateButtonBounds(textX, textY-30, g2.getFontMetrics().stringWidth(menuItem), 40, menuItem);
                if(isTextHovered(getButtonForText(menuItem))){
                    g2.setColor(Color.gray);
                }else{
                    g2.setColor(MENU_TEXT);
                }
            }

            g2.drawString(menuItem, textX, textY);
            textY += menuItem.equals("PAUSED") ? 60 : 40;
        }
    }
    public void drawGameOverScreen(Graphics2D g2){
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        int menuWidth = 300;
        int menuHeight = 200;
        int menuX = (screenWidth - menuWidth)/2;
        int menuY = (screenHeight - menuHeight)/2;


        int buttonY = menuY + 60;
        restartButton = new Rectangle(menuX + 50, buttonY, 200, 40);
        quitButton = new Rectangle(menuX + 50, buttonY + 60, 200, 40);

        g2.setFont(MENU_FONT);
        g2.setColor(Color.WHITE);
        g2.drawString("GAME OVER", menuX + 70, menuY + 40);

        Point mousePos = getMousePosition();
        if(mousePos != null && restartButton.contains(mousePos)){
            g2.setColor(Color.gray);
        } else{
            g2.setColor(MENU_TEXT);
        }
        g2.drawString("Restart", restartButton.x + (restartButton.width - g2.getFontMetrics().stringWidth("Restart"))/2, restartButton.y + 30);

        if(mousePos != null && quitButton.contains(mousePos)){
            g2.setColor(Color.gray);
        } else{
            g2.setColor(MENU_TEXT);
        }
        g2.drawString("Quit", quitButton.x + (quitButton.width - g2.getFontMetrics().stringWidth("Quit"))/2, quitButton.y + 30);
    }

    public void drawLevelCompleteMessage(Graphics2D g2) {
        String message = "Level Completed";
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.GRAY);

        int messageWidth = g2.getFontMetrics().stringWidth(message);
        int messageX = (screenWidth - messageWidth) / 2;
        int messageY = screenHeight / 16;

        g2.drawString(message, messageX, messageY);
    }

    private Rectangle getButtonForText(String text) {
        return switch(text) {
            case "Resume" -> resumeButton;
            case "Load Save" -> savesButton;
            case "Save Game" -> saveGameButton;
            case "Exit" -> exitButton;
            default -> null;
        };
    }

    public Player getPlayer() {
        return  player;
    }
    public LevelExit getLevelExit() {return levelExit;}
}
