import java.net.*;
import java.io.*;

public class BJMultiServerThread extends Thread {

    private Socket socket = null;
    private PrintWriter out2;
    private BufferedReader in;
    private int user_id;

    public BJMultiServerThread(Socket socket) {
        super("BJMultiServerThread");
        this.socket = socket;
    }

    public void run() {
        int other_player;
        int money;
        int bet;
        boolean userWins;

        user_id = BJMultiServer.bjp.player_count++;
        if (user_id >= 2)
        {
            BJMultiServer.bjp.player_count--;
            out2.println("no slots open , sorry :(");
            return ;
        }

        try {
            out2 = new PrintWriter(socket.getOutputStream(), true);
            in   = new BufferedReader(
                   new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String inputLine, outputLine;
            outputLine = "Welcome to the game of blackjack.";
            money = 100;
            out2.println(outputLine);
            while (true)
            {
                if (BJMultiServer.bjp.player_count < 2)
                {
                    out2.println("Waiting for second player...");
                    while (BJMultiServer.bjp.player_count < 2)
                    {
                        try { this.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                BJMultiServer.bjp.player_moved[user_id] = 0;
                out2.println("You have " + money + " dollars.");
                bet = -1;
                while (bet < 0 || bet > money)
                {
                    out2.println("How many dollars do you want to bet? (Enter 0 to end.) ? ");
                    out2.println("Your answer must be between 0 and " + money + '.');
                    try { bet = Integer.parseInt(in.readLine());
                    } catch (Exception e) {
                        out2.println("give me a integer pls :(");
                    }
                }
                if (bet == 0)
                    break;
                if (user_id == 1)
                    other_player = 0;
                else
                    other_player = 1;
                userWins = playBlackjack(user_id);
                BJMultiServer.bjp.player_moved[user_id] = 1;
                BJMultiServer.bjp.player_moved[other_player] = 0;
                out2.println("Waiting for other player to play...");
                while(BJMultiServer.bjp.player_moved[other_player] != 1)
                {
                    try{this.sleep(200);}
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                BJMultiServer.bjp = new BlackJackProtocol();
                BJMultiServer.bjp.player_count = 2;
                if (userWins)
                    money = money + bet;
                else
                    money = money - bet;
                if (money == 0)
                {
                    out2.println("Looks like you've are out of money!");
                    break;
                }
            }
        socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BJMultiServer.bjp.player_count--;
    }

    private int checkForBlackjack(int id){

        if (BJMultiServer.bjp.dealerHand.getBlackjackValue() == 21)
        {
             out2.println("Dealer has the " + BJMultiServer.bjp.dealerHand.getCard(0) + " and the " + BJMultiServer.bjp.dealerHand.getCard(1) + ".");
             out2.println("User has the " + BJMultiServer.bjp.userHand[id].getCard(0) + " and the " + BJMultiServer.bjp.userHand[id].getCard(1) + ".\n");
             out2.println("Dealer has Blackjack.  Dealer wins.");
             return 1;
        }

        if (BJMultiServer.bjp.userHand[id].getBlackjackValue() == 21)
        {
             out2.println("Dealer has the " + BJMultiServer.bjp.dealerHand.getCard(0) + " and the " + BJMultiServer.bjp.dealerHand.getCard(1) + ".");
             out2.println("User has the " + BJMultiServer.bjp.userHand[id].getCard(0) + " and the " + BJMultiServer.bjp.userHand[id].getCard(1) + ".\n");
             out2.println("You have Blackjack.  You win.");
             return 2;
        }
        return 0;
    }

    private boolean playBlackjack(int id) {

         int userAction;
         int blackjack;
         BJMultiServer.bjp.deck.shuffle();
         BJMultiServer.bjp.userHand[id].addCard( BJMultiServer.bjp.deck.dealCard() );
         BJMultiServer.bjp.userHand[id].addCard( BJMultiServer.bjp.deck.dealCard() );

         out2.println("\n");
         if (BJMultiServer.bjp.dealerHand.getCardCount() == 0)
         {
             BJMultiServer.bjp.dealerHand.addCard( BJMultiServer.bjp.deck.dealCard() );
             BJMultiServer.bjp.dealerHand.addCard( BJMultiServer.bjp.deck.dealCard() );
         }

         blackjack = checkForBlackjack(id);
         if (blackjack > 0)
         {
            if (blackjack == 1)
                return false;
            return true;
         }
         userAction = -1;
         while (userAction != 1)
         {
             out2.println("\n\nYour cards are:");
             for (int i = 0; i < BJMultiServer.bjp.userHand[id].getCardCount(); i++)
                out2.println("    " + BJMultiServer.bjp.userHand[id].getCard(i));
             out2.println("Your total is " + BJMultiServer.bjp.userHand[id].getBlackjackValue() + "\n");
             out2.println("Dealer is showing the " + BJMultiServer.bjp.dealerHand.getCard(0) + "\n");
             out2.println("Hit (0) or Stand (1)? ");
             while (userAction != 0 && userAction != 1)
             {
                 try { userAction = Integer.parseInt(in.readLine());
                 } catch(NumberFormatException e) {
                     System.out.println("connection to client lost.");
                     return false;
                 }
                   catch (IOException e) {
                    userAction = 2;
                    e.printStackTrace();
                 }
                 if (userAction != 0 && userAction != 1)
                    out2.print("Please respond with '0' or '1':  ");
              }
             if (userAction == 1)
                break;
             Card newCard = BJMultiServer.bjp.deck.dealCard();
             BJMultiServer.bjp.userHand[id].addCard(newCard);
             out2.println("\nUser hits.");
             out2.println("Your card is the " + newCard);
             out2.println("Your total is now " + BJMultiServer.bjp.userHand[id].getBlackjackValue());
             if (BJMultiServer.bjp.userHand[id].getBlackjackValue() > 21)
             {
                 out2.println();
                 out2.println("You busted by going over 21.  You lose.");
                 out2.println("Dealer's other card was the " + BJMultiServer.bjp.dealerHand.getCard(1));
                 return false;
             }
         }
         out2.println("\nUser stands.");
         out2.println("Dealer's cards are");
         out2.println("    " + BJMultiServer.bjp.dealerHand.getCard(0));
         out2.println("    " + BJMultiServer.bjp.dealerHand.getCard(1));
         if (BJMultiServer.bjp.dealerHand.getCardCount() >= 3)
         {
             for ( int i = 2; i < BJMultiServer.bjp.dealerHand.getCardCount(); i++)
             {
                Card newCard = BJMultiServer.bjp.dealerHand.getCard(i);
                out2.println("Dealer hits and gets the " + newCard);
                if (BJMultiServer.bjp.dealerHand.getBlackjackValue() > 21)
                {
                   out2.println();
                   out2.println("Dealer busted by going over 21.  You win.");
                   return true;
                }
             }
         }
         while (BJMultiServer.bjp.dealerHand.getBlackjackValue() <= 16)
         {
            Card newCard = BJMultiServer.bjp.deck.dealCard();
            out2.println("Dealer hits and gets the " + newCard);
            BJMultiServer.bjp.dealerHand.addCard(newCard);
            if (BJMultiServer.bjp.dealerHand.getBlackjackValue() > 21)
            {
               out2.println();
               out2.println("Dealer busted by going over 21.  You win.");
               return true;
            }
         }
         out2.println("Dealer's total is " + BJMultiServer.bjp.dealerHand.getBlackjackValue() + "\n");
         if (BJMultiServer.bjp.dealerHand.getBlackjackValue() == BJMultiServer.bjp.userHand[id].getBlackjackValue())
         {
            out2.println("Dealer wins on a tie. You lose.");
            return false;
         }
         else if (BJMultiServer.bjp.dealerHand.getBlackjackValue() > BJMultiServer.bjp.userHand[id].getBlackjackValue())
         {
            out2.println("Dealer wins, " + BJMultiServer.bjp.dealerHand.getBlackjackValue() + " points to " + BJMultiServer.bjp.userHand[id].getBlackjackValue() + ".");
            return false;
         }
         out2.println("You win, " + BJMultiServer.bjp.userHand[id].getBlackjackValue() + " points to " + BJMultiServer.bjp.dealerHand.getBlackjackValue() + ".");
         return true;
        }
}
