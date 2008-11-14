package com.hp.hpl.guess.action;

import java.util.UUID;

import com.hp.hpl.guess.storage.StorageFactory;

/**
 * Implements a GAction with the Guess states concept.
 * Before an action is called the stated is saved and after the 
 * action the state is saved again. Undo and Redo load these states. 
 * @author johannes
 *
 */
public abstract class GStateAction extends GAction {

	/**
	 * Name of the old state
	 */
	private String oldStateName = null;
	
	/**
	 * Delimiter to separate from ordinary states
	 */
	public static final String delimiter = "_";
	
	/**
	 * Creates a new state in the storage listener
	 * @return the statename
	 */
	private String createNewState() {
		String statename = delimiter + UUID.randomUUID().toString().replace('-', 'X');
		StorageFactory.getSL().saveState(statename);
		return statename;
	}
	
	/**
	 * Executes the action
	 */
	public void run() {
		setOldState();
		super.run();
	}
	
	/**
	 * Save a state before the action was
	 * executed
	 */
	private void setOldState() {
		oldStateName = createNewState();
	}
	
	/**
	 * Load the old state
	 * @return Actions item for redo
	 */
	public GAction getUndoAction() {
		if (oldStateName==null) {
			System.err.println("No state for undo action.");
		}
		GStateAction undoAction = new GStateAction() {
			protected void actionContent() {
				StorageFactory.getSL().loadState(oldStateName);
			}
		};
		undoAction.setDescription(getDescription());
		undoAction.setOldState();
		
		return undoAction;
		
	}

	/**
	 * Delete old state
	 */
	public void dispose() {
		StorageFactory.getSL().deleteState(oldStateName);
	}	

}
