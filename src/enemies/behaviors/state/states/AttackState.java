package enemies.behaviors.state.states;

import enemies.Enemy;
import enemies.behaviors.state.IEnemyState;
import main.GamePanel;

public class AttackState implements IEnemyState {
    private final GamePanel gp;

    public AttackState(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void enter(Enemy enemy) {}

    @Override
    public void update(Enemy enemy){

        /*
        dx will be positive if player is to the right
        dx will be negative if player is to the left
        dy will be positive if player is below
        dy will be negative if player is above
        * */
        int dx = gp.getPlayer().worldX - enemy.worldX;
        int dy = gp.getPlayer().worldY - enemy.worldY;

        if(Math.abs(dx) > Math.abs(dy)){
            enemy.direction = dx > 0 ? "right" : "left";
        } else {
            enemy.direction = dy > 0 ? "down" : "up";
        }

        // Handle attack behavior
        if(enemy.getAttackBehavior() != null){
            enemy.getAttackBehavior().update(enemy);
        }
    }

    @Override
    public IEnemyState checkTransition(Enemy enemy) {
        int distanceToPlayer = calculateDistanceToPlayer(enemy);

        if(distanceToPlayer > enemy.getAttackRange()) {
            return new ChaseState(gp);
        }
        return this;
    }

    @Override
    public void exit(Enemy enemy) {}

    private int calculateDistanceToPlayer(Enemy enemy) {
        int dx = gp.getPlayer().worldX - enemy.worldX;
        int dy = gp.getPlayer().worldY - enemy.worldY;
        return (int)Math.sqrt(dx * dx + dy * dy);
    }
}