package com.mtihc.regionselfservice.v2.plugin;

public class Permission {

	
	private static final String INFORM = "selfservice.inform";
	
	
	private static final String INFORM_MEMBER = INFORM + ".member";
	
	public static final String INFORM_MEMBER_SOLD = INFORM_MEMBER + ".sold";
	public static final String INFORM_MEMBER_RENTED = INFORM_MEMBER + ".rented";
	public static final String INFORM_MEMBER_UPFORRENT = INFORM_MEMBER + ".upforrent";
	public static final String INFORM_MEMBER_UPFORSALE = INFORM_MEMBER + ".upforsale";
	public static final String INFORM_MEMBER_RESIZE = INFORM_MEMBER + ".resize";
	
	private static final String INFORM_OWNER = INFORM + ".owner";
	
	public static final String INFORM_OWNER_SOLD = INFORM_OWNER + ".sold";
	public static final String INFORM_OWNER_RENTED = INFORM_OWNER + ".rented";
	public static final String INFORM_OWNER_UPFORRENT = INFORM_OWNER + ".upforrent";
	public static final String INFORM_OWNER_UPFORSALE = INFORM_OWNER + ".upforsale";
	public static final String INFORM_OWNER_RESIZE = INFORM_OWNER + ".resize";
	
	public static final String INFORM_OWNER_REMOVED = INFORM_OWNER + ".removed";
	public static final String INFORM_MEMBER_REMOVED = INFORM_MEMBER + ".removed";
	
	
	
	public static final String RENTOUT = "selfservice.rentout";
	
	public static final String RENTOUT_ANYREGION = RENTOUT + ".anyregion";
	public static final String RENTOUT_ANYWHERE = RENTOUT + ".anywhere";
	public static final String RENTOUT_FREE = RENTOUT + ".forfree";
	
	public static final String SELL = "selfservice.sell";
	
	public static final String SELL_ANYREGION = SELL + ".anyregion";
	public static final String SELL_ANYWHERE = SELL + ".anywhere";
	public static final String SELL_FREE = SELL + ".forfree";
	
	
	public static final String WORTH = "selfservice.worth";
	public static final String REMOVE = "selfservice.remove";
	public static final String REMOVE_ANYREGION = REMOVE + ".anyregion";
	public static final String RELOAD = "selfservice.reload";
	public static final String RENT = "selfservice.rent";

	public static final String RENT_BYPASSCOST = RENT + ".bypasscost";
	public static final String COUNT = "selfservice.count";
	public static final String INFO = "selfservice.info";
	
	
	public static final String CREATE = "selfservice.create";
	public static final String CREATE_EXACT = CREATE + ".exact";
	public static final String CREATE_BYPASSCOST = CREATE + ".bypasscost";
	public static final String CREATE_ANYWHERE = CREATE + ".anywhere";
	public static final String CREATE_ANYSIZE = CREATE + ".anysize";
	
	public static final String REDEFINE = "selfservice.resize";
	public static final String REDEFINE_EXACT = REDEFINE + ".exact";
	public static final String REDEFINE_ANYREGION = REDEFINE + ".anyregion";
	
	public static final String BUY = "selfservice.buy";
	
	public static final String BYPASSMAX_REGIONS = "selfservice.bypassmaxregions";
	public static final String BUY_BYPASSCOST = BUY + ".bypasscost";

	public static final String BREAK_ANY_SIGN = "selfservice.breakanysign";


	public static final String CREATE_AND_SELL = "selfservice.create-and-sell";


	





}
