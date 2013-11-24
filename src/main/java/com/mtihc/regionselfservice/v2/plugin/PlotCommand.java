package com.mtihc.regionselfservice.v2.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.regionselfservice.v2.plots.Permission;
import com.mtihc.regionselfservice.v2.plots.Plot;
import com.mtihc.regionselfservice.v2.plots.PlotData;
import com.mtihc.regionselfservice.v2.plots.PlotManager;
import com.mtihc.regionselfservice.v2.plots.PlotWorld;
import com.mtihc.regionselfservice.v2.plots.exceptions.PlotControlException;
import com.mtihc.regionselfservice.v2.plugin.util.commands.Command;
import com.mtihc.regionselfservice.v2.plugin.util.commands.CommandException;
import com.mtihc.regionselfservice.v2.plugin.util.commands.ICommand;
import com.mtihc.regionselfservice.v2.plugin.util.commands.SimpleCommand;

public class PlotCommand extends SimpleCommand {

	private PlotManager mgr;

	public PlotCommand(PlotManager manager, ICommand parent, String[] aliases) {
		super(parent, aliases, "", "This is the main plot command.", new String[0], null);
		
		this.mgr = manager;
		
		// setNested
		addNested("buy");
		addNested("count");
		addNested("define");
		addNested("defineExact");
		addNested("redefine");
		addNested("redefineExact");
		addNested("delete");
		addNested("info");
		addNested("reload");
		addNested("rent");
		//addNested("sell");
		addNested("worth");
	}
	
	private Player getPlayer(CommandSender sender) throws CommandException {
		if(!(sender instanceof Player)) {
			throw new CommandException("This command must be executed by a player, in game.");
		}
		return (Player) sender;
	}

	@Command(aliases = { "buy", "claim" }, args = "", desc = "Buy a region.", help = { "" }, perm = Permission.BUY)
	public void buy(CommandSender sender, String[] args) throws CommandException {
		Player player = getPlayer(sender);
		try {
			mgr.getControl().buy(player);
		} catch (PlotControlException e) {
			throw new CommandException(e.getMessage());
		}
	}
	
	@Command(aliases = { "count" }, args = "[player] [world]", desc = "Count regions.", help = { "Specify a player name, to count another player's regions.", "Specify a world name, to count regions in another world." }, perm = Permission.COUNT)
	public void count(CommandSender sender, String[] args) throws CommandException {

		String playerName;
		OfflinePlayer player;
		try {
			// defined player argument
			playerName = args[0];
			player = null;
		} catch (Exception e) {
			// didn't define player argument
			if (sender instanceof Player) {
				// sender is player,
				// player will be sender himself
				player = (Player) sender;
				playerName = player.getName();
			} else {
				// sender is not player, so no player found
				throw new CommandException(
						"Incorrect number of arguments. Expected player name.");
			}
		}
		
		if (player == null) {
			// sender specified offline/online player
			player = sender.getServer().getOfflinePlayer(playerName);
			if (player != null) {
				playerName = player.getName();
			}
		}

		if (player == null) {
			// still no player found
			throw new CommandException("Player " + "'" + playerName + "'"
					+ " doesn't exist.");
		}
		// we have a player

		World world;
		String worldName;
		try {
			worldName = args[1];
			world = null;
		} catch (Exception e) {
			if (sender instanceof Player) {
				// sender is player and didn't specify a world,
				// world will be sender's world
				world = ((Player) sender).getWorld();
				worldName = world.getName();
			} else {
				throw new CommandException("Incorrect number of arguments. Expected world name.");
			}
		}

		if (world == null) {
			world = sender.getServer().getWorld(worldName);
			if (world == null) {
				throw new CommandException("World " + "'" + worldName + "'"
						+ " doesn't exist.");
			}
		}

		mgr.getControl().sendRegionCount(sender, player, world);
	}
	
	@Command(aliases = { "define", "set", "create" }, args = "<id>", desc = "Create a new region.", help = { "The top-y and bottom-y will be set according to the world's configuration." }, perm = Permission.CREATE)
	public void define(CommandSender sender, String[] args) throws CommandException {
		
		Player player = getPlayer(sender);
		
		String regionId;
		try {
			regionId = args[0];
		} catch(Exception e) {
			throw new CommandException("Expected a region id.");
		}
		
		if(args.length > 1) {
			throw new CommandException("Expected only a region id.");
		}
		
		try {
			mgr.getControl().define(player, regionId);
		} catch (PlotControlException e) {
			throw new CommandException(e.getMessage());
		}
	}
	
