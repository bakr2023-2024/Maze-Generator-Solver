import java.util.*;

public class MazeGeneratorSideWinder {
    int[][] grid;
    int r;
    int c;
    Random rand = new Random();

    MazeGeneratorSideWinder(int m, int n) {
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

    ArrayList<Vertex> run = new ArrayList<Vertex>();
    ArrayList<Edge> solution = new ArrayList<Edge>();

    public int[][] generateMaze() {
        // the edge case is top row
        for (int i = 0; i < this.r; i++) {
            for (int j = 0; j < this.c; j++) {
                if (i == 0) {
                    if (j < this.c - 1)
                        this.solution.add(new Edge(new Vertex(i, j), new Vertex(i, j + 1)));
                    continue;
                }
                int choose = rand.nextInt(2);
                this.run.add(new Vertex(i, j));
                if (choose == 0 || j == this.c - 1) {
                    Vertex test = this.run.get(rand.nextInt(this.run.size()));
                    this.solution.add(new Edge(test, new Vertex(test.x - 1, test.y)));
                    this.run.clear();
                } else {
                    this.solution.add(new Edge(new Vertex(i, j), new Vertex(i, j + 1)));
                    this.run.add(new Vertex(i, j));
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