package me.mckuhei.nbs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class Main extends JavaPlugin {
	public static Plugin plugin;
	public static ProtocolManager pm;
	public static PlayerThread playerThread;
	public void onEnable() {
		File folder=new File("plugins/NoteBlockStudioPlayer/");
		if (!folder.exists()) {
			folder.mkdir();
		}
		plugin=this;
		pm=ProtocolLibrary.getProtocolManager();
		if(pm.getMinecraftVersion().getMinor()<=14) {
			getServer().getConsoleSender().sendMessage("[NoteBlockStudioPlayer] ��4����:��������Ҫ1.14�汾���ϲ���ʹ��������");
			getServer().getPluginManager().disablePlugins();
			return;
		}
		playerThread=new PlayerThread();
		playerThread.start();
	}
	public void onDisable() {
		if(playerThread!=null&&playerThread.isAlive()) {
			playerThread.interrupt();
		}
	}
	private boolean checkSong(NBS nbs,Player player) {
		boolean flag=true;
		if(nbs.header.tempo>100f) {
			player.sendMessage(String.format("��4����:tempo����С��100,���Ƿ�����%f", nbs.header.tempo));
			flag=false;
		}
		for(Note i:nbs.notes) {
			if(i.key-33>24||i.key-33<0) {
				player.sendMessage(String.format("��4����:��%dtick�ĵ�%d���������Ч", i.tick,i.layer));
				flag=false;
			}
			if(i.instrument>15) {
				player.sendMessage(String.format("��4����:��%dtick�ĵ�%d�㷢����Ч����", i.tick,i.layer));
				flag=false;
			}
			if(i.layer>=nbs.header.songLayers) {
				player.sendMessage(String.format("��4����:��%dtick�ĵ�%d�㷢����Ч��", i.tick,i.layer));
				flag=false;
			}
		}
		
		return flag;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("songplayer")) {
			if (args.length==0) {
				sender.sendMessage("/songplayer <play/stop>");
				return false;
			}
			switch(args[0].toLowerCase()) {
			case "play":
				if (args.length==1) {
					sender.sendMessage("/songplayer play <file>");
					return false;
				}
				String filename="";
				for(int i=1;i<args.length;i++) {
					filename+=args[i];
					if (i!=args.length-1) filename+=" ";
				}
				File file=new File("plugins/NoteBlockStudioPlayer/"+filename);
				if (!file.exists()) {
					return false;
				}
				MyInputStream stream;
				try {
					stream = new MyInputStream(new FileInputStream(file));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return false;
				}
				NBS nbs;
				try {
					nbs=new NBS(stream);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				playerThread.players.add(new SongPlayer((Player)sender,nbs));
				break;
			case "stop":
				break;
			}
		}
		return true;
	}
}