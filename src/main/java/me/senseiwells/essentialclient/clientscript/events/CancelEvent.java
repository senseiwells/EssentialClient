package me.senseiwells.essentialclient.clientscript.events;

import me.senseiwells.arucas.exceptions.Propagator;

public class CancelEvent extends Propagator {
	public static final CancelEvent INSTANCE = new CancelEvent();

	private CancelEvent() {
		super("Cannot cancel event outside of a cancellable event");
	}
}
