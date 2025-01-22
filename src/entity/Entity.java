package entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {
    public int worldX, worldY;
    public int speed;
    public boolean collisionOn = false;

    public Rectangle solidArea;
    public int solidAreaDefaultX;
    public int solidAreaDefaultY;
    // wwalk
    public BufferedImage[] up;
    public BufferedImage[] down;
    public BufferedImage[] left;
    public BufferedImage[] right;


    protected enum PlayerState {
        IDLE,
        WALKING,
        ATTACKING
    }

    protected PlayerState currentState = PlayerState.IDLE;

    // Attack animation arrays
    protected BufferedImage[] attackUp;
    protected BufferedImage[] attackDown;
    protected BufferedImage[] attackLeft;
    protected BufferedImage[] attackRight;

    // Animation configuration
    protected static final int WALK_FRAMES = 6;
    protected static final int ATTACK_FRAMES = 4;
    protected static final int ATTACK_ANIMATION_SPEED = 8;

    //idle
    protected BufferedImage[] idleUp;
    protected BufferedImage[] idleDown;
    protected BufferedImage[] idleLeft;
    protected BufferedImage[] idleRight;

    protected static final int IDLE_FRAMES = 6;
    protected static final int IDLE_ANIMATION_SPEED = 8;
    public String direction;
    public int spriteNum = 0;
    public int spriteCounter = 0;


    //combat
    public boolean isAttacking;
    public long lastAttackTime;
    protected static final int ATTACK_COOLDOWN = 800;
    protected int attackDamage;
    protected int attackRange;
    protected static final int ATTACK_DURATION = 400;
    protected static final int ATTACK_DAMAGE = 20;



    //health
    protected int maxHealth = 100;
    protected int currentHealth = maxHealth;
    protected BufferedImage heartIcon;
    protected boolean isHurt = false;
    protected long hurtStartTime = 0;
    protected static final long HURT_DURATION = 50;
    protected Color damage = new Color(255, 0, 0, 180).darker();

}