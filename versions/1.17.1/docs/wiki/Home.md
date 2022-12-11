# EssentialClient

## Client Rules:
# afkLogout
Number of ticks until client will disconnect you from world (must be >= 200 to be active)
- Category: Utility
- Type: Integer
- Default Value: 0

# announceAFK
This announces when you become afk after a set amount of time (ticks)
- Category: Utility
- Type: Integer
- Default Value: 0

# announceAFKMessage
This is the message you announce after you are afk
- Category: Utility
- Type: String
- Default Value: I am now AFK

# announceBackMessage
This is the message you announce after you are back from being afk
- Category: Utility
- Type: String
- Default Value: 

# autoWalk
This will auto walk after you have held your key for set amount of ticks
- Category: Utility
- Type: Integer
- Default Value: 0

# betterAccurateBlockPlacement
This is the same as accurate block placement for tweakeroo but handled all client side, see controls...
- Category: Utility
- Type: Boolean
- Default Value: false

# carpetAlwaysSetDefault
This makes it so whenever you set a carpet rule it automatically sets it to default
- Category: Utility
- Type: Boolean
- Default Value: false

# chunkDebugMinimapBackground
This renders a box showing the bounds of the chunk debug minimap
- Category: Rendering
- Type: Boolean
- Default Value: true

# chunkDebugShowUnloadedChunks
This shows you unloaded chunks in ChunkDebug
- Category: Rendering
- Type: Boolean
- Default Value: false

# clientScriptAnnouncements
This messages in chat when a script starts and finishes
- Category: Miscellaneous
- Type: Boolean
- Default Value: true

# clientScriptFont
This allows you to change the font for client script errors
- Category: Rendering
- Type: Cycle
- Default Value: Minecraft

# commandAlternateDimension
This command calculates the coordinates of the alternate dimension
- Category: Commands
- Type: Boolean
- Default Value: false

# commandClientNick
This allows you to rename player names on the client
- Category: Commands
- Type: Boolean
- Default Value: false

# commandPlayerClient
This command allows you to save /player... commands and execute them
- Category: Commands
- Type: Boolean
- Default Value: false

# commandPlayerList
This command allows you to execute /player... commands in one command (requires commandPlayerClient)
- Category: Commands
- Type: Boolean
- Default Value: false

# commandRegion
This command allows you to determine the region you are in or the region at set coords
- Category: Commands
- Type: Boolean
- Default Value: false

# commandSuggesterIgnoresSpaces
This makes the command suggester ignore spaces
- Category: Utility
- Type: Boolean
- Default Value: false

# craftingHax
This allows you to craft items with the mouse
- Category: Utility
- Type: Boolean
- Default Value: false

# customClientCape
This allows you to select a Minecraft cape to wear, this only appears client side
- Category: Rendering
- Type: Cycle
- Default Value: None

# disableArmourRendering
This allows you to disable armour rendering for entities
- Category: Rendering
- Type: Cycle
- Default Value: None

# disableBobViewWhenHurt
Disables the camera bobbing when you get hurt
- Category: Utility
- Type: Boolean
- Default Value: false

# disableBossBar
This will disable the boss bar from rendering
- Category: Rendering
- Type: Boolean
- Default Value: false

# disableHotbarScrolling
This will prevent you from scrolling in your hotbar, learn to use hotkeys :)
- Category: Utility
- Type: Boolean
- Default Value: false

# disableJoinLeaveMessages
This will prevent join/leave messages from displaying
- Category: Rendering
- Type: Boolean
- Default Value: false

# disableMapRendering
This disables maps rendering in item frames
- Category: Rendering
- Type: Boolean
- Default Value: false

# disableNameTags
This disables all name tags from rendering
- Category: Rendering
- Type: Boolean
- Default Value: false

# disableNarrator
Disables cycling narrator when pressing CTRL + B
- Category: Miscellaneous
- Type: Boolean
- Default Value: false

# disableNightVisionFlash
Disables the flash that occurs when night vision is about to run out
- Category: Rendering
- Type: Boolean
- Default Value: false

