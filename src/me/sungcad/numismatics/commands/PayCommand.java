package me.sungcad.numismatics.commands;

import static me.sungcad.numismatics.tools.MoneyParser.format;
import static me.sungcad.numismatics.tools.MoneyParser.parse;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sungcad.numismatics.NumismaticsPlugin;
import me.sungcad.numismatics.tools.Files;
import net.milkbowl.vault.economy.Economy;

public class PayCommand implements CommandExecutor {
    private HashMap<String, String> confirmation = new HashMap<String, String>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdname, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[0]);
                double amount = parse(args[1]);
                if (target != null) {
                    if (Files.CONFIG.getConfig().getBoolean("pay.confirm")) {
                        if (confirmation.containsKey(player.getName())) {
                            if (confirmation.get(player.getName()).equals(target.getName() + ", " + amount)) {
                                confirmation.remove(player.getName());
                                return pay(player, target, amount);
                            }
                            confirmation.remove(player.getName());
                        }
                        sendMessage(player, formatConfigString("pay.confirmation.one", player, target.getName(), amount));
                        confirmation.put(player.getName(), target.getName() + ", " + amount);
                        return true;
                    } else {
                        return pay(player, target, amount);
                    }
                } else if (args[0].equals("*")) {
                    if (sender.hasPermission("numismatics.pay.payall")) {
                        int playercount = Bukkit.getOnlinePlayers().size();
                        if (NumismaticsPlugin.getEconomy().has(player, amount * (playercount - 1))) {
                            if (Files.CONFIG.getConfig().getBoolean("pay.confirm")) {
                                if (confirmation.containsKey(player.getName())) {
                                    if (confirmation.get(player.getName()).equals("*, " + amount)) {
                                        confirmation.remove(player.getName());
                                        payAll(player, amount);
                                        return true;
                                    }
                                    confirmation.remove(player.getName());
                                }
                                sendMessage(player, formatConfigString("pay.confirmation.all", player, "*", amount));
                                confirmation.put(player.getName(), "*, " + amount);
                                return true;
                            } else {
                                payAll(player, amount);
                                return true;
                            }
                        }
                        sendMessage(sender, this.formatConfigString("pay.failure.lowbalance", player, "*", amount * (playercount - 1)));
                        return true;
                    } else
                        sendMessage(sender, cmd.getPermissionMessage());
                } else
                    sendMessage(sender, translateAlternateColorCodes('&', Files.CONFIG.getConfig().getString("pay.failure.playernotfound")));
            } else
                sendMessage(sender, cmd.getUsage().replace("<command>", cmdname));
        } else
            ;
        return true;
    }

    private void payAll(Player sender, double amount) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.equals(sender)) {
                pay(sender, target, amount);
            }
        }
    }

    private boolean pay(Player sender, Player target, double amount) {
        Economy econ = NumismaticsPlugin.getEconomy();
        if (sender != target) {
            if (amount >= Files.CONFIG.getConfig().getDouble("pay.min")) {
                if (econ.has(sender, amount)) {
                    if (econ.withdrawPlayer(sender, amount).transactionSuccess() && econ.depositPlayer(target, amount).transactionSuccess()) {
                        sendMessage(sender, formatConfigString("pay.success.sender", sender, target.getName(), amount));
                        sendMessage(target, formatConfigString("pay.success.receiver", sender, target.getName(), amount));
                    }
                } else
                    sendMessage(sender, formatConfigString("pay.failure.lowbalance", sender, target.getName(), amount));
            } else
                sendMessage(sender, formatConfigString("pay.failure.belowminimum", sender, target.getName(), amount));
        } else
            sendMessage(sender, formatConfigString("pay.failure.senttoself", sender, target.getName(), amount));
        return true;
    }

    private String formatConfigString(String message, Player sender, String target, double amount) {
        String text = Files.CONFIG.getConfig().getString(message);
        text = text.replace("{amount}", format(amount, true));
        text = text.replace("{receiver}", target);
        text = text.replace("{sender}", sender.getName());
        text = translateAlternateColorCodes('&', text);
        return text;
    }

    private void sendMessage(CommandSender reciever, String message) {
        if (!message.isEmpty()) {
            reciever.sendMessage(message);
        }
    }
}
