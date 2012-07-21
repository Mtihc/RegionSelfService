package com.mtihc.minecraft.regionselfservice;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import com.mtihc.minecraft.regionselfservice.commands.AcceptCommand;
import com.mtihc.minecraft.regionselfservice.commands.BuyCommand;
import com.mtihc.minecraft.regionselfservice.commands.CountCommand;
import com.mtihc.minecraft.regionselfservice.commands.DefineCommand;
import com.mtihc.minecraft.regionselfservice.commands.DefineExecutor;
import com.mtihc.minecraft.regionselfservice.commands.DeleteCommand;
import com.mtihc.minecraft.regionselfservice.commands.InfoCommand;
import com.mtihc.minecraft.regionselfservice.commands.RedefineCommand;
import com.mtihc.minecraft.regionselfservice.commands.ReloadCommand;
import com.mtihc.minecraft.regionselfservice.commands.RentCommand;
import com.mtihc.minecraft.regionselfservice.commands.SellCommand;
import com.mtihc.minecraft.regionselfservice.commands.WorthCommand;
import com.mtihc.minecraft.regionselfservice.core.SimpleCommand;

public class RegionSelfServiceCommand extends SimpleCommand {

	
	public RegionSelfServiceCommand(RegionSelfServicePlugin plugin) {
		super(null, "selfservice", null, null, "", "", "Reloads the plugin's configuration.");
		
		PluginCommand cmd = plugin.getCommand("selfservice");
		
		this.setAliases(cmd.getAliases());
		
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		
		if(args.length != 0) {
			String cmd = args[0];
			sender.sendMessage(ChatColor.RED + "Unknown command '/" + label + " " + cmd + "'");
			sender.sendMessage(ChatColor.RED + "To get command help, type: " + ChatColor.WHITE + "/" + label + " " + "help");
			return true;
		}

		sendHelp(sender, -1);
		return true;
	}

	@Override
	public boolean hasNested() {
		return true;
	}

	@Override
	public SimpleCommand getNested(String labelOrAlias) {
		Action action = Action.valueByLabelOrAlias(labelOrAlias.toLowerCase());
		if(action == null) {
			return null;
		}
		
		switch(action) {
			case ACCEPT:
				return new AcceptCommand(this);
			case COUNT:
				return new CountCommand(this, Permissions.COUNT);
			case WORTH:
				return new WorthCommand(this, Permissions.WORTH);
			case BUY:
				return new BuyCommand(this, Permissions.BUY);
			case DEFINE:
				return new DefineCommand(this, new DefineExecutor(RegionSelfServicePlugin.getPlugin()), Permissions.CREATE);
			case DELETE:
				return new DeleteCommand(this, Permissions.REMOVE);
			case INFO:
				return new InfoCommand(this, Permissions.INFO);
			case REDEFINE:
				return new RedefineCommand(this, new DefineExecutor(RegionSelfServicePlugin.getPlugin()), Permissions.REDEFINE);
			case RELOAD:
				return new ReloadCommand(this, Permissions.RELOAD);
			case SELL:
				return new SellCommand(this, Permissions.CREATE_AND_SELL);
			case RENT:
				return new RentCommand(this, Permissions.RENT);
			default:
				return null;
		}
	}

	@Override
	public String[] getNestedCommandLabels() {
		Action[] actions = Action.values();
		String[] result = new String[actions.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = actions[i].getLabel();
		}
		return result;
	}
	
	public enum Action {
		
		ACCEPT("accept", new String[]{"yes"}),
		INFO("info"),
		COUNT("count"),
		WORTH("worth"),
		DEFINE("define", new String[]{"set", "create"}),
		REDEFINE("redefine", new String[]{"resize", "reset", "recreate"}),
		DELETE("delete", new String[]{"del", "rem", "remove"}),
		RENT("rent"),
		BUY("buy", new String[]{"claim"}),
		SELL("sell"),
		RELOAD("reload");
		
		private String label;
		private List<String> aliases;
		
		private Action(String label) {
			this(label, null);
		}
		
		private Action(String label, String[] aliases) {
			this.label = label.toLowerCase();
			if(aliases != null) {
				this.aliases = new ArrayList<String>(aliases.length);
				for (int i = 0; i < aliases.length; i++) {
					this.aliases.add(aliases[i].toLowerCase());
				}
			}
		}
		
		
		public String getLabel() {
			return label;
		}
		
		public List<String> getAliases() {
			return aliases;
		}
		
		public boolean hasLabelOrAlias(String lbl) {
			return lbl.equalsIgnoreCase(label) || (aliases != null && aliases.contains(lbl)); 
		}
		
		public static Action valueByLabelOrAlias(String label) {
			String lbl = label.toLowerCase();
			Action[] values = Action.values();
			for (Action action : values) {
				if(action.hasLabelOrAlias(lbl)) {
					return action;
				}
			}
			return null;
		}
	}

	
}
