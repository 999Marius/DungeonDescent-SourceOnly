package enemies;

import collisions.Collisions;

import enemies.behaviors.state.RandomAttackBehavior;
import enemies.behaviors.state.states.StateMachineBehavior;
import enemies.types.Ogre;
import main.GamePanel;
import main.SaveManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class EnemyManager {
    private final SaveManager saveManager;
    private GamePanel gp;
    private Collisions collisionChecker;
    private List<Enemy> enemies;  // Store all active enemies

    public EnemyManager(GamePanel gp, Collisions collisionChecker) {
        this.gp = gp;
        this.collisionChecker = collisionChecker;
        this.enemies = new ArrayList<>();
        this.saveManager = SaveManager.getInstance();
    }
    public int getEnemiesForLevel(int level) {
        switch(level) {
            case 0: return 1;  // Level 0 has 1 enemy
            case 1: return 2;  // Level 1 has 2 enemies
            case 2: return 3;  // Level 2 has 3 enemies
            default: return 1; // Default to 1 enemy
        }
    }
    public void spawnEnemiesForLevel(int level) {
        int enemyCount = getEnemiesForLevel(level);
        spawnRandomEnemies(enemyCount, 1000, 2000, 1000, 2000);
    }
    // Create and add a new enemy
    public Enemy spawnEnemy(String enemyType, int x, int y) {
        Enemy enemy = null;
        switch (enemyType.toUpperCase()) {
            case "OGRE":
                x = (x / gp.tileSize) * gp.tileSize;
                y = (y / gp.tileSize) * gp.tileSize;
                enemy = new Ogre(gp, collisionChecker, x, y, 100);
                enemy.setBehavior(new StateMachineBehavior(gp));
                enemy.setAttackBehavior(new RandomAttackBehavior());
                enemies.add(enemy);
                break;
        }
        return enemy;
    }
    public void spawnRandomEnemies(int count, int minX, int maxX, int minY, int maxY){
        Random random = new Random();
        for(int i = 0; i < count; i++){
            int x, y;
            do{
                x = minX + random.nextInt(maxX - minX);
                x = (x / gp.tileSize) * gp.tileSize;
                y = minY + random.nextInt(maxY - minY);
                y = (y / gp.tileSize) * gp.tileSize;
            } while(!collisionChecker.canMove(x, y, "down") && !collisionChecker.canMove(x, y, "up") );

            spawnEnemy("OGRE", x, y);
        }
    }

    // Update all enemies
    public void update() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() || enemy.isInDeathAnimation()){
                enemy.getBehavior().update(enemy);
                if (enemy.attackBehavior != null) {
                    enemy.attackBehavior.update(enemy);
                }
            }
        }
    }

    // Draw all enemies
    private Color getHealthBarColor(double healthPercent){
        if(healthPercent > 0.6) return new Color(0, 255, 0, 50); // Green
        if(healthPercent > 0.3) return new Color(255, 255, 0, 50); // Yellow
        return new Color(255, 0, 0, 50); // Red
    }

    public void draw(Graphics2D g2, int cameraX, int cameraY) {
        for(Enemy enemy : enemies) {
            if(enemy.isAlive() ||  enemy.isInDeathAnimation) {
                BufferedImage image = null;

                if(enemy.isInDeathAnimation){
                    switch(enemy.direction){
                        case "up":
                            image  = enemy.deathUp[enemy.spriteNum];
                            break;
                        case "down":
                            image = enemy.deathDown[enemy.spriteNum];
                            break;
                        case "left":
                            image = enemy.deathLeft[enemy.spriteNum];
                            break;
                        case "right":
                            image = enemy.deathRight[enemy.spriteNum];
                            break;
                    }
                }else {
                    switch (enemy.direction) {
                        case "up":
                            image = enemy.isAttacking ? enemy.attackUp[enemy.spriteNum] : enemy.walkUp[enemy.spriteNum];
                            break;
                        case "down":
                            image = enemy.isAttacking ? enemy.attackDown[enemy.spriteNum] : enemy.walkDown[enemy.spriteNum];
                            break;
                        case "left":
                            image = enemy.isAttacking ? enemy.attackLeft[enemy.spriteNum] : enemy.walkLeft[enemy.spriteNum];
                            break;
                        case "right":
                            image = enemy.isAttacking ? enemy.attackRight[enemy.spriteNum] : enemy.walkRight[enemy.spriteNum];
                            break;
                    }
                }
                // Draw enemy sprite
                g2.drawImage(image,
                        enemy.worldX + cameraX - (gp.tileSize),
                        enemy.worldY + cameraY - (gp.tileSize),
                        gp.tileSize * 3,
                        gp.tileSize * 3,
                        null
                );

                if(enemy.isAlive()) {
                    //health bar dimensions
                    int healthBarWidth = gp.tileSize;
                    int healthBarHeight = 10;
                    int x = enemy.worldX + cameraX;
                    int y = enemy.worldY + cameraY - healthBarHeight - 10;

                    //draw health bar background (empty bar) with 50% opacity
                    g2.setColor(new Color(128, 128, 128, 128)); // Gray with alpha
                    g2.fillRect(x, y, healthBarWidth, healthBarHeight);

                    //draw current health
                    double healthPercent = (double) enemy.currentHealth / enemy.maxHealth;
                    int currentHealthWidth = (int) (healthBarWidth * healthPercent);
                    g2.setColor(getHealthBarColor(healthPercent));
                    g2.fillRect(x, y, currentHealthWidth, healthBarHeight);

                    //draw border
                    g2.setColor(Color.BLACK);
                    g2.drawRect(x, y, healthBarWidth, healthBarHeight);
                }
            }
        }
    }
    public List<Enemy> getEnemies(){return enemies;}
    public void setCollisionChecker(Collisions collisionChecker) {
        this.collisionChecker = collisionChecker; // Update the manager's collision checker

        for (Enemy enemy : enemies) {
            enemy.collisionChecker = collisionChecker; // Directly update each enemy's collision checker
        }
    }

}
