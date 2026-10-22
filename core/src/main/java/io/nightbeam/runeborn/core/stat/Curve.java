package io.nightbeam.runeborn.core.stat;

/** Piecewise-linear curve through (x, y) knots, clamped at both ends. */
public final class Curve {
    private final double[] xs;
    private final double[] ys;

    private Curve(double[] xs, double[] ys) {
        this.xs = xs;
        this.ys = ys;
    }

    /** {@code of(x0, y0, x1, y1, ...)} with strictly increasing x. */
    public static Curve of(double... pairs) {
        if (pairs.length < 4 || pairs.length % 2 != 0) {
            throw new IllegalArgumentException("Curve needs at least two (x, y) pairs");
        }
        int n = pairs.length / 2;
        double[] xs = new double[n];
        double[] ys = new double[n];
        for (int i = 0; i < n; i++) {
            xs[i] = pairs[i * 2];
            ys[i] = pairs[i * 2 + 1];
            if (i > 0 && xs[i] <= xs[i - 1]) {
                throw new IllegalArgumentException("Curve x values must increase");
            }
        }
        return new Curve(xs, ys);
    }

    public double at(double x) {
        if (x <= xs[0]) {
            return ys[0];
        }
        int last = xs.length - 1;
        if (x >= xs[last]) {
            return ys[last];
        }
        for (int i = 1; i <= last; i++) {
            if (x <= xs[i]) {
                double t = (x - xs[i - 1]) / (xs[i] - xs[i - 1]);
                return ys[i - 1] + (ys[i] - ys[i - 1]) * t;
            }
        }
        return ys[last];
    }
}
