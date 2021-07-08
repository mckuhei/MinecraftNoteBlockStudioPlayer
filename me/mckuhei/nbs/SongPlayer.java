package me.mckuhei.nbs;

import org.bukkit.entity.Player;

public class SongPlayer {
	Player player;
	NBS nbsFile;
	long startTime;
	int tick;
	public SongPlayer(Player playerIn,NBS nbsIn) {
		this.player=playerIn;
		this.nbsFile=nbsIn;
		this.startTime=System.currentTimeMillis();
	}
	public void onTick() {
		if(System.currentTimeMillis()<this.startTime+tick*(1/nbsFile.header.tempo)*1000)
			return;
		
		this.tick++;
	}
}
