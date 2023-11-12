import java.util.Random;
import java.util.Scanner;
public class Minefield {
    /**
    Global Section
    */
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_GREY_BG = "\u001b[0m";

    private int rows;
    private int columns;
    private int flags;
    private Cell[][] field;
    private boolean[][] flag_field;
    private int[] first_mine;
    /**
     * Constructor
     * @param rows       Number of rows.
     * @param columns    Number of columns.
     * @param flags      Number of flags, should be equal to mines
     */
    public Minefield(int rows, int columns, int flags) {
        this.rows = rows;
        this.columns = columns;
        this.flags = flags;
        this.field = new Cell[this.rows][this.columns];
        for (int y = 0; y < this.rows; y++){
            for (int x = 0; x < this.columns; x++){
                this.field[y][x] = new Cell(false, "-");
            }
        }
        this.flag_field = new boolean[this.rows][this.columns];
        this.first_mine = new int[] {-1, -1};
    }

    public int getRows(){ return this.rows;}

    public int getColumns(){return this.columns;}

    public Cell[][] getField() {return this.field;}
    public int get_flag(){ return this.flags; }
/**
     * evaluateField
     *
     * @function When a mine is found in the field, calculate the surrounding 9x9 tiles values. If a mine is found, increase the count for the square.
     */
    public void evaluateField() {
        for (int row = 0; row < this.rows; row++){
            for (int col = 0; col < this.columns; col++){
                if (!this.field[row][col].getStatus().equals("M")){
//                    check all the possible neighboring cells and count how many "M" are there

                    int num_mines = 0;
                    for (int y = row-1; y <= row+1; y++) {
                        for (int x = col-1; x <= col+1; x++) {
                            if (y >= 0 && x >= 0 && y < this.rows && x < this.columns && this.field[y][x].getStatus().equals("M")) {
                                num_mines++;
                            }
                        }
                    }
                    this.field[row][col].setStatus(Integer.toString(num_mines));
                }
            }
        }
    }
    /**
     * createMines
     *
     * @param x       Start x, avoid placing on this square.
     * @param y        Start y, avoid placing on this square.
     * @param mines      Number of mines to place.
     */
    public void createMines(int x, int y, int mines) {
        Random ran = new Random();
        for (int i = 0; i < mines; i++){
            int xx = ran.nextInt(this.columns);
            int yy = ran.nextInt(this.rows);
            while (this.field[y][x].equals("M") || (xx == x && yy == y) || this.field[y][x].getRevealed() == true){
                xx = ran.nextInt(this.columns);
                yy = ran.nextInt(this.rows);
//                System.out.print(xx + " ");
//                System.out.println(yy);
            }
            this.field[yy][xx].setStatus("M");
        }
    }

    /**
     * guess
     *
     * @param x       The x value the user entered.
     * @param y       The y value the user entered.
     * @param flag    A boolean value that allows the user to place a flag on the corresponding square.
     * @return boolean Return false if guess did not hit mine or if flag was placed, true if mine found.
     */
    public boolean guess(int x, int y, boolean flag) {
//        checking out of bounds
        if (x < 0 || x >= this.rows || y < 0 || y >= this.columns || (flag == true && this.flags < 1)){
            return false;
        }
//        checking if it is ok to put flags
        if (flag == true && this.flags >= 1 && this.flag_field[y][x] == false && this.field[y][x].getRevealed() == false){
            this.flags--;
            this.flag_field[y][x] = true;
//            System.out.println("Flags remaining: " + this.flags);
            return true;
        }
//        if user wants to dig
        if (flag == false){
            revealZeroes(x, y);
            return true;
        }
        return false;
    }
    /**
     * gameOver
     *
     * @return boolean Return false if game is not over and squares have yet to be revealed, otheriwse return true.
     */
    public boolean gameOver() {
//        return true if digged a mine
        for (int y = 0; y < this.rows; y++){
            for (int x = 0; x < this.columns; x++){
                if (this.field[y][x].getRevealed() == true && this.field[y][x].getStatus().equals("M")){
                    System.out.println(ANSI_RED + "you have digged a mine. Game over" + ANSI_GREY_BG);
                    return true;
                }
            }
        }
//        return true if digged the whole board but didn't dig the mine
        boolean done = true;
        for (int y = 0; y < this.rows; y++){
            for (int x = 0; x < this.columns; x++){
                if (this.field[y][x].getRevealed() == false && !this.field[y][x].getStatus().equals("M")){
                    done = false;
                }
            }
        }
        if (done == true){
            System.out.println(ANSI_GREEN + "you have completed minesweeper. You have won!" + ANSI_GREY_BG);
            printMinefield();
            return true;
        }
        return false;
    }

    public void revealZeroes(int x, int y) {
        Stack1Gen<int[]> s = new Stack1Gen<>();
        int[] curr = new int[]{x, y};
        boolean[][] visited = new boolean[getRows()][getColumns()];
        s.push(curr);

        if (!this.field[curr[1]][curr[0]].getStatus().equals("0")){
            s.pop();
            visited[curr[1]][curr[0]] = true;
            this.field[curr[1]][curr[0]].setRevealed(true);
            return ;
        }
        while(!s.isEmpty()){
            curr = s.pop();
            int newX = curr[0];
            int newY = curr[1];

            System.out.print(newX + ", ");
            System.out.println(newY);

            printMinefield();
            System.out.println(toString());

            this.field[newY][newX].setRevealed(true);
            visited[newY][newX] = true;
            if(newX - 1 >= 0 && field[newY][newX - 1].getRevealed() == false && field[newY][newX - 1].getStatus().equals("0") && visited[newY][newX - 1] == false){
                int[] Coor2 = new int[] {newX - 1,newY};
                s.push(Coor2);
            }
            if(newX + 1 < field.length && field[newY][newX + 1].getRevealed() == false && field[newY][newX + 1].getStatus().equals("0") && visited[newY][newX + 1] == false){
                int[] Coor2 = new int[] {newX + 1,newY};
                s.push(Coor2);
            }
            if(newY - 1 >= 0 && field[newY - 1][newX].getRevealed() == false && field[newY - 1][newX].getStatus().equals("0") && visited[newY - 1][newX] == false){
                int[] Coor2 = new int[] {newX,newY - 1};
                s.push(Coor2);
            }
            if(newY + 1 < field.length && field[newY + 1][newX].getRevealed() == false && field[newY + 1][newX].getStatus().equals("0") && visited[newY + 1][newX] == false){
                int[] Coor2 = new int[] {newX,newY + 1};
                s.push(Coor2);
            }
        }
    }

