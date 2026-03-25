package app.data.scripts.game.enemies;

import app.data.scripts.engine.collision.HandleCollision;
import app.data.scripts.engine.collision.RectCollisionType;
import app.data.scripts.engine.collision.RectToRect;
import app.data.scripts.game.combat.HitBox;
import app.data.scripts.game.map.Tile;
import app.data.scripts.game.map.TileGroupManager;
import app.data.scripts.game.player.Player;

public class SwordsmanUpdateStrategy extends EnemyUpdateStrategy {
    public SwordsmanUpdateStrategy(Player player, TileGroupManager gameMap, double dt) {
        super(player, gameMap, dt);
    }

    @Override
    public void perform() {
        //System.out.println("In upd: " + player.speed.getYSpeed() + " " + player.speed.yAcceleration);
        Swordsman swordsman = (Swordsman)self;
        HitBox hitbox = swordsman.atkHitBox;

        //follow player
        if (swordsman.fsm.getCurrentState() == EnemyState.run) {
            if (player.getX() < swordsman.getX()) {
                swordsman.setInput(EnemyInput.runLeft, true);
            }else {
                swordsman.setInput(EnemyInput.runRight, true);
            }
        }

        swordsman.speed.setXSpeed();
        swordsman.speed.setYSpeed();
        //System.out.println("In upd: " + swordsman.speed.getXSpeed() + " " + swordsman.speed.xAcceleration);

        swordsman.initCollisionType();

        swordsman.setX(swordsman.getX() + swordsman.speed.getXSpeed() * dt);
        double right = Math.min(gameMap.getMapWidth(), swordsman.getX() + gameMap.getChuckSize());
        double left = Math.max(0, swordsman.getX() - gameMap.getChuckSize());
        HandleCollision handleX = (sEntity, tileEntity) -> {
            Swordsman s = (Swordsman)sEntity;
            Tile tile = (Tile)tileEntity;
            if (s.speed.getXSpeed() > 0) {
                s.setRight(tile.getLeft());
                s.setCollisionType(RectCollisionType.RIGHT, true);
            }else if (s.speed.getXSpeed() < 0) {
                s.setLeft(tile.getRight());
                s.setCollisionType(RectCollisionType.LEFT, true);
            }
        };
        RectToRect.checkCollision(swordsman, gameMap.getCurrentChuckGroup(swordsman.getX(), swordsman.getY()), handleX);
        RectToRect.checkCollision(swordsman, gameMap.getCurrentChuckGroup(right, swordsman.getY()), handleX);
        RectToRect.checkCollision(swordsman, gameMap.getCurrentChuckGroup(left, swordsman.getY()), handleX);

        swordsman.setY(swordsman.getY() + swordsman.speed.getYSpeed() * dt);
        double top = Math.min(0, swordsman.getY() - gameMap.getChuckSize());
        double bottom = Math.max(gameMap.getMapHeight(), swordsman.getY() + gameMap.getChuckSize());
        HandleCollision handleY = (sEntity, tileEntity) -> {
            Swordsman s = (Swordsman)sEntity;
            Tile tile = (Tile)tileEntity;
            if (s.speed.getYSpeed() < 0) {
                s.setTop(tile.getBottom());
                s.setCollisionType(RectCollisionType.TOP, true);
            }else if (s.speed.getYSpeed() > 0) {
                s.setBottom(tile.getTop());
                s.setCollisionType(RectCollisionType.BOTTOM, true);
            }
        };
        RectToRect.checkCollision(swordsman, gameMap.getCurrentChuckGroup(swordsman.getX(), swordsman.getY()), handleY);
        RectToRect.checkCollision(swordsman, gameMap.getCurrentChuckGroup(swordsman.getX(), top), handleY);
        RectToRect.checkCollision(swordsman, gameMap.getCurrentChuckGroup(swordsman.getX(), bottom), handleY);

        if (swordsman.getLeft() < 0) {
            swordsman.setLeft(0);
            swordsman.setFlip(false);
        }else if (swordsman.getRight() > gameMap.getMapWidth()) {
            swordsman.setRight(gameMap.getMapWidth());
            swordsman.setFlip(true);
        }
        if (swordsman.getTop() < 0) {
            swordsman.setTop(0);
        }else if (swordsman.getBottom() > gameMap.getMapHeight()) {
            swordsman.setBottom(gameMap.getMapHeight());
        }
      
        if(swordsman.getCollisionType(RectCollisionType.BOTTOM) && Math.abs((player.getX()+player.getWidth()/2) - (swordsman.getX()+swordsman.getWidth()/2))<60){
            if((player.getX() > swordsman.getX()) == !swordsman.getFlip()){
                swordsman.setInput(EnemyInput.attack, true);
            }
            else{
                swordsman.setFlip();
                swordsman.setInput(EnemyInput.attack, true);
            }
        }

        //System.out.println(swordsman.getCollisionType(RectCollisionType.BOTTOM));
        if(hitbox.isActive()){
            if(RectToRect.isCollided(hitbox, player)){
                //System.out.print("Collided: ");
                if(player.isVulnerable()){
                    //System.out.println("Hit");
                    player.incDamage(hitbox.getDamage());
                    //player.setHealth(player.getHealth()- hitbox.getDamage());
                }
                //else System.out.println("No Hit");
                player.collisionCount++;
            }
        }
    }
}
