package me.mckuhei.nbs;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.entity.Player;

public class ThreadDownloadNBS extends Thread {
	Player player;
	URL url;
	public ThreadDownloadNBS(Player playerIn,URL urlIn) {
		setName("Download Thread");
		this.player=playerIn;
		this.url=urlIn;
	}
	public void run() {
		try {
			player.sendMessage(Main.plugin.config.getString("downloading"));
			URLConnection conn=this.url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			NBS nbs=new NBS(new MyInputStream(conn.getInputStream()));
			player.sendMessage(Main.plugin.config.getString("downloaded"));
			if(!Main.plugin.checkSong(nbs, player)) {
				return;
			}
			System.out.println(nbs.notes.length);
			synchronized(Main.playerThread.players){
				Main.playerThread.players.add(new SongPlayer(player,nbs));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
