package enemies.behaviors.state;

import enemies.Enemy;

public interface IAttackBehavior{
    void update(Enemy enemy);
    boolean canAttack(Enemy enemy);
    void startAttack(Enemy enemy);
}