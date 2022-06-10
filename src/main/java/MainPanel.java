import graph.Edge;
import graph.Graph;
import graph.Vertex;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;

public class MainPanel extends JPanel implements MouseListener, MouseMotionListener {

    private static int vertexID = 0;

    private File backgroundImage;
    private Graph graph;

    private Vertex hoveredVertex;
    private Edge hoveredEdge;

    private int lastMouseX;
    private int lastMouseY;

    private boolean leftMousePressed;
    private boolean rightMousePressed;

    private Vertex sourceVertex;

    private Color vertexColor = Color.RED;
    private Color edgeColor = Color.BLACK;

    private boolean directed;

    public MainPanel() {
        if (graph == null) {
            graph = new Graph();
        }

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            if (backgroundImage != null) {
                g.drawImage(ImageIO.read(backgroundImage), 0, 0, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (graph != null) {
            graph.draw(g);
        }
    }

    public File getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(File backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        setCursor(mouseEvent);
        if (SwingUtilities.isMiddleMouseButton(mouseEvent)) {
            if (hoveredVertex != null) {
                graph.removeVerticle(hoveredVertex);
                graph.reindex(hoveredVertex);
                vertexID--;
            } else if (hoveredEdge != null) {
                graph.removeEdge(hoveredEdge);
            }
        } else if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            if (hoveredVertex != null) {

            } else {
                graph.addVertex(new Vertex(mouseEvent.getX(), mouseEvent.getY(), vertexID++, vertexColor));
            }
        } else if (SwingUtilities.isRightMouseButton(mouseEvent)) {
            //TODO
        }
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        setCursor(mouseEvent);
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            leftMousePressed = true;
        } else if (SwingUtilities.isRightMouseButton(mouseEvent)) {
            rightMousePressed = true;
            if (hoveredVertex != null) {
                sourceVertex = hoveredVertex;
            } else {
                sourceVertex = null;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        setCursor(mouseEvent);
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            leftMousePressed = false;
        } else if (SwingUtilities.isRightMouseButton(mouseEvent)) {
            rightMousePressed = false;
            if (hoveredVertex != null && sourceVertex != null && !hoveredVertex.equals(sourceVertex)) {
                Edge edge = new Edge(sourceVertex, hoveredVertex, directed, edgeColor);
                graph.addEdge(edge);
                sourceVertex = null;
            }
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        if (leftMousePressed) {
            dragElement(mouseEvent.getX(), mouseEvent.getY());
        }
        setCursor(mouseEvent);
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        lastMouseX = mouseEvent.getX();
        lastMouseY = mouseEvent.getY();
        setCursor(mouseEvent);

    }

    private void dragElement(int mx, int my) {
        int toMoveX = mx - lastMouseX;
        int toMoveY = my - lastMouseY;

        if (hoveredVertex != null) {
            hoveredVertex.move(toMoveX, toMoveY);
        } else if (hoveredEdge != null) {
            hoveredEdge.move(toMoveX, toMoveY);
        } else {
            graph.move(toMoveX, toMoveY);
        }

        lastMouseX = mx;
        lastMouseY = my;

        repaint();
    }

    private void setCursor(MouseEvent mouseEvent) {
        hoveredVertex = graph.getHoveredVertex(mouseEvent);
        if (hoveredVertex == null) {
            hoveredEdge = graph.getHoveredEdge(mouseEvent);
        }

        int cursor;
        if (hoveredVertex != null) {
            cursor = Cursor.HAND_CURSOR;
        } else if (hoveredEdge != null) {
            cursor = Cursor.CROSSHAIR_CURSOR;
        } else if (leftMousePressed) {
            cursor = Cursor.MOVE_CURSOR;
        } else {
            cursor = Cursor.DEFAULT_CURSOR;
        }
        setCursor(Cursor.getPredefinedCursor(cursor));
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public void setVertexColor(Color vertexColor) {
        this.vertexColor = vertexColor;
    }

    public void setEdgeColor(Color edgeColor) {
        this.edgeColor = edgeColor;
    }

    public static void setVertexID(int vertexID) {
        MainPanel.vertexID = vertexID;
    }
}
