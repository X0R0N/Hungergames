package x0r0n.heavyhacker.hungergames;

import java.lang.Runnable;
import java.lang.Thread;

import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class GameCountDown implements Runnable {
	private int countdown;
	private Hungergames hunger;

	GameCountDown(Hungergames g, int count) {
		hunger=g;
		countdown=count;
	}
	
	public void run() {
		int playersready = 0;
		for(Player p: hunger.getServer().getWorlds().get(0).getPlayers()) {
			if (!hunger.IsSpectator(p)) {
				++playersready;
			}
		}
		if (playersready<hunger.config.getInt("settings.MinPlayers",2)) {
			hunger.getServer().broadcastMessage(ChatColor.RED + "You need "+(hunger.config.getInt("settings.MinPlayers",2)-playersready)+" more player(s) to start Hungergames!");
			return;
		}

		JoinCapsules();
		
		for(CapsuleInformation c: hunger.capsuleInformation) {
			if(c.GetPlayer()==null) {continue;}
			
			Location l=new Location(c.GetPlayer().getWorld(),c.GetVector().getX()+0.5,c.GetVector().getY()+1,c.GetVector().getZ()+0.5);
			if(l.distance(c.GetPlayer().getLocation())>0.9) { 
				c.GetPlayer().teleport(l); 
			}
		}
		
		ChatColor c=ChatColor.DARK_GREEN;
		for(int i=countdown; i>0; --i) {
			try {
				if (i<4) { c = ChatColor.RED; } else if (i<7) { c = ChatColor.GOLD; }
				if  (i==30 || i==25 || i==20 || i==15 || i < 11) {
					hunger.getServer().broadcastMessage(c + "Starting Hungergames in " + i +" seconds!");
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		hunger.StartGame();
	}
	
	public void JoinCapsules() {
		for(CapsuleInformation c: hunger.capsuleInformation) {
			if (c.GetPlayer()!=null) { continue; }
			for(Player p: hunger.getServer().getWorlds().get(0).getPlayers()) {
				if (hunger.IsSpectator(p)) { continue; }
				boolean b=true;
				
				for(CapsuleInformation c2: hunger.capsuleInformation) {
					if(c2.GetPlayer()==null) {continue;}
					if(c2.GetPlayer().getName().equalsIgnoreCase(p.getName())) {
						b=false;
						break;
					}
				}
				
				if(!b) {continue;}
				
				c.InsertPlayer(p);
				p.teleport(new Location(p.getWorld(),c.GetVector().getX()+0.5,c.GetVector().getY()+1,c.GetVector().getZ()+0.5));
				break;
			}
		}
	}
}
