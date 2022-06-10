import algorithm.GeneticAlgorithm;
import graph.Graph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GraphSolver extends JFrame implements ActionListener {

    private static final String SAVE_FILE = "SAVE.bin";
    public static final String APPLICATION_TITLE = "Graph Solver";
    private final MainPanel mainPanel = new MainPanel();

    JMenuBar jMenuBar = new JMenuBar();

    private JMenu fileMenu = new JMenu("File");
    private JMenuItem setImageMenuItem = new JMenuItem("Set image");

    private JMenu configurationMenu = new JMenu("Configuration");
    private JMenuItem vertexColorMenuItem = new JMenuItem("Vertex color");
    private JMenuItem edgeColorMenuItem = new JMenuItem("Edge color");
    private JMenuItem vertexSizeMenuItem = new JMenuItem("Vertex size");
    private JMenuItem edgeSizeMenuItem = new JMenuItem("Edge size");
    private JMenuItem createDirectedMenuItem = new JCheckBoxMenuItem("Directed edges");

    private JMenu helpMenu = new JMenu("Help");
    private JMenuItem helpMenuItem = new JMenuItem("Help");

    private JMenu geneticAlgorithmMenu = new JMenu("Genetic Algorithm");
    private JMenuItem geneticAlgorithmSettings = new JMenuItem("Settings");
    private JMenuItem getGeneticAlgorithmStart = new JMenuItem("Start");

    private GeneticAlgorithm geneticAlgorithm;

    public static final Integer[] SIZES = {2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40, 42, 44, 46, 48, 50};


    public GraphSolver() {
        super(APPLICATION_TITLE);
        setSize(800, 600);
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                Graph.serialize(SAVE_FILE, mainPanel.getGraph());
            }

            @Override
            public void windowClosing(WindowEvent event) {
                windowClosed(event);
            }
        });

        addActionListeners();
        createMenuBar();
        try {
            mainPanel.setGraph(Graph.deserializeGraph(SAVE_FILE));
            MainPanel.setVertexID(mainPanel.getGraph().getVertices().size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        geneticAlgorithm = new GeneticAlgorithm(mainPanel.getGraph());

        setVisible(true);
    }

    private void addActionListeners() {
        setImageMenuItem.addActionListener(this);
        helpMenuItem.addActionListener(this);
        vertexColorMenuItem.addActionListener(this);
        edgeColorMenuItem.addActionListener(this);
        vertexSizeMenuItem.addActionListener(this);
        edgeSizeMenuItem.addActionListener(this);
        createDirectedMenuItem.addActionListener(this);
        geneticAlgorithmSettings.addActionListener(this);
        getGeneticAlgorithmStart.addActionListener(this);
    }

    private void createMenuBar() {
        fileMenu.add(setImageMenuItem);
        jMenuBar.add(fileMenu);

        configurationMenu.add(vertexColorMenuItem);
        configurationMenu.add(edgeColorMenuItem);
        configurationMenu.addSeparator();
        configurationMenu.add(vertexSizeMenuItem);
        configurationMenu.add(edgeSizeMenuItem);
        configurationMenu.addSeparator();
        configurationMenu.add(createDirectedMenuItem);
        jMenuBar.add(configurationMenu);

        helpMenu.add(helpMenuItem);
        jMenuBar.add(helpMenu);

        geneticAlgorithmMenu.add(geneticAlgorithmSettings);
        geneticAlgorithmMenu.add(getGeneticAlgorithmStart);
        jMenuBar.add(geneticAlgorithmMenu);

        setJMenuBar(jMenuBar);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();

        if (source.equals(setImageMenuItem)) {
            JFileChooser fileChooser = new JFileChooser();
            int value = fileChooser.showSaveDialog(getParent());
            if (value == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage image = ImageIO.read(fileChooser.getSelectedFile());
                    setSize(image.getWidth(), image.getHeight());
                    setLocationRelativeTo(null);
                    mainPanel.setBackgroundImage(fileChooser.getSelectedFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (source.equals(createDirectedMenuItem)) {
            if (createDirectedMenuItem.isSelected()) {
                mainPanel.setDirected(true);
            } else {
                mainPanel.setDirected(false);
            }
        } else if (source.equals(vertexColorMenuItem)) {
            Color color = JColorChooser.showDialog(null, "Choose vertex color", Color.BLACK);
            mainPanel.setVertexColor(color);
        } else if (source.equals(edgeColorMenuItem)) {
            Color color = JColorChooser.showDialog(null, "Choose edge color", Color.BLACK);
            mainPanel.setEdgeColor(color);
        } else if (source.equals(vertexSizeMenuItem)) {
            int size = (Integer) JOptionPane.showInputDialog(null, null, "Choose vertex size", JOptionPane.PLAIN_MESSAGE, null, SIZES, SIZES[0]);
            mainPanel.getGraph().setVertexSize(size);
        } else if (source.equals(edgeSizeMenuItem)) {
            int size = (Integer) JOptionPane.showInputDialog(null, null, "Choose edge size", JOptionPane.PLAIN_MESSAGE, null, SIZES, SIZES[0]);
            mainPanel.getGraph().setEdgeSize(size);
        } else if (source.equals(geneticAlgorithmSettings)) {
            JTextField populationSize = new JTextField(String.valueOf(geneticAlgorithm.getPopulationSize()));
            JTextField crossover = new JTextField(String.valueOf(geneticAlgorithm.getCrossover()));
            JTextField mutation = new JTextField(String.valueOf(geneticAlgorithm.getMutation()));
            JTextField maxGenerations = new JTextField(String.valueOf(geneticAlgorithm.getGenerations()));

            JPanel myPanel = new JPanel();
            myPanel.setLayout(new GridLayout(4, 2));

            myPanel.add(new JLabel("Population size:"));
            myPanel.add(populationSize);
            myPanel.add(new JLabel("Crossover:"));
            myPanel.add(crossover);
            myPanel.add(new JLabel("Mutation:"));
            myPanel.add(mutation);
            myPanel.add(new JLabel("Generations:"));
            myPanel.add(maxGenerations);

            int result = JOptionPane.showConfirmDialog(null, myPanel,
                    "Genetic Algorithm Settings", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                //TODO refactor + case when first throw exception others are not assigned
                if (Integer.parseInt(populationSize.getText()) > 0)
                    geneticAlgorithm.setPopulationSize(Integer.parseInt(populationSize.getText()));
                if (Double.parseDouble(crossover.getText()) > 0 && Double.parseDouble(crossover.getText()) <= 1)
                    geneticAlgorithm.setCrossover(Double.parseDouble(crossover.getText()));
                if (Double.parseDouble(mutation.getText()) > 0 && Double.parseDouble(mutation.getText()) <= 1)
                    geneticAlgorithm.setMutation(Double.parseDouble(mutation.getText()));
                if (Integer.parseInt(maxGenerations.getText()) > 0)
                    geneticAlgorithm.setGenerations(Integer.parseInt(maxGenerations.getText()));
            }
        } else if (source.equals(getGeneticAlgorithmStart)) {
            JTextField initialVertex = new JTextField(String.valueOf(geneticAlgorithm.getStartVertex()));
            JTextField targetVertex = new JTextField(String.valueOf(geneticAlgorithm.getTargetVertex()));

            JPanel myPanel = new JPanel();
            myPanel.setLayout(new GridLayout(2, 2));

            myPanel.add(new JLabel("Start point:"));
            myPanel.add(initialVertex);

            myPanel.add(new JLabel("End point:"));
            myPanel.add(targetVertex);

            int result = JOptionPane.showConfirmDialog(null, myPanel,
                    "Run algorithm", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                int startVertexID = Integer.parseInt(initialVertex.getText());
                int endVertexID = Integer.parseInt(targetVertex.getText());
                if (startVertexID >= 0 && startVertexID <= mainPanel.getGraph().getVertices().size() - 1 &&
                        endVertexID >= 0 && endVertexID <= mainPanel.getGraph().getVertices().size() - 1 &&
                        mainPanel.getGraph().getVertices().size() > 2 &&
                        mainPanel.getGraph().isConnected()) {
                    geneticAlgorithm.setStartVertex(startVertexID);
                    geneticAlgorithm.setTargetVertex(endVertexID);
                    geneticAlgorithm.setMatrix(mainPanel.getGraph().getMatrix());
                    double distance = geneticAlgorithm.start();
                    repaint();
                    if (distance > 0) {
                        JOptionPane.showMessageDialog(this, "Distance = " + distance);
                    } else {
                        JOptionPane.showMessageDialog(this, "Path was not found. Try configuring algorithm parameters.");
                    }
                }
            }
        }






        repaint();
    }

    public static void main(String[] args) {
        new GraphSolver();
    }
}