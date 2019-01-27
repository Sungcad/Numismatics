package me.sungcad.numismatics.commands;

import static me.sungcad.numismatics.tools.MoneyParser.format;
import static me.sungcad.numismatics.tools.MoneyParser.parse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sungcad.numismatics.NumismaticsPlugin;

public class EconomyCommand implements CommandExecutor {

	private NumismaticsPlugin plugin;

	public EconomyCommand(NumismaticsPlugin plugin) {
		this.plugin = plugin;
	}

	private enum Type {
		error, give, set, take
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdname, String[] args) {
		if (args.length == 3) {
			Type type;
			try {
				type = Type.valueOf(args[0].toLowerCase());
			} catch (IllegalArgumentException | NullPointerException e) {
				type = Type.error;
			}
			double amount = parse(args[2]);
			if (amount < 0) {
				sendMessage(sender, cmd.getUsage().replace("<command>", cmdname));
				return true;
			}
			Player player = Bukkit.getPlayer(args[1]);
			if ((player instanceof Player)) {
				runCommand(player, amount, type, sender, cmd.getUsage().replace("<command>", cmdname));
			} else if (args[1].equals("*")) {
				for (Player target : Bukkit.getOnlinePlayers()) {
					runCommand(target, amount, type, sender, cmd.getUsage().replace("<command>", cmdname));
				}
			} else {
				sendMessage(sender, plugin.getConfig().getString("economy.error.playernotfound"));
			}
			return true;
		}
		sendMessage(sender, cmd.getUsage().replace("<command>", cmdname));
		return true;
	}

	private void runCommand(Player player, double amount, Type type, CommandSender sender, String usage) {
		double balance = NumismaticsPlugin.getEconomy().getBalance(player);
		switch (type) {
			case set :
				double change = balance > amount ? balance - amount : amount - balance;
				if (balance > amount)
					NumismaticsPlugin.getEconomy().withdrawPlayer(player, change);
				else
					NumismaticsPlugin.getEconomy().depositPlayer(player, change);
				sendMessage(sender, parseString("economy.set.sender", player, amount));
				sendMessage(player, parseString("economy.set.receiver", player, amount));
				break;
			case give :
				NumismaticsPlugin.getEconomy().depositPlayer(player, amount);
				sendMessage(sender, parseString("economy.give.sender", player, amount));
				sendMessage(player, parseString("economy.give.receiver", player, amount));
				break;
			case take :
				amount = balance >= amount ? amount : balance;
				NumismaticsPlugin.getEconomy().withdrawPlayer(player, amount);
				sendMessage(sender, parseString("economy.take.sender", player, amount));
				sendMessage(player, parseString("economy.take.receiver", player, amount));
				break;
			default :
				sendMessage(sender, usage);
		}
	}

	private String parseString(String string, Player player, double amount) {
		String text = plugin.getConfig().getString(string);
		text = text.replace("{amount}", format(amount, true));
		text = text.replace("{receiver}", player.getName());
		return text;
	}

	private void sendMessage(CommandSender reciever, String message) {
		if (!message.isEmpty()) {
			reciever.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
		}
	}
}