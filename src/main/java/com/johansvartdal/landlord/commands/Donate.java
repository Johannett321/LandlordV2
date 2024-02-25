package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.InfoChat;
import com.johansvartdal.landlord.levels.LevelManager;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class Donate implements CommandExecutor {

	enum DonateItemResponseType {
		ERROR,
		NO_ITEM,
		DONATED_EVERYTHING,
		STILL_REMAINING,
		READY_FOR_UPGRADE
	}

	@Getter
	class DonateItemProcess {
		public ItemStack itemStackBeingDonated;
		public String itemName;
		public String errorMessage;
		boolean errorOccurred;
		boolean everythingOfItemWasFilled;
		boolean readyForUpgrade;
		int amountDonated;

		DonateItemProcess (ItemStack itemStack) {
			this.itemStackBeingDonated = itemStack;
			this.itemName = Tools.getDisplayNameOfItem(itemStack);
		}

		public void setItemStackBeingDonated(ItemStack itemStackBeingDonated) {
			this.itemStackBeingDonated = itemStackBeingDonated;
			this.itemName = Tools.getDisplayNameOfItem(itemStackBeingDonated);
		}
	}

	private Main plugin;

	public Donate(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("donate").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (Tools.stateNotNormal(sender)) {
			Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
			return true;
		}

		Player player = (Player) sender;


		if (args.length == 0) {
			Tools.printMenuHeader(player, "Commands");
			Tools.printMenuOption(player, "/donate", "now");
			Tools.printMenuOption(player, "/donate", "all");
			Tools.printMenuOption(player, "/donate", "info");
			return true;
		}

		if (args[0].equals("now")) {
			donateNow(player);
			return true;
		}else if (args[0].equals("all")) {
			donateAll(player);
			return true;
		}else if (args[0].equals("info")) {
			//sellInfo(player);
			return true;
		}
		return false;
	}

	/**
	 * donates all items in the hotbar that is of the item in main hand if required
	 * @param player The player that attempts to donate
	 */
	private void donateAll(Player player) {
		ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
		Material materialType = itemInMainHand.getType();

		// do prechecks
		if (donationIsABadIdea(player)) {
			return;
		}

		DonateItemProcess process = new DonateItemProcess(itemInMainHand);

		//loop through hotbar to see if user got more to sell
		for (int i = 0; i < 9; i++) {
			ItemStack itemInCurrentSlot = player.getInventory().getItem(i);
			if (itemInCurrentSlot == null || !itemInCurrentSlot.getType().equals(materialType)) {
				String name = "EMTPY";
				if (itemInCurrentSlot != null) {
					name = itemInCurrentSlot.getType().name();
				}
				System.out.println("Item at " + i + " which is " + name + " is not " + itemInMainHand.getType().name());
				continue;
			}

			System.out.println("Attempting to donate number " + i + " which is " + itemInCurrentSlot.getType().name() + ". in main hand we have " + itemInMainHand.getType().name());

			// attempt donation
			process.setItemStackBeingDonated(itemInCurrentSlot);
			attemptDonateItem(player, process);

			// check if we should break
			if (process.readyForUpgrade || process.everythingOfItemWasFilled) {
				System.out.println("We broke because: " + process.readyForUpgrade + ":" + process.everythingOfItemWasFilled);
				break;
			}
		}

		// give feedback
		feedbackPlayer(player, process);
	}

	/**
	 * Donates the item in main hand
	 * @param player the player executing the command
	 */
	public void donateNow(Player player) {
		ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

		if (Properties.DEBUG_LOGGING) System.out.println("Attempting to donate: " + itemInMainHand.getType().name());

		// do prechecks
		if (donationIsABadIdea(player)) {
			if (Properties.DEBUG_LOGGING) System.out.println("Donation of item is a bad idea: " + itemInMainHand.getType().name());
			return;
		}

		// attempt donation
		DonateItemProcess process = new DonateItemProcess(itemInMainHand);
		attemptDonateItem(player, process);

		// give feedback
		feedbackPlayer(player, process);
	}

	/**
	 * Does a lot of prechecks to make sure it's a good idea to upgrade now
	 * @param player the player that is executing a command
	 * @return true if donation is a good idea
	 */
	public boolean donationIsABadIdea(Player player) {
		LevelManager.UpgradeDecision upgradeDecision = LevelManager.getUpgradeStatus();
		return !switch (upgradeDecision) {
			case UPGRADE, NOT_ENOUGH_ITEMS, NOT_EVERYONE_HAS_ACCEPTED -> true;
			case PLAYER_IN_EVENT -> {
				Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("commandResponses.errorMessages.upgradePlayerInEvent"));
				yield false;
			}
			case GAME_STATE_NOT_NORMAL -> false;
		};
	}

	/**
	 * Plays the appropriate feedback to the player. Includes audio and chat message
	 * @param player the player to get feedback
	 * @param donateItemProcess the received response
	 */
	private void feedbackPlayer(Player player, DonateItemProcess donateItemProcess) {
		if (donateItemProcess.errorOccurred) {
			Tools.tellPlayer(new ErrorChat(), player, donateItemProcess.errorMessage);
			return;
		}

		// play audio
		if (donateItemProcess.readyForUpgrade) {
			Tools.playSoundForEveryone(Sound.ENTITY_PLAYER_LEVELUP);
			God.speak(LangDict.getString("donate.itemDonationsComplete"));
		}else if (donateItemProcess.everythingOfItemWasFilled) {
			Tools.playSoundForEveryone(Sound.BLOCK_NOTE_BLOCK_GUITAR);
			Tools.broadcastMessage(new InfoChat(), player.getDisplayName() + LangDict.getString("donate.justDonated") + donateItemProcess.itemName + LangDict.getString("donate.toCommunity"), ChatColor.GREEN);
		}else {
			Tools.playSoundForEveryone(Sound.BLOCK_AMETHYST_CLUSTER_STEP);
			Tools.broadcastMessage(new InfoChat(), player.getDisplayName() + LangDict.getString("donate.onDonation") + donateItemProcess.getAmountDonated() + " " + donateItemProcess.itemName + LangDict.getString("donate.toCommunity"), ChatColor.GRAY);
		}
	}

	/**
	 * Attempt to donate an item
	 * @param player The player that is donating
	 * @param donateItemProcess The itemstack to donate
	 * @return A response
	 */
	private void attemptDonateItem(Player player, DonateItemProcess donateItemProcess) {
		ItemStack itemStack = donateItemProcess.itemStackBeingDonated;

		System.out.println("Item in donation process: " + itemStack.getType().name());

		// Return if it's AIR
		if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
			donateItemProcess.errorOccurred = true;
			donateItemProcess.errorMessage = "Please hold the item you would like to donate in your main hand";
			System.out.println("IT IS AIR: " + itemStack.getType().name());
			return;
		}

		System.out.println("NOT AIR: " + itemStack.getType().name());

		// make sure item is required for upgrade
		if (!LevelManager.itemRequiredForUpgrade(itemStack)) {
			donateItemProcess.errorOccurred = true;
			donateItemProcess.errorMessage = LangDict.getString("upgrade.notRequiredForUpgrade");
			System.out.println("NOT REQUIRED: " + itemStack.getType().name());
			return;
		}

		// determine how many should be donated
		int onHand = itemStack.getAmount();
		int required = LevelManager.getAmountRequiredForItem(itemStack.getType());

		if (onHand >= required) {
			donateItemProcess.everythingOfItemWasFilled = true;
			donateItemProcess.amountDonated += required;
			onHand -= required;
			required = 0;
			System.out.println("HAND IS GREATER: " + itemStack.getType().name());
		}else {
			donateItemProcess.amountDonated += onHand;
			required -= onHand;
			onHand = 0;
			System.out.println("HAND IS LESS: " + itemStack.getType().name());
		}

		System.out.println("Step1: " + itemStack.getType().name());

		// tell LevelManager to update amount required
		LevelManager.updateRequiredAmountForItem(itemStack.getType(), required);

		// update itemStack
		itemStack.setAmount(onHand);

		// check if we are ready for upgrade
		if (LevelManager.getListOfRemainingItems().isEmpty()) {
			donateItemProcess.readyForUpgrade = true;
		}
	}
}
