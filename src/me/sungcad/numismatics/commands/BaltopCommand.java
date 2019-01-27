package me.sungcad.numismatics.commands;

import static me.sungcad.numismatics.tools.MoneyParser.format;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.sungcad.numismatics.NumismaticsPlugin;
import me.sungcad.numismatics.tools.ValueComparator;

public class BaltopCommand implements CommandExecutor {
    private static HashMap<String, Double> unsorted = new HashMap<String, Double>();
    private static HashMap<String, Double> loading = new HashMap<String, Double>();
    private static boolean updating = false;
    private static BukkitTask updatetask;
    private boolean rounded;
    private NumismaticsPlugin plugin;

    public BaltopCommand(boolean rounded, NumismaticsPlugin plugin) {
        this.plugin = plugin;
        this.rounded = rounded;
        if (updatetask == null) {
            updatetask = new BukkitRunnable() {
                @Override
                public void run() {
                    update();
                }
            }.runTaskTimerAsynchronously(NumismaticsPlugin.getPlugin(), 1, plugin.getConfig().getLong("baltop.update") * 20);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdName, String[] args) {
        TreeMap<String, Double> sorted_map = new TreeMap<>(new ValueComparator(unsorted));
        sorted_map.putAll(unsorted);
        if (plugin.getConfig().getBoolean("baltop.online") && !sender.hasPermission("vanish.see")) {
            if (NumismaticsPlugin.getVanishManager() != null) {
                for (Player p : NumismaticsPlugin.getVanishManager().getVanishedPlayers()) {
                    sorted_map.remove(p.getName());
                }
            }
        }
        sendMessage(sender, translateAlternateColorCodes('&', plugin.getConfig().getString("baltop.format.first")));
        int page = 1;
        if (args.length != 0)
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                page = 1;
            }
        int playercount = plugin.getConfig().getInt("baltop.players");
        int playernumber = 1;
        if (page > 1)
            for (int i = 1; i <= playercount * (page - 1) && sorted_map.keySet().size() > playercount; i++, playernumber++)
                sorted_map.pollFirstEntry();
        for (int i = 1; i <= playercount && !sorted_map.isEmpty(); i++, playernumber++)
            sendMessage(sender, formatPlayer(sorted_map.pollFirstEntry(), playernumber));
        if (plugin.getConfig().getString("baltop.format.last").length() > 0)
            sendMessage(sender, translateAlternateColorCodes('&', plugin.getConfig().getString("baltop.format.last")));
        return true;
    }

    private static void update() {
        if (!updating) {
            updating = true;
            if (NumismaticsPlugin.getPlugin().getConfig().getBoolean("baltop.online"))
                for (Player p : Bukkit.getOnlinePlayers())
                    loading.put(p.getName(), NumismaticsPlugin.getEconomy().getBalance(p));
            else
                for (OfflinePlayer p : Bukkit.getOfflinePlayers())
                    loading.put(p.getName(), NumismaticsPlugin.getEconomy().getBalance(p));
            unsorted.clear();
            unsorted.putAll(loading);
            loading.clear();
            updating = false;
        }
    }

    public static void reload() {
        updatetask.cancel();
        updatetask = new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        }.runTaskTimerAsynchronously(NumismaticsPlugin.getPlugin(), 1, NumismaticsPlugin.getPlugin().getConfig().getLong("baltop.update") * 20);
    }

    private String formatPlayer(Entry<String, Double> player, int number) {
        String format = plugin.getConfig().getString("baltop.format.player");
        format = format.replace("{name}", player.getKey());
        format = format.replace("{number}", Integer.toString(number));
        format = format.replace("{balance}", format(player.getValue(), rounded));
        format = translateAlternateColorCodes('&', format);
        return format;
    }

    private void sendMessage(CommandSender reciever, String message) {
        if (!message.isEmpty()) {
            reciever.sendMessage(message);
        }
    }
}