package x0r0n.heavyhacker.hungergames;
import java.io.File;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.ChatColor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jnbt.DataException;


public class CapsuleInformation {
	private Vector vec;
	private int index;
	private Hungergames hunger;
	private Player player;
	private long leaveTime;
	private int joinAttempts;
	private boolean isPlaying;

	CapsuleInformation(Hungergames g, int i) {
		hunger=g;
		index=i;
		player=null;

		vec=g.config.getVector("capsules."+(i+1),new Vector(0,0,0));
		UpdateSigns();
	}

	public Vector GetVector() {
		return vec;
	}

	public boolean InsertPlayer(Player p){
		if(hunger.IsSpectator(p)) {
			return false;
		}
		player=p;
		p.getInventory().clear();
		p.getInventory().addItem(new ItemStack(Material.COMPASS,1));
		for(CapsuleInformation c: hunger.capsuleInformation) {
			if(c!=this && c.GetPlayer()==p) {
				player=null;
				return false;
			}
		}

		SchematicManager schm=new SchematicManager(hunger);
		try {
			isPlaying = true;

			schm.Load(new File(hunger.getDataFolder()+"/schematics", "capsule_closed.schematic"), new Location(p.getWorld(),vec.getX(),vec.getY(),vec.getZ()));

			Vector bv=new Vector(vec.getX(),vec.getY(),vec.getZ());
			bv.add(new Vector(0,4,0));


			Vector sv=new Vector(bv.getX(),bv.getY(),bv.getZ());
			sv.add(new Vector(2,0,0));
			Location loc=new Location(p.getWorld(),sv.getX(),sv.getY(),sv.getZ());
			WriteSign(loc);

			sv=new Vector(bv.getX(),bv.getY(),bv.getZ());
			sv.add(new Vector(-2,0,0));
			loc=new Location(p.getWorld(),sv.getX(),sv.getY(),sv.getZ());
			WriteSign(loc);

			sv=new Vector(bv.getX(),bv.getY(),bv.getZ());
			sv.add(new Vector(0,0,2));
			loc=new Location(p.getWorld(),sv.getX(),sv.getY(),sv.getZ());
			WriteSign(loc);

			sv=new Vector(bv.getX(),bv.getY(),bv.getZ());
			sv.add(new Vector(0,0,-2));
			loc=new Location(p.getWorld(),sv.getX(),sv.getY(),sv.getZ());
			WriteSign(loc);

			hunger.getServer().broadcastMessage(ChatColor.GOLD + p.getDisplayName()+ " joined District " + (GetIndex()/2+1)+"!");

			return true;
		} catch(DataException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void UpdateSigns() {
		Vector bv=new Vector(vec.getX(),vec.getY(),vec.getZ());
		bv.add(new Vector(0,4,0));

		Vector sv=new Vector(bv.getX(),bv.getY(),bv.getZ());
		sv.add(new Vector(2,0,0));
		Location loc = new Location(hunger.getServer().getWorlds().get(0),sv.getX(),sv.getY(),sv.getZ());
		WriteSign(loc);

		sv=new Vector(bv.getX(),bv.getY(),bv.getZ());
		sv.add(new Vector(-2,0,0));
		loc=new Location(hunger.getServer().getWorlds().get(0),sv.getX(),sv.getY(),sv.getZ());
		WriteSign(loc);

		sv=new Vector(bv.getX(),bv.getY(),bv.getZ());
		sv.add(new Vector(0,0,2));
		loc=new Location(hunger.getServer().getWorlds().get(0),sv.getX(),sv.getY(),sv.getZ());
		WriteSign(loc);

		sv=new Vector(bv.getX(),bv.getY(),bv.getZ());
		sv.add(new Vector(0,0,-2));
		loc=new Location(hunger.getServer().getWorlds().get(0),sv.getX(),sv.getY(),sv.getZ());
		WriteSign(loc);
	}

	private void WriteSign(Location loc) {
		Sign s=null;

		if(loc.getBlock().getType()==Material.WALL_SIGN) {
			s=(Sign)loc.getBlock().getState();
			if(player!=null) {
				ChatColor color=ChatColor.RED;
				if(IsPlaying()) {color=ChatColor.GREEN; }

				s.setLine(0, ChatColor.DARK_BLUE+"District "+(index/2+1));
				s.setLine(1, ChatColor.DARK_BLUE+"----------------");
				s.setLine(2, color+player.getDisplayName());

			} else {
				s.setLine(0, ChatColor.DARK_BLUE+"District "+(index/2+1));
				s.setLine(1, ChatColor.DARK_BLUE+"----------------");
				s.setLine(2, ChatColor.DARK_BLUE+"Empty Capsule");
			}
			s.update(true);
		} else {
			hunger.getLogger().info("[ERROR] Sign expected, found "+loc.getBlock().getType()+" at "+loc.getX()+","+loc.getZ()+","+loc.getY()+" ->"+index+"!");
		}
	}

	public Player GetPlayer() {
		return player;
	}

	public int GetIndex() {
		return index;
	}

	public void SetQuitTime() {
		leaveTime = System.currentTimeMillis() / 1000;  
	}
	public long GetQuitTime() {
		return leaveTime;
	}

	public void SetPlayer(Player p) {
		player=p;
	}

	public void AddJoinAttempt() {
		++joinAttempts;
	}

	public int GetJoinAttempts() {
		return joinAttempts;
	}

	public void kick() {
		isPlaying = false;
	}

	public boolean IsPlaying() {
		return isPlaying;
	}

}