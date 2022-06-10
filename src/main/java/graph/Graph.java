package graph;

import utils.Util;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;
import java.util.List;

public class Graph implements Serializable {

    private List<Vertex> vertices;
    private List<Edge> edges;

    private int vertexSize = 8;
    private int edgeSize = 2;

    public Graph() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
    }

    public void addEdge(Edge edge) {
        if (!edgeAlreadyExists(edge)) {
            edges.add(edge);
        }
    }

    public void draw(Graphics g) {
        vertices.forEach(vertex -> vertex.draw(g, vertexSize));
        edges.forEach(edge -> edge.draw(g, edgeSize));
    }

    public Vertex getHoveredVertex(MouseEvent mouseEvent) {
        for (Vertex vertex : vertices) {
            if (vertex.isHovered(mouseEvent.getX(), mouseEvent.getY(), vertexSize)) {
                return vertex;
            }
        }
        return null;
    }

    public Edge getHoveredEdge(MouseEvent mouseEvent) {
        for (Edge edge : edges) {
            if (edge.isHovered(mouseEvent.getX(), mouseEvent.getY())) {
                return edge;
            }
        }
        return null;
    }

    public void move(int toMoveX, int toMoveY) {
        vertices.forEach(vertex -> vertex.move(toMoveX, toMoveY));
    }

    public void removeVerticle(Vertex hoveredVertex) {
        edges.removeIf(edge -> edge.getSourceVertex().equals(hoveredVertex) || edge.getTargetVertex().equals(hoveredVertex));
        vertices.remove(hoveredVertex);
    }

    public void removeEdge(Edge hoveredEdge) {
        edges.remove(hoveredEdge);
    }

    public boolean edgeAlreadyExists(Edge edgeToCheck) {
        for (Edge edge : edges) {
            if (edge.getSourceVertex().equals(edgeToCheck.getSourceVertex()) &&
                    edge.getTargetVertex().equals(edgeToCheck.getTargetVertex())) {
                return true;
            }
            if (edge.getSourceVertex().equals(edgeToCheck.getTargetVertex()) &&
                    edge.getTargetVertex().equals(edgeToCheck.getSourceVertex())) {
                return true;
            }
        }
        return false;
    }

    public void setVertexSize(int vertexSize) {
        this.vertexSize = vertexSize;
    }

    public void setEdgeSize(int edgeSize) {
        this.edgeSize = edgeSize;
    }

    public List<List<Double>> getMatrix() {
        List<List<Double>> matrix = new ArrayList<>();
        vertices.forEach(vertex -> matrix.add(new ArrayList<>(Collections.nCopies(vertices.size(), -1.0))));
        for (Edge edge : edges) {
            Vertex source = edge.getSourceVertex();
            Vertex target = edge.getTargetVertex();
            double distance = Point2D.distance(source.getX(), source.getY(), target.getX(), target.getY());
            matrix.get(source.getId()).set(target.getId(), distance);
            if (!edge.isDirected()) {
                matrix.get(target.getId()).set(source.getId(), distance);
            }
        }
        for (int i = 0; i < vertices.size(); i++) {
            matrix.get(i).set(i, 0.0);
        }
        return matrix;
    }

    public double setRoutes(List<Integer> path) {
        double restltDistance = 0;
        for (int j = 0; j < path.size() - 1; j++) {
            int sourcePrintId = path.get(j);
            int targetPrintId = path.get(j + 1);
            Optional<Edge> edgeToPrint = edges.stream().filter(edge ->
                    edge.getSourceVertex().getId() == sourcePrintId && edge.getTargetVertex().getId() == targetPrintId ||
                            edge.getTargetVertex().getId() == sourcePrintId && edge.getSourceVertex().getId() == targetPrintId)
                    .findFirst();
            if (edgeToPrint.isPresent()) {
                if (!edgeToPrint.get().isDirected() || sourcePrintId == edgeToPrint.get().getSourceVertex().getId()) {
                    edgeToPrint.get().setColor(Color.BLUE);
                    restltDistance += Point2D.distance(edgeToPrint.get().getSourceVertex().getX(), edgeToPrint.get().getSourceVertex().getY(),
                            edgeToPrint.get().getTargetVertex().getX(), edgeToPrint.get().getTargetVertex().getY());
                }
            } else {
                System.out.println("Path is broken.");
                return -1;
            }
        }
        return restltDistance;
    }

    public void resetRoutes() {
        edges.forEach(edge -> edge.setColor(Color.BLACK));
    }

    public void reindex(Vertex deletedVertex) {
        int deletedID = deletedVertex.getId();
        for (Vertex vertex : vertices) {
            if (vertex.getId() > deletedID) {
                vertex.setId(vertex.getId() - 1);
            }
        }
    }

    public boolean isConnected() {
        //TODO not correct because parallel connection violates this
        List<List<Double>> matrix = getMatrix();
        List<List<Double>> transposed = Util.transpose(matrix);
        for (int i = 0; i < matrix.size(); i++) {
            boolean rowConnected = matrix.get(i).stream().anyMatch(aDouble -> aDouble != 0.0 && aDouble != -1.0);
            boolean columnConnected = transposed.get(i).stream().anyMatch(aDouble -> aDouble != 0.0 && aDouble != -1.0);
            if (!columnConnected || !rowConnected) {
                return false;
            }
        }
        return true;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public static void serialize(String fileName, Graph graph) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(graph);
        } catch (IOException e) {
            throw new RuntimeException("Serialization error!");
        }
    }

    public static Graph deserializeGraph(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Graph) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("No file found!");
        }
    }
}