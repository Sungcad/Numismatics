package me.sungcad.numismatics.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import static org.bukkit.ChatColor.*;

import me.sungcad.numismatics.NumismaticsPlugin;

public class NumismaticsCommand implements CommandExecutor {
    private NumismaticsPlugin plugin;

    public NumismaticsCommand(NumismaticsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdname, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("info")) {
            if (sender instanceof ConsoleCommandSender || sender.hasPermission("numismatics.info")) {
                sender.sendMessage(GREEN + plugin.getDescription().getName() + " " + GRAY + plugin.getDescription().getDescription());
                sender.sendMessage((GRAY + "Version " + YELLOW + plugin.getDescription().getVersion() + GRAY + " by" + YELLOW + plugin.getDescription().getAuthors()).replace('[', ' ').replace(']', ' '));
            } else
                sender.sendMessage(cmd.getPermissionMessage());
            return true;
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof ConsoleCommandSender || sender.hasPermission("numismatics.reload")) {
                plugin.reloadConfig();
                BaltopCommand.reload();
                sender.sendMessage(DARK_GRAY + "[" + GREEN + plugin.getDescription().getPrefix() + DARK_GRAY + "] " + GRAY + "The config has been reloaded");
            } else
                sender.sendMessage(cmd.getPermissionMessage());
            return true;
        } else if (args[0].equalsIgnoreCase("help")) {
            Command command;
            if (args.length > 1) {
                for (String s : plugin.getDescription().getCommands().keySet()) {
                    command = plugin.getCommand(s);
                    if (args[1].equalsIgnoreCase(s) || command.getAliases().contains(args[1])) {
                        sender.sendMessage(YELLOW + command.getName().substring(0, 1).toUpperCase() + command.getName().substring(1) + DARK_GRAY + " - " + GRAY + command.getDescription());
                        sender.sendMessage(GRAY + "Usage: " + command.getUsage().replace("<command>", command.getName()));
                        sender.sendMessage(GRAY + "Aliases: " + command.getAliases().toString().replaceAll("\\[|\\]", ""));
                        sender.sendMessage(GRAY + "Permission: " + command.getPermission());
                        return true;
                    }
                }
            } else {
                sender.sendMessage(GRAY + "Commands for " + GREEN + plugin.getDescription().getName());
                sender.sendMessage(GRAY + "For help with a specific command use /" + cmdname + " help (command)");
                for (String s : plugin.getDescription().getCommands().keySet()) {
                    command = plugin.getCommand(s);
                    if (sender.hasPermission(command.getPermission()))
                        sender.sendMessage(YELLOW + "/" + command.getName().substring(0, 1).toUpperCase() + command.getName().substring(1) + DARK_GRAY + " - " + GRAY + command.getDescription());
                }
            }
            return true;
        }
        return false;
    }
}