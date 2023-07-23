import java.util.*;

public class MazeGeneratorRecDiv {
    int[][] grid;
    int r;
    int c;
    Random rand = new Random();

    class Vertex {
        int x;
        int y;

        Vertex(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    MazeGeneratorRecDiv(int m, int n) {
        this.r = 2 * m + 1;
        this.c = 2 * n + 1;
        this.grid = new int[r][c];
    }

    private int divHorz(int sr, int er, int sc, int ec) {
        int k = 0, k1 = 0;
        do {
            k = rand.nextInt(er - sr + 1) + sr;
        } while (k % 2 != 0);
        do {
            k1 = rand.nextInt(ec - sc + 1) + sc;
        } while (k1 % 2 == 0);
        for (int i = sc; i <= ec; i++) {
            this.grid[k][i] = 1;
        }
        this.grid[k][k1] = 0;
        return k;
    }

    private int divVert(int sr, int er, int sc, int ec) {
        int k = 0, k1 = 0;
        do {
            k = rand.nextInt(ec - sc + 1) + sc;
        } while (k % 2 != 0);
        do {
            k1 = rand.nextInt(er - sr + 1) + sr;
        } while (k1 % 2 == 0);
        for (int i = sr; i <= er; i++) {
            this.grid[i][k] = 1;
        }
        this.grid[k1][k] = 0;
        return k;
    }

    private void recDiv(int i, int j, int m, int n) {
        if ((j - i + 1) <= 1 || (n - m + 1) <= 1) {
            return;
        }
        if ((j - i + 1) > (n - m + 1)) {
            int k = divHorz(i, j, m, n);
            recDiv(i, k - 1, m, n);
            recDiv(k + 1, j, m, n);
        } else if ((n - m + 1) > (j - i + 1)) {
            int k = divVert(i, j, m, n);
            recDiv(i, j, m, k - 1);
            recDiv(i, j, k + 1, n);
        } else {
            int choose = rand.nextInt(1);
            int k = 0;
            if (choose == 0) {
                k = divHorz(i, j, m, n);
                recDiv(i, k - 1, m, n);
                recDiv(k + 1, j, m, n);
            } else {
                k = divVert(i, j, m, n);
                recDiv(i, j, m, k - 1);
                recDiv(i, j, k + 1, n);
            }
        }
    }

    public int[][] generateMaze() {
        recDiv(1, this.grid.length - 2, 1, this.grid[0].length - 2);
        for (int i = 0; i < this.grid[0].length; i++) {
            this.grid[0][i] = 1;
            this.grid[this.grid.length - 1][i] = 1;
        }
        for (int i = 0; i < this.grid.length; i++) {
            this.grid[i][0] = 1;
            this.grid[i][this.grid[0].length - 1] = 1;
        }
        return this.grid;
    }

}
