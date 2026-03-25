package app.data.scripts.game.player;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import app.data.scripts.Config;
import app.data.scripts.engine.FSM.*;
import app.data.scripts.engine.collision.RectCollisionType;
import app.data.scripts.engine.entity.EntityAnimation;
import app.data.scripts.engine.entity.Gravity;
import app.data.scripts.engine.entity.Rect;
import app.data.scripts.engine.entity.SpeedVector;
import app.data.scripts.engine.entity.UpdateStrategy;
import app.data.scripts.engine.tools.ActionTimer;
import app.data.scripts.game.combat.*;
import static app.data.scripts.game.combat.HitBoxType.AttackBox;
import static app.data.scripts.game.combat.HitBoxType.BlockBox;

import javafx.util.Duration;

public class Player extends Rect {
    public final static int MAX_HEALTH = 100;
    public final static double ALPHA_CHANGE = 0.1;
    public final static double ALPHA_LOWER_BOUND = 0.1;

    private final static int hit_DAMAGE = 25;
    private final static int criticalHit_DAMAGE = 50;
    private final static double hit_LIFETIME = 21.0;
    private final static double criticalHit_LIFETIME = 12.0;
    private final static double burst_LIFETIME = 12.0;
    private final static double block_LIFETIME = 15.0;
    private final static double heldBlock_LIFETIME = 25.0;
    //private final static int COUNTER_DAMAGE = 30;
    private final static double KNOCKBACK_FORCE = 100.0;
    private final static int HEAL_AMOUNT = 20;
    private final static int invulnerabilityTime = 30;
    private static final int BONUS_TIME = 15;

    private final static double MAX_X_VELOCITY = 2;
    private final static double MAX_Y_VELOCITY = 8;
    private final static int MAX_JUMP_TIME = 20;
    private final static int X_SHIFT = 36;
    private final static int Y_SHIFT = 34;
    private final static int X_FLIP_SHIFT = 49;
    private final static int Y_FLIP_SHIFT = 34;
    
    private final static double hitMul = 7;
    private final static double criticalMul = 12;
    private final static double burstMul = 3;
    private final static double blockMul = 5;

    private Boolean flip = false;
    private double dt;
    private int health = MAX_HEALTH;
    private double opacity = 1.0;
    private int opacityChangeSign = -1;
    private int damage = 0;

    private MediaPlayer hitPlayer;
    private MediaPlayer hardHitPlayer;
    private MediaPlayer blockPlayer;
    private MediaPlayer hurtPlayer;

    public int cardA = 0;
    public int cardB = 0;
    public int bonus = 0;
    public int collisionCount = 0;

    public HashMap<PlayerInput, Integer> skillCount = new HashMap<>(Map.ofEntries(
        Map.entry(PlayerInput.hit, 0),
        Map.entry(PlayerInput.criticalHit, 0),
        Map.entry(PlayerInput.block, 0),
        Map.entry(PlayerInput.blockHeal, 0),
        Map.entry(PlayerInput.heldBlock, 0),
        Map.entry(PlayerInput.parry, 0),
        Map.entry(PlayerInput.parryBurst, 0)
    ));

    private HashMap<PlayerInput, Boolean> input = new HashMap<>(Map.ofEntries(
        Map.entry(PlayerInput.jump, false),
        Map.entry(PlayerInput.moveLeft, false),
        Map.entry(PlayerInput.moveRight, false),
        Map.entry(PlayerInput.skill, false),
        Map.entry(PlayerInput.hit, false),
        Map.entry(PlayerInput.criticalHit, false),
        Map.entry(PlayerInput.block, false),
        Map.entry(PlayerInput.blockHeal, false),
        Map.entry(PlayerInput.parry, false),
        Map.entry(PlayerInput.heldBlock, false),
        Map.entry(PlayerInput.parryBurst, false)
    ));

    private HashMap<PlayerState, ActionTimer> timer = new HashMap<>(Map.ofEntries(
        Map.entry(PlayerState.jump, new ActionTimer()),
        Map.entry(PlayerState.invulnerability, new ActionTimer())
    ));

    private Gravity gravity = new Gravity(Config.MAX_GRAVITY, this, (rect, g) -> {
        if (((Rect)rect).getCollisionType(RectCollisionType.BOTTOM)) {
            g.zeroGravity();
        }
    });   

