package worksheet1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

class GraphPanel extends JPanel {
    private double xMin = -10, xMax = 10;
    private double yMin = -6,  yMax = 6;
    private boolean showGrid = true;
    private int samples = 1200;

    private final java.util.List<PlottableFunction> functions = new ArrayList<>();

    // highlight points for zeros and extrema
    private final java.util.List<Point2D.Double> zeroPoints = new ArrayList<>();
    private final java.util.List<Point2D.Double> maximaPoints = new ArrayList<>();
    private final java.util.List<Point2D.Double> minimaPoints = new ArrayList<>();

    // histogram data (from loaded file)
    private double[] histValues = null;
    private int histBins = 20;
    private boolean showHistogram = false;

    private Point dragStart = null;
    private Point dragEnd   = null;

    public GraphPanel() {
        setBackground(Color.WHITE);
        setFocusable(true);
        installMouseHandlers();
        installKeyBindings();
    }

    // --- Public API ---
    public void addFunction(PlottableFunction f) {
        functions.add(f);
        repaint();
    }

    public void clearFunctions() {
        functions.clear();
        zeroPoints.clear();
        maximaPoints.clear();
        minimaPoints.clear();
        showHistogram = false;
        histValues = null;
        repaint();
    }

    public void setShowGrid(boolean value) {
        showGrid = value;
        repaint();
    }

    public void resetView() {
        xMin = -10; xMax = 10; yMin = -6; yMax = 6;
        repaint();
    }

    public void setViewport(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = xMin; this.xMax = xMax; this.yMin = yMin; this.yMax = yMax;
        repaint();
    }

    // --- Highlight zeros ---
    public void highlightZeros() {
        zeroPoints.clear();
        for (PlottableFunction f : functions) {
            double[] xs = new double[samples];
            double[] ys = new double[samples];
            for (int i = 0; i < samples; i++) {
                double t = i / (double)(samples - 1);
                double x = xMin + t * (xMax - xMin);
                xs[i] = x;
                ys[i] = f.func.applyAsDouble(x);
            }
            for (int i = 1; i < samples; i++) {
                if (ys[i - 1] * ys[i] < 0) {
                    double x0 = xs[i - 1], x1 = xs[i];
                    double y0 = ys[i - 1], y1 = ys[i];
                    double xZero = x0 - y0 * (x1 - x0) / (y1 - y0);
                    zeroPoints.add(new Point2D.Double(xZero, 0));
                }
            }
        }
        repaint();
    }

    // --- Extrema detection ---
    public void findExtrema() {
        maximaPoints.clear();
        minimaPoints.clear();

        for (PlottableFunction f : functions) {
            double[] xs = new double[samples];
            double[] ys = new double[samples];
            for (int i = 0; i < samples; i++) {
                double t = i / (double)(samples - 1);
                double x = xMin + t * (xMax - xMin);
                xs[i] = x;
                ys[i] = f.func.applyAsDouble(x);
            }

            for (int i = 1; i < samples - 1; i++) {
                double dy1 = ys[i] - ys[i - 1];
                double dy2 = ys[i + 1] - ys[i];
                if (dy1 > 0 && dy2 < 0) {
                    maximaPoints.add(new Point2D.Double(xs[i], ys[i]));
                } else if (dy1 < 0 && dy2 > 0) {
                    minimaPoints.add(new Point2D.Double(xs[i], ys[i]));
                }
            }
        }
        repaint();
    }

    // --- Histogram feature ---
    public void setHistogramData(java.util.List<Double> values) {
        histValues = values.stream().mapToDouble(Double::doubleValue).toArray();
        showHistogram = false; // wait until "Show histogram" clicked
    }

    public void toggleHistogram() {
        showHistogram = !showHistogram;
        repaint();
    }

    // --- Coordinate transforms ---
    private int xToScreen(double x) {
        return (int) Math.round((x - xMin) / (xMax - xMin) * getWidth());
    }

    private int yToScreen(double y) {
        return (int) Math.round((1 - (y - yMin) / (yMax - yMin)) * getHeight());
    }

    private double screenToX(int sx) {
        return xMin + (xMax - xMin) * sx / (double) getWidth();
    }

