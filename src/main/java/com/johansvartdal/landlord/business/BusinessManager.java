package com.johansvartdal.landlord.business;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.InfoChat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BusinessManager {

    private Main plugin;
    List<Business> businesses = new ArrayList<>();

    public BusinessManager(Main plugin) {
        this.plugin = plugin;
        loadBusinesses();
    }

    public boolean handleCommand(Player player, String[] args) {
        Business business = getPlayerBusiness(player);

        // if no args were given
        if (args.length == 0) {
            Tools.printMenuHeader(player, LangDict.getString("business.businessCommands"));
            if (business == null) {
                Tools.printMenuOption(player, "/business", "list");
                Tools.printMenuOption(player, "/business", "create [export | insurance | bank] <NAME>");
            }else {
                Tools.printMenuOption(player, "/business", "info");
                Tools.printMenuOption(player, "/business", "deposit <AMOUNT>");
                Tools.printMenuOption(player, "/business", "withdraw <AMOUNT>");
                Tools.printMenuOption(player, "/business", "list");
                Tools.printMenuOption(player, "/business", "delete");
                business.handleCommand(player, args);
            }
            return true;
        }

        // handle list command
        if (args[0].equals("list")) {
            handleListCommand(player);
            return true;
        }

        // if there are no business
        if (business == null) {
            return switch (args[0]) {
                case "create" -> handleCreateBusinessCommand(player, args);
                default -> false;
            };
        }

        // if there is a business
        return switch (args[0]) {
            case "delete" -> handleDeleteBusinessCommand(business, player, args);
            case "deposit" -> handleDepositCommand(business, player, args);
            case "withdraw" -> handleWithdrawCommand(business, player, args);
            case "info" -> handleInfoCommand(business, player);
            default -> business.handleCommand(player, args);
        };
    }

    private boolean handleListCommand(Player player) {
        Tools.printMenuHeader(player, LangDict.getString("business.businessLists"));
        for (Business business : businesses) {
            Tools.printMenuOption(player, business.getName(), Tools.formatCurrency(business.getRevenue()));
        }
        return true;
    }

    private boolean handleInfoCommand(Business business, Player player) {
        Tools.printMenuHeader(player, LangDict.getString("business.info"));
        Tools.printMenuOption(player, LangDict.getString("business.name"), business.getName());
        Tools.printMenuOption(player, LangDict.getString("banking.balance"), Tools.formatCurrency(business.getBankAccount()));
        Tools.printMenuOption(player, LangDict.getString("business.totalRevenue"), Tools.formatCurrency(business.getRevenue()));
        return true;
    }

    private boolean handleCreateBusinessCommand(Player player, String[] args) {
        if (args.length != 3) {
            return false;
        }

        String businessName = args[2];

        Business business = switch (args[1].toLowerCase()) {
            case "export" -> new ExportBusiness(plugin, player, businessName);
            case "insurance" -> new InsuranceBusiness(plugin, player, businessName);
            case "bank" -> new BankBusiness(plugin, player, businessName);
            default -> null;
        };

        if (business == null) {
            return false;
        }

        registerBusiness(player, business);

        return true;
    }

    private boolean handleDeleteBusinessCommand(Business business, Player player, String[] args) {
        if (args.length != 2 || !args[1].equalsIgnoreCase("confirm")) {
            Tools.tellPlayer(player, LangDict.getString("business.deleteConfirmUsage"));
            return true;
        }

        business.onBusinessUnregistered();

        int balance = business.getBankAccount();
        business.withdrawBank(balance);
        Bank.depositPlayer(player, balance);

        businesses.remove(business);
        saveBusinesses();

        return true;
    }

    private boolean handleDepositCommand(Business business, Player player, String[] args) {
        if (args.length != 2 || business == null) {
            return false;
        }

        try {
            int amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                Tools.tellPlayer(player, LangDict.getString("business.invalidAmount"));
                return true;
            }

            Bank.withdrawPlayerWithoutTax(player, amount);
            business.depositBank(amount);
            saveBusinesses();

            Tools.tellPlayer(business.getBusinessChatEntity(), player, LangDict.getString("business.deposit") + amount);
        } catch (NumberFormatException e) {
            Tools.tellPlayer(player, LangDict.getString("business.invalidAmount"));
            return true;
        }
        return true;
    }

    private boolean handleWithdrawCommand(Business business, Player player, String[] args) {
        if (args.length != 2 || business == null) {
            return false;
        }

        try {
            int amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                Tools.tellPlayer(player, LangDict.getString("business.invalidAmount"));
                return true;
            }

            Bank.depositPlayer(player, amount);
            business.withdrawBank(amount);
            saveBusinesses();

            Tools.tellPlayer(business.getBusinessChatEntity(), player, LangDict.getString("business.withdraw") + amount);
        } catch (NumberFormatException e) {
            Tools.tellPlayer(player, LangDict.getString("business.invalidAmount"));
            return true;
        }
        return true;
    }




    public void registerBusiness(Player player, Business business) {
        // make sure player can afford and withdraw
        if (!Bank.playerCanAfford(player, business.getRegistrationFee())) {
            Bank.tellPlayerTheyNeed(player, business.getRegistrationFee(), LangDict.getString("business.toStartABusiness"));
            return;
        }
        Bank.withdrawPlayer(LangDict.getString("business.startingABusiness"), player, business.getRegistrationFee());

        // each business should begin with 30000 in their bank
        business.depositBank(30000);

        // add and save business
        businesses.add(business);
        saveBusinesses();

        // broadcast business registered
        Tools.broadcastMessage(new InfoChat(), player.getDisplayName() + LangDict.getString("business.justFoundedANewBusiness") + business.getName());
        player.sendTitle(ChatColor.GOLD + business.getName(), ChatColor.RED + LangDict.getString("business.ceo") + player.getDisplayName());

        // Notify business registered
        business.onBusinessRegistered();
    }

    public Business getPlayerBusiness(Player player) {
        return businesses.stream()
                .filter(business -> business.getOwnerUUID().equals(player.getUniqueId().toString()))
                .findFirst()
                .orElse(null);
    }

    public void saveBusinesses() {
        JSONArray businessesJsonArray = new JSONArray();
        businesses.forEach(business -> businessesJsonArray.add(business.getJson()));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("businesses", businessesJsonArray);

        Tools.saveJsonToFile("businesses.json", jsonObject);
    }

    public void loadBusinesses() {
        businesses = new ArrayList<>();

        // load businesses.json
        JSONObject businessesJsonObject = Tools.loadJson("businesses.json");
        if (businessesJsonObject == null || !businessesJsonObject.containsKey("businesses")) {
            return;
        }

        // load businesses array
        JSONArray businessesJsonArray = (JSONArray) businessesJsonObject.get("businesses");
        if (businessesJsonArray == null) {
            return;
        }

        businessesJsonArray.forEach(businessObject -> {
            JSONObject businessJsonObject = (JSONObject) businessObject;
            String type = businessJsonObject.get("type").toString();

            Business business = switch (type) {
                case "export" -> new ExportBusiness(plugin, businessJsonObject);
                case "insurance" -> new InsuranceBusiness(plugin, businessJsonObject);
                case "bank" -> new BankBusiness(plugin, businessJsonObject);
                default -> null;
            };
            businesses.add(business);
        });
    }
}