    /**
     * revealMines
     *
     * This method should follow the psuedocode given.
     * Why might a queue be useful for this function?
     *
     * @param x     The x value the user entered.
     * @param y     The y value the user entered.
     */
    public void revealMines(int x, int y) {
        Q1Gen<int[]> queue = new Q1Gen();
        queue.add(new int[]{x, y});
        boolean[][] visited = new boolean[getRows()][getColumns()];
        while (queue.length() != 0)  {
            int[] curr = queue.remove();
            int currX = curr[0];
            int currY = curr[1];

            visited[curr[1]][curr[0]] = true;

            if(field[currY][currX].getStatus().equals("M")){
//                this.field[currY][currX].setRevealed(true);
                this.first_mine = new int[] {currX, currY};
//                System.out.println("first mine: " + currX + ", " + currY);
                break;
            }
            field[currY][currX].setRevealed(true);

            if(currX - 1 >= 0 && field[currY][currX - 1].getRevealed() == false && visited[currY][currX - 1] == false){
                int[] Coor2 = new int[] {currX - 1, currY};
                queue.add(Coor2);
            }
            if(currX + 1 < field.length && field[currY][currX + 1].getRevealed() == false && visited[currY][currX + 1] == false){
                int[] Coor2 = new int[] {currX + 1, currY};
                queue.add(Coor2);
            }
            if(currY - 1 >= 0 && field[currY - 1][currX].getRevealed() == false && visited[currY - 1][currX] == false){
                int[] Coor2 = new int[] {currX,currY - 1};
                queue.add(Coor2);
            }
            if(currY + 1 < field.length && field[currY + 1][currX].getRevealed() == false && visited[currY + 1][currX] == false){
                int[] Coor2 = new int[] {currX, currY + 1};
                queue.add(Coor2);
            }
        }
    }

    /**
     * revealStart
     *
     * @param x       The x value the user entered.
     * @param y       The y value the user entered.
     */
    public void revealStart(int x, int y) {
        revealZeroes(x, y);
        revealMines(x, y);
    }
    /**
     * printMinefield
     *
     * @fuctnion This method should print the entire minefield, regardless if the user has guessed a square.
     * *This method should print out when debug mode has been selected. 
     */
    public void printMinefield() {
        System.out.print("   ");
        for (int i = 0; i < this.columns; i++){
            if (i <= 9){
                System.out.print(i + "  ");
            }else {
                System.out.print(i + " ");
            }
        }

        System.out.println();
        for (int row = 0; row < this.rows; row++){
            if (row < 10){
                System.out.print(row + "  ");
            }else {
                System.out.print(row + " ");
            }
            for (int col = 0; col < this.columns; col++){
                if (this.field[row][col].getStatus().equals("M")){
                    System.out.print(ANSI_RED_BRIGHT + this.field[row][col].getStatus() + "  " + ANSI_GREY_BG);
                } else if (!this.field[row][col].getStatus().equals("0")){
                    System.out.print(ANSI_GREEN + this.field[row][col].getStatus() + "  " + ANSI_GREY_BG);
                } else {
                    System.out.print(this.field[row][col].getStatus() + "  ");
                }
            }
            System.out.println();
        }
    } //do the wording color

    /**
     * toString
     *
     * @return String The string that is returned only has the squares that has been revealed to the user or that the user has guessed.
     */
    public String toString() {
        String out = "   ";
        for (int i = 0; i < this.columns; i++){
            if (i <= 9){
                out += i + "  ";
            }else {
                out += i + " ";
            }
        }
        out += "\n";
        System.out.println();
        for (int row = 0; row < this.rows; row++){
            if (row < 10){
                out += row + "  ";
            }else {
                out += row + " ";
            }
            for (int col = 0; col < this.columns; col++){
                if (this.flag_field[row][col] == true){
                    out += ANSI_BLUE_BRIGHT + "F" + "  " + ANSI_GREY_BG;
                } else if (row == this.first_mine[1] && col == this.first_mine[0]){
                    out += ANSI_RED_BRIGHT + this.field[row][col].getStatus() + "  " + ANSI_GREY_BG;
                } else if (this.field[row][col].getRevealed() == true){
                    if (this.field[row][col].getStatus().equals("M")){
                        out += ANSI_RED_BRIGHT + this.field[row][col].getStatus() + "  " + ANSI_GREY_BG;
                    } else if (!this.field[row][col].getStatus().equals("0")){
                        out += ANSI_GREEN + this.field[row][col].getStatus() + "  " + ANSI_GREY_BG;
                    } else {
                        out += this.field[row][col].getStatus() + "  ";
                    }
                } else {
                    out += "-" + "  ";
                }
            }
            out += "\n";
        }
        return out;
    }
}

