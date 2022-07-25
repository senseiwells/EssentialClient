package me.senseiwells.essentialclient.clientscript.events;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.RuntimeError;

public class CancelEvent extends RuntimeError {
	public static final CancelEvent INSTANCE = new CancelEvent();

	private CancelEvent() {
		super("Cannot cancel event outside of a cancellable event", ISyntax.empty(), null);
	}
}
