package app.data.scripts.game.player;

import app.data.scripts.engine.collision.HandleCollision;
import app.data.scripts.engine.collision.RectCollisionType;
import app.data.scripts.engine.collision.RectToRect;
import app.data.scripts.engine.entity.UpdateStrategy;
import app.data.scripts.game.combat.HitBox;
import app.data.scripts.game.combat.HitBoxType;
import app.data.scripts.game.enemies.Enemy;
import app.data.scripts.game.enemies.EnemyGroupManager;
import app.data.scripts.game.enemies.EnemyInput;
import app.data.scripts.game.map.Tile;
import app.data.scripts.game.map.TileGroupManager;

public class PlayerUpdateStrategy extends UpdateStrategy {
    private TileGroupManager gameMap;
    private EnemyGroupManager enemies;
    private double dt;

    public PlayerUpdateStrategy(Player self, TileGroupManager gameMap, EnemyGroupManager enemies, double dt) {
        setSelf(self);
        this.gameMap = gameMap;
        this.enemies = enemies;
        this.dt = dt;
    }

    public double getDt() {
        return dt;
    }

    @Override
    public void perform() {
        //System.out.println("In upd: " + self.speed.getYSpeed() + " " + self.speed.yAcceleration);
        Player player = (Player)self;
        player.speed.setXSpeed();
        player.speed.setYSpeed();
        //System.out.println("In upd: " + player.speed.getYSpeed() + " " + player.speed.yAcceleration);

        player.initCollisionType();

        player.setX(player.getX() + player.speed.getXSpeed() * dt);
        double right = Math.min(gameMap.getMapWidth(), player.getX() + gameMap.getChuckSize());
        double left = Math.max(0, player.getX() - gameMap.getChuckSize());
        HandleCollision handleX = (pEntity, tileEntity) -> {
            Player p = (Player)pEntity;
            Tile tile = (Tile)tileEntity;
            if (p.speed.getXSpeed() > 0) {
                p.setRight(tile.getLeft());
                p.setCollisionType(RectCollisionType.RIGHT, true);
            }else if (p.speed.getXSpeed() < 0) {
                p.setLeft(tile.getRight());
                p.setCollisionType(RectCollisionType.LEFT, true);
            }
        };
        RectToRect.checkCollision(player, gameMap.getCurrentChuckGroup(player.getX(), player.getY()), handleX);
        RectToRect.checkCollision(player, gameMap.getCurrentChuckGroup(right, player.getY()), handleX);
        RectToRect.checkCollision(player, gameMap.getCurrentChuckGroup(left, player.getY()), handleX);

        player.setY(player.getY() + player.speed.getYSpeed() * dt);
        double top = Math.min(0, player.getY() - gameMap.getChuckSize());
        double bottom = Math.max(gameMap.getMapHeight(), player.getY() + gameMap.getChuckSize());
        HandleCollision handleY = (pEntity, tileEntity) -> {
            Player p = (Player)pEntity;
            Tile tile = (Tile)tileEntity;
            if (p.speed.getYSpeed() < 0) {
                p.setTop(tile.getBottom());
                p.setCollisionType(RectCollisionType.TOP, true);
            }else if (p.speed.getYSpeed() > 0) {
                p.setBottom(tile.getTop());
                p.setCollisionType(RectCollisionType.BOTTOM, true);
            }
        };
        RectToRect.checkCollision(player, gameMap.getCurrentChuckGroup(player.getX(), player.getY()), handleY);
        RectToRect.checkCollision(player, gameMap.getCurrentChuckGroup(player.getX(), top), handleY);
        RectToRect.checkCollision(player, gameMap.getCurrentChuckGroup(player.getX(), bottom), handleY);

        if (player.getLeft() < 0) {
            player.setLeft(0);
        }else if (player.getRight() > gameMap.getMapWidth()) {
            player.setRight(gameMap.getMapWidth());
        }
        if (player.getTop() < 0) {
            player.setTop(0);
        }else if (player.getBottom() > gameMap.getMapHeight()) {
            player.setBottom(gameMap.getMapHeight());
        }

        HandleCollision handleHurtEnemy = (pEntity, eEntity) -> {
            HitBox p = (HitBox)pEntity;
            Enemy enemy = (Enemy)eEntity;
            //System.out.println("before: " + enemy.fsm.getCurrentState() + " " + enemy.getInput(EnemyInput.hurt));
            if (enemy.getInput(EnemyInput.hurt) == false) {
                enemy.setInput(EnemyInput.hurt, true);
                enemy.setDamage(p.getDamage());
            }
            //System.out.println("after: " + enemy.fsm.getCurrentState() + " " + enemy.getInput(EnemyInput.hurt));
        };
        HandleCollision handleStunEnemy = (pEntity, eEntity) -> {
            //HitBox p = (HitBox)pEntity;
            Enemy enemy = (Enemy)eEntity;
            if (enemy.getInput(EnemyInput.stun) == false) {
                enemy.setInput(EnemyInput.stun, true);
            }
        };
        for (var hitbox : player.skillHitBox.values()) {
            if (hitbox.isActive() == false) continue;
            //System.out.println("hitbox check collision: " + hitbox.getHitBoxType() + " " + hitbox.getX() + " " + hitbox.getY() + " " + hitbox.getWidth() + " " + hitbox.getHeight());
            right = Math.min(gameMap.getMapWidth(), hitbox.getX() + gameMap.getChuckSize());
            left = Math.max(0, hitbox.getX() - gameMap.getChuckSize());
            top = Math.min(0, hitbox.getY() - gameMap.getChuckSize());
            bottom = Math.max(gameMap.getMapHeight(), hitbox.getY() + gameMap.getChuckSize());
            
            if (hitbox.getHitBoxType() == HitBoxType.AttackBox) {
                //System.out.println("YESSS");
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(hitbox.getX(), hitbox.getY()), handleHurtEnemy);
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(hitbox.getX(), top), handleHurtEnemy);
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(hitbox.getX(), bottom), handleHurtEnemy);

                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(right, hitbox.getY()), handleHurtEnemy);
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(right, top), handleHurtEnemy);
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(right, bottom), handleHurtEnemy);
                
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(left, hitbox.getY()), handleHurtEnemy);
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(left, top), handleHurtEnemy);
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(left, bottom), handleHurtEnemy);
            }else {
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(hitbox.getX(), hitbox.getY()), handleStunEnemy);
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(hitbox.getX(), top), handleStunEnemy);
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(hitbox.getX(), bottom), handleStunEnemy);

                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(right, hitbox.getY()), handleStunEnemy);
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(right, top), handleStunEnemy);
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(right, bottom), handleStunEnemy);
                
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(left, hitbox.getY()), handleStunEnemy);
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(left, top), handleStunEnemy);
                RectToRect.checkCollision(hitbox, enemies.getCurrentChuckGroup(left, bottom), handleStunEnemy);
            }  
        }
    }
    
}
