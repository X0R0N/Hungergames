package x0r0n.heavyhacker.hungergames;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerExpell implements java.lang.Runnable {
	private Hungergames hunger;
	private int OfflineTimeAllowed;
	private int JoinAttemptsAllowed;
	PlayerExpell(Hungergames g) {
		hunger=g;
		
		OfflineTimeAllowed=hunger.config.getInt("playerquit.OfflineTimeAllowed",20);
		JoinAttemptsAllowed=hunger.config.getInt("playerquit.JoinAttemptsAllowed",3);
	}
	
	public void run() {	
		if(!hunger.IsRunning())
			return;
		
		long x=System.currentTimeMillis() / 1000;
			
		int count=0;
		
		for(CapsuleInformation c: hunger.capsuleInformation) {
			if(c.GetPlayer()==null) {
				continue;
			}
				
			if(!c.IsPlaying()) {
				continue;
			}
			
			if(((x-c.GetQuitTime()>OfflineTimeAllowed) && !c.GetPlayer().isOnline()) || c.GetJoinAttempts()>JoinAttemptsAllowed) {
				hunger.getServer().broadcastMessage(c.GetPlayer().getDisplayName()+" got expelled due retreat!");
				
				for(Player p: c.GetPlayer().getWorld().getPlayers()) {
					for(CapsuleInformation c2: hunger.capsuleInformation) {
						if (p.getEntityId() != c.GetPlayer().getEntityId()) {
							if (c2.GetPlayer()!=null) {
								if(c2.GetPlayer().getName().equalsIgnoreCase(p.getName())) {
									p.getWorld().createExplosion(p.getLocation().add(0, -15, 0), 0);
								}
							}
						}
					}
				}
				c.GetPlayer().getWorld().createExplosion(c.GetPlayer().getWorld().getSpawnLocation().add(0, -15, 0), 0);
				
				c.kick();
				c.UpdateSigns();
				if(c.GetPlayer().isOnline() && !c.GetPlayer().isDead()) {
					c.GetPlayer().teleport(c.GetPlayer().getWorld().getSpawnLocation());
				}
			} else {
				++count;
			}
		}
		
		if(count==0) {
			hunger.getServer().broadcastMessage(ChatColor.GREEN+"Hungergames ended with a draw!");
			hunger.StopGame();
		}
		if(count==1) {
			for(CapsuleInformation c: hunger.capsuleInformation) {
				if(c.GetPlayer()==null || !c.IsPlaying())
					continue;
				c.GetPlayer().teleport(c.GetPlayer().getWorld().getSpawnLocation());
				hunger.getServer().broadcastMessage(ChatColor.GREEN+c.GetPlayer().getDisplayName()+" won the Hungergames!");
				hunger.StopGame();
				break;
			}
		} else if(count==2) {
			// check wegen fapper
		}
	}
}
