package it.jincenzo.configs;

import java.io.Serializable;
import java.util.List;
/**
 * Class that represents a trigger to a list of actions 
 */
public class TriggerConfig implements Serializable{
	

	private static final long serialVersionUID = 1L;
	private String triggerId;
	private List<ActionSet> actionSets;
	
	/**
	 * Constructor
	 */
	public TriggerConfig(String triggerId, List<ActionSet> actionSets) {
		super();
		this.triggerId = triggerId;
		this.actionSets = actionSets;
	}
	
	
	/**
	 * 
	 */
	public TriggerConfig() {
		super();
	}


	public String getTriggerId() {
		return triggerId;
	}
	public List<ActionSet> getActionSets() {
		return actionSets;
	}
	
	
}
