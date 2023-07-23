import java.io.*;
import java.util.*;

public class InOut {
    public static int[][] loadMaze(File file) {
        int rows = 0;
        boolean cur = false;
        ArrayList<Integer> data = new ArrayList<Integer>();
        try (Scanner scanner = new Scanner(file);) {
            String line;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                for (String str : line.split(" ")) {
                    data.add(Integer.parseInt(str));
                    if (!cur)
                        rows++;
                }
                cur = true;
                if (data.size() % rows != 0)
                    throw new IllegalArgumentException("non-uniform maze dimensions");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOError e) {
            e.printStackTrace();
        }
        if (rows < 2 || rows > 50) {
            throw new IllegalArgumentException("dimension limit is between 2 and 50 inclusive");
        }
        int idx = 0;
        int cols = data.size() / rows;
        int[][] arr = new int[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                arr[i][j] = data.get(idx++);
        return arr;
    }

    public static void saveMaze(int[][] arr, String name) {
        File file = new File(name);
        try (FileWriter writer = new FileWriter(file);) {

            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr[0].length; j++) {
                    writer.write(arr[i][j] + " ");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
