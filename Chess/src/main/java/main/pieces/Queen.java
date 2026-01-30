package main.pieces;

import main.GamePanel;

public class Queen extends Pieces {

    public Queen(int color, int col, int row) {
        super(color, col, row);
        this.type = "Queen";

        if (color == GamePanel.WHITE) {
            image = getImage("pieces/white-queen");
        } else {
            image = getImage("pieces/black-queen");
        }
    }
    
    @Override
    public boolean canMove(int destCol, int destRow){
        if (withinBoard(destCol, destRow)) {
            int colDiff = Math.abs(destCol - preCol);
            int rowDiff = Math.abs(destRow - preRow);
            if (colDiff == rowDiff || colDiff == 0 || rowDiff == 0){
                if (isPathClear(destCol, destRow) && isValidSquare(destCol, destRow)){
                    return true;
                }
            }
        }
        return false;
    }
}
