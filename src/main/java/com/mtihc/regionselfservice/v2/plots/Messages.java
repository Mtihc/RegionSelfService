package com.mtihc.regionselfservice.v2.plots;

import java.util.NoSuchElementException;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messages {

	private IEconomy economy;
	
	/**
    *
    * SOLD
    * members Region "id" was sold to "buyer". You were member of that region.
    * owners Region "id" was sold to "buyer". You were owner of that region.
    * You are sharing "cost" with "owners".
    * You all received an equal share of "share".
    * buyer You bought region "id" for "cost" from "owners".
    *
    *
    * RENTED
    * members Region "id" was rented out to "renter" for "time". You are member of this region.
    * owners Region "id" was rented out to "renter" for "time". You are owner of this region.
    * You are sharing "cost" with "owners".
    * You all received an equal share of "share".
    * renter You rented region "id" for "time", for the price of "cost", from "owners".
    *
    * UPFORSALE
    * members Player "seller" put region "id" up for sale, for "cost". You are member of this region.
    * owners Player "seller" put region "id" up for sale, for "cost". You are owner of this region.
    * If the region is sold, the profits are shared amongst "owners".
    * seller You put region "id" up for sale, for "cost".
    * If the region is sold, the profits are shared amongst "owners".
    *
    *
    * UPFORRENT
    * members Player "letter" put region "id" up for rent, for "cost" per "time". You are member of this region.
    * owners Player "letter" put region "id" up for rent, for "cost" per "time". You are owner of this region.
    * If the region is rented out, the profits are shared amongst "owners".
    * letter You put region "id" up for rent, for "cost" per "time".
    * If the region is rented out, the profits are shared amongst "owners".
    *
    * REMOVED
    * members Player "remover" removed region "id". You were member of that region.
    * owners Player "remover" removed region "id". You were owner of that region.
    * You are sharing the refund of "refund" with "owners".
    * You all received an equal share of "share".
    * remover Region "id" removed.
    *
    * RESIZED (bigger)
    * members Player "resizer" resized region "id" from "old-size" to "new-size". You are member of that region.
    * owners Player "resizer" payed "cost" to resize region "id" from "old-size" to "new-size". You are owner of that region.
    * resizer You payed "cost" to resize region "id" from "old-size" to "new-size".
    *
    * RESIZED (smaller)
    * members Player "resizer" resized region "id" from "old-size" to "new-size". You are member of that region.
    * owners Player "resizer" resized region "id" from "old-size" to "new-size". You are owner of that region.
    * The region became smaller, so you are sharing the refund of "refund" with "owners".
    * You all received an equal share of "share".
    * resizer You payed "cost" to resize region "id" from "old-size" to "new-size".
    *
    * CREATE
    * creator Region "id" protected.
    * ...show region info....
    *
    */
   public Messages(IEconomy economy) {
           this.economy = economy;
   }
   
   public void bought(String regionId, CommandSender buyer, double cost, Set<String> owners, Set<String> members, double share, String taxAccount, double tax) {
           String ownerNames = toUserfriendlyString(owners);
           String permOwner = Permission.INFORM_OWNER_SOLD;
           String permMember = Permission.INFORM_MEMBER_SOLD;
           // You bought region <id> for <cost> from <owners>
           buyer.sendMessage(ChatColor.GREEN + "You bought region " + ChatColor.WHITE + regionId + ChatColor.GREEN + " for " + ChatColor.WHITE + format(cost) + ChatColor.GREEN + " from " + ChatColor.WHITE + ownerNames + ChatColor.GREEN + ".");
           explainTax(buyer, taxAccount, tax);
           // Region <id> was sold to <buyer>.
           String msg = ChatColor.GREEN + "Region " + ChatColor.WHITE + regionId + ChatColor.GREEN + " was sold to " + ChatColor.WHITE + buyer.getName() + ChatColor.GREEN + ".";
           
           if(owners != null) {
                   if(owners.size() > 1) {
                           for (String name : owners) {
                                   Player owner = buyer.getServer().getPlayerExact(name);
                                   if(owner == null || !owner.isOnline() || !owner.hasPermission(permOwner) || owner.getName().equalsIgnoreCase(buyer.getName())) {
                                           continue;
                                   }
                                   owner.sendMessage(msg);
                                   // Sharing <cost> with <owners>
                                   owner.sendMessage(ChatColor.GREEN + "Sharing " + ChatColor.WHITE + format(cost) + ChatColor.GREEN + " with " + ChatColor.WHITE + ownerNames + ChatColor.GREEN + ".");
                                   // You all received an equal share of <share>
                                   owner.sendMessage(ChatColor.GREEN + "You all received an equal share of " + ChatColor.WHITE + formatShare(cost, owners));
                                   explainTax(owner, taxAccount, tax);
                           }
                   }
                   else {
                           Player owner;
                           try {
                                   owner = buyer.getServer().getPlayerExact(owners.iterator().next());
                           } catch(NoSuchElementException e) {
                                   owner = null;
                           }
                           if(owner != null && owner.isOnline() && owner.hasPermission(permOwner) && !owner.getName().equalsIgnoreCase(buyer.getName())) {
                                   owner.sendMessage(msg + ChatColor.GREEN + " You received " + ChatColor.WHITE + format(cost));
                                   explainTax(owner, taxAccount, tax);
                           }
                   }
                   
           }
           if(members != null) {
                   for (String name : members) {
                           Player member = buyer.getServer().getPlayerExact(name);
                           if(member == null || !member.isOnline() || !member.hasPermission(permMember) || member.getName().equalsIgnoreCase(buyer.getName())) {
                                   continue;
                           }
                           member.sendMessage(msg);
                           // You are member of that region
                           member.sendMessage(ChatColor.GREEN + "You are member of that region.");
                   }
           }
   }

   private void explainTax(CommandSender sender, String taxAccount, double tax) {
           if(tax != 0) {
                   sender.sendMessage(ChatColor.WHITE + taxAccount + ChatColor.GREEN + " received the tax of " + ChatColor.WHITE + format(tax) + ChatColor.GREEN + ".");
           }
   }
   
   public void rented(CommandSender renter, Set<String> owners, Set<String> members, String regionId, double cost, String time) {
           String ownerNames = toUserfriendlyString(owners);
           String permOwner = Permission.INFORM_OWNER_RENTED;
           String permMember = Permission.INFORM_MEMBER_RENTED;
           // You rented region <id> for <cost> from <owners>
           renter.sendMessage(ChatColor.GREEN + "You rented region " + ChatColor.WHITE + regionId + ChatColor.GREEN + " for " + ChatColor.WHITE + format(cost) + ChatColor.GREEN + " from " + ChatColor.WHITE + ownerNames + ChatColor.GREEN + ", for " + ChatColor.WHITE + time + ChatColor.GREEN + ".");
           
           // Region <id> was rented out to <buyer> for <time>.
           String msg = ChatColor.GREEN + "Region " + ChatColor.WHITE + regionId + ChatColor.GREEN + " was rented out to " + ChatColor.WHITE + renter.getName() + ChatColor.GREEN + " for " + ChatColor.WHITE + time + ChatColor.GREEN + ".";
           
           if(owners != null) {
                   if(owners.size() > 1) {
                           for (String name : owners) {
                                   Player owner = renter.getServer().getPlayerExact(name);
                                   if(owner == null || !owner.isOnline() || !owner.hasPermission(permOwner) || owner.getName().equalsIgnoreCase(renter.getName())) {
                                           continue;
                                   }
                                   owner.sendMessage(msg);
                                   // Sharing <cost> with <owners>
                                   owner.sendMessage(ChatColor.GREEN + "Sharing " + ChatColor.WHITE + format(cost) + ChatColor.GREEN + " with " + ChatColor.WHITE + ownerNames + ChatColor.GREEN + ".");
                                   // You all received an equal share of <share>
                                   owner.sendMessage(ChatColor.GREEN + "You all received an equal share of " + ChatColor.WHITE + formatShare(cost, owners));
                           }
                   }
                   else {
                           Player owner;
                           try {
                                   owner = renter.getServer().getPlayerExact(owners.iterator().next());
                           } catch(NoSuchElementException e) {
                                   owner = null;
                           }
                           if(owner != null && owner.isOnline() && owner.hasPermission(permOwner) && !owner.getName().equalsIgnoreCase(renter.getName())) {
                                   owner.sendMessage(msg);
                                   owner.sendMessage(ChatColor.GREEN + "You received " + ChatColor.WHITE + format(cost));
                           }
                   }
           }
           if(members != null) {
                   for (String name : members) {
                           Player member = renter.getServer().getPlayerExact(name);
                           if(member == null || !member.isOnline() || !member.hasPermission(permMember) || member.getName().equalsIgnoreCase(renter.getName())) {
                                   continue;
                           }
                           member.sendMessage(msg);
                           //
                           member.sendMessage(ChatColor.GREEN + "You are also member of that region.");
                   }
           }
   }
   
   /* UPFORSALE
    * members Player "seller" put region "id" up for sale, for "cost". You are member of this region.
    * owners Player "seller" put region "id" up for sale, for "cost". You are owner of this region.
    * If the region is sold, the profits are shared amongst "owners".
    * seller You put region "id" up for sale, for "cost".
    * If the region is sold, the profits are shared amongst "owners".
    */
   public void upForSale(CommandSender seller, Set<String> owners, Set<String> members, String regionId, double cost) {
           String ownerNames = toUserfriendlyString(owners);
           String permOwner = Permission.INFORM_OWNER_UPFORSALE;
           String permMember = Permission.INFORM_MEMBER_UPFORSALE;
           // If the region is sold, the profits are shared amongst <owners>.
           String msg2 = ChatColor.GREEN + "If the region is sold, the profits ";
           if(owners != null && owners.size() > 1) {
                   msg2 += "are shared amongst " + ChatColor.WHITE + ownerNames + ChatColor.GREEN + ".";
           }
           else {
                   msg2 += "are for " + ChatColor.WHITE + ownerNames + ChatColor.GREEN + ".";
           }
           // You put region <id> up for sale, for <cost>.
           seller.sendMessage(ChatColor.GREEN + "You put region " + ChatColor.WHITE + regionId + ChatColor.GREEN + " up for sale, for " + ChatColor.WHITE + format(cost) + ChatColor.GREEN + ".");
           seller.sendMessage(msg2);
           
           // Player <seller> put region <id> up for sale, for <cost>.
           String msg = ChatColor.GREEN + "Player " + ChatColor.WHITE + seller.getName() + ChatColor.GREEN + " put region " + ChatColor.WHITE + regionId + ChatColor.GREEN + " up for sale, for " + ChatColor.WHITE + format(cost) + ChatColor.GREEN
            + ".";
           
           
           if(owners != null) {
                   for (String name : owners) {
                           Player player = seller.getServer().getPlayerExact(name);
                           if(player == null || !player.isOnline() || !player.hasPermission(permOwner) || player.getName().equalsIgnoreCase(seller.getName())) {
                                   continue;
                           }
                           // You are owner of that region.
                           player.sendMessage(msg + " You are owner of that region.");
                           player.sendMessage(msg2);
                   }
           }
           else if(members != null) {
                   for (String name : members) {
                           Player player = seller.getServer().getPlayerExact(name);
                           if(player == null || !player.isOnline() || !player.hasPermission(permMember) || player.getName().equalsIgnoreCase(seller.getName())) {
                                   continue;
                           }
                           // You are member of that region.
                           player.sendMessage(msg + " You are member of that region.");
                   }
           }
   }
   /* UPFORRENT
    * members Player "letter" put region "id" up for rent, for "cost" per "time". You are member of this region.
    * owners Player "letter" put region "id" up for rent, for "cost" per "time". You are owner of this region.
    * If the region is rented out, the profits are shared amongst "owners".
    * letter You put region "id" up for rent, for "cost" per "time".
    * If the region is rented out, the profits are shared amongst "owners".
    */
   public void upForRent(CommandSender letter, Set<String> owners, Set<String> members, String regionId, double costPerTime, String time) {
           String ownerNames = toUserfriendlyString(owners);
           String permOwner = Permission.INFORM_OWNER_UPFORRENT;
           String permMember = Permission.INFORM_MEMBER_UPFORRENT;
           
           // If the region is sold, the profits are shared amongst <owners>.
           String msg2 = ChatColor.GREEN + "If the region is rented out, the profits ";
           if(owners != null && owners.size() > 1) {
                   msg2 += "are shared amongst " + ChatColor.WHITE + ownerNames + ChatColor.GREEN + ".";
           }
           else {
                   msg2 += "are for " + ChatColor.WHITE + ownerNames + ChatColor.GREEN + ".";
           }
           letter.sendMessage(ChatColor.GREEN + "You put region " + ChatColor.WHITE + regionId + ChatColor.GREEN + " up for rent, for " + ChatColor.WHITE + format(costPerTime) + ChatColor.GREEN + " per " + ChatColor.WHITE + time + ChatColor.GREEN + ".");
           letter.sendMessage(msg2);
           
           // Player <letter> put region <id> up for rent, for <cost> per <time>.
           String msg = ChatColor.GREEN + "Player "+ ChatColor.WHITE + letter.getName() + ChatColor.GREEN + " put region " + ChatColor.WHITE + regionId + ChatColor.GREEN + " up for rent, for " + ChatColor.WHITE + format(costPerTime) + ChatColor.GREEN + " per " + ChatColor.WHITE + time + ChatColor.GREEN + ".";
           
           if(owners != null) {
                   for (String name : owners) {
                           Player player = letter.getServer().getPlayerExact(name);
                           if(player == null || !player.isOnline() || !player.hasPermission(permOwner) || player.getName().equalsIgnoreCase(letter.getName())) {
                                   continue;
                           }
                           player.sendMessage(msg);
                           // You are owner of this region.
                           player.sendMessage(ChatColor.GREEN + "You are owner of this region.");
                           player.sendMessage(msg2);
                   }
           }
           else if(members != null) {
                   for (String name : members) {
                           Player player = letter.getServer().getPlayerExact(name);
                           if(player == null || !player.isOnline() || !player.hasPermission(permMember) || player.getName().equalsIgnoreCase(letter.getName())) {
                                   continue;
                           }
                           player.sendMessage(msg);
                           // You are member of this region.
                           player.sendMessage(ChatColor.GREEN + "You are member of this region.");
                   }
           }
   }
   /* REMOVED
    * members Player "remover" removed region "id". You were member of that region.
    * owners Player "remover" removed region "id". You were owner of that region.
    * You are sharing the refund of "refund" with "owners".
    * You all received an equal share of "share".
    * remover Region "id" removed.
    */
   public void removed(CommandSender remover, Set<String> owners, Set<String> members, String regionId, double refund) {
           String ownerNames = toUserfriendlyString(owners);
           String permOwner = Permission.INFORM_OWNER_REMOVED;
           String permMember = Permission.INFORM_MEMBER_REMOVED;
           
           // Region <id> removed.
           remover.sendMessage(ChatColor.YELLOW + "Region " + ChatColor.WHITE + regionId + ChatColor.YELLOW + " removed.");
           
           // Player <remover> removed region <id>.
           String msg = ChatColor.GREEN + "Player " + ChatColor.WHITE + remover.getName() + ChatColor.GREEN + " removed region " + ChatColor.WHITE + regionId + ChatColor.GREEN + ".";
           
           if(owners != null && !owners.isEmpty()) {
                   if(owners.size() > 1) {
                           for (String name : owners) {
                                   Player player = remover.getServer().getPlayerExact(name);
                                   if(player == null || !player.isOnline() || !player.hasPermission(permOwner)) {
                                           continue;
                                   }
                                   if(player.getName().equalsIgnoreCase(remover.getName())) {
                                           player.sendMessage(msg);
                                   }
                                   player.sendMessage(ChatColor.GREEN + "You were owner of that region.");
                                   if(refund > 0) {
                                	   player.sendMessage(ChatColor.GREEN + "So you're sharing the refund of " + ChatColor.WHITE + format(refund));
                                	   player.sendMessage(ChatColor.GREEN + "with " + ChatColor.WHITE + ownerNames);
                                	   player.sendMessage(ChatColor.GREEN + "You all received an equal share of " + ChatColor.WHITE + formatShare(refund, owners));
                                   }
                                   
                                   
                           }
                   }
                   else {
                           Player player = remover.getServer().getPlayerExact(owners.iterator().next());
                           if(player != null && player.isOnline() && player.hasPermission(permOwner)) {
                                   if(player.getName().equalsIgnoreCase(remover.getName())) {
                                           player.sendMessage(msg);
                                   }
                                   player.sendMessage(ChatColor.GREEN + "You were owner of that region. ");
                                   if(refund > 0) {
                                	   player.sendMessage(ChatColor.GREEN + "So you received the refund of " + ChatColor.WHITE + format(refund));
                                   }
                           }
                           
                           
                   }
                   
           }
           else if(members != null) {
                   for (String name : members) {
                           Player player = remover.getServer().getPlayerExact(name);
                           if(player == null || !player.isOnline() || !player.hasPermission(permMember)) {
                                   continue;
                           }
                           player.sendMessage(msg);
                           player.sendMessage(ChatColor.GREEN + "You were member of that region.");
                   }
           }
   }
   
   public void resized(Player resizer, Set<String> owners, Set<String> members, String regionId, double oldWorth, double newWorth, int oldWidth, int oldLength, int oldHeight, int newWidth, int newLength, int newHeight) {
           if(oldWidth * oldLength > newWidth * newLength) {
                   resized_smaller(resizer, owners, members, regionId, newWorth - oldWorth, oldWidth, oldLength, oldHeight, newWidth, newLength, newHeight);
           }
           else {
                   resized_bigger(resizer, owners, members, regionId, newWorth - oldWorth, oldWidth, oldLength, oldHeight, newWidth, newLength, newHeight);
           }
   }
   /* RESIZED (bigger)
    * members Player "resizer" resized region "id" from "old-size" to "new-size". You are member of that region.
    * owners Player "resizer" payed "cost" to resize region "id" from "old-size" to "new-size". You are owner of that region.
    * resizer You payed "cost" to resize region "id" from "old-size" to "new-size".
    *
    * RESIZED (smaller)
    * members Player "resizer" resized region "id" from "old-size" to "new-size". You are member of that region.
    * owners Player "resizer" resized region "id" from "old-size" to "new-size". You are owner of that region.
    * The region became smaller, so you are sharing the refund of "refund" with "owners".
    * You all received an equal share of "share".
    * resizer You payed "cost" to resize region "id" from "old-size" to "new-size".
    *
    * CREATE
    * creator Region "id" protected.
    * ...show region info....
    */
   public void resized_bigger(Player resizer, Set<String> owners, Set<String> members, String regionId, double cost, int oldWidth, int oldLength, int oldHeight, int newWidth, int newLength, int newHeight) {
           String permOwner = Permission.INFORM_OWNER_RESIZE;
           String permMember = Permission.INFORM_MEMBER_RESIZE;
           
           String oldSize = formatSize(oldWidth, oldLength, oldHeight);
           String newSize = formatSize(newWidth, newLength, newHeight);
           
           String msg = ChatColor.GREEN + "You ";
           if(cost > 0) {
        	   msg += "payed " + ChatColor.WHITE + format(cost) + ChatColor.GREEN + " and ";
           }
           msg += "resized region " + ChatColor.WHITE + regionId + ChatColor.GREEN + " from " + ChatColor.WHITE + oldSize + ChatColor.GREEN + " to " + ChatColor.WHITE + newSize + ChatColor.GREEN + ".";
           resizer.sendRawMessage(msg);
           
           String resizeMsg = getResizeMessage(resizer.getName(), regionId, oldSize, newSize);
           
           if(owners != null) {
                   for (String name : owners) {
                           Player player = resizer.getServer().getPlayerExact(name);
                           if(player == null || !player.isOnline() || !player.hasPermission(permOwner) || player.getName().equalsIgnoreCase(resizer.getName())) {
                                   continue;
                           }
                           if(!player.getName().equalsIgnoreCase(resizer.getName())) {
                                   player.sendRawMessage(resizeMsg);
                           }
                           player.sendRawMessage(ChatColor.GREEN + "You are owner of that region.");
                   }
                   
           }
           else if(members != null) {
                   for (String name : members) {
                           Player player = resizer.getServer().getPlayerExact(name);
                           if(player == null || !player.isOnline() || !player.hasPermission(permMember) || player.getName().equalsIgnoreCase(resizer.getName())) {
                                   continue;
                           }
                           if(!player.getName().equalsIgnoreCase(resizer.getName())) {
                                   player.sendRawMessage(resizeMsg);
                           }
                           player.sendRawMessage(ChatColor.GREEN + "You are member of that region.");
                   }
           }
   }
   public void resized_smaller(Player resizer, Set<String> owners, Set<String> members, String regionId, double refund, int oldWidth, int oldLength, int oldHeight, int newWidth, int newLength, int newHeight) {
           String ownerNames = toUserfriendlyString(owners);
           String permOwner = Permission.INFORM_OWNER_RESIZE;
           String permMember = Permission.INFORM_MEMBER_RESIZE;
           
           String oldSize = formatSize(oldWidth, oldLength, oldHeight);
           String newSize = formatSize(newWidth, newLength, newHeight);
           
           resizer.sendRawMessage(ChatColor.GREEN + "Resized region " + ChatColor.WHITE + regionId + ChatColor.GREEN + " from " + ChatColor.WHITE + oldSize + ChatColor.GREEN + " to " + ChatColor.WHITE + newSize + ChatColor.GREEN + ".");
           
           String msg = getResizeMessage(resizer.getName(), regionId, oldSize, newSize);
           
           if(owners != null) {
                   if(owners.size() > 1) {
                           for (String name : owners) {
                                   Player player = resizer.getServer().getPlayerExact(name);
                                   if(player == null || !player.isOnline() || !player.hasPermission(permOwner)) {
                                           continue;
                                   }
                                   if(!player.getName().equalsIgnoreCase(resizer.getName())) {
                                           player.sendRawMessage(msg);
                                   }
                                   player.sendRawMessage(ChatColor.GREEN + "You are owner of that region.");
                                   if(refund != 0) {
                                	   player.sendRawMessage(ChatColor.GREEN + "Sharing the refund of " + ChatColor.WHITE + format(Math.abs(refund)) + ChatColor.GREEN + " with " + ChatColor.WHITE + ownerNames + ChatColor.GREEN + ".");
                                	   player.sendRawMessage(ChatColor.GREEN + "You all got an equal share of " + ChatColor.WHITE + formatShare(Math.abs(refund), owners));
                                   }
                                   
                           }
                   }
                   else {
                           Player player = resizer.getServer().getPlayerExact(owners.iterator().next());
                           if(player != null && player.isOnline() && player.hasPermission(permOwner)) {
                                   if(!player.getName().equalsIgnoreCase(resizer.getName())) {
                                           player.sendRawMessage(msg);
                                   }
                                   if(refund != 0) {
                                	   player.sendRawMessage(ChatColor.GREEN + "You received a refund of " + ChatColor.WHITE + format(Math.abs(refund)));
                                   }
                           }
                   }
                   
           }
           else if(members != null) {
                   for (String name : members) {
                           Player player = resizer.getServer().getPlayerExact(name);
                           if(player == null || !player.isOnline() || !player.hasPermission(permMember)) {
                                   continue;
                           }
                           if(!player.getName().equalsIgnoreCase(resizer.getName())) {
                                   player.sendRawMessage(msg);
                           }
                           player.sendRawMessage(ChatColor.GREEN + "You are member of that region.");
                   }
           }
   }
   
   private String getResizeMessage(String resizerName, String regionId, String oldSize, String newSize) {
           return ChatColor.GREEN + "Player " + ChatColor.WHITE + resizerName + ChatColor.GREEN + " resized region " + ChatColor.WHITE + regionId + ChatColor.GREEN + " from " + ChatColor.WHITE + oldSize + ChatColor.GREEN + " to " + ChatColor.WHITE + newSize + ChatColor.GREEN + ".";
   }
   private String formatSize(int width, int length, int height) {
           return width + "x" + length + "x" + height;
   }
   
   public String format(double amount) {
           return economy.format(amount);
   }
   
   public double formatShare(double cost, Set<String> owners) {
           if(owners == null || owners.size() == 0) {
                   return 0;
           }
           else {
                   return cost / owners.size();
           }
   }
   
   public String toUserfriendlyString(Set<String> names) {
           if(names == null || names.size() < 1) {
                   return "nobody";
           }
           String result = "";
           for (String name : names) {
                   result += ", " + name;
           }
           return result.substring(2);
   }

}
