package enemies;

import collisions.Collisions;

import enemies.behaviors.state.IAttackBehavior;
import enemies.behaviors.state.IEnemyBehavior;
import enemies.behaviors.state.states.StateMachineBehavior;
import entity.Entity;
import entity.Player;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

public abstract class Enemy extends Entity {
    protected int maxHealth;
    protected int currentHealth;
    protected GamePanel gp;
    Collisions collisionChecker;


    //sprite configuration
    protected String spritePath;
    protected int spriteSize;
    protected int animationFrames;
    protected int animationSpeed;
    protected int DOWN_ROW = 0;
    protected int LEFT_ROW = 1;
    protected int RIGHT_ROW = 2;
    protected int UP_ROW = 3;


    //combat
    protected int damage;
    protected int defense;
    protected int attackRange;

    //movement
    protected int visionRange;
    protected int detectRange;
    protected String enemyType;

    //sprites
    protected BufferedImage[] walkUp, walkDown, walkLeft, walkRight;
    protected BufferedImage[] attackUp, attackDown, attackLeft, attackRight;
    protected BufferedImage[] idleUp, idleDown, idleLeft, idleRight;
    protected BufferedImage[] deathUp, deathDown, deathLeft, deathRight;
    protected BufferedImage[] hitUp, hitDown, hitLeft, hitRight;

    //death
    private static final long CORPSE_DURATION = 60 * 1000;
    public boolean isInDeathAnimation = false;


    protected IAttackBehavior attackBehavior;
    protected IEnemyBehavior behavior;

    public Enemy(GamePanel gp, Collisions collisionChecker){
        this.gp = gp;
        this.collisionChecker = collisionChecker;
        this.behavior = new StateMachineBehavior(gp);
    }

    protected abstract void setDefaultValues(int x, int y);
    protected abstract void configureSpriteSheet();


    protected void loadSpritesByType(String type, BufferedImage[] up, BufferedImage[] down, BufferedImage[] left, BufferedImage[] right){
        try {
            String path = "res/enemies/" + enemyType.toLowerCase() + "/" + type + "/"
                    + enemyType.toLowerCase() + " - " + type + ".png";
            BufferedImage spriteSheet = ImageIO.read(new File(path));

            for(int col = 0; col < animationFrames; col++){
                down[col] = spriteSheet.getSubimage(col * spriteSize, DOWN_ROW * spriteSize, spriteSize, spriteSize);
                left[col] = spriteSheet.getSubimage(col * spriteSize, LEFT_ROW * spriteSize, spriteSize, spriteSize);
                right[col] = spriteSheet.getSubimage(col * spriteSize, RIGHT_ROW * spriteSize, spriteSize, spriteSize);
                up[col] = spriteSheet.getSubimage(col * spriteSize, UP_ROW * spriteSize, spriteSize, spriteSize);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    public boolean attackPlayer(){
        if(isAttacking && spriteNum == 2){  // Check specific attack frame

            if(!isAlive() || isInDeathAnimation){
                return false;
            }

            int enemyCenterX = worldX + gp.tileSize/2;
            int enemyCenterY = worldY + gp.tileSize/2;
            int playerCenterX = gp.getPlayer().worldX + gp.tileSize/2;
            int playerCenterY = gp.getPlayer().worldY + gp.tileSize/2;

            double distance = Math.sqrt(
                    Math.pow(playerCenterX - enemyCenterX, 2) +
                            Math.pow(playerCenterY - enemyCenterY, 2)
            );

            if(distance <= attackRange){
                gp.getPlayer().takeDamage(damage);
                return true;
            }
        }
        return false;
    }


    public void update(){
        if(isAlive()){
            behavior.update(this);
        }
    }

    public void setAttackBehavior(IAttackBehavior attackBehavior) {
        this.attackBehavior = attackBehavior;
    }

    public void takeDamage(int damage){
        currentHealth = Math.max(0, currentHealth - damage);
        if(currentHealth <= 0 && !isInDeathAnimation){
            startDeathAnimation();
        }
    }

    private void startDeathAnimation() {
        isInDeathAnimation = true;
        spriteNum = 0;
        gp.killTracker.incrementKills(gp.mapManager.getCurrentMap().getLevelNumber());
    }


    public boolean isAlive() {
        return currentHealth > 0;
    }
    public boolean isInDeathAnimation() {
        return isInDeathAnimation;
    }

    public int getDamage() { return damage; }
    public int getDefense() { return defense; }
    public int getDetectRange() { return detectRange; }
    public int getVisionRange() { return visionRange; }
    public String getEnemyType() { return enemyType; }
    public Collisions getCollisionChecker() { return collisionChecker; }
    public int getAnimationFrames(){return animationFrames;}
    public int getAnimationSpeed(){return animationSpeed;}
    public GamePanel getGamePanel(){return gp;}
    public int getAttackRange(){return attackRange;}
    public IAttackBehavior getAttackBehavior(){ return attackBehavior;}

    public void setBehavior(StateMachineBehavior stateMachineBehavior) {
        this.behavior = stateMachineBehavior;
    }
    public IEnemyBehavior getBehavior(){
        return behavior;
    }
    public void setCollisionChecker(Collisions collisionChecker) {
        this.collisionChecker = collisionChecker;
    }

    public int getCurrentHealth() {
         return currentHealth;
    }

    public void setCurrentHealth(int health) {
        this.currentHealth = health;
    }
}
