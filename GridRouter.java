import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class GridRouter {
    static final int N = 20; // Size of the grid
    static final int[] dx = { 0, 0, -1, 1 }; // Directions: left, right, up, down
    static final int[] dy = { -1, 1, 0, 0 };
    private static final int[][] DIRECTIONS = {
            { 0, 1 }, // Right
            { 1, 0 }, // Down
            { 0, -1 }, // Left
            { -1, 0 } // Up
    };

    static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }

    // Reconstruct the path from end to start using the parent map
    private static List<Point> reconstructPath(Map<Point, Point> parent, Point end) {
        List<Point> path = new ArrayList<>();
        Point current = end;

        // Traverse backwards from the end to the start using the parent map
        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }

        // The path was reconstructed backwards, so we reverse it
        Collections.reverse(path);
        return path;
    }

    private static List<Point> bfs(int[][] grid, Point start, Point end) {
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> parent = new HashMap<>();
        boolean[][] visited = new boolean[N][N];
        queue.add(start);
        visited[start.x][start.y] = true;

        while (!queue.isEmpty()) {
            Point cur = queue.poll();

            // If we reached the destination, reconstruct the path
            if (cur.equals(end)) {
                return reconstructPath(parent, end);
            }

            // Explore the 4 possible directions
            for (int[] direction : DIRECTIONS) {
                int newX = cur.x + direction[0];
                int newY = cur.y + direction[1];

                if (isValid(newX, newY, grid, visited)) {
                    Point next = new Point(newX, newY);
                    queue.add(next);
                    visited[newX][newY] = true;
                    parent.put(next, cur);
                }
            }
        }

        // Return an empty list if no path is found
        return Collections.emptyList();
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
        // Check if it's within bounds, not a barrier (1), and not already visited
        return x >= 0 && x < N && y >= 0 && y < N && grid[x][y] != 1 && !visited[x][y];
    }

    private static int markPath(int[][] grid, List<Point> path, int color) {
        int length = 0;
        for (Point p : path) {
            // If the cell is not already part of a path, mark it
            if (grid[p.x][p.y] == 0) {
                grid[p.x][p.y] = color; // Mark the path with the terminal color
                length++;
            }
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
                } else {
                    System.out.println("No path found for color: " + color);
                }
            }
        }

        // Output the result
        outputResult(grid, connectedPairs, totalLength);

        // Generate PNG image of the grid
        generatePNG(grid, "solucao_X.png");
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

    // Generate PNG image of the grid solution
    private static void generatePNG(int[][] grid, String filename) throws IOException {
        int cellSize = 20; // Size of each grid cell in pixels
        int imgSize = N * cellSize; // Total image size

        BufferedImage image = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Draw the grid
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                // Set color based on grid value
                switch (grid[i][j]) {
                    case 0:
                        g2d.setColor(Color.WHITE);
                        break; // Free space
                    case 1:
                        g2d.setColor(Color.BLACK);
                        break; // Barrier
                    case 2:
                        g2d.setColor(Color.RED);
                        break; // Red terminal
                    case 3:
                        g2d.setColor(Color.GREEN);
                        break; // Green terminal
                    case 4:
                        g2d.setColor(Color.BLUE);
                        break; // Blue terminal
                    default:
                        g2d.setColor(Color.WHITE);
                        break;
                }
                // Fill the grid cell with the corresponding color
                g2d.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
            }
        }

        g2d.dispose();
        ImageIO.write(image, "png", new File(filename)); // Save the image as PNG
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