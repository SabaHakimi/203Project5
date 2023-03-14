import java.util.Objects;

public class Node {

    private final Node prev;
    private final Point location;
    private double f;
    private int g;
    private final double h;

    public Node(Point p, double f, int g, double h, Node prev) {
        this.location = p;
        this.f = f;
        this.g = g;
        this.h = h;
        this.prev = prev;
    }

    public Point getLocation() { return this.location; }
    public double getF() { return this.f; }
    public int getG() { return this.g; }
    public void setG(int g) { this.g = g;}
    public double getH() { return this.h; }

    public Node getPrev() { return this.prev; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Node n = (Node) obj;
        return (this.location == n.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }
}
