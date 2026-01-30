package main.pieces;

import main.GamePanel;

public class Pawn extends Pieces {
    public boolean justMovedTwoSquares = false;

    public Pawn(int color, int col, int row) {
        super(color, col, row);
        this.type = "Pawn";

        if (color == GamePanel.WHITE) {
            image = getImage("pieces/white-pawn");
        } else {
            image = getImage("pieces/black-pawn");
        }
    }
    
    @Override
    public boolean canMove(int destCol, int destRow) {
        collision = null;

        int direction = (color == GamePanel.WHITE) ? -1 : 1;
        int startRow = (color == GamePanel.WHITE) ? 6 : 1;

        if (withinBoard(destCol, destRow)) {

            // Standard move
            if (destCol == preCol && destRow == preRow + direction) {
                if (collisionCheck(destCol, destRow) == null) {
                    return true;
                }
            }

            // Initial double move
            if (destCol == preCol && destRow == preRow + 2 * direction && preRow == startRow) {
                if (collisionCheck(destCol, preRow + direction) == null && collisionCheck(destCol, destRow) == null) {
                    return true;
                }
            }

            // Capturing
            if (Math.abs(destCol - preCol) == 1 && destRow == preRow + direction) {
                Pieces target = collisionCheck(destCol, destRow);
                if (target != null && target.color != this.color) {
                    collision = target;
                    return true;
                }
            }

            // En passant
            if (Math.abs(destCol - preCol) == 1 && destRow == preRow + direction) {
                Pieces target = collisionCheck(destCol, preRow);
                if (target != null && target instanceof Pawn && target.color != this.color) {
                    Pawn adjacentPawn = (Pawn) target;
                    if (adjacentPawn.justMovedTwoSquares) {
                        collision = adjacentPawn;
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
