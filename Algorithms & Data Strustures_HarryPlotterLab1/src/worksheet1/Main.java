package worksheet1;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Harry Plotter – Lab 1");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            GraphPanel graph = new GraphPanel();
            ControlPanel controls = new ControlPanel(graph);

            frame.add(graph, BorderLayout.CENTER);
            frame.add(controls, BorderLayout.EAST);

            frame.setSize(1100, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            graph.requestFocusInWindow();
        });
    }
}
