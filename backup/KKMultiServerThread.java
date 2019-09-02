import java.net.*;
import java.io.*;

public class KKMultiServerThread extends Thread {
    private Socket socket = null;

    public KKMultiServerThread(Socket socket) {
        super("KKMultiServerThread");
        this.socket = socket;
    }
    PrintWriter out2;
    BufferedReader in;
    int user_id;
    public void run() {
        user_id = KKMultiServer.bjp.player_count++;
        if (user_id >= 3)
        {
            KKMultiServer.bjp.player_count--;
            out2.println("no slots open , sorry :(");
            return ;
        }
        int other_player;
        int money;
        int bet;
        boolean userWins;
        try{
        out2 = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(
            new InputStreamReader(
                socket.getInputStream()));
        } catch (IOException e) {
        e.printStackTrace();
        }
        try{
            String inputLine, outputLine;
            outputLine = "Welcome to the game of blackjack.";
            money = 100;
            out2.println(outputLine);

            //while ((inputLine = in.readLine()) != null) {
                while (true) {
                    if (KKMultiServer.bjp.player_count < 2)
                    {
                        out2.println("Waiting for second player...");
                    }
                    while(KKMultiServer.bjp.player_count < 2)
                    {
                        try{ this.sleep(200);}
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    KKMultiServer.bjp.player_moved[user_id] = 0;
                    out2.println("You have " + money + " dollars.");
                    do
                    {
                        out2.println("How many dollars do you want to bet? (Enter 0 to end.) ? ");
                        try{bet = Integer.parseInt(in.readLine());}
                        catch(Exception e){
                            bet = -1;
                            out2.println("give me a integer pls :(");
                        }
                        if (bet < 0 || bet > money)
                            out2.println("Your answer must be between 0 and " + money + '.');
                    } while (bet < 0 || bet > money);
                    if (bet == 0)
                        break;
                    if (user_id == 1)
                        other_player = 0;
                    else
                        other_player = 1;
                    userWins = playBlackjack(user_id);
                    KKMultiServer.bjp.player_moved[user_id] = 1;
                    KKMultiServer.bjp.player_moved[other_player] = 0;
                    out2.println("Waiting for other player to play...");
                    while(KKMultiServer.bjp.player_moved[other_player] != 1)
                    {
                        try{ this.sleep(200);}
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    KKMultiServer.bjp = new BlackJackProtocol();
                    KKMultiServer.bjp.player_count = 2;
                    if (userWins)
                        money = money + bet;
                    else
                        money = money - bet;

                    if (money == 0) {
                            out2.println("Looks like you've are out of money!");
                            break;
                        }
                    }

            //    outputLine = (inputLine);
            //    out2.println(outputLine);
            //    if (outputLine.equals("Bye"))
            //        break;
        //    }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        KKMultiServer.bjp.player_count--;
    }
    boolean playBlackjack(int id) {
            // Let the user play one game of Blackjack.
            // Return true if the user wins, false if the user loses.

        // Deck deck;                  // A deck of cards.  A new deck for each game.
        // BlackjackHand dealerHand;   // The dealer's hand.
        // BlackjackHand userHand[id];     // The user's hand.

         //deck = new Deck();
        // dealerHand = new BlackjackHand();
    //     userHand[id] = new BlackjackHand();

         /*  Shuffle the deck, then deal two cards to each player. */

         KKMultiServer.bjp.deck.shuffle();
         KKMultiServer.bjp.userHand[id].addCard( KKMultiServer.bjp.deck.dealCard() );
         KKMultiServer.bjp.userHand[id].addCard( KKMultiServer.bjp.deck.dealCard() );

         out2.println();
         out2.println();

         /* Check if one of the players has Blackjack (two cards totaling to 21).
            The player with Blackjack wins the game.  Dealer wins ties.
         */
         if (KKMultiServer.bjp.dealerHand.getCardCount() == 0)
         {
             KKMultiServer.bjp.dealerHand.addCard( KKMultiServer.bjp.deck.dealCard() );
             KKMultiServer.bjp.dealerHand.addCard( KKMultiServer.bjp.deck.dealCard() );
         }

         if (KKMultiServer.bjp.dealerHand.getBlackjackValue() == 21) {
              out2.println("Dealer has the " + KKMultiServer.bjp.dealerHand.getCard(0)
                                      + " and the " + KKMultiServer.bjp.dealerHand.getCard(1) + ".");
              out2.println("User has the " + KKMultiServer.bjp.userHand[id].getCard(0)
                                        + " and the " + KKMultiServer.bjp.userHand[id].getCard(1) + ".");
              out2.println();
              out2.println("Dealer has Blackjack.  Dealer wins.");

              return false;
         }

         if (KKMultiServer.bjp.userHand[id].getBlackjackValue() == 21) {
              out2.println("Dealer has the " + KKMultiServer.bjp.dealerHand.getCard(0)
                                      + " and the " + KKMultiServer.bjp.dealerHand.getCard(1) + ".");
              out2.println("User has the " + KKMultiServer.bjp.userHand[id].getCard(0)
                                        + " and the " + KKMultiServer.bjp.userHand[id].getCard(1) + ".");
              out2.println();
              out2.println("You have Blackjack.  You win.");
              return true;
         }

         /*  If neither player has Blackjack, play the game.  First the user
             gets a chance to draw cards (i.e., to "Hit").  The while loop ends
             when the user chooses to "Stand".  If the user goes over 21,
             the user loses immediately.
         */

         while (true) {

              /* Display user's cards, and let user decide to Hit or Stand. */

              out2.println();
              out2.println();
              out2.println("Your cards are:");
              for ( int i = 0; i < KKMultiServer.bjp.userHand[id].getCardCount(); i++ )
                 out2.println("    " + KKMultiServer.bjp.userHand[id].getCard(i));
              out2.println("Your total is " + KKMultiServer.bjp.userHand[id].getBlackjackValue());
              out2.println();
              out2.println("Dealer is showing the " + KKMultiServer.bjp.dealerHand.getCard(0));
              out2.println();
              out2.println("Hit (0) or Stand (1)? ");
              int userAction;
              do {
                  try{
                 userAction = Integer.parseInt(in.readLine());
             }catch(NumberFormatException e) {
                 System.out.println("connection to client lost.");
                 return false;
             }catch (IOException e) {
                    userAction = 2;
                e.printStackTrace();
                }
                 if (userAction != 0 && userAction != 1)
                    out2.print("Please respond with '0' or '1':  ");
              } while (userAction != 0 && userAction != 1);

              /* If the user Hits, the user gets a card.  If the user Stands,
                 the loop ends (and it's the dealer's turn to draw cards).
              */

              if ( userAction == 1 ) {
                      // Loop ends; user is done taking cards.
                  break;
              }
              else {  // userAction is 'H'.  Give the user a card.
                      // If the user goes over 21, the user loses.
                  Card newCard = KKMultiServer.bjp.deck.dealCard();
                  KKMultiServer.bjp.userHand[id].addCard(newCard);
                  out2.println();
                  out2.println("User hits.");
                  out2.println("Your card is the " + newCard);
                  out2.println("Your total is now " + KKMultiServer.bjp.userHand[id].getBlackjackValue());
                  if (KKMultiServer.bjp.userHand[id].getBlackjackValue() > 21) {
                      out2.println();
                      out2.println("You busted by going over 21.  You lose.");
                      out2.println("Dealer's other card was the "
                                                         + KKMultiServer.bjp.dealerHand.getCard(1));
                      return false;
                  }
              }

         } // end while loop

         /* If we get to this point, the user has Stood with 21 or less.  Now, it's
            the dealer's chance to draw.  Dealer draws cards until the dealer's
            total is > 16.  If dealer goes over 21, the dealer loses.
         */

         out2.println();
         out2.println("User stands.");
         out2.println("Dealer's cards are");
         out2.println("    " + KKMultiServer.bjp.dealerHand.getCard(0));
         out2.println("    " + KKMultiServer.bjp.dealerHand.getCard(1));
         out2.println("wating for other player...");
         KKMultiServer.bjp.player_moved[id] = 1;
         // tmp = 0;
         // while(tmp != 1)
         // {
         //     try{ this.sleep(200);}
         //     catch (InterruptedException e) {
         //         e.printStackTrace();
         //     }
         //    tmp = KKMultiServer.bjp.player_moved[other_player];
         // }
         if (KKMultiServer.bjp.dealerHand.getCardCount() >= 3)
         {
             for ( int i = 2; i < KKMultiServer.bjp.dealerHand.getCardCount(); i++ )
             {
                Card newCard = KKMultiServer.bjp.dealerHand.getCard(i);
                out2.println("Dealer hits and gets the " + newCard);
                if (KKMultiServer.bjp.dealerHand.getBlackjackValue() > 21) {
                   out2.println();
                   out2.println("Dealer busted by going over 21.  You win.");
                   return true;
                }
             }

         }
         while (KKMultiServer.bjp.dealerHand.getBlackjackValue() <= 16) {
            Card newCard = KKMultiServer.bjp.deck.dealCard();
            out2.println("Dealer hits and gets the " + newCard);
            KKMultiServer.bjp.dealerHand.addCard(newCard);
            if (KKMultiServer.bjp.dealerHand.getBlackjackValue() > 21) {
               out2.println();
               out2.println("Dealer busted by going over 21.  You win.");
               return true;
            }
         }
         out2.println("Dealer's total is " + KKMultiServer.bjp.dealerHand.getBlackjackValue());

         /* If we get to this point, both players have 21 or less.  We
            can determine the winner by comparing the values of their hands. */

         out2.println();
         if (KKMultiServer.bjp.dealerHand.getBlackjackValue() == KKMultiServer.bjp.userHand[id].getBlackjackValue()) {
            out2.println("Dealer wins on a tie.  You lose.");
            return false;
         }
         else if (KKMultiServer.bjp.dealerHand.getBlackjackValue() > KKMultiServer.bjp.userHand[id].getBlackjackValue()) {
            out2.println("Dealer wins, " + KKMultiServer.bjp.dealerHand.getBlackjackValue()
                             + " points to " + KKMultiServer.bjp.userHand[id].getBlackjackValue() + ".");
            return false;
         }
         else {
            out2.println("You win, " + KKMultiServer.bjp.userHand[id].getBlackjackValue()
                             + " points to " + KKMultiServer.bjp.dealerHand.getBlackjackValue() + ".");
            return true;
         }

      }



}
