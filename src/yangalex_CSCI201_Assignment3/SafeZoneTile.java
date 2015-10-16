package yangalex_CSCI201_Assignment3;

import java.awt.Color;

public class SafeZoneTile extends Tile {
	public static final long serialVersionUID = 2341431;
	int safeZoneId;
	SafeZoneEntryTile entryTile;
	
	public SafeZoneTile(Color color, int safeZoneId) {
		super(color, false);
		this.safeZoneId = safeZoneId;
	}
	
	public SafeZoneTile[] getSafeZone() {
		return entryTile.safeZone;
	}
}
