package yangalex_CSCI201_Assignment3;

import java.awt.Color;

public class Player {
	
	Color color;
	private boolean inGame;
	private int location;
	Tile currentTile;
	
	public Player(Color playerColor) {
		this.color = playerColor;
	}
	
	public void setLocation(int newLocation) {
		if (!inGame) {
			inGame = true;
		}
		location = newLocation;
	}
	
	public int getLocation() {
		if (!inGame) {
			return -1;
		} else {
			return location;
		}
	}
	
	public boolean isInGame() {
		if (inGame) {
			return true;
		} else {
			return false;
		}
	}
	
	public void removeFromGame() {
		inGame = false;
		location = -1;
	}
}
