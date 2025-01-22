package collisions;

import map.Map;

public class Collisions {
    private Map currentMap;
    private int tileSize;
    private final float HITBOX_SCALE = 0.6f;
    private final int HITBOX_OFFSET; //offset from the edge of the tile to the hitbox
    private final int CHECK_POINT_OFFSET;
    private final int BOTTOM_OFFSET;

    public Collisions(Map currentMap, int tileSize) {
        this.currentMap = currentMap;
        this.tileSize = tileSize;

        //simpler we make de hitbox smaller by 15 pixels than the tile size overall in this case
        //like we have the player 60% of the tile hitbox_offset /2 because we need to make it smaller in each side
        HITBOX_OFFSET = (int)((1 - HITBOX_SCALE) * tileSize / 2);

        //check more than the hitbox
        CHECK_POINT_OFFSET = HITBOX_OFFSET + (int)(tileSize * 0.05f);

        //bottom bigger so it does not clip as easy
        BOTTOM_OFFSET = (int)(tileSize * 0.9f);
    }

    private boolean checkPoint(int worldX, int worldY) {
        int tileX = (worldX / 3) / 16;
        int tileY = (worldY / 3) / 16;

        if(tileX < 0 || tileY < 0 ||
                tileX >= currentMap.getCollisionMap()[0].length ||
                tileY >= currentMap.getCollisionMap().length) {
            return false;
        }

        return currentMap.getCollisionMap()[tileY][tileX] != 1;
    }

    public boolean canMove(int nextWorldX, int nextWorldY, String direction) {
        switch(direction) {
            case "up":
                return checkPoint(nextWorldX + CHECK_POINT_OFFSET, nextWorldY + CHECK_POINT_OFFSET) &&
                        checkPoint(nextWorldX + tileSize - CHECK_POINT_OFFSET, nextWorldY + CHECK_POINT_OFFSET);

            case "down":
                return checkPoint(nextWorldX + CHECK_POINT_OFFSET, nextWorldY + BOTTOM_OFFSET) &&
                        checkPoint(nextWorldX + tileSize/2, nextWorldY + BOTTOM_OFFSET) &&
                        checkPoint(nextWorldX + tileSize - CHECK_POINT_OFFSET, nextWorldY + BOTTOM_OFFSET);

            case "left":
                return checkPoint(nextWorldX + CHECK_POINT_OFFSET, nextWorldY + CHECK_POINT_OFFSET) &&
                        checkPoint(nextWorldX + CHECK_POINT_OFFSET, nextWorldY + tileSize - CHECK_POINT_OFFSET);

            case "right":
                return checkPoint(nextWorldX + tileSize - CHECK_POINT_OFFSET, nextWorldY + CHECK_POINT_OFFSET) &&
                        checkPoint(nextWorldX + tileSize - CHECK_POINT_OFFSET, nextWorldY + tileSize - CHECK_POINT_OFFSET);
        }
        return false;
    }

    public boolean canMoveEnemy(int nextWorldX, int nextWorldY, String direction) {
        switch(direction) {
            case "up":
                return checkPoint(nextWorldX + CHECK_POINT_OFFSET, nextWorldY + CHECK_POINT_OFFSET) &&
                        checkPoint(nextWorldX + tileSize - CHECK_POINT_OFFSET, nextWorldY + CHECK_POINT_OFFSET);

            case "down":
                return checkPoint(nextWorldX + CHECK_POINT_OFFSET, nextWorldY + BOTTOM_OFFSET) &&
                        checkPoint(nextWorldX + tileSize/2, nextWorldY + BOTTOM_OFFSET) &&
                        checkPoint(nextWorldX + tileSize - CHECK_POINT_OFFSET, nextWorldY + BOTTOM_OFFSET);

            case "left":
                return checkPoint(nextWorldX + CHECK_POINT_OFFSET, nextWorldY + CHECK_POINT_OFFSET) &&
                        checkPoint(nextWorldX + CHECK_POINT_OFFSET, nextWorldY + tileSize - CHECK_POINT_OFFSET);

            case "right":
                return checkPoint(nextWorldX + tileSize - CHECK_POINT_OFFSET, nextWorldY + CHECK_POINT_OFFSET) &&
                        checkPoint(nextWorldX + tileSize - CHECK_POINT_OFFSET, nextWorldY + tileSize - CHECK_POINT_OFFSET);
        }
        return false;
    }
}