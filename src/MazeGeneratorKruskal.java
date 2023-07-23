import java.util.*;

public class MazeGeneratorKruskal {
    int[][] grid;
    int[] disjointset;
    int width;
    int height;

    MazeGeneratorKruskal(int r, int c) {
        this.width = c;
        this.height = r;
        this.grid = new int[r * 2 + 1][c * 2 + 1];
        this.disjointset = new int[r * c];
        Arrays.fill(this.disjointset, -1);
    }

    class Edge {
        Vertex from;
        Vertex to;

        Edge(Vertex x, Vertex y) {
            this.from = x;
            this.to = y;
        }

        public String toString() {
            return this.from + "-" + this.to;
        }
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

    Hashtable<Vertex, Integer> map = new Hashtable<Vertex, Integer>(this.height * this.width);
    ArrayList<Edge> edges = new ArrayList<Edge>();
    ArrayList<Edge> solution = new ArrayList<Edge>();

    private void setVerticesAndEdges() {
        int index = 0;
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                map.put(new Vertex(i, j), index++);
                if (j < this.width - 1)
                    this.edges.add(new Edge(new Vertex(i, j), new Vertex(i, j + 1)));
                if (i < this.height - 1)
                    this.edges.add(new Edge(new Vertex(i, j), new Vertex(i + 1, j)));
            }
        }
    }

    private int find(int x) {
        if (this.disjointset[x] < 0)
            return x;
        else {
            this.disjointset[x] = find(this.disjointset[x]);
            return this.disjointset[x];
        }
    }

    private void union(Edge e) {
        int x = find(this.map.get(e.from));
        int y = find(this.map.get(e.to));
        if (x != y) {
            this.solution.add(e);
            if (this.disjointset[x] <= this.disjointset[y]) {
                this.disjointset[x] += this.disjointset[y];
                this.disjointset[y] = x;
            } else {
                this.disjointset[y] += this.disjointset[x];
                this.disjointset[x] = y;
            }
        }
    }

    public int[][] generateMaze() {
        setVerticesAndEdges();
        Collections.shuffle(this.edges);
        for (Edge e : this.edges)
            union(e);
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