package app.data.scripts.game.enemies;

import app.data.scripts.engine.entity.UpdateStrategy;
import app.data.scripts.game.map.TileGroupManager;
import app.data.scripts.game.player.Player;

public class EnemyUpdateStrategy extends UpdateStrategy {
    protected Player player;
    protected TileGroupManager gameMap;
    protected double dt;

    public EnemyUpdateStrategy(Player player, TileGroupManager gameMap, double dt) {
        setSelf(null);
        this.gameMap = gameMap;
        this.player = player;
        this.dt = dt;
    }

    public Player getPlayer() {
        return player;
    }

    public TileGroupManager getTileGroupManager() {
        return gameMap;
    }

    public double getDt() {
        return dt;
    }

    @Override
    public void perform() {}
}
