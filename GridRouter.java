import java.io.*;
import java.util.*;

public class GridRouter {
    static final int N = 20; // Size of the grid
    static final int[] dx = { 0, 0, -1, 1 }; // Directions: left, right, up, down
    static final int[] dy = { -1, 1, 0, 0 };

    static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // BFS to find the shortest path between two terminals
    public static List<Point> bfs(int[][] grid, Point start, Point end) {
        boolean[][] visited = new boolean[N][N];
        Point[][] parent = new Point[N][N]; // Track the path

        Queue<Point> queue = new LinkedList<>();
        queue.add(start);
        visited[start.x][start.y] = true;

        while (!queue.isEmpty()) {
            Point cur = queue.poll();
            if (cur.x == end.x && cur.y == end.y) { // Path found
                return reconstructPath(parent, end);
            }
            for (int i = 0; i < 4; i++) {
                int nx = cur.x + dx[i];
                int ny = cur.y + dy[i];
                if (isValid(nx, ny, grid, visited)) {
                    visited[nx][ny] = true;
                    queue.add(new Point(nx, ny));
                    parent[nx][ny] = cur; // Track the parent
                }
            }
        }
        return new ArrayList<>(); // No path found
    }

    // Reconstruct the path from end to start
    private static List<Point> reconstructPath(Point[][] parent, Point end) {
        List<Point> path = new ArrayList<>();
        for (Point at = end; at != null; at = parent[at.x][at.y]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    // Check if the move is valid (within bounds, not a barrier, and not visited)
    private static boolean isValid(int x, int y, int[][] grid, boolean[][] visited) {
        return x >= 0 && x < N && y >= 0 && y < N && grid[x][y] != 1 && !visited[x][y];
    }

    // Mark the path in the grid and calculate its length
    private static int markPath(int[][] grid, List<Point> path, int color) {
        int length = 0;
        for (Point p : path) {
            grid[p.x][p.y] = color; // Mark the path with the terminal color
            length++;
        }
        return length;
    }

    // Find terminals by color
    private static List<Point> findTerminals(int[][] grid, int color) {
        List<Point> terminals = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (grid[i][j] == color) {
                    terminals.add(new Point(i, j));
                }
            }
        }
        return terminals;
    }

    // Main function to run the routing algorithm
    public static void solveGrid(int[][] grid) throws IOException {
        int totalLength = 0;
        int connectedPairs = 0;

        // Terminal colors: 2 (red), 3 (green), 4 (blue)
        int[] colors = { 2, 3, 4 };

        for (int color : colors) {
            List<Point> terminals = findTerminals(grid, color);
            if (terminals.size() == 2) {
                Point start = terminals.get(0);
                Point end = terminals.get(1);
                List<Point> path = bfs(grid, start, end);
                if (!path.isEmpty()) {
                    totalLength += markPath(grid, path, color); // Mark the path in the grid
                    connectedPairs++;
                }
            }
        }

        // Output the result
        outputResult(grid, connectedPairs, totalLength);
    }

    // Output the result to resultado_X.txt
    private static void outputResult(int[][] grid, int connectedPairs, int totalLength) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("resultado_X.txt"));
        writer.write(connectedPairs + "\n");
        writer.write(totalLength + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                writer.write(grid[i][j] + " ");
            }
            writer.write("\n");
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java GridRouter <input_file>");
            return;
        }

        // Read the input grid from the provided file
        int[][] grid = new int[N][N];
        BufferedReader reader = new BufferedReader(new FileReader(args[0]));

        for (int i = 0; i < N; i++) {
            String line = reader.readLine();
            for (int j = 0; j < N; j++) {
                grid[i][j] = Character.getNumericValue(line.charAt(j));
            }
        }
        reader.close();

        // Solve the grid routing problem
        solveGrid(grid);
    }
}
