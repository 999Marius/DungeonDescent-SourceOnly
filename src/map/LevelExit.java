// LevelExit.java
package map;

import java.awt.*;

public class LevelExit{
    private Point exitCoords;
    private boolean isActive;
    private int tileSize;
    private Color exitColor;
    private int pulseAlpha = 100;
    private boolean increasingAlpha = true;

    public LevelExit(int level, int tileSize){
        this.tileSize = tileSize;
        this.exitColor = new Color(0, 255, 0, pulseAlpha);
        setExitForLevel(level);
    }

    private void setExitForLevel(int level){
        switch(level){
            case 0 -> exitCoords = new Point(26, 15);
            case 1 -> exitCoords = new Point(12, 23);
            case 2 -> exitCoords = new Point(1, 1);
        }
    }

    public void update(){

    }

    public boolean isPlayerAtExit(int playerX, int playerY){
        int playerTileX = playerX/tileSize;
        int playerTileY = playerY/tileSize;
        return (playerTileX == exitCoords.x && playerTileY == exitCoords.y);
    }

    public Point getExitCoords(){ return exitCoords; }
    public void setActive(boolean active){ isActive = active; }
    public boolean isActive(){ return isActive; }
}