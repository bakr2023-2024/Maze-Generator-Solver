import java.util.*;

public class MazeGeneratorAldousBroder {
    int[][] grid;
    int r;
    int c;
    Random rand = new Random();

    MazeGeneratorAldousBroder(int m, int n) {
        this.grid = new int[m * 2 + 1][n * 2 + 1];
        this.r = m;
        this.c = n;
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

    ArrayList<Edge> sol = new ArrayList<Edge>();
    ArrayList<Vertex> uv = new ArrayList<Vertex>();

    private void addVertices() {
        for (int i = 0; i < this.r; i++) {
            for (int j = 0; j < this.c; j++)
                this.uv.add(new Vertex(i, j));
        }
    }

    private Vertex getN(Vertex c) {
        int d = rand.nextInt(4);
        switch (d) {
            case 0:
                return new Vertex(c.x + 1, c.y);
            case 1:
                return new Vertex(c.x - 1, c.y);
            case 2:
                return new Vertex(c.x, c.y + 1);
            case 3:
                return new Vertex(c.x, c.y - 1);
            default:
                return c;
        }
    }

    private boolean collide(int x, int y) {
        if (x >= 0 && x < this.r && y >= 0 && y < this.c)
            return false;
        return true;
    }

    public int[][] generateMaze() {
        addVertices();
        Vertex c = this.uv.remove(rand.nextInt(this.uv.size()));
        while (!this.uv.isEmpty()) {
            Vertex n = getN(c);
            while (collide(n.x, n.y)) {
                n = getN(c);
            }
            if (this.uv.contains(n)) {
                this.sol.add(new Edge(c, n));
                this.uv.remove(n);
            }
            c = n;
        }
        for (int i = 0; i < this.grid.length; i++)
            Arrays.fill(this.grid[i], 1);
        for (Edge e : this.sol) {
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
