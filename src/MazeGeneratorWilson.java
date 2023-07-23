import java.util.*;

public class MazeGeneratorWilson {
    int[][] grid;
    int r;
    int c;
    Random rand = new Random();

    MazeGeneratorWilson(int m, int n) {
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

    ArrayList<Edge> solution = new ArrayList<Edge>();
    ArrayList<Edge> path = new ArrayList<Edge>();
    ArrayList<Vertex> unvisited = new ArrayList<Vertex>();

    private void addVertices() {
        for (int i = 0; i < this.r; i++) {
            for (int j = 0; j < this.c; j++) {
                this.unvisited.add(new Vertex(i, j));
            }
        }
    }

    private boolean collide(int x, int y) {
        if (x >= 0 && x < this.r && y >= 0 && y < this.c)
            return false;
        return true;
    }

    private void eraseLoop(Vertex current) {
        int idx = -1;
        for (int i = 0; i < this.path.size(); i++) {
            if (this.path.get(i).from.equals(current)) {
                idx = i;
                break;
            }
        }
        if (idx != -1) {
            this.path.subList(idx, this.path.size()).clear();
        }
    }

    public int[][] generateMaze() {
        addVertices();
        this.unvisited.remove(rand.nextInt(this.unvisited.size()));
        while (!this.unvisited.isEmpty()) {
            Vertex current = this.unvisited.get(rand.nextInt(this.unvisited.size()));
            while (this.unvisited.contains(current)) {
                int direction = rand.nextInt(4);
                while ((direction == 0 && collide(current.x + 1, current.y)) ||
                        (direction == 1 && collide(current.x - 1, current.y)) ||
                        (direction == 2 && collide(current.x, current.y + 1)) ||
                        (direction == 3 && collide(current.x, current.y - 1))) {
                    direction = rand.nextInt(4);
                }
                Vertex k;
                switch (direction) {
                    case 0:
                        k = new Vertex(current.x + 1, current.y);
                        this.path.add(new Edge(current, k));
                        current = k;
                        eraseLoop(current);
                        break;
                    case 1:
                        k = new Vertex(current.x - 1, current.y);
                        this.path.add(new Edge(current, k));
                        current = k;
                        eraseLoop(current);
                        break;
                    case 2:
                        k = new Vertex(current.x, current.y + 1);
                        this.path.add(new Edge(current, k));
                        current = k;
                        eraseLoop(current);
                        break;
                    case 3:
                        k = new Vertex(current.x, current.y - 1);
                        this.path.add(new Edge(current, k));
                        current = k;
                        eraseLoop(current);
                        break;
                }
            }
            for (Edge e : this.path) {
                this.unvisited.remove(e.from);
                this.solution.add(e);
            }
            this.path.clear();
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
