package enemies.behaviors.state.states;

import enemies.Enemy;
import enemies.behaviors.state.IEnemyBehavior;
import enemies.behaviors.state.IEnemyState;
import main.GamePanel;

public class StateMachineBehavior implements IEnemyBehavior {
    private IEnemyState currentState;
    private final GamePanel gp;

    public StateMachineBehavior(GamePanel gp) {
        this.gp = gp;
        this.currentState = new PatrolState(gp);
    }

    @Override
    public void update(Enemy enemy) {
        if(enemy.isInDeathAnimation){
            updateAnimation(enemy);
            return;
        }
        IEnemyState newState = currentState.checkTransition(enemy);
        if(newState != currentState) {
            currentState.exit(enemy);
            currentState = newState;
            currentState.enter(enemy);
        }

        currentState.update(enemy);
        updateAnimation(enemy);
    }

    // StateMachineBehavior.java
    private void updateAnimation(Enemy enemy){

        if(enemy.isInDeathAnimation){
            enemy.spriteCounter++;
            if(enemy.spriteCounter > enemy.getAnimationSpeed() ){
                enemy.spriteCounter = 0;

                if(enemy.spriteNum < 4 - 1){
                    enemy.spriteNum++;
                }
            }
        }

        else if(enemy.isAttacking){
            enemy.spriteCounter++;
            if(enemy.spriteCounter > enemy.getAnimationSpeed()){
                enemy.spriteCounter = 0;
                enemy.spriteNum++;

                // reset animation ifgone through all frames
                if(enemy.spriteNum >= enemy.getAnimationFrames()){
                    enemy.spriteNum = 0;
                }
            }
        }else {
            enemy.spriteCounter++;
            if(enemy.spriteCounter > enemy.getAnimationSpeed()){
                enemy.spriteNum = (enemy.spriteNum + 1) % enemy.getAnimationFrames();
                enemy.spriteCounter = 0;
            }
        }
    }

    @Override
    public void onSpawn() {}

    @Override
    public void onCollision() {}
}