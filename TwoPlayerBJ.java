public class TwoPlayerBJ extends Thread {

  private BJMultiServerThread playerOneThread;
  private BJMultiServerThread playerTwoThread;
  private Deck deck;
  private BlackjackHand dealerHand;
  private BlackjackHand userHand[];
  private int money[];
  private int bet[];
  private int userAction[];

  public TwoPlayerBJ()
  {
    playerOneThread = null;
    playerTwoThread = null;
    deck = new Deck();
    dealerHand = new BlackjackHand();
    userHand = new BlackjackHand[2];
    userHand[0] = new BlackjackHand();
    userHand[1] = new BlackjackHand();
    money = new int[2];
    bet = new int[2];
    userAction = new int[2];
    money[0] = 100;
    money[1] = 100;
    bet[0] = 0;
    bet[1] = 0;
  }

  public TwoPlayerBJ(BJMultiServerThread one, BJMultiServerThread two){
    playerOneThread = one;
    playerTwoThread = two;
    deck = new Deck();
    dealerHand = new BlackjackHand();
    userHand = new BlackjackHand[2];
    userHand[0] = new BlackjackHand();
    userHand[1] = new BlackjackHand();
    money = new int[2];
    userAction = new int[2];
    money[0] = 100;
    money[1] = 100;
    bet = new int[2];
    bet[0] = 0;
    bet[1] = 0;
  }

  private void sendToBothPlayers(String msg){
    if (playerOneThread != null)
    {
      playerOneThread.out.println(msg);
      playerTwoThread.out.println(msg);
    }
  }

    private void sendPOne(String msg){
      if (playerOneThread != null)
        playerOneThread.out.println(msg);
  }

  private void sendPTwo(String msg){
    if (playerTwoThread != null)
      playerTwoThread.out.println(msg);
    }


    private int checkForBlackjack(){
          int ret = 42;
          if (dealerHand.getBlackjackValue() == 21)
          {
               sendToBothPlayers("Dealer has Blackjack.  Dealer wins.");
               return 0;
          }

          if (userHand[0].getBlackjackValue() == 21)
          {
               sendPOne("You have Blackjack.  You win.");
               sendPTwo("Player 2 has Blackjack.");
               ret = 1;
          }
          if (userHand[1].getBlackjackValue() == 21)
          {
               sendPTwo("You have Blackjack.  You win.");
               if (ret == 1)
                ret = 3;
               ret = 2;
          }
          return ret;
      }

    private void printHands(){

      int size = dealerHand.getCardCount();
      sendToBothPlayers("Dealer has the Cards:");
      for(int i = 0; i < size; i++){
        sendToBothPlayers("\t" + dealerHand.getCard(i));
      }
      sendToBothPlayers("\n");

      size = userHand[0].getCardCount();
      sendToBothPlayers("Player 1 has the Cards:");
      for(int i = 0; i < size; i++){
        sendToBothPlayers("\t" + userHand[0].getCard(i));
      }
      sendToBothPlayers("\n");

      size = userHand[1].getCardCount();
      sendToBothPlayers("Player 2 has the Cards:");
      for(int i = 0; i < size; i++){
        sendToBothPlayers("\t" + userHand[1].getCard(i));
      }
      sendToBothPlayers("\n");
    }


    private void playHand(int j){
      if (j == 0)
      {
        while (userHand[0].getBlackjackValue() < 21 && userAction[0] != 1)
        {
          sendPOne("\nYour cards are:");
          for (int i = 0; i < userHand[0].getCardCount(); i++)
            sendPOne("\t" + userHand[0].getCard(i));
            sendPOne("\nWanna \"Hit\" dat?");
            sendPOne("Read user input");
            while (playerOneThread.userInput == null)
            {
              try { Thread.sleep(500);
              } catch(Exception e) {
                System.out.println("Thread cant sleep");
              }
            }
            if (playerOneThread.userInput.equals("Hit"))
            {
              Card newCard = deck.dealCard();
              userHand[0].addCard(newCard);
              sendPOne("\nUser hits.");
              sendPOne("Your card is the " + newCard);
              sendPOne("Your total is now " + userHand[0].getBlackjackValue());
              playerOneThread.userInput = null;
            }
            else
            {
              sendPOne("\nUser stands.");
              userAction[0] = 1;
            }
        }
        if (userHand[0].getBlackjackValue() > 21)
        {
          sendPOne("You busted by going over 21.  You lose.");
          userAction[0] = 0;
        }
      }
      else if (j == 1)
      {
        while (userHand[1].getBlackjackValue() < 21 && userAction[1] != 1)
        {
          sendPTwo("\nYour cards are:");
          for (int i = 0; i < userHand[1].getCardCount(); i++)
            sendPTwo("\t" + userHand[1].getCard(i));
            sendPTwo("\nWanna \"Hit\" dat?");
            sendPTwo("Read user input");
            while (playerTwoThread.userInput == null)
            {
              try { Thread.sleep(500);
              } catch(Exception e) {
                System.out.println("Thread cant sleep");
              }
            }
            if (playerTwoThread.userInput.equals("Hit"))
            {
              Card newCard = deck.dealCard();
              userHand[1].addCard(newCard);
              sendPTwo("\nUser hits.");
              sendPTwo("Your card is the " + newCard);
              sendPTwo("Your total is now " + userHand[1].getBlackjackValue());
              playerTwoThread.userInput = null;
            }
            else
            {
              sendPTwo("\nUser stands.");
              userAction[1] = 1;
            }
        }
        if (userHand[1].getBlackjackValue() > 21)
        {
          sendPTwo("You busted by going over 21.  You lose.");
          userAction[0] = 0;
        }

      }
      else if (j == 2)
      {
        while (dealerHand.getBlackjackValue() <= 16)
        {
          Card newCard = deck.dealCard();
           sendToBothPlayers("Dealer hits and gets the " + newCard);
           dealerHand.addCard(newCard);
           if (dealerHand.getBlackjackValue() > 21)
              sendToBothPlayers("\nDealer busted by going over 21.");

        }
        if (dealerHand.getBlackjackValue() < 22) //check if players lost
        {
          if (userHand[0].getBlackjackValue() >= 22 || userHand[0].getBlackjackValue() <= dealerHand.getBlackjackValue())
            userAction[0] = 0;
          if (userHand[1].getBlackjackValue() >= 22 || userHand[1].getBlackjackValue() <= dealerHand.getBlackjackValue())
            userAction[1] = 0;
        }
        if (userAction[0] == 0){
          sendToBothPlayers("Player one lost " + bet[0]+ " $$$");
          money[0] -= bet[0];
        }
        else
        {
          sendToBothPlayers("Player one won " + bet[0]+ " $$$");
          money[0] += bet[0];
        }
        if (userAction[1] == 0){
          sendToBothPlayers("Player two lost " + bet[1] +" $$$");
          money[1] -= bet[1];
        }
        else
        {
          sendToBothPlayers("Player two won " + bet[1]+ " $$$");
          money[1] += bet[1];
        }
      }
    }


    private void playRound(){

      int blackjack;

      userAction[0] = 0;
      userAction[1] = 0;
      deck.shuffle();
      dealerHand.addCard(deck.dealCard());
      userHand[0].addCard(deck.dealCard());
      userHand[1].addCard(deck.dealCard());

      dealerHand.addCard(deck.dealCard());
      userHand[0].addCard(deck.dealCard());
      userHand[1].addCard(deck.dealCard());

      blackjack = checkForBlackjack();

      if (blackjack == 0 || blackjack == 3)
      {
        if (blackjack == 3)
        {
          sendToBothPlayers("WoW both Players have blackjack!!!!");
        }
        return ;
      }

      printHands();

      sendPTwo("Waiting for Player 1");

      if (blackjack != 1)
      {
        sendPOne("It's your turn!");
        playHand(0);
      }

      sendPOne("Waiting for Player 2");

      if (blackjack != 2)
      {
        sendPTwo("It's your turn!");
        playHand(1);
      }
      sendToBothPlayers("Dealers turn");
      playHand(2);

      }

  public void run(){


    sendToBothPlayers("starting Game...");
    System.out.println("starting Game...");
    // getting bets
    while (money[0] > 0 && money[1] > 0)
    {
      sendPOne("You have " + money[0] + " dollars.\nYour answer must be between 0 and " + money[0] + '.');
      sendPTwo("You have " + money[1] + " dollars.\nYour answer must be between 0 and " + money[1] + '.');
      sendToBothPlayers("Read user input");

    //p1
      while (playerOneThread.userInput == null)
      {
        try { Thread.sleep(1000);
        } catch(Exception e) {
          System.out.println("Thread cant sleep");
        }
      }

      try { bet[0] = Integer.parseInt(playerOneThread.userInput);
      } catch(Exception e) {
          System.out.println("Execption prasing int... ending game");
          return ;
        }
        playerOneThread.userInput = null;

    //p2
      while (playerTwoThread.userInput == null)
      {
        try { Thread.sleep(1000);
        } catch(Exception e) {
          System.out.println("Thread cant sleep");
        }
      }

      try { bet[1] = Integer.parseInt(playerTwoThread.userInput);
      } catch(Exception e) {
          System.out.println("Execption prasing int... ending game");
          return ;
        }
        playerTwoThread.userInput = null;


    playRound();
    // reseting shit
    playerOneThread.userInput = null;
    playerTwoThread.userInput = null;
    dealerHand.clear();
    userHand[0].clear();
    userHand[1].clear();

    /*
    userAction == 2 <=> Player busted
    userAction == 1 <=> Player won
    */




  }
  if (money[0] == 0)
    sendToBothPlayers("Player One ran out of money...\n\n\nwhat a loser...\nPlayer two walks away with " + money[1] +" GAME OVER");
  else
    sendToBothPlayers("Player Two ran out of money...\n\n\nwhat a loser...\nPlayer one walks away with " + money[0]+ " GAME OVER");
  sendToBothPlayers("Game Over");
}
}
