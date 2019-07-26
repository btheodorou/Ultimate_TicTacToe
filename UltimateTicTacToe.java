/*
* Brandon Theodorou
* Coding Project
* Ultimate Tic-Tac-Toe game with GUI, Single Player, and Multiplayer
*/

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;
import javax.swing.JOptionPane;

public class UltimateTicTacToe {
    
    TicTacToe[][] board;
    boolean turn;
    private boolean won;
    private boolean tie;
    private TicTacToeAgent ai;
    int[] nextBoard;
    String player1;
    String player2;

    public static void playGame() {
        UltimateTicTacToe game = new UltimateTicTacToe();
        game.startTurn();
    }

    public UltimateTicTacToe() {
        board = new TicTacToe[3][3];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                board[i][j] = new TicTacToe();
            }
        }
        won = false;
        nextBoard = new int[] {-1, -1};

        String[] options = new String[] {"Multiplayer", "Single Player"};
        int mode = JOptionPane.showOptionDialog(null, "Which type of Ultimate Tic-Tac-Toe game do you want to play?", "", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (mode == 0) {
            ai = null;
            turn = true;

            player1 = JOptionPane.showInputDialog("Which player wants to go first?");
            player2 = JOptionPane.showInputDialog("Which player wants to go second?");
        }
        else {
            player1 = JOptionPane.showInputDialog("What is your name?");
            player2 = "";
            
            options = new String[] {"Difficult", "Intermediate", "Easy"};
            int difficulty = JOptionPane.showOptionDialog(null, "What difficulty do you want to play on?", "", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            ai = new TicTacToeAgent(2 - difficulty);
            
            options = new String[] {"Second", "First"};
            int firstTurn = JOptionPane.showOptionDialog(null, "Do you want to go first or second?", "", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (firstTurn == 1)
                    turn = true;
                else
                    turn = false;
        }
    }

    private void startTurn() {
        // Check if the game is over    
        if (won) {
            turn = !turn;
            if (ai != null && !turn)
                new UltimateTicTacToeBoard(this, 2, "That's too bad, you lost. Maybe next time.");
            else
                new UltimateTicTacToeBoard(this, 2, "Congrats " + playerName() + "! You Won!");
            return;
        }

        if (tie) {
            new UltimateTicTacToeBoard(this, 2, "Yuck, a tie. Better than losing I guess.");
            return;
        }

        // If it's the computer's turn, let it play
        if (!turn && ai != null) {
            // if it's single player and the computer's turn, let it move
            int[] move = ai.getAction(new GameState(board, turn, nextBoard));
            board[move[0]][move[1]].playTurnAI(new int[] { move[2], move[3] });
            nextBoard = new int[] { move[2], move[3] };
            checkDone();
            startTurn();
            return;
        }
        // otherwise proceed to making a move
        // if the board is won or it's the first turn of the game, let the player pick which board to play before making a move on that board
        if (nextBoard[0] == -1 || board[nextBoard[0]][nextBoard[1]].getDone())
            new UltimateTicTacToeBoard(this, 0, playerName() + ": Pick which board to play on.");
        else
            // if the board is determined, make a move on that board
            new UltimateTicTacToeBoard(this, 1, playerName() + ": Make a move on the " + nextBoard() + " board.");
    }

    private void makeMove(int[] choice) {
        // get the board from the square clicked
        choice[0] /= 3;
        choice[1] /= 3;

        // if the board chosen was already done, pick again
        if (board[choice[0]][choice[1]].getDone()) {
            new UltimateTicTacToeBoard(this, 0, playerName() + ": That board is already done, pick another one.");
            return;
        }
        
        nextBoard = choice;

        // play turn on that board
        new UltimateTicTacToeBoard(this, 1, playerName() + ": Make a move on the " + nextBoard() + " board.");
    }

    private void playTurn(int[] choice) {
        // if the move was on the wrong board, pick again
        if ((nextBoard[0] != (choice[0] / 3)) || (nextBoard[1] != (choice[1] / 3))) {
            new UltimateTicTacToeBoard(this, 1, playerName() + ": That was the wrong board, pick again on the " + nextBoard() + " board.");
            return;
        }

        // otherwise pass than choice into the specific board and make the move there
        board[nextBoard[0]][nextBoard[1]].playTurn(choice);
    }

    void checkDone() {
        // check if the game is won
        won = checkWin();

        // if it's not won, check if it's a tie
        if (!won)
            tie = checkTie();

        // switch whose turn it is
        turn = !turn;
    }

    String playerName() {
        if (turn)
            return player1;
           
        return player2;
    }

    String nextBoard() {
        if (Arrays.equals(nextBoard, new int[] {0, 0}))
            return "top left";
        if (Arrays.equals(nextBoard, new int[] {0, 1}))
            return "top center";
        if (Arrays.equals(nextBoard, new int[] {0, 2}))
            return "top right";
        if (Arrays.equals(nextBoard, new int[] {1, 0}))
            return "left center";
        if (Arrays.equals(nextBoard, new int[] {1, 1}))
            return "center";
        if (Arrays.equals(nextBoard, new int[] {1, 2}))
            return "right center";
        if (Arrays.equals(nextBoard, new int[] {2, 0}))
            return "bottom left";
        if (Arrays.equals(nextBoard, new int[] {2, 1}))
            return "bottom center";
        if (Arrays.equals(nextBoard, new int[] {2, 2}))
            return "bottom right";
        return "";
    }

    void playAgain() {
        String[] options = new String[] {"No", "Yes"};
        if (JOptionPane.showOptionDialog(null, "Do you want to play again?", "", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]) == 1)
            playGame();
    }

    private boolean checkWin() {
        boolean threeInARow = ((board[0][0].getWinner() != 0 && board[0][0].getWinner() == board[0][1].getWinner() && board[0][0].getWinner() == board[0][2].getWinner()) ||
                              (board[1][0].getWinner() != 0 && board[1][0].getWinner() == board[1][1].getWinner() && board[1][0].getWinner() == board[1][2].getWinner()) ||
                              (board[2][0].getWinner() != 0 && board[2][0].getWinner() == board[2][1].getWinner() && board[2][0].getWinner() == board[2][2].getWinner()));
        boolean threeInAColumn = ((board[0][0].getWinner() != 0 && board[0][0].getWinner() == board[1][0].getWinner() && board[0][0].getWinner() == board[2][0].getWinner()) ||
                              (board[0][1].getWinner() != 0 && board[0][1].getWinner() == board[1][1].getWinner() && board[0][1].getWinner() == board[2][1].getWinner()) ||
                              (board[0][2].getWinner() != 0 && board[0][2].getWinner() == board[1][2].getWinner() && board[0][2].getWinner() == board[2][2].getWinner()));
        boolean threeInADiagonal = ((board[1][1].getWinner() != 0) && ((board[0][0].getWinner() == board[1][1].getWinner() && board[0][0].getWinner() == board[2][2].getWinner()) ||
                                (board[0][2].getWinner() == board[1][1].getWinner() && board[0][2].getWinner() == board[2][0].getWinner())));

        return threeInARow || threeInAColumn || threeInADiagonal;
    }

    private boolean checkTie() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if(board[i][j].getDone() == false)
                    return false;
            }
        } 
        return true;
    }

    public boolean getWon() {
        return won;
    }

    public boolean getTurn() {
        return turn;
    }

    // A single, regular Tic-Tac-Toe board
    class TicTacToe {

        private int[][] board;
        private int winner;
        private boolean done;

        public TicTacToe() {
            board = new int[3][3];
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    board[i][j] = 0;
                }
            }
            winner = 0;
            done = false;
        } 
        
        public void playTurn(int[] choice) {
            // Update the small board
            makeMove(UltimateTicTacToe.this.turn, choice);

            // Check if the small board is finished
            if (checkWin()) {
                done = true;
                if (turn)
                    winner = 1;
                else
                    winner = -1;
            }
            else if (checkTie()) {
                done = true;
            }

            // Check if the overall game is over
            UltimateTicTacToe.this.checkDone();
            UltimateTicTacToe.this.startTurn();
        }

        private void makeMove(boolean turn, int[] choice) {
            while (board[choice[0] % 3][choice[1] % 3] != 0) {
                new UltimateTicTacToeBoard(UltimateTicTacToe.this, 1, UltimateTicTacToe.this.playerName() + "That space was already filled, pick another one on the " + UltimateTicTacToe.this.nextBoard() + " board.");
                return;
            }

            if (turn)
                board[choice[0] % 3][choice[1] % 3] = 1;
            else
                board[choice[0] % 3][choice[1] % 3] = -1;
            
            UltimateTicTacToe.this.nextBoard = new int[] { (choice[0] % 3), (choice[1] % 3) };
        }

        public void playTurnAI(int[] coordinates) {
            board[coordinates[0]][coordinates[1]] = -1;
            
            if (checkWin()) {
                done = true;
                winner = -1;
            }
            else if (checkTie()) {
                done = true;
            }
        }

        private boolean checkTie() {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    if(board[i][j] == 0)
                        return false;
                }
            } 
            return true;
        }

        private boolean checkWin() {
            boolean threeInARow = ((board[0][0] != 0 && board[0][0] == board[0][1] && board[0][0] == board[0][2]) ||
                                  (board[1][0] != 0 && board[1][0] == board[1][1] && board[1][0] == board[1][2]) ||
                                  (board[2][0] != 0 && board[2][0] == board[2][1] && board[2][0] == board[2][2]));
            boolean threeInAColumn = ((board[0][0] != 0 && board[0][0] == board[1][0] && board[0][0] == board[2][0]) ||
                                  (board[0][1] != 0 && board[0][1] == board[1][1] && board[0][1] == board[2][1]) ||
                                  (board[0][2] != 0 && board[0][2] == board[1][2] && board[0][2] == board[2][2]));
            boolean threeInADiagonal = ((board[1][1] != 0) && ((board[0][0] == board[1][1] && board[0][0] == board[2][2]) ||
                                       (board[0][2] == board[1][1] && board[0][2] == board[2][0])));

            return threeInARow || threeInAColumn || threeInADiagonal;
        }

        public int[][] getBoard() {
            return board;
        }

        public int getWinner() {
            return winner;
        }

        public boolean getDone() {
            return done;
        }

        void setBoard(int[][] b) {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    board[i][j] = b[i][j];
                }
            }  
        }

        void setWinner(int w) {
            winner = w;
        }

        void setDone(boolean d) {
            done = d;
        }
    }

    class GameState {
        TicTacToe[][] board;
        boolean turn;
        int[] nextBoard;

        public GameState(TicTacToe[][] b, boolean t, int[] n) {
            board = b;
            turn = t;
            nextBoard = n;
        }

        public ArrayList<int[]> getMoves() {
            ArrayList<int[]> moves = new ArrayList<int[]>();
            
            // if the computer can pick from any little board, cycle through all of them
            if (nextBoard[0] == -1 || board[nextBoard[0]][nextBoard[1]].getDone()) {
                for (int i = 0; i < 3; ++i) {
                    for (int j = 0; j < 3; ++j) {
                        if (!board[i][j].getDone()) {
                            int[][] smallBoard = board[i][j].getBoard();
                            for (int k = 0; k < 3; ++k) {
                                for (int l = 0; l < 3; ++l) {
                                    if (smallBoard[j][k] == 0)
                                        moves.add(new int[] { i, j, k, l });
                                }
                            }
                        }
                    }
                }   
                return moves;                    
            }
            
            // otherwise just do the board they have to play on
            int[][] smallBoard = board[nextBoard[0]][nextBoard[1]].getBoard();
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    if (smallBoard[i][j] == 0)
                        moves.add(new int[] { nextBoard[0], nextBoard[1], i, j });
                }
            }
            return moves;
        }

        public GameState generateSuccessor(int[] action, boolean t) {
            // copy all of the old small board data
            TicTacToe[][] newBoard = new TicTacToe[3][3];
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    newBoard[i][j] = new TicTacToe();
                    newBoard[i][j].setWinner(board[i][j].getWinner());
                    newBoard[i][j].setDone(board[i][j].getDone());
                    newBoard[i][j].setBoard(board[i][j].getBoard());
                }
            }  
            
            // update it with the action
            if (t)
                (newBoard[action[0]][action[1]].getBoard())[action[2]][action[3]] = 1;
            else
                (newBoard[action[0]][action[1]].getBoard())[action[2]][action[3]] = -1;

            if (newBoard[action[0]][action[1]].checkWin()) {
                newBoard[action[0]][action[1]].setDone(true);
                if (t)
                    newBoard[action[0]][action[1]].setWinner(1);
                else
                    newBoard[action[0]][action[1]].setWinner(-1);
            }
            else if (newBoard[action[0]][action[1]].checkTie()) {
                newBoard[action[0]][action[1]].setDone(true);
            }

            return new GameState(newBoard, !t, new int[] { action[2], action[3] });
        }

        public boolean isDone() {
            return isWin() || isLoss() || isTie();
        }

        public boolean isWin() {
            boolean threeInARow = ((board[0][0].getWinner() == -1 && board[0][0].getWinner() == board[0][1].getWinner() && board[0][0].getWinner() == board[0][2].getWinner()) ||
                                  (board[1][0].getWinner() == -1 && board[1][0].getWinner() == board[1][1].getWinner() && board[1][0].getWinner() == board[1][2].getWinner()) ||
                                  (board[2][0].getWinner() == -1 && board[2][0].getWinner() == board[2][1].getWinner() && board[2][0].getWinner() == board[2][2].getWinner()));
            boolean threeInAColumn = ((board[0][0].getWinner() == -1 && board[0][0].getWinner() == board[1][0].getWinner() && board[0][0].getWinner() == board[2][0].getWinner()) ||
                                     (board[0][1].getWinner() == -1 && board[0][1].getWinner() == board[1][1].getWinner() && board[0][1].getWinner() == board[2][1].getWinner()) ||
                                     (board[0][2].getWinner() == -1 && board[0][2].getWinner() == board[1][2].getWinner() && board[0][2].getWinner() == board[2][2].getWinner()));
            boolean threeInADiagonal = ((board[1][1].getWinner() == -1) && ((board[0][0].getWinner() == board[1][1].getWinner() && board[0][0].getWinner() == board[2][2].getWinner()) ||
                                       (board[0][2].getWinner() == board[1][1].getWinner() && board[0][2].getWinner() == board[2][0].getWinner())));

            return threeInARow || threeInAColumn || threeInADiagonal;
        }

        public boolean isLoss() {
            boolean threeInARow = ((board[0][0].getWinner() == 1 && board[0][0].getWinner() == board[0][1].getWinner() && board[0][0].getWinner() == board[0][2].getWinner()) ||
                                  (board[1][0].getWinner() == 1 && board[1][0].getWinner() == board[1][1].getWinner() && board[1][0].getWinner() == board[1][2].getWinner()) ||
                                  (board[2][0].getWinner() == 1 && board[2][0].getWinner() == board[2][1].getWinner() && board[2][0].getWinner() == board[2][2].getWinner()));
            boolean threeInAColumn = ((board[0][0].getWinner() == 1 && board[0][0].getWinner() == board[1][0].getWinner() && board[0][0].getWinner() == board[2][0].getWinner()) ||
                                     (board[0][1].getWinner() == 1 && board[0][1].getWinner() == board[1][1].getWinner() && board[0][1].getWinner() == board[2][1].getWinner()) ||
                                     (board[0][2].getWinner() == 1 && board[0][2].getWinner() == board[1][2].getWinner() && board[0][2].getWinner() == board[2][2].getWinner()));
            boolean threeInADiagonal = ((board[1][1].getWinner() == 1) && ((board[0][0].getWinner() == board[1][1].getWinner() && board[0][0].getWinner() == board[2][2].getWinner()) ||
                                       (board[0][2].getWinner() == board[1][1].getWinner() && board[0][2].getWinner() == board[2][0].getWinner())));

            return threeInARow || threeInAColumn || threeInADiagonal;
        }

        public boolean isTie() {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    if(board[i][j].getDone() == false)
                        return false;
                }
            } 
            return true;
        }

        public int bigTwoInARow(int value) {
            int count = 0;

            // checking the rows
            if (board[0][0].getWinner() == value && board[0][1].getWinner() == value && !board[0][2].getDone())
                ++count;
            else if (board[0][0].getWinner() == value && board[0][2].getWinner() == value && !board[0][1].getDone())
                ++count;
            else if (board[0][1].getWinner() == value && board[0][2].getWinner() == value && !board[0][0].getDone())
                ++count;

            if (board[1][0].getWinner() == value && board[1][1].getWinner() == value && !board[1][2].getDone())
                ++count;
            else if (board[1][0].getWinner() == value && board[1][2].getWinner() == value && !board[1][1].getDone())
                ++count;
            else if (board[1][1].getWinner() == value && board[1][2].getWinner() == value && !board[1][0].getDone())
                ++count;

            if (board[2][0].getWinner() == value && board[2][1].getWinner() == value && !board[2][2].getDone())
                ++count;
            else if (board[2][0].getWinner() == value && board[2][2].getWinner() == value && !board[2][1].getDone())
                ++count;
            else if (board[2][1].getWinner() == value && board[2][2].getWinner() == value && !board[2][0].getDone())
                ++count;

            // checking the columns
            if (board[0][0].getWinner() == value && board[1][0].getWinner() == value && !board[2][0].getDone())
                ++count;
            else if (board[0][0].getWinner() == value && board[2][0].getWinner() == value && !board[1][0].getDone())
                ++count;
            else if (board[1][0].getWinner() == value && board[2][0].getWinner() == value && !board[0][0].getDone())
                ++count;

            if (board[0][1].getWinner() == value && board[1][1].getWinner() == value && !board[2][1].getDone())
                ++count;
            else if (board[0][1].getWinner() == value && board[2][1].getWinner() == value && !board[1][1].getDone())
                ++count;
            else if (board[1][1].getWinner() == value && board[2][1].getWinner() == value && !board[0][1].getDone())
                ++count;

            if (board[0][2].getWinner() == value && board[1][2].getWinner() == value && !board[2][2].getDone())
                ++count;
            else if (board[0][2].getWinner() == value && board[2][2].getWinner() == value && !board[1][2].getDone())
                ++count;
            else if (board[1][2].getWinner() == value && board[2][2].getWinner() == value && !board[0][2].getDone())
                ++count;

            // checking the diagonals
            if (board[0][0].getWinner() == value && board[1][1].getWinner() == value && !board[2][2].getDone())
                ++count;
            else if (board[0][0].getWinner() == value && board[2][2].getWinner() == value && !board[1][1].getDone())
                ++count;
            else if (board[1][1].getWinner() == value && board[2][2].getWinner() == value && !board[0][0].getDone())
                ++count;

            if (board[2][0].getWinner() == value && board[1][1].getWinner() == value && !board[0][2].getDone())
                ++count;
            else if (board[2][0].getWinner() == value && board[0][2].getWinner() == value && !board[1][1].getDone())
                ++count;
            else if (board[1][1].getWinner() == value && board[0][2].getWinner() == value && !board[2][0].getDone())
                ++count;

            return count;
        }

        public int boardsWon(int value) {
            int count = 0;

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    if (board[i][j].getWinner() == value)
                        ++count;
                }
            }
            return count;
        }

        public int smallTwoInARow(int value) {
            int count = 0;

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    // getting the board
                    int[][] smallBoard = board[i][j].getBoard();

                    // checking the rows
                    if (smallBoard[0][0] == value && smallBoard[0][1] == value && smallBoard[0][2] == 0)
                        ++count;
                    else if (smallBoard[0][0] == value && smallBoard[0][2] == value && smallBoard[0][1] == 0)
                        ++count;
                    else if (smallBoard[0][1] == value && smallBoard[0][2] == value && smallBoard[0][0] == 0)
                        ++count;

                    if (smallBoard[1][0] == value && smallBoard[1][1] == value && smallBoard[1][2] == 0)
                        ++count;
                    else if (smallBoard[1][0] == value && smallBoard[1][2] == value && smallBoard[1][1] == 0)
                        ++count;
                    else if (smallBoard[1][1] == value && smallBoard[1][2] == value && smallBoard[1][0] == 0)
                        ++count;

                    if (smallBoard[2][0] == value && smallBoard[2][1] == value && smallBoard[2][2] == 0)
                        ++count;
                    else if (smallBoard[2][0] == value && smallBoard[2][2] == value && smallBoard[2][1] == 0)
                        ++count;
                    else if (smallBoard[2][1] == value && smallBoard[2][2] == value && smallBoard[2][0] == 0)
                        ++count;

                    // checking the columns
                    if (smallBoard[0][0] == value && smallBoard[1][0] == value && smallBoard[2][0] == 0)
                        ++count;
                    else if (smallBoard[0][0] == value && smallBoard[2][0] == value && smallBoard[1][0] == 0)
                        ++count;
                    else if (smallBoard[1][0] == value && smallBoard[2][0] == value && smallBoard[0][0] == 0)
                        ++count;

                    if (smallBoard[0][1] == value && smallBoard[1][1] == value && smallBoard[2][1] == 0)
                        ++count;
                    else if (smallBoard[0][1] == value && smallBoard[2][1] == value && smallBoard[1][1] == 0)
                        ++count;
                    else if (smallBoard[1][1] == value && smallBoard[2][1] == value && smallBoard[0][1] == 0)
                        ++count;

                    if (smallBoard[0][2] == value && smallBoard[1][2] == value && smallBoard[2][2] == 0)
                        ++count;
                    else if (smallBoard[0][2] == value && smallBoard[2][2] == value && smallBoard[1][2] == 0)
                        ++count;
                    else if (smallBoard[1][2] == value && smallBoard[2][2] == value && smallBoard[0][2] == 0)
                        ++count;

                    // checking the diagonals
                    if (smallBoard[0][0] == value && smallBoard[1][1] == value && smallBoard[2][2] == 0)
                        ++count;
                    else if (smallBoard[0][0] == value && smallBoard[2][2] == value && smallBoard[1][1] == 0)
                        ++count;
                    else if (smallBoard[1][1] == value && smallBoard[2][2] == value && smallBoard[0][0] == 0)
                        ++count;

                    if (smallBoard[2][0] == value && smallBoard[1][1] == value && smallBoard[0][2] == 0)
                        ++count;
                    else if (smallBoard[2][0] == value && smallBoard[0][2] == value && smallBoard[1][1] == 0)
                        ++count;
                    else if (smallBoard[1][1] == value && smallBoard[0][2] == value && smallBoard[2][0] == 0)
                        ++count;
                }
            }

            return count;
        }
    }

    class TicTacToeAgent {
        private int depth;

        public TicTacToeAgent(int difficulty) {
            if (difficulty == 0)
                depth = 1;
            else if (difficulty == 1)
                depth = 2;
            else   
                depth = 3;
        }
 
        public int[] getAction(GameState state) {
            
            ArrayList<int[]> options = state.getMoves(); // get all possible legal actions

            // loop through all of the options and determine the best
            int maxScore = Integer.MIN_VALUE;
            int[] maxMove = null;
            for (int[] o : options) {
                int score = alphaBetaFunction(state.generateSuccessor(o, false), true, 1, maxScore, Integer.MAX_VALUE);
                if (score > maxScore) {
                    maxScore = score;
                    maxMove = o;
                }
            }

            // return the optimal move
            return maxMove;
        }

        // the minimax algorithm with alpha beta pruning
        private int alphaBetaFunction(GameState state, boolean turn, int aiTurns, int alpha, int beta) {
            if (state.isDone())
                return evaluationFunction(state);

            if (turn) 
                return minValue(state, aiTurns, alpha, beta);

            if (aiTurns == depth)
                return evaluationFunction(state);
            
            return maxValue(state, aiTurns, alpha, beta);
        }

        private int minValue(GameState state, int aiTurns, int alpha, int beta) {
            int value = Integer.MAX_VALUE;
            for (int[] o : state.getMoves()) {
                value = Math.min(value, alphaBetaFunction(state.generateSuccessor(o, true), false, aiTurns, alpha, beta));
                if (value < alpha)
                    return value;
                beta = Math.min(beta, value);
            }
            return value;
        }

        private int maxValue(GameState state, int aiTurns, int alpha, int beta) {
            int value = Integer.MIN_VALUE;
            for (int[] o : state.getMoves()) {
                value = Math.max(value, alphaBetaFunction(state.generateSuccessor(o, false), true, aiTurns + 1, alpha, beta));
                if (value > beta)
                    return value;
                alpha = Math.max(alpha, value);
            }
            return value;
        }

        private int evaluationFunction(GameState state) {
            if (state.isWin())
                return Integer.MAX_VALUE;

            if (state.isLoss())
                return Integer.MIN_VALUE;

            if (state.isTie())
                return 0;

            int compBigTwoInARow = state.bigTwoInARow(-1);
            int userBigTwoInARow = state.bigTwoInARow(1);
            int compBoardsWon = state.boardsWon(-1);
            int userBoardsWon = state.boardsWon(1);
            int compSmallTwoInARow = state.smallTwoInARow(-1);
            int userSmallTwoInARow = state.smallTwoInARow(1);

            return (150 * (compBigTwoInARow - userBigTwoInARow)) + (100 * (compBoardsWon - userBoardsWon)) + (25 * (compSmallTwoInARow - userSmallTwoInARow));
        }
    }

    // the GUI that allows the players to choose which board/space to play on
    class UltimateTicTacToeBoard extends JFrame {

        private JPanel gui;
        private JButton[][] ticTacToeBoxes;
        private JPanel ticTacToeBoard;
        private JLabel label;
        int[] toReturn;
        int returnMethod;

        UltimateTicTacToeBoard(UltimateTicTacToe game, int marker, String message) {
            super("B-Shizzle's Ultimate Tic-Tac-Toe™");
            gui = new JPanel(new BorderLayout(1, 1));
            ticTacToeBoxes = new JButton[9][9];
            label = new JLabel(message);
            returnMethod = marker;
            initializeGui(game.board);
            super.add(getGui());
            super.pack();
            super.setLocationRelativeTo(null);
            super.setMinimumSize(super.getSize());
            super.setVisible(true);
            super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            WindowListener exitReturn = new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent exit) {
                    switch(UltimateTicTacToeBoard.this.returnMethod) {
                        case 0:
                            game.makeMove(toReturn);
                            break;
                        case 1:
                            game.playTurn(toReturn);
                            break;
                        case 2:
                            game.playAgain();
                    }
                }
            };
            super.addWindowListener(exitReturn);
        }

        private final void initializeGui(TicTacToe[][] board) {
            // set up the main GUI
            gui.setBorder(new EmptyBorder(3, 3, 3, 3));
            JToolBar tools = new JToolBar();
            tools.setFloatable(false);
            gui.add(tools, BorderLayout.PAGE_START);
            tools.add(label);
            gui.add(new JLabel(""), BorderLayout.LINE_START);
            ticTacToeBoard = new JPanel(new GridLayout(0, 9));
            gui.add(ticTacToeBoard);

            // create the Tic-Tac-Toe squares
            Insets buttonMargin = new Insets(0,0,0,0);
            Icon x = new ImageIcon(getClass().getResource("x.png"));
            Icon o = new ImageIcon(getClass().getResource("o.png"));
            ClickHandler handler;
            TicTacToeSquare b;
            for (int i = 0; i < ticTacToeBoxes.length; ++i) {
                for (int j = 0; j < ticTacToeBoxes[i].length; ++j) {
                    handler = new ClickHandler(i, j);
                    if ((board[i/3][j/3].getBoard())[i%3][j%3] == 1)
                        b = new TicTacToeSquare(x, i, j);
                    else if ((board[i/3][j/3].getBoard())[i%3][j%3] == -1)
                        b = new TicTacToeSquare(o, i, j);
                    else
                        b = new TicTacToeSquare(i, j);
                    b.setPreferredSize(new Dimension(60,60));
                    b.setMargin(buttonMargin);
                    b.addActionListener(handler);
                    int top = 1;
                    int left = 1;
                    int right = 1;
                    int bottom = 1;
                    if (j % 3 == 2)
                        right = 3;
                    if (j % 3 == 0)
                        left = 3;
                    if (i % 3 == 2)
                        bottom = 3;
                    if (i % 3 == 0)
                        top = 3;
                    b.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
                    ticTacToeBoxes[i][j] = b;
                }
            }

            //fill the board
            for (int i = 0; i < ticTacToeBoxes.length; ++i) {
                for (int j = 0; j < ticTacToeBoxes[i].length; ++j) {
                    ticTacToeBoard.add(ticTacToeBoxes[i][j]);
                }
            }
        }

        private final JComponent getTicTacToeBoard() {
            return ticTacToeBoard;
        }

        private final JComponent getGui() {
            return gui;
        }

        private class ClickHandler implements ActionListener {
            private int[] location;

            public ClickHandler(int i, int j) {
                location = new int[] {i, j};
            }

            public void actionPerformed(ActionEvent event) {
                UltimateTicTacToeBoard.this.toReturn = location;
                UltimateTicTacToeBoard.this.dispatchEvent(new WindowEvent(UltimateTicTacToeBoard.this, WindowEvent.WINDOW_CLOSING));
            }
        }

        private class TicTacToeSquare extends JButton {
            private int[] location;

            public TicTacToeSquare(int i, int j) {
                super();
                location = new int[] {i, j};
            }

            public TicTacToeSquare(Icon background, int i, int j) {
                super(background);
                location = new int[] {i, j};
            }
        }
    }

    public static void main(String[] args) {
        playGame();
    }
}
