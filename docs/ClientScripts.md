# ClientScript

## What is it?

ClientScript is a feature of [EssentialClient](https://github.com/senseiwells/EssentialClient) what allows you to write
scripts for the client directly into Minecraft. It extends [Arucas](https://github.com/senseiwells/Arucas) which is
an interpreted language, with very similar syntax to Java using Java as it's host language,
[Arucas](https://github.com/senseiwells/Arucas), has the core functionality of a programming language and ClientScript
adds all the Minecraft functions.

The main purpose of this feature is to make automating the player easier without having to use any external programs or
macros, and without having to create a whole mod for simple tasks.

## How do you use it?

You must have [EssentialClient](https://github.com/senseiwells/EssentialClient) installed, then after you have booted the
game, you can navigate to the Essential Client Menu (to get to this menu you must join a world and press `ESC`). Once
you have opened this menu there will be an option to open your Script file (in the bottom right), this will open a prompt
asking you what program to open the file with, open it with any text editor (Visual Studio Code has syntax [highlighting support](https://cdn.discordapp.com/attachments/894353843738017802/897616637832884264/arucas.zip)). You can also
change the current Script file that you open, and run by changing the `clientScriptFilename` in the Client Rules Menu.
ClientScripts are stored in `.minecraft/config/EssentialClient/Scripts`.

In game, you can navigate to controls and bind `Client Script` to a keybind. Once you press this key in game the script
will execute, if the script fails it will notify you in game, and will give you an error of why it failed making debugging
convenient.

# What's avaiable in the language?

## Variables:

There are 6 variable types that this language supports:

`Double` - This stores floating point numbers, can store integers too

`String` - This stores any text

`Boolean` - This stores either a true or false

`Functions` - This can store a function as a variable

`Null` - This just means that there is no value

`List` - This can store a list of any other values that the language supports

## Basic Operators:

`+` - Adds 2 numbers together, or concatenates 2 strings

`-` - Subtracts 1 number from another

`*` - Multiplies 2 numbers together

`/` - Divides ` number from another

`^` - Exponent of one number to another

`//` - Used to comment

`/* */` - Used for block comments

## Logical Operators:

`and` or `&&` - This is the logical AND

`or` or `||` - This is the logical OR

`not` or `!` - This is the logical NOT

## Keywords:

### `var`

- This is used for initialising any variables, this is not required for initialising variables, think of it as a notation
- Examples:
  - `var number = 0;`
  - `var string = "foo";`

### `fun`

- This is used for defining functions, or for defining lambdas
- Example:
  - `fun foo(bar) { print(bar); }`
  - `fun () { print("this is a lambda"); }`

### `return`

- This is used for returning values in functions
- Example: `fun example() { return "foo bar"; }`

### `if`

- This is used to evaluate a conditional statement
- Examples: `if(true) { print("bar"); }`

### `else`

- This is used to further evaluate a conditional statement following an `if`, can also be used in conjuction with `if`
  for an `else if` statement
- Examples: `if (false) { print("foo"); } else if (false) { print("bar"); } else { print("baz); }`

### `while`

- This is used to loop while evaluating a conditional statement
- Example: `while (true) { print("This infinite loop with crash"); }`

### `continue`

- This keyword is used to return to the top of the loop in a `while` loop
- Example: `continue;`

### `break`

- This is used to break out of a loop
- Example: `break;`

### `{`

- This is used to create a scope block that can contain multiple lines
- Example: `fun example() { print("foo"); print("bar"); print("baz"); }`

### `}`

- This is used after the ending of your code block to close it off
- Example: `fun example() { print("foo"); print("bar"); print("baz"); }`

## Basic Functions:

These are the functions that Acuras Supports:

Anything inside brackets are parameters and these values are used in the function to perform an action

### `run(filePath)`

- This is used to run a `.acuras` file, you can use one script to run other scripts
- Example: `run(E:/foo/bar.acuras);`

### `stop()`

- This is used to stop a program
- Example: `stop();`
### `debug(boolean)`

- This is used to enable debug mode, which just prints all returns to console
- Example: `debug(true);`
-
### `print(string)`

- This is used to print a string value to console
- Example: `print("Hello World");`

### `sleep(milliseconds)`

- This pauses your program for set amount of ms
- Example: `sleep(1000);`

### `schedule(milliseconds, function)`

- This schedules a function to run in set amount of ms. This also pushes the function to a separate thread.
- The function that you pass through cannot have arguments
- Example: `schedule(10000, stop);`

### `random(bound)`

- This gets a pseudo random integer between 0 and the bound
- Example: `random(10);`
- Returns NumberValue: `5`

### `round(number)`

- This rounds a number to the nearest integer
- Example: `round(99.9);`
- Returns NumberValue: `100`

### `roundUp(number)`

- This rounds a number up to the next integer
- Example: `roundUp(0.1);`
- Returns NumberValue: `1`

### `roundDown(number)`

- This rounds a number down to the next integer
- Example: `roundDown(0.9);`
- Returns NumberValue: `0`

### `modulus(num1, num2)`

- This returns the remainder of num1/num2
- Example: `modulus(9, 2);`
- Returns NumberValue: `1`

### `len(value)`

- Returns the length of a value passed in, works with strings and lists
- Example: `len("What is the length of this string?");`
- Returns NumberValue `34`

### `stringToList(string)`

- Returns a list of characters from the string
- Example: `stringToList("stringToList");`
- Returns ListValue: `["s", "t", "r", "i", "n", "g", " ", "T", "o", " ", "L", "i", "s", "t"]`

### `stringOf(value)`

- Returns string of whatever value input
- Example: `stringOf(9008);`
- Returns StringValue: `"9008"`

### `numberOf(string)`

- Returns number of a string
- Example: `numberOf("8892");`
- Returns NumberValue: `8892`

### `getTime()`

- This gets the current time as a string
- Example: `getTime();`
- Returns NumberValue: `00:18`

### `isNumber(value)`

- This returns a boolean based on whether value is a number
- Example: `isNumber("this is not a number");`
- Returns BooleanValue: `false`

### `isString(value)`

- This returns a boolean based on whether value is a string
- Example: `isString("this is a string");`
- Returns BooleanValue: `true`

### `isBoolean(value)`

- This returns a boolean based on whether value is a boolean
- Example: `isBoolean(80);`
- Returns BooleanValue: `false`

### `isFunction(value)`

- This returns a boolean based on whether value is a function
- Example: `isFunction(false);`
- Returns BooleanValue: `false`

### `isList(value)`

- This returns a boolean based on whether value is a list
- Example: `isList(null);`
- Returns BooleanValue: `false`

### `getIndex(list, index)`

- This is a list variable specific function, it returns the value at index of a list, index starts at 0
- Example: `var stringList = ["foo", "bar", "baz"]; getIndex(stringList, 0);`
- Returns Value: `"foo"`

### `removeIndex(list, index)`

- This is a list variable specific function, it removes a value from a list at index
- Example: `var stringList = ["foo", "bar", "baz"]; removeIndex(stringList, 0);`
- Returns Value: `"foo"`

### `append(list, value)`

- This is a list variable specific function, it allows you to add a value to a list
- Example: `var list = []; append(list, 3.14159);`

### `concat(list, otherList)`

- This is a list variable specific function, it allows you to concatenate two lists
- Example: `var list1 = ["foo"]; var list2 = ["bar", "baz"]; concat(list1, list2);`
- Returns ListValue: `["foo", "bar", "baz"]`

## Minecraft Functions

These are functions that hook into Minecraft

### `use(type)`

- This allows you to manipulate whether your player is using, pass "hold", "stop", or "once" as a parameter
- Example: `use("once");`

### `attack(type)`

- This allows you to manipulate whether your player is attacking, pass "hold", "stop", or "once" as a parameter
- Example: `attack("hold");`

### `message(string)`

- This allows you to send a message to your player, only they will see this
- Example: `message("Hello!");`

### `messageActionBar(string)`

- This allows you to set the current message displaying on the action bar
- Example: `messageActionBar("Hello!");`

### `say(string);`

- This allows you to make your player send a message in chat, this includes commands
- Examples:
  - `say("Hi :)");`
  - `say("/gamemode creative");`

### `addCommand(commandName, arguments)`

- This allows you to add your own custom commands
- Example: `addCommand("cs", 0)`

### `setSelectedSlot(slotNumber)`

- This allows you to set the slot number your player is holding, slots must be between 1-9
- Example: `setSelectedSlot(5);`

### `inventory(type)`

- This allows you to manipulate the inventory screen, pass "open", or "close" as a parameter
- Example: `inventory("close");`

### `setWalking(boolean)`

- This allows you to set whether your player is holding the walk key or not
- Example: `setWalking(true);`

### `setSprinting(boolean)`

- This allows you to set whether your player is sprinting or not
- Example: `setSprinting(false);`

### `setSneaking(boolean)`

- This allows you to set whether your player is sneaking or not
- Example: `setSneaking(true);`

### `dropItemInHand(boolean)`

- This allows you to drop the item(s) you are currently holding, passing in true as a parameter will drop the whole stack
- Example: `dropItemInHand(true);`

### `dropAll(itemType)`

- This allows you to drop all of a select item type from your inventory, pass in itemType as string
- Example: `dropAll("diamond_block");`

### `tradeIndex(index, boolean)`

- This allows you to trade with a villager at a certain index, passing true into second parameter will drop all the traded items
- Example: `tradeIndex(2, true);`

### `tradeFor(itemType, boolean)`

- This allows you to trade with a villager for a certain item, passing true into second parameter will drop all the traded items
- Example: `tradeFor("quartz_block", true);`

### `swapSlots(slot1, slot2)`

- This allows you to swap 2 slots with one another
- Example: `swapSlots(13, 46)`

### `craftRecipe(recipe)`

- This allows you to craft an item using the recipe book
- Example: `craftRecipe(["birch_planks", "air", "air", "air", "air", "air", "air", "air", "air")]);`
- ^ This would craft birch buttons

### `screenshot()`

- This takes a screenshot
- Example: `screenshot()`

### `look(yaw, pitch)`

- This allows you to manipulate where your player is looking
- Example: `look(-76.2, 80.1);`

### `jump()`

- This will make the player jump if they are on the ground
- Example: `jump()`

### `clearChat()`

- This will clear the chat history
- Example: `clearChat();`

### `hold()`

- This stops the macro from ending after it has executed all of its code, meaning it needs to be stopped manually
- Example: `hold()`


## Minecraft Functions (that return values)

### `getCurrentSlot()`

- This returns the current slot the player has selected as a number
- Example: `getCurrentSlot();`
- Returns NumberValue: `5`

### `getHeldItem()`

- This returns the current item the player is holding as a string, if player was holding a grass block it would return "grass_block"
- Example: `getHeldItem();`
- Returns StringValue: `"grass_block"`

### `getStatusEffects()`

- This returns the current status effects the player currently has
- Example: `getStatusEffects()`
- Returns ListValue: `["resistance", "fire_resistance", "regeneration"]`

### `getLookingAtBlock()`

- This returns the block that the player is looking at as a string, if looking at nothing will return "air"
- Example: `getLookingAtBlock();`
- Returns StringValue: `"diamond_block"`

### `getLookingAtEntity()`

- This returns the entity the player is looking at as a string, if looking at no entity will return "none"
- Example: `getLookingAtEntity();`
- Returns StringValue: `"villager"`

### `getHealth()`

- This returns the player's current health
- Example: `getHealth();`
- Returns NumberValue: `18`

### `getPos()`

- This returns the position as a list containing `x`, `y`, `z`, `yaw`, `pitch`
- Example: `getPos();`
- Returns ListValue: `[100, 40, 200, 90, 0]`

### `getDimension()`

- This returns the current dimension that the player is in as a string
- Example: `getDimension();`
- Returns StringValue: `"overworld"`

### `getBlockAt(x, y, z)`

- This returns the block at the position given in the parameters as a string, if it is out of render then it will return "void_air"
- Example: `getBlockAt(getPos("x"), getPos("y") - 1, getPos("z"));`
- Returns StringValue: `"dirt"`

### `getScriptsPath()`

- This returns the path that your scripts are stored as a string
- Example: `getScriptsPath();`
- Returns StringValue: `"C:/.minecraft/config/EssentialClient/Scripts"`

### `isTradeDisabled(itemType)` and `isTradeDisabled(index)`

- This returns whether a trade is disabled as a boolean, you can either pass an index or item type as a parameter
- Examples:
  - `isTradeDisabled("experience_bottle");`
  - `isTradeDisabled(3);`
- Returns BooleanValue: `false`

### `getEnchantmentsForTrade(itemType)` and `getEnchatmentsForTrade(index)`

- This returns the enchantments that a trade item has
- Examples:
  - `getEnchantmentsForTrade("diamond_pickaxe");`
  - `getEnchantmentsForTrade(5);`
- Returns ListValue: `[["efficiency", 5]. ["unbreaking", 3], ["mending", 1]]`

### `doesVillagerHaveTrade(itemType)`

- This returns whether a villager has a trade for an item type as a boolean
- Example: `doesVillagerHaveTrade("green_terracotta");`
- Returns BooleanValue: `true`

### `isInventoryFull()`

- This returns whether the players inventory is full as a boolean
- Example: `isInventoryFull();`
- Returns BooleanValue: `true`

### `isInInventoryGui()`

- This returns whether the player is inside an inventory Gui as a boolean
- Example: `isInInventoryGui();`
- Returns BooleanValue: `false`

### `isBlockEntity(blockType)`

- This returns whether a block is a block entity
- Example: `isBlockEntity("hopper");`
- Returns BooleanValue: `true`

### `getSlotFor(itemType)`

- This returns an integer for the item slot that wanted item type is in
- Example: `getSlotFor("diamond_pickaxe");`
- Returns NumberValue: `38`

### `getTotalSlots()`

- This returns the current number of slots that the player can access (this changes depending on container)
- Example: `getTotalSlots();`
- Returns NumberValue: `46`

### `getItemForSlot(slotNum)`

- This returns the item in a current slot
- Example: `getItemForSlot(46);`
- Returns StringValue: `shield`

### `getEnchantmentsForSlot(slotNum)`

- This returns the enchantments for an item in dedicated slot
- Example: `getEnchantmentsForSlot(38);`
- Returns ListValue: `[["efficiency", 4], ["mending", 1], ["unbeaking", 3]]`

### `getDurabilityForSlot(slotNum)`

- This returns the amount of durability the item in a dedicated slot has
- Example: `getDurabilityForSlot(17);`
- Returns NumberValue: `1300`

### `getLatestChatMessage()`

- This returns the latest chat message, it contains the creation tick of the message and the string of the message
- Example: `getLatestChatMessage();`
- Returns ListValue: `[1400, "<senseiwells> This was sent in chat"]`

### `getOnlinePlayers()`

- This returns a list of the online players
- Example: `getOnlinePlayers();`
- Returns ListValue: `["senseiwells", "SuperSanta"]`

### `getGamemode()`

- This returns the current gamemode that the player is in
- Example: `getGamemode();`
- Returns StringValue: `creative`

### `getPlayerName()`

- This returns the players name
- Example: `getPlayerName();`
- Returns StringValue: `senseiwells`

### `getWeather()`

- This returns the current weather in the world
- Example: `getWeather();`
- Returns StringValue: `rain`

### `getTimeOfDay()`

- This returns the current time of day
- Example: `getTimeOfDay();`
- Returns NumberValue: `1800`

### `isSneaking()`

- This returns whether the player is sneaking
- Example: `isSneaking();`
- Returns BooleanValue: `false`

### `isSprinting()`

- This returns whether the player is sprinting
- Example: `isSprinting();`
- Returns BooleanValue: `true`

### `isFalling()`

- This returns whether the player is falling
- Example: `isFalling();`
- Returns BooleanValue: `false`

## Minecraft Events

### `_onClientTick_()`

- This event gets called every client tick
- Example: `fun _onClientTick() { message("Hi"); }`

### `_onHealthUpdate_()`

- This event gets called when the players health changes
- Example: `fun _onHealthUpdate_() { message("Health Update"); }`

### `_onTotem_()`

- This event gets called when the player activates a totem
- Example: `fun _onTotem_() { message("You just used a totem"); }`

### `_onAttack_()`

- This event gets called when the player attacks
- Example: `fun _onAttack_() { message("You just attacked") } `

### `_onUse_()`

- This event gets called when the player uses
- Example: `fun _onUse_() { message("You just used"); }`

### `_onPickBlock_()`

- This event gets called when the player picks block
- Example: `fun _onPickBlock_() { message("You just picked a block"); }`

### `_onCloseScreen_()`

- This event gets called when the player closes a screen
- Example: `fun _onCloseScreen_() { message("You just closed screen"); } `

### `_onCommand_(commandName, arguments)`

- This event gets called when a set command that you added gets called, passes in a string and a list of strings
- Example: `fun _onCommand_(commandName, arguments) { if (commandName == "cs") { message(stringOf(arguments)); } } `

### `_onOpenScreen_(screen)`

- This event gets called when a screen is opened and it will pass the name of the screen in
- Example: `fun _onOpenScreen_(screen) { message("You just opened " + screen); } `

### `_onPickUp_(entity)`

- This event gets called when the player picks up an item/xp, the item name/exp will get passed in
- Example: `fun _onPickUp_(entity) { message("You just picked up " + entity); }`

### `_onDropItem_(item)`

- This event gets called when the player drops an item, the item name gets passed in
- Example: `fun _onDropItem_(item) { message("You just dropped " + item); } `

### `_onEat_(food)`

- This event gets called when you eat, the food gets passed in
- Example: `fun _onEat_(food) { message("You just ate " + food); } `

### `_onInteractItem_(item)`

- This event gets called when the player interacts with an item (e.g using an enderpearl), the item gets passed in
- Example: `fun _onInteractItem_(item) { message("You just interacted with " + item); } `

### `_onInteractBlock_(block)`

- This event gets called when the player interacts with a block (e.g opening a chest), the block gets passed in
- Example: `fun _onInteractBlock_(block) { message("You just interacted with " + block); } `

### `_onInteractEntity_(entity)`

- This event gets called when the player interacts with an entity (e.g interacting with a villager), the entity gets passed in
- Example: `fun _onInteractEntity_(entity) { message("You just interacted with " + entity); } `

### `_onChatMessage_(message)`

- This event gets called when a chat message is added to the chat screen, the message gets passed in
- Example: `fun _onChatMessage_(message) { message("Message: " + message); } `

### `_onGamemodeChange_(gamemode)`

- This event gets called when the player changes gamemode
- Example: `fun _onGamemodeChange_(gamemode) { message("You just changed gamemode to: " + gamemode); } `

### `_onClickSlot_(slotNum)`

- This event gets called when the player clicks on an inventory slot, slot number gets passed in
- Example: `fun _onClickSlot_(slotNum) { message("You just clicked on slot: " + slotNum); } `

### `_onBlockBroken_(block, x, y, z)`

- This event gets called when the player breaks a block, block name, x, y, and z gets passed in
- Example: `fun _onBlockBroken_(block, x, y ,z) { message("You just broke " + block + " at " + stringOf(x) + " " + stringOf(y) + " " + stringOf(z)); } `

## Additional Information:

I will probably upload a video about this shortly, if I have it will be linked here.

Top Tip - Don't run a `while(true)` loop without at least 10ms delay :)

#### VSCode

VSCode syntax highlighting file can be found [here](https://cdn.discordapp.com/attachments/894353843738017802/897616637832884264/arucas.zip)

## Example Code

### Iterating through a list

For loops are useless, we have to work with while loops, thankfully it's still very simple to iterate through a list

```groovy
// Lets sort this list for only strings

valueList = ["foo", 89, true, "bar", null, false, 3.14, "baz"];
stringList = [];
iterator = 0;

while (len(valueList) > iterator) {
    value = getIndex(valueList, iterator);
    if (isString(value)) { 
        append(stringList, value);
    }
    iterator = iterator + 1;
}
print(stringList);

//Expected list for stringList is ["foo", "bar", "baz"]
```

### Running multiple scripts at once

Yes this is possible, however for them to not interfere with eachother you need to put each one on its own thread.
This is possible by using the `schedule()` function. We also need to use `hold()` as otherwise the script will end
automatically as the main code has finished.

Here's an example:

```groovy
// Here we must define our functions, remember that schedule() cannot take functions with arguments

fun runScript1() {
    run(getScriptsPath() + "/script1.arucas");
}

fun runScript2() {
    run(getScriptsPath() + "/script2.arucas");
}

// Here we can schedule both scripts to run
// I have staggered their start as to not cause any conflicts initialising

schedule(20, runScript1);
schedule(40, runScript2);

// Here the main script ends, and once it ends it will turn the script off automatically
// This would stop the other 2 scripts from running, so we need to call hold();

hold();

// Now the script will only end when the player toggles it off

```

### Are you looking at a shulker box?

Let's create a simple function that will send you a message if you are looking at a shulker box, this isn't practical
at all but this is for example purpose.

```groovy
fun isLookingAtBlockShulker() {

    // I know this doesn't look amazing but it works
    
    block = getLookingAtBlock();
    if (
    block == "shulker_box" ||
    block == "white_shulker_box" ||
    block == "orange_shulker_box" ||
    block == "magenta_shulker_box" ||
    block == "light_blue_shulker_box" ||
    block == "yellow_shulker_box" ||
    block == "lime_shulker_box" || 
    block == "pink_shulker_box" ||
    block == "gray_shulker_box" ||
    block == "light_gray_shulker_box" ||
    block == "cyan_shulker_box" ||
    block == "purple_shulker_box" ||
    block == "blue_shulker_box" ||
    block == "brown_shulker_box" || 
    block == "red_shulker_box" ||
    block == "black_shulker_box"
        ) { return true; } 
    return false;
}

while (true) {

    // The sleep statement here means that this will loop every 0.2 seconds
    
    sleep(200);
    if (isLookingAtBlockShulker()) { message("you are looking at a shulker"); }
    
    // This sends a message to the client if they are looking at a shulker
    
}
```

### Minecraft challenge idea: "I get every block I look at"

Let's create a simple script that will give us a block if we are looking at it

```groovy
fun giveBlock() {
    block = getLookingAtBlock();
    
    // We can't give the player "air", "water", or "lava", so we ignore those
    
    if (
    block == "air" ||
    block == "water" || 
    block == "lava" || 
        ) { return; }
    say("/give senseiwells " + block);
}

while (true) {
    giveBlock();
    sleep(50);
    
    // This will execute every tick
    
}

```

### CommandCameraMode clientside!

This obviously requires you to have OP on the server as it relies on using vanilla commands, but it stores all the information on the client
essentially allowing you to use CommandCameraMode on any Server as long as you have the permissions :)

```groovy
prevGamemode = null;
location = null;

// Inner scope just for adding commands, not needed but looks less messy

{
    // adds the command you want and the number of arguments you can have
  
    addCommand("cs", 0);
    addCommand("c", 0);
    addCommand("s", 0);
}

fun listString(list, index) {
    return stringOf(list, index);
}

// This is the onCommand event, which gets called everytime you send a command with the name that you added previously

fun _onCommand_(command, arguments) {
    if (command == "cs") {
        if (getGamemode() != "spectator") {
            prevGamemode = getGamemode();
            location = getPos();
            append(location, getDimension());
            say("/gamemode spectator");
            return;
        }
        if (prevGamemode == null) {
            prevGamemode = getGamemode();
            say("/gamemode survival");
        }
        else { 
            say("/gamemode " + prevGamemode); 
        }
      
        // This is restoring the location  
      
        if (location != null) {
            say(
                "/execute in " + getIndex(location, 5) + 
                " run tp @s " + listString(location, 0) + " " + 
                listString(location, 1) + " " + 
                listString(location, 2) + " " + 
                listString(location, 3) + " " + 
                listString(location, 4)
            );
        }
    }
    else if (command == "c") {
        say("/gamemode spectator");
    }
    else if (command == "s") {
        say("/gamemode survival");
    }
}

// Add a nice touch by letting the user know their gamemode :)

fun _onGamemodeChange_(gamemode) {
  messageActionBar("§6You are now in §a" +  gamemode);
}
```

### Infinite Trading Script

This is meant to automate the process of infinite trading, you'd have a setup in the end in Minecraft and run this script and it should trade automatically for you
infinitely!

```groovy
item = "iron_axe"   // Change this depending on the item you want to trade

// Could also use _onClientTick_() here

while (true) {
	if (getLookingAtEntity() == "villager") {
		if (!isInInventoryGui()) {
			use("once");
		}
	}
	else if (isInInventoryGui()) {
		sleep(200);
		while(!isTradeDisabled(item)) {
			sleep(20);
			tradeFor(item, true);
		}
		sleep(1000);
		inventory("close");
	}
	sleep(50);
}
```