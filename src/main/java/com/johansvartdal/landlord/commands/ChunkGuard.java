package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ChunkGuardChat;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.levels.LevelManager;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChunkGuard implements CommandExecutor {

	private Main plugin;

	public ChunkGuard(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("chunkguard").setExecutor(this);;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (Tools.stateNotNormal(sender)) {
			Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
			return true;
		}

		Player player = (Player) sender;

		// Has feature been unlocked yet?
		if (!LevelManager.featureUnlocked("chunkguard")) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
			return true;
		}

		// make sure format is correct
		if (args.length < 1) {
			Tools.printMenuHeader(player, "CHUNKGUARD");
			Tools.printMenuOption(player, "/chunkguard", "protect");
			Tools.printMenuOption(player, "/chunkguard", "unprotect");
			Tools.printMenuOption(player, "/chunkguard", "refill <amount>");
			Tools.printMenuOption(player, "/chunkguard", "info");
			return true;
		}

		switch (args[0]) {
			case "info":
				printChunkGuardInfo(player);
				break;
			case "protect":
				protectChunk(player);
				break;
			case "unprotect":
				unprotectChunk(player);
				break;
			case "refill":
				if (args.length < 2) {
					return false;
				}

				int amount = Integer.parseInt(args[1]);
				refillPlayer(player, amount);
				break;
			default:
				return false;
		}


		return true;
	}

	private void printChunkGuardInfo(Player player) {
		boolean chunkIsProtectedByPlayer = Main.chunkGuardManager.isChunkProtectedByPlayer(player, player.getLocation().getChunk());
		boolean chunkIsCurrentlyProtected = Main.chunkGuardManager.isChunkCurrentlyProtected(player.getLocation().getChunk());

		String protectionStatus = "UNPROTECTED";
		if (chunkIsCurrentlyProtected && chunkIsProtectedByPlayer) {
			protectionStatus = "protected";
		}else if (chunkIsCurrentlyProtected) {
			protectionStatus = "unprotection pending...";
		}else if (chunkIsProtectedByPlayer) {
			protectionStatus = "protection pending...";
		}

		int singleChunkPrice = Main.chunkGuardManager.getChunkProtectionPrice(1);
		int numOfGuardedChunks = Main.chunkGuardManager.getNumOfChunksProtectedForPlayer(player);
		int amountPerHour = singleChunkPrice * numOfGuardedChunks;
		int currentBalance = Main.chunkGuardManager.getCurrentBalanceForPlayer(player);

		int remainingProtection = 0;
		if (currentBalance > 0 && numOfGuardedChunks > 0) {
			remainingProtection = currentBalance / (numOfGuardedChunks * singleChunkPrice);
		}

		if (chunkIsCurrentlyProtected) {
			remainingProtection += 1;
		}

		Tools.printMenuHeader(player, "CHUNKGUARD INFO");
		Tools.printMenuOption(player, "This chunk:", protectionStatus); // either UNPROTECTED or protected
		Tools.printMenuOption(player, "Protected chunks:", String.valueOf(numOfGuardedChunks));
		Tools.printMenuOption(player, "Price per chunk:", Tools.formatCurrency(singleChunkPrice) + LangDict.getString("generalSentenceParts.perHour")); // self explaining
		Tools.printMenuOption(player, "Current amount per hour:", Tools.formatCurrency(amountPerHour)); // current cost per hour
		Tools.printMenuOption(player, "Balance:", Tools.formatCurrency(currentBalance)); // how much money is set in
		Tools.printMenuOption(player, "Remaining protection:", (remainingProtection) + LangDict.getString("generalSentenceParts.hours")); // how many hours
	}

	private void protectChunk(Player player) {
		PlayerData playerData = Main.playerDataManager.getPlayerData(player);
		Chunk chunk = player.getLocation().getChunk();
		if (!playerData.ownsChunk(chunk)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("commandResponses.errorMessages.onlyWithinOwnChunk"));
			return;
		}

		Main.chunkGuardManager.startProtectingChunk(player, chunk);
		Tools.tellPlayer(new ChunkGuardChat(), player, LangDict.getString("chunkGuard.iWillProtect"));
	}

	private void unprotectChunk(Player player) {
		PlayerData playerData = Main.playerDataManager.getPlayerData(player);
		Chunk chunk = player.getLocation().getChunk();
		if (!playerData.ownsChunk(chunk)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("commandResponses.errorMessages.onlyWithinOwnChunk"));
			return;
		}

		Main.chunkGuardManager.stopWatchingChunk(Main.playerDataManager.getPlayerData(player), chunk);
		Tools.tellPlayer(new ChunkGuardChat(), player, LangDict.getString("chunkGuard.noLongerProtect"));
	}

	private void refillPlayer(Player player, int amount) {
		if (amount < 50) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("banking.amountTooLow") + Tools.formatCurrency(amount));
			return;
		}

		PlayerData playerData = Main.playerDataManager.getPlayerData(player);

		if (!Bank.playerCanAfford(playerData, amount)) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.YOU_CANNOT_AFFORD_) + "chunkguard");
			return;
		}

		Bank.withdrawPlayer("chunkguard", player, amount);
		Main.chunkGuardManager.depositPlayer(playerData, amount);
	}
}