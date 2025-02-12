package com.johansvartdal.landlord.business;


import com.johansvartdal.landlord.Bank;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.levels.LevelManager;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.Random;

public class ExportBusiness extends Business {

    private final Random random = new Random();

    private ExportJob exportJob = null;

    private int jobSchedulerTaskId = -1;

    public ExportBusiness(Main plugin, Player player, String name) {
        super(plugin, player, name);
    }

    public ExportBusiness(Main plugin, JSONObject businessJson) {
        super(plugin, businessJson);

        if (businessJson.containsKey("currentJob")) {
            exportJob = new ExportJob((JSONObject) businessJson.get("currentJob"));
        }
        startJobScheduler();
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
        startJobScheduler();
    }

    @Override
    protected void onBusinessUnregistered() {
        stopJobScheduler();
    }

    @Override
    protected boolean handleCommand(Player player, String[] args) {
        if (args.length == 0) {
            if (exportJob != null) {
                Tools.printMenuOption(player, "/business", "accept");
                Tools.printMenuOption(player, "/business", "reject");
                Tools.printMenuOption(player, "/business", "shipgoods");
            }
            return true;
        }
        if (args[0].equals("accept")) {
            acceptJobOffer(player);
        }else if (args[0].equals("reject")) {
            rejectJobOffer(player);
        }else if (args[0].equals("shipgoods")) {
            shipGoods(player);
        }
        return false;
    }

    private void shipGoods(Player player) {
        // make sure there is an active job
        if (exportJob == null || exportJob.getStatus() == ExportJobStatus.PROPOSED) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return;
        }

        // get chest
        String playerFacingDirection = Tools.getPlayerFacingDirection(player);
        Block chestBlock = getBlockInDirection(player, playerFacingDirection);
        if (chestBlock == null) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("business.cannotFindChest"));
            return;
        }
        Chest chest = (Chest) chestBlock.getState();

        // ensure chest only contains the job items, and no more
        int remaining = exportJob.getItems().getAmount();
        for (ItemStack itemStack : chest.getBlockInventory().getContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue; // Ignore empty slots
            }

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

        // remove chest from world
        chestBlock.setType(Material.AIR);
        player.playEffect(chestBlock.getLocation(), Effect.ELECTRIC_SPARK, null);
        player.playSound(chestBlock.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0);

        // deposit and inform player
        Tools.tellPlayer(getBusinessChatEntity(),
                player,
                LangDict.getString("business.justShipped") +
                        exportJob.getItems().getAmount() +
                        " " +
                        exportJob.getItems().getType() +
                        LangDict.getString("sellItem.for") +
                        Tools.formatCurrency(exportJob.getPay()) +
                        LangDict.getString("business.taxFree")
        );
        Bank.depositPlayerWithoutTax(player, exportJob.getPay());

        // reset the exportJob
        exportJob = null;
    }

    private Block getBlockInDirection(Player player, String direction) {
        Location playerLocation = player.getLocation();
        Location location = switch (direction) {
            case "north" ->
                    new Location(playerLocation.getWorld(), playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ() - 1);
            case "south" ->
                    new Location(playerLocation.getWorld(), playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ() + 1);
            case "west" ->
                    new Location(playerLocation.getWorld(), playerLocation.getBlockX() - 1, playerLocation.getBlockY(), playerLocation.getBlockZ());
            case "east" ->
                    new Location(playerLocation.getWorld(), playerLocation.getBlockX() + 1, playerLocation.getBlockY(), playerLocation.getBlockZ());
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

    public void stopJobScheduler() {
        if (jobSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(jobSchedulerTaskId);
            jobSchedulerTaskId = -1;
        }
    }

    public void startJobScheduler() {
        if (jobSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(jobSchedulerTaskId); // Cancel previous task if running
        }

        int intervalMinutes = random.nextInt(90) + 90; // Min 1.5 hours, max 3 hours
        long intervalTicks = Tools.secToTicks(60 * intervalMinutes);

        jobSchedulerTaskId = Bukkit.getScheduler().runTaskTimer(this.getPlugin(), () -> {
            if (!Main.properties.gameStateIsNormal()) {
                return;
            }

            if (exportJob == null || exportJob.status == ExportJobStatus.PROPOSED) {
                generateJobOffer();
                proposeJob();
            }
        }, intervalTicks, intervalTicks).getTaskId();
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
        Player businessOwner = Bukkit.getPlayer(this.getOwnerUUID());
        if (businessOwner == null) return; // Player is offline, don't send the message

        Tools.tellPlayer(this.getBusinessChatEntity(), businessOwner,
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

    /*------------------------------------------------ EXPORT JOB ------------------------------------------------*/

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
            try {
                Material material = Material.valueOf(json.get("material").toString());
                ItemStack itemStack = new ItemStack(material);
                itemStack.setAmount((int) json.get("amount"));
                this.items = itemStack;
                this.pay = (int) json.get("pay");
                this.status = ExportJobStatus.valueOf(json.get("status").toString());
            } catch (IllegalArgumentException | NullPointerException e) {
                Bukkit.getLogger().warning("Invalid material found in ExportJob JSON: " + json);
                this.items = new ItemStack(Material.AIR);
                this.pay = 0;
                this.status = ExportJobStatus.PROPOSED;
            }
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
