package com.johansvartdal.landlord.business;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Properties;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.*;

@Getter
public class BankBusiness extends Business {

    private final List<Loan> activeLoans = new ArrayList<>();
    private final List<LoanApplication> pendingApplications = new ArrayList<>();
    private final Random random = new Random();
    private int loanRequestSchedulerTaskId = -1;
    private int loanRepaymentSchedulerTaskId = -1;

    private static final String[] BUSINESS_NAMES = {
            "SteelCorp", "Quantum Solutions", "Blue Horizon Ventures",
            "Evergreen Industries", "Titan Enterprises", "NovaTech",
            "Skyline Investments", "Omega Holdings", "Solaris Tech",
            "BlockBnB inc", "CakeFarm inc", "ElCarts inc"
    };

    public BankBusiness(Main plugin, Player player, String name) {
        super(plugin, player, name);
    }

    public BankBusiness(Main plugin, JSONObject businessJson) {
        super(plugin, businessJson);
        if (businessJson.containsKey("activeLoans")) {
            List<?> loansJson = (List<?>) businessJson.get("activeLoans");
            for (Object obj : loansJson) {
                activeLoans.add(new Loan((JSONObject) obj));
            }
        }
        if (businessJson.containsKey("pendingApplications")) {
            List<?> applicationsJson = (List<?>) businessJson.get("pendingApplications");
            for (Object obj : applicationsJson) {
                pendingApplications.add(new LoanApplication((JSONObject) obj));
            }
        }
        startLoanRequestScheduler();
        startLoanRepaymentScheduler();
    }

    @Override
    public JSONObject getJson() {
        JSONObject json = super.getJson();
        List<JSONObject> loansJson = new ArrayList<>();
        for (Loan loan : activeLoans) {
            loansJson.add(loan.toJson());
        }
        json.put("activeLoans", loansJson);

        List<JSONObject> applicationsJson = new ArrayList<>();
        for (LoanApplication application : pendingApplications) {
            applicationsJson.add(application.toJson());
        }
        json.put("pendingApplications", applicationsJson);
        return json;
    }

    @Override
    protected int getRegistrationFee() {
        return 500000; // Higher fee for banks
    }

    @Override
    protected BusinessType getType() {
        return BusinessType.BANK;
    }

    @Override
    protected void onBusinessRegistered() {
        startLoanRequestScheduler();
        startLoanRepaymentScheduler();
    }

    @Override
    protected void onBusinessUnregistered() {
        stopLoanRequestScheduler();
        stopLoanRepaymentScheduler();
    }

    @Override
    protected boolean handleCommand(Player player, String[] args) {
        if (args.length == 0) {
            Tools.printMenuOption(player, "/business", "application list");
            Tools.printMenuOption(player, "/business", "application approve <NUMBER>");
            Tools.printMenuOption(player, "/business", "application reject <NUMBER>");
            Tools.printMenuOption(player, "/business", "loan list");
            return true;
        }

        if (args[0].equals("application")) {
            return switch (args[1]) {
                case "list" -> viewLoanRequests(player);
                case "approve" -> {
                    if (args.length < 3) {
                        yield false;
                    }
                    yield approveLoan(player, Integer.parseInt(args[2]));
                }
                case "reject" -> {
                    if (args.length < 3) {
                        yield false;
                    }
                    yield rejectLoan(player, Integer.parseInt(args[2]));
                }
                default -> false;
            };
        }else if(args[0].equals("loan")) {
            return switch (args[1]) {
                case "list" -> viewLoans(player);
                default -> false;
            };
        }

        return false;
    }

    @Override
    protected BusinessType getBusinessType() {
        return BusinessType.BANK;
    }

    private boolean viewLoanRequests(Player player) {
        Tools.printMenuHeader(player, LangDict.getString("business.bank.loanRequestsHeader"));
        for (int i = 0; i < pendingApplications.size(); i++) {
            LoanApplication app = pendingApplications.get(i);
            Tools.printMenuOption(player,i + ":",
                    app.getBusinessName() +
                    " | " + LangDict.getString("business.bank.amount") + Tools.formatCurrency(app.getAmount()) +
                    " | " + LangDict.getString("business.bank.interest") + app.getInterestRate() + "%" +
                    " | " + LangDict.getString("business.bank.duration") + app.getDurationHours() + "h");
        }
        return true;
    }

