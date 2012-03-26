package com.mtihc.minecraft.regionselfservice.tasks;

public abstract class AcceptableTask {

	public enum AcceptResult {
		ACCEPTED,
		DENIED,
		NOT_REQUIRED, IGNORED;
	}
	
	protected String playerName;
	
	public AcceptableTask(String playerName) {
		this.playerName = playerName;
	}
	
	/**
	 * Run the task, depending on the accept result
	 * @param result The accept result enum value
	 * @throws AcceptableTaskException Thrown if anything goes wrong during the task
	 */
	public abstract void run(AcceptResult result) throws AcceptableTaskException;
	
	/**
	 * Whether the player needs to accept before running the task
	 * @return
	 */
	public abstract boolean acceptIsRequired();
	
	public abstract long getAcceptTime();

	/**
	 * @return the playerName
	 */
	public String getPlayerName() {
		return playerName;
	}
}