    private HashMap<PlayerState, EntityAnimation> anim = new HashMap<>(Map.ofEntries(
        Map.entry(
            PlayerState.idle, 
            new EntityAnimation(
                8,
                new String[]{
                    "app/data/images/PackCharacterPixelArt09/Player/Idle/Player_Idle_01.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Idle/Player_Idle_02.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Idle/Player_Idle_03.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Idle/Player_Idle_04.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Idle/Player_Idle_05.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Idle/Player_Idle_06.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Idle/Player_Idle_07.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Idle/Player_Idle_08.png",
                },
                new int[]{3, 3, 3, 3, 3, 3, 3, 3}
            )
        ),
        Map.entry(
            PlayerState.run,
            new EntityAnimation(
                8,
                new String[]{
                    "app/data/images/PackCharacterPixelArt09/Player/Run/Player_Run_01.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Run/Player_Run_02.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Run/Player_Run_03.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Run/Player_Run_04.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Run/Player_Run_05.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Run/Player_Run_06.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Run/Player_Run_07.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Run/Player_Run_08.png",
                },
                new int[]{3, 3, 3, 3, 3, 3, 3, 3}
            )
        ),
        Map.entry(
            PlayerState.jump,
            new EntityAnimation("app/data/images/PackCharacterPixelArt09/Player/Idle/Player_Idle_01.png")
        ),
        Map.entry(
            PlayerState.runJump,
            new EntityAnimation("app/data/images/PackCharacterPixelArt09/Player/Idle/Player_Idle_01.png")
        ),
        Map.entry(
            PlayerState.skillEntry,
            new EntityAnimation("app/data/images/PackCharacterPixelArt09/Player/Idle/Player_Idle_01.png")
        ),
        Map.entry(
            PlayerState.hit,
            new EntityAnimation(
                12,
                new String[]{
                    "app/data/images/PackCharacterPixelArt09/Player/Attack01/Player_Attack01_01.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack01/Player_Attack01_02.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack01/Player_Attack01_03.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack01/Player_Attack01_04.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack01/Player_Attack01_05.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack01/Player_Attack01_06.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack01/Player_Attack01_07.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack01/Player_Attack01_08.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack01/Player_Attack01_09.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack01/Player_Attack01_10.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack01/Player_Attack01_11.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack01/Player_Attack01_12.png",
                },
                3,
                false
            )
        ),
        Map.entry(
            PlayerState.criticalHit,
            new EntityAnimation(
                13,
                new String[]{
                    "app/data/images/PackCharacterPixelArt09/Player/Attack03/Player_Attack03_01.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack03/Player_Attack03_02.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack03/Player_Attack03_03.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack03/Player_Attack03_04.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack03/Player_Attack03_05.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack03/Player_Attack03_06.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack03/Player_Attack03_07.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack03/Player_Attack03_08.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack03/Player_Attack03_09.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack03/Player_Attack03_10.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack03/Player_Attack03_11.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack03/Player_Attack03_12.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack03/Player_Attack03_13.png"
                },
                3,
                false
            )
        ),
        Map.entry(
            PlayerState.block,
            new EntityAnimation(
                5,
                new String[]{
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_06.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_07.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_08.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_09.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_10.png",
                },
                3,
                false
            )
        ),
        Map.entry(
            PlayerState.blockHeal,
            new EntityAnimation(
                5,
                new String[]{
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_06.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_07.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_08.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_09.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_10.png",
                },
                3,
                false
            )
        ),
        Map.entry(
            PlayerState.parry,
            new EntityAnimation(
                19,
                new String[]{
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_06.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_07.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_08.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_09.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_10.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack02/Player_Attack02_01.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack02/Player_Attack02_02.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack02/Player_Attack02_03.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack02/Player_Attack02_04.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack02/Player_Attack02_05.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack02/Player_Attack02_06.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack02/Player_Attack02_07.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack02/Player_Attack02_08.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack02/Player_Attack02_09.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack02/Player_Attack02_10.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack02/Player_Attack02_11.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack02/Player_Attack02_12.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack02/Player_Attack02_13.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Attack02/Player_Attack02_14.png"
                },
                3,
                false
            )
        ),
        Map.entry(
            PlayerState.heldBlock,
            new EntityAnimation(
                5,
                new String[]{
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_06.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_07.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_08.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_09.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_10.png",
                },
                7,
                false
            )
        ),
        Map.entry(
            PlayerState.parryBurst,
            new EntityAnimation(
                15,
                new String[]{
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_06.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_07.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_08.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_09.png",
                    "app/data/images/PackCharacterPixelArt09/Player/Parry/Player_Parry_10.png",
                    "app/data/images/PackCharacterPixelArt09/Player/AttackHard/Player_AttackHard_01.png",
                    "app/data/images/PackCharacterPixelArt09/Player/AttackHard/Player_AttackHard_02.png",
                    "app/data/images/PackCharacterPixelArt09/Player/AttackHard/Player_AttackHard_03.png",
                    "app/data/images/PackCharacterPixelArt09/Player/AttackHard/Player_AttackHard_04.png",
                    "app/data/images/PackCharacterPixelArt09/Player/AttackHard/Player_AttackHard_05.png",
                    "app/data/images/PackCharacterPixelArt09/Player/AttackHard/Player_AttackHard_06.png",
                    "app/data/images/PackCharacterPixelArt09/Player/AttackHard/Player_AttackHard_07.png",
                    "app/data/images/PackCharacterPixelArt09/Player/AttackHard/Player_AttackHard_08.png",
                    "app/data/images/PackCharacterPixelArt09/Player/AttackHard/Player_AttackHard_09.png",
                    "app/data/images/PackCharacterPixelArt09/Player/AttackHard/Player_AttackHard_10.png",
                },
                3,
                false
            )
        ),
        Map.entry(
            PlayerState.parryWithoutHit,
            new EntityAnimation(
                10,
                new String[] {
                    "app/data/images/PackCharacterPixelArt09/Player/ParryWithoutHit/Player_ParryWithoutHit_01.png",
                    "app/data/images/PackCharacterPixelArt09/Player/ParryWithoutHit/Player_ParryWithoutHit_02.png",
                    "app/data/images/PackCharacterPixelArt09/Player/ParryWithoutHit/Player_ParryWithoutHit_03.png",
                    "app/data/images/PackCharacterPixelArt09/Player/ParryWithoutHit/Player_ParryWithoutHit_04.png",
                    "app/data/images/PackCharacterPixelArt09/Player/ParryWithoutHit/Player_ParryWithoutHit_05.png",
                    "app/data/images/PackCharacterPixelArt09/Player/ParryWithoutHit/Player_ParryWithoutHit_06.png",
                    "app/data/images/PackCharacterPixelArt09/Player/ParryWithoutHit/Player_ParryWithoutHit_07.png",
                    "app/data/images/PackCharacterPixelArt09/Player/ParryWithoutHit/Player_ParryWithoutHit_08.png",
                    "app/data/images/PackCharacterPixelArt09/Player/ParryWithoutHit/Player_ParryWithoutHit_09.png",
                    "app/data/images/PackCharacterPixelArt09/Player/ParryWithoutHit/Player_ParryWithoutHit_10.png",
                },
                new int[] {1, 1, 1, 1, 1, 5, 5, 5, 5, 5},
                false
            )
        )
    ));
    
    private HashMap<PlayerInput, Boolean> toState = new HashMap<>(Map.ofEntries(
        Map.entry(PlayerInput.block, false),
        Map.entry(PlayerInput.heldBlock, false),
        Map.entry(PlayerInput.parry, false),
        Map.entry(PlayerInput.blockHeal, false),
        Map.entry(PlayerInput.parryBurst, false)
    ));
    
