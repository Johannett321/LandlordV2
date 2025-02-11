package com.johansvartdal.landlord.business;


import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.levels.LevelManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
        if (args[0].equals("accept")) {
            acceptJobOffer(player);
        }else if (args[0].equals("reject")) {
            rejectJobOffer(player);
        }
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
        exportJob.status = ExportJobStatus.ACCEPTED;

        Main.businessManager.saveBusinesses();

        Tools.tellPlayer(getBusinessChatEntity(), player, LangDict.getString("business.jobAccepted"));
    }

    private void rejectJobOffer(Player player) {
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
