public class BlackJackProtocol {

    public int player_count;
    public int player_moved[];
    public Deck deck;                  // A deck of cards.  A new deck for each game.
    public BlackjackHand dealerHand;   // The dealer's hand.
    public BlackjackHand userHand[];     // The user's hand.

    public BlackJackProtocol(){
        player_count = 0;
        deck = new Deck();
        dealerHand = new BlackjackHand();
        userHand = new BlackjackHand[2];
        userHand[0] = new BlackjackHand();
        userHand[1] = new BlackjackHand();
        player_moved = new int[2];
        player_moved[0] = 0;
        player_moved[1] = 0;
    }
}
