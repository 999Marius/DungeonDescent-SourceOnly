package enemies.behaviors.state;
import enemies.Enemy;

public interface IEnemyBehavior{
    void update(Enemy enemy);
    void onSpawn();
    void onCollision();
}
