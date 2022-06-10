package graph;

import java.awt.*;
import java.io.Serializable;

public class Edge implements Serializable {

    private Vertex sourceVertex;
    private Vertex targetVertex;
    private boolean isDirected;
    private Color color;

    public Edge(Vertex sourceVertex, Vertex targetVertex, boolean isDirected, Color color) {
        this.sourceVertex = sourceVertex;
        this.targetVertex = targetVertex;
        this.isDirected = isDirected;
        this.color = color;
    }

    public void move(int dx, int dy) {
        sourceVertex.move(dx, dy);
        targetVertex.move(dx, dy);
    }

    public void draw(Graphics g, int size) {
        int xSource = sourceVertex.getX();
        int ySource = sourceVertex.getY();
        int xTarget = targetVertex.getX();
        int yTarget = targetVertex.getY();

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(size));
        if (isDirected) {
            drawArrowLine(g2, xSource, ySource, xTarget, yTarget, 20, 10);
        } else {
            g2.drawLine(xSource, ySource, xTarget, yTarget);
        }
        g2.setStroke(new BasicStroke());
    }

    public boolean isHovered(int mx, int my) {
        if (mx < Math.min(sourceVertex.getX(), targetVertex.getX()) ||
                mx > Math.max(sourceVertex.getX(), targetVertex.getX()) ||
                my < Math.min(sourceVertex.getY(), targetVertex.getY()) ||
                my > Math.max(sourceVertex.getY(), targetVertex.getY())) {
            return false;
        }

        int A = targetVertex.getY() - sourceVertex.getY();
        int B = targetVertex.getX() - sourceVertex.getX();

        double distance = Math.abs(A * mx - B * my + targetVertex.getX() * sourceVertex.getY() - targetVertex.getY() * sourceVertex.getX()) / Math.sqrt(A * A + B * B);
        return distance <= 5;
    }

    public Vertex getSourceVertex() {
        return sourceVertex;
    }

    public Vertex getTargetVertex() {
        return targetVertex;
    }

    public boolean isDirected() {
        return isDirected;
    }

    /**
     * Draw an arrow line between two points.
     *
     * @param g  the graphics component.
     * @param x1 x-position of first point.
     * @param y1 y-position of first point.
     * @param x2 x-position of second point.
     * @param y2 y-position of second point.
     * @param d  the width of the arrow.
     * @param h  the height of the arrow.
     */
    private void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2, int d, int h) {
        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx * dx + dy * dy);
        double xm = D - d, xn = xm, ym = h, yn = -h, x;
        double sin = dy / D, cos = dx / D;

        x = xm * cos - ym * sin + x1;
        ym = xm * sin + ym * cos + y1;
        xm = x;

        x = xn * cos - yn * sin + x1;
        yn = xn * sin + yn * cos + y1;
        xn = x;

        int[] xpoints = {x2, (int) xm, (int) xn};
        int[] ypoints = {y2, (int) ym, (int) yn};

        g.drawLine(x1, y1, x2, y2);
        g.fillPolygon(xpoints, ypoints, 3);
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
