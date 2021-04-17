package it.jincenzo.configs;

import java.io.Serializable;
import java.util.List;

public class ActionSet implements Serializable {
	private boolean longPress;
	private List<ActionConfig> actions;

	public ActionSet(List<ActionConfig> actions) {
		super();
		this.actions = actions;
	}

	/**
	 * 
	 */
	public ActionSet() {
		super();
	}

	public List<ActionConfig> getActions() {
		return actions;
	}
	public boolean isLongPress() {
		return longPress;
	}

}
