package main;

import javax.swing.JFrame;

/**
 * Main class to launch the Chess game
 * @author toans
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {

        JFrame window = new JFrame("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
    
        GamePanel gp = new GamePanel();
        window.add(gp);
        window.pack();
        
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gp.launchGame();
    }
}
