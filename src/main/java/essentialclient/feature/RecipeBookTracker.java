package essentialclient.feature;

import java.util.concurrent.atomic.AtomicBoolean;

public final class RecipeBookTracker {
	public static final AtomicBoolean IS_VANILLA_CLICK = new AtomicBoolean(false);
	public static final AtomicBoolean IS_SCRIPT_CLICK = new AtomicBoolean(false);
}
