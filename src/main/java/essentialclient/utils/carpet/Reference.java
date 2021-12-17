package essentialclient.utils.carpet;

import net.minecraft.util.Identifier;

public class Reference {
	public static final Identifier CARPET_CHANNEL_NAME = new Identifier("carpet:client");
	public static final int ALL_GUI_INFO = 0;
	public static final int RULE_REQUEST = 1;
	public static final int CHANGE_RULE = 2;
	public static boolean isCarpetServer = false;
	public static boolean isCarpetClientPresent = false;
}