    private boolean viewLoans(Player player) {
        Tools.printMenuHeader(player, LangDict.getString("business.bank.loanHeader"));
        for (int i = 0; i < activeLoans.size(); i++) {
            Loan loan = activeLoans.get(i);
            Tools.printMenuOption(player, i + ":",
                    loan.getBusinessName() +
                    " | " + LangDict.getString("business.bank.remaining") + Tools.formatCurrency(loan.getRemainingAmount()) +
                    " | " + LangDict.getString("business.bank.principal") + Tools.formatCurrency(loan.getPrincipal()) +
                    " | " + LangDict.getString("business.bank.interest") + loan.getInterestRate() + "%" +
                    " | " + LangDict.getString("business.bank.totalDue") + Tools.formatCurrency(loan.getTotalDue()));
        }
        return true;
    }

    private boolean approveLoan(Player player, int loanIndex) {
        if (loanIndex < 0 || loanIndex >= pendingApplications.size()) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("business.bank.invalidLoanID"));
            return true;
        }

        LoanApplication app = pendingApplications.get(loanIndex);

        if (!canAfford(app.getAmount())) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("business.cannotAfford") + Tools.formatCurrency(app.getAmount()));
            return true;
        }

        pendingApplications.remove(app);

        withdrawBank(app.getAmount());

        activeLoans.add(new Loan(app.getBusinessName(), app.getAmount(), app.getInterestRate(), app.getDurationHours()));

        Tools.tellPlayer(getBusinessChatEntity(), player, LangDict.getString("business.bank.preLoanApproved") + app.getBusinessName() + LangDict.getString("business.bank.midLoanApproved") + Tools.formatCurrency(app.getAmount()), ChatColor.GREEN);
        return true;
    }

    private boolean rejectLoan(Player player, int loanIndex) {
        if (loanIndex < 0 || loanIndex >= pendingApplications.size()) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("business.bank.invalidLoanID"));
            return true;
        }

        Tools.tellPlayer(getBusinessChatEntity(), player, LangDict.getString("business.bank.preLoanRejected") + pendingApplications.get(loanIndex).getBusinessName(), ChatColor.RED);
        pendingApplications.remove(loanIndex);

        Main.businessManager.saveBusinesses();
        return true;
    }

    private void startLoanRequestScheduler() {
        if (loanRequestSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(loanRequestSchedulerTaskId);
        }

        scheduleNextLoanRequest();
    }

    private void stopLoanRequestScheduler() {
        if (loanRequestSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(loanRequestSchedulerTaskId);
            loanRequestSchedulerTaskId = -1;
        }
    }

    private void stopLoanRepaymentScheduler() {
        if (loanRepaymentSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(loanRepaymentSchedulerTaskId);
            loanRepaymentSchedulerTaskId = -1;
        }
    }


    private void scheduleNextLoanRequest() {
        int delayMinutes = random.nextInt(60) + 90; // Between 1.5 to 2.5 hours
        long delayTicks = Tools.secToTicks(60 * delayMinutes);

        loanRequestSchedulerTaskId = Bukkit.getScheduler().runTaskLater(this.getPlugin(), () -> {
            String businessName = BUSINESS_NAMES[random.nextInt(BUSINESS_NAMES.length)];
            int amount = (int) (random.nextDouble() * 50000 + 50000); // 50,000 - 100,000
            double interestRate = Tools.round(random.nextDouble() * 5 + 5, 2); // 5% - 10%
            int durationHours = random.nextInt(26) + 10; // 10-36 hours

            if (pendingApplications.size() >= 3) {
                pendingApplications.remove(0);
            }
            pendingApplications.add(new LoanApplication(businessName, amount, interestRate, durationHours));

            Main.businessManager.saveBusinesses();

            Player owner = Bukkit.getPlayer(UUID.fromString(getOwnerUUID()));
            if (owner != null) {
                Tools.tellPlayer(getBusinessChatEntity(), owner, LangDict.getString("business.bank.preNewLoanRequest") + businessName + LangDict.getString("business.bank.postNewLoanRequest"), ChatColor.GRAY);
            }

            scheduleNextLoanRequest();
        }, delayTicks).getTaskId();
    }

    private void startLoanRepaymentScheduler() {
        if (loanRepaymentSchedulerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(loanRepaymentSchedulerTaskId);
        }

        long interval = Tools.secToTicks(60*60); // every hour

        loanRepaymentSchedulerTaskId = Bukkit.getScheduler().runTaskLater(this.getPlugin(), () -> {
            for (Iterator<Loan> iterator = activeLoans.iterator(); iterator.hasNext(); ) {
                Loan loan = iterator.next();
                int totalDue = (int) loan.getTotalDue();
                Player player = Bukkit.getPlayer(UUID.fromString(getOwnerUUID()));

                if (random.nextDouble() > 0.02) { // 98% chance of repayment
                    depositBank(totalDue);
                    loan.payPrincipal();

                    if (loan.getRemainingAmount() <= 0 || loan.getRemainingHours() <= 0) {
                        Tools.tellPlayer(getBusinessChatEntity(), player, loan.getBusinessName() + LangDict.getString("business.bank.midLoanPayOff"), ChatColor.GREEN);
                        iterator.remove();
                    }else {
                        Tools.tellPlayer(getBusinessChatEntity(), player, loan.getBusinessName() + LangDict.getString("business.bank.midPaidPrincipal") + Tools.formatCurrency(totalDue), ChatColor.GRAY);
                    }
                } else {
                    Tools.tellPlayer(getBusinessChatEntity(), player, loan.getBusinessName() + LangDict.getString("business.bank.loanDefaulted") + Tools.formatCurrency(loan.getRemainingAmount()), ChatColor.RED);
                    iterator.remove();
                }
            }

            Main.businessManager.saveBusinesses();

            startLoanRepaymentScheduler();
        }, interval).getTaskId();
    }



    @Getter
    public class Loan {
        private final String businessName;
        private int remainingAmount;
        private int remainingHours;
        private final double interestRate;

        public Loan(String businessName, int remainingAmount, double interestRate, int remainingHours) {
            this.businessName = businessName;
            this.remainingAmount = remainingAmount;
            this.interestRate = interestRate;
            this.remainingHours = remainingHours;
        }

        public Loan(JSONObject json) {
            this.businessName = json.get("businessName").toString();
            this.remainingAmount = ((Long) json.get("remainingAmount")).intValue();
            this.interestRate = (double) json.get("interestRate");
            this.remainingHours = ((Long) json.get("remainingHours")).intValue();
        }

        public double getPrincipal() {
            return ((double) remainingAmount / remainingHours);
        }

        /**
         * Calculates the total repayment amount.
         */
        public double getTotalDue() {
            return getPrincipal() + (remainingAmount * (interestRate / 100));
        }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("businessName", businessName);
            json.put("remainingAmount", remainingAmount);
            json.put("interestRate", interestRate);
            json.put("remainingHours", remainingHours);
            return json;
        }

        public void payPrincipal() {
            remainingAmount -= (int) getPrincipal();
            remainingHours -= 1;
        }
    }

    @Getter
    public class LoanApplication {
        private final String businessName;
        private final int amount;
        private final double interestRate;
        private final int durationHours;

        public LoanApplication(String businessName, int amount, double interestRate, int durationHours) {
            this.businessName = businessName;
            this.amount = amount;
            this.interestRate = interestRate;
            this.durationHours = durationHours;
        }

        public LoanApplication(JSONObject json) {
            this.businessName = json.get("businessName").toString();
            this.amount = ((Long) json.get("amount")).intValue();
            this.interestRate = (double) json.get("interestRate");
            this.durationHours = ((Long) json.get("durationHours")).intValue();
        }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("businessName", businessName);
            json.put("amount", amount);
            json.put("interestRate", interestRate);
            json.put("durationHours", durationHours);
            return json;
        }
    }
}

