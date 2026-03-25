package app.data.scripts.game.enemies;

import java.util.HashMap;
import java.util.Map;

import app.data.scripts.Config;
import app.data.scripts.engine.collision.RectCollisionType;
import app.data.scripts.engine.entity.Gravity;
import app.data.scripts.engine.entity.Rect;

public class Enemy extends Rect {
    protected int health;

    protected HashMap<EnemyInput, Boolean> input = new HashMap<>(Map.ofEntries(
        Map.entry(EnemyInput.runRight, false),
        Map.entry(EnemyInput.runLeft, false),
        Map.entry(EnemyInput.attack, false),
        Map.entry(EnemyInput.hurt, false),
        Map.entry(EnemyInput.stun, false)
    ));


    protected Gravity gravity = new Gravity(Config.MAX_GRAVITY, this, (rect, g) -> {
        if (((Rect)rect).getCollisionType(RectCollisionType.BOTTOM)) {
            g.zeroGravity();
        }
    });

    protected Boolean flip = false;
    protected double dt;
    protected int damage = 0;

    public Enemy(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public void setInput(EnemyInput tp, Boolean bool) {
        if (input.containsKey(tp) == false) {
            throw new IllegalArgumentException("The status " + tp + " does not exist.");
        }
        input.put(tp, bool);
    }

    public Boolean getInput(EnemyInput tp) {
        if (input.containsKey(tp) == false) {
            throw new IllegalArgumentException("The status " + tp + " does not exist.");
        }
        return input.get(tp);
    }

    public void setFlip(Boolean val) {
        flip = val;
    }

    public void setHealth(int val) {
        health = Math.max(0, val);
    }

    public int getHealth() {
        return health;
    }

    public boolean getFlip(){
        return flip;
    }

    public void setFlip(){
        flip = !flip;
    }

    public void setDamage(int val) {
        damage = val;
    }

    public int getDamage() {
        int res = damage;
        damage = 0;
        return res;
    }
}
