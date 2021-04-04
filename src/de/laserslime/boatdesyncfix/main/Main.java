package de.laserslime.boatdesyncfix.main;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import de.laserslime.boatdesyncfix.listeners.PacketInjectorListener;
import de.laserslime.boatdesyncfix.listeners.PlayerInteractListener;

public class Main extends JavaPlugin {

	private String nmsVersion;
	private boolean log;

	@Override
	public void onLoad() {
		getLogger().info("Initializing...");
		nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}

	@Override
	public void onEnable() {
		loadConfig();
		registerListeners();
		getLogger().info("Done!");
	}

	private void loadConfig() {
		getLogger().info("Loading config...");
		saveDefaultConfig();
		log = getConfig().getBoolean("log", true);
	}

	private void registerListeners() {
		getLogger().info("Registering listeners...");
		if(getConfig().getBoolean("prevent-doubleclick-boat-desync", true)) Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
		if(getConfig().getBoolean("packet-check", false)) {
			if(nmsVersion.equals("v1_16_R3"))
				Bukkit.getPluginManager().registerEvents(new PacketInjectorListener(), this);
			else
				getLogger().info("Failed to enable packet injector: You are using an unsupported server version!");
		}
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
	}

	public void log(String message) {
		if(log) getLogger().info(message);
	}
}
