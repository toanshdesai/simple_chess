package main.pieces;

import main.GamePanel;

public class Bishop extends Pieces {

    public Bishop(int color, int col, int row) {
        super(color, col, row);
        this.type = "Bishop";

        if (color == GamePanel.WHITE) {
            image = getImage("pieces/white-bishop");
        } else {
            image = getImage("pieces/black-bishop");
        }
    }

    @Override
    public boolean canMove(int destCol, int destRow){
        if (withinBoard(destCol, destRow)) {
            int colDiff = Math.abs(destCol - preCol);
            int rowDiff = Math.abs(destRow - preRow);
            if (colDiff == rowDiff){
                if (isPathClear(destCol, destRow) && isValidSquare(destCol, destRow)){
                    return true;
                }
            }
        }
        return false;
    }
}
