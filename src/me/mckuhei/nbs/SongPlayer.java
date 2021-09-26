package me.mckuhei.nbs;

import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SongPlayer {
	Player player;
	NBS nbsFile;
	int tick;
	Timer timer;
	int loopCount;
	public SongPlayer(Player playerIn,NBS nbsIn) {
		this.player=playerIn;
		this.nbsFile=nbsIn;
		timer=new Timer();
		tick=0;
	}
	public void onTick() {
		if(!timer.hasTimePassed((long) ((this.tick/nbsFile.header.tempo)*1000F))) {
			return;
		}
		for(int i=0;i<nbsFile.notes.length&&nbsFile.notes[i].tick<=tick;i++) {
			if(tick==nbsFile.notes[i].tick) {
				Note note=nbsFile.notes[i];
				Layer layer = nbsFile.layers[note.layer];
				player.getWorld().playSound(player.getLocation(), Instrument.getDefaultInstrument(note.instrument), SoundCategory.RECORDS, note.velocity*layer.volume, note.getPitch());
			}
		}
		this.tick++;
		if(!player.isOnline()) {
			Main.playerThread.stop(player.getName());
		}
		if(tick>nbsFile.header.songLength) {
			if(!nbsFile.header.loop||(nbsFile.header.maxLoopCount!=0&&loopCount==nbsFile.header.maxLoopCount)) {
				Main.playerThread.stop(player.getName());
			} else {
				loopCount++;
				tick=0;
				timer.reset();
			}
		}
	}
}
