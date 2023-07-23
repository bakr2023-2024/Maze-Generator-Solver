import java.util.*;

public class MazeGeneratorBinaryTree {
    int[][] grid;
    int r;
    int c;
    Random rand = new Random();

    MazeGeneratorBinaryTree(int m, int n) {
        this.r = m;
        this.c = n;
        this.grid = new int[m * 2 + 1][n * 2 + 1];
    }

    class Vertex {
        int x;
        int y;

        Vertex(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return "(" + this.x + "," + this.y + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || this.getClass() != obj.getClass())
                return false;
            if (this == obj)
                return true;
            Vertex test = (Vertex) obj;
            return this.x == test.x && this.y == test.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.x, this.y);
        }
    }

    class Edge {
        Vertex from;
        Vertex to;

        Edge(Vertex x, Vertex y) {
            this.from = x;
            this.to = y;
        }

        public String toString() {
            return this.from + "<->" + this.to;
        }
    }

    ArrayList<Edge> solution = new ArrayList<Edge>();

    public int[][] generateMaze() {
        // if you will only look up/left then edge cases are first row first column and
        // first cell
        for (int i = 0; i < this.r; i++) {
            for (int j = 0; j < this.c; j++) {
                if (i == 0 && j == 0)
                    continue;
                if (i == 0)
                    this.solution.add(new Edge(new Vertex(i, j - 1), new Vertex(i, j)));
                else if (j == 0)
                    this.solution.add(new Edge(new Vertex(i - 1, j), new Vertex(i, j)));
                else {
                    int choose = rand.nextInt(2);
                    if (choose == 1)
                        this.solution.add(new Edge(new Vertex(i, j - 1), new Vertex(i, j)));
                    else
                        this.solution.add(new Edge(new Vertex(i - 1, j), new Vertex(i, j)));
                }
            }
        }
        for (int i = 0; i < this.grid.length; i++)
            Arrays.fill(this.grid[i], 1);
        for (Edge e : this.solution) {
            int x1 = e.from.x * 2 + 1;
            int y1 = e.from.y * 2 + 1;
            int x2 = e.to.x * 2 + 1;
            int y2 = e.to.y * 2 + 1;
            this.grid[x1][y1] = 0;
            this.grid[x2][y2] = 0;
            this.grid[(x1 + x2) / 2][(y1 + y2) / 2] = 0;
        }
        return this.grid;
    }
}