    public HashMap<PlayerModule, StateMachine<PlayerState>> fsmManager = new HashMap<> (Map.ofEntries(
        Map.entry(PlayerModule.move, new PlayerMoveFsm()),
        Map.entry(PlayerModule.skill, new PlayerSkillFsm())
    ));
    public PlayerFsm moduleFsm = new PlayerFsm();
    
    public SpeedVector speed = new SpeedVector(MAX_X_VELOCITY, MAX_Y_VELOCITY);

    public HashMap<PlayerHitBoxType, HitBox> skillHitBox = new HashMap<>(Map.ofEntries(
        Map.entry(
            PlayerHitBoxType.hit,
            new HitBox(
                this,
                this.getX(),
                this.getY(),
                this.getWidth() * hitMul,
                this.getHeight(),
                hit_DAMAGE,
                hit_LIFETIME,
                AttackBox,
                KNOCKBACK_FORCE
            )
        ),
        Map.entry(
            PlayerHitBoxType.criticalHit,
            new HitBox(
                this,
                this.getX(),
                this.getY(),
                this.getWidth() * criticalMul,
                this.getHeight(),
                criticalHit_DAMAGE,
                criticalHit_LIFETIME,
                AttackBox,
                KNOCKBACK_FORCE
            )
        ),
        Map.entry(
            PlayerHitBoxType.block,
            new HitBox(
                this,
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                0,
                block_LIFETIME,
                BlockBox,
                KNOCKBACK_FORCE
            )
        ),
        Map.entry(
            PlayerHitBoxType.heldBlock,
            new HitBox(
                this,
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                hit_DAMAGE,
                heldBlock_LIFETIME,
                BlockBox,
                KNOCKBACK_FORCE
            )
        ),
        Map.entry(
            PlayerHitBoxType.parryHit,
            new HitBox(
                this,
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                hit_DAMAGE,
                heldBlock_LIFETIME,
                AttackBox,
                KNOCKBACK_FORCE
            )
        ),
        Map.entry(
            PlayerHitBoxType.burst,
            new HitBox(
                this,
                this.getX(),
                this.getY(),
                this.getWidth() * burstMul,
                this.getHeight(),
                criticalHit_DAMAGE,
                burst_LIFETIME,
                AttackBox,
                KNOCKBACK_FORCE
            )
        )
    ));
    
    public Player(double x, double y, double width, double height) {
        super(x, y, width, height);
        
        Media mediaNormal = new Media(getClass().getResource("/app/data/music/normalAtt.mp3").toExternalForm());
        hitPlayer = new MediaPlayer(mediaNormal);
        Media mediaHard = new Media(getClass().getResource("/app/data/music/hardAtt.mp3").toExternalForm());
        hardHitPlayer = new MediaPlayer(mediaHard);
        Media mediaBlock = new Media(getClass().getResource("/app/data/music/block.mp3").toExternalForm());
        Media mediaHurt = new Media(getClass().getResource("/app/data/music/hurtSound.mp3").toExternalForm());
        blockPlayer = new MediaPlayer(mediaBlock);
        hurtPlayer = new MediaPlayer(mediaHurt);
    }

    @Override
    public void update(UpdateStrategy upd) {
        checkUpdateStrategy(upd);
        dt = ((PlayerUpdateStrategy)upd).getDt();

        moduleFsm.listenTrans();
        moduleFsm.exec();

        //System.out.println(getX() + " " + getY() + " " + speed.getXSpeed() + " " + speed.getYSpeed() + " " + ((PlayerUpdateStrategy)upd).getDt());
        PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
        anim.get(cur).update(dt);
        
        for (ActionTimer t : timer.values()) {
            t.decTimer(dt);
        }

        for (HitBox hitbox : skillHitBox.values()) {
            hitbox.update(dt);
        }

        //System.out.println(skillHitBox.get(PlayerHitBoxType.block).getLife() + " " + skillHitBox.get(PlayerHitBoxType.block).isActive());
        collisionCount = 0;
        upd.perform();
        //System.out.println(getX() + " " + getY() + " " + speed.getXSpeed() + " " + speed.getYSpeed());
    }

    @Override
    public void draw(GraphicsContext display, int[] scroll) {
        if (timer.get(PlayerState.invulnerability).isOver() == false) {
            changeOpacity();
            display.setGlobalAlpha(opacity);
        }
        if (flip) {
            anim.get(
                fsmManager.get(moduleFsm.getCurrentState()).getCurrentState()
            ).draw(display, getX() - scroll[0] - X_FLIP_SHIFT, getY() - scroll[1] - Y_FLIP_SHIFT, true);
        }else {
            anim.get(
                fsmManager.get(moduleFsm.getCurrentState()).getCurrentState()
            ).draw(display, getX() - scroll[0] - X_SHIFT, getY() - scroll[1] - Y_SHIFT, false);
        }
        display.setGlobalAlpha(1.0);

        //System.out.println("hit active: " + hit_HitBox.isActive());

        /*if (skillHitBox.get(PlayerHitBoxType.hit).isActive()) {
            skillHitBox.get(PlayerHitBoxType.hit).draw(display, scroll);
        }
        if (skillHitBox.get(PlayerHitBoxType.criticalHit).isActive()) {
            skillHitBox.get(PlayerHitBoxType.criticalHit).draw(display, scroll);
        }
        if (skillHitBox.get(PlayerHitBoxType.block).isActive()) {
            skillHitBox.get(PlayerHitBoxType.block).draw(display, scroll);
        }
        if (skillHitBox.get(PlayerHitBoxType.parryHit).isActive()) {
            skillHitBox.get(PlayerHitBoxType.parryHit).draw(display, scroll);
        }
        if (skillHitBox.get(PlayerHitBoxType.heldBlock).isActive()) {
            skillHitBox.get(PlayerHitBoxType.heldBlock).draw(display, scroll);
        }*/
        if (skillHitBox.get(PlayerHitBoxType.burst).isActive()) {
            skillHitBox.get(PlayerHitBoxType.burst).draw(display, scroll);
        }
        //System.out.println("--------------");
    }

