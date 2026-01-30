package main.pieces;

import main.GamePanel;

public class Rook extends Pieces {

    public Rook(int color, int col, int row) {
        super(color, col, row);
        this.type = "Rook";

        if (color == GamePanel.WHITE) {
            image = getImage("pieces/white-rook");
        } else {
            image = getImage("pieces/black-rook");
        }
    }

    @Override
    public boolean canMove(int destCol, int destRow){
        if (withinBoard(destCol, destRow)) {
            int colDiff = Math.abs(destCol - preCol);
            int rowDiff = Math.abs(destRow - preRow);
            if (colDiff == 0 || rowDiff == 0){
                if (isPathClear(destCol, destRow) && isValidSquare(destCol, destRow)){
                    return true;
                }
            }
        }
        return false;
    }
}