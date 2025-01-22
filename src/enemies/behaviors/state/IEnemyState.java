package enemies.behaviors.state;

import enemies.Enemy;
import main.GamePanel;

public interface IEnemyState {
    void enter(Enemy enemy);
    void update(Enemy enemy);
    void exit(Enemy enemy);
    IEnemyState checkTransition(Enemy enemy);
}