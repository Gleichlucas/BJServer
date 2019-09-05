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

  private void sendToBothClients(String msg){
    if (playerOneThread != null && playerTwoThread != null)
    {
      playerOneThread.out.println(msg);
      playerTwoThread.out.println(msg);
    }
    else
    {
      sendClientOne("Game Over");
      sendClientTwo("Game Over");
      System.out.println("Player Offline! Killing thread.");
      return ;
    }
  }

    private void sendClientOne(String msg){
      if (playerOneThread != null)
        playerOneThread.out.println(msg);
      else
      {
        sendClientTwo("Game Over");
        System.out.println("Player Offline! Killing thread.");
        return ;
      }
  }

  private void sendClientTwo(String msg){
    if (playerTwoThread != null)
      playerTwoThread.out.println(msg);
    else
    {
      sendClientOne("Game Over");
      System.out.println("Player Offline! Killing thread.");
      return ;
    }
    }
    private void askPlayer(String name, String action)
    {
      if (name.equals("One"))
      {
        if (action.equals("nbr"))
          sendClientOne("Read nbr");
        else
          sendClientOne("Read user input");
        while (playerOneThread.userInput == null)
        {
          //sendClientOne("Hey man, u stil there?");
          try { Thread.sleep(500);
          } catch(Exception e) {
            System.out.println("Thread cant sleep");
          }
        //  if (playerOneThread.alive.equals("Yaas man, relax") == false)
        //  {
      //      sendClientTwo("Player 1 died.. RIP");
      //      return ;
      //    }
    //       playerOneThread.alive = "idk...";
        }
      }

      if (name.equals("Two"))
      {
        if (action.equals("nbr"))
          sendClientTwo("Read nbr");
        else
          sendClientTwo("Read user input");
        while (playerTwoThread.userInput == null)
        {
        //  sendClientTwo("ping");
          try { Thread.sleep(500);
          } catch(Exception e) {
            System.out.println("Thread cant sleep");
          }
      //    if (playerTwoThread.alive.equals("Yaas man, relax") == false)
      //    {
      //      sendClientOne("Player 2 died.. RIP");
      //      return ;
        //    }
          }
      }
    }

    private int checkForBlackjack(){
          int ret = 42;
          if (dealerHand.getBlackjackValue() == 21)
          {
               sendToBothClients("Dealer has Blackjack.  Dealer wins.");
               return 0;
          }

          if (userHand[0].getBlackjackValue() == 21)
          {
               sendClientOne("You have Blackjack.  You win.");
               sendClientTwo("Player 2 has Blackjack.");
               ret = 1;
          }
          if (userHand[1].getBlackjackValue() == 21)
          {
               sendClientTwo("You have Blackjack.  You win.");
               if (ret == 1)
                ret = 3;
               else
                ret = 2;
          }
          return ret;
      }

    private void printHands(){

      int size = dealerHand.getCardCount();
      sendToBothClients("Dealer has the Cards:");
      for(int i = 0; i < size; i++){
        sendToBothClients("\t" + dealerHand.getCard(i));
      }
      sendToBothClients("\n");

      size = userHand[0].getCardCount();
      sendToBothClients("Player 1 has the Cards:");
      for(int i = 0; i < size; i++){
        sendToBothClients("\t" + userHand[0].getCard(i));
      }
      sendToBothClients("\n");

      size = userHand[1].getCardCount();
      sendToBothClients("Player 2 has the Cards:");
      for(int i = 0; i < size; i++){
        sendToBothClients("\t" + userHand[1].getCard(i));
      }
      sendToBothClients("\n");
    }


    private void playHand(int j){
      if (j == 0)
      {
        while (userHand[0].getBlackjackValue() < 21 && userAction[0] != 1)
        {
          sendClientOne("\nYour cards are:");
          for (int i = 0; i < userHand[0].getCardCount(); i++)
            sendClientOne("\t" + userHand[0].getCard(i));
            sendClientOne("\nWanna \"Hit\" dat?");
            askPlayer("One", "action");
            if (playerOneThread.userInput.equals("Hit"))
            {
              Card newCard = deck.dealCard();
              userHand[0].addCard(newCard);
              sendClientOne("\nUser hits.");
              sendClientOne("Your card is the " + newCard);
              sendClientOne("Your total is now " + userHand[0].getBlackjackValue());
              playerOneThread.userInput = null;
            }
            else
            {
              sendClientOne("\nUser stands.");
              userAction[0] = 1;
            }
        }
        if (userHand[0].getBlackjackValue() > 21)
        {
          sendClientOne("You busted by going over 21.  You lose.");
          userAction[0] = 0;
        }
      }
      else if (j == 1)
      {
        while (userHand[1].getBlackjackValue() < 21 && userAction[1] != 1)
        {
          sendClientTwo("\nYour cards are:");
          for (int i = 0; i < userHand[1].getCardCount(); i++)
            sendClientTwo("\t" + userHand[1].getCard(i));
            sendClientTwo("\nWanna \"Hit\" dat?");
              askPlayer("Two", "action");
            if (playerTwoThread.userInput.equals("Hit"))
            {
              Card newCard = deck.dealCard();
              userHand[1].addCard(newCard);
              sendClientTwo("\nUser hits.");
              sendClientTwo("Your card is the " + newCard);
              sendClientTwo("Your total is now " + userHand[1].getBlackjackValue());
              playerTwoThread.userInput = null;
            }
            else
            {
              sendClientTwo("\nUser stands.");
              userAction[1] = 1;
            }
        }
        if (userHand[1].getBlackjackValue() > 21)
        {
          sendClientTwo("You busted by going over 21.  You lose.");
          userAction[0] = 0;
        }

      }
      else if (j == 2)
      {
        while (dealerHand.getBlackjackValue() <= 16)
        {
          Card newCard = deck.dealCard();
           sendToBothClients("Dealer hits and gets the " + newCard);
           dealerHand.addCard(newCard);
           if (dealerHand.getBlackjackValue() > 21)
              sendToBothClients("\nDealer busted by going over 21.");

        }
        if (dealerHand.getBlackjackValue() < 22) //check if players lost
        {
          if (userHand[0].getBlackjackValue() >= 22 || userHand[0].getBlackjackValue() <= dealerHand.getBlackjackValue())
            userAction[0] = 0;
          if (userHand[1].getBlackjackValue() >= 22 || userHand[1].getBlackjackValue() <= dealerHand.getBlackjackValue())
            userAction[1] = 0;
        }
        if (userAction[0] == 0){
          sendToBothClients("Player one lost " + bet[0]+ " $$$");
          money[0] -= bet[0];
        }
        else
        {
          sendToBothClients("Player one won " + bet[0]+ " $$$");
          money[0] += bet[0];
        }
        if (userAction[1] == 0){
          sendToBothClients("Player two lost " + bet[1] +" $$$");
          money[1] -= bet[1];
        }
        else
        {
          sendToBothClients("Player two won " + bet[1]+ " $$$");
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
          sendToBothClients("WoW both Players have blackjack!!!!");
        }
        return ;
      }

      printHands();

      sendClientTwo("Waiting for Player 1");

      if (blackjack != 1)
      {
        sendClientOne("It's your turn!");
        playHand(0);
      }

      sendClientOne("Waiting for Player 2");

      if (blackjack != 2)
      {
        sendClientTwo("It's your turn!");
        playHand(1);
      }
      sendToBothClients("Dealers turn");
      playHand(2);

      }

  public void run(){


    sendToBothClients("starting Game...");
    System.out.println("starting Game...");
    // getting bets
    while (money[0] > 0 && money[1] > 0)
    {
      sendClientOne("You have " + money[0] + " dollars.\nYour answer must be between 0 and " + money[0] + '.');
      sendClientTwo("You have " + money[1] + " dollars.\nYour answer must be between 0 and " + money[1] + '.');
      askPlayer("One", "nbr");
      askPlayer("Two", "nbr");

      try { bet[0] = Integer.parseInt(playerOneThread.userInput);
      } catch(Exception e) {
          System.out.println("Execption prasing int... ending game");
          return ;
        }
        playerOneThread.userInput = null;
      try { bet[1] = Integer.parseInt(playerTwoThread.userInput);
      } catch(Exception e) {
          System.out.println("Execption prasing int... ending game");
          return ;
        }


    playerOneThread.userInput = null;
    playerTwoThread.userInput = null;
    playRound();
    playerOneThread.userInput = null;
    playerTwoThread.userInput = null;
    dealerHand.clear();
    userHand[0].clear();
    userHand[1].clear();




  }
  if (money[0] == 0)
    sendToBothClients("Player One ran out of money...\n\n\nwhat a loser...\nPlayer two walks away with " + money[1] +" GAME OVER");
  else
    sendToBothClients("Player Two ran out of money...\n\n\nwhat a loser...\nPlayer one walks away with " + money[0]+ " GAME OVER");
  sendToBothClients("Game Over");
}
}
