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
    DISABLEOPMESSAGES                       ("disableOpMessages"                        , "This will prevent system messages from displaying"                                                       , false),
    DISABLEHOTBARSCROLLING                  ("disableHotbarScrolling"                   , "This will prevent you from scrolling in your hotbar, learn to use hotkeys :)"                            , false),
    DISABLEJOINLEAVEMESSAGES                ("disableJoinLeaveMessages"                 , "This will prevent join/leave messages from displaying"                                                   , false),
    DISPLAYTIMEPLAYED                       ("displayTimePlayed"                        , "This will display how long you have had your current client open for in the corner of the pause menu."   , false),
    ESSENTIALCLIENTMAINMENU                 ("essentialClientMainMenu"                  , "This renders the Essential Client Menu on the main menu screen"                                          , false),
    MISSINGTOOLS                            ("missingTools"                             , "Adds client functionality to missingTools from Carpet for the client"                                    , false),
    INCREASESPECTATORSCROLLSPEED            ("increaseSpectatorScrollSpeed"             , "Increases the limit at which you can scroll to go faster in spectator"                                   , false),
    HIGHLIGHTLAVASOURCES                    ("highlightLavaSources"                     , "Highlights lava sources, credit to plusls for the original code for this"                                , false),
    REMOVEWARNRECEIVEDPASSENGERS            ("removeWarnReceivedPassengers"             , "This removes the 'Received passengers for unknown entity' warning on the client"                         , false),
    STACKABLESHULKERSINPLAYERINVENTORIES    ("stackableShulkersInPlayerInventories"     , "This allows for shulkers to stack only in your inventory"                                                , false),
    STACKABLESHULKERSWITHITEMS              ("stackableShulkersWithItems"               , "This allows for shulkers with items to stack only in your inventory"                                     , false),
    UNLOCKALLRECIPESONJOIN                  ("unlockAllRecipesOnJoin"                   , "Unlocks every recipe when joining a singleplayer world"                                                  , false),

    //Number Rules
    ANNOUNCEAFK                             ("announceAFK"                              , Type.INTEGER          , "This announces when you become afk after a set amount of time (ticks)"       , "0"           , false),
    AUTOWALK                                ("autoWalk"                                 , Type.INTEGER          , "This will auto walk after you have held your key for set amount of ticks"    , "0"           , false),
    INCREASESPECTATORSCROLLSENSITIVITY      ("increaseSpectatorScrollSensitivity"       , Type.INTEGER          , "Increases the sensitivity at which you can scroll to go faster in spectator" , "0"           , false),
    OVERRIDECREATIVEWALKSPEED               ("overrideCreativeWalkSpeed"                , Type.DOUBLE           , "This allows you to override the vanilla walk speed in creative mode"         , "0.0"         , false),
    SWITCHTOTOTEM                           ("switchToTotem"                            , Type.INTEGER          , "This will switch to a totem (if you have one), under a set amount of health" , "0"           , false),

    //String Rules
    ANNOUNCEAFKMESSAGE                      ("announceAFKMessage"                       , "This is the message you announce after you are afk", "I am now AFK");

    public final String name;
    public final Type type;
    public final String description;
    public final String defaultValue;
    public final boolean isCommand;

    //used for booleans
    ClientRules(String name, String description, boolean isCommand) {
        this(name, Type.BOOLEAN, description, "false", isCommand);
    }

    //used for strings
    ClientRules(String name, String description, String defaultValue) {
        this(name, Type.STRING, description, defaultValue, false);
    }

    //used for wanting custom values
    ClientRules(String name, Type type, String description, String defaultValue, boolean isCommand) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.defaultValue = defaultValue;
        this.isCommand = isCommand;
    }
    public void invertBoolean() {
        String value = ClientRuleHelper.clientRulesMap.get(this.name);
        if (this.type == Type.BOOLEAN && value != null)
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

    public enum Type {
        BOOLEAN ("Boolean", Boolean.class),
        INTEGER ("Integer", Integer.class),
        DOUBLE ("Double", Double.class),
        STRING ("String", String.class);

        private final String name;
        private final Class<?> classType;

        Type(String string, Class<?> classType) {
            this.name = string;
            this.classType = classType;
        }

        public Class<?> getTypeClass() {
            return this.classType;
        }

        @Override
        public String toString() {
            return this.name;
        }

    }
}