    public boolean getFlip(){
        return flip;
    }

    public void setInput(PlayerInput tp, Boolean bool) {
        if (input.containsKey(tp) == false) {
            throw new IllegalArgumentException("The status " + tp + " does not exist.");
        }
        input.put(tp, bool);
    }

    public Boolean getInput(PlayerInput tp) {
        if (input.containsKey(tp) == false) {
            throw new IllegalArgumentException("The status " + tp + " does not exist.");
        }
        return input.get(tp);
    }

    public void setToState(PlayerInput tp, Boolean bool) {
        if (toState.containsKey(tp) == false) {
            throw new IllegalArgumentException("The status " + tp + " does not exist.");
        }
        toState.put(tp, bool);
    }

    public Boolean getToState(PlayerInput tp) {
        if (toState.containsKey(tp) == false) {
            throw new IllegalArgumentException("The status " + tp + " does not exist.");
        }
        return toState.get(tp);
    }

    private void changeOpacity() {
        opacity += opacityChangeSign * ALPHA_CHANGE;
        if (opacity <= ALPHA_LOWER_BOUND) {
            opacityChangeSign *= -1;
            opacity = ALPHA_LOWER_BOUND;
        }else if (opacity >= 1.0) {
            opacityChangeSign *= -1;
            opacity = 1.0;
        }
    }

    public void setInvulnerability(){
        timer.get(PlayerState.invulnerability).setTimer(invulnerabilityTime);
        opacity = 1.0;
        opacityChangeSign = -1;
    }

    public boolean isVulnerable(){
        return timer.get(PlayerState.invulnerability).isOver();
    }

    public void setHealth(int val) {
        if (val < 0) {
            health = 0;
        }else {
            health = Math.min(val, MAX_HEALTH);
        }
    }

    public int getHealth() {
        return health;
    }

    public Boolean isHurt() {
        return damage > 0;
    }

    public void incDamage(int val) {
        damage += val;
    }

    public int getDamage() {
        int res = damage;
        damage = 0;
        return res;
    }

    private void playerDir(HitBox box, Double Mul){
        box.setLife();
        if(flip){
            box.setX(getX() - getWidth()*Mul - getWidth());
            box.setY(getY());
            box.setWidth(getWidth()*2 + getWidth()*Mul);
            box.setHeight(getHeight());
        }
        else{
            box.setX(getX());
            box.setY(getY());
            box.setWidth(getWidth()*Mul +getWidth()*2);
            box.setHeight(getHeight());
        }
        //box.setActive();
    }

    private void HitBoxInit(HitBox box){
        box.setLife();
        if(flip){
            box.setX(getX() - getWidth() * blockMul);
            box.setY(getY());
            box.setWidth(getWidth() * blockMul + getWidth() * blockMul);
            box.setHeight(getHeight());
        }
        else{
            box.setX(getX() - getWidth() * blockMul);
            box.setY(getY());
            box.setWidth(getWidth() * blockMul + getWidth() * blockMul);
            box.setHeight(getHeight());
        }
        //box.setActive();
    }

    private class PlayerFsm extends StateMachine<PlayerModule> {
        PlayerFsm() {
            super(PlayerModule.move);
        }

        @Override
        public void initState() {
            setStateExec(PlayerModule.move, new Move());
            setStateExec(PlayerModule.skill, new Skill());
        }

        @Override
        public void initStateTrans() {
            setStateTrans(PlayerModule.move, new StateTransition<PlayerModule>(PlayerModule.skill, new MoveToSkill()));
            setStateTrans(PlayerModule.skill, new StateTransition<PlayerModule>(PlayerModule.move, new SkillToMove()));
        }
    }

    private class PlayerMoveFsm extends StateMachine<PlayerState> {
        PlayerMoveFsm() {
            super(PlayerState.idle);
        }

        @Override
        public void initState() {
            setStateExec(PlayerState.idle, new Idle());
            setStateExec(PlayerState.run, new Run());
            setStateExec(PlayerState.jump, new Jump());
            setStateExec(PlayerState.runJump, new RunJump());
        }

        @Override
        public void initStateTrans() {
            setStateTrans(PlayerState.idle, new StateTransition<PlayerState>(PlayerState.run, new IdleToRun()));
            setStateTrans(PlayerState.idle, new StateTransition<PlayerState>(PlayerState.jump, new IdleToJump()));
            setStateTrans(PlayerState.run, new StateTransition<PlayerState>(PlayerState.idle, new RunToIdle()));
            setStateTrans(PlayerState.run, new StateTransition<PlayerState>(PlayerState.runJump, new RunToRunJump()));
            setStateTrans(PlayerState.jump, new StateTransition<PlayerState>(PlayerState.idle, new JumpToIdle()));
            setStateTrans(PlayerState.jump, new StateTransition<PlayerState>(PlayerState.runJump, new JumpToRunJump()));
            setStateTrans(PlayerState.runJump, new StateTransition<PlayerState>(PlayerState.idle, new RunJumpToIdle()));
        }
    }

    private class PlayerSkillFsm extends StateMachine<PlayerState> {
        PlayerSkillFsm() {
            super(PlayerState.skillEntry);
        }

        @Override
        public void initState() {
            setStateExec(PlayerState.skillEntry, new skillEntry());
            setStateExec(PlayerState.hit, new Hit());
            setStateExec(PlayerState.criticalHit, new CriticalHit());
            setStateExec(PlayerState.block, new Block());
            setStateExec(PlayerState.blockHeal, new BlockHeal());
            setStateExec(PlayerState.parry, new Parry());
            setStateExec(PlayerState.heldBlock, new heldBlock());
            setStateExec(PlayerState.parryBurst, new parryBurst());
            setStateExec(PlayerState.parryWithoutHit, new ParryWithoutHit());
        }

