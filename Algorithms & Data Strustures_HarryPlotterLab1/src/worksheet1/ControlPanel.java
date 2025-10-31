package worksheet1;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.util.*;

class ControlPanel extends JPanel {
    private final GraphPanel graph;
    private final JCheckBox sinBox = new JCheckBox("sin(x)", true);
    private final JCheckBox cosBox = new JCheckBox("cos(x)");
    private final JCheckBox quadBox = new JCheckBox("0.1x^2 - 2");
    private final JCheckBox gridBox = new JCheckBox("Grid", true);

    public ControlPanel(GraphPanel graph) {
        this.graph = graph;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(280, 0));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // --- Title ---
        JLabel title = new JLabel("Controls");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        // --- Function selection ---
        JPanel fnPanel = new JPanel();
        fnPanel.setLayout(new BoxLayout(fnPanel, BoxLayout.Y_AXIS));
        fnPanel.setBorder(BorderFactory.createTitledBorder("Functions"));
        fnPanel.add(sinBox);
        fnPanel.add(cosBox);
        fnPanel.add(quadBox);

        // --- Options ---
        JPanel optPanel = new JPanel();
        optPanel.setLayout(new BoxLayout(optPanel, BoxLayout.Y_AXIS));
        optPanel.setBorder(BorderFactory.createTitledBorder("Options"));
        optPanel.add(gridBox);

        // --- Action buttons ---
        JButton apply = new JButton("Apply selection");
        JButton reset = new JButton("Reset view (Esc)");
        JButton load  = new JButton("Load data file…");
        JButton zeros = new JButton("Highlight zeros");
        JButton extrema = new JButton("Find extrema");
        JButton histogram = new JButton("Show histogram");

        JPanel btns = new JPanel(new GridLayout(0, 1, 8, 8));
        btns.setBorder(BorderFactory.createTitledBorder("Actions"));
        btns.add(apply);
        btns.add(reset);
        btns.add(load);
        btns.add(zeros);
        btns.add(extrema);
        btns.add(histogram);

        // --- Layout composition ---
        add(title, BorderLayout.NORTH);
        add(fnPanel, BorderLayout.CENTER);
        add(optPanel, BorderLayout.SOUTH);

        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.add(btns, BorderLayout.CENTER);
        add(southWrapper, BorderLayout.PAGE_END);

        // --- Button logic ---
        apply.addActionListener(e -> refreshFunctions());
        reset.addActionListener(e -> graph.resetView());
        gridBox.addActionListener(e -> graph.setShowGrid(gridBox.isSelected()));
        load.addActionListener(e -> loadDataSeries());
        zeros.addActionListener(e -> graph.highlightZeros());
        extrema.addActionListener(e -> graph.findExtrema());
        histogram.addActionListener(e -> graph.toggleHistogram()); // ✅ new feature

        // Initial state
        refreshFunctions();
    }

    // --- Helper: refresh function selection ---
    private void refreshFunctions() {
        graph.clearFunctions();
        if (sinBox.isSelected())
            graph.addFunction(new PlottableFunction("sin(x)", new Color(220, 20, 60), Math::sin));
        if (cosBox.isSelected())
            graph.addFunction(new PlottableFunction("cos(x)", new Color(34, 139, 34), Math::cos));
        if (quadBox.isSelected())
            graph.addFunction(new PlottableFunction("0.1x^2 - 2", new Color(30, 144, 255), x -> 0.1 * x * x - 2));
        graph.requestFocusInWindow();
    }

    // --- Helper: load text data from file ---
    private void loadDataSeries() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open text data – one y per line or x y per line");
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                java.util.List<String> lines = Files.readAllLines(f.toPath());
                java.util.List<Double> xs = new ArrayList<>();
                java.util.List<Double> ys = new ArrayList<>();
                int i = 0;
                for (String line : lines) {
                    String t = line.trim();
                    if (t.isEmpty()) continue;
                    String[] parts = t.split("\\s+");
                    if (parts.length >= 2) {
                        xs.add(Double.parseDouble(parts[0]));
                        ys.add(Double.parseDouble(parts[1]));
                    } else {
                        xs.add((double) i++);
                        ys.add(Double.parseDouble(parts[0]));
                    }
                }

                // --- Create plottable data function ---
                PlottableFunction dataFn = new PlottableFunction("data(series)", new Color(128, 0, 128), x -> {
                    if (xs.isEmpty()) return Double.NaN;
                    int idx = 0;
                    while (idx + 1 < xs.size() && xs.get(idx + 1) < x) idx++;
                    if (idx + 1 >= xs.size()) return ys.get(ys.size() - 1);
                    double x0 = xs.get(idx), x1 = xs.get(idx + 1);
                    double y0 = ys.get(idx), y1 = ys.get(idx + 1);
                    if (x1 == x0) return y0;
                    double t = (x - x0) / (x1 - x0);
                    return y0 * (1 - t) + y1 * t;
                });
                graph.addFunction(dataFn);
                graph.setHistogramData(ys); // ✅ pass to GraphPanel for histogram
                graph.requestFocusInWindow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to load: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
