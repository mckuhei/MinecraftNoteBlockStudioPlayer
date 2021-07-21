package me.mckuhei.nbs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class Main extends JavaPlugin implements TabCompleter {
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
			getServer().getConsoleSender().sendMessage("[NoteBlockStudioPlayer] §4错误:你至少需要1.14版本以上才能使用这个插件");
			getServer().getPluginManager().disablePlugins();
			return;
		}
		playerThread=new PlayerThread();
		playerThread.start();
	}
	public void walk(File f,List list,String prefix) {
		for(File file:f.listFiles()) {
			if(file.isFile()) {
				list.add(prefix+file.getName());
			} else {
				walk(file,list,prefix+file.getName()+"/");
			}
		}
	}
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(command.getName().equalsIgnoreCase("songplayer")) {
			if(args.length==0||args.length==1)
				return Arrays.asList(new String[] {"play","stop"});
			else if(args[0].equalsIgnoreCase("play")) {
				String filename="";
				for(int i=1;i<args.length;i++) {
					filename+=args[i];
					filename+=" ";
				}
				filename=filename.trim();
				File file=new File("plugins/NoteBlockStudioPlayer/");
				ArrayList<String> list=new ArrayList<String>();
				walk(file, list, "");
				ArrayList<String> list2=new ArrayList<String>();
				for(String s:list) {
					if(s.startsWith(filename)) list2.add(s);
				}
				return list2;
			}
		}
		return new ArrayList(0);
	}
	public void onDisable() {
		if(playerThread!=null&&playerThread.isAlive()) {
			playerThread.interrupt();
		}
	}
	private boolean checkSong(NBS nbs,CommandSender player) {
		boolean flag=true;
		if(nbs.header.tempo>100f) {
			player.sendMessage(String.format("§4错误:tempo必须小于100,但是发现了%f", nbs.header.tempo));
			flag=false;
		}
		for(Note i:nbs.notes) {
			if(i.key-33>24||i.key-33<0) {
				player.sendMessage(String.format("§4错误:在%dtick的第%d层的音符无效", i.tick,i.layer));
				flag=false;
			}
			if(i.instrument>15) {
				player.sendMessage(String.format("§4错误:在%dtick的第%d层发现无效乐器", i.tick,i.layer));
				flag=false;
			}
			if(i.layer>nbs.header.songLayers) {
				player.sendMessage(String.format("§4错误:在%dtick的第%d层发现无效层", i.tick,i.layer));
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
				if(playerThread.isPlaying(sender.getName())) {
					sender.sendMessage("已经播放了");
					return true;
				}
				if (args.length==1) {
					sender.sendMessage("/songplayer play <file>");
					return false;
				}
				String filename="";
				for(int i=1;i<args.length;i++) {
					filename+=args[i];
					filename+=" ";
				}
				File file=new File("plugins/NoteBlockStudioPlayer/"+filename.trim());
				if (!file.exists()||file.isDirectory()) {
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
				if(!checkSong(nbs, sender)) {
					return true;
				}
				playerThread.players.add(new SongPlayer((Player)sender,nbs));
				break;
			case "stop":
				playerThread.stop(sender.getName());
				break;
			}
		}
		return true;
	}
}
