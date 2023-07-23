import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.*;
import java.io.File;

public class App {
    public static class MazeComponent extends JComponent
            implements MouseListener, MazeSolver.Observer, MouseWheelListener {
        int[][] maze;
        int[][] copy;
        int cellWidth = 0;
        int cellHeight = 0;
        int cellSize = 0;
        int xOffset = 0;
        int yOffset = 0;
        int startx = -1;
        int starty = -1;
        int endx = -1;
        int endy = -1;
        int explored;
        int path;
        boolean running = false;

        public MazeComponent(int[][] maze) {
            this.maze = maze;
            addMouseListener(this);
            addMouseWheelListener(this);
        }

        @Override
        public void updateMaze(int[][] arr) {
            this.maze = arr;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    repaint();
                }
            });
        }

        public void resetMaze() {
            for (int i = 0; i < this.maze.length; i++)
                for (int j = 0; j < this.maze[0].length; j++)
                    if (this.maze[i][j] == -1 || this.maze[i][j] == -5 || this.maze[i][j] == -6 || this.maze[i][j] == -3
                            || this.maze[i][j] == -2)
                        this.maze[i][j] = 0;
            revalidate();
            repaint();
        }

        public void solve(String type, Runnable success, Runnable fail) {
            this.path = -1;
            this.explored = -1;
            this.running = true;
            MazeSolver solver = new MazeSolver();
            MazeSolver.keepSolving = true;
            solver.addObserver(this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    solver.solveMaze(copy, starty, startx, endy, endx, type);
                    maze = solver.map;
                    if (solver.found) {
                        for (int i = 0; i < solver.map.length; i++) {
                            for (int j = 0; j < solver.map[0].length; j++) {
                                if (solver.map[i][j] == -1)
                                    explored++;
                                else if (solver.map[i][j] == -5) {
                                    explored++;
                                    path++;
                                }
                            }
                        }
                        success.run();
                    } else {
                        fail.run();
                    }
                    running = false;
                }
            }).start();
        }

        private double scale = 1.0;

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                double scaleChange = -e.getUnitsToScroll() * 0.1;
                double newScale = scale + scaleChange;
                if (newScale > 0.1 && newScale < 10) {
                    scale = newScale;
                    repaint();
                }
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int x = (int) (e.getX() / scale);
            int y = (int) (e.getY() / scale);
            int row = (y - yOffset) / this.cellSize;
            int column = (x - xOffset) / this.cellSize;
            if (row >= 0 && row < this.maze.length && column >= 0 && column < this.maze[0].length
                    && this.maze[row][column] == 0) {
                if (startx == -1 && starty == -1) {
                    startx = column;
                    starty = row;
                    this.maze[starty][startx] = -2;
                    repaint();
                } else if (endx == -1 && endy == -1) {
                    endx = column;
                    endy = row;
                    this.maze[endy][endx] = -3;
                    repaint();
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(720, 720);
        }

        public void setMaze(int[][] arr) {
            this.maze = arr;
            this.startx = -1;
            this.endx = -1;
            this.starty = -1;
            this.endy = -1;
            this.copy = new int[maze.length][maze[0].length];
            for (int i = 0; i < this.copy.length; i++) {
                for (int j = 0; j < this.copy[0].length; j++) {
                    this.copy[i][j] = this.maze[i][j];
                }
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.scale(this.scale, this.scale);
            this.cellWidth = this.getWidth() / this.maze[0].length;
            this.cellHeight = this.getHeight() / this.maze.length;
            this.cellSize = Math.min(this.cellWidth, this.cellHeight);
            this.xOffset = (this.getWidth() - (this.cellSize * this.maze[0].length)) / 2;
            this.yOffset = (this.getHeight() - (this.cellSize * this.maze.length)) / 2;
            for (int i = 0; i < this.maze.length; i++) {
                for (int j = 0; j < this.maze[0].length; j++) {
                    if (this.maze[i][j] == 1) {
                        g2d.setColor(Color.BLACK);
                        g2d.fillRect(this.xOffset + j * this.cellSize, this.yOffset + i * this.cellSize, this.cellSize,
                                this.cellSize);
                    } else if (this.maze[i][j] == 0) {
                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(this.xOffset + j * this.cellSize, this.yOffset + i * this.cellSize, this.cellSize,
                                this.cellSize);
                    } else if (i == this.starty && j == this.startx) {
                        g2d.setColor(Color.BLUE);
                        g2d.fillRect(this.xOffset + j * this.cellSize, this.yOffset + i * this.cellSize, this.cellSize,
                                this.cellSize);
                    } else if (i == this.endy && j == this.endx) {
                        g2d.setColor(Color.MAGENTA);
                        g2d.fillRect(this.xOffset + j * this.cellSize, this.yOffset + i * this.cellSize, this.cellSize,
                                this.cellSize);
                    } else if (this.maze[i][j] == -6) {
                        g2d.setColor(Color.RED);
                        g2d.fillRect(this.xOffset + j * this.cellSize, this.yOffset + i * this.cellSize, this.cellSize,
                                this.cellSize);
                    } else if (this.maze[i][j] == -1) {
                        g2d.setColor(Color.YELLOW);
                        g2d.fillRect(this.xOffset + j * this.cellSize, this.yOffset + i * this.cellSize, this.cellSize,
                                this.cellSize);
                    } else if (this.maze[i][j] == -5) {
                        g2d.setColor(Color.GREEN);
                        g2d.fillRect(this.xOffset + j * this.cellSize, this.yOffset + i * this.cellSize, this.cellSize,
                                this.cellSize);
                    }
                }
            }
        }

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Maze Generator+Solver");
        frame.setVisible(true);
        frame.setSize(1280, 720);
        frame.setLayout(new BorderLayout());
        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        frame.add(leftPanel, BorderLayout.WEST);
        int[][] defaultTemplate = { { 0, 0 } };
        MazeComponent component = new MazeComponent(defaultTemplate);
        leftPanel.add(component, BorderLayout.CENTER);
        component.revalidate();
        component.repaint();
        rightPanel.setLayout(new GridBagLayout());
        frame.add(rightPanel, BorderLayout.EAST);
        String[] algorithms = { "Aldous-Broder", "Binary Tree", "Eller", "Growing Forest", "Growing Tree",
                "Hunt and Kill", "Kruskal", "Prim", "Recursive Backtracker", "Recursive Division", "SideWinder",
                "Wilson" };
        JComboBox<String> algorithmList = new JComboBox<>(algorithms);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        JLabel generateTitle = new JLabel("Generation Algorithms");
        rightPanel.add(generateTitle, c);
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        rightPanel.add(algorithmList, c);
        JPanel spinnerPanel = new JPanel();
        spinnerPanel.setLayout(new GridLayout(2, 2));
        spinnerPanel.setMaximumSize(new Dimension(60, 40));
        c.gridy = 1;
        rightPanel.add(spinnerPanel, c);
        JSpinner rowSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 100, 1));
        JSpinner columnSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 100, 1));
        spinnerPanel.add(new JLabel("Rows: "));
        spinnerPanel.add(rowSpinner);
        spinnerPanel.add(new JLabel("Columns: "));
        spinnerPanel.add(columnSpinner);
        JButton generate = new JButton("Generate");
        c.gridy = 4;
        rightPanel.add(generate, c);
        JLabel generationTimeText = new JLabel();
        generationTimeText.setText("generated in: nil ms");
        c.gridy = 5;
        rightPanel.add(generationTimeText, c);
        generate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!component.running) {
                    generationTimeText.setForeground(Color.BLACK);
                    int rows = (int) rowSpinner.getValue();
                    int columns = (int) columnSpinner.getValue();
                    if (algorithmList.getSelectedItem().equals("Aldous-Broder")) {
                        MazeGeneratorAldousBroder maze1 = new MazeGeneratorAldousBroder(rows, columns);
                        double start = System.nanoTime();
                        int[][] arr = maze1.generateMaze();
                        double end = (System.nanoTime() - start) / 1e6;
                        component.setMaze(arr);
                        generationTimeText.setText(String.format("generated in: %.1f ms", end));
                    } else if (algorithmList.getSelectedItem().equals("Binary Tree")) {
                        MazeGeneratorBinaryTree maze2 = new MazeGeneratorBinaryTree(rows, columns);
                        double start = System.nanoTime();
                        int[][] arr = maze2.generateMaze();
                        double end = (System.nanoTime() - start) / 1e6;
                        component.setMaze(arr);
                        generationTimeText.setText(String.format("generated in: %.1f ms", end));
                    } else if (algorithmList.getSelectedItem().equals("Eller")) {
                        MazeGeneratorEller maze3 = new MazeGeneratorEller(rows, columns);
                        double start = System.nanoTime();
                        int[][] arr = maze3.generateMaze();
                        double end = (System.nanoTime() - start) / 1e6;
                        component.setMaze(arr);
                        generationTimeText.setText(String.format("generated in: %.1f ms", end));
                    } else if (algorithmList.getSelectedItem().equals("SideWinder")) {
                        MazeGeneratorSideWinder maze4 = new MazeGeneratorSideWinder(rows, columns);
                        double start = System.nanoTime();
                        int[][] arr = maze4.generateMaze();
                        double end = (System.nanoTime() - start) / 1e6;
                        component.setMaze(arr);
                        generationTimeText.setText(String.format("generated in: %.1f ms", end));
                    } else if (algorithmList.getSelectedItem().equals("Growing Forest")) {
                        MazeGeneratorGrowingForest maze5 = new MazeGeneratorGrowingForest(rows, columns);
                        double start = System.nanoTime();
                        int[][] arr = maze5.generateMaze();
                        double end = (System.nanoTime() - start) / 1e6;
                        component.setMaze(arr);
                        generationTimeText.setText(String.format("generated in: %.1f ms", end));
                    } else if (algorithmList.getSelectedItem().equals("Growing Tree")) {
                        MazeGeneratorGrowingTree maze6 = new MazeGeneratorGrowingTree(rows, columns);
                        double start = System.nanoTime();
                        int[][] arr = maze6.generateMaze();
                        double end = (System.nanoTime() - start) / 1e6;
                        component.setMaze(arr);
                        generationTimeText.setText(String.format("generated in: %.1f ms", end));
                    } else if (algorithmList.getSelectedItem().equals("Hunt and Kill")) {
                        MazeGeneratorHuntAndKill maze7 = new MazeGeneratorHuntAndKill(rows, columns);
                        double start = System.nanoTime();
                        int[][] arr = maze7.generateMaze();
                        double end = (System.nanoTime() - start) / 1e6;
                        component.setMaze(arr);
                        generationTimeText.setText(String.format("generated in: %.1f ms", end));
                    } else if (algorithmList.getSelectedItem().equals("Kruskal")) {
                        MazeGeneratorKruskal maze8 = new MazeGeneratorKruskal(rows, columns);
                        double start = System.nanoTime();
                        int[][] arr = maze8.generateMaze();
                        double end = (System.nanoTime() - start) / 1e6;
                        component.setMaze(arr);
                        generationTimeText.setText(String.format("generated in: %.1f ms", end));
                    } else if (algorithmList.getSelectedItem().equals("Prim")) {
                        MazeGeneratorPrim maze9 = new MazeGeneratorPrim(rows, columns);
                        double start = System.nanoTime();
                        int[][] arr = maze9.generateMaze();
                        double end = (System.nanoTime() - start) / 1e6;
                        component.setMaze(arr);
                        generationTimeText.setText(String.format("generated in: %.1f ms", end));
                    } else if (algorithmList.getSelectedItem().equals("Recursive Backtracker")) {
                        MazeGeneratorDFS maze10 = new MazeGeneratorDFS(rows, columns);
                        double start = System.nanoTime();
                        int[][] arr = maze10.generateMaze();
                        double end = (System.nanoTime() - start) / 1e6;
                        component.setMaze(arr);
                        generationTimeText.setText(String.format("generated in: %.1f ms", end));
                    } else if (algorithmList.getSelectedItem().equals("Recursive Division")) {
                        MazeGeneratorRecDiv maze11 = new MazeGeneratorRecDiv(rows, columns);
                        double start = System.nanoTime();
                        int[][] arr = maze11.generateMaze();
                        double end = (System.nanoTime() - start) / 1e6;
                        component.setMaze(arr);
                        generationTimeText.setText(String.format("generated in: %.1f ms", end));
                    } else if (algorithmList.getSelectedItem().equals("Wilson")) {
                        MazeGeneratorWilson maze12 = new MazeGeneratorWilson(rows, columns);
                        double start = System.nanoTime();
                        int[][] arr = maze12.generateMaze();
                        double end = (System.nanoTime() - start) / 1e6;
                        component.setMaze(arr);
                        generationTimeText.setText(String.format("generated in: %.1f ms", end));
                    }
                } else {
                    generationTimeText.setForeground(Color.RED);
                    generationTimeText.setText("Solver still running");
                }
            }
        });
        JTextArea title = new JTextArea("MazeGenerator\n+Solver");
        title.setEditable(false);
        title.setBackground(rightPanel.getBackground());
        title.setFont(new Font("Papyrus", Font.BOLD, 20));
        c.gridy = 0;
        c.weighty = 1;
        rightPanel.add(title, c);
        c.weighty = 0;
        String[] solvingalgorithms = { "Breadth-First Search", "Depth-First Search", "A* Search", "Best-First Search",
                "Wall Follower", "Pledge", "Chain", "Trémaux", "Dead-end filler" };
        JComboBox<String> comboBox = new JComboBox<>(solvingalgorithms);
        JTextArea solveText = new JTextArea(
                "After generating the maze\nselect start and end cells\nand method of solving\nBFS or DFS, then click solve");
        solveText.setBackground(rightPanel.getBackground());
        solveText.setEditable(false);
        c.gridy = 6;
        JLabel solveTitle = new JLabel("Solving Algorithms");
        rightPanel.add(solveTitle, c);
        c.gridy = 7;
        rightPanel.add(comboBox, c);
        c.gridy = 8;
        rightPanel.add(solveText, c);
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 5, 1000, 50);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    MazeSolver.delay = (int) source.getValue();
                }
            }
        });
        c.gridy = 9;
        rightPanel.add(slider, c);
        c.gridy = 10;
        JButton solveButton = new JButton("Solve");
        rightPanel.add(solveButton, c);
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAlgorithm = (String) comboBox.getSelectedItem();
                if (!component.running) {
                    if (component.startx == -1 || component.starty == -1 || component.endx == -1 || component.endy == -1
                            || selectedAlgorithm == null) {
                        solveText.setForeground(Color.RED);
                        solveText.setText(
                                "please make sure that\nyou generated the maze\nchose start and end\ncells and method of solving\nbefore clicking solve button");
                    } else {
                        String type = (selectedAlgorithm.equals("Breadth-First Search")) ? "BFS"
                                : (selectedAlgorithm.equals("Depth-First Search")) ? "DFS"
                                        : (selectedAlgorithm.equals("A* Search")) ? "A*"
                                                : (selectedAlgorithm.equals("Best-First Search")) ? "Best"
                                                        : (selectedAlgorithm.equals("Wall Follower")) ? "WallFollower"
                                                                : (selectedAlgorithm.equals("Pledge")) ? "Pledge"
                                                                        : (selectedAlgorithm.equals("Chain")) ? "Chain"
                                                                                : (selectedAlgorithm.equals("Trémaux"))
                                                                                        ? "Trémaux"
                                                                                        : (selectedAlgorithm.equals(
                                                                                                "Dead-end filler"))
                                                                                                        ? "Dead-end filling"
                                                                                                        : "";
                        solveButton.setText("Stop");
                        component.solve(type, new Runnable() {
                            @Override
                            public void run() {
                                solveText.setForeground(Color.GREEN);
                                solveText.setText(String.format("Solved using %s\npath cost: %d\nexplored cells: %d",
                                        type, component.path, component.explored));
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                solveText.setForeground(Color.RED);
                                solveText.setText("Can't be solved");
                            }
                        });
                    }
                } else {
                    MazeSolver.keepSolving = false;
                    component.startx = -1;
                    component.starty = -1;
                    component.endx = -1;
                    component.endy = -1;
                    component.resetMaze();
                    solveText.setText("Stopped");
                    solveButton.setText("Solve");
                }
            }
        });
        c.gridy = 11;
        c.weighty = 1;
        JPanel loadSave = new JPanel(new GridLayout(1, 2));
        JButton load = new JButton("Load");
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedfile = fileChooser.getSelectedFile();
                    component.setMaze(InOut.loadMaze(selectedfile));
                }
            }
        });
        JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    InOut.saveMaze(component.maze, path);
                }
            }
        });
        loadSave.add(load);
        loadSave.add(save);
        rightPanel.add(loadSave, c);
        frame.pack();
    }
}
