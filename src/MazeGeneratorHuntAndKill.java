import java.util.*;

public class MazeGeneratorHuntAndKill {
    int[][] grid;
    int r;
    int c;
    Random rand = new Random();

    MazeGeneratorHuntAndKill(int m, int n) {
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

    ArrayList<Vertex> unvisited = new ArrayList<Vertex>();
    ArrayList<Edge> solution = new ArrayList<Edge>();

    private void addVertices() {
        for (int i = 0; i < this.r; i++)
            for (int j = 0; j < this.c; j++)
                this.unvisited.add(new Vertex(i, j));
    }

    private boolean collide(int x, int y) {
        if (x >= 0 && x < this.r && y >= 0 && y < this.c)
            return false;
        return true;
    }

    private Vertex hunt() {
        for (Vertex current : this.unvisited) {
            int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
            List<int[]> list = Arrays.asList(directions);
            Collections.shuffle(list);
            for (int[] direction : list) {
                int nx = direction[0];
                int ny = direction[1];
                Vertex neighbor = new Vertex(current.x + nx, current.y + ny);
                if (!collide(neighbor.x, neighbor.y)
                        && !this.unvisited.contains(neighbor)) {
                    this.solution.add(new Edge(current, neighbor));
                    return current;
                }
            }
        }
        return null;
    }

    public int[][] generateMaze() {
        addVertices();
        Vertex current = this.unvisited.remove(rand.nextInt(this.unvisited.size()));
        while (!this.unvisited.isEmpty()) {
            int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
            List<int[]> list = Arrays.asList(directions);
            Collections.shuffle(list);
            int flag = 0;
            for (int[] direction : list) {
                int nx = direction[0];
                int ny = direction[1];
                Vertex neighbor = new Vertex(current.x + nx, current.y + ny);
                if (!collide(neighbor.x, neighbor.y)
                        && this.unvisited.contains(neighbor)) {
                    this.solution.add(new Edge(current, neighbor));
                    this.unvisited.remove(neighbor);
                    current = neighbor;
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                this.unvisited.remove(current);
                current = hunt();
                this.unvisited.remove(current);
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