        @Override
        public void initStateTrans() {
            setStateTrans(PlayerState.skillEntry, new StateTransition<PlayerState>(PlayerState.hit, new skillEntryToHit()));
            setStateTrans(PlayerState.skillEntry, new StateTransition<PlayerState>(PlayerState.criticalHit, new skillEntryToCriticalHit()));
            
            /*setStateTrans(PlayerState.skillEntry, new StateTransition<PlayerState>(PlayerState.block, new skillEntryToBlock()));
            setStateTrans(PlayerState.skillEntry, new StateTransition<PlayerState>(PlayerState.blockHeal, new skillEntryToBlockHeal()));
            setStateTrans(PlayerState.skillEntry, new StateTransition<PlayerState>(PlayerState.parry, new skillEntryToParry()));
            setStateTrans(PlayerState.skillEntry, new StateTransition<PlayerState>(PlayerState.heldBlock, new skillEntryToheldBlock()));
            setStateTrans(PlayerState.skillEntry, new StateTransition<PlayerState>(PlayerState.parryBurst, new skillEntryToParryBurst()));*/
            setStateTrans(PlayerState.skillEntry, new StateTransition<PlayerState>(PlayerState.parryWithoutHit, new skillEntryToParryWithoutHit()));
            
            setStateTrans(PlayerState.parryWithoutHit, new StateTransition<PlayerState>(PlayerState.block, new ParryWithoutHitToBlock()));
            setStateTrans(PlayerState.parryWithoutHit, new StateTransition<PlayerState>(PlayerState.blockHeal, new ParryWithoutHitToBlockHeal()));
            setStateTrans(PlayerState.parryWithoutHit, new StateTransition<PlayerState>(PlayerState.parry, new ParryWithoutHitToParry()));
            setStateTrans(PlayerState.parryWithoutHit, new StateTransition<PlayerState>(PlayerState.heldBlock, new ParryWithoutHitToheldBlock()));
            setStateTrans(PlayerState.parryWithoutHit, new StateTransition<PlayerState>(PlayerState.parryBurst, new ParryWithoutHitToParryBurst()));

            setStateTrans(PlayerState.hit, new StateTransition<PlayerState>(PlayerState.skillEntry, new AnySkillToskillEntry()));
            setStateTrans(PlayerState.criticalHit, new StateTransition<PlayerState>(PlayerState.skillEntry, new AnySkillToskillEntry()));
            setStateTrans(PlayerState.block, new StateTransition<PlayerState>(PlayerState.skillEntry, new AnySkillToskillEntry()));
            setStateTrans(PlayerState.blockHeal, new StateTransition<PlayerState>(PlayerState.skillEntry, new AnySkillToskillEntry()));
            setStateTrans(PlayerState.parry, new StateTransition<PlayerState>(PlayerState.skillEntry, new AnySkillToskillEntry()));
            setStateTrans(PlayerState.heldBlock, new StateTransition<PlayerState>(PlayerState.skillEntry, new AnySkillToskillEntry()));
            setStateTrans(PlayerState.parryBurst, new StateTransition<PlayerState>(PlayerState.skillEntry, new AnySkillToskillEntry()));
            setStateTrans(PlayerState.parryWithoutHit, new StateTransition<PlayerState>(PlayerState.skillEntry, new AnySkillToskillEntry()));
        }
    }

    /* PlayerFsm Start */
    /* PlayerFsm StateExec Definition Start */
    private class Move extends StateExec<PlayerModule> {
        @Override
        public void onStateEnter() {
            speed.initXSpeed();
            speed.initYSpeed();
        }

        @Override
        public void onStateExec() {
            //moduleFsm -> fsm
            //fsmManager -> (module, fsm)
            //fsmManager.get(moduleFsm.getCurrentState()).showCurrentState();
            fsmManager.get(moduleFsm.getCurrentState()).listenTrans();

            //System.out.println("hit active before: " + hit_HitBox.isActive());
            //System.out.println("state: " + moduleFsm.getCurrentState() + " " + fsmManager.get(moduleFsm.getCurrentState()).getCurrentState());
            //fsmManager.get(moduleFsm.getCurrentState()).showCurrentState();
            fsmManager.get(moduleFsm.getCurrentState()).exec();
            
            if (isHurt()) {
                setHealth(getHealth() - getDamage());
                hurtPlayer.seek(Duration.ZERO);
                hurtPlayer.play();
                setInvulnerability();
            }
        }

        @Override
        public void onStateExit() {
            speed.initXSpeed();
            speed.initYSpeed();
        }
    }

    private class Skill extends Move {}
    /* PlayerFsm StateExec Definition End */

    /* PlayerFsm StateTransitionCondition Definition Start */
    private class MoveToSkill implements StateTransCond<PlayerModule> {
        @Override
        public Boolean isSatisfied() {
            return getInput(PlayerInput.skill);
        }
    }

    private class SkillToMove implements StateTransCond<PlayerModule> {
        private int counter = 0;
        @Override
        public Boolean isSatisfied() {
            PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            if (counter >= 2) {
                counter = 0;
            }
            if (cur == PlayerState.skillEntry) {
                counter++;
            }
            return counter >= 2;
        }
    }
    /* PlayerFsm StateTransitionCondition Definition End */
    /* PlayerFsm End */

    /* MoveFsm Start */
    /* MoveFsm StateExec Definition Start */
    private class Idle extends StateExec<PlayerState> {
        @Override
        public void onStateEnter() {
            anim.get(
                fsmManager.get(moduleFsm.getCurrentState()).getCurrentState()
            ).initAnimation();
            gravity.zeroGravity();
            speed.initXSpeed();
            speed.initYSpeed();
        }

        @Override
        public void onStateExec() {
            gravity.fall(speed);
        }

        @Override
        public void onStateExit() {
            speed.initXSpeed();
            speed.initYSpeed();
        }
    }

