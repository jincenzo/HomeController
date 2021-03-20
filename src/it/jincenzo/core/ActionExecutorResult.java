package it.jincenzo.core;

public class ActionExecutorResult {
	private ActionExecutorFeedback feedback;
	private Exception exception;
	
	public static final ActionExecutorResult OK = new ActionExecutorResult(ActionExecutorFeedback.TRIGGER_EXECUTED, null);
	public static final ActionExecutorResult NOT_FOUND = new ActionExecutorResult(ActionExecutorFeedback.TRIGGER_NOT_FOUND, null);

	private ActionExecutorResult(ActionExecutorFeedback feedback, Exception exception) {
		super();
		this.feedback = feedback;
		this.exception = exception;
	}

	public ActionExecutorFeedback getFeedback() {
		return feedback;
	}

	public Exception getException() {
		return exception;
	}
	
	public static ActionExecutorResult createErrorFeedBack(Exception exception) {
		return new ActionExecutorResult(ActionExecutorFeedback.TRIGGER_ERROR, exception);
	}

}
