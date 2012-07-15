package x0r0n.heavyhacker.hungergames;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;
import org.bukkit.scheduler.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.awt.Event;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Hungergames extends JavaPlugin {
	private boolean isRunning=false;
	private GameCountDown gameCountDown;
	public List<CapsuleInformation> capsuleInformation=new ArrayList<CapsuleInformation>();
	private HashMap<String,Integer> spectator = new HashMap<String,Integer> ();
	public BukkitScheduler s;
	public ChestManager chestMgr;
	
	// Config declaration
	File configFile;
	FileConfiguration config;

	public void onEnable() {
		// Disabling map saving
		getServer().getWorlds().get(0).setAutoSave(false);
		
		// Kill all Mobs
		Butcher();

		// Load Scheduler for creating Multiple Thread Tasks
		s=getServer().getScheduler();

		// Try to load config file
		configFile = new File(getDataFolder(), "config.yml");
		try {
			firstRun();
	    } catch (Exception e) {}
		config = new YamlConfiguration();
		loadYamls();

		// Create Player Objects (Capsule Slots) *24
		for(int i=0; i<24; ++i) {
			capsuleInformation.add(new CapsuleInformation(this,i));
		}
		
		// Init Chestmanager
		chestMgr = new ChestManager(this);

		// Register tasks
		s.scheduleSyncRepeatingTask(this, new PlayerExpell(this), 0, 40);
		s.scheduleSyncRepeatingTask(this, new ForceField(this), 0, 30);

		// Load countdownlength from config
		gameCountDown=new GameCountDown(this,config.getInt("settings.countDownLength",30));

		// Register eventlisteners
		getServer().getPluginManager().registerEvents(new LoginEventListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
		getServer().getPluginManager().registerEvents(new BlockEventListener(this), this);
		getServer().getPluginManager().registerEvents(new EntityEventListener(this), this);

	}

	// Checks if config exists + imports it using copy()
	private void firstRun() throws Exception {
	    if(!configFile.exists()){
	        configFile.getParentFile().mkdirs();
	        copy(getResource("config.yml"), configFile);
	    }
	}

	// called by firstRun() to fetch config
	private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


	// Call to reload config
	public void loadYamls() {
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	// Call to save config
	public void saveYamls() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void onDisable() {
		getLogger().info("Stopping HungerGames Plugin");
	}

	// Register commands
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if ((sender instanceof Player)) {
		    Player player = (Player) sender;
		    if(cmd.getName().equalsIgnoreCase("hungergames")) {
		    	if(args.length==0) {
		    		sender.sendMessage(ChatColor.RED+"+--------------------------------------------+");
		    		sender.sendMessage(ChatColor.RED+"+ Command Syntax: "+ChatColor.YELLOW+"/hungergames <parameter>");
		    		sender.sendMessage(ChatColor.RED+"+ Parameters are:");
		    		sender.sendMessage(ChatColor.RED+"+ "+ChatColor.YELLOW+"info"+ChatColor.RED+" - Shows the Plugin Info");
		    		sender.sendMessage(ChatColor.RED+"+ "+ChatColor.YELLOW+"spectator"+ChatColor.RED+" - Toggle spectator mode");
		    		if (player.isOp()) {
		    			sender.sendMessage(ChatColor.RED+"+ OP Parameters:");
		    			sender.sendMessage(ChatColor.RED+"+ "+ChatColor.YELLOW+"start"+ChatColor.RED+" - Begins hungergames");
		    			sender.sendMessage(ChatColor.RED+"+ "+ChatColor.YELLOW+"stop"+ChatColor.RED+" - Ends hungergames");
		    		}
		    		sender.sendMessage(ChatColor.RED+"+--------------------------------------------+");
		    	} else {
		    		if(args[0].equalsIgnoreCase("start") && player.isOp()) {
		    			s.scheduleAsyncDelayedTask(this, gameCountDown, 0);
		    		} else if(args[0].equalsIgnoreCase("stop") && player.isOp()) {
		    			StopGame();
		    		} else if(args[0].equalsIgnoreCase("save") && player.isOp()) {
		    			SaveCoords();
		    		} else if(args[0].equalsIgnoreCase("info")) {
		    			sender.sendMessage("You are running the Hungergames Plugin v1.0 (ALPHA) by X0R0N (eddy123) and HeavyHacker (HolyWater) Yaay!");		    			
		    		} else if(args[0].equalsIgnoreCase("spectator")) {
		    			if (ToggleSpectator(((Player) sender).getName())) {
		    				sender.sendMessage("You are spectator now!");	
		    			} else {
		    				sender.sendMessage("You are no more spectator!");
		    			}
		    		}
		    	}
				return true;
			} else {
				return true;
			}
	    } else {
	        sender.sendMessage(ChatColor.RED + "This command if for players only!");
	        return false;
	    }
	}

	private void SaveCoords() {
		/*
		Vector v=p.getLocation().toVector().subtract(p.getWorld().getSpawnLocation().toVector());
		v.setX(Math.round(v.getX()));
		v.setY(Math.round(v.getY()));
		v.setZ(Math.round(v.getZ()));
		config.set("spawnchest."+string, v);
		saveYamls();*/
		List<ItemStack> homolist=new ArrayList<ItemStack>();
		homolist.add(new ItemStack(Material.BLAZE_POWDER,14));
		homolist.add(new ItemStack(Material.COMPASS,4));
		config.set("fap", homolist);
		saveYamls();
	}

	public void StartGame() {
		// Launch players and start hungergames
		getServer().broadcastMessage(ChatColor.RED + "Starting Hungergames!");
		for(CapsuleInformation c: capsuleInformation) {
			if(c.GetPlayer()!=null) {
				Launch(c.GetPlayer(), 1.9);
			}
		}
		getServer().getWorlds().get(0).setTime(0);
		chestMgr.FillSpawnChests();
		isRunning=true;
	}

	public void StopGame() {
		// Stop game in progress and reset capsules
		getServer().broadcastMessage(ChatColor.RED + "Stopping Hungergames!");
		capsuleInformation.clear();
		loadYamls();
		for(int i=0; i<24; ++i) {
			capsuleInformation.add(new CapsuleInformation(this,i));
		}
		isRunning=false;
		spectator.clear();
	}

	public boolean IsRunning() {
		return isRunning;
	}	

	public void die(Player p) {
		p.setHealth(2);
		p.damage(1);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
		for(CapsuleInformation c: capsuleInformation) {
			if(c.GetPlayer()==null) {continue;}
			if(c.GetPlayer().getEntityId()==p.getEntityId()) {
				c.kick();
				c.UpdateSigns();
				break;
			} else {
				c.GetPlayer().getWorld().createExplosion(c.GetPlayer().getLocation().add(0, -15, 0), 0);
			}
		}
		p.getWorld().createExplosion(p.getWorld().getSpawnLocation().add(0, -15, 0), 0);
		for(ItemStack i: p.getInventory().getContents()) {
			if(i==null) {continue;}
			p.getWorld().dropItem(p.getPlayer().getLocation(),i);
		}
		p.getInventory().clear();		
		p.teleport(p.getWorld().getSpawnLocation());
		p.setHealth(20);
		p.setFoodLevel(20);
		p.getServer().broadcastMessage(p.getDisplayName()+" died and lost the hungergames!");
	}

	public void Launch(Player p, double distance) {
		p.setVelocity(new Vector(0,distance,0));
	}
	
	public boolean ToggleSpectator(String name) {
		
		if (spectator.get(name)!=null) {
			if (spectator.get(name)==0) {
				spectator.put(name,1);
				return true;
			} else {
				spectator.put(name,0);
				return false;
			}
		}
		spectator.put(name,1);
		return true;
	}
	
	public boolean IsSpectator(Player p) {
		if(p==null) { return false; }
		if(spectator.get(p.getName())==null) { return false; }
		if(spectator.get(p.getName())!=1) { return false; }
		return true;
	}
	
	public void Butcher() {
		for(Entity e:getServer().getWorlds().get(0).getEntities()) {
			if(e.getType()==EntityType.OCELOT || e.getType()==EntityType.SHEEP || e.getType()==EntityType.SQUID || e.getType()==EntityType.SLIME) {
				//Ocelot bitches
				e.remove();
			}
		}
	}
}