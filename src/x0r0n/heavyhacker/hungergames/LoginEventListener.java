package x0r0n.heavyhacker.hungergames;

import java.io.IOException;

import jnbt.DataException;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class LoginEventListener implements Listener {
	private Hungergames hunger;
	
	LoginEventListener(Hungergames g) {
		hunger=g;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (hunger.IsRunning()) {
			for(CapsuleInformation c: hunger.capsuleInformation) {
				if(c.GetPlayer()==null) { continue; }
				if((event.getPlayer().getName().equalsIgnoreCase(c.GetPlayer().getName()))) {
					c.SetPlayer(event.getPlayer());
					c.AddJoinAttempt();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		for(CapsuleInformation c: hunger.capsuleInformation) {
			if(c.GetPlayer()==null) {continue;}
			if(c.GetPlayer().getName().equalsIgnoreCase(event.getPlayer().getName())) {
				c.SetQuitTime();
				c.UpdateSigns();
				break;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.getPlayer().sendMessage(ChatColor.GOLD+"Welcome, "+ event.getPlayer().getDisplayName() + " to the Hunger Games!");
		event.getPlayer().sendMessage(ChatColor.GOLD+"Type /hungergames to see your commands!");
		
		if(!hunger.IsRunning())
			return;
		
		for(CapsuleInformation c: hunger.capsuleInformation) {
			if(c.GetPlayer()==null)
				continue;
			
			if(c.GetPlayer().getName().equalsIgnoreCase(event.getPlayer().getName()) && c.IsPlaying()) {
				return;
			}
		}
		
		event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
	}
}