    private class Run extends StateExec<PlayerState> {
        @Override
        public void onStateEnter() {
            anim.get(
                fsmManager.get(moduleFsm.getCurrentState()).getCurrentState()
            ).initAnimation();
        }

        @Override
        public void onStateExec() {
            gravity.fall(speed);
            if (getInput(PlayerInput.moveRight)) {
                speed.xAcceleration += +1 * MAX_X_VELOCITY;
                flip = false;
            }else if (getInput(PlayerInput.moveLeft)) {
                speed.xAcceleration += -1 * MAX_X_VELOCITY;
                flip = true;
            }
        }

        @Override
        public void onStateExit() {
            speed.initYSpeed();
            gravity.zeroGravity();
        }
    }

    private class Jump extends StateExec<PlayerState> {
        @Override
        public void onStateEnter() {
            gravity.zeroGravity();
            speed.yAcceleration += -1 * MAX_Y_VELOCITY;
            timer.get(PlayerState.jump).setTimer(MAX_JUMP_TIME);
        }

        @Override
        public void onStateExec() {
            gravity.fall(speed);   
        }

        @Override
        public void onStateExit() {
            speed.initYSpeed();
        }
    }

    private class RunJump extends StateExec<PlayerState> {
        @Override
        public void onStateEnter() {
            if (getInput(PlayerInput.jump) && timer.get(PlayerState.jump).isOver()) {
                speed.yAcceleration += -1 * MAX_Y_VELOCITY;
                timer.get(PlayerState.jump).setTimer(MAX_JUMP_TIME);
            }
        }

        @Override
        public void onStateExec() {
            gravity.fall(speed);
            if (getInput(PlayerInput.moveRight)) {
                speed.xAcceleration += +1 * MAX_X_VELOCITY;
                flip = false;
            }else if (getInput(PlayerInput.moveLeft)) {
                speed.xAcceleration += -1 * MAX_X_VELOCITY;
                flip = true;
            }
        }

        @Override
        public void onStateExit() {
            speed.initYSpeed();
        }
    }
    /* MoveFsm StateExec Definition End */

    /* MoveFsm StateTransitionCondition Definition Start */
    /* (idle -> ...) Start */
    private class IdleToRun implements StateTransCond<PlayerState> {
        @Override
        public Boolean isSatisfied() {
            return getInput(PlayerInput.moveRight) || getInput(PlayerInput.moveLeft);
        }
    }

    private class IdleToJump implements StateTransCond<PlayerState> {
        @Override
        public Boolean isSatisfied() {
            return getInput(PlayerInput.jump) && timer.get(PlayerState.jump).isOver();
        }
    }
    /* (idle -> ...) End */

    /* (run -> ...) Start */
    private class RunToIdle implements StateTransCond<PlayerState> {
        @Override
        public Boolean isSatisfied() {
            return getInput(PlayerInput.moveRight) == false && getInput(PlayerInput.moveLeft) == false;
        }
    }

    private class RunToRunJump implements StateTransCond<PlayerState> {
        @Override
        public Boolean isSatisfied() {
            return getInput(PlayerInput.jump) && timer.get(PlayerState.jump).isOver();
        }
    }
    /* (run -> ...) End */

    /* (jump -> ...) Start */
    private class JumpToIdle implements StateTransCond<PlayerState> {
        private int hitCount = 0;
        @Override
        public Boolean isSatisfied() {
            if (hitCount >= 2) {
                hitCount = 0;
            }
            if (getCollisionType(RectCollisionType.BOTTOM)) {
                hitCount++;
            }
            return hitCount >= 2;
        }
    }

    private class JumpToRunJump implements StateTransCond<PlayerState> {
        @Override
        public Boolean isSatisfied() {
            return getInput(PlayerInput.moveRight) || getInput(PlayerInput.moveLeft);
        }
    }
    /* (jump -> ...) End */

    /* (runJump -> ...) Start */
    private class RunJumpToIdle implements StateTransCond<PlayerState> {
        private int hitCount = 0;
        @Override
        public Boolean isSatisfied() {
            if (hitCount >= 2) {
                hitCount = 0;
            }
            if (getCollisionType(RectCollisionType.BOTTOM)) {
                hitCount++;
            }
            return hitCount >= 2;
        }
    }
    /* (runJump -> ...) End */
    /* MoveFsm StateTransitionCondition Definition End */
    /* MoveFsm End */

    /* SkillFsm Start */
    /* SkillFsm StateExec Definition Start */
    private class skillEntry extends StateExec<PlayerState> {
        @Override
        public void onStateEnter() {}

        @Override
        public void onStateExec() {}

        @Override
        public void onStateExit() {}
    }


    private class Hit extends StateExec<PlayerState> {
        public static final int HIT_BOX_FRAME = 5;

        @Override
        public void onStateEnter() {
            playerDir(skillHitBox.get(PlayerHitBoxType.hit), hitMul);
            skillCount.put(PlayerInput.hit, skillCount.get(PlayerInput.hit) - 1);
        }

        @Override
        public void onStateExec() {
            PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            EntityAnimation currentAnim = anim.get(cur);
            int currentFrame = currentAnim.getPointer();
            if (currentFrame == HIT_BOX_FRAME) {
                skillHitBox.get(PlayerHitBoxType.hit).setActive();
                hitPlayer.seek(Duration.ZERO);
                hitPlayer.play();
            }
            //skillHitBox.get(PlayerHitBoxType.hit).update(dt);
            if (isHurt()) {
                setHealth(getHealth() - getDamage());
                hurtPlayer.seek(Duration.ZERO);
                hurtPlayer.play();
                setInvulnerability();
            }
        }
            
        @Override
        public void onStateExit() {
            //skillHitBox.get(PlayerHitBoxType.hit).deactivate();
            PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            anim.get(cur).initAnimation();
        }
    }

    private class CriticalHit extends StateExec<PlayerState> {
        public static final int HIT_BOX_FRAME = 9;

