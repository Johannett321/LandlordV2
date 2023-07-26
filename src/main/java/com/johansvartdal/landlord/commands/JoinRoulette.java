package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.RouletteGame;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.LevelManager;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinRoulette implements CommandExecutor {

    private Main plugin;

    public JoinRoulette(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("joinroulette").setExecutor(this);

        if (LevelManager.getCurrentDisplayLevelNum() <= 3) {
            new RouletteGame(plugin);
        }
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can only be executed by players");
            return true;
        }

        Player player = (Player) commandSender;

        // make sure the command has been unlocked
        if (!LevelManager.featureUnlocked("roulette")) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
            return true;
        }

        RouletteGame.addToGame(player);
        return true;
    }
}
