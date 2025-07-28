import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import java.net.URL;
import java.util.HashSet;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class MainMenu extends JPanel implements ActionListener, KeyListener {
    private Cliente cliente;
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;
        


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
    PacMan pacmanGame = new PacMan(cliente);

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    public boolean startGame = false;

    private String[] tileMap = {
        "                   ",
        "                   ",
        "                   ",
        "                   ",
        "                   ",
        "      P            ",
        "                   ",
        "                   ",
        "                   ",
        "                   ",
        "                   ",
        "                   ",
        "                   ",
        "      C            ",
        "                   ",
        "                   ",
        "                   ",
        "                   ",
        "                   ",
        "                   ",
        "                   "
    };

    private Image logoImage;
    Block logo;
    Block text;


    MainMenu() throws Exception {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        URL file = getClass().getResource("/musica.wav");  // Nota la barra al inicio
        AudioInputStream ais = AudioSystem.getAudioInputStream(file);
        Clip clip = AudioSystem.getClip();
        clip.open(ais);

        clip.setFramePosition(0);
        clip.start();

        logoImage = new ImageIcon(getClass().getResource("./logo.png")).getImage();
        loadMap();
    }

    public void loadMap() {
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c*tileSize;
                int y = r*tileSize;

                if (tileMapChar == 'P') { //pacman logo
                    logo = new Block(logoImage, x, y, tileSize, tileSize);
                } else if (tileMapChar == 'C') { //press start text
                    text = new Block(null, x, y, x, y);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(logo.image, logo.x, logo.y, logo.width + 230, logo.height + 200, null);

        g.setFont(new Font("Pixelate", Font.PLAIN, 18));
        g.setColor(Color.WHITE);
        g.drawString("PRESIONA E PARA EMPEZAR", text.x -6, text.y);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_E) {
        String name = JOptionPane.showInputDialog(this, "Ingresa tu nombre:");

        if (name == null || name.trim().isEmpty()) {
            return;
        }

        cliente = new Cliente(name.trim());

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        PacMan pacmanGame;
        try {
            pacmanGame = new PacMan(cliente);  
            frame.getContentPane().removeAll(); 
            frame.add(pacmanGame);
            frame.pack();
            pacmanGame.requestFocus();
            frame.setVisible(true);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
        if (e.getKeyCode() == KeyEvent.VK_E) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

            PacMan pacmanGame;
            try {
                pacmanGame = new PacMan(cliente);
                frame.add(pacmanGame);
                frame.pack();
                pacmanGame.requestFocus();
                frame.setVisible(true);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}

}
