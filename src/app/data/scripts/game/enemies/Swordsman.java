package app.data.scripts.game.enemies;

import java.util.HashMap;
import java.util.Map;

import app.data.scripts.Config;
import app.data.scripts.engine.FSM.StateExec;
import app.data.scripts.engine.FSM.StateMachine;
import app.data.scripts.engine.FSM.StateTransCond;
import app.data.scripts.engine.FSM.StateTransition;
import app.data.scripts.engine.collision.RectCollisionType;
import app.data.scripts.engine.entity.EntityAnimation;
import app.data.scripts.engine.entity.Gravity;
import app.data.scripts.engine.entity.Rect;
import app.data.scripts.engine.entity.SpeedVector;
import app.data.scripts.engine.entity.UpdateStrategy;
import app.data.scripts.game.combat.HitBox;
import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.media.MediaPlayer;

import static app.data.scripts.game.combat.HitBoxType.AttackBox;

public class Swordsman extends Enemy {
    public static final double MAX_X_VELOCITY = 2;
    public static final double MAX_Y_VELOCITY = 5;
    public static final double ATTACK_DISTANCE = 25;

    public static final double X_SHIFT = 29;
    public static final double Y_SHIFT = 20;
    public static final double X_FLIP_SHIFT = 41;
    public static final double Y_FLIP_SHIFT = 20;
    public static final int MAX_HEALTH = 50;

    public final static int hit_DAMAGE = 5;
    public final static double hit_LIFETIME = 24.0;
    public final static double KNOCKBACK_FORCE = 50.0;
    public final static double hitMul = 4.5;

    //private MediaPlayer attPlayer;

    public HitBox atkHitBox = new HitBox(this, this.getX(), this.getY(), 37, 7,
            hit_DAMAGE, hit_LIFETIME, AttackBox, KNOCKBACK_FORCE);

    /*private HashMap<EnemyState, ActionTimer> timer = new HashMap<>() {{

    }};*/