	@Command(aliases = { "define-exact", "set-exact", "create-exact" }, args = "<id> [bottom-y] [top-y]", desc = "Create a new region.", help = { "The top-y and bottom-y will be set according to your region selection. ", "Or specify the top-y and bottom-y as command arguments." }, perm = Permission.CREATE_EXACT)
	public void defineExact(CommandSender sender, String[] args) throws CommandException {
		Player player = getPlayer(sender);
		
		String regionId;
		try {
			regionId = args[0];
		} catch(Exception e) {
			throw new CommandException("Expected a region id.");
		}
		
		int bottomY;
		try {
			bottomY = Integer.parseInt(args[1]);
		} catch(NumberFormatException e) {
			throw new CommandException("Expected a number instead of text for the bottom-y coordinate..");
		} catch(Exception e) {
			bottomY = -1;
		}
		
		int topY;
		try {
			topY = Integer.parseInt(args[2]);
		} catch(NumberFormatException e) {
			throw new CommandException("Expected a number instead of text for the top-y coordinate.");
		} catch(Exception e) {
			topY = -1;
		}
		
		try {
			mgr.getControl().define(player, regionId, bottomY, topY);
		} catch (PlotControlException e) {
			throw new CommandException(e.getMessage());
		}
	}
	
	@Command(aliases = { "redefine", "reset", "resize" }, args = "<id>", desc = "Change a region.", help = { "The top-y and bottom-y will be set according to the world's configuration." }, perm = Permission.REDEFINE)
	public void redefine(CommandSender sender, String[] args) throws CommandException {
		Player player = getPlayer(sender);
		
		String regionId;
		try {
			regionId = args[0];
		} catch(Exception e) {
			throw new CommandException("Expected a region id.");
		}
		
		if(args.length > 1) {
			throw new CommandException("Expected only a region id.");
		}
		
		try {
			mgr.getControl().redefine(player, regionId);
		} catch (PlotControlException e) {
			throw new CommandException(e.getMessage());
		}
	}
	
	@Command(aliases = { "redefine-exact", "reset-exact", "resize-exact" }, args = "<id> [bottom-y] [top-y]", desc = "Change a region.", help = { "The top-y and bottom-y will be set according to your region selection. ", "Or specify the top-y and bottom-y as command arguments." }, perm = Permission.REDEFINE_EXACT)
	public void redefineExact(CommandSender sender, String[] args) throws CommandException {
		Player player = getPlayer(sender);
		
		String regionId;
		try {
			regionId = args[0];
		} catch(Exception e) {
			throw new CommandException("Expected a region id.");
		}
		
		int bottomY;
		try {
			bottomY = Integer.parseInt(args[1]);
		} catch(NumberFormatException e) {
			throw new CommandException("Expected a number instead of text for the bottom-y coordinate..");
		} catch(Exception e) {
			bottomY = -1;
		}
		
		int topY;
		try {
			topY = Integer.parseInt(args[2]);
		} catch(NumberFormatException e) {
			throw new CommandException("Expected a number instead of text for the top-y coordinate.");
		} catch(Exception e) {
			topY = -1;
		}
		
		try {
			mgr.getControl().redefine(player, regionId, bottomY, topY);
		} catch (PlotControlException e) {
			throw new CommandException(e.getMessage());
		}
	}
	
	@Command(aliases = { "delete", "remove" }, args = "<id>", desc = "Delete a region.", help = { "All for-sale signs will break automatically.", "Regions can only be deleted when ", "they are not being rented out anymore." }, perm = Permission.REMOVE)
	public void delete(CommandSender sender, String[] args) throws CommandException {
		
		String regionId;
		try {
			regionId = args[0];
		} catch(Exception e) {
			throw new CommandException("Expected a region id.");
		}
		
		World world = null;
		if(args.length > 1) {
			try {
				world = Bukkit.getWorld(args[1]);
			} catch(Exception e) {
				world = null;
			}
			
			if(args.length > 2) {
				throw new CommandException("Expceted only a region id. And optionally a world name.");
			}
			
		}
		
		if(world == null) {
			if(!(sender instanceof Player)) {
				throw new CommandException("Expected a region id and world name.");
			}
			world = ((Player) sender).getWorld();
		}
		
		
		try {
			mgr.getControl().delete(sender, world, regionId);
		} catch (PlotControlException e) {
			throw new CommandException(e.getMessage());
		}
	}
	
	@Command(aliases = { "info" }, args = "<id> [world]", desc = "Get region info.", help = { "Click a sign, or use this command to show region info. "}, perm = Permission.INFO)
	public void info(CommandSender sender, String[] args) throws CommandException {
		String regionId;
		try {
			regionId = args[0];
		} catch(Exception e) {
			throw new CommandException("Expected a region id.");
		}
		
		World world = null;
		if(args.length > 1) {
			try {
				world = Bukkit.getWorld(args[1]);
			} catch(Exception e) {
				world = null;
			}
			
			if(args.length > 2) {
				throw new CommandException("Expceted only a region id. And optionally a world name.");
			}
			
		}
		
		if(world == null) {
			if(!(sender instanceof Player)) {
				throw new CommandException("Expected a region id and world name.");
			}
			world = ((Player) sender).getWorld();
		}
		
		PlotWorld plotWorld = mgr.getPlotWorld(world.getName());
		Plot plot = plotWorld.getPlot(regionId);
		if(plot == null) {
			plot = new Plot(plotWorld, new PlotData(regionId, 0, 0, 0));
		}
		plot.sendInfo(sender);
	}
	
