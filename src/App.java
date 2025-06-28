import javax.swing.JFrame;
public class App {
    public static void main(String[] args) throws Exception {
       //determinar el tamaño de la ventana
       int rowCount = 21;
       int columnCount = 19;
       int tileSize = 32;
       int boardWidth =  columnCount * tileSize;
       int boardHeight = rowCount * tileSize;


       JFrame frame = new JFrame("Pac Man");
       frame.setVisible(true);
       frame.setSize(boardWidth, boardHeight);
       frame.setLocationRelativeTo(null);
       frame.setResizable(false);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);





// se crea la instancia del frame del juego

        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);
        frame.pack();
        frame.setVisible(true);

    }
}
