package map;

import collisions.Collisions;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class MapManager {
    GamePanel gp;
    Map currentMap;
    HashMap<Integer, Map> maps;
    static private final int SCALE = 3;

    public MapManager(GamePanel gp){
        this.gp = gp;
        maps = new HashMap<>();
        loadAllMaps();
        setCurrentMap(0);
    }


    private void loadAllMaps() {
        try{
            BufferedImage level0 = ImageIO.read(new File("res/levelMaps/maps/level0.png"));
            maps.put(0, new Map(level0, 0, SCALE, "level0"));
            BufferedImage level1 = ImageIO.read(new File("res/levelMaps/maps/level1.png"));
            maps.put(1, new Map(level1, 1, SCALE, "level1"));
            BufferedImage level2 = ImageIO.read(new File("res/levelMaps/maps/level2.png"));
            maps.put(2, new Map(level2, 2, SCALE, "level2"));
            /// Add more maps here
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentMap(int i) {
        currentMap = maps.get(i);
        loadCollisionMap(i);
        gp.collisionChecker = new Collisions(currentMap, gp.tileSize);
    }
    private void loadCollisionMap(int x) {
        if(currentMap == null) return;

        try{
            Scanner scanner = new Scanner(new File("res/levelMaps/CollisionMaps/level" + x + "_collision_map.txt"));
            int rows = currentMap.getImage().getHeight() / gp.originalTileSize;
            int cols = currentMap.getImage().getWidth() / gp.originalTileSize;
            currentMap.setCollisionMap(new int[rows][cols]);
            for(int i = 0; i < rows; i++){
                for(int j = 0; j < cols; j++) {
                    currentMap.addCollisionMap(i, j, scanner.nextInt());
                }
            }
            scanner.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    //testing
    public void printCollisionMap() {
        if(currentMap == null) return;
        for(int i = 0; i < currentMap.getCollisionMap().length; i++){
            for(int j = 0; j < currentMap.getCollisionMap()[0].length; j++){
                System.out.print(currentMap.getCollisionMap()[i][j] + " ");
            }
            System.out.println();
        }
    }



    public void draw(Graphics2D g2, int playerWorldX, int playerWorldY) {
        if(currentMap != null){
            //calculate camera offset to center on player
            int cameraX = gp.screenWidth/2 - playerWorldX;
            int cameraY = gp.screenHeight/2 - playerWorldY;


            //draw map at camera offset position
            g2.drawImage(currentMap.getImage(),
                    cameraX - gp.tileSize/2, cameraY,
                    currentMap.getScaleWidth(),
                    currentMap.getScaleHeight(),
                    null);
        }
    }

    public Map getCurrentMap() {
        return currentMap;
    }
    public int getCurrentMapWidth() {
        return currentMap.getScaleWidth();
    }
    public int getCurrentMapHeight() {
        return currentMap.getScaleHeight();
    }
}
