package essentialclient.gui.clientrule;

public class ClientRules {

    public static String announceAFK = "announceAFK";
    public static String announceAFKMessage = "announceAFKMessage";
    public static String commandPlayerClient = "commandPlayerClient";
    public static String commandRegion = "commandRegion";
    public static String commandTravel = "commandTravel";
    public static String disableRecipeNotifications = "disableRecipeNotifications";
    public static String disableTutorialNotifications = "disableTutorialNotifications";
    public static String removeWarnReceivedPassengers = "removeWarnReceivedPassengers";
    public static String stackableShulkersInPlayerInventories = "stackableShulkersInPlayerInventories";
    public static String stackableShulkersWithItems = "stackableShulkersWithItems";
    public static String unlockAllRecipesOnJoin = "unlockAllRecipesOnJoin";

    protected static void checkRules() {
        //Boolean Rules
        addDefaultBooleanRule(commandPlayerClient                       , "This command allows you to save /player... commands and execute them"                        , true);
        addDefaultBooleanRule(commandRegion                             , "This command allows you to determine the region you are in or the region at set coords"      , true);
        addDefaultBooleanRule(commandTravel                             , "This command allows you to travel to a set location"                                         , true);
        addDefaultBooleanRule(disableRecipeNotifications                , "Disables the recipe toast from showing"                                                      , false);
        addDefaultBooleanRule(disableTutorialNotifications              , "Disables the tutorial toast from showing"                                                    , false);
        addDefaultBooleanRule(removeWarnReceivedPassengers              , "This removes the 'Received passengers for unkown entity' warning on the client"              , false);
        addDefaultBooleanRule(stackableShulkersInPlayerInventories      , "This allows for shulkers to stack only in your inventory"                                    , false);
        addDefaultBooleanRule(stackableShulkersWithItems                , "This allows for shulkers with items to stack only in your inventory"                         , false);
        addDefaultBooleanRule(unlockAllRecipesOnJoin                    , "Unlocks every recipe when joining a singleplayer world"                                                , false);

        //Number Rules
        addDefaultNumberRule (announceAFK                               , "This announces when you become afk after a set amount of time (ticks)");

        //String Rules
        addDefaultStringRule (announceAFKMessage                        , "This is the message you announce after you are afk"                                           , "I am now AFK");
    }
    private static void addDefaultBooleanRule(String name, String description, boolean isCommand) {
        addRule(name, "boolean", description, "false", "false", isCommand);
    }

    private static void addDefaultNumberRule(String name, String description) {
        addRule(name, "number", description, "0", "0", false);
    }

    private static void addDefaultStringRule(String name, String description, String defaultValue) {
        addRule(name, "string", description, defaultValue, defaultValue, false);
    }

    private static void addRule(String name, String type, String description, String defaultValue, String value, boolean isCommand) {
        ClientRule.clientRulesMap.putIfAbsent(name, new ClientRule(name, type, description, defaultValue, value, isCommand));
    }
}
