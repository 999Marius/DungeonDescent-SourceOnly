package entity;

import enemies.Enemy;
import enemies.EnemyManager;
import main.GamePanel;
import main.KeyHandler;
import collisions.Collisions;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyH;

    public Collisions collisionChecker;
    EnemyManager enemyManager;


    public Player(GamePanel gp, KeyHandler keyH, Collisions collisionChecker, EnemyManager enemyManager){
        this.gp = gp;
        this.keyH = keyH;
        this.collisionChecker = collisionChecker;
        this.enemyManager = enemyManager;


        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues(){
        // Starting world position
        worldX = 1280;
        worldY = 1024;
        speed = 4;
        direction = "down";

        //health
        maxHealth = currentHealth = 100;
    }

    //loads the sprites for player, each are stored in different folders and not a sprite sheet, individual
    public void getPlayerImage(){
        try{
            up = new BufferedImage[WALK_FRAMES];
            down = new BufferedImage[WALK_FRAMES];
            left = new BufferedImage[WALK_FRAMES];
            right = new BufferedImage[WALK_FRAMES];

            attackUp = new BufferedImage[ATTACK_FRAMES];
            attackDown = new BufferedImage[ATTACK_FRAMES];
            attackLeft = new BufferedImage[ATTACK_FRAMES];
            attackRight = new BufferedImage[ATTACK_FRAMES];

            idleUp = new BufferedImage[IDLE_FRAMES];
            idleDown = new BufferedImage[IDLE_FRAMES];
            idleLeft = new BufferedImage[IDLE_FRAMES];
            idleRight = new BufferedImage[IDLE_FRAMES];

            for(int i = 0; i < WALK_FRAMES; i++){
                up[i] = ImageIO.read(new File("res/player/sprites/up/frame" + i + ".png"));
                down[i] = ImageIO.read(new File("res/player/sprites/down/frame" + i + ".png"));
                left[i] = ImageIO.read(new File("res/player/sprites/left/frame" + i + ".png"));
                right[i] = ImageIO.read(new File("res/player/sprites/right/frame" + i + ".png"));
            }

            for(int i = 0; i < ATTACK_FRAMES; i++){
                attackUp[i] = ImageIO.read(new File("res/player/sprites/attacks/up/frame" + i + ".png"));
                attackDown[i] = ImageIO.read(new File("res/player/sprites/attacks/down/frame" + i + ".png"));
                attackLeft[i] = ImageIO.read(new File("res/player/sprites/attacks/left/frame" + i + ".png"));
                attackRight[i] = ImageIO.read(new File("res/player/sprites/attacks/right/frame" + i + ".png"));
            }

            for(int i = 0; i < IDLE_FRAMES; i++){
                idleUp[i] = ImageIO.read(new File("res/player/sprites/idle/up/frame" + i + ".png"));
                idleDown[i] = ImageIO.read(new File("res/player/sprites/idle/down/frame" + i + ".png"));
                idleLeft[i] = ImageIO.read(new File("res/player/sprites/idle/left/frame" + i + ".png"));
                idleRight[i] = ImageIO.read(new File("res/player/sprites/idle/right/frame" + i + ".png"));
            }

            heartIcon = ImageIO.read(new File("res/misc/heart.png"));

        }catch(Exception e){
            JOptionPane.showMessageDialog(null,
                    "Failed to load player resources: " + e.getMessage(),
                    "Resource Loading Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void update() {
        //handle attack state first
        if(currentState == PlayerState.ATTACKING){
            updateAttackAnimation();
            return;  // this shit does not allow movement when attacking
        }

        //handle movement animations
        if(keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed){
            handleMovement();
            currentState = PlayerState.WALKING;
        }else{
            //not moving nor attacking = idle
            currentState = PlayerState.IDLE;
        }

        // checks for attack input
        if(keyH.spacePressed && canAttack()){
            startAttack();
        }
        if(keyH.hPressed){
            testDamage();
        }

        //handle idle animations
        if(currentState == PlayerState.IDLE){
            spriteCounter++;
            if(spriteCounter > IDLE_ANIMATION_SPEED){
                spriteNum++;
                if (spriteNum >= IDLE_FRAMES){
                    spriteNum = 0;
                }
                spriteCounter = 0;
            }
        }
    }

    private void handleMovement(){
        if(keyH.upPressed){
            direction = "up";
            int nextWorldY = worldY - speed;
            if(collisionChecker.canMove(worldX, nextWorldY, direction)){
                worldY = nextWorldY;
            }
        }
       if(keyH.downPressed){
            direction = "down";
            int nextWorldY = worldY + speed;
            if (collisionChecker.canMove(worldX, nextWorldY, direction)){
                worldY = nextWorldY;
            }
        }
        if(keyH.leftPressed){
            direction = "left";
            int nextWorldX = worldX - speed;
            if (collisionChecker.canMove(nextWorldX, worldY, direction)){
                worldX = nextWorldX;
            }
        }
        if(keyH.rightPressed){
            direction = "right";
            int nextWorldX = worldX + speed;
            if (collisionChecker.canMove(nextWorldX, worldY, direction)){
                worldX = nextWorldX;
            }
        }
        // Update walking animation
        spriteCounter++;
        if(spriteCounter > 10){
            spriteNum++;
            if(spriteNum >= WALK_FRAMES){
                spriteNum = 0;
            }
            spriteCounter = 0;
        }
    }

    private void updateAttackAnimation(){
        spriteCounter++;
        if(spriteCounter > ATTACK_ANIMATION_SPEED){
            spriteNum++;
            if(spriteNum == 2) {
                for (Enemy enemy : enemyManager.getEnemies()) {
                    if (canHitEnemy(enemy)) {
                        enemy.takeDamage(ATTACK_DAMAGE);
                    }
                }
            }
            if(spriteNum >= ATTACK_FRAMES){
                // attack animation finished
                spriteNum = 0;
                currentState = PlayerState.IDLE;
                isAttacking = false;
            }
            spriteCounter = 0;
        }
    }

    private void startAttack(){
        currentState = PlayerState.ATTACKING;
        isAttacking = true;
        spriteNum = 0;
        lastAttackTime = System.currentTimeMillis();
    }

    private boolean canAttack(){
        long currentTime = System.currentTimeMillis();
        return !isAttacking && currentTime - lastAttackTime >= ATTACK_COOLDOWN;
    }

    public void drawHealthBar(Graphics2D g2){
        int pixelSize = 12;
        int barLength = 10;

        for(int i = 0; i < barLength; i++){
            //background pixels
            g2.setColor(Color.GRAY);
            g2.fillRect(64 + (i * pixelSize), gp.screenHeight - 35, pixelSize-1, pixelSize-1);

            // health pixels
            if(i < barLength * ((double)currentHealth/maxHealth)){
                g2.setColor(Color.RED);
                g2.fillRect(64 + (i * pixelSize), gp.screenHeight - 35, pixelSize-1, pixelSize-1);
            }
            g2.drawImage(heartIcon, 10, gp.screenHeight -54, pixelSize * 4, pixelSize * 4, null);
        }
    }
    public void testDamage(){
        takeDamage(10); //reduces health by 10
    }

    public void takeDamage(int damage){
        currentHealth = Math.max(0, currentHealth - damage);
        isHurt = true;
        hurtStartTime = System.currentTimeMillis();
    }



    private boolean canHitEnemy(Enemy enemy){
        // Get centers of player and enemy
        int playerCenterX = worldX + gp.tileSize/2;
        int playerCenterY = worldY + gp.tileSize/2;
        int enemyCenterX = enemy.worldX + gp.tileSize/2;
        int enemyCenterY = enemy.worldY + gp.tileSize/2;


        // Calculate distance
        double distance = Math.sqrt(
                Math.pow(enemyCenterX - playerCenterX, 2) +
                        Math.pow(enemyCenterY - playerCenterY, 2)
        );

        // Check if enemy is within range
        int ATTACK_RANGE = gp.tileSize * 2;
        if(distance > ATTACK_RANGE){
            return false;
        }

        // Check if enemy is in attack direction
        switch(direction){
            case "right": return enemyCenterX > playerCenterX;
            case "left": return enemyCenterX < playerCenterX;
            case "up": return enemyCenterY < playerCenterY;
            case "down": return enemyCenterY > playerCenterY;
        }
        return false;
    }

    public void draw(Graphics2D g2, int cameraX, int cameraY){
        BufferedImage image = null;

        //flash red when took damage
        if(isHurt && System.currentTimeMillis() - hurtStartTime < HURT_DURATION){
            g2.setXORMode(damage);
        }

        // select the sprite based on state and direction
        if(currentState == PlayerState.ATTACKING){
            switch (direction){
                case "up": image = attackUp[spriteNum]; break;
                case "down": image = attackDown[spriteNum]; break;
                case "left": image = attackLeft[spriteNum]; break;
                case "right": image = attackRight[spriteNum]; break;
            }
        }else if(currentState == PlayerState.WALKING){
            switch(direction){
                case "up": image = up[spriteNum]; break;
                case "down": image = down[spriteNum]; break;
                case "left": image = left[spriteNum]; break;
                case "right": image = right[spriteNum]; break;
            }
        }else{
            switch(direction){
                case "up": image = idleUp[spriteNum]; break;
                case "down": image = idleDown[spriteNum]; break;
                case "left": image = idleLeft[spriteNum]; break;
                case "right": image = idleRight[spriteNum]; break;
            }
        }

        if(currentState == PlayerState.WALKING){
            g2.drawImage(image,
                    worldX + cameraX,
                    worldY + cameraY,
                    gp.tileSize,
                    gp.tileSize,
                    null
            );
        }else{
            //yeah the problem is for attack and idle the sprite frames are 64x65 with a bunch of png space so we need to render them differently when we scale them to be the same
            int attackScale = 4;
            int offsetX = (gp.tileSize * (attackScale - 1)) / 2;
            int offsetY = (gp.tileSize * (attackScale - 1)) / 2;
            g2.drawImage(image,
                    worldX + cameraX - offsetX,
                    worldY + cameraY - offsetY,
                    gp.tileSize * attackScale,
                    gp.tileSize * attackScale,
                    null
            );
        }
        g2.setPaintMode();
        if(isHurt && System.currentTimeMillis() - hurtStartTime >= HURT_DURATION) {
            isHurt = false;
        }
    }

    public void setCollisionChecker(Collisions collisionChecker) {
        this.collisionChecker = collisionChecker;
    }
    public int getCurrentHealth(){return currentHealth;}

    public void setCurrentHealth(int health) {
        this.currentHealth = health;
    }
    public boolean isDead(){
        return this.currentHealth <= 0;
    }

    public void setHealth(int i)
    {
        this.currentHealth = i;
    }
}