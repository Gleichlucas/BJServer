public class BlackJackProtocol {

    public int player_count;
    public int player_moved[];
    public Deck deck;
    public BlackjackHand dealerHand;
    public BlackjackHand userHand[];     

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
