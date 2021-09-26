package me.mckuhei.nbs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class Main extends JavaPlugin implements TabCompleter {
	public static Main plugin;
	public static ProtocolManager pm;
	public static PlayerThread playerThread;
	public FileConfiguration config=getConfig();
	public String pluginPrefix=config.getString("prefix");
	public void onEnable() {
		File folder=new File("plugins/NoteBlockStudioPlayer/");
		if (!folder.exists()) {
			folder.mkdir();
		}
		if(pluginPrefix==null) {
			this.saveDefaultConfig();
			config=getConfig();
			pluginPrefix=config.getString("prefix");
		}
		plugin=this;
		pm=ProtocolLibrary.getProtocolManager();
		if(pm.getMinecraftVersion().getMinor()<=14) {
			getServer().getConsoleSender().sendMessage(pluginPrefix+config.getString("version_error"));
			getServer().getPluginManager().disablePlugins();
			return;
		}
		playerThread=new PlayerThread();
		playerThread.start();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void walk(File f,List list,String prefix) {
		for(File file:f.listFiles()) {
			if(file.isFile()) {
				list.add(prefix+file.getName());
			} else {
				walk(file,list,prefix+file.getName()+"/");
			}
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
				if(filename.matches("https?://.*")) {
					return new ArrayList(0);
				}
				filename=filename.trim().toLowerCase();
				File file=new File("plugins/NoteBlockStudioPlayer/");
				ArrayList<String> list=new ArrayList<String>();
				walk(file, list, "");
				ArrayList<String> list2=new ArrayList<String>();
				for(String s:list) {
					if(s.toLowerCase().startsWith(filename)) list2.add(s);
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
	public boolean checkSong(NBS nbs,CommandSender player) {
		boolean flag=true;
		if(nbs.header.tempo>100f) {
			player.sendMessage(String.format(pluginPrefix+config.getString("tempo_error"), nbs.header.tempo));
			flag=false;
		}
		for(Note i:nbs.notes) {
			int key = (int) Math.ceil(i.key+i.pitch/100F);
			if(key-33>24||key-33<0) {
				player.sendMessage(String.format(pluginPrefix+config.getString("note_error"), i.tick,i.layer));
				flag=false;
			}
			// TODO: 自定义乐器
			if(i.instrument>15) {
				player.sendMessage(String.format(pluginPrefix+config.getString("instrument_error"), i.tick,i.layer));
				flag=false;
			}
			/*if(i.layer>nbs.header.songLayers) {
				player.sendMessage(String.format("��4����:��%dtick�ĵ�%d�㷢����Ч��", i.tick,i.layer));
				flag=false;
			}*/
		}
		
		return flag;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("songplayer")) {
			if (args.length==0||!(sender instanceof Player)) {
				sender.sendMessage("/songplayer <play/stop>");
				return false;
			}
			switch(args[0].toLowerCase()) {
			case "play":
				if(playerThread.isPlaying(sender.getName())) {
					sender.sendMessage(pluginPrefix+config.getString("already_playing"));
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
				if(filename.matches("https?://.*")) {
					try {
						new ThreadDownloadNBS((Player) sender, new URL(filename)).start();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						sender.sendMessage(pluginPrefix+e);
					}
				} else {
					File file=new File("plugins/NoteBlockStudioPlayer/"+filename.trim());
					if (!file.exists()||file.isDirectory()) {
						sender.sendMessage(pluginPrefix+config.getString("file_not_found"));
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
				}
				break;
			case "stop":
				playerThread.stop(sender.getName());
				break;
			}
		}
		return true;
	}
}
