package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChangeLanguage implements CommandExecutor {

	private Main plugin;

	public ChangeLanguage(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("changelang").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		if (!player.isOp()) {
			LangDict.getString(LangDict.YOU_ARE_NOT_ALLOWED);
			return true;
		}

		if (args.length != 1) {
			return false;
		}

		LangDict.attemptChangeLanguage(player, args[0]);
		return true;
	}
}
