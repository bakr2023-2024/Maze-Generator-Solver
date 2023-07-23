import java.util.*;

public class MazeGeneratorEller {
    int[][] grid;
    int r;
    int c;
    int[] disjointedSet;

    MazeGeneratorEller(int m, int n) {
        this.r = m;
        this.c = n;
        this.grid = new int[m * 2 + 1][n * 2 + 1];
        this.disjointedSet = new int[m * n];
        Arrays.fill(this.disjointedSet, -1);
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

    HashMap<Vertex, Integer> map = new HashMap<Vertex, Integer>();
    ArrayList<Edge> solution = new ArrayList<Edge>();

    private void addVertices() {
        int index = 0;
        for (int i = 0; i < this.r; i++) {
            for (int j = 0; j < this.c; j++)
                this.map.put(new Vertex(i, j), index++);
        }
    }

    private int find(int f) {
        if (this.disjointedSet[f] < 0) {
            return f;
        } else {
            this.disjointedSet[f] = find(this.disjointedSet[f]);
            return this.disjointedSet[f];
        }
    }

    private void union(Edge e) {
        int x = find(this.map.get(e.from));
        int y = find(this.map.get(e.to));
        if (x != y) {
            this.solution.add(e);
            if (this.disjointedSet[x] <= this.disjointedSet[y]) {
                this.disjointedSet[x] += this.disjointedSet[y];
                this.disjointedSet[y] = x;
            } else {
                this.disjointedSet[y] += this.disjointedSet[x];
                this.disjointedSet[x] = y;
            }
        }
    }

    public int[][] generateMaze() {
        Random rand = new Random(System.nanoTime());
        addVertices();
        for (int i = 0; i < this.r - 1; i++) {
            for (int j = 0; j < this.c - 1; j++) {
                Vertex k = new Vertex(i, j);
                int choose = rand.nextInt(2);
                if (choose == 1)
                    union(new Edge(k, new Vertex(i, j + 1)));
            }
            int checkerBelow = 0;
            for (int j = 0; j < this.c; j++) {
                if (checkerBelow == 0) {
                    union(new Edge(new Vertex(i, j), new Vertex(i + 1, j)));
                    checkerBelow = 1;
                } else {
                    int choose = rand.nextInt(2);
                    if (choose == 1) {
                        union(new Edge(new Vertex(i, j), new Vertex(i + 1, j)));
                    }
                }
                if (j == this.c - 1)
                    continue;
                if (find(this.map.get(new Vertex(i, j))) != find(this.map.get(new Vertex(i, j + 1)))) {
                    checkerBelow = 0;
                }
            }
        }
        for (int j = 0; j < this.c - 1; j++) {
            union(new Edge(new Vertex(this.r - 1, j), new Vertex(this.r - 1, j + 1)));
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