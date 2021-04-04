package de.laserslime.boatdesyncfix.listeners;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.laserslime.boatdesyncfix.util.PacketHandler;
import io.netty.channel.ChannelHandler;

public class PacketInjectorListener implements Listener {

	Map<UUID, PacketHandler> packetHandlers = new HashMap<>();

	@EventHandler
	public void handlePlayerJoin(PlayerJoinEvent event) {
		add(event.getPlayer());
	}

	@EventHandler
	public void handlePlayerQuit(PlayerQuitEvent event) {
		remove(event.getPlayer());
	}

	private void add(Player player) {
		packetHandlers.put(player.getUniqueId(), new PacketHandler(player));
		// Using reflections to prepare for future multi version support
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
			Object channel = networkManager.getClass().getField("channel").get(networkManager);
			Object pipeline = channel.getClass().getMethod("pipeline").invoke(channel);
			pipeline.getClass().getDeclaredMethod("addBefore", String.class, String.class, ChannelHandler.class).invoke(pipeline, "packet_handler", player.getName(),
					packetHandlers.get(player.getUniqueId()));
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException ex) {
			ex.printStackTrace();
		}
	}

	private void remove(Player player) {
		// Removing packet handler doesn't seem to be possible with reflections
//		try {
//			Object handle = player.getClass().getMethod("getHandle").invoke(player);
//			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
//			Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
//			Object channel = networkManager.getClass().getField("channel").get(networkManager);
//			Object pipeline = channel.getClass().getMethod("pipeline").invoke(channel);
//			pipeline.getClass().getMethod("remove", String.class).invoke(pipeline, "packet_handler");
//		} catch(NoSuchFieldException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
//			ex.printStackTrace();
//		}
		packetHandlers.remove(player.getUniqueId());
	}
}