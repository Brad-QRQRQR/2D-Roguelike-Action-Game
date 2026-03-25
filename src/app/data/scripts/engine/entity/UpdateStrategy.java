package app.data.scripts.engine.entity;

public abstract class UpdateStrategy {
    protected Entity self;
    public abstract void perform();
    
    public void setSelf(Entity self) {
        this.self = self;
    }
}