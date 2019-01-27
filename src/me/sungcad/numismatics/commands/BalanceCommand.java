package me.sungcad.numismatics.commands;

import static me.sungcad.numismatics.tools.MoneyParser.format;
import static org.bukkit.ChatColor.translateAlternateColorCodes;
import static org.bukkit.Bukkit.getPlayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sungcad.numismatics.NumismaticsPlugin;

public class BalanceCommand implements CommandExecutor {
    private boolean rounded;
    private NumismaticsPlugin plugin;

    public BalanceCommand(boolean rounded, NumismaticsPlugin plugin) {
        this.plugin = plugin;
        this.rounded = rounded;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdname, String[] args) {
        // used to check a target players balance
        if (args.length == 1) {
            // check if the command sender has permission to check other players
            // balances
            if (sender.hasPermission(rounded ? "numismatics.balance.others" : "numismatics.ebalance.others")) {
                // set the target player
                Player target = getPlayer(args[0]);
                if (target == null && sender instanceof Player)
                    // if there is no target and the sender is a player check
                    // their balance instead
                    target = (Player) sender;
                else if (target == null && !(sender instanceof Player))
                    // console has no balance so
                    return false;
                sendMessage("balance.others", target, sender);
            }
        }
        // check the senders balance
        else {
            if (sender instanceof Player)
                sendMessage("balance.self", (Player) sender, sender);
            else
                // can't check consoles balance as it has no balance
                return false;
        }
        return true;
    }

    private void sendMessage(String string, Player target, CommandSender user) {
        String text = plugin.getConfig().getString(string);
        text = text.replace("{balance}", format(NumismaticsPlugin.getEconomy().getBalance(target), rounded));
        text = text.replace("{name}", target.getName());
        text = translateAlternateColorCodes('&', text);
        user.sendMessage(text);
    }

}