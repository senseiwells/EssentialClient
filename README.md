# EssentialClient
EssentialClient is a client side only mod originally forked from [Carpet Client for 1.15.2](https://github.com/gnembon/carpet-client) that implements new client side features.

This mod is currently ONLY for 1.16.5 and requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) v0.35.1+ and [Carpet Mod](https://www.curseforge.com/minecraft/mc-mods/carpet) v1.4.26+.

Feel free to contribute by adding as many features as you want!

## Index of Client Rules

* [announceAFK]()
* [announceAFKMessage]()
* [autoWalk]()
* [commandMusic]()
* [commandPlayerClient]()
* [commandPlayerList]()
* [commandRegion]()
* [commandTravel]()
* [disableBobViewWhenHurt]()
* [disableHotbarScrolling]()
* [disableJoinLeaveMessages]()
* [disableNarrator]()
* [disableNightVisionFlash]()
* [disableOpMessages]()
* [disableRecipeNotifications]()
* [disableTutorialNotifications]()
* [displayRuleType]()
* [displayTimePlayed]()
* [essentialClientMainMenu]()
* [highlightLavaSources]()
* [increaseSpectatorScrollSensitivity]()
* [increaseSpectatorScrollSpeed]()
* [missingTools]()
* [musicInterval]()
* [musicTypes]()
* [overrideCreativeWalkSpeed]()
* [removeWarnReceivedPassengers]()
* [stackableShulkerInPlayerInventories]()
* [stackableShulkersWithItems]()
* [switchToTotem]()
* [unlockAllRecipesOnJoin]()

#Index of Other Features:

* [carpetClient]()
* [clientMacros]()
* [rebindF3]()

# Client Rules:

For commands any value inside [ ] are variables and should be replaced with real values when using the command

##announceAFK
This announces when you become afk after a set amount of time (ticks), 
* Type: `Integer`
* Default Value: `0`
* Extra Info: 
  * This is judges by weather your player position is constant
  * Prints the message determined by [announceAFKMessage](#announceafkmessage)

##announceAFKMessage
This is the message you announce after you are afk"
* Type: `String`
* Default Value: `I am now AFK`
* Extra Info:
    * Requires [announceAFK](#announceafk)

##autoWalk
This will auto walk after you have held your key for set amount of ticks
* Type: `Integer`
* Default Value: `0`
* Extra Info:
  * Once auto walking press backwards or forward again to cancel

##commandMusic
This command allows you to manipulate the current music
* Type: `Boolean`
* Default Value: `false`
* Extra Info:
  * Usage: `/music skip`, `/music play [musictype]`, `music volume [percent]`

##commandPlayerClient
This command allows you to save /player... commands and execute them
* Type `Boolean`
* Default Value: `false`
* Extra Info:
  * Requires `commandPlayer` (from carpet) on server/singeplayer to be `true`
  * Documentation on how to use [here]()

##commandPlayerList
This command allows you to execute /player... commands in one command
* Type: `Boolean`
* Default Value: `false`
* Extra Info:
  * Requires [commandPlayerClient](#commandplayerclient)
  * Documentation on how to use [here]()

##commandRegion
This command allows you to determine the region you are in or the region at set coords
* Type: `Boolean`
* Default Value: `false`
* Extra Info:
  * Usage: `/region get`, `/region get [x] [y]`

##commandTravel
This command allows you to travel to a set location
* Type: `Boolean`
* Default Value: `false`
* Extra Info:
  * Usage: `/travel start [x] [y]`, `/travel stop`

##disableBobViewWhenHurt
Disables the camera bobbing when you get hurt
* Type: `Boolean`
* Default Value: `false`

##disableHotbarScrolling
This will prevent you from scrolling in your hotbar
* Type: `Boolean`
* Default Value: `false`
* Extra Info:
  * Learn to use hotkeys :)

##disableJoinLeaveMessages
This will prevent join/leave messages from displaying
* Type: `Boolean`
* Default Value: `false`

##disableNarrator
Disables cycling narrator when pressing CTRL + B
* Type: `Boolean`
* Default Value: `false`

##disableNightVisionFlash
Disables the flash that occurs when night vision is about to run out
* Type: `Boolean`
* Default Value: `false`

##disableOpMessages
This will prevent system messages from displaying
* Type: `Boolean`
* Default Value: `false`

##disableRecipeNotifications
Disables the recipe toast from showing
* Type: `Boolean`
* Default Value: `false`

##disableTutorialNotifications
Disables the tutorial toast from showing
* Type: `Boolean`
* Default Value: `false`
* Extra Info:
  * Useful for when switching versions when using vanilla launcher

##displayRuleType
This allows you to choose the order you want rules to be displayed
* Type: `Cycle`
* Default Value: `Alphabetical`
* Extra Info: 
  * Current options: `Alphabetical` and `RuleType`

##displayTimePlayed
This will display how long you have had your current client open for in the corner of the pause menu
* Type: `Boolean`
* Default Value: `false`
* Extra Info:
    * Now you can see how much time you've ~~wasted~~ been productive

##essentialClientMainMenu
This renders the Essential Client Menu on the main menu screen
* Type: `Boolean`
* Default Value: `false`
* Extra Info:
  * This doesn't default to `true` because it might conflict with replay mod

##highlightLavaSource
Highlights lava sources, credit to [plusls](https://github.com/plusls) for the original code for this in [their mod](https://github.com/plusls/oh-my-minecraft-client)
* Type: `Boolean`
* Default Value: `false`

##increaseSpectatorScrollSensitivity
Increases the sensitivity at which you can scroll to go faster in spectator
* Type: `Boolean`
* Default Value: `false`
* Extra Info:
  * Couples nicely with [increaseSpectatorScrollSpeed](#increasespectatorscrollspeed)

##increaseSpectatorScrollSpeed
Increases the limit at which you can scroll to go faster in spectator
* Type: `Boolean`
* Default Value: `false`
* Extra Info:
  * You can now go faster than ever before!

##missingTools
Adds client functionality to missingTools from Carpet for the client
* Type: `Boolean`
* Default Value: `false`
* Extra Info:
  * This is implemented to fix any desync issues with servers, doesn't work if server doesn't have this rule enabled

##musicInterval
The amount of ticks between each soundtrack that is played
* Type: `Integer`
* Default Value: `0`
* Extra Info:
  * 0 = (Vanilla Behaviour) random

##musicTypes
This allows you to select what music types play
* Type: `Cycle`
* Default Value: `Default`
* Extra Info:
  * Current Options: `Default`, `Overworld`, `Nether`, `Overworld + Nether`, `End`, `Creative`, `Menu`, `Credits`, and `Any`

##overrideCreativeWalkSpeed
This allows you to override the vanilla walk speed in creative mode
* Type: `Double`
* Default Value: `0.0`
* Extra Info:
  * Limited to creative mode, stop thinking of cheating

##removeWarnReceivedPassengers
"This removes the 'Received passengers for unknown entity' warning on the client
* Type: `Boolean`
* Default Value: `false`
* Extra Info:
  * This warning just clogs up logs when arround Minecarts

##stackableShulkersInPlayerInventories
This allows for shulkers to stack only in your inventory
* Type: `Boolean`
* Default Value: `false`
* Extra Info:
  * This only works if the server has [EssentialAddons](https://github.com/Super-Santa/EssentialAddons) installed with `stackableShulkersInPlayerInventories` enabled

##stackableShulkersWithItems
This allows for shulkers with items to stack only in your inventory
* Type: `Boolean`
* Default Value: `false`
* Extra Info:
  * This requires [stackableShulkersInPlayerInventories](#stackableshulkersinplayerinventories)

##switchToTotem
This will switch to a totem (if you have one), under a set amount of health
* Type: `Integer`
* Default Value: `0`
* Extra Info:
  * Health is out of 20

##unlockAllRecipesOnJoin
Unlocks every recipe when joining a singleplayer world
* Type: `Boolean`
* Default Value: `false`
* Extra Info:
  * This currently only works in singleplayer

#Other Features

##carpetClient
This is the code that was left over from the carpet client 1.15.2 version, this allows you to use a GUI to modify carpet rules instead of having to use commands, this only works in singleplayer.

You can find more about it on the original github: [here](https://github.com/gnembon/carpet-client)

##clientMacros
This is a mini scripting language that allows you to make simple macros directly in minecraft without any external programs.

Documentation and how to use can be found: [here]()

##rebindF3
This allows you to rebind the F3 key, the option for this is in controls under "EssentialClient"
