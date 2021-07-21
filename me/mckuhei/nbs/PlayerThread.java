package me.mckuhei.nbs;

import java.util.ArrayList;

public class PlayerThread extends Thread {
	public ArrayList<SongPlayer> players=new ArrayList<SongPlayer>();
	public PlayerThread() {
		super();
		this.setName("PlayerThread");
	}
	@SuppressWarnings("unchecked")
	public void run() {
		try {
			Main.plugin.getLogger().info(Main.plugin.config.getString("player_starting"));
			for(;;) {
				Thread.sleep(10);
				synchronized(players) {
					for(SongPlayer sp : (ArrayList<SongPlayer>)players.clone()) {
						sp.onTick();
					}
				}
			}
		} catch(InterruptedException e) {
			Main.plugin.getLogger().info(Main.plugin.config.getString("player_quitting"));
		} catch(Exception e) {
			Main.plugin.getLogger().warning(Main.plugin.config.getString("player_quitting_by_exception"));
			e.printStackTrace();
		}
	}
	public void stop(String username) {
		synchronized(players) {
			for(int i=0;i<players.size();i++) if(players.get(i).player.getName().equals(username)) {
				players.get(i).player.sendMessage(Main.plugin.pluginPrefix+Main.plugin.config.getString("stopped"));
				players.remove(i);
			};
		}
	}
	public boolean isPlaying(String username) {
		synchronized(players) {
			for(int i=0;i<players.size();i++) if(players.get(i).player.getName().equals(username)) return true;
		}
		return false;
	}
}
