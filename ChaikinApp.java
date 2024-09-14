import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ChaikinApp extends JPanel implements MouseListener, KeyListener {

    private List<Point> controlPoints = new ArrayList<>();
    private List<List<Point>> chaikinSteps = new ArrayList<>();
    private boolean animating = false;
    private int currentStep = 0;

    public ChaikinApp() {
        this.setPreferredSize(new Dimension(800, 600));
        this.addMouseListener(this);
        this.addKeyListener(this);
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    // Paint method to render the points and lines
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw control points as small circles
        for (Point p : controlPoints) {
            g2d.setColor(Color.RED);
            g2d.fillOval(p.x - 5, p.y - 5, 10, 10);
        }

        // Draw the lines for the current step in the animation
        if (animating && chaikinSteps.size() > 0) {
            List<Point> currentPoints = chaikinSteps.get(currentStep);
            g2d.setColor(Color.BLUE);
            for (int i = 0; i < currentPoints.size() - 1; i++) {
                Point p1 = currentPoints.get(i);
                Point p2 = currentPoints.get(i + 1);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        } else if (controlPoints.size() > 1) {
            g2d.setColor(Color.BLACK);
            for (int i = 0; i < controlPoints.size() - 1; i++) {
                Point p1 = controlPoints.get(i);
                Point p2 = controlPoints.get(i + 1);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    // Chaikin's algorithm: Generate next step of smoothing
    private List<Point> generateChaikinStep(List<Point> points) {
        List<Point> newPoints = new ArrayList<>();
        newPoints.add(points.get(0));
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            // Calculate new points at 1/4 and 3/4 positions
            Point q = new Point((int) (0.75 * p1.x + 0.25 * p2.x), (int) (0.75 * p1.y + 0.25 * p2.y));
            Point r = new Point((int) (0.25 * p1.x + 0.75 * p2.x), (int) (0.25 * p1.y + 0.75 * p2.y));
            newPoints.add(q);
            newPoints.add(r);
        }
        newPoints.add(points.get(points.size()-1));
        if (points.size() < 3) {
            return null;
        }
        return newPoints;
    }

    // Method to compute all Chaikin steps
    private void computeChaikinSteps() {
        chaikinSteps.clear();
        List<Point> currentPoints = new ArrayList<>(controlPoints);
        for (int i = 0; i < 7; i++) {
            currentPoints = generateChaikinStep(currentPoints);
            chaikinSteps.add(currentPoints);
        }
    }

    // Mouse click event to add control points
    @Override
    public void mouseClicked(MouseEvent e) {
        if (!animating) {
            controlPoints.add(e.getPoint());
            repaint();
        }
    }

    // Start the animation on pressing Enter
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!controlPoints.isEmpty() && controlPoints.size() > 2) {
                computeChaikinSteps();
                animating = true;
                currentStep = 0;
                new Timer(500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        currentStep = (currentStep + 1) % chaikinSteps.size();
                        repaint();
                    }
                }).start();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);  // Exit the program on Escape key
        }
    }

    // Other required MouseListener and KeyListener methods
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    // Main method to launch the application
    public static void main(String[] args) {
        JFrame frame = new JFrame("Chaikin's Algorithm Animation");
        ChaikinApp panel = new ChaikinApp();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
