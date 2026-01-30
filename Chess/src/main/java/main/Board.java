package main;

import java.awt.Color;
import java.awt.Graphics2D;

public class Board {
    
    final int rows = 8;
    final int cols = 8;
    public final static int tileSize = 100;

    public void draw(Graphics2D g2) {
        // Draw the chess board here
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if ((row + col) % 2 == 0) {
                    g2.setColor(new Color(210, 165, 125)); // Light brown
                } else {
                    g2.setColor(new Color(175, 135, 95)); // Dark brown
                }
                g2.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
            }
        }
    }
}