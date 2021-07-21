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
		if(!(player.isOnline()&&timer.hasTimePassed((long) ((this.tick/nbsFile.header.tempo)*1000F)))) {
			return;
		}
		for(int i=0;i<nbsFile.notes.length&&nbsFile.notes[i].tick<=tick;i++) {
			if(tick==nbsFile.notes[i].tick) {
				Note note=nbsFile.notes[i];
				player.getWorld().playSound(player.getLocation(), Instrument.getDefaultInstrument(note.instrument), SoundCategory.RECORDS, note.velocity/100F, note.getPitch());
			}
		}
		this.tick++;
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
