package app.data.scripts.game.userInterface;

import app.data.scripts.engine.entity.UpdateStrategy;
import app.data.scripts.game.player.Player;
import app.data.scripts.game.player.PlayerInput;

public class SkillDemoUpdateStrategy extends UpdateStrategy {
    private Player player;

    public SkillDemoUpdateStrategy(SkillDemo skillDemo, Player player) {
        setSelf(skillDemo);
        this.player = player;
    }

    @Override
    public void perform() {
        SkillDemo skillDemo = (SkillDemo)self;
        skillDemo.setNumberAt(0, player.skillCount.get(PlayerInput.hit));
        skillDemo.setNumberAt(1, player.skillCount.get(PlayerInput.block));
        skillDemo.setNumberAt(2, player.skillCount.get(PlayerInput.criticalHit));
        skillDemo.setNumberAt(3, player.skillCount.get(PlayerInput.parry));
        skillDemo.setNumberAt(4, player.skillCount.get(PlayerInput.blockHeal));
        skillDemo.setNumberAt(5, player.skillCount.get(PlayerInput.heldBlock));
        skillDemo.setNumberAt(6, player.skillCount.get(PlayerInput.parryBurst));
    }
}
