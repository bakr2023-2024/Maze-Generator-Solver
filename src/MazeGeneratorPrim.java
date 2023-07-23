import java.util.*;

public class MazeGeneratorPrim {
    int[][] grid;
    int r;
    int c;
    Random rand = new Random();

    MazeGeneratorPrim(int m, int n) {
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
        int weight;

        Edge(Vertex x, Vertex y, int w) {
            this.from = x;
            this.to = y;
            this.weight = w;
        }

        public String toString() {
            return this.from + "<->" + this.to + ":" + this.weight;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Edge edge = (Edge) o;
            return (from.equals(edge.from) && to.equals(edge.to)) || (from.equals(edge.to) && to.equals(edge.from));
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }

    HashSet<Vertex> visited = new HashSet<Vertex>();
    ArrayList<Edge> sol = new ArrayList<Edge>();
    ArrayList<Edge> remain = new ArrayList<Edge>();
    HashMap<Vertex, List<Edge>> map = new HashMap<Vertex, List<Edge>>();

    private boolean collide(int x, int y) {
        if (x >= 0 && x < this.r && y >= 0 && y < this.c)
            return false;
        return true;
    }

    private void setMap() {
        for (int i = 0; i < this.r; i++) {
            for (int j = 0; j < this.c; j++) {
                Vertex k = new Vertex(i, j);
                this.map.put(k, new ArrayList<Edge>());
            }
        }
        for (int i = 0; i < this.r; i++) {
            for (int j = 0; j < this.c; j++) {
                Vertex k = new Vertex(i, j);
                if (!collide(k.x + 1, k.y)) {
                    int weight = rand.nextInt(this.r * this.c);
                    Vertex neighbor = new Vertex(i + 1, j);
                    this.map.get(k).add(new Edge(k, neighbor, weight));
                    this.map.get(neighbor).add(new Edge(neighbor, k, weight));
                }
                if (!collide(k.x - 1, k.y)) {
                    int weight = rand.nextInt(this.r * this.c);
                    Vertex neighbor = new Vertex(i - 1, j);
                    this.map.get(k).add(new Edge(k, neighbor, weight));
                    this.map.get(neighbor).add(new Edge(neighbor, k, weight));
                }
                if (!collide(k.x, k.y + 1)) {
                    int weight = rand.nextInt(this.r * this.c);
                    Vertex neighbor = new Vertex(i, j + 1);
                    this.map.get(k).add(new Edge(k, neighbor, weight));
                    this.map.get(neighbor).add(new Edge(neighbor, k, weight));
                }
                if (!collide(k.x, k.y - 1)) {
                    int weight = rand.nextInt(this.r * this.c);
                    Vertex neighbor = new Vertex(i, j - 1);
                    this.map.get(k).add(new Edge(k, neighbor, weight));
                    this.map.get(neighbor).add(new Edge(neighbor, k, weight));
                }
            }
        }
    }

    public int[][] generateMaze() {
        setMap();
        this.visited.add(new Vertex(rand.nextInt(this.r), rand.nextInt(this.c)));
        while (this.visited.size() < this.r * this.c) {
            PriorityQueue<Edge> queue = new PriorityQueue<Edge>(new Comparator<Edge>() {
                public int compare(Edge e1, Edge e2) {
                    return e1.weight - e2.weight;
                }
            });
            for (Vertex vertex : this.visited) {
                for (Edge e : this.map.get(vertex)) {
                    if ((this.visited.contains(e.from) && !this.visited.contains(e.to))
                            || (this.visited.contains(e.to) && !this.visited.contains(e.from))) {
                        queue.add(e);
                    }
                }
            }
            Edge e = queue.poll();
            if (!this.visited.contains(e.from))
                this.visited.add(e.from);
            else
                this.visited.add(e.to);
            this.sol.add(e);
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
