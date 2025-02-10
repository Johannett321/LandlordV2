package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BuyChunk implements CommandExecutor {


	private final Main plugin;

	public BuyChunk(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("buychunk").setExecutor(this);;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (Tools.stateNotNormal(sender)) {
			Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
			return true;
		}

		Player player = (Player) sender;
		int baseChunkPrice = Main.playerDataManager.getPlayerData(player).getChunkPurchasePrice();
		double discountMultiplier = 1 - Main.properties.getChunkDiscountPercentPoint();

		int finalChunkPrice = (int) (baseChunkPrice * discountMultiplier);


		// check if player is flying
		if (PlayerEventManager.playerIsInFlyingEvent(player)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.END_FLIGHT_FIRST));
			return true;
		}

		if (args.length == 0) {
			Tools.printMenuHeader(player, "Commands");
			Tools.printMenuOption(player, "/buychunk", "now");
			Tools.printMenuOption(player, "/buychunk", "info");
			return true;
		}

		// INFO ABOUT CHUNKS
		if (args[0].equals("info")) {
			Tools.printMenuHeader(player, LangDict.getString("chunks.chunkInfo"));
			Tools.printMenuOption(player, LangDict.getString("chunks.chunkPoints"), String.valueOf(Main.playerDataManager.getPlayerData(player).getChunkPoints()));
			Tools.printMenuOption(player, LangDict.getString("chunks.priceOfNextChunk"), Tools.formatCurrency(finalChunkPrice) + LangDict.getString("banking.plusTax"));
			Tools.printMenuOption(player, LangDict.getString("chunks.currentlyOwned"), Main.playerDataManager.getPlayerData(player).getOwnedChunks().size() + " chunks");
			return true;
		}

		// make sure the player includes 'now' keyword to confirm
		if (!args[0].equals("now")) {
			return false;
		}

		/*
		----------------------------------- START VALIDATIONS -----------------------------------
		 */

		// Make sure the player actually owns the chunk we are currently in
		if (!Main.playerDataManager.getPlayerData(player).ownsChunk(player.getLocation().getChunk())) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
			return true;
		}

		// Make sure player can afford with ChunkPoints
		if (!Main.playerDataManager.getPlayerData(player).hasChunkPoints()) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("chunks.noChunkPointsLeft"), ChatColor.RED);
			return true;
		}

		// Make sure player can afford it
		if (!Bank.playerCanAfford(player, finalChunkPrice)) {
			Bank.tellPlayerTheyNeed(player, finalChunkPrice, LangDict.getString("chunks.toPurchaseAChunk"));
			return true;
		}

		// Get direction of chunk
		String direction = getPlayerFacingDirection(player);
		Chunk chunkAtDirection = getChunkAtDirection(player, direction);

		// Make sure the chunk is available for purchase
		if (!ChunkBuilder.chunkIsAvailableForPurchaseBy(player, chunkAtDirection)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("chunks.cannotPurchaseThisChunk"), ChatColor.RED);
			return true;
		}

		/*
		----------------------------------- END VALIDATIONS -----------------------------------
		 */

		// Withdraw player
		Bank.withdrawPlayer(LangDict.getString("chunks.aChunk"), player, finalChunkPrice);  // money
		Main.playerDataManager.getPlayerData(player).withdrawChunkPoint();  // chunk points

		// Unlock chunk using the animation
		playEffectAndUnlock(player, direction);

		// Inform everyone
		Tools.broadcastMessage(player.getDisplayName() + LangDict.getString("chunks.justBoughtAChunk"), new Player[]{player});
		return true;
	}

	private void playEffectAndUnlock(Player player, String direction) {
		// levitation effect
		PotionEffect levitationFast = new PotionEffect(PotionEffectType.LEVITATION, (int) Tools.secToTicks(20), 3);
		player.addPotionEffect(levitationFast);

		// 6 sec
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			// levitation
			player.removePotionEffect(PotionEffectType.LEVITATION);
			PotionEffect levitationSlow = new PotionEffect(PotionEffectType.LEVITATION, (int) Tools.secToTicks(9), 1);
			player.addPotionEffect(levitationSlow);

			// 3 sec
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				// get chunk
				Chunk chunkAtDirection = getChunkAtDirection(player, direction);

				// Unlock chunk
				ChunkBuilder.unlockDirection(player, direction);

				// play anim and sound
				SpecialEffects.playChunkUnlockAnim(chunkAtDirection, (int) player.getLocation().getY());

				// play sounds
				player.playSound(player, Sound.ITEM_TOTEM_USE, 1, 0);
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() {
						player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 0);
					}
				}, Tools.secToTicks(1));
			}, Tools.secToTicks(2));

			// 8 sec -> slow falling
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				player.removePotionEffect(PotionEffectType.LEVITATION);
				PotionEffect slowFalling = new PotionEffect(PotionEffectType.SLOW_FALLING, (int) Tools.secToTicks(6), 3);
				player.addPotionEffect(slowFalling);
			}, Tools.secToTicks(3));

		}, Tools.secToTicks(5));
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
