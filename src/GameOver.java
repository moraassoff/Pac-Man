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

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class GameOver extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;
        String description;

        int startX;
        int startY;
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
        "                   ",
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
    Block logo;
    Block text;

    GameOver() throws Exception {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        logoImage = new ImageIcon(getClass().getResource("./logo.png")).getImage();
        loadMap();
    }

    private ArrayList<String> GetLeaderboards () {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./leaderboards.txt"));
            String line;
            ArrayList<String> scores = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // Ignora líneas vacías
                try {
                    scores.add(line);
                } catch (NumberFormatException ex) {
                    // Ignora líneas no numéricas
                    continue;
                }
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

                int x = c*tileSize;
                int y = r*tileSize;

                if (tileMapChar == 'P') { //pacman logo
                    logo = new Block(logoImage, x, y, tileSize, tileSize);
                } else if (tileMapChar == 'C') { //press any key
                    text = new Block(null, x, y, x, y);
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
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(Color.WHITE);
        g.drawString("MEJORES PUNTAJES", logo.x, logo.y + 100);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(Color.WHITE);
        g.drawString("PRESIONA E PARA REGRESAR", text.x -6, text.y);
        
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

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_E) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            MainMenu mainMenu;
            try {
                mainMenu = new MainMenu();
                frame.add(mainMenu);
                frame.pack();
                mainMenu.requestFocus();
                frame.setVisible(true);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}

}
