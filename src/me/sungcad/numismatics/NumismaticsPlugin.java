package me.sungcad.numismatics;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.sungcad.numismatics.commands.BalanceCommand;
import me.sungcad.numismatics.commands.BaltopCommand;
import me.sungcad.numismatics.commands.EconomyCommand;
import me.sungcad.numismatics.commands.NumismaticsCommand;
import me.sungcad.numismatics.commands.PayCommand;
import me.sungcad.numismatics.tools.VanishMan;
import net.milkbowl.vault.economy.Economy;

public class NumismaticsPlugin extends JavaPlugin {
	private static Economy economy;
	private static NumismaticsPlugin instance;
	private static VanishMan vanman;

	public static Economy getEconomy() {
		return economy;
	}

	public static NumismaticsPlugin getPlugin() {
		return instance;
	}

	public static VanishMan getVanishManager() {
		return vanman;
	}

	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		setupCommands();
		setupEconomy();
		setupVanishManager();
	}

	@Override
	public void onDisable() {
		economy = null;
		instance = null;
	}

	private void setupCommands() {
		getCommand("balance").setExecutor(new BalanceCommand(true, this));
		getCommand("ebalance").setExecutor(new BalanceCommand(false, this));
		getCommand("baltop").setExecutor(new BaltopCommand(true, this));
		getCommand("ebaltop").setExecutor(new BaltopCommand(false, this));
		getCommand("economy").setExecutor(new EconomyCommand(this));
		getCommand("numismatics").setExecutor(new NumismaticsCommand(this));
		getCommand("pay").setExecutor(new PayCommand(this));
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
			economy = economyProvider.getProvider();
		return (economy != null);
	}

	private void setupVanishManager() {
		if (Bukkit.getServer().getPluginManager().getPlugin("VanishNoPacket") != null)
			vanman = new VanishMan();
	}
}