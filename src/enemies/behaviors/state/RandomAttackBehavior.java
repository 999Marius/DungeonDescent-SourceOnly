package enemies.behaviors.state;

import enemies.Enemy;
import java.util.Random;

public class RandomAttackBehavior implements IAttackBehavior{
    private Random random = new Random();
    private static final int ATTACK_CHANCE = 120;
    private static final int ATTACK_DURATION = 400;
    private static final int ATTACK_COOLDOWN = 1000;
    private boolean hasHit = false;

    public void update(Enemy enemy){
        if(!enemy.isAlive() || enemy.isInDeathAnimation){
            return;
        }
        if(enemy.isAttacking){
            //control attack animation with time
            long attackTime = System.currentTimeMillis() - enemy.lastAttackTime;
            int previousSprite = enemy.spriteNum;
            enemy.spriteNum = (int)((attackTime / (ATTACK_DURATION/4)));

            //only try to hit if we just entered frame 2 and haven't hit yet
            if(enemy.spriteNum == 2 && previousSprite != 2 && !hasHit){
                hasHit = true;
                enemy.attackPlayer();
            }

            if(attackTime >= ATTACK_DURATION){
                enemy.isAttacking = false;
                enemy.spriteNum = 0;
                hasHit = false;  //reset hit flag for next attack
            }
        } else {
            if(canAttack(enemy) &&
                    System.currentTimeMillis() - enemy.lastAttackTime >= ATTACK_COOLDOWN){
                startAttack(enemy);
            }
        }
    }
    public boolean canAttack(Enemy enemy){
        int distanceToPlayer = calculateDistanceToPlayer(enemy);
        return distanceToPlayer <= enemy.getAttackRange() &&
                random.nextInt(ATTACK_CHANCE) == 0;
    }

    private int calculateDistanceToPlayer(Enemy enemy){
        int dx = enemy.getGamePanel().getPlayer().worldX - enemy.worldX;
        int dy = enemy.getGamePanel().getPlayer().worldY - enemy.worldY;
        return (int)Math.sqrt(dx*dx + dy*dy);
    }

    @Override
    public void startAttack(Enemy enemy){

        enemy.isAttacking = true;
        enemy.lastAttackTime = System.currentTimeMillis();
    }
}