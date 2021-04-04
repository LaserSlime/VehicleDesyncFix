package de.laserslime.boatdesyncfix.util;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.laserslime.boatdesyncfix.main.Main;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.server.v1_16_R3.PacketPlayInFlying.PacketPlayInPosition;
import net.minecraft.server.v1_16_R3.PacketPlayInFlying.PacketPlayInPositionLook;
import net.minecraft.server.v1_16_R3.PacketPlayOutMount;

public class PacketHandler extends ChannelDuplexHandler {

	private final Player player;
	private int preVL = 0;

	public PacketHandler(Player player) {
		this.player = player;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof PacketPlayInPosition || msg instanceof PacketPlayInPositionLook) {
			if(((CraftPlayer) player).getHandle().getVehicle() != null) {
				if(preVL >= ((CraftPlayer) player).getHandle().ping * 0.1) { // This is to prevent potential "false positives" for very
																				// laggy players entering a boat while moving. They
																				// shouldn't cause any issues, but this is just to be sure.
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutMount(((CraftEntity) player.getVehicle()).getHandle()));
					Main.getPlugin(Main.class).log("Attempted to resyncronize player " + player.getName() + " because of wrong packets while in a vehicle.");
					preVL = 0; // Reset preVL to prevent spam
				} else
					preVL++;
			} else
				preVL = 0;
		}
		super.channelRead(ctx, msg);
	}
}
