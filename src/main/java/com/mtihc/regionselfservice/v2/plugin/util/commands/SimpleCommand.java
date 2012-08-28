package com.mtihc.regionselfservice.v2.plugin.util.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SimpleCommand implements ICommand {

	protected final String label;
	protected List<String> aliases;
	protected String argumentSyntax;
	protected String permission;
	protected String desc;
	protected String[] help;
	
	protected final Map<String, CommandFactory> factories = new LinkedHashMap<String, CommandFactory>();
	protected final Set<String> labels = new LinkedHashSet<String>();
	
	protected final ICommand parent;
	
	public SimpleCommand(ICommand parent, String[] aliases, String argumentSyntax, String permission, String desc, String[] help) {
		
		this.parent = parent;
		
		String[] a;
		try {
			a = Arrays.copyOfRange(aliases, 1, aliases.length);
		} catch(Exception e) {
			a = new String[0];
		}
		this.label = aliases[0];
		this.aliases = Arrays.asList(a);
		this.argumentSyntax = argumentSyntax;
		this.permission = permission;
		this.desc = desc;
		this.help = (help == null ? new String[0] : help);
		
	}


	@Override
	public ICommand getParent() {
		return parent;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public List<String> getAliases() {
		return aliases;
	}

	@Override
	public String getArgumentSyntax() {
		return argumentSyntax;
	}
	
	@Override
	public String getPermission() {
		return permission;
	}

	@Override
	public String getDescription() {
		return desc;
	}

	@Override
	public String getUsage() {
		return getUsage(this);
	}
	
	@Override
	public String getUniqueLabel() {
		return getUniqueLabel(this);
	}
	
	@Override
	public String[] getHelp() {
		return help;
	}
	
	
	@Override
	public ICommand getNested(String label) {
		CommandFactory f = factories.get(label.toLowerCase());
		if(f == null) {
			return null;
		}
		else {
			return f.getCommand();
		}
	}
	
	@Override
	public String[] getNestedLabels() {
		return labels.toArray(new String[labels.size()]);
	}
	
	@Override
	public boolean hasNested() {
		return !labels.isEmpty();
	}
	
	
	
	public void testPermission(CommandSender sender, String perm) throws CommandPermissionException {
		if(!hasPermission(sender, perm)) {
			throw new CommandPermissionException("You don't have permission \"" + perm + "\".");
		}
	}
	
	public boolean hasPermission(CommandSender sender, String perm) {
		if(perm == null || perm.isEmpty()) {
			return true;
		}
		else {
			return sender.hasPermission(perm);
		}
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws CommandException {

		testPermission(sender, getPermission());
		
		String firstArgument;
		if(args.length == 0) {
			// no arguments
			onCommand(sender, args);
			return;
		}
		else {
			firstArgument = args[0];
			// first argument is not help
			ICommand nested = getNested(firstArgument);
			if(nested == null) {
				// first argument is not a nested command
				onCommand(sender, args);
				return;
			}
			else {
				// first argument is nested command
				
				try {
					args = Arrays.copyOfRange(args, 1, args.length);
				} catch(Exception e) {
					args = new String[0];
				}
				
				String help;
				try {
					help = args[0];
					if(help.equalsIgnoreCase("?")) {
						sendHelp(sender, nested);
						return;
					}
				} catch(IndexOutOfBoundsException e) {
					
				}
				
				nested.execute(sender, args);
				return;
			}
		}
	}

	protected void onCommand(CommandSender sender, String[] args) throws CommandException {
		int page;
		try {
			page = Integer.parseInt(args[0]);
		} catch(IndexOutOfBoundsException e) {
			sendHelp(sender, this, -1);
			return;
		} catch(NumberFormatException e) {
			String lbl = getUniqueLabel();
			sender.sendMessage(ChatColor.RED + "Unknown command: " + ChatColor.WHITE + "/" + lbl + " " + args[0]);
			throw new CommandException("To get command help, type: " + ChatColor.WHITE + "/" + lbl + " ?");
		}
		sendHelp(sender, this, page);
	}
	
	
	
	
	
	
	
	
	
	protected final void findNestedCommandMethods() {

		Method[] methods = getClass().getMethods();
		for (Method method : methods) {
			if(method.isAnnotationPresent(Command.class)) {
				setNested(method);
			}
		}
	}
	
	protected <T extends ICommand> void setNested(Class<T> commandClass, Object... args) {
		CommandFactory f = new CommandObjectFactory(commandClass, args);
		
		ICommand cmd = f.getCommand();
		String lbl = cmd.getLabel();
		lbl = lbl.toLowerCase();
		factories.put(lbl, f);
		labels.add(lbl);
		List<String> aliases = cmd.getAliases();
		for (String alias : aliases) {
			factories.put(alias.toLowerCase(), f);
		}
	}
	
	protected void setNested(String methodName) {
		Method method;
		try {
			method = getClass().getMethod(methodName, CommandSender.class, String[].class);
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "Couldn't add method \"" + methodName + "\" as subcommand of \"" + getUsage() + "\"", e);
			return;
		}
		setNested(method);
	}
	
	protected void setNested(Method method) {
		if(!method.isAnnotationPresent(Command.class)) {
			throw new IllegalArgumentException("Method \"" + method.getName() + "\" of class \"" + method.getDeclaringClass().getCanonicalName() + "\" doesn't have the " + Command.class.getName() + " annotation.");
		}
		Command command = method.getAnnotation(Command.class);
		CommandFactory f = new CommandMethodFactory(this, method);
		
		String[] aliases = command.aliases();
		labels.add(aliases[0]);
		for (String alias : aliases) {
			factories.put(alias.toLowerCase(), f);
		}
	}
	

	
	
	
	
	
	
	
	
	
	
	public static String getUsage(ICommand command) {
		return "/" + getUniqueLabel(command) + " " + command.getArgumentSyntax();
	}
	
	public static String getUniqueLabel(ICommand command) {
		String lbl = command.getLabel();
		ICommand cmd = command;
		while(cmd.getParent() != null) {
			cmd = cmd.getParent();
			lbl = cmd.getLabel() + " " + lbl;
			
		}
		return lbl;
	}
	
	public void sendHelp(CommandSender sender, ICommand cmd) throws CommandException {
		sendHelp(sender, cmd, -1);
	}

	public void sendHelp(CommandSender sender, ICommand cmd, int page) throws CommandException {
		
		sender.sendMessage(ChatColor.GRAY + "" + ChatColor.UNDERLINE + "Command:" + ChatColor.WHITE + " " + cmd.getUsage());
		
		if(cmd.getDescription() != null) {
			sender.sendMessage(ChatColor.GRAY + cmd.getDescription());
		}
		
		String[] help = cmd.getHelp();
		if(help != null) {
			for (String string : help) {
				sender.sendMessage(ChatColor.GRAY + string);
			}
		}
		
		if(cmd.hasNested()) {
			
			String[] labels = cmd.getNestedLabels();
			
			int total = labels.length;
			int totalPerPage;
			
			if(page == -1) {
				page = 1;
				totalPerPage = total;
			}
			else {
				totalPerPage = 10;
			}
			
			int startIndex = (page - 1) * totalPerPage;
			int endIndex = startIndex + totalPerPage;

			int totalPages = (int) Math.ceil((float) total / totalPerPage);
			if (page > totalPages || page < 1) {
				return;
			}
			sender.sendMessage(ChatColor.GRAY + "" + ChatColor.UNDERLINE + "Nested commands:" + ChatColor.UNDERLINE + " (page "
					+ page + "/" + totalPages + "):");
			
			for (int i = startIndex; i < endIndex && i < total; i++) {
				String lbl = labels[i];
				ICommand nested = cmd.getNested(lbl);
				if(hasPermission(sender, nested.getPermission())) {
					sender.sendMessage(nested.getUsage() + ChatColor.GRAY + " " + nested.getDescription());
				}
				else {
					i--;
				}
				
			}
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	private interface CommandFactory {
		ICommand getCommand();
	}
	
	
	
	
	
	
	
	
	
	
	private class CommandObjectFactory implements CommandFactory {

		private Class<?> commandClass;
		private Object[] args;
		private Constructor<?> constructor;

		private <T extends ICommand> CommandObjectFactory(Class<T> commandClass,
				Object[] args) {
			this.commandClass = commandClass;
			this.args = args;

			findConstructor(commandClass, args);
		}

		private void findConstructor(Class<?> commandClass, Object... args) {

			Constructor<?> result = null;
			Constructor<?>[] ctrs = commandClass.getConstructors();
			for (int i = 0; result == null && i < ctrs.length; i++) {
				result = ctrs[i];
				Class<?>[] parameterTypes = result.getParameterTypes();
				if (parameterTypes.length != args.length) {
					result = null;
					continue;
				}
				parameter_iterator: for (int j = 0; j < parameterTypes.length; j++) {
					if (!parameterTypes[j].isAssignableFrom(args[j].getClass())) {
						result = null;
						break parameter_iterator;
					}
				}
			}

			if (result == null) {
				String typeString = "";
				for (Object arg : args) {
					typeString += ", " + arg.getClass().getName();
				}
				if (!typeString.isEmpty()) {
					typeString = typeString.substring(2);
				}
				throw new IllegalArgumentException("Class \""
						+ commandClass.getCanonicalName()
						+ "\" doesn't have a constructor with parameter types ("
						+ typeString + ")");
			}

			this.constructor = result;
		}

		private ICommand newInstance(Constructor<?> constructor) {
			constructor.setAccessible(true);
			try {
				return (ICommand) constructor.newInstance(args);
			} catch (Exception e) {

				Logger.getLogger(getClass().getCanonicalName()).log(
						Level.SEVERE,
						"Failed to create command class \""
								+ commandClass.getCanonicalName() + "\".", e);

				return null;
			}
		}

		@Override
		public ICommand getCommand() {
			return newInstance(constructor);
		}
	}
	
	
	
	
	
	
	
	
	
	
	private class CommandMethodFactory implements CommandFactory {

		private ICommand parent;
		private Method method;

		private CommandMethodFactory(ICommand parent, Method method) {
			this.parent = parent;
			this.method = method;
		}

		@Override
		public ICommand getCommand() {
			return new CommandMethod(parent, method);
		}

	}
	
	
	
	
	
	
	
	
	
	
	private class CommandMethod implements ICommand {

		private Method method;
		private Command command;
		private ICommand parent;
		private String label;
		private List<String> aliases;

		private CommandMethod(ICommand parent, Method method) {
			this.parent = parent;
			this.method = method;
			this.command = method.getAnnotation(Command.class);
			
			String[] aliases = command.aliases();
			this.label = aliases[0];
			try {
				aliases = Arrays.copyOfRange(aliases, 1, aliases.length);
			} catch (Exception e) {
				aliases = new String[0];
			}
			this.aliases = Arrays.asList(aliases);
		}

		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public List<String> getAliases() {
			return aliases;
		}

		@Override
		public String getArgumentSyntax() {
			return command.args();
		}
		
		@Override
		public String getPermission() {
			return command.perm();
		}

		@Override
		public String getDescription() {
			return command.desc();
		}

		@Override
		public String[] getHelp() {
			return command.help();
		}

		@Override
		public void execute(CommandSender sender, String[] args)
				throws CommandException {
			
			testPermission(sender, getPermission());
			
			Object instance = parent;
			try {
				method.invoke(instance, sender, args);
			} catch (Exception e) {
				if(e.getCause() instanceof CommandException) {
					throw (CommandException)e.getCause();
				}
				else {
					Logger.getLogger(getClass().getCanonicalName()).log(
							Level.SEVERE,
							"Failed to invoke command method \"" + method.getName()
									+ "\" of class \""
									+ instance.getClass().getCanonicalName() + "\"", e);
				}
				
			}
			return;
		}

		@Override
		public ICommand getParent() {
			return parent;
		}

		@Override
		public String getUniqueLabel() {
			return SimpleCommand.getUniqueLabel(this);
		}

		@Override
		public String getUsage() {
			return SimpleCommand.getUsage(this);
		}

		@Override
		public ICommand getNested(String label) {
			return null;
		}

		@Override
		public String[] getNestedLabels() {
			return null;
		}

		@Override
		public boolean hasNested() {
			return false;
		}
	}
}
