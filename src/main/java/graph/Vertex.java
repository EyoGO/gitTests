package graph;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;

public class Vertex implements Serializable {

    private int x;
    private int y;
    private int id;
    private Color color;

    public Vertex(int x, int y, int id, Color color) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.color = color;
    }

    public void draw(Graphics g, int size) {
        g.setColor(color);
        g.fillOval(x - size, y - size, size + size, size + size);
        g.setColor(Color.BLACK);
        g.drawOval(x - size, y - size, size + size, size + size);

        FontMetrics fm = g.getFontMetrics();
        int tx = x - fm.stringWidth(String.valueOf(id)) / 2 + size + 10;
        int ty = y - fm.getHeight() / 2 + fm.getAscent();
        Font font = g.getFont().deriveFont(Font.PLAIN, 16f);
        g.setFont(font);
        g.drawString(String.valueOf(id), tx, ty);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isHovered(int mx, int my, int size) {
        int a = x - mx;
        int b = y - my;

        return a * a + b * b <= size * size;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return x == vertex.x &&
                y == vertex.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
