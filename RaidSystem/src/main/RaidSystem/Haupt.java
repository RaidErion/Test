package main.RaidSystem;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import beruf_miner.CommandSpitzhacke;
import beruf_miner.CommandVeredeln;
import beruf_pelzner.CommandGerben;
import beruf_schmied.CommandHaerten;
import beruf_schmied.CommandVerhaerten;
import beruf_schmied.CommandZerlegen;
import beruf_schmied_juwelier.CommandSchleifen;
import berufe.CommandBerufBoni;
import berufe.CommandBerufInfo;
import berufe.CommandBerufe;
import commands.CommandCharacter;
import commands.CommandHebel;
import commands.CommandInventar;
import commands.CommandWochenQuest;
import commands_op.CommandCheat;
import commands_op.CommandGetBeruf;
import commands_op.CommandGetFraktion;
import commands_op.CommandItem;
import commands_op.CommandRaidReload;
import commands_op.CommandSetBeruf;
import commands_op.CommandSetFraktion;
import commands_op.CommandSign;
import economy.CommandMoney;
import economy.CommandMoneyTop;
import economy.CommandPay;
import economy.CommandPunkte;
import economy.CommandTaler;
import fraktionbereich.CommandFraktion;
import listener.ListenerCreatureSpawn;
import listener.ListenerEntityDeath;
import listener.ListenerEntityExplodeEvent;
import listener.ListenerInteract;
import listener.ListenerInventarClick;
import listener.ListenerInventoryClose;
import listener.ListenerJoin;
import listener.ListenerMove;
import listener.ListenerQuit;
import listener.ListenerSchaden;
import mysql.MySQL;

public class Haupt extends JavaPlugin{
	
	public static List<UUID> fraktion_tempelritter = new ArrayList<UUID>();
	public static List<UUID> fraktion_sonnenkrieger = new ArrayList<UUID>();
	
	@Override
	public void onDisable() {
		MySQL.disconnect();
	}

	@Override
	public void onEnable() {
		enableDatabase();
		registerListener();
		registerCommands();
	}

	private void enableDatabase() {
		MySQL.connect();
		try {
			PreparedStatement psFraktionen = MySQL.getCon().prepareStatement("CREATE TABLE IF NOT EXISTS Fraktionen (UUID VARCHAR(100),Spielername VARCHAR(20),Fraktion VARCHAR(15),PRIMARY KEY (UUID))");
			PreparedStatement psBerufe = MySQL.getCon().prepareStatement("CREATE TABLE IF NOT EXISTS Berufe (UUID VARCHAR(100),Spielername VARCHAR(20),Beruf VARCHAR(50),Level INT(2), Punkte INT(3),Rezepte TEXT,PRIMARY KEY (UUID))");
			PreparedStatement psBooks = MySQL.getCon().prepareStatement("CREATE TABLE IF NOT EXISTS Books (Name VARCHAR(100),Author VARCHAR(20),Title VARCHAR(50),Pages TEXT,PRIMARY KEY (Name))");
			PreparedStatement psWochenQuest = MySQL.getCon().prepareStatement("CREATE TABLE IF NOT EXISTS WochenQuest (UUID VARCHAR(100),Spielername VARCHAR(20),Abgaben INT(10),PRIMARY KEY (UUID))");
			PreparedStatement psInsertWQ = MySQL.getCon().prepareStatement("INSERT IGNORE INTO WochenQuest (UUID,Spielername,Abgaben) VALUES (\"AKTUELL\",\"Steine\",0)");
			PreparedStatement psTagesQuests = MySQL.getCon().prepareStatement("CREATE TABLE IF NOT EXISTS TagesQuests (UUID VARCHAR(100),Spielername VARCHAR(20),LastSammelQuest INT(4),LastMonsterQuest INT(4),AktuelleMonsterStufe INT(1),GekillteMonster INT(4),MaxMonsterStufe INT(1),PRIMARY KEY (UUID))");

			PreparedStatement psCharakter = MySQL.getCon().prepareStatement("CREATE TABLE IF NOT EXISTS Charakter (UUID VARCHAR(100),Spielername VARCHAR(20),Hals INT(3),HandschuhLinks INT(3),HandschuhRechts INT(3),Umhang INT(3),RingLinks INT(3),RingRechts INT(3),NeuLinks INT(3),NeuRechts INT(3),PRIMARY KEY (UUID))");
			PreparedStatement psCharakterItems = MySQL.getCon().prepareStatement("CREATE TABLE IF NOT EXISTS CharakterItems (ID INT(4),Material VARCHAR(100),Name VARCHAR(100),Effekt TEXT,PRIMARY KEY (ID))");
			
			PreparedStatement psScore = MySQL.getCon().prepareStatement("CREATE TABLE IF NOT EXISTS Score (UUID VARCHAR(100),Spielername VARCHAR(20),Ehre INT(100),PRIMARY KEY (UUID))");
			PreparedStatement psEconomy = MySQL.getCon().prepareStatement("CREATE TABLE IF NOT EXISTS Economy (UUID VARCHAR(100),Spielername VARCHAR(100),RaidCoins INT(100),Glanzpunkte INT(100),Heldenpunkte INT(100), Taler INT(100), VotePunkte INT(4), LastVote INT(3),PRIMARY KEY (UUID))");
			
			
			psFraktionen.executeUpdate();
			psBerufe.executeUpdate();
			psBooks.executeUpdate();
			psWochenQuest.executeUpdate();
			psInsertWQ.executeUpdate();
			psTagesQuests.executeUpdate();
			
			psCharakter.executeUpdate();
			psCharakterItems.executeUpdate();
			
			psScore.executeUpdate();
			psEconomy.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void registerListener() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new ListenerJoin(), this);
		pm.registerEvents(new ListenerQuit(), this);
		pm.registerEvents(new ListenerSchaden(), this);
		pm.registerEvents(new ListenerInteract(), this);
		pm.registerEvents(new ListenerInventarClick(), this);
		pm.registerEvents(new ListenerCreatureSpawn(), this);
		pm.registerEvents(new ListenerEntityDeath(), this);
		pm.registerEvents(new ListenerInventoryClose(), this);
		pm.registerEvents(new ListenerMove(), this);
		pm.registerEvents(new ListenerEntityExplodeEvent(), this);
	}
	