    private double screenToY(int sy) {
        return yMin + (yMax - yMin) * (1 - sy / (double) getHeight());
    }

    // --- Painting ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGridAndAxes(g2);
        if (showHistogram && histValues != null)
            drawHistogram(g2);
        drawFunctions(g2);
        drawZeroPoints(g2);
        drawExtremaPoints(g2);
        drawRubberBand(g2);
        g2.dispose();
    }

    private void drawGridAndAxes(Graphics2D g2) {
        if (showGrid) {
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(new Color(230, 230, 230));

            double xStep = niceStep((xMax - xMin) / 10);
            double yStep = niceStep((yMax - yMin) / 8);

            for (double x = Math.floor(xMin / xStep) * xStep; x <= xMax; x += xStep)
                g2.drawLine(xToScreen(x), 0, xToScreen(x), getHeight());
            for (double y = Math.floor(yMin / yStep) * yStep; y <= yMax; y += yStep)
                g2.drawLine(0, yToScreen(y), getWidth(), yToScreen(y));
        }

        g2.setColor(new Color(80, 80, 80));
        g2.setStroke(new BasicStroke(1.5f));
        int y0 = yToScreen(0);
        int x0 = xToScreen(0);
        g2.drawLine(0, y0, getWidth(), y0);
        g2.drawLine(x0, 0, x0, getHeight());
    }

    private static double niceStep(double raw) {
        double exp = Math.pow(10, Math.floor(Math.log10(raw)));
        double frac = raw / exp;
        double step;
        if (frac < 1.5) step = 1;
        else if (frac < 3) step = 2;
        else if (frac < 7) step = 5;
        else step = 10;
        return step * exp;
    }

    private void drawFunctions(Graphics2D g2) {
        for (PlottableFunction f : functions) {
            g2.setColor(f.color);
            double[] xs = new double[samples];
            double[] ys = new double[samples];
            for (int i = 0; i < samples; i++) {
                double t = i / (double) (samples - 1);
                double x = xMin + t * (xMax - xMin);
                xs[i] = x;
                ys[i] = f.func.applyAsDouble(x);
            }
            Path2D path = new Path2D.Double();
            boolean started = false;
            for (int i = 0; i < samples; i++) {
                int sx = xToScreen(xs[i]);
                int sy = yToScreen(ys[i]);
                if (!started) { path.moveTo(sx, sy); started = true; }
                else { path.lineTo(sx, sy); }
            }
            g2.setStroke(new BasicStroke(2f));
            g2.draw(path);
        }
    }

    private void drawZeroPoints(Graphics2D g2) {
        g2.setColor(Color.RED);
        for (Point2D.Double p : zeroPoints) {
            int sx = xToScreen(p.x);
            int sy = yToScreen(p.y);
            g2.fill(new Ellipse2D.Double(sx - 4, sy - 4, 8, 8));
        }
    }

    private void drawExtremaPoints(Graphics2D g2) {
        for (Point2D.Double p : maximaPoints) {
            int sx = xToScreen(p.x);
            int sy = yToScreen(p.y);
            Polygon tri = new Polygon(new int[]{sx - 5, sx + 5, sx}, new int[]{sy + 5, sy + 5, sy - 5}, 3);
            g2.setColor(Color.BLUE);
            g2.fill(tri);
        }
        for (Point2D.Double p : minimaPoints) {
            int sx = xToScreen(p.x);
            int sy = yToScreen(p.y);
            Polygon tri = new Polygon(new int[]{sx - 5, sx + 5, sx}, new int[]{sy - 5, sy - 5, sy + 5}, 3);
            g2.setColor(Color.GREEN.darker());
            g2.fill(tri);
        }
    }

    // --- draw histogram ---
    private void drawHistogram(Graphics2D g2) {
        if (histValues == null || histValues.length == 0) return;
        double min = Arrays.stream(histValues).min().orElse(0);
        double max = Arrays.stream(histValues).max().orElse(0);
        if (min == max) return;

        double binSize = (max - min) / histBins;
        int[] counts = new int[histBins];
        for (double v : histValues) {
            int idx = (int) ((v - min) / binSize);
            if (idx < 0) idx = 0;
            if (idx >= histBins) idx = histBins - 1;
            counts[idx]++;
        }
        int maxCount = Arrays.stream(counts).max().orElse(1);

        g2.setColor(new Color(128, 0, 128, 150));
        double binWidth = (xMax - xMin) / histBins;
        for (int i = 0; i < histBins; i++) {
            double x0 = min + i * binSize;
            double height = (counts[i] / (double) maxCount) * (yMax - yMin) * 0.8;
            double y0 = yMin;
            int sx = xToScreen(x0);
            int sy = yToScreen(y0 + height);
            int barWidth = (int) Math.round(getWidth() / (double) histBins);
            int barHeight = yToScreen(y0) - sy;
            g2.fillRect(sx, sy, barWidth, barHeight);
        }
    }

    // --- input handling (unchanged) ---
    private void installMouseHandlers() {
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { dragStart = e.getPoint(); dragEnd = null; repaint(); }
            @Override public void mouseDragged(MouseEvent e) { dragEnd = e.getPoint(); repaint(); }
            @Override public void mouseReleased(MouseEvent e) {
                if (dragStart != null && dragEnd != null) {
                    Rectangle r = rectFromPoints(dragStart, dragEnd);
                    if (r.width > 5 && r.height > 5) {
                        double nxMin = screenToX(r.x);
                        double nxMax = screenToX(r.x + r.width);
                        double nyMax = screenToY(r.y);
                        double nyMin = screenToY(r.y + r.height);
                        setViewport(nxMin, nxMax, nyMin, nyMax);
                    }
                }
                dragStart = dragEnd = null; repaint();
            }
            @Override public void mouseWheelMoved(MouseWheelEvent e) {
                double factor = (e.getWheelRotation() < 0) ? 0.9 : 1.1;
                double cx = screenToX(e.getX());
                double cy = screenToY(e.getY());
                zoomAround(cx, cy, factor);
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
        addMouseWheelListener(ma);
    }

    private void installKeyBindings() {
        int cond = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap im = getInputMap(cond);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("LEFT"), "panLeft");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "panRight");
        im.put(KeyStroke.getKeyStroke("UP"), "panUp");
        im.put(KeyStroke.getKeyStroke("DOWN"), "panDown");
        im.put(KeyStroke.getKeyStroke("ESCAPE"), "reset");

        am.put("panLeft",  new PanAction(-0.1, 0));
        am.put("panRight", new PanAction(+0.1, 0));
        am.put("panUp",    new PanAction(0, -0.1));
        am.put("panDown",  new PanAction(0, +0.1));
        am.put("reset",    new AbstractAction(){ public void actionPerformed(ActionEvent e){ resetView(); }});
    }

    private class PanAction extends AbstractAction {
        private final double dxFrac, dyFrac;
        PanAction(double dxFrac, double dyFrac) { this.dxFrac = dxFrac; this.dyFrac = dyFrac; }
        @Override public void actionPerformed(ActionEvent e) {
            double dx = (xMax - xMin) * dxFrac;
            double dy = (yMax - yMin) * dyFrac;
            setViewport(xMin + dx, xMax + dx, yMin + dy, yMax + dy);
        }
    }

    private void zoomAround(double cx, double cy, double factor) {
        double nxMin = cx + (xMin - cx) * factor;
        double nxMax = cx + (xMax - cx) * factor;
        double nyMin = cy + (yMin - cy) * factor;
        double nyMax = cy + (yMax - cy) * factor;
        setViewport(nxMin, nxMax, nyMin, nyMax);
    }

    private Rectangle rectFromPoints(Point a, Point b) {
        int x = Math.min(a.x, b.x);
        int y = Math.min(a.y, b.y);
        int w = Math.abs(a.x - b.x);
        int h = Math.abs(a.y - b.y);
        return new Rectangle(x, y, w, h);
    }

    private void drawRubberBand(Graphics2D g2) {
        if (dragStart != null && dragEnd != null) {
            Rectangle r = rectFromPoints(dragStart, dragEnd);
            g2.setColor(new Color(30, 144, 255, 50));
            g2.fill(r);
            g2.setColor(new Color(30, 144, 255));
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{6f,6f}, 0f));
            g2.draw(r);
        }
    }
}
