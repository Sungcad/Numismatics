package me.sungcad.numismatics.tools;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishPlugin;

public class VanishMan {
    private VanishPlugin plugin;

    public VanishMan() {
        load();
    }

    private void load() {
        if (Bukkit.getPluginManager().isPluginEnabled("VanishNoPacket")) {
            plugin = (VanishPlugin) Bukkit.getPluginManager().getPlugin("VanishNoPacket");

        }
    }

    public Set<Player> getVanishedPlayers() {
        if (plugin == null) {
            return new HashSet<>();
        }
        Set<Player> players = new HashSet<Player>();
        for (String s : plugin.getManager().getVanishedPlayers()) {
            players.add(Bukkit.getServer().getPlayer(s));
        }
        return players;
    }
}
