/**************************************************************************************************
 * Author:           Evan Shimkanon
 * Major:            Computer Science
 * Creation Date:    February 28th, 2021
 * Due Date:         April 17th, 2021
 * Course:           CSC472
 * Professor Name:   Dr. Parson
 * Assignment:       CSC472 - Honors Capstone
 * Filename:         dilemma.java
 * Purpose:          This is the main implementation of the Prisoner's Dilemma using threads and
 *                   multicore programming techniques
 **************************************************************************************************/
import java.lang.Thread;                        //for thread creation and thread methods
import java.util.Scanner;                       //for user input
import java.util.concurrent.ThreadLocalRandom;  //for selecting random numbers in the random strategy (Player 1)

public class dilemma
{
    static int player1Score = 0;    //random strategy; simulates a regular human player
    static int player2Score = 0;    //player which implements a strategy
    static int FriedmanFlag = 0;    //flag for Grim Trigger / Friedman strategy
    static int ALLC = 1;            //All-Cooperate rule
    static int Friedman = 2;        //Grim Trigger / Friedman rule
    static int TitForTat = 3;       //Tit For Tat rule
    static int previousMove = -1;   //Used for Friedman and Tit For Tat
    public static void main (String[] args)
    {
        Scanner s = new Scanner(System.in);
        int strategyCode = 1;
        while (strategyCode != 0)
        {
            player1Score = 0; player2Score = 0; FriedmanFlag = 0; previousMove = -1;
            System.out.println("Which strategy would you like Player 2 to use?");
            System.out.println("0: Exit");
            System.out.println("1: ALL-C");
            System.out.println("2: FRIEDMAN");
            System.out.println("3: TIT FOR TAT");
            System.out.println("4: RANDOM");
                try { strategyCode = s.nextInt(); }
			    catch(Exception e) {System.exit(0);}
            if (strategyCode == 0)
            {
                System.exit(0);
            }
            else if (strategyCode <= 4 && strategyCode >= 0)
            {
                game(strategyCode);
                System.out.println("Player 1's score using RANDOM against strategy "+strategyCode+": "+player1Score);
                System.out.println("Player 2's score with strategy "+strategyCode+": "+player2Score);
            }
            else
            {
                System.out.println("----------------------------");
                System.out.println("Please select a valid option");
                System.out.println("----------------------------");
            }
        }
    }
    public static void game(int strategyCode)
    {
        gameThread t1 = new gameThread(strategyCode);
        gameThread t2 = new gameThread(strategyCode);
        gameThread t3 = new gameThread(strategyCode);

        t1.start();
        t2.start();
        t3.start();
        try
        {
            t1.join();
            t2.join();
            t3.join();
        }
        catch(Exception e)
        {
            System.out.println("ERROR: Failed to join threads.");
        }
    }

    public static class gameThread extends Thread
    {
        //0 = cooperate
        //1 = defect
        int [] scores = new int [2];
        int outcomeFromTurn = -1;
        int gameStrategy = -1;
        gameThread() {}
        gameThread(int strategy)
        {
            this.gameStrategy = strategy;
        }
        public void run()
        {
            int player1Choice = -1; //initial value for debugging
            int player2Choice = -1; //initial value for debugging
            for(int i=0; i<=10000; i++)
            {
                previousMove = player1Choice;
                player1Choice = ThreadLocalRandom.current().nextInt(0, 2); // selects a random number between 0 and 1
                if(gameStrategy == 1)
                {
                    player2Choice = determineALLC(player1Choice); // always cooperative
                }
                else if(gameStrategy == 2)
                {
                    player2Choice = determineFriedman(i, player1Choice); // Friedman: cooperates until first defection, then never cooperates again
                }
                else if(gameStrategy == 3)
                {
                    player2Choice = determineTitForTat(i, previousMove); // Tit For Tat: cooperates until defection, then sends a retaliation defection
                                                                         // Depends on the nature of the other player's choices
                }
                else
                {
                    player2Choice = ThreadLocalRandom.current().nextInt(0, 2); // selects a random number between 0 and 1
                }
                scores = determineScores(player1Choice, player2Choice);
                outcomeFromTurn = scores[0];
                incrementPlayer1Score(outcomeFromTurn);
                outcomeFromTurn = scores[1];
                incrementPlayer2Score(outcomeFromTurn);
            }
        } 
    }
    public static int determineALLC(int player1Choice)
    {
        return 0;
    }
    public static int determineFriedman(int turn, int player1Choice)
    {
        if(turn == 0)
        {
            return 0;
        }
        else
        {
            if (previousMove == 1)
            {
                FriedmanFlag = 1;
                return 1;
            }
            else if (FriedmanFlag == 1)
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }
    public static int determineTitForTat(int turn, int previousMove)
    {
        if(turn == 0)
        {
            return 0;
        }
        else
        {
            if (previousMove == 0)
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
    }

    public static int [] determineScores(int player1Choice, int player2Choice)
    {
        int [] scores = new int [2];
        if(player1Choice == 0 && player2Choice == 0)
        {
            scores[0] = 1;
            scores[1] = 1;
        }
        else if(player1Choice == 0 && player2Choice == 1)
        {
            scores[0] = 5;
            scores[1] = 0;
        }
        else if(player1Choice == 1 && player2Choice == 0)
        {
            scores[0] = 0;
            scores[1] = 5;
        }
        else if(player1Choice == 1 && player2Choice == 1)
        {
            scores[0] = 3;
            scores[1] = 3;
        }
        return scores;
    }

    public static synchronized void incrementPlayer1Score(int outcomeFromTurn)
    {
        player1Score = player1Score + outcomeFromTurn;
    }
    public static synchronized void incrementPlayer2Score(int outcomeFromTurn)
    {
        player2Score = player2Score + outcomeFromTurn;
    }
}