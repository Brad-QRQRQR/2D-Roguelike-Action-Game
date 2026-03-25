package app.data.scripts.engine.tools;

public class RangeRandom {
    public static double random(double mn, double mx) {
        return Math.random() * (mx - mn + 1) + mn;
    }
}