        @Override
        public void onStateEnter() {
            playerDir(skillHitBox.get(PlayerHitBoxType.criticalHit), criticalMul);
            skillCount.put(PlayerInput.criticalHit, skillCount.get(PlayerInput.criticalHit) - 1);
        }

        @Override
        public void onStateExec() {
            PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            EntityAnimation currentAnim = anim.get(cur);
            int currentFrame = currentAnim.getPointer();
            if (currentFrame == HIT_BOX_FRAME) {
                skillHitBox.get(PlayerHitBoxType.criticalHit).setActive();
                hardHitPlayer.seek(Duration.ZERO);
                hardHitPlayer.play();
            }
            //skillHitBox.get(PlayerHitBoxType.criticalHit).update(dt);
            if (isHurt()) {
                setHealth(getHealth() - getDamage());
                hardHitPlayer.seek(Duration.ZERO);
                hurtPlayer.play();
                setInvulnerability();
            }
        }

        @Override
        public void onStateExit() {
            //skillHitBox.get(PlayerHitBoxType.criticalHit).deactivate();
            PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            anim.get(cur).initAnimation();
        }
    }

    private class ParryWithoutHit extends StateExec<PlayerState> {
        private PlayerInput state;
        public static final int HIT_BOX_FRAME = 4;

        @Override
        public void onStateEnter() {
            HitBoxInit(skillHitBox.get(PlayerHitBoxType.block));
            for (var sta : toState.keySet()) {
                if (getInput(sta)) {
                    state = sta;
                    skillCount.put(sta, skillCount.get(sta) - 1);
                    break;
                }
            }
        }

        @Override
        public void onStateExec() {
            PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            EntityAnimation currentAnim = anim.get(cur);
            int currentFrame = currentAnim.getPointer();

            if (currentFrame == HIT_BOX_FRAME) {
                skillHitBox.get(PlayerHitBoxType.block).setActive();
            }

            //System.out.println("frame: " + currentFrame + " " + isHurt() + " " + damage);
            
            if (skillHitBox.get(PlayerHitBoxType.block).isActive() && isHurt()) {
                //System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                getDamage();
                bonus += collisionCount;
                setToState(state, true);
                blockPlayer.seek(Duration.ZERO);
                blockPlayer.play();
            }else if (isHurt()) {
                System.out.println("BB");
                setHealth(getHealth() - getDamage());
                hurtPlayer.seek(Duration.ZERO);
                hurtPlayer.play();
                setInvulnerability();
            }
        }

        @Override
        public void onStateExit() {
            //skillHitBox.get(PlayerHitBoxType.block).deactivate();
            
            PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            anim.get(cur).initAnimation();
            
            for (var sta : toState.keySet()) {
                toState.put(sta, false);
            }
        }
    }

    private class Block extends StateExec<PlayerState> {
        @Override
        public void onStateEnter() {
            int tot = anim.get(PlayerState.block).getTotalFrames();
            timer.get(PlayerState.invulnerability).setTimer(tot + BONUS_TIME);
            //HitBoxInit(skillHitBox.get(PlayerHitBoxType.block));
        }

        @Override
        public void onStateExec() {
            /*PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            EntityAnimation currentAnim = anim.get(cur);
            int currentFrame = currentAnim.getPointer();
            if (currentFrame == 4) {
                skillHitBox.get(PlayerHitBoxType.block).setActive();
                blockPlayer.play();
            }
            skillHitBox.get(PlayerHitBoxType.block).update(dt);*/
            getDamage();
        }

        @Override
        public void onStateExit() {
            //skillHitBox.get(PlayerHitBoxType.block).deactivate();
            PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            anim.get(cur).initAnimation();
        }
    }

    private class BlockHeal extends StateExec<PlayerState> {
        @Override
        public void onStateEnter() {
            int tot = anim.get(PlayerState.block).getTotalFrames();
            timer.get(PlayerState.invulnerability).setTimer(tot + BONUS_TIME);
            setHealth(getHealth() + HEAL_AMOUNT);
            //HitBoxInit(skillHitBox.get(PlayerHitBoxType.block));
        }

        @Override
        public void onStateExec() {
            /*PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            EntityAnimation currentAnim = anim.get(cur);
            int currentFrame = currentAnim.getPointer();
            if (currentFrame == 4) {
                skillHitBox.get(PlayerHitBoxType.block).setActive();
                blockPlayer.play();
            }
            skillHitBox.get(PlayerHitBoxType.block).update(dt);*/
            getDamage();
        }

        @Override
        public void onStateExit() {
            //skillHitBox.get(PlayerHitBoxType.block).deactivate();
            PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            anim.get(cur).initAnimation();
        }
    }

    private class Parry extends StateExec<PlayerState> {
        public static final int HIT_BOX_FRAME = 11;

        @Override
        public void onStateEnter() {
            int tot = anim.get(PlayerState.block).getTotalFrames();
            timer.get(PlayerState.invulnerability).setTimer(tot + BONUS_TIME);
            //HitBoxInit(skillHitBox.get(PlayerHitBoxType.block)); //use for block type
            playerDir(skillHitBox.get(PlayerHitBoxType.parryHit), hitMul); //use for attack type
        }

        @Override
        public void onStateExec() {
            PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            EntityAnimation currentAnim = anim.get(cur);
            int currentFrame = currentAnim.getPointer();
            //System.out.println("frame"+currentAnim.getPointer());
            if (currentFrame == HIT_BOX_FRAME) {
                skillHitBox.get(PlayerHitBoxType.parryHit).setActive();
                hitPlayer.seek(Duration.ZERO);
                hitPlayer.play();
            }
            /*if (currentFrame == 4) {
                skillHitBox.get(PlayerHitBoxType.block).setActive();
                blockPlayer.play();
            }*/
            //System.out.println("parry" + parryHit_HitBox.isActive());
            //System.out.println("block" + block_HitBox.isActive());
            //skillHitBox.get(PlayerHitBoxType.parryHit).update(dt);
            //skillHitBox.get(PlayerHitBoxType.block).update(dt);
            getDamage();
        }

