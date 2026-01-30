package main;

// Imports
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import main.pieces.Bishop;
import main.pieces.King;
import main.pieces.Knight;
import main.pieces.Pawn;
import main.pieces.Pieces;
import main.pieces.Queen;
import main.pieces.Rook;

public class GamePanel extends JPanel implements Runnable {
    public static int height = 800;
    public static int width = 1200;

    Thread gameThread;
    final int FPS = 60;

    Board board = new Board();
    Mouse mouse = new Mouse();

    // Pieces
    public static ArrayList<Pieces> pieces = new ArrayList<>();
    public static ArrayList<Pieces> simPieces = new ArrayList<>();
    ArrayList<Pieces> promotionPieces = new ArrayList<>();
    Pieces selectedPiece;

    // Colors
    public static final int WHITE=0;
    public static final int BLACK=1;
    int currentTurn = WHITE;

    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameOver;

    /**
     * Constructor to set up the game panel
     */
    public GamePanel() {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.decode("#838383"));
        setDoubleBuffered(true);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        //setPieces();
        testPieces();
        copyPieces(pieces, simPieces);
    }

    /**
     * Launch the game thread
     */
    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces(){
        // White pieces
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));

        // Black pieces
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));
    }

    public void testPieces(){
        pieces.add(new Bishop(WHITE, 7, 5));
        pieces.add(new Pawn(WHITE, 3, 2));
        pieces.add(new King(BLACK, 4, 1));
    }

    private void copyPieces(ArrayList<Pieces> source, ArrayList<Pieces> dest){
        dest.clear();
        for (int i=0; i < source.size(); i++){
            dest.add(source.get(i));
        }
    }

    
    @Override
    public void run() {

        // GAME LOOP
        double drawInterval = 1000000000 / FPS; // 1/FPS seconds
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {

            // 1. Update: update information such as character positions
            update();

            // 2. Draw: draw the screen with the updated information
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);

                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Update the objects in the game
     */
    private void update() {

        if (promotion){
            promoting();
        }
        else {
            if (mouse.pressed) {
                int mouseCol = mouse.x / Board.tileSize;
                int mouseRow = mouse.y / Board.tileSize;

                if (selectedPiece == null) {
                    // Select a piece
                    for (Pieces p : simPieces) {
                        if (p.col == mouseCol && p.row == mouseRow && p.color == currentTurn) {
                            selectedPiece = p;
                            break;
                        }
                    }
                } else {
                    // Move the selected piece

                    canMove = false;
                    validSquare = false;

                    copyPieces(pieces,simPieces);

                    selectedPiece.x = mouse.x - (Board.tileSize/2);
                    selectedPiece.y = mouse.y - (Board.tileSize/2);
                    selectedPiece.col = selectedPiece.getCol(selectedPiece.x);
                    selectedPiece.row = selectedPiece.getRow(selectedPiece.y);

                    // Check if the move is valid
                    if (selectedPiece.canMove(mouseCol, mouseRow)) {
                        canMove = true;

                        // If capturing an opponent's piece, remove it from the list
                        if (selectedPiece.collision != null) {
                            simPieces.remove(selectedPiece.collision.getIndex());
                        }

                        if (illegalMove(selectedPiece) == false) {
                            // Move would put own king in check
                            validSquare = true;
                        }
                    }
                }
            }

            if (mouse.pressed == false && selectedPiece != null) {
                // Drop the selected piece
                // Check that the piece actually moved
                boolean notOnSameSquare = selectedPiece.col != selectedPiece.preCol || selectedPiece.row != selectedPiece.preRow;

                if (validSquare && notOnSameSquare){

                    // Commit the move
                    copyPieces(simPieces, pieces);

                    // Check for pawn double move before updatePosition changes preRow
                    boolean pawnMovedTwo = false;
                    if (selectedPiece instanceof Pawn) {
                        pawnMovedTwo = Math.abs(selectedPiece.row - selectedPiece.preRow) == 2;
                    }

                    selectedPiece.updatePosition();
                    selectedPiece.hasMoved = true;

                    // Finalize castling - move the rook to its new position
                    if (selectedPiece instanceof King king) {
                        if (king.castlingRook != null) {
                            // Kingside: rook moves from col 7 to col 5 (next to king on left)
                            // Queenside: rook moves from col 0 to col 3 (next to king on right)
                            if (king.castlingRook.preCol == 7) {
                                // Kingside - rook goes to king's left
                                king.castlingRook.col = king.col - 1;
                            } else {
                                // Queenside - rook goes to king's right
                                king.castlingRook.col = king.col + 1;
                            }
                            king.castlingRook.x = king.castlingRook.getX(king.castlingRook.col);
                            king.castlingRook.updatePosition();
                            king.castlingRook.hasMoved = true;
                        }
                    }

                    // Reset justMovedTwoSquares for all pawns, then set for current pawn if applicable
                    for (Pieces p : pieces) {
                        if (p instanceof Pawn pawn) {
                            pawn.justMovedTwoSquares = false;
                        }
                    }
                    if (pawnMovedTwo) {
                        ((Pawn) selectedPiece).justMovedTwoSquares = true;
                    }

                    promotion = canPromote();

                    // Switch turn
                    if (currentTurn == WHITE) {
                        currentTurn = BLACK;
                    } else {
                        currentTurn = WHITE;
                    }

                    // Check for checkmate
                    if (isCheckmate(currentTurn)) {
                        gameOver = true;
                    }

                } else{
                    copyPieces(pieces, simPieces);
                    selectedPiece.resetPosition();
                    selectedPiece = null;
                }

                if (!promotion) {
                    selectedPiece = null;
                }
            }
        }
    }

    private boolean isCheckmate(int color) {
        return isInCheck(color) && !hasLegalMoves(color);
    }

    private boolean isInCheck(int color) {
        // Find the king of the given color
        Pieces king = null;
        for (Pieces p : pieces) {
            if (p instanceof King && p.color == color) {
                king = p;
                break;
            }
        }

        if (king == null) {
            return false;
        }

        // Check if any opponent piece can capture the king
        for (Pieces p : pieces) {
            if (p.color != color) {
                if (p.canMove(king.col, king.row)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasLegalMoves(int color) {
        for (Pieces p : pieces) {
            if (p.color == color) {
                // Try moving to every square on the board
                for (int destCol = 0; destCol < 8; destCol++) {
                    for (int destRow = 0; destRow < 8; destRow++) {
                        copyPieces(pieces, simPieces);
                        if (p.canMove(destCol, destRow)) {
                            // Simulate the move
                            Pieces originalCollision = p.collision;
                            if (originalCollision != null) {
                                simPieces.remove(originalCollision.getIndex());
                            }
                            int originalCol = p.col;
                            int originalRow = p.row;
                            p.col = destCol;
                            p.row = destRow;

                            // Check if the move leaves the king in check
                            if (!illegalMove(p)) {
                                // Restore original position
                                p.col = originalCol;
                                p.row = originalRow;
                                p.collision = originalCollision;
                                return true; // Found a legal move
                            }

                            // Restore original position
                            p.col = originalCol;
                            p.row = originalRow;
                            p.collision = originalCollision;
                        }
                    }
                }
            }
        }
        return false; // No legal moves found
    }

    private boolean illegalMove(Pieces piece){
        // Find the king of the same color as the moved piece
        Pieces king = null;
        for (Pieces p : simPieces) {
            if (p instanceof King && p.color == piece.color) {
                king = p;
                break;
            }
        }

        if (king == null) {
            return false;
        }

        // Check if any opponent piece can capture the king
        for (Pieces p : simPieces) {
            if (p.color != piece.color) {
                if (p.canMove(king.col, king.row)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canPromote(){
        if (selectedPiece == null || !(selectedPiece instanceof Pawn)){
            return false;
        }
        if (selectedPiece.color == WHITE && selectedPiece.row == 0 || selectedPiece.color == BLACK && selectedPiece.row == 7){
            promotionPieces.clear();
            promotionPieces.add(new Rook(currentTurn, 9, 2));
            promotionPieces.add(new Knight(currentTurn, 9, 3));
            promotionPieces.add(new Bishop(currentTurn, 9, 4));
            promotionPieces.add(new Queen(currentTurn, 9, 5));
            return true;
        }
        return false;
    }

    public void promoting(){
        
        if (mouse.pressed) {
            int mouseCol = mouse.x / Board.tileSize;
            int mouseRow = mouse.y / Board.tileSize;
            int color;

            for (Pieces p : promotionPieces) {
                if (p.col == mouseCol && p.row == mouseRow) {

                    switch (p.type) {
                        case "Rook":
                            simPieces.add(new Rook(selectedPiece.color, selectedPiece.col, selectedPiece.row)); break;
                        case "Knight":
                            simPieces.add(new Knight(selectedPiece.color, selectedPiece.col, selectedPiece.row));   break;
                        case "Bishop":
                            simPieces.add(new Bishop(selectedPiece.color, selectedPiece.col, selectedPiece.row));   break;
                        case "Queen":
                            simPieces.add(new Queen(selectedPiece.color, selectedPiece.col, selectedPiece.row));    break;
                        default:
                            break;
                    }
                    simPieces.remove(selectedPiece.getIndex());
                    copyPieces(simPieces, pieces);
                    // Clear promotion state
                    promotion = false;
                    promotionPieces.clear();
                    selectedPiece = null;
                    
                    break;
                }
            }
        }
    }

    /**
     * Draw the objects to the screen
     * @param g Graphics object
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        board.draw(g2);

        for (Pieces p: pieces){
            p.draw(g2);
        }

        if (selectedPiece != null) {
            // Draw transparent ghost at original position
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2.drawImage(selectedPiece.image,
                selectedPiece.preCol * Board.tileSize,
                selectedPiece.preRow * Board.tileSize,
                Board.tileSize, Board.tileSize, null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            if (canMove){
                if (illegalMove(selectedPiece) == false) {
                    // Highlight valid square in white
                    g2.setColor(Color.WHITE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(selectedPiece.col * Board.tileSize, selectedPiece.row * Board.tileSize, Board.tileSize, Board.tileSize);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                } else {
                    // Highlight invalid square in red
                    g2.setColor(Color.RED);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(selectedPiece.col * Board.tileSize, selectedPiece.row * Board.tileSize, Board.tileSize, Board.tileSize);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            }

            selectedPiece.draw(g2);
        }

        // Draw turn indicator
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Monospaced", Font.BOLD, 40));
        g2.setColor(Color.WHITE);

        if (promotion){
            g2.drawString("Promote to:", 880, 150);
            for (Pieces p : promotionPieces){
                g2.drawImage(p.image, p.getX(p.col), p.getY(p.row), Board.tileSize, Board.tileSize, null);
            }
        } else {
            if (currentTurn == WHITE) {
                g2.drawString("White Move", 880, 550);
            } else {
                g2.drawString("Black Move", 880, 250);
            }
        }

        if (gameOver) {
            g2.setFont(new Font("Monospaced", Font.BOLD, 80));
            g2.setColor(Color.GREEN);
            String winner = (currentTurn == WHITE) ? "Black" : "White";
            g2.drawString(winner + " wins!", 150, 400);
        }

        g2.dispose();
    }
}