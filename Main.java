import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner xxx = new Scanner(System.in);
//        verify in debug mode or not
        String debug = "dd";
        while (!debug.equals("y") && !debug.equals("n")){
            System.out.println("Do you want to play in Debug mode? [y/n]");
            debug = xxx.nextLine();
        }
        boolean debug_mode;
        if (debug.equals("y")){
            debug_mode = true;
        } else {
            debug_mode = false;
        }

        System.out.println("please choose difficulty mode: \"easy\", \"medium\", \"hard\"");
        Scanner scan = new Scanner(System.in);

        String mode = scan.nextLine();
        while (!mode.equals("easy") && !mode.equals("medium") && !mode.equals("hard")){
            System.out.println("please choose difficulty mode: \"easy\", \"medium\", \"hard\"");
            mode = scan.nextLine();
        }
        Minefield game = new Minefield(5, 5, 5);
        Random ran = new Random();
        switch(mode){
            case "easy":
                game = new Minefield(5, 5, 5);
                break;
            case "medium":
                game = new Minefield(9, 9, 12);
                break;
            case "hard":
                game = new Minefield(20, 20, 40);
                break;
        }
        if (debug_mode == true) {
            game.printMinefield();
        }
        System.out.println(game.toString());

//        getting the suitable input from user
        String input;
        String[] input2;
        int[] input3 = new int[]{0, 0};
        while (input3.length != 3 || (input3.length == 3 && (input3[2] != 1))) {
//            getting input
            System.out.println("Enter a coordinate and if you wish to place a flag (remaining: " + game.get_flag() + "): [x] [y] [f(-1, else: 1)]");
            input = scan.nextLine();
            input2 = input.split("\\s+");
            input3 = new int[input2.length];
            for (int i = 0; i < input2.length; i++) {
                input3[i] = Integer.parseInt(input2[i]);
            } // finish getting input in the form of int[]

            if (input3.length == 3 && input3[2] == -1 && game.guess(input3[0], input3[1], true)) {
                game.guess(input3[0], input3[1], true);

                if (debug_mode == true) {
                    game.printMinefield();
                }
                System.out.println(game.toString());
            }
        }

        if (input3[2] == 1) {
//        creating random mines
            switch (mode) {
                case "easy":
                    game.createMines(input3[0], input3[1], 5);
                    break;
                case "medium":
                    game.createMines(input3[0], input3[1], 12);
                    break;
                case "hard":
                    game.createMines(input3[0], input3[1], 40);
                    break;
            }
//            game.guess(input3[0], input3[1], false);
            game.evaluateField();
            game.revealStart(input3[0], input3[1]);

            if (debug_mode == true) {
                game.printMinefield();
            }
            System.out.println(game.toString());
        }

//        continue running the game until the game is over
        while(!game.gameOver()){
//            getting suitable input from the user
            input3 = new int[] {0, 0, 0, 0};
            while (input3.length != 3 || (input3[2] != -1 && input3[2] != 1) || input3[0] < 0 || input3[1] >= game.getRows() || input3[1] >= game.getColumns()){
                System.out.println("Enter a coordinate and if you wish to place a flag (remaining: " + game.get_flag() + "): [x] [y] [f(-1, else: 1)]");
                input = scan.nextLine();
                input2 = input.split("\\s+");
                input3 = new int[input2.length];
                for (int i = 0; i < input2.length; i++) {
                    input3[i] = Integer.parseInt(input2[i]);
                } // finish getting input in the form of int[]
            }// exit the while loop only with a suitable input
            if (input3[2] == -1){
                game.guess(input3[0], input3[1], true);
            } else {
                game.guess(input3[0], input3[1], false);
            }

            if (debug_mode == true) {
                game.printMinefield();
            }
            System.out.println(game.toString());
        }
    }
}