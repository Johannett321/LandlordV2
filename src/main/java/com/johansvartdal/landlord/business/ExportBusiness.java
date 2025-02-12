package com.johansvartdal.landlord.business;


import com.johansvartdal.landlord.Bank;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.levels.LevelManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.Random;

public class ExportBusiness extends Business {

    private final Random random = new Random();

    private ExportJob exportJob = null;

    public ExportBusiness(Main plugin, Player player, String name) {
        super(plugin, player, name);
    }

    public ExportBusiness(Main plugin, JSONObject businessJson) {
        super(plugin, businessJson);

        if (businessJson.containsKey("currentJob")) {
            exportJob = new ExportJob((JSONObject) businessJson.get("currentJob"));
        }else {
            scheduleRandomJob();
        }
    }

    @Override
    protected int getRegistrationFee() {
        return 100000;
    }

    @Override
    protected BusinessType getType() {
        return BusinessType.EXPORT;
    }

    @Override
    protected void onBusinessRegistered() {
        scheduleRandomJob();
    }

    @Override
    protected void handleCommand(Player player, String[] args) {
        if (args.length == 0) {
            Tools.printMenuHeader(player, LangDict.getString("business.businessCommands"));
            if (exportJob != null) {
                Tools.printMenuOption(player, "/business", "accept");
                Tools.printMenuOption(player, "/business", "reject");
                Tools.printMenuOption(player, "/business", "shipgoods");
            }
            return;
        }
        if (args[0].equals("accept")) {
            acceptJobOffer(player);
        }else if (args[0].equals("reject")) {
            rejectJobOffer(player);
        }else if (args[0].equals("shipgoods")) {
            shipGoods(player);
        }
    }

    private void shipGoods(Player player) {
        String playerFacingDirection = Tools.getPlayerFacingDirection(player);
        Block chestBlock = getChestInDirection(player, playerFacingDirection);
        if (chestBlock == null) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("business.cannotFindChest"));
            return;
        }

        Chest chest = (Chest) chestBlock.getState();

        int remaining = exportJob.getItems().getAmount();

        for (ItemStack itemStack : chest.getBlockInventory().getContents()) {
            if (!itemStack.getType().toString().equals(exportJob.getItems().getType().toString())) {
                Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("business.wrongItemsInChest") + exportJob.getItems().getAmount() + " " + exportJob.getItems().getType());
                return;
            }
            remaining -= itemStack.getAmount();
        }

        if (remaining != 0) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("business.wrongItemsInChest") + exportJob.getItems().getAmount() + " " + exportJob.getItems().getType());
            return;
        }

        chestBlock.setType(Material.AIR);

        Tools.tellPlayer(getBusinessChatEntity(), player, LangDict.getString("business.justShipped") + exportJob.getItems().getAmount() + " " + exportJob.getItems().getType() + LangDict.getString(""));

        Bank.depositPlayerWithoutTax(player, remaining);
    }

    private Block getChestInDirection(Player player, String playerFacingDirection) {
        Location playerLocation = player.getLocation();
        Location location = switch (playerFacingDirection) {
            case "north" ->
                    new Location(playerLocation.getWorld(), playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ() - 1);
            case "south" ->
                    new Location(playerLocation.getWorld(), playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ() - 1);
            case "west" ->
                    new Location(playerLocation.getWorld(), playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ() - 1);
            case "east" ->
                    new Location(playerLocation.getWorld(), playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ() - 1);
            default -> null;
        };

        if (location == null || !(location.getBlock().getState() instanceof Chest)) {
            return null;
        }

        return location.getBlock();
    }

    @Override
    public JSONObject getJson() {
        JSONObject json = super.getJson();
        if (exportJob != null) {
            json.put("currentJob", exportJob.toJson());
        }
        return json;
    }

    public void scheduleRandomJob() {
        int minutesTillNextOrder = random.nextInt(90) + 90; // minimum 1.5 hours, maximum 3 hours

        Bukkit.getScheduler().runTaskLater(this.getPlugin(), () -> {
            if (!Main.properties.gameStateIsNormal()) {
                scheduleRandomJob();
                return;
            }

            if (exportJob == null || exportJob.status == ExportJobStatus.PROPOSED) {
                generateJobOffer();
                proposeJob();
            }
            scheduleRandomJob();
        }, Tools.secToTicks(60*minutesTillNextOrder));
    }

    private void generateJobOffer() {
        // available materials
        Material[] materials = new Material[]{ Material.WHEAT, Material.SUGAR_CANE, Material.CARROT, Material.POTATO, Material.BEETROOT, Material.COCOA_BEANS };

        // generate job
        int randomMaterialIndex = random.nextInt(materials.length);
        ItemStack itemStack = new ItemStack(materials[randomMaterialIndex]);
        itemStack.setAmount(random.nextInt(32 * (LevelManager.getCurrentDisplayLevelNum() * LevelManager.getCurrentDisplaySeasonNum())));
        int pay = itemStack.getAmount() * (random.nextInt(40) + 70);
        exportJob = new ExportJob(itemStack, pay);

        // save
        Main.businessManager.saveBusinesses();
    }

    private void proposeJob() {
        Tools.tellPlayer(this.getBusinessChatEntity(), Bukkit.getPlayer(
                this.getOwnerUUID()),
                LangDict.getString("business.newExportJob") +
                        exportJob.getItems().getAmount() +
                        exportJob.getItems().getType() + " (" +
                        Tools.formatCurrency(exportJob.getPay()) + ")" +
                        LangDict.getString("business.acceptTheJobBy")
        );
    }

    private void acceptJobOffer(Player player) {
        if (exportJob == null || Tools.stateNotNormal(player)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW));
            return;
        }
        exportJob.status = ExportJobStatus.ACCEPTED;

        Main.businessManager.saveBusinesses();

        Tools.tellPlayer(getBusinessChatEntity(), player, LangDict.getString("business.jobAccepted"));
    }

    private void rejectJobOffer(Player player) {
        if (exportJob == null || Tools.stateNotNormal(player)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW));
            return;
        }

        exportJob = null;
        Main.businessManager.saveBusinesses();

        Tools.tellPlayer(getBusinessChatEntity(), player, LangDict.getString("business.jobRejected"));
    }

    @Getter
    class ExportJob {
        private ItemStack items;
        private int pay;
        private ExportJobStatus status;

        public ExportJob(ItemStack items, int pay) {
            this.items = items;
            this.pay = pay;
            this.status = ExportJobStatus.PROPOSED;
        }

        public ExportJob(JSONObject json) {
            ItemStack itemStack = new ItemStack(Material.valueOf(json.get("material").toString()));
            itemStack.setAmount((int) json.get("amount"));
            this.items = itemStack;
            this.pay = (int) json.get("pay");
            this.status = ExportJobStatus.valueOf(json.get("status").toString());
        }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("material", items.getType().toString());
            json.put("amount", items.getAmount());
            json.put("pay", pay);
            json.put("status", status.toString());
            return json;
        }
    }

    enum ExportJobStatus {
        PROPOSED,
        ACCEPTED
    }
}
