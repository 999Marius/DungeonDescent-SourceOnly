package map;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Map {
    private final LevelExit levelExit;
    private BufferedImage image;
    private int levelNumber;
    private int scale;
    private String mapName;
    private int [][] collisionMap;
    private Point exitTile;
    private boolean exitActive;

    public Map(BufferedImage image, int levelNumber, int scale, String mapName) {
        this.image = image;
        this.levelNumber = levelNumber;
        this.scale = scale;
        this.mapName = mapName;
        this.levelExit = new LevelExit(levelNumber, 64);
    }

    public void setCollisionMap(int[][] collisionMap) {
        this.collisionMap = collisionMap;
    }
    public void addCollisionMap(int row, int col, int value) {
        collisionMap[row][col] = value;
    }
    public int[][] getCollisionMap() {
        return collisionMap;
    }

    public BufferedImage getImage() {
        return image;
    }
    public int getLevelNumber() {
        return levelNumber;
    }
    public int getScale() {
        return scale;
    }
    public String getMapName() {
        return mapName;
    }
    public int getScaleWidth() {
        return image.getWidth() * scale;
    }
    public int getScaleHeight() {
        return image.getHeight() * scale;
    }
    public LevelExit getLevelExit(){ return levelExit; }

    public int getMapNumber() {
        return levelNumber;
    }
}
