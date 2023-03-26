package com.johansvartdal.landlord;

import com.johansvartdal.landlord.commands.*;
import com.johansvartdal.landlord.lan.LanController;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	public static ScoreboardHelper scoreboardHelper;
	public static Properties properties;
	public static Configurator configurator;
	public static TradeCenter tradeCenter;
	public static PlayerDataManager playerDataManager;
	
	@Override
	public void onEnable() {
		SpecialEffects.setPlugin(this);
		Tools.init(this);
		properties = new Properties();
		properties.load();
		configurator = new Configurator(this);
		new CheatProtection(this);
		new SleepPercentage(this);
		configurator.configure();
		LangDict.loadLanguage();
		Bank.load();
		StockManager.loadStocks();
		ChunkBuilder.load();
		LevelManager.init(this);
		new EmissionTax(this);


		tradeCenter = new TradeCenter();
		playerDataManager = new PlayerDataManager(Bukkit.getWorlds().get(0), this);
		playerDataManager.loadData();
		scoreboardHelper = new ScoreboardHelper(this);

		// COMMANDS
		new Sell(this);
		new Landlord(this);
		new BuyChunk(this);
		new Day(this);
		new DailyBonus(this);
		new Bal(this);
		new Home(this);
		new SetHome(this);
		new Trade(this);
		new Donate(this);
		new Upgrade(this);
		new Stocks(this);
		new JoinRoulette(this);
		new Capture(this);
		new Wilderness(this);
		new Visit(this);
		new SendHome(this);
		new Adm(this);
		new SetTrade(this);
		new TreasuryCommand(this);

		Bank.startTaxCollector(this);

		getServer().getPluginManager().registerEvents(this, this);

		// Event
		LandlordEventManager.loadEventIfAny(this);

		// Only for LAN
		LanController.initiate();
	}

	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.getPlayer().getDisplayName().toLowerCase().equals("johannett321")) {
			event.setCancelled(true);
			for(Player player: Bukkit.getOnlinePlayers()) {
				player.sendMessage(ChatColor.DARK_GREEN + "[DEV] " + ChatColor.WHITE + "<Johannett321> " + event.getMessage());
			}
		}if (event.getPlayer().getDisplayName().toLowerCase().equals("karafo")) {
			event.setCancelled(true);
			for(Player player: Bukkit.getOnlinePlayers()) {
				player.sendMessage(ChatColor.GREEN + "[BUILDER]" + ChatColor.WHITE + " <Karafo> " + event.getMessage());
			}
		}else if (event.getPlayer().getDisplayName().toLowerCase().equals("ss")) {
			event.setCancelled(true);
			for(Player player: Bukkit.getOnlinePlayers()) {
				player.sendMessage(ChatColor.DARK_GREEN + "[" + LangDict.getString(LangDict.TREASURY_SENTINEL) + "] " + ChatColor.WHITE + event.getMessage());
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		// inform player about debug mode
		if (Properties.DEBUG_MODE) {
			Tools.tellPlayer(event.getPlayer(), "WARNING: DEBUG MODE ENABLED!", ChatColor.RED);
		}

		if (playerDataManager.playerExists(event.getPlayer())) {
			event.setJoinMessage(ChatColor.GREEN + "[INFO]" + ChatColor.GOLD + " A soldier by the name of " + ChatColor.DARK_AQUA + event.getPlayer().getDisplayName() + ChatColor.GOLD + " has made his return!");
			return;
		}

		// kick player if game already running
		if (properties.gameHasStarted()) {
			event.getPlayer().kickPlayer("You are not allowed to join. The game is already running");
			return;
		}

		// ------- ONLY RUN IF PLAYER JOINS FOR THE FIRST TIME --------

		// make sure number of players never exceeds max number
		if (playerDataManager.getPlayerDataList().size() >= StaticValues.MAX_PLAYERS) {
			event.getPlayer().kickPlayer("Sorry, the game is full");
			return;
		}

		// create the playerdata file
		PlayerData playerData = new PlayerData(event.getPlayer().getWorld(), event.getPlayer());
		playerDataManager.addNewPlayer(playerData);
		event.setJoinMessage(ChatColor.DARK_PURPLE + LangDict.getString("god") + ChatColor.WHITE + LangDict.getString("newCitizen") + event.getPlayer().getDisplayName());

		// teleport to start location
		Bukkit.getScheduler().runTaskLater(this, ()-> {
			event.getPlayer().teleport(StaticValues.GAME_START_LOCATION);
		}, Tools.secToTicks(1));


		// inform OP about commands
		if (event.getPlayer().isOp()) {
			Tools.tellPlayer(event.getPlayer(), "When everyone has joined, run the command '/landlord config' to find a suitable location to start the game");
		}

		// give player playguide
		givePlayGuide(event.getPlayer());
	}

	private void givePlayGuide(Player player) {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		BookMeta meta = (BookMeta) book.getItemMeta();
		meta.setTitle(ChatColor.translateAlternateColorCodes('&', "Playguide"));
		meta.setAuthor(ChatColor.translateAlternateColorCodes('&', "The Landlord"));
		meta.addPage(ChatColor.translateAlternateColorCodes('&', "&2Welcome to Landlord!&0 \nType '&3/help landlord&0' for a list of commands."));
		meta.addPage(ChatColor.translateAlternateColorCodes('&', "&2How to play - preparations&0 \nOnce the admin initiates the game, you will have 5 minutes to collect all the items you want to bring into your chunk. The chat will tell you what items you are required to collect."));
		meta.addPage(ChatColor.translateAlternateColorCodes('&', "Once five minutes have past, you will be teleported into your 16x16 block chunk. At a later level, you will be able to get out of your chunk for 5 minutes at a time, by using the command '&3/wilderness&0'"));
		meta.addPage(ChatColor.translateAlternateColorCodes('&', "&2How to play - Setting home&0 \nInside your chunk, you can perform the command '&3/sethome&0', to change the home location in your chunk. You can then teleport to that location by typing '&3/home&0'"));
		meta.addPage(ChatColor.translateAlternateColorCodes('&', "&2How to play - Leveling&0 \nDo '&3/upgrade info&0' to see what items you need to collect to level up. When you get an item you want to donate to the vault, do '&3/donte&0'. Every time you level up, you will be rewarded a chunk that you can claim."));
		meta.addPage(ChatColor.translateAlternateColorCodes('&', "&2How to play - Claiming chunks&0 \nTo claim a chunk, do '&3/buychunk&0' while looking towards the chunk you'd like to claim. A new chunk will be unlocked in the preferred direction. The borders will then fall between you and that chunk"));
		meta.addPage(ChatColor.translateAlternateColorCodes('&', "&2How to play - Capturing animals&0 \nAt at later lever, you will unlock the command '&3/capture&0', which captures the closest animal to you."));
		meta.addPage(ChatColor.translateAlternateColorCodes('&', "&2How to play - Trade&0 \nIf you want to trade with another player, type the command '&3/trade&0' to get to the middle chunk where you can meet. All players are free to build inside that chunk"));
		meta.addPage(ChatColor.translateAlternateColorCodes('&', "&2How to play - Selling items&0 \nThe command '&3/sell now&0' allow you to sell the current item in your hand. Do '&3/sell info&0' to check the item's current value. The value will change over time, so one may wait selling till the item has a greater value."));
		meta.addPage(ChatColor.translateAlternateColorCodes('&', "&2How to play - Roulette game&0 \nOnce every hour, a new game of Roulette will start. To join the game, type '&3/joinroulette&0' before the time limit. The roulette is not activated before getting to a certain level."));
		book.setItemMeta(meta);

		player.getInventory().addItem(book);
	}
}
