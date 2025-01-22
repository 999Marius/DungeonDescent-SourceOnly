package enemies.types;

import collisions.Collisions;
import enemies.Enemy;
import main.GamePanel;

import java.awt.image.BufferedImage;

public class Ogre extends Enemy {

    //sprites
    private static final int SPRITE_SIZE = 48;
    private static final int FRAMES_PER_DIRECTION = 4;
    private static final int ANIMATION_SPEED = 10;

    //Stats
    private static final int OGRE_SPEED = 3;
    private static final int OGRE_MAX_HEALTH = 150;
    private static final int OGRE_DAMAGE = 10;
    private static final int OGRE_DEFENSE = 15;
    private static final int OGRE_VISION = 400;
    private static final int OGRE_DETECT = 150;

    public Ogre(GamePanel gp, Collisions collisionChecker, int x, int y, int initialHealth){
        super(gp, collisionChecker);
        configureSpriteSheet();
        setDefaultValues(x,y);
        if(initialHealth > 0) {
            currentHealth = initialHealth;
        }

        walkDown = new BufferedImage[animationFrames];
        walkLeft = new BufferedImage[animationFrames];
        walkRight = new BufferedImage[animationFrames];
        walkUp = new BufferedImage[animationFrames];


        attackDown = new BufferedImage[animationFrames];
        attackLeft = new BufferedImage[animationFrames];
        attackRight = new BufferedImage[animationFrames];
        attackUp = new BufferedImage[animationFrames];

        deathDown = new BufferedImage[animationFrames];
        deathLeft = new BufferedImage[animationFrames];
        deathRight = new BufferedImage[animationFrames];
        deathUp = new BufferedImage[animationFrames];

        loadSpritesByType("death", deathUp,  deathDown, deathLeft, deathRight);
        loadSpritesByType("walk", walkUp, walkDown, walkLeft, walkRight);
        loadSpritesByType("attack", attackUp, attackDown, attackLeft, attackRight);
    }

    @Override
    protected void setDefaultValues(int x, int y){
        worldX = x;
        worldY = y;

        attackRange = gp.tileSize * 2;

        enemyType = "OGRE";
        speed = OGRE_SPEED;
        direction = "down";
        currentHealth = maxHealth = 100;
        damage = OGRE_DAMAGE;
        defense = OGRE_DEFENSE;
        visionRange = OGRE_VISION;
        detectRange = OGRE_DETECT;
    }

    @Override
    protected void configureSpriteSheet(){
        spriteSize = SPRITE_SIZE;
        animationSpeed = ANIMATION_SPEED;
        animationFrames = FRAMES_PER_DIRECTION;
    }
    public void setCurrentHealth(int health) {
        this.currentHealth = health;
    }
}
