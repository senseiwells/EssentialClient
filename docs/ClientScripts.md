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
asking you what program to open the file with, open it with any text editor (NotePad++ has [syntax support](#notepad)). You can also
change the current Script file that you open, and run by changing the `clientScriptFilename` in the Client Rules Menu.
ClientScripts are stored in `.minecraft/config/EssentialClient/Scripts`.

In game, you can navigate to controls and bind `Client Script` to a keybind. Once you press this key in game the script
will execute, if the script fails it will notify you in game, and will give you an error of why it failed making debugging
convenient.

# What's avaiable in the language?

## Variables:

There are 6 variable types that this language supports:

`Float` - This stores floating point numbers, can store integers too

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

## Logical Operators:

`and` or `&&` - This is the logical AND

`or` or `||` - This is the logical OR

`not` or `!` - This is the logical NOT

## Keywords:

### `var`

- This is used for initialising any variables
- Examples:
    - `var number = 0;`
    - `var string = "foo";`

### `const`

- This is used for stating any constant values
- Examples:
    - `const pi = 3.141592`
    - `const euler = 2.718281`

### `fun`

- This is used for defining functions
- Example: `fun foo(bar) { print(bar); }`

### `return`

- This is used for returning values in functions
- Example: `fun example() { return "foo bar"; }`

### `if`

- This is used to evaluate a conditional statement
- Examples:
    - `if (true) -> print("foo");`
    - `if(true) { print("bar"); }`

### `else`

- This is used to further evaluate a conditional statement following an `if`, can also be used in conjuction with `if`
  for an `else if` statement
- Examples:
    - `if (false) -> print("foo") else if (true) -> print("bar") else -> print("baz");`
    - `if (false) { print("foo"); } else if (false) { print("bar"); } else { print("baz); }`

### `then` or `->`

- This is used for using `if`'s and `else`'s on one line, this is also how the ternary operator is expressed
- Examples:
    - `if (true) -> print("foo");`
    - `var string = if(true) -> "it works!" else -> "this should not return";`

### `while`

- This is used to loop while evaluating a conditional statement
- Example: `while (true) { print("This infinite loop with crash"); }`

### `continue`

- This keyword is used to return to the top of the loop in a `while` loop
- Example: `continue;`

### `break`

- This is used to break out of a loop
- Example: `break;`

### `start` or `{`

- This is used after a `fun` identifier, `if` conditional or `else` to create a block that can contain multiple lines
- Example: `fun example() { print("foo"); print("bar"); print("baz"); }`

### `end` or `}`

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

### `round(number)`

- This rounds a number to the nearest integer
- Example: `round(99.9);`

### `roundUp(number)`

- This rounds a number up to the next integer
- Example: `roundUp(0.1);`

### `roundDown(number)`

- This rounds a number down to the next integer
- Example: `roundDown(0.9);`

### `getTime()`

- This gets the current time as a string
- Example: `getTime();`

### `isNumber(value)`

- This returns a boolean based on whether value is a number
- Example: `isNumber("this is not a number");`

### `isString(value)`

- This returns a boolean based on whether value is a string
- Example: `isNumber("this is a string");`

### `isBoolean(value)`

- This returns a boolean based on whether value is a boolean
- Example: `isBoolean(80);`

### `isFunction(value)`

- This returns a boolean based on whether value is a function
- Example: `isFunction(false);`

### `isList(value)`

- This returns a boolean based on whether value is a list
- Example: `isList(null);`

### `getIndex(list, index)`

- This is a list variable specific function, it returns the value at index of a list, index starts at 0
- Example: `var stringList = ["foo", "bar", "baz"]; getIndex(stringList, 0);`

### `removeIndex(list, index)`

- This is a list variable specific function, it removes a value from a list at index
- Example: `var stringList = ["foo", "bar", "baz"]; removeIndex(stringList, 0);`

### `append(list, value)`

- This is a list variable specific function, it allows you to add a value to a list
- Example: `var list = []; append(list, 3.14159);`

### `concat(list, otherList)`

- This is a list variable specific function, it allows you to concatenate two lists
- Example: `var list1 = ["foo"]; var list2 = ["bar", "baz"]; concat(list1, list2);`

### `len(list)`

- This is a list variable specific function, it allows you to get the length of a list
- Example: `var list = ["foo", "bar"]; len(list);`

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

### `say(string);`

- This allows you to make your player send a message in chat, this includes commands
- Examples:
    - `say("Hi :)");`
    - `say("/gamemode creative");`

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

### `screenshot()`

- This takes a screenshot
- Example: `screenshot()`

### `look(yaw, pitch)`

- This allows you to manipulate where your player is looking
- Example: `look(-76.2, 80.1);`

### `jump()`

- This will make the player jump if they are on the ground
- Example: `jump()`

### `hold()`

- This stops the macro from ending after it has executed all of its code, meaning it needs to be stopped manually
- Example: `hold()`


## Minecraft Functions (that return values)

### `getCurrentSlot()`

- This returns the current slot the player has selected as a number
- Example: `getCurrentSlot();`

### `getHeldItem()`

- This returns the current item the player is holding as a string, if player was holding a grass block it would return "grass_block"
- Example: `getHeldItem();`

### `getLookingAtBlock()`

- This returns the block that the player is looking at as a string, if looking at nothing will return "air"
- Example: `getLookingAtBlock();`

### `getLookingAtEntity()`

- This returns the entity the player is looking at as a string, if looking at no entity will return "none"
- Example: `getLookingAtEntity();`

### `getHealth()`

- This returns the player's current health
- Example: `getHealth();`

### `getPos(axis)`

- This returns the position (or rotation) of a player on desired axis as a number, pass "x", "y", "z", "yaw", or "pitch" as a parameter
- Example: `getPos("x");`

### `getDimension()`

- This returns the current dimension that the player is in as a string
- Example: `getDimension();`

### `getBlockAt(x, y, z)`

- This returns the block at the position given in the parameters as a string, if it is out of render then it will return "void_air"
- Example: `getBlockAt(getPos("x"), getPos("y") - 1, getPos("z"));`

### `getScriptsPath()`

- This returns the path that your scripts are stored as a string, e.g. `C:/.minecraft/config/EssentialClient/Scripts`
- Example: `getScriptsPath();`

### `isTradeDisabled(itemType)` and `isTradeDisabled(index)`

- This returns whether a trade is disabled as a boolean, you can either pass an index or item type as a parameter
- Examples:
    - `isTradeDisabled("experience_bottle");`
    - `isTradeDisabled(3);`

### `doesVillagerHaveTrade(itemType)`

- This returns whether a villager has a trade for an item type as a boolean
- Example: `doesVillagerHaveTrade("green_terracotta");`

### `isInventoryFull()`

- This returns whether the players inventory is full as a boolean
- Example: `isInventoryFull()`

### `isInInventoryGui()`

- This returns whether the player is inside an inventory Gui as a boolean
- Example: `isInInventoryGui()`

### `isBlockEntity(blockType)`

- This returns whether a block is a block entity
- Example: `isBlockEntity("hopper");`


## Additional Information:

I will probably upload a video about this shortly, if I have it will be linked here.

Top Tip - Don't run a `while(true)` loop without at least 10ms delay :)

#### Notepad++

Notepad++ syntax highlighting xml file can be found [here](https://cdn.discordapp.com/attachments/559400132710236160/894352681529925652/me.senseiwells.arucas.xml)

## Example Code

### Iterating through a list

For loops are useless, we have to work with while loops, thankfully it's still very simple to iterate through a list

```groovy
// Lets sort this list for only strings

var valueList = ["foo", 89, true, "bar", null, false, 3.14, "baz"];
var stringList = [];
var iterator = 0;

while (len(valueList) > iterator) {
    var value = getIndex(valueList, iterator);
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
    run(getScriptsPath() + "/script1.me.senseiwells.arucas");
}

fun runScript2() {
    run(getScriptsPath() + "/script2.me.senseiwells.arucas");
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
    
    var block = getLookingAtBlock();
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
        ) -> return true;
    return false;
}

while (true) {

    // The sleep statement here means that this will loop every 0.2 seconds
    
    sleep(200);
    if (isLookingAtBlockShulker()) -> message("you are looking at a shulker");
    
    // This sends a message to the client if they are looking at a shulker
    
}
```

### Minecraft challenge idea: "I get every block I look at"

Let's create a simple script that will give us a block if we are looking at it

```groovy
fun giveBlock() {
    var block = getLookingAtBlock();
    
    // We can't give the player "air", "water", or "lava", so we ignore those
    
    if (
    block == "air" ||
    block == "water" || 
    block == "lava" || 
        ) -> return;
    say("/give senseiwells " + block);
}

while (true) {
    giveBlock();
    sleep(50);
    
    // This will execute every tick
    
}

```