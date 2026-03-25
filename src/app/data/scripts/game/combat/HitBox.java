package app.data.scripts.game.combat;

import app.data.scripts.engine.entity.Rect;
import app.data.scripts.engine.entity.SpeedVector;
import app.data.scripts.engine.entity.Entity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class HitBox extends Rect {
    private Entity owner;           // 攻擊者
    private int damage;            // 傷害值
    private double lifeTime;       // 存活時間
    private double maxLifeTime;    // 最大存活時間
    private boolean active;        // 是否啟用
    private HitBoxType hitBoxType; // 攻擊類型
    private double knockbackForce; // 擊退力度
    private Color color;

    private final double MAX_X_VELOCITY = 50;
    private final double MAX_Y_VELOCITY = 0;
    
    public SpeedVector speed = new SpeedVector(MAX_X_VELOCITY, MAX_Y_VELOCITY);

    public HitBox(Entity owner, double x, double y, double width, double height,
                  int damage, double lifeTime, HitBoxType hitBoxType, double knockbackForce){
        super(x, y, width, height); //Rect大小
        this.owner = owner;
        this.damage = damage;
        this.lifeTime = 0;
        this.maxLifeTime = lifeTime;
        this.active = false;
        this.hitBoxType = hitBoxType;
        this.knockbackForce = knockbackForce;
        color = Color.GREEN;
    }


    public void update(double dt){
        if (active){
            lifeTime -= dt;
            setX(getX() + speed.getXSpeed() * dt);
        }
        if (lifeTime <= 0) {
            lifeTime = 0;
            active = false;
        }
    }

    @Override
    public void draw(GraphicsContext display, int[] scroll){
        //顯示HitBox
        if (active) {
            display.setGlobalAlpha(0.8);
            display.setFill(color);
            display.fillRect(getX() - scroll[0], getY() - scroll[1], getWidth(), getHeight());
            display.setGlobalAlpha(1.0);
            display.setFill(Color.BLACK);
        }
    }

    public void setLife(){
        lifeTime=maxLifeTime;
    }

    public double getLife() {
        return lifeTime;
    }

    public void setColor(){
        color = Color.RED;
    }
    public void setColorG(){
        color = Color.PURPLE;
    }
     
    // Getters
    public Entity getOwner(){ return owner;}
    public int getDamage(){ return damage;}
    public boolean isActive(){ return active;}
    public HitBoxType getHitBoxType(){ return hitBoxType;}
    public double getKnockbackForce(){ return knockbackForce;}

    // Setters
    public void setActive(){ this.active = true;}
    public void deactivate(){ this.active = false;}
}