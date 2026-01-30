package main.pieces;

import main.GamePanel;

public class King extends Pieces {

    public Pieces castlingRook;

    public King(int color, int col, int row) {
        super(color, col, row);
        this.type = "King";

        if (color == GamePanel.WHITE) {
            image = getImage("pieces/white-king");
        } else {
            image = getImage("pieces/black-king");
        }
    }
    
    @Override
    public boolean canMove(int destCol, int destRow){
        castlingRook = null;

        if (withinBoard(destCol, destRow)) {
            int colDiff = Math.abs(destCol - preCol);
            int rowDiff = Math.abs(destRow - preRow);
            if ((colDiff + rowDiff == 1) || (colDiff == 1 && rowDiff == 1)){
                if (isValidSquare(destCol, destRow)){
                    return true;
                }
            }

            // Castling
            if (hasMoved == false){
                // Kingside castling
                if (destCol == preCol + 2 && destRow == preRow){
                    Pieces rook = collisionCheck(preCol + 3, preRow);
                    if (rook != null && rook instanceof Rook && rook.hasMoved == false){
                        if (collisionCheck(preCol + 1, preRow) == null && collisionCheck(preCol + 2, preRow) == null){
                            castlingRook = rook;
                            return true;
                        }
                    }
                }
                // Queenside castling
                if (destCol == preCol - 2 && destRow == preRow){
                    Pieces rook = collisionCheck(preCol - 4, preRow);
                    if (rook != null && rook instanceof Rook && rook.hasMoved == false){
                        if (collisionCheck(preCol - 1, preRow) == null && collisionCheck(preCol - 2, preRow) == null && collisionCheck(preCol - 3, preRow) == null){
                            castlingRook = rook;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
