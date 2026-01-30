package main.pieces;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;

public class Pieces{
    public BufferedImage image;
    public int x, y;
    public int row, col, preRow, preCol;
    public int color; // 0: white, 1: black
    public Pieces collision;
    public boolean hasMoved;
    public String type;

    public Pieces(int color, int col, int row) {

        this.row = row;
        this.col = col;
        this.color = color;
        this.hasMoved = false;

        x = getX(col);
        y = getY(row);
        preRow = row;
        preCol = col;
    }

    public int getX(int col) {
        return col * Board.tileSize;
    }
    public int getY(int row) {
        return row * Board.tileSize;
    }
    public int getCol(int x) {
        return (x + Board.tileSize / 2) / Board.tileSize;
    }
    public int getRow(int y) {
        return (y + Board.tileSize / 2) / Board.tileSize;
    }

    public int getIndex(){
        for (int i = 0; i < GamePanel.pieces.size(); i++) {
            if (GamePanel.pieces.get(i) == this){
                return i;
            }
        }
        return -1;
    }

    public BufferedImage getImage(String imagePath) {
        BufferedImage image=null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/" + imagePath + ".png"));
        } catch  (IOException e) {
            System.out.println(e);
        }
        return image;
    }
    
    public void updatePosition(){
        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
    }

    public void resetPosition(){
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }

    public boolean canMove(int destCol, int destRow){
        return false;
    }

    public boolean withinBoard(int destCol, int destRow){
        return destCol >=0 && destCol <8 && destRow >=0 && destRow <8;
    }

    public Pieces collisionCheck(int destCol, int destRow){
        for (Pieces p : GamePanel.simPieces) {
            if (p.col == destCol && p.row == destRow && p != this) {
                return p;
            }
        }
        return null;
    }

    public boolean isValidSquare(int destCol, int destRow){
        collision = collisionCheck(destCol, destRow);

        if (collision == null) {
            return true;
        } else{
            if (collision.color != this.color) {
                return true;
            } else {
                collision = null;
            }
        }
        return false;
    }

    public boolean isPathClear(int destCol, int destRow){
        // Calculate direction of movement (-1, 0, or 1)
        int colStep = Integer.compare(destCol, preCol);
        int rowStep = Integer.compare(destRow, preRow);

        // Check each square along the path (excluding start and destination)
        int checkCol = preCol + colStep;
        int checkRow = preRow + rowStep;

        while (checkCol != destCol || checkRow != destRow) {
            if (collisionCheck(checkCol, checkRow) != null) {
                return false;
            }
            checkCol += colStep;
            checkRow += rowStep;
        }
        return true;
    }

    public void draw(Graphics2D g2){
        g2.drawImage(image, x, y, Board.tileSize, Board.tileSize, null);
    }
}
