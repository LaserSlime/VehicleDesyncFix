package de.laserslime.boatdesyncfix.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import de.laserslime.boatdesyncfix.main.Main;

public class PlayerInteractListener implements Listener {

	@EventHandler
	public void handlePlayerInteract(PlayerInteractEntityEvent e) {
		if(e.getRightClicked().getType() != EntityType.BOAT || e.getPlayer().getVehicle() == null || e.getPlayer().getVehicle().getType() != EntityType.BOAT) return;
		if(e.getRightClicked().getEntityId() == e.getPlayer().getVehicle().getEntityId()) {
			Main.getPlugin(Main.class).log("Prevented player " + e.getPlayer().getName() + " from entering a boat while they were already in the boat.");
			e.setCancelled(true);
		}
	}
}
