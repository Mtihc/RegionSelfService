package com.mtihc.regionselfservice.v2.plots;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

public abstract class YesNoPrompt extends ValidatingPrompt {

	private static final ChatColor COLOR_DEFAULT = ChatColor.GREEN;
	private static final ChatColor COLOR_HIGHLIGHT = ChatColor.WHITE;

    protected abstract Prompt onYes();
    protected abstract Prompt onNo();
    
    @Override
    public String getPromptText(ConversationContext context) {
            return (COLOR_DEFAULT + "Type " + COLOR_HIGHLIGHT + "YES" + COLOR_DEFAULT + " or " + COLOR_HIGHLIGHT + "NO" + COLOR_DEFAULT + ".");
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
            if(input.startsWith("/")) {
                    Bukkit.dispatchCommand((CommandSender) context.getForWhom(), input.substring(1));
                    return false;
            }
            else if(input.equalsIgnoreCase("YES") || input.equalsIgnoreCase("NO")) {
                    return true;
            }
            else {
            	return false;
            }
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, String input) {
            if(input.equalsIgnoreCase("YES")) {
                    return onYes();
            }
            return onNo();
    }
    

}
