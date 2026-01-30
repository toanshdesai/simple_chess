package main.pieces;

import main.GamePanel;

public class Knight extends Pieces {

    public Knight(int color, int col, int row) {
        super(color, col, row);
        this.type = "Knight";

        if (color == GamePanel.WHITE) {
            image = getImage("pieces/white-knight");
        } else {
            image = getImage("pieces/black-knight");
        }
    }
    
    @Override
    public boolean canMove(int destCol, int destRow){
        if (withinBoard(destCol, destRow)) {
            int colDiff = Math.abs(destCol - preCol);
            int rowDiff = Math.abs(destRow - preRow);
            if ((colDiff == 2 && rowDiff == 1) || (colDiff == 1 && rowDiff == 2)){
                if (isValidSquare(destCol, destRow)){
                    return true;
                }
            }
        }
        return false;
    }
}
