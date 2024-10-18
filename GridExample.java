import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class GridExample {
    private static final int GRID_SIZE = 20;
    private static final Color BLACK = Color.BLACK;
    private static final Color WHITE = Color.WHITE;
    private static final Color RED   = Color.RED;
    private static final Color GREEN = Color.GREEN;
    private static final Color BLUE = Color.BLUE;
    private static int[][] gridConfig = new int[GRID_SIZE][GRID_SIZE];
    
    private static JButton[][] gridButtons = new JButton[GRID_SIZE][GRID_SIZE];
    
    private static int redCount     = 0;// To track the number of red cells
    private static int greenCount   = 0;// To track the number of green cells
    private static int blueCount    = 0;// To track the number of blue cells



    public static void main(String[] args) {
        JFrame frame = new JFrame("Grid Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        

        // Initialize grid buttons
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = new JButton();
                gridButtons[row][col] = button;

                // Define the borders (external grid boundary)
                if (row == 0 || col == 0 || row == GRID_SIZE - 1 || col == GRID_SIZE - 1) {
                    button.setBackground(BLACK);
                    gridConfig[row][col] = 1; // Border cells marked with 1
                } else {
                    button.setBackground(WHITE);
                    gridConfig[row][col] = 0; // Inner cells initialized to 0
                    // Add mouse listener to handle left and right click behavior
                    final int r = row;
                    final int c = col;
                    button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            // comportamento do botão esquerdo: troca entre outras cores e branco
                            if (gridConfig[r][c] == 0) {
                                button.setBackground(BLACK);
                                gridConfig[r][c] = 1;
                            } else {
                                button.setBackground(WHITE); // limpa
                                // registra um pino a menos conforme o tipo
                                if (gridConfig[r][c] == 2) redCount--;  
                                if (gridConfig[r][c] == 3) greenCount--;
                                if (gridConfig[r][c] == 4) blueCount--;
                                gridConfig[r][c] = 0; // registra espaço livre
                            }
                        }else if(SwingUtilities.isRightMouseButton(e)){
                            // Comportamento do botão direito: troca entre 
                            // vermelho, verde, azul, nesta ordem.
                            // antes de apagar, registro que apaguei o pino
                            if (gridConfig[r][c] == 2) redCount--;
                            if (gridConfig[r][c] == 3) greenCount--;
                            if (gridConfig[r][c] == 4) blueCount--;
                            if (redCount < 2) { // red
                                button.setBackground(RED);
                                gridConfig[r][c] = 2; // red color
                                redCount++;
                            }else if (greenCount < 2) { // green
                                button.setBackground(GREEN);
                                gridConfig[r][c] = 3; // green color
                                greenCount++;
                            }else if (blueCount < 2) { // blue
                                button.setBackground(BLUE);
                                gridConfig[r][c] = 4; // blue color
                                blueCount++;
                            }else{
                                // ignora.
                            }
                        }
                        // System.out.println("-----------------------");
                        // printMatrix();
                        // System.out.println("-----------------------");
                        }
                    });
                }
                

                gridPanel.add(button);
            }
        }

        // Button to load grid configuration from a file
        JButton loadButton = new JButton("Carregar");
        loadButton.addActionListener(e -> carregarGridDeArquivo());

        // Button to save grid configuration to a file
        JButton saveButton = new JButton("Salvar");
        saveButton.addActionListener(e -> saveGridToFile());

        // Button to save grid configuration to a file
        JButton helpButton = new JButton("Ajuda");
        helpButton.addActionListener(e -> showHelp());

        // Add components to the frame
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(helpButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        
        
        // Add components to the frame
        frame.add(gridPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setSize(420,420);
        frame.setVisible(true);
        showHelp();
    }

    private static void saveGridToFile() {
        if (redCount != 2 || greenCount != 2 || blueCount != 2) {
            JOptionPane.showMessageDialog(null, "O grid deve conter todo os pinos (vermelho, verde, azul) setados antes de salvar.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("grid.txt"))) {
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    writer.write(String.valueOf(gridConfig[row][col]));
                }
                writer.newLine();
            }
            JOptionPane.showMessageDialog(null, "Grid salvo em grid.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void carregarGridDeArquivo() {
        try (java.io.BufferedReader reader = new BufferedReader(new FileReader("grid.txt"))) {
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null && row < GRID_SIZE) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    int value = Character.getNumericValue(line.charAt(col));
                    gridConfig[row][col] = value;

                    switch (value) {
                        case 0:
                            gridButtons[row][col].setBackground(WHITE);

                            break;
                        case 1:
                            gridButtons[row][col].setBackground(BLACK);
                            break;
                        case 2:
                            gridButtons[row][col].setBackground(RED);
                            redCount++;
                            break;
                        case 3:
                            gridButtons[row][col].setBackground(GREEN);
                            greenCount++;
                            break;
                        case 4:
                            gridButtons[row][col].setBackground(BLUE);
                            blueCount++;
                            break;
                    }
                }
                row++;
            }

            JOptionPane.showMessageDialog(null, "Grid carregado de grid.txt");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao carregar o arquivo.");
        }
    }

    // private static void printMatrix(){
    //     for (int row = 0; row < GRID_SIZE; row++) {
    //         for (int col = 0; col < GRID_SIZE; col++) {
    //             System.out.print(String.valueOf(gridConfig[row][col]));
    //         }
    //         System.out.println();
    //     }
    // }
    private static void showHelp(){
        JOptionPane.showMessageDialog(null, 
            "Instruções: \n " +
            "* Botão esquerdo marca barreiras (preto) ou apaga\n" + 
            "* Botão direito marca os pares de pinos na ordem:\n" + 
            "\tvermelho, \tverde,\tazul.\n" + 
            "Salve ou carregue o grid " + GRID_SIZE +"x" + GRID_SIZE+".");
    }
}
