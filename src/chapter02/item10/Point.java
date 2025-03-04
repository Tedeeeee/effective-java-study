package chapter02.item10;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point) ) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }
}

class ColorPoint extends Point {
    private final String color;

    public ColorPoint(int x, int y, String color) {
        super(x, y);
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) return false;

        if (o instanceof ColorPoint) {
            ColorPoint colorPoint = (ColorPoint) o;
            return super.equals(colorPoint) && Objects.equals(color, colorPoint.color);
        }
        return super.equals(o);
    }
}

class CompositionColorPoint {
    private final Point point;
    private final String color;

    public CompositionColorPoint(int x, int y, String color) {
        this.point = new Point(x, y);
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CompositionColorPoint)) return false;
        CompositionColorPoint cp = (CompositionColorPoint) o;
        return point.equals(cp.point) && color.equals(cp.color);
    }

    // ...
}

class Main {
    public static void main(String[] args) {
        Point x = new Point(1, 2);
        ColorPoint y = new ColorPoint(1, 2, "red");
        ColorPoint z = new ColorPoint(1, 2, "blue");

        System.out.println(x.equals(y)); // true
        System.out.println(x.equals(z)); // true
        System.out.println(y.equals(z)); // false

        Set<Point> set = new HashSet<>();
        set.add(x);
        System.out.println(set.contains(y));
    }
}
