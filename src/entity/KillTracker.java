package entity;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class KillTracker {
    private int totalKills;
    private HashMap<Integer, Integer> killsPerLevel;
    private HashMap<Integer, Integer> totalEnemiesPerLevel;
    private BufferedImage skullIcon;
    private GamePanel gp;
    private final int ICON_SIZE = 16;
    private final int OFFSET_X = 40;
    private final int OFFSET_Y = 10;

    public KillTracker(GamePanel gp) {
        this.gp = gp;
        killsPerLevel = new HashMap<>();
        totalEnemiesPerLevel = new HashMap<>();
        loadSkullIcon();
    }

    private void loadSkullIcon(){
        try {
            skullIcon = ImageIO.read(new File("res/misc/Skull.png"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setTotalEnemiesPerLevel(int level, int totalEnemies){
        totalEnemiesPerLevel.put(level, totalEnemies);
        killsPerLevel.putIfAbsent(level, 0);
    }

    public void incrementKills(int level){
        totalKills++;
        killsPerLevel.putIfAbsent(level, 0);
        int currentLevelKills = killsPerLevel.get(level);
        killsPerLevel.put(level, currentLevelKills + 1);
    }

    public boolean isLevelComplete(int level){
        int kills = killsPerLevel.getOrDefault(level, 0);
        int total = totalEnemiesPerLevel.getOrDefault(level, 0);
        return kills >= total;
    }

    public void drawKillCounters(Graphics g2){
        drawTotalKills(g2);
        drawLevelProgress(g2);
    }

    private void drawTotalKills(Graphics g2){
        int x = 20;
        int y = gp.screenHeight - 90;

        g2.drawImage(skullIcon, x, y, ICON_SIZE*2, ICON_SIZE*2, null);
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.WHITE);
        g2.drawString( "Total: " + String.valueOf(totalKills),  x + ICON_SIZE + 25, y + 31);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("kills", x + ICON_SIZE + 25, y+11);
    }
    private void drawLevelProgress(Graphics g2){
        int currentLevel = gp.mapManager.getCurrentMap().getLevelNumber();
        int currentKills = killsPerLevel.getOrDefault(currentLevel, 0);
        int totalEnemies = totalEnemiesPerLevel.getOrDefault(currentLevel, 0);

        int x = 10;
        int y = gp.screenHeight - 90;

        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString("Lvl: " +String.valueOf(currentKills)+ '/' + String.valueOf(totalEnemies), x + ICON_SIZE + 105, y + 31);
    }

    public int getTotalKills() {
        return totalKills;
    }
    public void setTotalKills(int kills){
        this.totalKills = kills;
    }

    public void setKillsForLevel(int level, int kills){
        killsPerLevel.put(level, kills);
    }
    public int getKillsForLevel(int level) {
        return killsPerLevel.getOrDefault(level, 0);
    }
}

