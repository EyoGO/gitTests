package algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import graph.Graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GeneticAlgorithm {

    private int startVertex = 0;
    private int targetVertex = 0;
    private List<List<Double>> matrix;

    private int populationSize = 500;
    private double crossover = 0.9;
    private double mutation = 0.1;
    private int generations = 30;

    private Graph graph;

    public GeneticAlgorithm(Graph graph) {
        this.graph = graph;
    }

    public double start() {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(this);
            json = json.replaceAll("\"","\"\"");
        } catch (JsonProcessingException e) {
            //TODO
            e.printStackTrace();
        }

        try {
            //TODO path
            ProcessBuilder builder = new ProcessBuilder(
                    "python", "ga.py", json);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);
                if (line.startsWith("[")) {
                    graph.resetRoutes();
                    return graph.setRoutes(getPath(line));
//                    System.out.println(getPath(line));
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    private List<Integer> getPath(String resp) {
        String trimmed = resp.substring(1, resp.length() - 1);
        String[] path = trimmed.split(", ");
        List<Integer> res = new ArrayList<>();
        if (targetVertex != startVertex) {
            res.add(startVertex);
            for (String s : path) {
                int nextPointID = Integer.parseInt(s);
                if (nextPointID == startVertex)
                    continue;
                if (nextPointID == targetVertex) {
                    res.add(nextPointID);
                    break;
                }
                res.add(nextPointID);
            }
        }
        return res;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public double getCrossover() {
        return crossover;
    }

    public void setCrossover(double crossover) {
        this.crossover = crossover;
    }

    public double getMutation() {
        return mutation;
    }

    public void setMutation(double mutation) {
        this.mutation = mutation;
    }

    public int getGenerations() {
        return generations;
    }

    public void setGenerations(int generations) {
        this.generations = generations;
    }

    public int getStartVertex() {
        return startVertex;
    }

    public void setStartVertex(int startVertex) {
        this.startVertex = startVertex;
    }

    public int getTargetVertex() {
        return targetVertex;
    }

    public void setTargetVertex(int targetVertex) {
        this.targetVertex = targetVertex;
    }

    public List<List<Double>> getMatrix() {
        return matrix;
    }

    public void setMatrix(List<List<Double>> matrix) {
        this.matrix = matrix;
    }
}
