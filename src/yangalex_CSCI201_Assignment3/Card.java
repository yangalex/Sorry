package yangalex_CSCI201_Assignment3;

// SORRY! card is represented by number 13
public class Card {
	int cardNumber;
	String message;
	
	public Card(int cardNum) {
		this.cardNumber = cardNum;
		
		if (cardNumber == 1) {
			message = "Start a pawn OR move one pawn forward 1 space.";
		} else if (cardNumber == 2) {
			message = "Start a pawn OR move one pawn forward 2 spaces,then draw again.";
		} else if (cardNumber == 3) {
			message = "Move one pawn forward 3 spaces.";
		} else if (cardNumber == 4) {
			message = "Move one pawn backwards 4 spaces.";
		} else if (cardNumber == 5) {
			message = "Move one pawn forward 5 spaces.";
		} else if (cardNumber == 7) {
			message = "Move one pawn forward 7 spaces OR split forward move between any two pawns";
		} else if (cardNumber == 8) {
			message = "Move one pawn forward 8 spaces.";
		} else if (cardNumber == 10) {
			message = "Move one pawn forward 10 spaces OR move one pawn backwards 1 space.";
		} else if (cardNumber == 11) {
			message = "Move one pawn forward 11 spaces OR switch any one of your pawns with one of any opponent's.";
		} else if (cardNumber == 12) {
			message = "Move one pawn forward 12 spaces.";
		} else if (cardNumber == 13) {
			message = "SORRY! Take one pawn from your Start and place it on any space that is occupied by any opponent";
		} else {
			message = "Invalid card!";
		}
	}
}
