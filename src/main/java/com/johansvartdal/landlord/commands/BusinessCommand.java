package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.Bank;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.levels.LevelManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BusinessCommand implements CommandExecutor {

	private final Main plugin;

	public BusinessCommand(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("business").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can only be executed by players");
			return true;
		}

		Player player = (Player) sender;
		if (!plugin.properties.gameHasStarted()) {
			Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("commandResponses.errorMessages.cmdNotNow"), ChatColor.RED);
			return true;
		}

		if (!LevelManager.featureUnlocked("business")) {
			Tools.tellPlayer(new ErrorChat(), player,LangDict.getString(LangDict.CMD_NOT_UNLOCKED));
			return true;
		}

		return Main.businessManager.handleCommand(player, args);
	}
}
