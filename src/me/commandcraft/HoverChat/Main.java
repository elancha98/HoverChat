package me.commandcraft.HoverChat;

import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	public Permission permission = null;
	public final static Logger logger = Logger.getLogger("Minecraft");

	public void onEnable() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(this, this);
	}

	public void onDisable() {

	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		event.setCancelled(true);
		String message = event.getMessage();
		String json = "{text:";
		try {
			json += p.getScoreboard().getTeam(p.getName()).getPrefix();
		} catch (Exception e){}
		json += "\"[";
		json += p.getName();
		json += "] ";
		json += message;
		json += "\",hoverEvent:{action:show_text,value:\"";
		try {
			json += permission.getPrimaryGroup(p);
		} catch (Exception e) {
			try {
				json += p.getScoreboard().getTeam(p.getName()).getDisplayName();
			} catch (Exception ex) {}
		}
		json += "\"}}";
		sendAllJsonMessage(json);
	}

	// code from Gamecube762
	public static PacketPlayOutChat createPacketPlayOutChat(String s) {
		return new PacketPlayOutChat(ChatSerializer.a(s));
	}

	public static void sendAllJsonMessage(String s) {
		PacketPlayOutChat a = createPacketPlayOutChat(s);
		for (Player p : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(a);
		}
	}
}
