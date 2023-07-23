import java.util.*;

public class MazeGeneratorGrowingForest {
    int[][] grid;
    int r;
    int c;
    Random rand = new Random(System.nanoTime());
    int[] disjointedSet;

    MazeGeneratorGrowingForest(int m, int n) {
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

    ArrayList<Vertex> newList = new ArrayList<Vertex>();
    ArrayList<Edge> solution = new ArrayList<Edge>();
    ArrayList<Vertex> active = new ArrayList<Vertex>();
    HashMap<Vertex, Integer> map = new HashMap<Vertex, Integer>();

    private void addVertices() {
        int idx = 0;
        for (int i = 0; i < this.r; i++)
            for (int j = 0; j < this.c; j++) {
                Vertex k = new Vertex(i, j);
                this.newList.add(k);
                this.map.put(k, idx++);
            }
    }

    private boolean collide(int x, int y) {
        if (x >= 0 && x < this.r && y >= 0 && y < this.c)
            return false;
        return true;
    }

    private Vertex check(Vertex current) {
        int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        List<int[]> list = Arrays.asList(directions);
        Collections.shuffle(list);
        for (int[] direction : list) {
            int nx = direction[0];
            int ny = direction[1];
            Vertex neighbor = new Vertex(current.x + nx, current.y + ny);
            if (!collide(neighbor.x, neighbor.y)
                    && this.newList.contains(neighbor)) {
                return neighbor;
            }
        }
        return current;
    }

    private int find(int f) {
        if (this.disjointedSet[f] < 0)
            return f;
        else {
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
        addVertices();
        this.active.add(this.newList.remove(rand.nextInt(this.newList.size())));
        while (!this.active.isEmpty()) {
            Vertex current = this.active.get(rand.nextInt(this.active.size()));
            Vertex result = check(current);
            if (current.equals(result)) {
                this.active.remove(current);
            } else {
                this.active.add(result);
                this.newList.remove(result);
                union(new Edge(current, result));
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
