package yangalex_CSCI201_Assignment3;

import java.awt.Color;

// a subclass of Tile that has an array to represent the safezone
public class SafeZoneEntryTile extends Tile {
	public static final long serialVersionUID = 231231;
	
	// has length 6, index 5 is Home tile
	public SafeZoneTile[] safeZone;
	SafeZoneTile firstSafeZoneTile;
	
	public SafeZoneEntryTile(Color tileColor, int tileId) {
		super(tileColor, true);
		this.tileId = tileId;
		
		buildSafeZone();
	}
	
	private void buildSafeZone() {
		safeZone = new SafeZoneTile[6];
		
		int index = 0;
		
		for (int i=0; i < 5; i++) {
			safeZone[i] = new SafeZoneTile(tileColor, index);
			safeZone[i].entryTile = this;
			index++;
		}
		safeZone[5] = new HomeTile(tileColor);
		
		firstSafeZoneTile = safeZone[0];
		
		// link up safeZoneTiles
		safeZone[0].previousTile = this;
		safeZone[0].nextTile = safeZone[1];
		safeZone[5].previousTile = safeZone[4];
		for (int i=1; i < 5; i++) {
			safeZone[i].previousTile = safeZone[i-1];
			safeZone[i].nextTile = safeZone[i+1];
		}
	}
	
	public HomeTile getHomeTile() {
		return (HomeTile)safeZone[safeZone.length-1];
	}
}
