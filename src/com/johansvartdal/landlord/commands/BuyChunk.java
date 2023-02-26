package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.Bank;
import com.johansvartdal.landlord.ChunkBuilder;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.Directional;
import org.bukkit.util.Vector;

public class BuyChunk implements CommandExecutor {


	private final Main plugin;
	int chunkPrice = 17000;

	public BuyChunk(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("buychunk").setExecutor(this);;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Make sure command can only be executed from a player in a game
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can execute this command");
			return true;
		}
		Player player = (Player) sender;

		// Make sure player can afford with ChunkPoints
		if (!Main.playerDataManager.getPlayerData(player).hasChunkPoints()) {
			sender.sendMessage("You don't have any chunk points left");
			return true;
		}

		// Make sure player can afford it
		if (!Bank.playerCanAfford(player, chunkPrice)) {
			sender.sendMessage("You need " + chunkPrice + "kr to purchase a new chunk");
			return true;
		}

		// Make sure we are not in an event
		if (Main.properties.gameStateIsNormal()) {
			sender.sendMessage("You are not allowed to run this command at the moment");
			return true;
		}

		// Get direction of chunk
		String direction = getPlayerFacingDirection(player);
		Chunk chunkAtDirection = getChunkAtDirection(player, direction);

		// Make sure the chunk is available for purchase
		if (!ChunkBuilder.chunkIsAvailableForPurchaseBy(player, chunkAtDirection)) {
			sender.sendMessage("You cannot purchase this chunk");
			return true;
		}

		// Withdraw player
		Bank.withdrawPlayer(player, chunkPrice);

		// Unlock chunk
		ChunkBuilder.unlockDirection(player, direction);
		return true;
	}

	public String getPlayerFacingDirection(Player player) {
		int yaw = (int) player.getLocation().getYaw();

		// normalize the angle to be between 0 and 359
		yaw = yaw % 360;
		if (yaw < 0) {
			yaw += 360;
		}

		// determine the direction based on the angle
		if (yaw >= 45 && yaw < 135) {
			return "west"; // west
		} else if (yaw >= 135 && yaw < 225) {
			return "north";  // north
		} else if (yaw >= 225 && yaw < 315) {
			return "east";  // east
		} else {
			return "south";  // south
		}
	}

	public Chunk getChunkAtDirection(Player player, String direction) {
		Chunk currentChunk = player.getLocation().getChunk();
		int chunkX = currentChunk.getX();
		int chunkZ = currentChunk.getZ();
		switch (direction) {
			case "north":
				chunkZ -= 1;
				break;
			case "south":
				chunkZ += 1;
				break;
			case "west":
				chunkX -= 1;
				break;
			case "east":
				chunkX += 1;
				break;
		}
		return player.getWorld().getChunkAt(chunkX, chunkZ);
	}
}
