package com.mtihc.minecraft.regionselfservice.exceptions;

import org.bukkit.ChatColor;


public class WoodenSignException extends Exception {

	private static final long serialVersionUID = 3018032575646489596L;

	public enum Type
	{
		NO_REGION_NAME(ChatColor.RED + "Invalid text on sign. Couldn't find" + ChatColor.WHITE + " region name" + ChatColor.RED + " on line" + ChatColor.WHITE + " <line number>"),
		NO_COST(ChatColor.RED + "Invalid text on sign. Couldn't find" + ChatColor.WHITE + " cost" + ChatColor.RED + " on line" + ChatColor.WHITE + " <line number>"),
		NOT_ALLOWED_OUTSIDE(ChatColor.RED + "You can only place this sign inside the region itself."),
		NOT_ALLOWED_FREE(ChatColor.RED + "You have no permission to put the region up for free."),
		NOT_ALLOWED_THAT_PRICE(ChatColor.RED + "The price must be between " + ChatColor.WHITE + "the minimum price" + ChatColor.RED + " and" + ChatColor.WHITE + " the maximum price");
		
		private String message;

		private Type(String msg) {
			this.message = msg;
		}
		
		public String getMessage() {
			return message;
		}
		
	}
	
	private Type type;
	
	public WoodenSignException(Type type) {
		super(type.getMessage());
		this.type = type;
	}
	
	public WoodenSignException(Type type, String message){
		super(message);
		this.type = type;
	}
	
	public WoodenSignException(Type type, int lineNumber) {
		super(type.getMessage().replace("<line number>", String.valueOf(lineNumber)));
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}

}
