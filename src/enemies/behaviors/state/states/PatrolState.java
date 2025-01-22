package enemies.behaviors.state.states;

import enemies.Enemy;
import enemies.behaviors.state.IEnemyState;
import main.GamePanel;
import java.util.Random;

public class PatrolState implements IEnemyState {
    private final GamePanel gp;
    private int patrolTimer;
    private final int PATROL_DURATION = 120;
    private final Random random = new Random();

    public PatrolState(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void enter(Enemy enemy) {
        patrolTimer = 0;
    }

    @Override
    public void update(Enemy enemy) {
        patrolTimer++;
        if(patrolTimer >= PATROL_DURATION) {
            String[] directions = {"up", "down", "left", "right"};
            enemy.direction = directions[random.nextInt(directions.length)];
            patrolTimer = 0;
        }
        moveInCurrentDirection(enemy);
    }

    @Override
    public IEnemyState checkTransition(Enemy enemy) {
        int distanceToPlayer = calculateDistanceToPlayer(enemy);
        if(distanceToPlayer <= enemy.getDetectRange()) {
            return new ChaseState(gp);
        }
        return this;
    }

    @Override
    public void exit(Enemy enemy) {}

    private void moveInCurrentDirection(Enemy enemy) {
        int nextX = enemy.worldX;
        int nextY = enemy.worldY;

        switch(enemy.direction) {
            case "up" -> nextY -= enemy.speed;
            case "down" -> nextY += enemy.speed;
            case "left" -> nextX -= enemy.speed;
            case "right" -> nextX += enemy.speed;
        }

        if(enemy.getCollisionChecker().canMoveEnemy(nextX, nextY, enemy.direction)) {
            enemy.worldX = nextX;
            enemy.worldY = nextY;
        }
    }

    private int calculateDistanceToPlayer(Enemy enemy) {
        int dx = gp.getPlayer().worldX - enemy.worldX;
        int dy = gp.getPlayer().worldY - enemy.worldY;
        return (int)Math.sqrt(dx * dx + dy * dy);
    }
}