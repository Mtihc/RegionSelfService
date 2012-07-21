package com.mtihc.minecraft.regionselfservice.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class SimpleCommand {

	public static String getUniqueName(SimpleCommand command) {
		String result = command.getLabel().toLowerCase();
		if (command.hasParent()) {
			return SimpleCommand.getUniqueName(command.getParent()) + " " + result;
		} else {
			return result;
		}
	}

	private final SimpleCommand parent;
	private final String label;
	private List<String> aliases;
	private final String permission;
	private final String permissionMessage;
	private final String argumentSyntax;
	private final String description;

	private String[] longDescription;

	public SimpleCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission, String permissionMessage, String argumentSyntax, String description) {

		this.parent = parent;
		this.label = label.toLowerCase();
		this.aliases = aliases;
		this.permission = permission;
		this.permissionMessage = permissionMessage;
		this.argumentSyntax = argumentSyntax;
		this.description = description;

	}
	
	public String getPermission() {
		return permission;
	}
	
	public String getPermissionMessage() {
		if(permissionMessage != null) {
			return ChatColor.RED + permissionMessage;
		}
		else {
			return ChatColor.RED + "You don't have permission for command: " + ChatColor.WHITE + getUsage();
		}
	}
	
	protected boolean testPermission(CommandSender sender) {
		if(testPermissionSilent(sender)) {
			return true;
		}
		else {
			sender.sendMessage(getPermissionMessage());
			return false;
		}
	}
	
	protected boolean testPermissionSilent(CommandSender sender) {
		if(permission == null || permission.isEmpty() || sender.hasPermission(permission)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * @return the parent
	 */
	public SimpleCommand getParent() {
		return parent;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the aliases for the label
	 */
	public List<String> getAliases() {
		return aliases;
	}

	/**
	 * 
	 * @param aliases
	 *            the aliases for the label
	 */
	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

	/**
	 * The argument syntax.
	 * 
	 * <p>
	 * For example:
	 * 
	 * <pre>
	 * &lt;mandatory&gt; &lt;mandatory&gt; [optional]
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @return the argument syntax
	 */
	public String getArgumentSyntax() {
		return argumentSyntax;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	public String getUniqueName() {
		return SimpleCommand.getUniqueName(this);
	}

	public String getUsage() {
		return "/" + getUniqueName() + " " + argumentSyntax;
	}

	protected abstract boolean onCommand(CommandSender sender, String label,
			String[] args);

	protected void sendHelp(CommandSender sender, String[] args) {
		int page;
		if (args == null || args.length == 0) {
			page = -1;
		} else if (args.length == 1) {
			try {
				page = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Unknown command: /"
						+ getUniqueName() + " " + args[0]);
				sender.sendMessage(getUsage());
				return;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Unknown command: /"
					+ getUniqueName() + " " + args[0]);
			sender.sendMessage(getUsage());
			return;
		}
		sendHelp(sender, page);
	}

	protected void sendHelp(CommandSender sender, int page) {
		List<String> help = getHelp(sender);
		if (page < 1) {
			sender.sendMessage(ChatColor.GREEN + "Command help:");
			for (int i = 0; i < help.size(); i++) {
				String line = help.get(i);
				sender.sendMessage(line);
			}
		} else {
			int total = help.size();
			int totalPerPage = 10;
			int startIndex = (page - 1) * totalPerPage;
			int endIndex = startIndex + totalPerPage;

			int totalPages = (int) Math.ceil((float) total / totalPerPage);
			if (page > totalPages || page < 1) {
				sender.sendMessage(ChatColor.RED + "Page " + page
						+ " does not exist.");
				return;
			}
			if (totalPages > 1) {
				sender.sendMessage(ChatColor.GREEN + "Command Help (page "
						+ page + "/" + totalPages + "):");
			} else {
				sender.sendMessage(ChatColor.GREEN + "Command help:");
			}

			for (int i = startIndex; i < endIndex && i < total; i++) {
				String line = help.get(i);
				sender.sendMessage(line);
			}
		}

	}

	protected List<String> getHelp(CommandSender sender) {
		List<String> help = new ArrayList<String>();
		help.add(ChatColor.GREEN + "Usage: " + ChatColor.WHITE + getUsage());
		if (hasLongDescription()) {
			help.add(ChatColor.GREEN + "Description: ");
			for (String line : longDescription) {
				help.add(line);
			}
		} else {
			help.add(ChatColor.GREEN + "Description: " + ChatColor.WHITE
					+ description);
		}

		if (hasNested()) {
			help.add(ChatColor.GREEN + "Nested commands: ");
			String[] cmds = getNestedCommandLabels();
			for (int i = 0; i < cmds.length; i++) {
				SimpleCommand cmd = getNested(cmds[i]);
				if(cmd.testPermissionSilent(sender)) {
					help.add("  " + cmd.getUsage());
				}
			}
		}

		return help;
	}

	public boolean execute(CommandSender sender, String label, String[] args) {

		if (args.length > 0) {

			if (hasNested()) {
				SimpleCommand nestedCommand = getNested(args[0]);

				if (nestedCommand != null) {
					return nestedCommand.execute(sender, label,
							Arrays.copyOfRange(args, 1, args.length));
				}
			}

			
			if (args[0].equalsIgnoreCase("help") || args[0].equals("?")) {
				if(!testPermission(sender)) {
					return false;
				}
				int page;
				try {
					page = Integer.parseInt(args[1]);
				} catch (Exception e) {
					page = 1;
				}
				if (page < 1) {
					page = 1;
				}
				sendHelp(sender, page);
				return true;
			}
		}

		if(!testPermission(sender)) {
			return false;
		}
		return onCommand(sender, label, args);

	}

	public boolean hasLongDescription() {
		return longDescription != null && longDescription.length > 0;
	}

	public String[] getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String[] longDescription) {
		this.longDescription = longDescription;
	}

	public boolean hasParent() {
		return parent != null;
	}

	// public boolean hasNested() {
	// return nested != null && !nested.isEmpty();
	// }
	//
	// public SimpleCommand getNested(String label) {
	// return nested.get(label.toLowerCase());
	// }

	public abstract boolean hasNested();

	public abstract SimpleCommand getNested(String labelOrAlias);

	public abstract String[] getNestedCommandLabels();

}