	// TODO add [world] argument to reload command?
	@Command(aliases = { "reload" }, args = "", desc = "Reload the configuration.", help = { "" }, perm = Permission.RELOAD)
	public void reload(CommandSender sender, String[] args) throws CommandException {
		
		if(args != null && args.length != 0) {
			throw new CommandException("Expected no arguments.");
		}
		
		mgr.getPlugin().reloadConfig();
		sender.sendMessage(ChatColor.GREEN + "Configuration reloaded.");
	}
	
	@Command(aliases = { "rent" }, args = "", desc = "Rent a region", help = { "" }, perm = Permission.RENT)
	public void rent(CommandSender sender, String[] args) throws CommandException {
		Player player = getPlayer(sender);
		try {
			mgr.getControl().rent(player);
		} catch (PlotControlException e) {
			throw new CommandException(e.getMessage());
		}
	}
	
	// TODO sell command to define and place a sign in one go
	/*public void sell(CommandSender sender, String[] args) {
		
	}*/
	
	@Command(aliases = { "worth" }, args = "<id> | <width> <length> | <money>", desc = "See how much a region is worth. Or see how big of a region your money can buy.", help = { "Specify a region id, to see how much it's worth.", "Specify a width and length, to see how much it would be worth.", "Specify an amount of money, to see how big of a region you can afford." }, perm = Permission.WORTH)
	public void worth(CommandSender sender, String[] args) throws CommandException {
		
		double arg1;
		try {
			// try to get argument 1 (width or money)
			arg1 = Double.parseDouble(args[0]);
		} catch (IndexOutOfBoundsException e) {
			throw new CommandException("Incorrect number of arguments. Expected at least 1 number or a region name.");
		} catch (NumberFormatException e) {
			// argument 1 is not a number, so it must be a region name
			String regionName = args[0];
			World world;
			String worldName;
			// second must be a world name, if not a player
			try {
				worldName = args[1];
				world = sender.getServer().getWorld(worldName);
				if(world == null) {
					throw new CommandException("World '" + world + "' doesn't exist.");
				}
			} catch(IndexOutOfBoundsException exception) {
				// no world argument defined
				if(sender instanceof Player) {
					// take player's world
					world = ((Player)sender).getWorld();
					worldName = world.getName();
				}
				else {
					// if not player, send error message
					throw new CommandException("Incorrect number or arguments. Expected world name.");
				}
				
			}
			mgr.getControl().sendWorth(sender, regionName, world);
			return;
		}

		double arg2;
		try {
			// try to get argument 2 (height or undefined)
			arg2 = Double.parseDouble(args[1]);
		} catch (IndexOutOfBoundsException e) {
			arg2 = -1;
		} catch (NumberFormatException e) {
			arg2 = -1;
		}

		
		// check existance of second argument
		if (arg2 == -1) {
			
			
			// get the world again
			World world;
			String worldName;
			// second must be a region name, if not a player
			try {
				worldName = args[1];
				world = sender.getServer().getWorld(worldName);
				if(world == null) {
					throw new CommandException("World '" + world + "' doesn't exist.");
				}
			} catch(IndexOutOfBoundsException exception) {
				// no world argument defined
				if(sender instanceof Player) {
					// take player's world
					world = ((Player)sender).getWorld();
					worldName = world.getName();
				}
				else {
					// if not player, send error message
					throw new CommandException("Incorrect number or arguments. Expected world name.");
				}
				
			}
			
			
			// show how big a region you can get for <arg1> money.
			mgr.getControl().sendWorth(sender, arg1, world);
		} else {
			
			
			// get the world again
			World world;
			String worldName;
			// second must be a region name, if not a player
			try {
				worldName = args[1];
				world = sender.getServer().getWorld(worldName);
				if(world == null) {
					throw new CommandException("World '" + world + "' doesn't exist.");
				}
			} catch(IndexOutOfBoundsException exception) {
				// no world argument defined
				if(sender instanceof Player) {
					// take player's world
					world = ((Player)sender).getWorld();
					worldName = world.getName();
				}
				else {
					// if not player, send error message
					throw new CommandException("Incorrect number or arguments. Expected world name.");
				}
				
			}
			
			int width = (int) arg1;
			int length = (int) arg2;
			// show how much money you need for a region of size <arg1> x <arg2>
			mgr.getControl().sendWorth(sender, width, length, world);
		}
		
	}
	
}
