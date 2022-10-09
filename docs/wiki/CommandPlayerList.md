# CommandPlayerList

## What is commandPlayerList

CommandPlayerList is a client side command that allows you to combine players you have saved from
[commandPlayerClient](https://github.com/senseiwells/EssentialClient/tree/master/docs/PlayerList.md)
to allow you to spawn multiple players in one command.

## How to use commandPlayerList

CommandPlayerClient is a feature of [EssentialClient](https://github.com/senseiwells/EssentialClient),
and relies on [commandPlayerClient](https://github.com/senseiwells/EssentialClient/tree/master/docs/PlayerList.md)
but requires [Carpet Mod](https://www.curseforge.com/minecraft/mc-mods/carpet)
on the server to function (since it relies on the server side command `/player`).

I would recommend using `commandPlayerClient` first as this completely relies on it to function.
After you have some players saved and have enabled the rule `commandPlayerList` you can continue.

Anything that has a [ ] should be replaced with an appropriate value

### `/playerlist createlist...`

- This command allows you to create a new list which you will use later to spawn multiple players at once.
- Usage: `/playerlist createlist [list_name]`
- Example: `/playerlist createlist mobswitches`

### `/playerlist deletelist...`

- Pretty self explanitory, just deletes one of the existing lists
- Usage: `/playerlist deletelist [list_name]`
- Example: `/playerlist deletelist mobswitches`

### `/playerlist addplayer...`

- The command allows you to add players to your lists.
- To add a player they must be already made in `/playerclient`
- You can add as many players as you want in a list
- Usage: `/playerlist addplayer [list_name] [player_name]`
- Example: `/playerlist addplayer mobswitches overworldswitch`, `/playerlist addplayer mobswitches netherswitch`

### `/playerlist spawnlist...`

- This command allows you to spawn a list you have already set
- Usage: `/playerlist spawnlist [list_name]`
- Example: `/playerlist spawnlist mobswitches`
    - This command would spawn a player called "overworldswitch" and "netherswitch" (as above I added those two players)

## Directly modifying the config file

Since everything is saved to a config you are able to manually modify this and also share it with other players.

An example config would look like this:

```json
{
  "mobswitches": "overworldswitch, netherswitch",
  "serverstart": "overworldswitch, netherswitch, bedbot, storage",
  "raidfarm": "raider, insurance, 001, 002, 003"
}
```

Again you can easily add/remove players and create/delete lists, just remember to format the json correctly.