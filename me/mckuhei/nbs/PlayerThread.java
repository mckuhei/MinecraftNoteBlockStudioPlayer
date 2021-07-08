package me.mckuhei.nbs;

import java.util.ArrayList;

public class PlayerThread extends Thread {
	public ArrayList<SongPlayer> players=new ArrayList<SongPlayer>();
	public PlayerThread() {
		super();
		this.setName("PlayerThread");
	}
	public void run() {
		try {
			Main.plugin.getLogger().info("播放线程已创建");
			for(;;) {
				Thread.sleep(10);
				synchronized(players) {
					for(SongPlayer sp : players) {
						sp.onTick();
					}
				}
			}
		} catch(InterruptedException e) {
			Main.plugin.getLogger().info("播放线程已退出");
		} catch(Exception e) {
			Main.plugin.getLogger().warning("播放线程遇到错误，已退出");
			e.printStackTrace();
		}
	}
}