    private HashMap<EnemyState, EntityAnimation> anim = new HashMap<>(Map.ofEntries(
        Map.entry(
            EnemyState.run, 
            new EntityAnimation(
                8,
                new String[] {
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Run/Enemy01_Run_01.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Run/Enemy01_Run_02.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Run/Enemy01_Run_03.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Run/Enemy01_Run_04.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Run/Enemy01_Run_05.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Run/Enemy01_Run_06.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Run/Enemy01_Run_07.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Run/Enemy01_Run_08.png",
                },
                new int[] {3, 3, 3, 3, 3, 3, 3, 3}
            )
        ),
        Map.entry(
            EnemyState.attack,
            new EntityAnimation(
                21,
                new String[] {
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_01.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_02.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_03.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_04.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_05.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_06.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_07.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_08.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_09.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_10.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_11.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_12.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_13.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_14.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_15.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_16.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_17.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_18.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_19.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_20.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Attack/Enemy01_Attack_21.png",
                },
                new int[] {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
                false
            )
        ),
        Map.entry(
            EnemyState.hurt,
            new EntityAnimation(
                9,
                new String[] {
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Hit/Enemy01_Hit_01.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Hit/Enemy01_Hit_02.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Hit/Enemy01_Hit_03.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Hit/Enemy01_Hit_04.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Hit/Enemy01_Hit_05.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Hit/Enemy01_Hit_06.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Hit/Enemy01_Hit_07.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Hit/Enemy01_Hit_08.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Hit/Enemy01_Hit_09.png",
                },
                new int[] {3, 3, 3, 3, 3, 3, 3, 3, 3},
                false
            )
        ),
        Map.entry(
            EnemyState.stun,
            new EntityAnimation(
                7,
                new String[] {
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Stun/Enemy01_Stun_01.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Stun/Enemy01_Stun_02.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Stun/Enemy01_Stun_03.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Stun/Enemy01_Stun_04.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Stun/Enemy01_Stun_05.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Stun/Enemy01_Stun_06.png",
                    "app/data/images/PackCharacterPixelArt09/Enemies/Enemy01/Stun/Enemy01_Stun_07.png",
                },
                new int[] {3, 3, 3, 3, 3, 3, 3},
                false
            )
        )
    ));

    private Gravity gravity = new Gravity(Config.MAX_GRAVITY, this, (rect, g) -> {
        if (((Rect)rect).getCollisionType(RectCollisionType.BOTTOM)) {
            g.zeroGravity();
        }
    });

    public SpeedVector speed = new SpeedVector(MAX_X_VELOCITY, MAX_Y_VELOCITY);
    public SwordsmanFsm fsm = new SwordsmanFsm();

    public Swordsman(double x, double y, double width, double height) {
        super(x, y, width, height);
        atkHitBox.setColor();
        setHealth(MAX_HEALTH);
        //Media mediaAtt = new Media(new File("src/app/data/music/GunsmanAttack.wav").toURI().toString());
        //attPlayer = new MediaPlayer(mediaAtt);
    }

    @Override
    public void update(UpdateStrategy upd) {
        checkUpdateStrategy(upd);
        dt = ((SwordsmanUpdateStrategy)upd).getDt();
        fsm.listenTrans();
        fsm.exec();

        //fsm.showCurrentState();

        anim.get(fsm.getCurrentState()).update(dt);
        upd.perform();
    }

    @Override
    public void draw(GraphicsContext display, int scroll[]) {
        if (flip) {
            anim.get(fsm.getCurrentState()).draw(display, getX() - scroll[0] - X_FLIP_SHIFT, getY() - scroll[1] - Y_FLIP_SHIFT, true);
        }else {
            anim.get(fsm.getCurrentState()).draw(display, getX() - scroll[0] - X_SHIFT, getY() - scroll[1] - Y_SHIFT, false);
        }
        /*if(atkHitBox.isActive()){
            atkHitBox.draw(display, scroll);
        }*/
    }

    public class SwordsmanFsm extends StateMachine<EnemyState> {
        SwordsmanFsm() {
            super(EnemyState.run);
            enter();
        }

        @Override
        public void initState() {
            setStateExec(EnemyState.run, new Run());
            setStateExec(EnemyState.attack, new Attack());
            setStateExec(EnemyState.hurt, new Hurt());
            setStateExec(EnemyState.stun, new Stun());
        }

        @Override
        public void initStateTrans() {
            setStateTrans(EnemyState.run, new StateTransition<EnemyState>(EnemyState.attack, new RunToAttack()));
            setStateTrans(EnemyState.run, new StateTransition<EnemyState>(EnemyState.hurt, new RunToHurt()));
            setStateTrans(EnemyState.attack, new StateTransition<EnemyState>(EnemyState.run, new AttackToRun()));
            setStateTrans(EnemyState.attack, new StateTransition<EnemyState>(EnemyState.stun, new AttackToStun()));
            setStateTrans(EnemyState.attack, new StateTransition<EnemyState>(EnemyState.hurt, new AttackToHurt()));
            setStateTrans(EnemyState.hurt, new StateTransition<EnemyState>(EnemyState.run, new HurtToRun()));
            setStateTrans(EnemyState.stun, new StateTransition<EnemyState>(EnemyState.run, new StunToRun()));
            setStateTrans(EnemyState.stun, new StateTransition<EnemyState>(EnemyState.hurt, new StunToHurt()));
        }
    };

    /* StateExec Definiton Start */
    private class Run extends StateExec<EnemyState> {
        @Override
        public void onStateEnter() {
            speed.initXSpeed();
            
            speed.initYSpeed();
        }

        @Override
        public void onStateExec() {
            //System.out.println(speed.getXSpeed());
            if (getInput(EnemyInput.runLeft)) {
                speed.xAcceleration += -1 * Swordsman.MAX_X_VELOCITY;
                flip = true;
            }else if (getInput(EnemyInput.runRight)) {
                speed.xAcceleration += +1 * Swordsman.MAX_X_VELOCITY;
                flip = false;
            }
            setInput(EnemyInput.runRight, false);
            setInput(EnemyInput.runLeft, false);
            gravity.fall(speed);
        }

        @Override
        public void onStateExit() {
            speed.initXSpeed();
            speed.initYSpeed();
        }
    }

    private void attDir(HitBox box, Double Mul){
        box.setLife();
        if(flip){
            box.setX(getX() - box.getWidth());
            box.setY(getY() + 9);
            //box.setWidth(getWidth()+getWidth() * Mul);
            //box.setHeight(getHeight());
        }
        else{
            box.setX(getRight());
            box.setY(getY() + 9);
            //box.setWidth(getWidth() * Mul + +getWidth());
            //box.setHeight(getHeight());
        }
    }

    private class Attack extends StateExec<EnemyState> {
        @Override
        public void onStateEnter() {
            attDir(atkHitBox, hitMul);
            if (flip) {
                atkHitBox.speed.xAcceleration += -3;
            }else {
                atkHitBox.speed.xAcceleration += +3;
            }
        }

        @Override
        public void onStateExec() {
            if(anim.get(fsm.getCurrentState()).getPointer()==14){
                atkHitBox.setActive();
                //attPlayer.seek(Duration.ZERO);
                //attPlayer.play();
            }
            atkHitBox.update(dt);
        }

        @Override
        public void onStateExit() {
            setInput(EnemyInput.attack, false);
            atkHitBox.deactivate();
            anim.get(fsm.getCurrentState()).initAnimation();
        }
    }

    private class Hurt extends StateExec<EnemyState> {
        @Override
        public void onStateEnter() {
            setHealth(getHealth() - getDamage());
        }

        @Override
        public void onStateExec() {

        }

        @Override
        public void onStateExit() {
            setInput(EnemyInput.hurt, false);
            anim.get(fsm.getCurrentState()).initAnimation();
        }
    }

    private class Stun extends StateExec<EnemyState> {
        @Override
        public void onStateEnter() {
            
        }

        @Override
        public void onStateExec() {

        }

        @Override
        public void onStateExit() {
            setInput(EnemyInput.stun, false);
            anim.get(fsm.getCurrentState()).initAnimation();
        }
    }
    /* StateExec Definiton End */

    /* StateTransitionCondition Definition Start */
    /* (run -> ...) Start */
    private class RunToAttack implements StateTransCond<EnemyState> {
        @Override
        public Boolean isSatisfied() {
            return getInput(EnemyInput.attack);
        }
    };

    private class RunToHurt implements StateTransCond<EnemyState> {
        @Override
        public Boolean isSatisfied() {
            return getInput(EnemyInput.hurt);
        }
    };
    /* (run -> ...) End */

    /* (attack -> ...) Start */
    private class AttackToRun implements StateTransCond<EnemyState> {
        @Override
        public Boolean isSatisfied() {
            return anim.get(fsm.getCurrentState()).isOver();
        }
    };

    private class AttackToStun implements StateTransCond<EnemyState> {
        @Override
        public Boolean isSatisfied() {
            return getInput(EnemyInput.stun);
        }
    };

    private class AttackToHurt implements StateTransCond<EnemyState> {
        @Override
        public Boolean isSatisfied() {
            return getInput(EnemyInput.hurt);
        }
    };
    /* (attack -> ...) End */

    /* (hurt -> ...) Start */
    private class HurtToRun implements StateTransCond<EnemyState> {
        @Override
        public Boolean isSatisfied() {
            return anim.get(fsm.getCurrentState()).isOver();
        }
    };
    /* (hurt -> ...) End */

    /* (stun -> ...) Start */
    private class StunToRun implements StateTransCond<EnemyState> {
        @Override
        public Boolean isSatisfied() {
            return anim.get(fsm.getCurrentState()).isOver();
        }
    };

    private class StunToHurt implements StateTransCond<EnemyState> {
        @Override
        public Boolean isSatisfied() {
            return getInput(EnemyInput.hurt);
        }
    };
    /* (stun -> ...) End */
    /* StateTransitionCondition Definition End */
}
