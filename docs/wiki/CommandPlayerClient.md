# CommandPlayerClient

## What is commandPlayerClient

CommandPlayerClient is a client side command that allows you to save `/player` (carpet mod feature) commands
and then allows you to execute the saved command at a later time, abstracting the command to a simple name.
This way there is no need to go between Minecraft and Discord copying and pasting `/player` commands.

## How to use commandPlayerClient

CommandPlayerClient is a feature of [EssentialClient](https://github.com/senseiwells/EssentialClient), but
requires [Carpet Mod](https://www.curseforge.com/minecraft/mc-mods/carpet)
on the server to function (since it relies on the server side command `/player`).

Once you have EssentialClient installed enable the rule `commandPlayerClient` in the Client Rules screen.
After this has been enabled, in game you should be able to execute a commmand called `/playerclient`, if not
relog and try again.

Anything that has a [ ] should be replaced with an appropriate value

### `/playerclient add...`

- This command allows you to add players to the config
- You are able to set the name, which is what you will use later to spawn the player
- You can specify the location, or alternatively you can use `here` which will just take the current location
  of the player
- You do not have to specify gamemode, if you do not it will allow you to spawn the player in any gamemode,
  however if you do specify gamemode it will prevent you from spawning a player if you are in the incorrect gamemode.
- Usage: `/playerclient add [name] spawn at [x] [y] [z] facing [yaw] [pitch] in [dimension] in [gamemode]`
  , `/playerclient add [name] spawn here in [gamemode]`
- Example: `/playerclient add senseiwells spawn at 0 128 0 facing 80 -90 in overworld in spectator`
  , `/playerclient add senseiwells spawn here in any`

### `/playerclient remove...`

- This command is pretty self explanitory, it will remove a player from your config
- Usage: `/playerclient remove [name]`
- Example: `/playerclient remove senseiwells`

### `/playerclient spawn...`

- This command allows you to spawn the players that you have saved to your config
- You are able to offset the spawning position from the one saved in the config
- Usage: `/playerclient spawn [name]`, `/playerclient spawn [name] offset [x] [y] [z]`
- Example `/playerclient spawn senseiwells`, `playerclient spawn senseiwells offset 100 0 20`

## Directly modifying the config file

Since everything is saved to a config you are able to manually modify this and also share it with other players.

An example config would look like this:

```json
{
  "SleepBot": {
    "z": 3784.51,
    "yaw": -450.69,
    "pitch": 8.25,
    "dimension": "overworld",
    "gamemode": "survival",
    "name": "SleepBot",
    "x": -1848.30,
    "y": 244.94
  },
  "NetherSwitch": {
    "z": -2498.47,
    "yaw": 812.02,
    "pitch": 73.20,
    "dimension": "overworld",
    "gamemode": "any",
    "name": "NetherSwitch",
    "x": -7575.70,
    "y": 73.00
  },
  "OverworldSwitch": {
    "z": 487,
    "yaw": 90,
    "pitch": 40,
    "dimension": "the_nether",
    "gamemode": "any",
    "name": "OverworldSwitch",
    "x": -14.5,
    "y": 158.00
  }
}
```

You can easily modify this and add/remove players from your config, just remember to format it correctly; the second to
last
curly bracket doesn't have a comma after it while the rest of them do, and remember the config stores the name twice.
The first
name is used for the command and getting the data whereas the other is for when executing the command it uses that name.