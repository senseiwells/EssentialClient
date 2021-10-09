package essentialclient.feature.clientrule;

import java.util.Arrays;

public enum ClientRules {
    //Boolean Rules
    COMMAND_MUSIC                           ("commandMusic"                             , "This command allows you to manipulate the current music"                                                 , true),
    COMMAND_PLAYERCLIENT                    ("commandPlayerClient"                      , "This command allows you to save /player... commands and execute them"                                    , true),
    COMMAND_PLAYERLIST                      ("commandPlayerList"                        , "This command allows you to execute /player... commands in one command (requires commandPlayerClient)"    , true),
    COMMAND_REGION                          ("commandRegion"                            , "This command allows you to determine the region you are in or the region at set coords"                  , true),
    COMMAND_TRAVEL                          ("commandTravel"                            , "This command allows you to travel to a set location"                                                     , true),
    DISABLE_BOB_VIEW_WHEN_HURT              ("disableBobViewWhenHurt"                   , "Disables the camera bobbing when you get hurt"                                                           , false),
    DISABLE_HOTBAR_SCROLLING                ("disableHotbarScrolling"                   , "This will prevent you from scrolling in your hotbar, learn to use hotkeys :)"                            , false),
    DISABLE_JOIN_LEAVE_MESSAGES             ("disableJoinLeaveMessages"                 , "This will prevent join/leave messages from displaying"                                                   , false),
    DISABLE_NARRATOR                        ("disableNarrator"                          , "Disables cycling narrator when pressing CTRL + B"                                                        , false),
    DISABLE_NIGHT_VISION_FLASH              ("disableNightVisionFlash"                  , "Disables the flash that occurs when night vision is about to run out"                                    , false ),
    DISABLE_OP_MESSAGES                     ("disableOpMessages"                        , "This will prevent system messages from displaying"                                                       , false),
    DISABLE_RECIPE_NOTIFICATIONS            ("disableRecipeNotifications"               , "Disables the recipe toast from showing"                                                                  , false),
    DISABLE_TUTORIAL_NOTIFICATIONS          ("disableTutorialNotifications"             , "Disables the tutorial toast from showing"                                                                , false),
    DISPLAY_TIME_PLAYED                     ("displayTimePlayed"                        , "This will display how long you have had your current client open for in the corner of the pause menu"    , false),
    ESSENTIAL_CLIENT_MAIN_MENU              ("essentialClientMainMenu"                  , "This renders the Essential Client Menu on the main menu screen"                                          , false),
    HIGHLIGHT_LAVA_SOURCES                  ("highlightLavaSources"                     , "Highlights lava sources, credit to plusls for the original code for this"                                , false),
    INCREASE_SPECTATOR_SCROLL_SPEED         ("increaseSpectatorScrollSpeed"             , "Increases the limit at which you can scroll to go faster in spectator"                                   , false),
    MISSING_TOOLS                           ("missingTools"                             , "Adds client functionality to missingTools from Carpet for the client"                                    , false),
    REMOVE_WARN_RECEIVED_PASSENGERS         ("removeWarnReceivedPassengers"             , "This removes the 'Received passengers for unknown entity' warning on the client"                         , false),
    STACKABLE_SHULKERS_IN_PLAYER_INVENTORIES("stackableShulkersInPlayerInventories"     , "This allows for shulkers to stack only in your inventory"                                                , false),
    STACKABLE_SHULKERS_WITH_ITEMS           ("stackableShulkersWithItems"               , "This allows for shulkers with items to stack only in your inventory"                                     , false),
    UNLOCK_ALL_RECIPES_ON_JOIN              ("unlockAllRecipesOnJoin"                   , "Unlocks every recipe when joining a singleplayer world"                                                  , false),

    //Number Rules
    ANNOUNCE_AFK                            ("announceAFK"                              , Type.INTEGER          , "This announces when you become afk after a set amount of time (ticks)"       , "0"           , false),
    AUTO_WALK                               ("autoWalk"                                 , Type.INTEGER          , "This will auto walk after you have held your key for set amount of ticks"    , "0"           , false),
    INCREASE_SPECTATOR_SCROLL_SENSITIVITY   ("increaseSpectatorScrollSensitivity"       , Type.INTEGER          , "Increases the sensitivity at which you can scroll to go faster in spectator" , "0"           , false),
    MUSIC_INTERVAL                          ("musicInterval"                            , Type.INTEGER          , "The amount of ticks between each soundtrack that is played, 0 = random"      , "0"           , false),
    OVERRIDE_CREATIVE_WALK_SPEED            ("overrideCreativeWalkSpeed"                , Type.DOUBLE           , "This allows you to override the vanilla walk speed in creative mode"         , "0.0"         , false),
    SWITCH_TO_TOTEM                         ("switchToTotem"                            , Type.INTEGER          , "This will switch to a totem (if you have one), under a set amount of health" , "0"           , false),

    //String Rules
    ANNOUNCE_AFK_MESSAGE                    ("announceAFKMessage"                       , "This is the message you announce after you are afk"              , "I am now AFK"),
    CLIENT_SCRIPT_FILENAME                  ("clientScriptFilename"                     , "This allows you to choose a specific script file name"           , "clientscript"),

    //Cycling Rules
    DISPLAY_RULE_TYPE                       ("displayRuleType"                          , Type.CYCLE            , "This allows you to choose the order you want rules to be displayed"          , "Alphabetical", new String[]{"Alphabetical", "Rule Type"}                                                                             , false),
    MUSIC_TYPES                             ("musicTypes"                               , Type.CYCLE            , "This allows you to select what music types play"                             , "Default"     , new String[]{"Default", "Overworld", "Nether", "Overwrld + Nethr", "End", "Creative", "Menu", "Credits", "Any"}       , false);

    public final String name;
    public final Type type;
    public final String description;
    public final String defaultValue;
    public final String[] cycleValues;
    public final boolean isCommand;

    //used for booleans
    ClientRules(String name, String description, boolean isCommand) {
        this(name, Type.BOOLEAN, description, "false", isCommand);
    }

    //used for strings
    ClientRules(String name, String description, String defaultValue) {
        this(name, Type.STRING, description, defaultValue, false);
    }

    ClientRules(String name, Type type, String description, String defaultValue, boolean isCommand) {
        this(name, type, description, defaultValue, null, isCommand);
    }

    //used for wanting custom values
    ClientRules(String name, Type type, String description, String defaultValue, String[] cycleValues, boolean isCommand) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.defaultValue = defaultValue;
        this.cycleValues = cycleValues;
        this.isCommand = isCommand;
    }
    public void invertBoolean() {
        String value = ClientRuleHelper.clientRulesMap.get(this.name);
        if (this.type == Type.BOOLEAN && value != null)
            ClientRuleHelper.clientRulesMap.put(this.name, String.valueOf(!Boolean.parseBoolean(value)));
    }

    public void cycleValues() {
        String value = ClientRuleHelper.clientRulesMap.get(this.name);
        if (this.type == Type.CYCLE && value != null) {
            int index = Arrays.asList(this.cycleValues).indexOf(value);
            if (index == this.cycleValues.length - 1)
                index = 0;
            else
                index++;
            ClientRuleHelper.clientRulesMap.put(this.name, this.cycleValues[index]);
        }
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
        STRING ("String", String.class),
        CYCLE  ("Cycle", String.class);

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
