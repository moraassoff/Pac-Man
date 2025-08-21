import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.awt.FontMetrics;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class GameOver extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x, y, width, height;
        Image image;
        String description;
        int startX, startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }
    }

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private String[] tileMap = {
        "        G          ",
        "      P            ",
        "                   ",
        "  1                ",
        "  2                ",
        "  3                ",
        "  4                ",
        "  5                ",
        "  6                ",
        "  7                ",
        "  8                ",
        "  9                ",
        "  0                ",
        "                   ",
        "                   ",
        "                   ",
        "      C            ",
        "                   ",
        "                   ",
        "                   ",
        "                   "
    };

    private ArrayList<Block> leaderboards = new ArrayList<>();
    private Image logoImage;
    private Block logo;
    private Block text;
    private Block gameOverText;
    private Cliente cliente; // NUEVO: Jugador actual

    // CONSTRUCTOR MODIFICADO
    public GameOver(Cliente cliente) throws Exception {
        this.cliente = cliente;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        logoImage = new ImageIcon(getClass().getResource("./logo.png")).getImage();
        loadMap();
    }

    private ArrayList<String> GetLeaderboards() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./leaderboards.txt"));
            String line;
            ArrayList<String> scores = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                scores.add(line);
            }
            reader.close();
            return scores;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadMap() {
        int count = 0;
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                if (tileMapChar == 'P') {
                    logo = new Block(logoImage, x, y, tileSize, tileSize);
                } else if (tileMapChar == 'C') {
                    text = new Block(null, x, y, x, y);
                } else if (tileMapChar == 'G') {
                    gameOverText = new Block(null, x, y, tileSize, tileSize);
                }

                if (Character.isDigit(tileMapChar) && count < 10) {
                    leaderboards.add(new Block(null, x, y, tileSize, tileSize));
                    count++;
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // GAME OVER text
        if (gameOverText != null) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String gameOverStr = "GAME OVER";
            int stringWidth = g.getFontMetrics().stringWidth(gameOverStr);
            g.drawString(gameOverStr, (boardWidth - stringWidth) / 2, gameOverText.y + 100);
        }

        // JUGADOR y PUNTAJE actual
        if (cliente != null) {
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String info = "Jugador: " + cliente.getName() + " | Puntaje: " + cliente.getScore();
            int width = g.getFontMetrics().stringWidth(info);
            g.drawString(info, (boardWidth - width) / 2, gameOverText.y + 140);
        }

        // Título de Leaderboard
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        String scoreTitle = "MEJORES PUNTAJES";
        int titleWidth = g.getFontMetrics().stringWidth(scoreTitle);
        int xTitle = (boardWidth - titleWidth) / 2;
        int yTitle = logo.y + 150;
        g.drawString(scoreTitle, xTitle, yTitle);

        // Instrucciones
        String pressE = "PRESIONA E PARA REGRESAR";
        int pressEWidth = g.getFontMetrics().stringWidth(pressE);
        int xPressE = (boardWidth - pressEWidth) / 2;
        int yPressE = text.y;
        g.drawString(pressE, xPressE, yPressE);

        // Mostrar top 10
        int count = 0;
        ArrayList<String> scores = GetLeaderboards();
        if (scores != null) {
            for (String score : scores) {
                if (count < leaderboards.size()) {
                    Block block = leaderboards.get(count);
                    block.description = score;
                    count++;
                }
            }
        } else {
            g.drawString("No hay puntajes disponibles", 10, 50);
        }

        for (Block block : leaderboards) {
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(Color.WHITE);
            g.drawString(block.description == null ? "" : block.description, block.x, block.y + 100);
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_E) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            try {
                MainMenu mainMenu = new MainMenu();
                frame.getContentPane().removeAll();
                frame.add(mainMenu);
                frame.pack();
                mainMenu.requestFocus();
                frame.setVisible(true);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override public void actionPerformed(ActionEvent e) {}
}