	private void registerCommands() {
		this.getCommand("inventar").setExecutor(new CommandInventar());
		this.getCommand("hebel").setExecutor(new CommandHebel());
		this.getCommand("getfraktion").setExecutor(new CommandGetFraktion());
		this.getCommand("setfraktion").setExecutor(new CommandSetFraktion());
		this.getCommand("item").setExecutor(new CommandItem());
		this.getCommand("cheat").setExecutor(new CommandCheat());
		this.getCommand("raidreload").setExecutor(new CommandRaidReload());
		this.getCommand("sign").setExecutor(new CommandSign());
		// Berufe - Allgemein
		this.getCommand("beruf").setExecutor(new CommandBerufe());
		this.getCommand("berufinfo").setExecutor(new CommandBerufInfo());
		this.getCommand("berufboni").setExecutor(new CommandBerufBoni());
		this.getCommand("getberuf").setExecutor(new CommandGetBeruf());
		this.getCommand("setberuf").setExecutor(new CommandSetBeruf());
		// Beruf - Miner
		this.getCommand("spitzhacke").setExecutor(new CommandSpitzhacke());
		this.getCommand("veredeln").setExecutor(new CommandVeredeln());
		// Beruf - Pelzner
		this.getCommand("gerben").setExecutor(new CommandGerben());
		// Beruf - Schmied
		this.getCommand("verhaerten").setExecutor(new CommandVerhaerten());
		this.getCommand("zerlegen").setExecutor(new CommandZerlegen());
		this.getCommand("haerten").setExecutor(new CommandHaerten());
		this.getCommand("schleifen").setExecutor(new CommandSchleifen());
		// Commands
		this.getCommand("character").setExecutor(new CommandCharacter());
		this.getCommand("wochenquest").setExecutor(new CommandWochenQuest());
		// Economy
		this.getCommand("money").setExecutor(new CommandMoney());
		this.getCommand("moneytop").setExecutor(new CommandMoneyTop());
		this.getCommand("punkte").setExecutor(new CommandPunkte());
		this.getCommand("taler").setExecutor(new CommandTaler());
		this.getCommand("pay").setExecutor(new CommandPay());
		
		// Fraktionen
		this.getCommand("fraktion").setExecutor(new CommandFraktion());
	}
	
	public static Plugin getPlugin() {
		return Bukkit.getPluginManager().getPlugin("RaidSystem");
	}
}