        @Override
        public void onStateExit() {
            //skillHitBox.get(PlayerHitBoxType.parryHit).deactivate();
            //skillHitBox.get(PlayerHitBoxType.block).deactivate();
            PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            anim.get(cur).initAnimation();
        }
    }

    private class heldBlock extends StateExec<PlayerState> {
        @Override
        public void onStateEnter() {
            int tot = anim.get(PlayerState.block).getTotalFrames();
            timer.get(PlayerState.invulnerability).setTimer(tot);
            //HitBoxInit(skillHitBox.get(PlayerHitBoxType.block));
        }

        @Override
        public void onStateExec() {
            //hit to ++ health

            /*PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            EntityAnimation currentAnim = anim.get(cur);
            int currentFrame = currentAnim.getPointer();
            if (currentFrame == 4) {
                skillHitBox.get(PlayerHitBoxType.block).setActive();
                blockPlayer.play();
            }
            skillHitBox.get(PlayerHitBoxType.block).update(dt);*/
            getDamage();
        }

        @Override
        public void onStateExit() {
            //skillHitBox.get(PlayerHitBoxType.block).deactivate();
            PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            anim.get(cur).initAnimation();
        }
    }

    private class parryBurst extends StateExec<PlayerState> {
        public static final int HIT_BOX_FRAME = 10;

        @Override
        public void onStateEnter() {
            playerDir(skillHitBox.get(PlayerHitBoxType.burst), burstMul);
            //HitBoxInit(skillHitBox.get(PlayerHitBoxType.block));
            if (flip) {
                skillHitBox.get(PlayerHitBoxType.burst).speed.xAcceleration += -10;
            }else {
                skillHitBox.get(PlayerHitBoxType.burst).speed.xAcceleration += +10;
            }

            int tot = anim.get(PlayerState.block).getTotalFrames();
            timer.get(PlayerState.invulnerability).setTimer(tot + BONUS_TIME);
        }

        @Override
        public void onStateExec() {
            PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            EntityAnimation currentAnim = anim.get(cur);
            int currentFrame = currentAnim.getPointer();
            /*if (currentFrame == 4) {
                skillHitBox.get(PlayerHitBoxType.block).setActive();
                blockPlayer.play();
            }*/
            if (currentFrame == HIT_BOX_FRAME) {
                skillHitBox.get(PlayerHitBoxType.burst).setActive();
                skillHitBox.get(PlayerHitBoxType.burst).speed.setXSpeed();
                hardHitPlayer.seek(Duration.ZERO);
                hardHitPlayer.play();
            }
            //sktillHitBox.get(PlayerHitBoxType.block).update(dt);
            //skillHitBox.get(PlayerHitBoxType.burst).update(dt);
            getDamage();
        }

        @Override
        public void onStateExit() {
            //parryHit_HitBox.deactivate();
            //skillHitBox.get(PlayerHitBoxType.burst).deactivate();
            skillHitBox.get(PlayerHitBoxType.burst).speed.initXSpeed();
            //block_HitBox.deactivate();
            //skillHitBox.get(PlayerHitBoxType.block).deactivate();
            PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            anim.get(cur).initAnimation();
        }
    }
    /* SkillFsm StateExec Definition End */
    
    /* SkillFsm StateTransitionCondition Definition End */
    /* (skillEntry -> ...) Start */
    private class skillEntryToHit implements StateTransCond<PlayerState> {
        @Override
        public Boolean isSatisfied() {
            return getInput(PlayerInput.hit) && skillCount.get(PlayerInput.hit) > 0;
        }
    }

    private class skillEntryToCriticalHit implements StateTransCond<PlayerState> {
        @Override
        public Boolean isSatisfied() {
            return getInput(PlayerInput.criticalHit) && skillCount.get(PlayerInput.criticalHit) > 0;
        }
    }

    private class skillEntryToParryWithoutHit implements StateTransCond<PlayerState> {
        @Override
        public Boolean isSatisfied() {
            Boolean ok = (getInput(PlayerInput.block) && skillCount.get(PlayerInput.block) > 0) ||
                (getInput(PlayerInput.blockHeal) && skillCount.get(PlayerInput.blockHeal) > 0) ||
                (getInput(PlayerInput.parry) && skillCount.get(PlayerInput.parry) > 0) ||
                (getInput(PlayerInput.heldBlock) && skillCount.get(PlayerInput.heldBlock) > 0) ||
                (getInput(PlayerInput.parryBurst) && skillCount.get(PlayerInput.parryBurst) > 0);
            return ok;
        }
    }

    private class ParryWithoutHitToBlock implements StateTransCond<PlayerState> {
        @Override
        public Boolean isSatisfied() {
            return getToState(PlayerInput.block);
        }
    }

    private class ParryWithoutHitToBlockHeal implements StateTransCond<PlayerState> {
        @Override
        public Boolean isSatisfied() {
            return getToState(PlayerInput.blockHeal);
        }
    }

    private class ParryWithoutHitToParry implements StateTransCond<PlayerState> {
        @Override
        public Boolean isSatisfied() {
            return getToState(PlayerInput.parry);
        }
    }

    private class ParryWithoutHitToheldBlock implements StateTransCond<PlayerState> {
        @Override
        public Boolean isSatisfied() {
            return getToState(PlayerInput.heldBlock);
        }
    }

    private class ParryWithoutHitToParryBurst implements StateTransCond<PlayerState> {
        @Override
        public Boolean isSatisfied() {
            return getToState(PlayerInput.parryBurst);
        }
    }
    /* (skillEntry -> ...) End */

    /* (any skill -> skillEntry) Start */
    private class AnySkillToskillEntry implements StateTransCond<PlayerState> {
        @Override
        public Boolean isSatisfied() {
            PlayerState cur = fsmManager.get(moduleFsm.getCurrentState()).getCurrentState();
            return anim.get(cur).isOver();
        }
    }
    /* (any skill -> skillEntry) End */
    /* SkillFsm StateTransitionCondition Definition End */
    /* SkillFsm End */
}