# disableOpMessages
This will prevent system messages from displaying
- Category: Rendering
- Type: Boolean
- Default Value: false

# disableRecipeNotifications
Disables the recipe toast from showing
- Category: Rendering
- Type: Boolean
- Default Value: false

# disableScreenshotMessage
Disables the message that pops up when you take a screenshot
- Category: Rendering
- Type: Boolean
- Default Value: false

# disableTutorialNotifications
Disables the tutorial toast from showing
- Category: Rendering
- Type: Boolean
- Default Value: false

# displayRuleType
This allows you to choose the order you want rules to be displayed
- Category: Miscellaneous
- Type: Cycle
- Default Value: Alphabetical

# displayTimePlayed
This will display how long you have had your current client open for in the corner of the pause menu
- Category: Miscellaneous
- Type: Boolean
- Default Value: false

# essentialClientButton
This renders the Essential Client Menu on the main menu screen, and pause screen
- Category: Utility
- Type: Boolean
- Default Value: true

# highlightLavaSources
Highlights lava sources, credit to plusls for the original code for this
- Category: Rendering
- Type: Boolean
- Default Value: false

# highlightWaterSources
highlights water sources
- Category: Rendering
- Type: Boolean
- Default Value: false

# increaseSpectatorScrollSensitivity
Increases the sensitivity at which you can scroll to go faster in spectator
- Category: Utility
- Type: Integer
- Default Value: 0

# increaseSpectatorScrollSpeed
Increases the limit at which you can scroll to go faster in spectator
- Category: Utility
- Type: Boolean
- Default Value: false

# openScreenshotDirectory
This opens the screenshot directory instead of directly opening the screenshot
- Category: Utility
- Type: Boolean
- Default Value: false

# overrideCreativeWalkSpeed
This allows you to override the vanilla walk speed in creative mode
- Category: Utility
- Type: Double
- Default Value: 0.0

# permanentChatHud
This prevents the chat from being cleared, also applies when changing worlds/servers
- Category: Rendering
- Type: Boolean
- Default Value: false

# permanentTime
This forces your client to set a time of day
- Category: Rendering
- Type: Slider
- Default Value: -1

# quickLockRecipe
If you middle click a recipe it will put the name of the item in the search bar stopping you from craftin the wrong recipe
- Category: Utility
- Type: Boolean
- Default Value: false

# removeWarnReceivedPassengers
This removes the 'Received passengers for unknown entity' warning on the client
- Category: Miscellaneous
- Type: Boolean
- Default Value: false

# soulSpeedFovMultiplier
Determines the percentage of Fov scaling when walking on soil soul or soul sand
- Category: Rendering
- Type: Slider
- Default Value: 0.0

# stackableShulkersInPlayerInventories
This allows for shulkers to stack only in your inventory
- Category: Utility
- Type: Boolean
- Default Value: false

# stackableShulkersWithItems
This allows for shulkers with items to stack only in your inventory
- Category: Utility
- Type: Boolean
- Default Value: false

# startSelectedScriptsOnJoin
This will enable your selected scripts when you join a world automatically
- Category: Miscellaneous
- Type: Boolean
- Default Value: false

# survivalInventoryInCreative
This lets you open the survival inventory in creative mode
- Category: Utility
- Type: Boolean
- Default Value: false

# switchToTotem
This will switch to a totem (if you have one), under a set amount of health
- Category: Utility
- Type: Integer
- Default Value: 0

# titleTextToTop
Forces the Minecraft version and Mojang text to the top of the screen
- Category: Rendering
- Type: Boolean
- Default Value: false

# toggleTab
This allows you to toggle tab instead of holding to see tab
- Category: Utility
- Type: Boolean
- Default Value: false

# unlockAllRecipesOnJoin
Unlocks every recipe when joining a world
- Category: Utility
- Type: Boolean
- Default Value: false

# waterFovMultiplier
Determines the percentage of Fov scaling when fully submerged in water
- Category: Rendering
- Type: Slider
- Default Value: 0.0

