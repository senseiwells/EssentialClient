# Client Macros

## Index
* [What is clientMacros?](#what-is-clientmacros)
* [How to use clientMacros](#how-to-use-clientmacros)
* [Available functions](#available-functions)
* [How to use conditions](#how-to-use-conditions)
* [Available conditions](#available-conditions)
* [Example scripts](#example-scripts)

## What is clientMacros?
ClientMacros is a feature of [EssentialClient](https://github.com/senseiwells/EssentialClient) that allows you to script your own macros directly into minecraft.
The benefit of this over traditional macros is that there is no need for external programs, and the ability to easily
share and script macros.

Client macros provides pre-defined functions that allow you to be able to manipulate player actions, as well as having
some basic conditionals allowing for more complex macros. 

The intended use is to be able to script a boring repetitive task into a macro and have the game do it for you, without having to write a whole mod for it.

## How to use clientMacros
You must have [EssentialClient](https://github.com/senseiwells/EssentialClient) installed, then after you have booted the game go to your .minecraft/config/EssentialClient folder.
If there is not a text file called "macro" then create one, then open the text file and this is where you can write your script.

In game, you can navigate to controls and find the Client Macro keybind which is under "EssentialClient", bind this to a key and once you press this key
in game the script you have written will execute (if valid). 

## Available functions

Anything that has a [ ] should be replaced with an appropriate value 

### `loop` 
- This function should always be used at the start of the macro to determine whether the macro should loop
- Usages: `loop true`, `loop false`

### `attack` 
- This function makes the player attack (or break)
- Usages: `attack once`, `attack hold` (this simulates holding down LMB), `attack interval [ticks]`, `attack stop`

### `use` 
- This function makes the player use (or place)
- Usages: `use once`, `use hold` (this simulates holding down RMB), `attack interval [ticks]`, `attack stop`

### `slot` 
- This function allows you to switch to a desired slot
- Usages: `slot [0-8]`

### `say` 
- This function sends a message to chat, can be any text (including commands)
- Usages: `say [text]`

### `sleep` 
- This will pause the script for a set amount of ticks
- Usages: `sleep [ticks]`

### `inventory` 
- This allows you to open your inventory or close an inventory
- Usages: `inventory open`, `inventory close`

### `walk` 
- This allows to manipulate whether your player is walking
- Usages: `walk hold`, `walk stop`

### `jump` 
- Makes your player jump
- Usages: `jump`

### `sprint` 
- Allows you to toggle sprinting
- Usages: `sprint true`, `sprint false`

### `sneak`
- Allows you to toggle sneaking
- Usages: `sneak true`, `sneak false`

### `look`
- Allows you to make the player look a certain direction
- Usages: `look [yaw] [pitch]`

### `drop`
- Allows you to drop the item you are holding
- Usages: `drop one`, `drop all`

### `drop_all`
- Allows you to drop all of a certain item
- Usages: `drop_all minecraft:[item_name]`

### `trade`
- Allows you to trade with a villager by using the index of the trade (first trade index == 0)
- [drop] allows you to drop all of the traded items after trading (options are "drop", or leave blank)
- Usages: `trade [index] [drop]`

### `screenshot`
- Allows you to take a screenshot
- Usages: `screenshot`

### `continue`
- This will return the macro back to the start of the loop
- Usages: `continue`

### `stop`
- This stops the macro
- Usages: `stop`

### `if`
- This allows you to use conditionals
- Read [here](#how-to-use-conditions) for more info

### `else`
- This allows you to use conditionals
- Read [here](#how-to-use-conditions) for more info

## How to use conditions

Using conditions in clientMacros is very simple, you are able to use `if`, `else if`, and `else`
Conditions are used to check whether something is true or false, and a list of things that you are able to check
are [here](#available-conditions).

The structure of conditions is very important, when using an if it must be structured like this:
```
if [condition] == [wanted_value] {
    [function]
}
```
and if the condition you are checking is a numerical value you are able to use other operators like this:
```
if [condition] < [wanted_value] {
    [function]
}
```
List of valid operators: `==`, `<`, `<=`, `>`, `>=`, `not`

As you can see above you can also use the `not` operator which just inverts the statement, for example:
```
if not [condition] == [wanted_value] {
    [function]
}
```

You can have as many functions in an if statement as you like, but you must remember to include `{` after the if, and `}`
after the condition is complete. You **cannot** have the brackets in other positions such as:
```
if [condition] < [wanted_value] 
{ 
    [function] 
}
```

To structure `else (if)` statements you must have an `if` that comes before hand an example would be:
```
if [condition] == [wanted_value] {
    [function]
}
else if [condition] == [wanted_value2] {
    [function2]
}
else {
    [function3]
}
```

You are also able to nest `if`s inside another (you may encounter bugs in long nests/chains, if so report them), an example here:
```
if [condition] == [wanted_value] {
    if [condition2] == [wanted_value2 {
        if [condition3] == [wanted_value3] {
            [function3]
        }
        else {
            [function2]
        }
    }
    else {
        [function]
    }
}
```

## Available Conditions

Anything that has a [ ] should be replaced with an appropriate value 

### `looking_at_block`
- This allows you to check the block that the player is looking at
- Usage: `if looking_at_block == minecraft:[block_name]`

### `looking_at_entity`
- This allows you to check the entity that the player is looking at
- Usage: `if looking_at_entity == minecraft:[entity_name]`

### `held_item`
- This is similar to `looking_at_block` however checks the item that the player is holding
- Usage: `if held_item == minecraft:[item_name]`

### `health`
- This allows you to check the players health
- Usage: `if health == [integer]`, `if health < [integer]`

### `is_trade_disabled`
- This allows you to check whether a trade is disabled or not using the index of a trade
- Usage: `if is_trade_disabled [index]`

### `inventory_is_full`
- This allows you to check if a player's inventory is full of items
- Usage: `if inventory_is_full`

### `in_inventory_gui`
- This allows you to check whether the player has an inventory gui open
- Usage: `if in_inventory_gui`

## Example scripts

### Command Script
A small script that essentially allows you to execute a command using a keybind.
In this case `/cs` which is a popular survival mod that toggles spectator mode
```
loop false
say /cs
```

### Sprint jumping script
This is again something simple that just allows you to continuously sprint jump
```
loop true
walk hold
sprint = true
jump
```

### Unloading shulkerboxes of iron ingots script
This is a good example of something specific, if you are unloading a lot of shulkers of iron ingots manually
obviously you can change iron ingots for other items but this is just an example
```
loop true
if looking_at_block == minecraft:shulker_box {
    use once
    sleep 10
    drop_all minecraft:iron_ingots
    sleep 2
    inventory close
}
```