package essentialclient.gui.clientrule;

public enum ClientRules {
    //Boolean Rules
    COMMANDPLAYERCLIENT                     ("commandPlayerClient"                      , "This command allows you to save /player... commands and execute them"                                    , true),
    COMMANDPLAYERLIST                       ("commandPlayerList"                        , "This command allows you to execute /player... commands in one command (requires commandPlayerClient)"    , true),
    COMMANDREGION                           ("commandRegion"                            , "This command allows you to determine the region you are in or the region at set coords"                  , true),
    COMMANDTRAVEL                           ("commandTravel"                            , "This command allows you to travel to a set location"                                                     , true),
    DISABLERECIPENOTIFICATIONS              ("disableRecipeNotifications"               , "Disables the recipe toast from showing"                                                                  , false),
    DISABLETUTORIALNOTIFICATIONS            ("disableTutorialNotifications"             , "Disables the tutorial toast from showing"                                                                , false),
    DISABLENARRATOR                         ("disableNarrator"                          , "Disables cycling narrator when pressing CTRL + B"                                                        , false),
    ESSENTIALCLIENTMAINMENU                 ("essentialClientMainMenu"                  , "This renders the Essential Client Menu on the main meny screen"                                          , false),
    REMOVEWARNRECEIVEDPASSENGERS            ("removeWarnReceivedPassengers"             , "This removes the 'Received passengers for unkown entity' warning on the client"                          , false),
    STACKABLESHULKERSINPLAYERINVENTORIES    ("stackableShulkersInPlayerInventories"     , "This allows for shulkers to stack only in your inventory"                                                , false),
    STACKABLESHULKERSWITHITEMS              ("stackableShulkersWithItems"               , "This allows for shulkers with items to stack only in your inventory"                                     , false),
    UNLOCKALLRECIPESONJOIN                  ("unlockAllRecipesOnJoin"                   , "Unlocks every recipe when joining a singleplayer world"                                                  , false),

    //Number Rules
    ANNOUNCEAFK                             ("announceAFK"                              , "int"         , "This announces when you become afk after a set amount of time (ticks)"       , "0"           , false),
    OVERRIDECREATIVEWALKSPEED               ("overrideCreativeWalkSpeed"                , "double"      , "This allows you to override the vanilla walk speed in creative mode"         , "0.0"         , false),

    //String Rules
    ANNOUNCEAFKMESSAGE                      ("announceAFKMessage"                       , "This is the message you announce after you are afk", "I am now AFK");

    public final String name;
    public final String type;
    public final String description;
    public final String defaultValue;
    public final boolean isCommand;

    //used for booleans
    ClientRules(String name, String description, boolean isCommand) {
        this(name, "boolean", description, "false", isCommand);
    }

    //used for strings
    ClientRules(String name, String description, String defaultValue) {
        this(name, "string", description, defaultValue, false);
    }

    //used for wanting custom values
    ClientRules(String name, String type, String description, String defaultValue, boolean isCommand) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.defaultValue = defaultValue;
        this.isCommand = isCommand;
    }
    public void invertBoolean() {
        String value = ClientRuleHelper.clientRulesMap.get(this.name);
        if (this.type.equalsIgnoreCase("boolean") && value != null)
            ClientRuleHelper.clientRulesMap.put(this.name, String.valueOf(!Boolean.parseBoolean(value)));
    }

    public boolean getBoolean() {
        return Boolean.parseBoolean(ClientRuleHelper.clientRulesMap.get(this.name));
    }

    public int getInt() {
        String data =  ClientRuleHelper.clientRulesMap.get(this.name);
        if (data != null)
            return Integer.parseInt(data);
        return -1;
    }

    public double getDouble() {
        String data = ClientRuleHelper.clientRulesMap.get(this.name);
        if (data != null)
            return Double.parseDouble(data);
        return -1;
    }

    public String getString() {
        return ClientRuleHelper.clientRulesMap.get(this.name);
    }

    public void setValue(String newValue) {
        String data = ClientRuleHelper.clientRulesMap.remove(this.name);
        if (data == null)
            return;
        ClientRuleHelper.clientRulesMap.put(this.name, newValue);
    }
}
