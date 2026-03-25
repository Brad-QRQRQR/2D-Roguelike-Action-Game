package app.data.scripts.game.enemies;

import app.data.scripts.engine.collision.HandleCollision;
import app.data.scripts.engine.collision.RectCollisionType;
import app.data.scripts.engine.collision.RectToRect;
import app.data.scripts.game.combat.HitBox;
import app.data.scripts.game.map.Tile;
import app.data.scripts.game.map.TileGroupManager;
import app.data.scripts.game.player.Player;

public class GunsmanUpdateStrategy extends EnemyUpdateStrategy {
    public GunsmanUpdateStrategy(Player player, TileGroupManager gameMap, double dt) {
        super(player, gameMap, dt);
    }

    @Override
    public void perform() {
        //System.out.println("In upd: " + player.speed.getYSpeed() + " " + player.speed.yAcceleration);
        Gunsman Gunsman = (Gunsman)self;
        HitBox hitbox = Gunsman.atkHitBox;

        //follow player
        if (Gunsman.fsm.getCurrentState() == EnemyState.run) {
            if (player.getX() < Gunsman.getX()) {
                Gunsman.setInput(EnemyInput.runLeft, true);
            }else {
                Gunsman.setInput(EnemyInput.runRight, true);
            }
        }

        Gunsman.speed.setXSpeed();
        Gunsman.speed.setYSpeed();
        //System.out.println("In upd: " + Gunsman.speed.getXSpeed() + " " + Gunsman.speed.xAcceleration);

        Gunsman.initCollisionType();

        Gunsman.setX(Gunsman.getX() + Gunsman.speed.getXSpeed() * dt);
        double right = Math.min(gameMap.getMapWidth(), Gunsman.getX() + gameMap.getChuckSize());
        double left = Math.max(0, Gunsman.getX() - gameMap.getChuckSize());
        HandleCollision handleX = (sEntity, tileEntity) -> {
            Gunsman s = (Gunsman)sEntity;
            Tile tile = (Tile)tileEntity;
            if (s.speed.getXSpeed() > 0) {
                s.setRight(tile.getLeft());
                s.setCollisionType(RectCollisionType.RIGHT, true);
            }else if (s.speed.getXSpeed() < 0) {
                s.setLeft(tile.getRight());
                s.setCollisionType(RectCollisionType.LEFT, true);
            }
        };
        RectToRect.checkCollision(Gunsman, gameMap.getCurrentChuckGroup(Gunsman.getX(), Gunsman.getY()), handleX);
        RectToRect.checkCollision(Gunsman, gameMap.getCurrentChuckGroup(right, Gunsman.getY()), handleX);
        RectToRect.checkCollision(Gunsman, gameMap.getCurrentChuckGroup(left, Gunsman.getY()), handleX);

        Gunsman.setY(Gunsman.getY() + Gunsman.speed.getYSpeed() * dt);
        double top = Math.min(0, Gunsman.getY() - gameMap.getChuckSize());
        double bottom = Math.max(gameMap.getMapHeight(), Gunsman.getY() + gameMap.getChuckSize());
        HandleCollision handleY = (sEntity, tileEntity) -> {
            Gunsman s = (Gunsman)sEntity;
            Tile tile = (Tile)tileEntity;
            if (s.speed.getYSpeed() < 0) {
                s.setTop(tile.getBottom());
                s.setCollisionType(RectCollisionType.TOP, true);
            }else if (s.speed.getYSpeed() > 0) {
                s.setBottom(tile.getTop());
                s.setCollisionType(RectCollisionType.BOTTOM, true);
            }
        };
        RectToRect.checkCollision(Gunsman, gameMap.getCurrentChuckGroup(Gunsman.getX(), Gunsman.getY()), handleY);
        RectToRect.checkCollision(Gunsman, gameMap.getCurrentChuckGroup(Gunsman.getX(), top), handleY);
        RectToRect.checkCollision(Gunsman, gameMap.getCurrentChuckGroup(Gunsman.getX(), bottom), handleY);

        if (Gunsman.getLeft() < 0) {
            Gunsman.setLeft(0);
            Gunsman.setFlip(false);
        }else if (Gunsman.getRight() > gameMap.getMapWidth()) {
            Gunsman.setRight(gameMap.getMapWidth());
            Gunsman.setFlip(true);
        }
        if (Gunsman.getTop() < 0) {
            Gunsman.setTop(0);
        }else if (Gunsman.getBottom() > gameMap.getMapHeight()) {
            Gunsman.setBottom(gameMap.getMapHeight());
        }
      
        if(Gunsman.getCollisionType(RectCollisionType.BOTTOM) && Math.abs((player.getX()+player.getWidth()/2) - (Gunsman.getX()+Gunsman.getWidth()/2))<150){
            if((player.getX() > Gunsman.getX()) == !Gunsman.getFlip()){
                Gunsman.setInput(EnemyInput.attack, true);
            }
            else{
                Gunsman.setFlip();
                Gunsman.setInput(EnemyInput.attack, true);
            }
        }

        //System.out.println(Gunsman.getCollisionType(RectCollisionType.BOTTOM));
        if(hitbox.isActive()){
            if(RectToRect.isCollided(hitbox, player)){
                if(player.isVulnerable()){
                    player.incDamage(hitbox.getDamage());
                }
                player.collisionCount++;
            }
        }
    }
}
