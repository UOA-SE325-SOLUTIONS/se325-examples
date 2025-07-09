package se325.websocketchat.common;
/*
 * CompSci 101 - Keyboard Class
 * ============================
 * Version 4th March, 2011
 *
 * This class is used for input from the keyboard.
 * YOU DO NOT NEED TO UNDERSTAND THE DETAILS OF THIS CLASS.
 * To use this class, put it in the same directory as the source file for your program.
 *
 * Example usage:
 *
 * String input = Keyboard.readInput();
 *
 * This will assign the line of text entered at the keyboard (as a String) to the input variable.
 *
 */

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Keyboard {

    private static Scanner in = new Scanner(System.in);
    private static boolean redirected = false;

    public static String readInput() {

        try {
            if (!redirected) {
                redirected = System.in.available() != 0;
            }
        } catch (IOException e) {
            System.err.println("An error has occurred in the Keyboard constructor.");
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            String input = in.nextLine();
            if (redirected) {
                System.out.println(input);
            }
            return input;
        } catch (NoSuchElementException e) {
            return null; // End of file
        } catch (IllegalStateException e) {
            System.err.println("An error has occurred in the Keyboard.readInput() method.");
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    public static String prompt(String prompt) {
        System.out.print(prompt + " > ");
        return readInput();
    }
}
