package FluidText.fluidtext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public final class fluidtext extends JavaPlugin {
	private String divider = "";
	private ChatColor yamlcolor=null;
	private float rowsperpage=0;
	@Override
	public void onEnable(){
		getLogger().info("FluidText has been Enabled");
		File f = getDataFolder();
		if (!f.exists()){
			f.mkdir();
		}
		ReloadCustomConfig();
		}
	@Override
	public void onDisable(){
		getLogger().info("FluidText has been disabled");
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("colors")){
			sender.sendMessage(FormatParse("&00&11&22&33&44&55&66&77&88&99&aa&bb&cc&dd&ee&ff  &ll&r&mm&r&nn&r&oo&rr",sender, true));
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("FluidTest")){
			if (sender instanceof Player){
				FancyMessage msg=new FancyMessage();
				msg.text(FormatParse("&6Text",sender,false));
				msg.send(sender);
				msg.text("This is a button: ");
				msg.then(FormatParse("&4[&6Click me&4]",sender,true)).command("/say boop").tooltip("Click here to boop");
				msg.then(" This is some more text");
				msg.send(sender);
			}else{
				sender.sendMessage("This Command can be executed only by a player");
			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("FluidHelp")){
			if (sender instanceof Player){
				if (args.length<2){
					FileParser(args[0],sender,1);
				}else{
					try{
						int num=Integer.parseInt(args[1]);
						FileParser(args[0],sender,num);
					}catch (NumberFormatException e){
						sender.sendMessage("Page number not valid");
					}
				}
			}else{
				sender.sendMessage("This Command can be executed only by a player");
			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("FluidYAML")){
			if (sender instanceof Player){
				if (args.length==2){
						YAMLParser(sender,args[0],args[1]);
				}else{
					sender.sendMessage("Usage: /FluidYAML file section");
				}
				return true;
			}
		}
		else if (cmd.getName().equalsIgnoreCase("FluidEdit")){
			if (sender instanceof Player){
				if (args.length==2){
					getConfig().set(args[0], args[1]);
					saveConfig();
					reloadConfig();
					ReloadCustomConfig();
					sender.sendMessage(args[0]+" setting successfully edited to "+args[1]);
				}else{
					sender.sendMessage("Usage: /FluidEdit section option");
				}
				return true;
			}
		}
		else if (cmd.getName().equalsIgnoreCase("setItem")){
			if (sender instanceof Player){
				ItemStack i=((Player) sender).getItemInHand();
				File customfile = new File(getDataFolder(),"items.yml");
				FileConfiguration customyml=YamlConfiguration.loadConfiguration(customfile);
				Map<String, Object> map=customyml.getValues(false);
				if (map.get(args[0])==null){
					customyml.createSection(args[0]);
					customyml.set(args[0],i);
				}else{
					customyml.set(args[0], i);
				}
				try {
					customyml.save(customfile);
				} catch (IOException e) {
					sender.sendMessage("Unable to save item, items.yml not found");
				}
			}else{
				sender.sendMessage("This command can be executed only by a player");
			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("delItem")){
			File customfile = new File(getDataFolder(),"items.yml");
			FileConfiguration customyml=YamlConfiguration.loadConfiguration(customfile);
			customyml.set(args[0], null);
			try {
				customyml.save(customfile);
			} catch (IOException e) {
				sender.sendMessage("Unable to save item, items.yml not found");
			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("getItem")){
			PlayerInventory pi = ((Player) sender).getInventory();
			if (pi.firstEmpty()==-1) {
				sender.sendMessage("You need at least 1 free space in your inventory");
			}else{
				File customfile = new File(getDataFolder(),"items.yml");
				FileConfiguration customyml=YamlConfiguration.loadConfiguration(customfile);
				ItemStack i=customyml.getItemStack(args[0]);
				pi.addItem(i);
			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("link")){
			if (sender instanceof Player){
				ItemStack is=((Player) sender).getItemInHand();
				FancyMessage msg=new FancyMessage();
				if (is.getItemMeta().hasDisplayName()){
					msg.text("<").color(ChatColor.YELLOW).then("Link").color(ChatColor.BLUE).then(">").color(ChatColor.YELLOW).then("<"+sender.getName()+">: ["+is.getItemMeta().getDisplayName()+"]");
				}else{
					msg.text("<").color(ChatColor.YELLOW).then("Link").color(ChatColor.BLUE).then(">").color(ChatColor.YELLOW).then("<"+sender.getName()+">: ["+is.getType()+"]");
				}
				msg.itemTooltip(is);
				Iterable<? extends Player> pl=Bukkit.getOnlinePlayers();
				msg.send(pl);
			}else{
				sender.sendMessage("This command must be executed by a player");
			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("version")){
			sender.sendMessage("FluidText version "+Bukkit.getServer().getPluginManager().getPlugin("FluidText").getDescription().getVersion()+"\n Developed by Penaz");
		}
		return false;
	}
	public void YAMLParser(CommandSender sender, String file, String section){
		try{
			File customFile= new File(getDataFolder()+"/YamlHelpFiles",file+".yml");
			FileConfiguration customyml= YamlConfiguration.loadConfiguration(customFile);
			Map<String, Object> map=customyml.getValues(false);
			String line=map.get(section).toString();
			FancyMessage msg=new FancyMessage();
			msg.text(FormatParse(divider,null,true));
			msg.send(sender);
			msg=new FancyMessage();
			msg=Parse(line,sender);
			msg.color(yamlcolor);
			msg.send(sender);
			msg=new FancyMessage();
			msg.text(FormatParse(divider,null,true));
			msg.send(sender);
			
		}catch (NullPointerException e){
			sender.sendMessage("File or Section not found");
		}
	}
	public void ReloadCustomConfig(){
		divider=getConfig().getString("divider");
		String col=getConfig().getString("YamlDefaultColor");
		if (col.equals("0")){yamlcolor=ChatColor.BLACK;}
		else if (col.equals("1")){yamlcolor=ChatColor.DARK_BLUE;}
		else if (col.equals("2")){yamlcolor=ChatColor.DARK_GREEN;}
		else if (col.equals("3")){yamlcolor=ChatColor.DARK_AQUA;}
		else if (col.equals("4")){yamlcolor=ChatColor.DARK_RED;}
		else if (col.equals("5")){yamlcolor=ChatColor.DARK_PURPLE;}
		else if (col.equals("6")){yamlcolor=ChatColor.GOLD;}
		else if (col.equals("7")){yamlcolor=ChatColor.GRAY;}
		else if (col.equals("8")){yamlcolor=ChatColor.DARK_GRAY;}
		else if (col.equals("9")){yamlcolor=ChatColor.BLUE;}
		else if (col.equals("a")){yamlcolor=ChatColor.GREEN;}
		else if (col.equals("b")){yamlcolor=ChatColor.AQUA;}
		else if (col.equals("c")){yamlcolor=ChatColor.RED;}
		else if (col.equals("d")){yamlcolor=ChatColor.LIGHT_PURPLE;}
		else if (col.equals("e")){yamlcolor=ChatColor.YELLOW;}
		else if (col.equals("f")){yamlcolor=ChatColor.WHITE;}
		rowsperpage=Integer.parseInt(getConfig().getString("RowsPerPage"));
	}
	public String FormatParse(String line, CommandSender sender, boolean simple){
		//TODO: Find a less hacky way to convert from the Legacy &xx format to the new JSON format
		//Also buttons still stop coloring in case of a new line
		String[] coloriser=line.split(" ");
		String lastcolor="";
		StringBuilder sb=new StringBuilder();
		String newline="";
		if (!simple){
			for (String word:coloriser){
				if (word.contains("&")){
					sb.append(word+" ");
					lastcolor=word.substring(0, 2);
				}else{
					word=lastcolor+word;
					sb.append(word+" ");
				}
			}
			newline=sb.toString();
		} else { newline=line; }
		newline = newline.replaceAll("&0", ChatColor.BLACK + "");
        newline = newline.replaceAll("&1", ChatColor.DARK_BLUE + "");
        newline = newline.replaceAll("&2", ChatColor.DARK_GREEN + "");
        newline = newline.replaceAll("&3", ChatColor.DARK_AQUA + "");
        newline = newline.replaceAll("&4", ChatColor.DARK_RED + "");
        newline = newline.replaceAll("&5", ChatColor.DARK_PURPLE + "");
        newline = newline.replaceAll("&6", ChatColor.GOLD + "");
        newline = newline.replaceAll("&7", ChatColor.GRAY + "");
        newline = newline.replaceAll("&8", ChatColor.DARK_GRAY+ "");
        newline = newline.replaceAll("&9", ChatColor.BLUE + "");
        newline = newline.replaceAll("&a", ChatColor.GREEN + "");
        newline = newline.replaceAll("&b", ChatColor.AQUA + "");
        newline = newline.replaceAll("&c", ChatColor.RED + "");
        newline = newline.replaceAll("&d", ChatColor.LIGHT_PURPLE + "");
        newline = newline.replaceAll("&e", ChatColor.YELLOW + "");
        newline = newline.replaceAll("&f", ChatColor.WHITE + "");
        newline = newline.replaceAll("&k", ChatColor.MAGIC + "");
        newline = newline.replaceAll("&l", ChatColor.BOLD + "");
        newline = newline.replaceAll("&o", ChatColor.ITALIC + "");
        newline = newline.replaceAll("&n", ChatColor.UNDERLINE + "");
        newline = newline.replaceAll("&m", ChatColor.STRIKETHROUGH + "");
        newline = newline.replaceAll("&r", ChatColor.RESET + "");
        if (sender!=null){
        	newline = newline.replaceAll("\\{PLAYER\\}", sender.getName());
        }
        return newline;
	}
	public void FileParser(String file,CommandSender sender, int page){
		try{
			FileReader f = new FileReader(getDataFolder()+"/HelpFiles/"+file+".txt");
			BufferedReader in=new BufferedReader(f);
			ArrayList<String> Lines=new ArrayList<String>();
			String line=in.readLine();
			FancyMessage msg=new FancyMessage();
			while (line!=null){
				Lines.add(line);
				line=in.readLine();
			}
			in.close();
			msg.text(FormatParse(divider,null,true));
			msg.send(sender);
			msg=new FancyMessage();
			msg.text(ChatColor.GOLD+"Helpfile: "+file+" Page " +page);
			msg.send(sender);
			msg=new FancyMessage();
			msg.text(FormatParse(divider,null,true));
			msg.send(sender);
			msg=new FancyMessage();
			for (int i=(int) ((page-1)*rowsperpage);i<(int) (Math.min(page*rowsperpage,Lines.size()));i++){
				msg=Parse(Lines.get(i),sender);
				msg.send(sender);
			}
			msg=new FancyMessage();
			msg.text(FormatParse(divider,null,true));
			msg.send(sender);
			msg=new FancyMessage();
			if (Lines.size()>rowsperpage){
				if (page==1){
					msg.text("         ").then(ChatColor.GREEN+"[Next]").command("/fluidhelp " + file + " "+ (page+1)).tooltip("Go To Next Page");
				}else{
					if (page>=Math.ceil(Lines.size()/rowsperpage)){
						msg.text(ChatColor.RED+"[Previous]").command("/fluidhelp " + file + " " + (page-1)).tooltip("Go To Previous Page");
					}else{
						msg.text(ChatColor.RED+"[Previous]").command("/fluidhelp " + file + " " + (page-1)).tooltip("Go To Previous Page").then("   ").then(ChatColor.GREEN+"[Next]").command("/fluidhelp " + file + " "+ (page+1)).tooltip("Go To Next Page");
					}
				}
				msg.send(sender);
				msg=new FancyMessage();
				msg.text(FormatParse(divider,null,true));
				msg.send(sender);
			}
		}catch (java.io.IOException e){
			sender.sendMessage("Chapter not found");
		}
	}
	public FancyMessage Parse(String text,CommandSender sender){
		FancyMessage msg = new FancyMessage();
		Pattern pt=Pattern.compile("\\`[^\\`]+\\`");
		Matcher m=pt.matcher(text);
		int start=0;
		int end=0;
		int oldend=-1;
		if(!(m.find())){
			msg.text(FormatParse(text,sender,false));
		}else{
			start=m.start();
			end=m.end();
			msg.text(FormatParse(text.substring(0,Math.max(start-1, 0)),sender,false));
			do{
				start=m.start();
				end=m.end();
				if (oldend!=-1){
					msg.then(FormatParse(text.substring(oldend,start-1),sender,false));
				}
				String toreplace=text.substring(start+1, end-1);
				String[] Options=toreplace.split("\\|");
				msg.then(FormatParse(Options[0],sender,true));
				if (!(Options[1].equals(""))){
						if (Options[1].equals("run_command")){
							msg.command(Options[3]);
						}else{
							if (Options[1].equals("suggest_command")){
								msg.suggest(Options[3]);
							}
						}
				}
				if (!(Options[2].equals(""))){
					if (Options[2].equals("show_text")){
						msg.tooltip(Options[4]);
					} else if (Options[2].equals("show_item")){
						File customfile = new File(getDataFolder(),"items.yml");
						FileConfiguration customyml=YamlConfiguration.loadConfiguration(customfile);
						ItemStack i=customyml.getItemStack(Options[4]);
						msg.itemTooltip(i);
					}
				}
				oldend=end;
			}while(m.find());
			if (text.length()!=0){
				msg.then(FormatParse(text.substring(Math.max(end-1, text.length()-1),Math.max(0, text.length()-1)),sender,false));
			}
		}
		return msg;
	}
}