package enemies.behaviors.state.states;

import enemies.Enemy;
import enemies.behaviors.state.IEnemyState;
import main.GamePanel;

public class ChaseState implements IEnemyState {
    private final GamePanel gp;

    public ChaseState(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void enter(Enemy enemy) {}

    @Override
    public void update(Enemy enemy) {
        int dx = gp.getPlayer().worldX - enemy.worldX;
        int dy = gp.getPlayer().worldY - enemy.worldY;

        if(Math.abs(dx) > Math.abs(dy)) {
            enemy.direction = dx > 0 ? "right" : "left";
        } else {
            enemy.direction = dy > 0 ? "down" : "up";
        }
        moveInCurrentDirection(enemy);
    }

    @Override
    public IEnemyState checkTransition(Enemy enemy) {
        int distanceToPlayer = calculateDistanceToPlayer(enemy);

        if(distanceToPlayer <= enemy.getAttackRange() / 2) {
            return new AttackState(gp);
        } else if(distanceToPlayer > enemy.getVisionRange()) {
            return new PatrolState(gp);
        }
        return this;
    }

    @Override
    public void exit(Enemy enemy) {}
    private void moveInCurrentDirection(Enemy enemy){
        // chase if in vision range
        int distanceToPlayer = calculateDistanceToPlayer(enemy);
        if(distanceToPlayer > enemy.getVisionRange()) return;

        int dx = gp.getPlayer().worldX - enemy.worldX;
        int dy = gp.getPlayer().worldY - enemy.worldY;


        //for directional movement
        double length = Math.sqrt(dx*dx + dy*dy);
        int moveX = (int)(dx/length * enemy.speed);
        int moveY = (int)(dy/length * enemy.speed);

        if(enemy.getCollisionChecker().canMoveEnemy(enemy.worldX + moveX, enemy.worldY + moveY, enemy.direction)){
            enemy.worldX += moveX;
            enemy.worldY += moveY;
        }
        //took 10 hgours
    }

    private int calculateDistanceToPlayer(Enemy enemy) {
        int dx = gp.getPlayer().worldX - enemy.worldX;
        int dy = gp.getPlayer().worldY - enemy.worldY;
        return (int)Math.sqrt(dx * dx + dy * dy);
    }
}
