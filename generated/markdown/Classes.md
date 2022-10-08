# Biome class
Biome class for Arucas

This class represents biomes, and allows you to interact with things inside of them.
Import with `import Biome from Minecraft;`

## Methods

### `<Biome>.canSnow(pos)`
- Description: This function calculates wheter snow will fall at given coordinates
- Parameter - Pos (`pos`): the position
- Returns - Boolean: whether snow will fall at given position
- Example:
```kotlin
biome.canSnow(new Pos(0, 100, 0));
```

### `<Biome>.canSnow(x, y, z)`
- Description: This function calculates wheter snow will fall at given coordinates
- Parameters:
  - Number (`x`): the x coordinate
  - Number (`y`): the y coordinate
  - Number (`z`): the z coordinate
- Returns - Boolean: whether snow will fall at given position
- Example:
```kotlin
biome.canSnow(0, 100, 0);
```

### `<Biome>.getFogColor()`
- Description: This function returns Fog color of the biome
- Returns - Number: fog color of the biome
- Example:
```kotlin
biome.getFogColor();
```

### `<Biome>.getId()`
- Description: This function returns the path id of the biome, e.g. 'plains'
- Returns - String: id of the biome
- Example:
```kotlin
biome.getId();
```

### `<Biome>.getSkyColor()`
- Description: This function returns sky color of the biome
- Returns - Number: sky color of the biome
- Example:
```kotlin
biome.getSkyColor();
```

### `<Biome>.getTemperature()`
- Description: This function returns temperature of the biome
- Returns - Number: temperature of the biome
- Example:
```kotlin
biome.getTemperature();
```

### `<Biome>.getWaterColor()`
- Description: This function returns Fog color of the biome
- Returns - Number: fog color of the biome
- Example:
```kotlin
biome.getWaterColor();
```

### `<Biome>.getWaterFogColor()`
- Description: This function returns water fog color of the biome
- Returns - Number: water fog color of the biome
- Example:
```kotlin
biome.getWaterFogColor();
```

### `<Biome>.hasHighHumidity()`
- Description: This function returns if biome has high humidity
- Returns - Boolean: whether biome has high humidity
- Example:
```kotlin
biome.hasHighHumidity();
```

### `<Biome>.isCold(pos)`
- Description: This function calculates wheter biome is cold at given position
- Parameter - Pos (`pos`): the position
- Returns - Boolean: whether temperature is cold at given position
- Example:
```kotlin
biome.isCold(0, 100, 0);
```

### `<Biome>.isCold(x, y, z)`
- Description: This function calculates wheter biome is cold at given position
- Parameters:
  - Number (`x`): the x coordinate
  - Number (`y`): the y coordinate
  - Number (`z`): the z coordinate
- Returns - Boolean: whether temperature is cold at given position
- Example:
```kotlin
biome.isCold(0, 100, 0);
```

### `<Biome>.isHot(pos)`
- Description: This function calculates wheter biome is hot at given position
- Parameter - Pos (`pos`): the position
- Returns - Boolean: whether temperature is hot at given position
- Example:
```kotlin
biome.isHot(0, 100, 0);
```

### `<Biome>.isHot(x, y, z)`
- Description: This function calculates wheter biome is hot at given position
- Parameters:
  - Number (`x`): the x coordinate
  - Number (`y`): the y coordinate
  - Number (`z`): the z coordinate
- Returns - Boolean: whether temperature is hot at given position
- Example:
```kotlin
biome.isHot(0, 100, 0);
```



# Block class
Block class for Arucas

This class allows interactions with blocks in Minecraft.
Import with `import Block from Minecraft;`

## Methods

### `<Block>.getBlastResistance()`
- Description: This gets the blast resistance of the Block
- Returns - Number: the blast resistance of the Block
- Example:
```kotlin
block.getBlastResistance();
```

### `<Block>.getBlockNbt()`
- Description: This gets the NBT of the Block
- Returns - Map: the NBT of the Block, may be null if the Block has no NBT
- Example:
```kotlin
block.getBlockNbt();
```

### `<Block>.getBlockProperties()`
- Description: This gets the properties of the Block
You can find a list of all block properties
[here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Block_states)
- Returns - Map: the properties of the Block, may be empty if there are no properties
- Example:
```kotlin
block.getBlockProperties();
```

### `<Block>.getDefaultState()`
- Description: This gets the default state of the block, it will conserve any positions
- Returns - Block: default state of the Block
- Example:
```kotlin
block.getDefaultState();
```

### `<Block>.getHardness()`
- Description: This gets the hardness of the Block
- Returns - Number: the hardness of the Block
- Example:
```kotlin
block.getHardness();
```

### `<Block>.getLuminance()`
- Description: This gets the luminance of the Block
- Returns - Number: the luminance of the Block
- Example:
```kotlin
block.getLuminance();
```

### `<Block>.getMapColour()`
- Description: This gets the map colour of the Block, can also be called with 'getMapColor'
- Returns - List: a list with the map colour of the Block as RGB values
- Example:
```kotlin
block.getMapColour();
```

### `<Block>.getMaterial()`
- Description: This gets the material of the Block
- Returns - Material: the material of the Block
- Example:
```kotlin
block.getMaterial();
```

### `<Block>.getPos()`
- Description: This gets the position of the Block
- Returns - Pos: the position of the Block, may be null if the Block has no position
- Example:
```kotlin
block.getPos();
```

### `<Block>.getX()`
- Description: This gets the X position of the Block
- Returns - Number: the X position of the Block, may be null if the Block has no position
- Example:
```kotlin
block.getX();
```

### `<Block>.getY()`
- Description: This gets the Y position of the Block
- Returns - Number: the Y position of the Block, may be null if the Block has no position
- Example:
```kotlin
block.getY();
```

### `<Block>.getZ()`
- Description: This gets the Z position of the Block
- Returns - Number: the Z position of the Block, may be null if the Block has no position
- Example:
```kotlin
block.getZ();
```

### `<Block>.hasBlockPosition()`
- Description: This checks if the Block has a position or not
- Returns - Boolean: true if the Block has a position
- Example:
```kotlin
block.hasBlockPosition();
```

### `<Block>.isBlockEntity()`
- Description: This checks if the Block is a BlockEntity
- Returns - Boolean: true if the Block is a BlockEntity
- Example:
```kotlin
block.isBlockEntity();
```

### `<Block>.isFluid()`
- Description: This checks if the Block is a fluid
- Returns - Boolean: true if the Block is a fluid
- Example:
```kotlin
block.isFluid();
```

### `<Block>.isFluidSource()`
- Description: This checks if the Block is a fluid source
- Returns - Boolean: true if the Block is a fluid source
- Example:
```kotlin
block.isFluidSource();
```

### `<Block>.isReplaceable()`
- Description: This checks if the Block is replaceable
- Returns - Boolean: true if the Block is replaceable
- Example:
```kotlin
block.isReplaceable();
```

### `<Block>.isSideSolidFullSquare(side)`
- Description: This checks if the Block is solid on the full square
- Parameter - String (`side`): the side to check, for example: 'north', 'south', 'east', 'west', 'up', 'down'
- Returns - Boolean: true if the Block is solid on the full square
- Example:
```kotlin
block.isSideSolidFullSquare('north');
```

### `<Block>.isSolidBlock()`
- Description: This checks if the Block is a solid block
- Returns - Boolean: true if the Block is a solid block
- Example:
```kotlin
block.isSolidBlock();
```

### `<Block>.isSpawnable()`
- Description: This checks if the Block is spawnable in the case of zombies
- Returns - Boolean: true if the Block is spawnable in the case of zombies
- Example:
```kotlin
block.isSpawnable();
```

### `<Block>.isSpawnable(entity)`
- Description: This checks if the Block allows spawning for given entity
- Parameter - Entity (`entity`): the entity to check
- Returns - Boolean: true if the Block allows spawning for given entity
- Example:
```kotlin
block.isSpawnable(zombie);
```

### `<Block>.isTransparent()`
- Description: This checks if the Block is transparent
- Returns - Boolean: true if the Block is transparent
- Example:
```kotlin
block.isTransparent();
```

### `<Block>.mirrorFrontBack()`
- Description: This mirrors the Block around the front and back
- Returns - Block: the mirrored Block
- Example:
```kotlin
block.mirrorFrontBack();
```

### `<Block>.mirrorLeftRight()`
- Description: This mirrors the Block around the left and right
- Returns - Block: the mirrored Block
- Example:
```kotlin
block.mirrorLeftRight();
```

### `<Block>.rotateYClockwise()`
- Description: This rotates the Block 90 degrees clockwise
- Returns - Block: the rotated Block
- Example:
```kotlin
block.rotateYClockwise();
```

### `<Block>.rotateYCounterClockwise()`
- Description: This rotates the Block 90 degrees counter-clockwise
- Returns - Block: the rotated Block
- Example:
```kotlin
block.rotateYCounterClockwise();
```

### `<Block>.sideCoversSmallSquare(side)`
- Description: This checks if the Block covers a small square
- Parameter - String (`side`): the side to check, for example: 'north', 'south', 'east', 'west', 'up', 'down'
- Returns - Boolean: true if the Block covers a small square
- Example:
```kotlin
block.sideCoversSmallSquare('north');
```

### `<Block>.with(property, value)`
- Description: This gets modified block with a property value, conserving positions
- Parameters:
  - String (`property`): property name, such as 'facing', 'extended'
  - String (`value`): value name, such as 'north', 'true'
- Returns - Block: new state of the Block
- Example:
```kotlin
block.with('facing', 'north');
```

## Static Methods

### `Block.of(material)`
- Description: This creates a Block from a material or string
- Parameter - Material (`material`): the material, item stack, block, or string to create the Block from
- Returns - Block: the Block created from the material or string
- Example:
```kotlin
Block.of(Material.STONE);
```



# Boolean class
Boolean class for Arucas

This is the boolean type, representing either true or false.
This class cannot be instantiated, or extended
Class does not need to be imported



# BoxShape class
BoxShape class for Arucas

This class allows you to create box shapes that can be rendered in the world.
Import with `import BoxShape from Minecraft;`

## Constructors

### `new BoxShape(pos)`
- Description: Creates a new box shape, this is used to render boxes
- Parameter - Pos (`pos`): The position which will be used for the first and second corner of the box
- Example:
```kotlin
new BoxShape(new Pos(0, 0, 0));
```

### `new BoxShape(pos1, pos2)`
- Description: Creates a new box shape, this is used to render boxes
- Parameters:
  - Pos (`pos1`): The position of the first corner of the box
  - Pos (`pos2`): The position of the second corner of the box
- Example:
```kotlin
new BoxShape(new Pos(0, 0, 0), new Pos(10, 10, 10));
```

### `new BoxShape(x, y, z)`
- Description: Creates a new box shape, this is used to render boxes
- Parameters:
  - Number (`x`): The x position which will be used for the first and second corner of the box
  - Number (`y`): The y position which will be used for the first and second corner of the box
  - Number (`z`): The z position which will be used for the first and second corner of the box
- Example:
```kotlin
new BoxShape(0, 0, 0);
```

### `new BoxShape(x1, y1, z1, x2, y2, z2)`
- Description: Creates a new box shape, this is used to render boxes
- Parameters:
  - Number (`x1`): The x position of the first corner of the box
  - Number (`y1`): The y position of the first corner of the box
  - Number (`z1`): The z position of the first corner of the box
  - Number (`x2`): The x position of the second corner of the box
  - Number (`y2`): The y position of the second corner of the box
  - Number (`z2`): The z position of the second corner of the box
- Example:
```kotlin
new BoxShape(0, 0, 0, 10, 10, 10);
```



# CentredShape class
CentredShape class for Arucas

This class represents shapes that are positioned centrally with a width
Import with `import CentredShape from Minecraft;`

## Methods

### `<CentredShape>.centrePositions()`
- Description: This centres the positions of the shape
- Example:
```kotlin
shape.centrePositions();
```

### `<CentredShape>.getPos()`
- Description: This gets the central position of the shape
- Returns - Pos: the central position of the shape
- Example:
```kotlin
shape.getPos();
```

### `<CentredShape>.getWidth()`
- Description: This gets the width of the shape
- Returns - Number: the width of the shape
- Example:
```kotlin
shape.getWidth();
```

### `<CentredShape>.setPos(pos)`
- Description: This sets the central position of the shape
- Parameter - Pos (`pos`): the central position of the shape
- Example:
```kotlin
shape.setPos(new Pos(1, 0, 100));
```

### `<CentredShape>.setWidth(width)`
- Description: This sets the width of the shape
- Parameter - Number (`width`): the width of the shape
- Example:
```kotlin
shape.setWidth(10.5);
```



# Collection class
Collection class for Arucas

This class is used to represent a collection of objects,
this class is used internally as the parent of maps, lists, and sets.
This cannot be instantiated directly.
All collections inherit Iterable, and thus can be iterated over
Class does not need to be imported

## Constructors

### `new Collection()`
- Description: This creates a collection, this cannot be called directly, only from child classes
- Example:
```kotlin
class ChildCollection: Collection {
    ChildCollection(): super();
    
    fun size() {
        return 0;
    }
}
```

## Methods

### `<Collection>.isEmpty()`
- Description: This allows you to check if the collection is empty
- Returns - Boolean: true if the collection is empty
- Example:
```kotlin
['object', 81, 96, 'case'].isEmpty(); // false
```

### `<Collection>.size()`
- Description: This allows you to get the size of the collection
- Returns - Number: the size of the list
- Example:
```kotlin
['object', 81, 96, 'case'].size();
```



# CommandBuilder class
CommandBuilder class for Arucas

This class allows you to build commands for Minecraft.
Import with `import CommandBuilder from Minecraft;`

## Methods

### `<CommandBuilder>.executes(function)`
- Description: This sets the function to be executed when the command is executed,
this should have the correct amount of parameters for the command
- Parameter - CommandBuilder (`function`): the function to execute
- Returns - CommandBuilder: the parent command builder
- Example:
```kotlin
commandBuilder.executes(fun() { });
```

### `<CommandBuilder>.then(childBuilder)`
- Description: This adds a child CommandBuilder to your command builder
- Parameter - CommandBuilder (`childBuilder`): the child command builder to add
- Returns - CommandBuilder: the parent command builder
- Example:
```kotlin
commandBuilder.then(CommandBuilder.literal('subcommand'));
```

## Static Methods

### `CommandBuilder.argument(argumentName, argumentType)`
- Description: Creates an argument builder with a specific argument type, and a name
to see all the different types refer to CommandBuilder.fromMap(...)
- Parameters:
  - String (`argumentName`): the name of the argument
  - String (`argumentType`): the type of the argument
- Returns - CommandBuilder: the argument builder
- Example:
```kotlin
CommandBuilder.argument('test', 'entityid');
```

### `CommandBuilder.argument(argumentName, argumentType, suggestions)`
- Description: Creates an argument builder with a specific argument type, a name, and a default value
to see all the different types refer to CommandBuilder.fromMap(...)
- Parameters:
  - String (`argumentName`): the name of the argument
  - String (`argumentType`): the type of the argument
  - List (`suggestions`): a list of strings for the suggestions for the argument
- Returns - CommandBuilder: the argument builder
- Example:
```kotlin
CommandBuilder.argument('test', 'word', ['wow', 'suggestion']);
```

### `CommandBuilder.fromMap(argumentMap)`
- Description: Creates an argument builder from a map.
The map must contain a 'name' key as a String that is the name of the command,
the map then can contain 'subcommands' as a map which contains the subcommands,
the key of the subcommands is the name of the subcommand, and the value is a map,
if the name is encased in '<' and '>' it will be treated as an argument, otherwise it will be treated as a literal.
You can chain arguments by leaving a space in the name like: 'literal <arg>'.
If the key has no name and is just an empty string the value will be used as the function
which will be executed when the command is executed, the function should have the appropriate
number of parameters, the number of parameters is determined by the number of arguments.
Argument types are defined in the main map under the key 'arguments' with the value of a map
the keys of this map should be the names of your arguments used in your subcommands,
this should be a map and must have the key 'type' which should be a string that is the type of the argument.
Optionally if the type is of 'integer' or 'double' you can also have the key 'min' and 'max' with numbers as the value,
and if the type is of 'enum' you must have the key 'enum' with the enum class type as the value: 'enum': MyEnum.type.
You can also optionally have 'suggests' which has the value of a list of strings that are suggestions for the argument.
You can also optionally have 'suggester' which has the value of a function that will be called to get suggestions for the argument,
this function should have arbitrary number of parameters which will be the arguments that the user has entered so far.
The possible argument types are: 'PlayerName', 'Word', 'GreedyString', 'Double', 'Integer', 'Boolean', 'Enum',
'ItemStack', 'Particle', 'RecipeId', 'EntityId', 'EnchantmentId'
- Parameter - Map (`argumentMap`): the map of arguments
- Returns - CommandBuilder: the argument builder
- Example:
```kotlin
effectCommandMap = {
    "name" : "effect",
    "subcommands" : {
        "give" : {
            "<targets> <effect>" : {
                "" : fun(target, effect) {
                    // do something
                },
                "<seconds>" : {
                    "" : fun(target, effect, second) {
                        // do something
                    },
                    "<amplifier>" : {
                        "" : fun(target, effect, second, amplifier) {
                            // do something
                        },
                        "<hideParticle>" : {
                            "" : fun(target, effect, second, amplifier, hideParticle) {
                                // do something
                            }
                        }
                    }
                }
            }
        },
        "clear" : {
            "" : fun() {
                // do something
            },
            "<targets>" : {
                "" : fun(target) {
                    // do something
                },
                "<effect>" : {
                    "" : fun(target, effect) {
                        // do something
                    }
                }
            }
        }
    },
    "arguments" : {
        "targets" : { "type" : "Entity" },
        "effect" : { "type" : "Effect", "suggests" : ["effect1", "effect2"] },
        "seconds" : { "type" : "Integer", "min" : 0, "max" : 1000000 },
        "amplifier" : { "type" : "Integer", "min" : 0, "max" : 255 },
        "hideParticle" : { "type" : "Boolean" }
    }
};
effectCommand = CommandBuilder.fromMap(effectCommandMap);
```

### `CommandBuilder.literal(argument)`
- Description: Creates a literal argument with just a string
- Parameter - String (`argument`): the literal argument
- Returns - CommandBuilder: the argument builder
- Example:
```kotlin
CommandBuilder.literal('test');
```



# Config class
Config class for Arucas

This class allows you to create configs for your scripts
Import with `import Config from Minecraft;`

## Methods

### `<Config>.addListener(listener)`
- Description: Adds a listener to the config, the listener will be called when the config is changed
The listener must have one parameter, this is the new value that was set
- Parameter - Function (`listener`): The listener to add
- Example:
```kotlin
config.addListener(function(newValue) {
    print(newValue);
});
```

### `<Config>.getCategory()`
- Description: Gets the category of the config
- Returns - String: The category of the config
- Example:
```kotlin
config.getCategory();
```

### `<Config>.getDefaultValue()`
- Description: Gets the default value of the config
- Returns - Object: The default value of the config
- Example:
```kotlin
config.getDefaultValue();
```

### `<Config>.getDescription()`
- Description: Gets the description of the config
- Returns - String: The description of the config
- Example:
```kotlin
config.getDescription();
```

### `<Config>.getName()`
- Description: Gets the name of the config
- Returns - String: The name of the config
- Example:
```kotlin
config.getName();
```

### `<Config>.getOptionalInfo()`
- Description: Gets the optional info of the config
- Returns - String: The optional info of the config
- Example:
```kotlin
config.getOptionalInfo();
```

### `<Config>.getType()`
- Description: Gets the type of the config
- Returns - String: The type of the config
- Example:
```kotlin
config.getType();
```

### `<Config>.getValue()`
- Description: Gets the value of the config
- Returns - Object: The value of the config
- Example:
```kotlin
config.getValue();
```

### `<Config>.resetToDefault()`
- Description: Resets the config to the default value
- Example:
```kotlin
config.resetToDefault();
```

### `<Config>.setValue(value)`
- Description: Sets the value of the config, if the value is invalid it will not be changed
- Parameter - Object (`value`): The new value of the config
- Example:
```kotlin
config.setValue(10);
```

### `<Config>.toJson()`
- Description: Converts the config into a json value, this will not keep the listeners
- Returns - Json: The config as a json value
- Example:
```kotlin
config.toJson();
```

## Static Methods

### `Config.fromListOfMap(list)`
- Description: Creates a config from a list of config maps
- Parameter - List (`list`): The list of config maps
- Returns - List: A list of configs created from the list of config maps
- Example:
```kotlin
configs = [
    {
        "type": "boolean",
        "name": "My Config",
        "description": "This is my config"
    },
    {
        "type": "cycle",
        "name": "My Cycle Config",
        "description": "This is my cycle config",
        "cycle_values": ["one", "two", "three"],
        "default_value": "two"
    }
];
configs = Config.fromListOfMap(configs);
```

### `Config.fromMap(map)`
- Description: Creates a config from a map
The map must contain the following keys:
'type' which is the type of the config which can be 'boolean', 'cycle', 'double', 'double_slider', 'integer', 'integer_slider', 'list', or 'string',
'name' which is the name of the config
And can optionally contain the following keys:
'description' which is a description of the config,
'optional_info' which is an optional info for the config,
'default_value' which is the default value of the config,
'category' which is the category of the config,
'value' which is the current value of the config, 
'listener' which is a function that will be called when the config changes, this must have 1 parameter which is the rule that was changed,
'max_length' which is the max length for the input of the config, this must be an integer > 0, default is 32
And 'cycle' types must contain the following keys:
'cycle_values' which is a list of values that the config can cycle through.
And slider types must contain the following keys:
'min' which is the minimum value of the slider,
'max' which is the maximum value of the slider
- Parameter - Map (`map`): The map to create the config from
- Returns - Config: The config created from the map
- Example:
```kotlin
configMap = {
    "type": "string",
    "name": "My Config",
    "description": "This is my config",
    "category": "Useful",
    "optional_info": "This is an optional info",
    "default_value": "foo",
    "value": "bar",
    "listener": fun(newValue) { },
    "max_length": 64
};
config = Config.fromMap(configMap);
```



# ConfigHandler class
ConfigHandler class for Arucas

This class allows you to easily read and write config files.
Import with `import ConfigHandler from Minecraft;`

## Constructors

### `new ConfigHandler(name)`
- Description: Creates a new ConfigHandler, this is used to read and save configs
- Parameter - String (`name`): The name of the config, this will also be the name of the config file
- Example:
```kotlin
new ConfigHandler('MyConfig');
```

### `new ConfigHandler(name, read)`
- Description: Creates a new ConfigHandler, this is used to read and save configs
- Parameters:
  - String (`name`): The name of the config, this will also be the name of the config file
  - Boolean (`read`): Whether or not to read the config on creation
- Example:
```kotlin
new ConfigHandler('MyConfig', false);
```

## Methods

### `<ConfigHandler>.addConfig(config)`
- Description: Adds a config to the handler
- Parameter - Config (`config`): The config to add
- Example:
```kotlin
config = Config.fromMap({
    "type": "boolean",
    "name": "My Config",
    "description": "This is my config"
});
configHandler.addConfig(config);
```

### `<ConfigHandler>.addConfigs(configs...)`
- Description: Adds multiple configs to the handler, you can pass in a list of configs
or a varargs of configs, this is for compatability with older scripts
- Parameter - Config (`configs...`): The configs to add
- Example:
```kotlin
config = Config.fromMap({
    "type": "boolean",
    "name": "My Config",
    "description": "This is my config"
});
configHandler.addConfigs(config, config);
```

### `<ConfigHandler>.createScreen()`
- Description: Creates a new config screen containing all of the configs in the handler, in alphabetical order.
The screen name will be the default, the same as the name of the config handler
- Returns - Screen: The new config screen
- Example:
```kotlin
configHandler.createScreen();
```

### `<ConfigHandler>.createScreen(title)`
- Description: Creates a new config screen containing all of the configs in the handler, in alphabetical order
- Parameter - Text (`title`): The title of the screen
- Returns - Screen: The new config screen
- Example:
```kotlin
configHandler.createScreen(Text.of('wow'));
```

### `<ConfigHandler>.createScreen(title, alphabetical)`
- Description: Creates a new config screen containing all of the configs in the handler
- Parameters:
  - Text (`title`): The title of the screen
  - Boolean (`alphabetical`): Whether or not to sort the configs alphabetically
- Returns - Screen: The new config screen
- Example:
```kotlin
configHandler.createScreen(Text.of('wow'), false);
```

### `<ConfigHandler>.getAllConfigs()`
- Description: Gets all the configs in the handler
- Returns - List: All the configs in the handler
- Example:
```kotlin
configHandler.getAllConfigs();
```

### `<ConfigHandler>.getConfig(name)`
- Description: Gets a config from the handler
- Parameter - String (`name`): The name of the config
- Returns - Config: The config
- Example:
```kotlin
configHandler.getConfig('MyConfig');
```

### `<ConfigHandler>.getName()`
- Description: Gets the name of the config
- Returns - String: The name of the config
- Example:
```kotlin
configHandler.getName();
```

### `<ConfigHandler>.read()`
- Description: Reads the all the configs from the file
If configs are already in the handler, only the values
will be overwritten
- Example:
```kotlin
configHandler.read();
```

### `<ConfigHandler>.removeConfig(name)`
- Description: Removes a config from the handler
- Parameter - String (`name`): The name of the config to remove
- Example:
```kotlin
configHandler.removeConfig('My Config');
```

### `<ConfigHandler>.resetAllToDefault()`
- Description: Resets all configs to their default values
- Example:
```kotlin
configHandler.resetAllToDefault();
```

### `<ConfigHandler>.save()`
- Description: Saves the configs to the file
- Example:
```kotlin
configHandler.save();
```

### `<ConfigHandler>.setSaveOnClose(saveOnClose)`
- Description: Sets whether or not the configs should be saved when the script ends, by default this is true
- Parameter - Boolean (`saveOnClose`): Whether or not the configs should be saved when the script ends
- Example:
```kotlin
configHandler.setSaveOnClose(false);
```

### `<ConfigHandler>.setSavePath(savePath)`
- Description: Sets the path to save the configs to, this shouldn't include the file name
- Parameter - File (`savePath`): The path to save the configs to
- Example:
```kotlin
configHandler.setSavePath(new File('/home/user/scripts/'));
```

### `<ConfigHandler>.willSaveOnClose()`
- Description: Gets whether or not the configs will be saved when the script ends
- Returns - Boolean: Whether or not the configs will be saved when the script ends
- Example:
```kotlin
configHandler.willSaveOnClose();
```



# CorneredShape class
CorneredShape class for Arucas

This class represents all shapes that use 2 corners to dictate their position
Import with `import CorneredShape from Minecraft;`

## Methods

### `<CorneredShape>.centrePositions()`
- Description: This centres the positions of the shape
- Example:
```kotlin
shape.centrePositions();
```

### `<CorneredShape>.getPos1()`
- Description: This gets the first position of the shape
- Returns - Pos: the first position of the shape
- Example:
```kotlin
shape.getPos1();
```

### `<CorneredShape>.getPos2()`
- Description: This gets the second position of the shape
- Returns - Pos: the second position of the shape
- Example:
```kotlin
shape.getPos2();
```

### `<CorneredShape>.setPos1(pos1)`
- Description: This sets the first position of the shape
- Parameter - Pos (`pos1`): the first position of the shape
- Example:
```kotlin
shape.setPos1(new Pos(1, 0, 100));
```

### `<CorneredShape>.setPos2(pos2)`
- Description: This sets the second position of the shape
- Parameter - Pos (`pos2`): the second position of the shape
- Example:
```kotlin
shape.setPos2(new Pos(1, 0, 100));
```



# Entity class
Entity class for Arucas

This class is mostly used to get data about entities.
Import with `import Entity from Minecraft;`

## Methods

### `<Entity>.canSpawnAt(pos)`
- Description: This checks whether the entity can spawn at given position with regard to light and hitbox
- Parameter - Pos (`pos`): the position to check
- Returns - Boolean: whether entity type can spawn at given position
- Example:
```kotlin
entity.canSpawnAt(new Pos(0,0,0));
```

### `<Entity>.collidesWith(pos, block)`
- Description: This checks whether the entity collides with a block at a given position
- Parameters:
  - Pos (`pos`): the position to check
  - Block (`block`): the block to check
- Returns - Boolean: whether the entity collides with the block
- Example:
```kotlin
entity.collidesWith(Pos.get(0, 0, 0), Block.of('minecraft:stone'));
```

### `<Entity>.getAge()`
- Description: This gets the age of the entity in ticks
- Returns - Number: the age of the entity in ticks
- Example:
```kotlin
entity.getAge();
```

### `<Entity>.getBiome()`
- Description: This gets the biome of the entity
- Returns - Biome: the biome the entity is in
- Example:
```kotlin
entity.getBiome();
```

### `<Entity>.getCustomName()`
- Description: This gets the custom name of the entity if it has one
- Returns - String: the custom name of the entity if it has one, otherwise null
- Example:
```kotlin
entity.getCustomName();
```

### `<Entity>.getDimension()`
- Description: This gets the dimension of the entity
- Returns - String: the dimension id of dimension the entity is in
- Example:
```kotlin
entity.getDimension();
```

### `<Entity>.getDistanceTo(otherEntity)`
- Description: This gets the distance between the entity and the other entity
- Parameter - Entity (`otherEntity`): the other entity
- Returns - Number: the distance between the entities
- Example:
```kotlin
entity.getDistanceTo(Player.get());
```

### `<Entity>.getEntityIdNumber()`
- Description: This gets the entity id number of the entity
- Returns - Number: the entity id number
- Example:
```kotlin
entity.getEntityIdNumber();
```

### `<Entity>.getEntityUuid()`
- Description: This gets the uuid of the entity
- Returns - String: the uuid of the entity
- Example:
```kotlin
entity.getEntityUuid();
```

### `<Entity>.getFullId()`
- Description: This gets the full id of the entity, this returns the full id, so for example
'minecraft:cow' you can find all entityNames on
[Joa's Entity Property Encyclopedia](https://joakimthorsen.github.io/MCPropertyEncyclopedia/entities.html)
- Returns - String: the full id of the entity
- Example:
```kotlin
entity.getFullId();
```

### `<Entity>.getHitbox()`
- Description: This gets the hitbox of the entity in a list containing the two corners of the hitbox, the minimum point and the maximum point
- Returns - List: the hitbox of the entity
- Example:
```kotlin
entity.getHitbox();
```

### `<Entity>.getId()`
- Description: This gets the id of the entity, this returns the id, so for examples 'cow'
- Returns - String: the id of the entity
- Example:
```kotlin
entity.getId();
```

### `<Entity>.getLookingAtBlock()`
- Description: This gets the block that the entity is currently looking at
with a max range of 20 blocks, if there is no block then it will return air
- Returns - Block: the block that the entity is looking at, containing the position
- Example:
```kotlin
entity.getLookingAtBlock();
```

### `<Entity>.getLookingAtBlock(maxDistance)`
- Description: This gets the block that the entity is currently looking at
with a specific max range, if there is no block then it will return air
- Parameter - Number (`maxDistance`): the max range to ray cast
- Returns - Block: the block that the entity is looking at, containing the position
- Example:
```kotlin
entity.getLookingAtBlock(10);
```

### `<Entity>.getLookingAtBlock(maxDistance, fluidType)`
- Description: This gets the block that the entity is currently looking at
with a specific max range, and optionally whether fluids should
be included, if there is no block then it will return air
- Parameters:
  - Number (`maxDistance`): the max range to ray cast
  - String (`fluidType`): the types of fluids to include, either 'none', 'sources', or 'all'
- Returns - Block: the block that the entity is looking at, containing the position
- Example:
```kotlin
entity.getLookingAtBlock(10, 'sources');
```

### `<Entity>.getLookingAtPos(maxDistance)`
- Description: This gets the position that the entity is currently looking at with a specific max range
- Parameter - Number (`maxDistance`): the max range to ray cast
- Returns - Pos: the position that the entity is looking at, containing the x, y, and z
- Example:
```kotlin
entity.getLookingAtPos(10);
```

### `<Entity>.getNbt()`
- Description: This gets the nbt of the entity as a map
- Returns - Map: the nbt of the entity
- Example:
```kotlin
entity.getNbt();
```

### `<Entity>.getPitch()`
- Description: This gets the pitch of the entity (vertical head rotation)
- Returns - Number: the pitch of the entity, between -90 and 90
- Example:
```kotlin
entity.getPitch();
```

### `<Entity>.getPos()`
- Description: This gets the position of the entity
- Returns - Pos: the position of the entity
- Example:
```kotlin
entity.getPos();
```

### `<Entity>.getSquaredDistanceTo(otherEntity)`
- Description: This gets the squared distance between the entity and the other entity
- Parameter - Entity (`otherEntity`): the other entity
- Returns - Number: the squared distance between the entities
- Example:
```kotlin
entity.getSquaredDistanceTo(Player.get());
```

### `<Entity>.getTranslatedName()`
- Description: This gets the translated name of the entity, for examples 'minecraft:pig' would return 'Pig' if your language is in english
- Returns - String: the translated name of the entity
- Example:
```kotlin
entity.getTranslatedName();
```

### `<Entity>.getVelocity()`
- Description: This gets the velocity of the entity in a list in the form [x, y, z]
- Returns - List: the velocity of the entity
- Example:
```kotlin
entity.getVelocity();
```

### `<Entity>.getWorld()`
- Description: This gets the world the entity is in
- Returns - World: the world the entity is in
- Example:
```kotlin
entity.getWorld();
```

### `<Entity>.getX()`
- Description: This gets the x position of the entity
- Returns - Number: the x position of the entity
- Example:
```kotlin
entity.getX();
```

### `<Entity>.getY()`
- Description: This gets the y position of the entity
- Returns - Number: the y position of the entity
- Example:
```kotlin
entity.getY();
```

### `<Entity>.getYaw()`
- Description: This gets the yaw of the entity (horizontal head rotation)
- Returns - Number: the yaw of the entity, between -180 and 180
- Example:
```kotlin
entity.getYaw();
```

### `<Entity>.getZ()`
- Description: This gets the z position of the entity
- Returns - Number: the z position of the entity
- Example:
```kotlin
entity.getZ();
```

### `<Entity>.isFalling()`
- Description: Returns true if the entity is falling
- Returns - Boolean: true if the entity is falling, false if not
- Example:
```kotlin
entity.isFalling();
```

### `<Entity>.isGlowing()`
- Description: Returns true if the entity is glowing
- Returns - Boolean: true if the entity is glowing, false if not
- Example:
```kotlin
entity.isGlowing();
```

### `<Entity>.isInLava()`
- Description: Returns true if the entity is in lava
- Returns - Boolean: true if the entity is in lava, false if not
- Example:
```kotlin
entity.isInLava();
```

### `<Entity>.isOf(entityId)`
- Description: This checks if the entity is of the given entity id
- Parameter - String (`entityId`): the entity id to check
- Returns - Boolean: true if the entity is of the given entity id
- Example:
```kotlin
entity.isOf('cow');
```

### `<Entity>.isOnFire()`
- Description: Returns true if the entity is on fire
- Returns - Boolean: true if the entity is on fire, false if not
- Example:
```kotlin
entity.isOnFire();
```

### `<Entity>.isOnGround()`
- Description: Returns true if the entity is on the ground
- Returns - Boolean: true if the entity is on the ground, false if not
- Example:
```kotlin
entity.isOnGround();
```

### `<Entity>.isSneaking()`
- Description: Returns true if the player is sneaking
- Returns - Boolean: true if the player is sneaking, false if not
- Example:
```kotlin
entity.isSneaking();
```

### `<Entity>.isSprinting()`
- Description: Returns true if the player is sprinting
- Returns - Boolean: true if the player is sprinting, false if not
- Example:
```kotlin
entity.isSprinting();
```

### `<Entity>.isSubmergedInWater()`
- Description: Returns true if the entity is submerged in water
- Returns - Boolean: true if the entity is submerged in water, false if not
- Example:
```kotlin
entity.isSubmergedInWater();
```

### `<Entity>.isTouchingWater()`
- Description: Returns true if the entity is touching water
- Returns - Boolean: true if the entity is touching water, false if not
- Example:
```kotlin
entity.isTouchingWater();
```

### `<Entity>.isTouchingWaterOrRain()`
- Description: Returns true if the entity is touching water or rain
- Returns - Boolean: true if the entity is touching water or rain, false if not
- Example:
```kotlin
entity.isTouchingWaterOrRain();
```

### `<Entity>.setGlowing(glowing)`
- Description: This sets the entity to either start glowing or stop glowing on the client
- Parameter - Boolean (`glowing`): the glowing state
- Example:
```kotlin
entity.setGlowing(true);
```

## Static Methods

### `Entity.of(entityId)`
- Description: This converts an entityId into an entity instance.
This will throw an error if the id is not valid.
- Parameter - String (`entityId`): the entityId to convert to an entity
- Returns - Entity: the entity instance from the id
- Example:
```kotlin
Entity.of('minecraft:pig');
```



# Enum class
Enum class for Arucas

This class is the super class of all enums in Arucas.
Enums cannot be instantiated or extended
Class does not need to be imported

## Methods

### `<Enum>.getName()`
- Description: This allows you to get the name of an enum value
- Returns - String: the name of the enum value
- Example:
```kotlin
enum.getName();
```

### `<Enum>.ordinal()`
- Description: This allows you to get the ordinal of the enum value
- Returns - Number: the ordinal of the enum value
- Example:
```kotlin
enum.ordinal();
```



# Error class
Error class for Arucas

This class is used for errors, and this is the only type that can be thrown.
You are able to extend this class to create your own error types
Class does not need to be imported

## Constructors

### `new Error()`
- Description: This creates a new Error value with no message
- Example:
```kotlin
new Error();
```

### `new Error(details)`
- Description: This creates a new Error value with the given details as a message
- Parameter - String (`details`): the details of the error
- Example:
```kotlin
new Error('This is an error');
```

### `new Error(details, value)`
- Description: This creates a new Error value with the given details as a message and the given value
- Parameters:
  - String (`details`): the details of the error
  - Object (`value`): the value that is related to the error
- Example:
```kotlin
new Error('This is an error', [1, 2, 3]);
```

## Methods

### `<Error>.getDetails()`
- Description: This returns the raw message of the error
- Returns - String: the details of the error
- Example:
```kotlin
error.getDetails();
```

### `<Error>.getStackTraceString()`
- Description: This prints the stack trace of this error
- Returns - String: the stack trace converted to a string
- Example:
```kotlin
error.getStackTraceString();
```

### `<Error>.getValue()`
- Description: This returns the value that is related to the error
- Returns - Object: the value that is related to the error
- Example:
```kotlin
error.getValue();
```



# FakeBlock class
FakeBlock class for Arucas

This class can be used to create fake blocks which can be rendered in the world.
Import with `import FakeBlock from Minecraft;`

## Constructors

### `new FakeBlock(block, pos)`
- Description: Creates a fake block with the given block and position
- Parameters:
  - Block (`block`): The block to use
  - Pos (`pos`): The position of the block
- Example:
```kotlin
new FakeBlock(Material.BEDROCK.asBlock(), new Pos(0, 0, 0));
```

## Methods

### `<FakeBlock>.getBlock()`
- Description: Gets the current block type of the fake block
- Returns - Block: The block type of the fake block
- Example:
```kotlin
fakeBlock.getBlock();
```

### `<FakeBlock>.getDirection()`
- Description: Gets the direction of the fake block
- Returns - String: The direction of the fake block, may be null
- Example:
```kotlin
fakeBlock.getDirection();
```

### `<FakeBlock>.getPos()`
- Description: Gets the position of the fake block
- Returns - Pos: The position of the fake block
- Example:
```kotlin
fakeBlock.getPos();
```

### `<FakeBlock>.setBlock(block)`
- Description: Sets the block type to render of the fake block
- Parameter - Block (`block`): The block to render
- Example:
```kotlin
fakeBlock.setBlock(Material.BEDROCK.asBlock());
```

### `<FakeBlock>.setCull(cull)`
- Description: Sets whether the fake block should be culled
- Parameter - Boolean (`cull`): Whether the fake block should be culled
- Example:
```kotlin
fakeBlock.setCull(true);
```

### `<FakeBlock>.setDirection(direction)`
- Description: Sets the direction of the fake block,
this may be null in which case the block will face the player
- Parameter - String (`direction`): The direction of the fake block
- Example:
```kotlin
fakeBlock.setDirection(Direction.UP);
```

### `<FakeBlock>.setPos(pos)`
- Description: Sets the position of the fake block
- Parameter - Pos (`pos`): The position of the fake block
- Example:
```kotlin
fakeBlock.setPos(new Pos(0, 0, 0));
```

### `<FakeBlock>.shouldCull()`
- Description: Gets whether the fake block should be culled
- Returns - Boolean: Whether the fake block should be culled
- Example:
```kotlin
fakeBlock.shouldCull();
```



# FakeEntity class
FakeEntity class for Arucas

This allows you to create a fake entity which can be rendered in the world.
Import with `import FakeEntity from Minecraft;`

## Constructors

### `new FakeEntity(entity, world)`
- Description: Creates a new fake entity
- Parameters:
  - Entity (`entity`): The entity that you want to create into a fake entity
  - World (`world`): The world that the entity is being rendered in
- Example:
```kotlin
fakeEntity = new FakeEntity();
```

## Methods

### `<FakeEntity>.despawn()`
- Description: Despawns the entity (makes it not render in the world)
- Example:
```kotlin
fakeEntity.despawn();
```

### `<FakeEntity>.getBodyYaw()`
- Description: Gets the body yaw of the entity
- Returns - Number: The body yaw of the entity
- Example:
```kotlin
bodyYaw = fakeEntity.getBodyYaw();
```

### `<FakeEntity>.getPitch()`
- Description: Gets the pitch of the entity
- Returns - Number: The pitch of the entity
- Example:
```kotlin
pitch = fakeEntity.getPitch();
```

### `<FakeEntity>.getPos()`
- Description: Gets the position of the entity
- Returns - Pos: The position of the entity
- Example:
```kotlin
pos = fakeEntity.getPos();
```

### `<FakeEntity>.getWorld()`
- Description: Gets the world that the entity is being rendered in
- Returns - World: The world that the entity is being rendered in
- Example:
```kotlin
world = fakeEntity.getWorld();
```

### `<FakeEntity>.getYaw()`
- Description: Gets the yaw of the entity
- Returns - Number: The yaw of the entity
- Example:
```kotlin
yaw = fakeEntity.getYaw();
```

### `<FakeEntity>.setBodyYaw(bodyYaw)`
- Description: Sets the body yaw of the entity with no interpolation
- Parameter - Number (`bodyYaw`): The new body yaw of the entity
- Example:
```kotlin
fakeEntity.setBodyYaw(0);
```

### `<FakeEntity>.setBodyYaw(bodyYaw, interpolationSteps)`
- Description: Sets the body yaw of the entity
- Parameters:
  - Number (`bodyYaw`): The new body yaw of the entity
  - Number (`interpolationSteps`): The number of interpolation steps to take
- Example:
```kotlin
fakeEntity.setBodyYaw(0, 10);
```

### `<FakeEntity>.setPitch(pitch)`
- Description: Sets the pitch of the entity with no interpolation
- Parameter - Number (`pitch`): The new pitch of the entity
- Example:
```kotlin
fakeEntity.setPitch(0);
```

### `<FakeEntity>.setPitch(pitch, interpolationSteps)`
- Description: Sets the pitch of the entity
- Parameters:
  - Number (`pitch`): The new pitch of the entity
  - Number (`interpolationSteps`): The number of interpolation steps to take
- Example:
```kotlin
fakeEntity.setPitch(0, 10);
```

### `<FakeEntity>.setPos(pos)`
- Description: Sets the position of the entity with no interpolation
- Parameter - Pos (`pos`): The new position of the entity
- Example:
```kotlin
fakeEntity.setPos(new Pos(0, 0, 0));
```

### `<FakeEntity>.setPos(pos, interpolationSteps)`
- Description: Sets the position of the entity
- Parameters:
  - Pos (`pos`): The new position of the entity
  - Number (`interpolationSteps`): The number of interpolation steps to take
- Example:
```kotlin
fakeEntity.setPos(new Pos(0, 0, 0), 0);
```

### `<FakeEntity>.setWorld(world)`
- Description: Sets the world that the entity is being rendered in
- Parameter - World (`world`): The world that the entity is being rendered in
- Example:
```kotlin
fakeEntity.setWorld(MinecraftClient.getClient().getWorld());
```

### `<FakeEntity>.setYaw(yaw)`
- Description: Sets the yaw of the entity with no interpolation
- Parameter - Number (`yaw`): The new yaw of the entity
- Example:
```kotlin
fakeEntity.setYaw(0);
```

### `<FakeEntity>.setYaw(yaw, interpolationSteps)`
- Description: Sets the yaw of the entity
- Parameters:
  - Number (`yaw`): The new yaw of the entity
  - Number (`interpolationSteps`): The number of interpolation steps to take
- Example:
```kotlin
fakeEntity.setYaw(0, 10);
```

### `<FakeEntity>.spawn()`
- Description: Spawns the entity (makes it render in the world)
- Example:
```kotlin
fakeEntity.spawn();
```

### `<FakeEntity>.updatePosAndRotation(pos, yaw, pitch)`
- Description: Updates the position and rotation of the entity with no interpolation
- Parameters:
  - Pos (`pos`): The new position of the entity
  - Number (`yaw`): The new yaw of the entity
  - Number (`pitch`): The new pitch of the entity
- Example:
```kotlin
fakeEntity.updatePosAndRotation(new Pos(100, 0, 100), 0, 0);
```

### `<FakeEntity>.updatePosAndRotation(pos, yaw, pitch, interpolationSteps)`
- Description: Updates the position and rotation of the entity
- Parameters:
  - Pos (`pos`): The new position of the entity
  - Number (`yaw`): The new yaw of the entity
  - Number (`pitch`): The new pitch of the entity
  - Number (`interpolationSteps`): The number of interpolation steps to take
- Example:
```kotlin
fakeEntity.updatePosAndRotation(new Pos(100, 0, 100), 0, 0, 10);
```



# FakeScreen class
FakeScreen class for Arucas

This class extends Screen and so inherits all of their methods too,
this class is used to create client side inventory screens.
Import with `import FakeScreen from Minecraft;`

## Constructors

### `new FakeScreen(name, rows)`
- Description: Creates a FakeScreen instance with given name and given amount of rows,
this will throw an error if the rows are not between 1 and 6
- Parameters:
  - String (`name`): the name of the screen
  - Number (`rows`): the number of rows between 1 - 6
- Example:
```kotlin
new FakeScreen('MyScreen', 6);
```

## Methods

### `<FakeScreen>.getStackForSlot(slotNum)`
- Description: Gets the stack for the given slot, if the slot is out of bounds it returns null
- Parameter - Number (`slotNum`): the slot number
- Returns - ItemStack: the stack for the given slot
- Example:
```kotlin
fakeScreen.getStackForSlot(0);
```

### `<FakeScreen>.onClick(function)`
- Description: This sets the callback for when a slot is clicked in the inventory.
The callback must have 3 parameters, the first is the item stack that was clicked,
then second is the slot number, third is the action as a string
- Parameter - Function (`function`): the callback function
- Example:
```kotlin
fakeScreen.onClick(fun(item, slotNum, action) {
    // action can be any of the following:
    // 'PICKUP', 'QUICK_MOVE', 'SWAP', 'CLONE', 'THROW', 'QUICK_CRAFT', or 'PICKUP_ALL'
    print(action);
});
```

### `<FakeScreen>.setStackForSlot(slotNum, stack)`
- Description: Sets the stack for the given slot, if the slot is out of bounds it won't be set
- Parameters:
  - Number (`slotNum`): the slot number
  - ItemStack (`stack`): the stack to set
- Example:
```kotlin
fakeScreen.setStackForSlot(0, Material.DIAMOND_BLOCK.asItemStack());
```



# File class
File class for Arucas

This class allows you to read and write files
Class does not need to be imported

## Constructors

### `new File(path)`
- Description: This creates a new File object with set path
- Parameter - String (`path`): the path of the file
- Example:
```kotlin
new File('foo/bar/script.arucas');
```

## Methods

### `<File>.createDirectory()`
- Description: This creates all parent directories of the file if they don't already exist
- Returns - Boolean: true if the directories were created
- Example:
```kotlin
file.createDirectory();
```

### `<File>.delete()`
- Description: This deletes the file
- Returns - Boolean: true if the file was deleted
- Example:
```kotlin
file.delete();
```

### `<File>.exists()`
- Description: This returns if the file exists
- Returns - Boolean: true if the file exists
- Example:
```kotlin
file.exists();
```

### `<File>.getAbsolutePath()`
- Description: This returns the absolute path of the file
- Returns - String: the absolute path of the file
- Example:
```kotlin
file.getAbsolutePath();
```

### `<File>.getName()`
- Description: This returns the name of the file
- Returns - String: the name of the file
- Example:
```kotlin
File.getName();
```

### `<File>.getPath()`
- Description: This returns the path of the file
- Returns - String: the path of the file
- Example:
```kotlin
file.getPath();
```

### `<File>.getSubFiles()`
- Description: This returns a list of all the sub files in the directory
- Returns - List: a list of all the sub files in the directory
- Example:
```kotlin
file.getSubFiles();
```

### `<File>.open()`
- Description: This opens the file (as in opens it on your os)
- Example:
```kotlin
file.open();
```

### `<File>.read()`
- Description: This reads the file and returns the contents as a string
- Returns - String: the contents of the file
- Example:
```kotlin
file.read();
```

### `<File>.resolve(filePath)`
- Description: This gets a resolves file object from the current one
- Parameter - String (`filePath`): the relative file path
- Returns - File: the resolved file
- Example:
```kotlin
file.resolve('child.txt');
```

### `<File>.write(string)`
- Description: This writes a string to a file
- Parameter - String (`string`): the string to write to the file
- Example:
```kotlin
file.write('Hello World!');
```

## Static Methods

### `File.getDirectory()`
- Description: This returns the file of the working directory
- Returns - File: the file of the working directory
- Example:
```kotlin
File.getDirectory();
```



# Function class
Function class for Arucas

This class is used for functions, and this is the only type that can be called.
You are able to extend this class and implement an 'invoke' method to create
your own function types, this class cannot be instantiated directly
Class does not need to be imported

## Constructors

### `new Function()`
- Description: This creates a function, this cannot be called directly, only from child classes
- Example:
```kotlin
class ChildFunction: Function {
    ChildFunction(): super();
}
```



# Future class
Future class for Arucas

This class is used to represent values that are in the future.
More precisely values that are being evaluated on another thread,
this allows you to access those values once they've been processed
Class does not need to be imported

## Methods

### `<Future>.await()`
- Description: This blocks the current thread until the future has
been completed and then returns the value of the future
- Returns - Object: The value of the future
- Example:
```kotlin
future.await();
```

### `<Future>.isComplete()`
- Description: This returns whether the future has been completed
- Returns - Boolean: Whether the future has been completed
- Example:
```kotlin
future.isComplete();
```

## Static Methods

### `Future.completed(value)`
- Description: This returns a future that with a complete value
- Parameter - Object (`value`): The value to complete the future with
- Returns - Future: The future that has been completed with the value
- Example:
```kotlin
future = Future.completed(true);
```



# GameEvent class
GameEvent class for Arucas

This class allows you to register listeners for game events in Minecraft.
Import with `import GameEvent from Minecraft;`

## Constructors

### `new GameEvent(eventName, onEvent)`
- Description: This creates a new GameEvent, that is not cancellable
- Parameters:
  - String (`eventName`): The name of the event, you can find these on the GameEvents page
  - Function (`onEvent`): The function to run when the event is called, some events may have parameters
- Example:
```kotlin
new GameEvent('onClientTick', fun() { });
```

### `new GameEvent(eventName, onEvent, cancellable)`
- Description: This creates a new GameEvent
- Parameters:
  - String (`eventName`): The name of the event, you can find these on the GameEvents page
  - Function (`onEvent`): The function to run when the event is called, some events may have parameters
  - Boolean (`cancellable`): Whether or not the event is cancellable, if it is then it will run on the main thread
- Example:
```kotlin
new GameEvent('onClientTick', fun() { }, true);
```

## Methods

### `<GameEvent>.isRegistered()`
- Description: This returns whether or not the event is registered
- Returns - Boolean: Whether or not the event is registered
- Example:
```kotlin
gameEvent.isRegistered();
```

### `<GameEvent>.register()`
- Description: This registers the event
- Example:
```kotlin
gameEvent.register();
```

### `<GameEvent>.unregister()`
- Description: This unregisters the event
- Example:
```kotlin
gameEvent.unregister();
```

## Static Methods

### `GameEvent.cancel()`
- Description: If called on a cancellable event, this will stop execution and cancel the event,
if called on a non-cancellable event, or not on an event, this will throw an error
- Example:
```kotlin
GameEvent.cancel();
```

### `GameEvent.future()`
- Description: This returns a future that allows you to wait for an event to occur
- Returns - Future: the future, will complete once the event has occurred
- Example:
```kotlin
GameEvent.future('onClientTick').await();
```

### `GameEvent.unregisterAll()`
- Description: This unregisters all events registered by this script
- Example:
```kotlin
GameEvent.unregisterAll();
```



# ItemEntity class
ItemEntity class for Arucas

This class extends Entity and so inherits all of their methods too,
ItemEntities are entities that are dropped items.
Import with `import ItemEntity from Minecraft;`

## Methods

### `<ItemEntity>.getCustomName()`
- Description: This method returns the custom name of the ItemEntity
- Returns - String: the custom name of the entity
- Example:
```kotlin
itemEntity.getCustomName();
```

### `<ItemEntity>.getItemAge()`
- Description: This method returns the age of the ItemEntity
this is increased every tick and the item entity despawns after 6000 ticks
- Returns - Number: the age of the entity
- Example:
```kotlin
itemEntity.getItemAge();
```

### `<ItemEntity>.getItemStack()`
- Description: This method returns the ItemStack that is held in the ItemEntity
- Returns - ItemStack: the ItemStack that the entity holds
- Example:
```kotlin
itemEntity.getItemStack();
```

### `<ItemEntity>.getThrower()`
- Description: This method returns the player that threw the ItemEntity, null if not thrown by a player or player not found
- Returns - Player: the player that threw the entity
- Example:
```kotlin
itemEntity.getThrower();
```



# ItemStack class
ItemStack class for Arucas

This class represents an item stack. It can be used to create new item stacks, or to modify existing ones.
Import with `import ItemStack from Minecraft;`

## Methods

### `<ItemStack>.asEntity()`
- Description: This creates an item entity with the item
- Returns - ItemEntity: the entity of the ItemStack
- Example:
```kotlin
itemStack.asEntity();
```

### `<ItemStack>.getCount()`
- Description: This gets the count of the ItemStack, the amount of items in the stack
- Returns - Number: the count of the ItemStack
- Example:
```kotlin
itemStack.getCount();
```

### `<ItemStack>.getCustomName()`
- Description: This gets the custom name of the ItemStack
- Returns - String: the custom name of the ItemStack
- Example:
```kotlin
itemStack.getCustomName();
```

### `<ItemStack>.getDurability()`
- Description: This gets the durability of the item
- Returns - Number: the durability of the item
- Example:
```kotlin
itemStack.getDurability();
```

### `<ItemStack>.getEnchantments()`
- Description: This gets the enchantments of the item, in a map containing the
id of the enchantment as the key and the level of the enchantment as the value
- Returns - Map: the enchantments of the item, map may be empty
- Example:
```kotlin
itemStack.getEnchantments();
```

### `<ItemStack>.getMaterial()`
- Description: This gets the material of the ItemStack
- Returns - Material: the material of the ItemStack
- Example:
```kotlin
itemStack.getMaterial();
```

### `<ItemStack>.getMaxCount()`
- Description: This gets the max stack size of the ItemStack
- Returns - Number: the max stack size of the ItemStack
- Example:
```kotlin
itemStack.getMaxCount();
```

### `<ItemStack>.getMaxDurability()`
- Description: This gets the max durability of the item
- Returns - Number: the max durability of the item
- Example:
```kotlin
itemStack.getMaxDurability();
```

### `<ItemStack>.getMiningSpeedMultiplier(block)`
- Description: This gets the mining speed multiplier of the ItemStack for the given Block,
for example a diamond pickaxe on stone would have a higher multiplier than air on stone
- Parameter - Block (`block`): the Block to get the mining speed multiplier for
- Returns - Number: the mining speed multiplier of the ItemStack for the given Block
- Example:
```kotlin
pickaxe = Material.DIAMOND_PICKAXE.asItemStack();
goldBlock = Material.GOLD_BLOCK.asBlock();

pickaxe.getMiningSpeedMultiplier(goldBlock);
```

### `<ItemStack>.getNbt()`
- Description: This gets the NBT data of the ItemStack as a Map
- Returns - Map: the NBT data of the ItemStack
- Example:
```kotlin
itemStack.getNbt();
```

### `<ItemStack>.getNbtAsString()`
- Description: This gets the NBT data of the ItemStack as a String
- Returns - String: the NBT data of the ItemStack
- Example:
```kotlin
itemStack.getNbtAsString();
```

### `<ItemStack>.getTranslatedName()`
- Description: This gets the translated name of the ItemStack, for example
'diamond_sword' would return 'Diamond Sword' if your language is English
- Returns - String: the translated name of the ItemStack
- Example:
```kotlin
itemStack.getTranslatedName();
```

### `<ItemStack>.isBlockItem()`
- Description: This checks if the ItemStack can be placed as a block
- Returns - Boolean: true if the ItemStack can be placed as a block, false otherwise
- Example:
```kotlin
itemStack.isBlockItem();
```

### `<ItemStack>.isNbtEqual(itemStack)`
- Description: This checks if the ItemStack has the same NBT data as the other given ItemStack
- Parameter - ItemStack (`itemStack`): the other ItemStack to compare to
- Returns - Boolean: true if the ItemStack has the same NBT data as the other given ItemStack
- Example:
```kotlin
itemStack.isNbtEqual(Material.GOLD_INGOT.asItemStack());
```

### `<ItemStack>.isStackable()`
- Description: This checks if the ItemStack is stackable
- Returns - Boolean: true if the ItemStack is stackable, false otherwise
- Example:
```kotlin
itemStack.isStackable();
```

### `<ItemStack>.setCustomName(customName)`
- Description: This sets the custom name of the ItemStack
- Parameter - Text (`customName`): the custom name of the ItemStack, this can be text or string
- Returns - ItemStack: the ItemStack with the new custom name
- Example:
```kotlin
itemStack.setCustomName('My Pickaxe');
```

### `<ItemStack>.setItemLore(lore)`
- Description: This sets the lore of the ItemStack
- Parameter - List (`lore`): the lore of the ItemStack as a list of Text
- Returns - ItemStack: the ItemStack with the new lore
- Example:
```kotlin
itemStack = Material.DIAMOND_PICKAXE.asItemStack();
itemStack.setItemLore([
    Text.of('This is a pickaxe'),
    Text.of('It is made of diamond')
]);
```

### `<ItemStack>.setNbt(nbtMap)`
- Description: This sets the NBT data of the ItemStack
- Parameter - Map (`nbtMap`): the NBT data of the ItemStack as a map
- Returns - ItemStack: the ItemStack with the new NBT data
- Example:
```kotlin
itemStack.setNbt({'Lore': []});
```

### `<ItemStack>.setNbtFromString(nbtString)`
- Description: This sets the NBT data of the ItemStack from an NBT string
- Parameter - String (`nbtString`): the NBT data of the ItemStack as a string
- Returns - ItemStack: the ItemStack with the new NBT data
- Example:
```kotlin
itemStack.setNbtFromString("{\"Lore\": []}");
```

### `<ItemStack>.setStackSize(stackSize)`
- Description: This sets the stack size of the ItemStack
- Parameter - Number (`stackSize`): the stack size of the ItemStack
- Returns - ItemStack: the ItemStack with the new stack size
- Example:
```kotlin
itemStack.setStackSize(5);
```

## Static Methods

### `ItemStack.of(material)`
- Description: This creates an ItemStack from a material or a string
- Parameter - Material (`material`): the material, item stack, block, or string to create the ItemStack from
- Returns - ItemStack: the new ItemStack instance
- Example:
```kotlin
ItemStack.of('dirt');
```

### `ItemStack.parse(nbtString)`
- Description: This creates an ItemStack from a NBT string, this can be in the form of a map
or an ItemStack NBT, or like the item stack command format
- Parameter - String (`nbtString`): the NBT string to create the ItemStack from
- Returns - ItemStack: the new ItemStack instance
- Example:
```kotlin
ItemStack.parse('{id:"minecraft:dirt",Count:64}')
```



# Iterable class
Iterable class for Arucas

This class represents an object that can be iterated over.
This class is used internally to denote whether an object can be
iterated over inside a foreach loop
Class does not need to be imported

## Constructors

### `new Iterable()`
- Description: This creates an iterable, this cannot be called directly, only from child classes
- Example:
```kotlin
class IterableImpl: Iterable {
    IterableImpl(): super();
    
    fun iterator() {
        // Example
        return [].iterator();
    }
}
```

## Methods

### `<Iterable>.iterator()`
- Description: This gets the generated iterator
- Returns - Iterator: the generated iterator
- Example:
```kotlin
iterable = [];
i = iterable.iterator();
while (i.hasNext()) {
    next = i.next();
}

// Or just, compiles to above
foreach (next : iterable); 
```



# Iterator class
Iterator class for Arucas

This class represents an object that iterates.
This is what is used internally to iterate in a
foreach loop and you can create your own iterators
to use be able to use them inside a foreach
Class does not need to be imported

## Constructors

### `new Iterator()`
- Description: This creates an iterator, this cannot be called directly, only from child classes
- Example:
```kotlin
class IteratorImpl: Iterator {
    IteratorImpl(): super();
    
    fun hasNext() {
        return false;
    }
    
    fun next() {
        throw new Error("Nothing next");
    }
}
```

## Methods

### `<Iterator>.hasNext()`
- Description: Checks whether the iterator has a next item to iterate
- Returns - Boolean: whether there are items left to iterate
- Example:
```kotlin
iterator = [].iterator();
iterator.hasNext();
```

### `<Iterator>.next()`
- Description: Gets the next item in the iterator, may throw if there is no next item
- Returns - Object: the next item
- Example:
```kotlin
iterator = [10, 20].iterator();
iterator.next(); // 10
iterator.next(); // 20
```



# Java class
Java class for Arucas

This class wraps Java values allowing for interactions between Java and Arucas.
This class cannot be instantiated or extended but you can create Java values by
using the static method 'Java.valueOf()' to convert Arucas to Java
Import with `import Java from util.Internal;`

## Methods

### `<Java>.callMethod(methodName, parameters...)`
- Deprecated: You should call the method directly on the value: Java.valueOf('').isBlank();
- Description: This calls the specified method with the specified parameters, calling the method
with this function has no benefits unless you are calling a function that also is
native to Arucas. For example `object.copy()` will use the Arucas 'copy' function.
But this is extremely rare so almost all of the time you should all the method normally.
- Parameters:
  - String (`methodName`): the name of the method
  - Object (`parameters...`): the parameters to call the method with
- Returns - Java: the return value of the method call wrapped in the Java wrapper
- Example:
```kotlin
Java.valueOf('').callMethod('isBlank');
```

### `<Java>.getField(fieldName)`
- Deprecated: You should call the method directly on the value: `Java.constructClass('me.senseiwells.impl.Test').A;`
- Description: This returns the Java wrapped value of the specified field.
There is no reason for you to be using this method, it will be removed in future versions
- Parameter - String (`fieldName`): the name of the field
- Returns - Java: the Java wrapped value of the field
- Example:
```kotlin
Java.constructClass('me.senseiwells.impl.Test').getField('A');
```

### `<Java>.getMethodDelegate(methodName, parameters)`
- Deprecated: Consider wrapping the method in a lambda instead
- Description: This returns a method delegate for the specified method name and parameters.
This should be avoided and replaced with a Arucas function wrapping the call instead.
For example: `delegate = (fun() { Java.valueOf('').isBlank(); });`.
Another thing to note is that the parameter count parameter is no longer
used and ignored internally, instead the parameters are calculated when you
call the delegate. The parameter remains for backwards compatability.
- Parameters:
  - String (`methodName`): the name of the method
  - Number (`parameters`): the number of parameters
- Returns - Function: the function containing the Java method delegate
- Example:
```kotlin
Java.valueOf('string!').getMethodDelegate('isBlank', 0);
```

### `<Java>.setField(fieldName, value)`
- Deprecated: You should assign the value directly on the value: Java.constructClass('me.senseiwells.impl.Test').A = 'Hello';
- Description: This sets the specified field to the specified value
There is no reason for you to be using this method, it will be removed in future versions
- Parameters:
  - String (`fieldName`): the name of the field
  - Object (`value`): the value to set the field to, the value type must match the type of the field
- Example:
```kotlin
Java.constructClass('me.senseiwells.impl.Test').setField('A', 'Hello');
```

### `<Java>.toArucas()`
- Description: This converts the Java value to an Arucas Value if possible, this may still
be of a Java value if it cannot be converted. For example, Strings, Numbers, Lists
will be converted but 
- Returns - Object: the Value in Arucas, this may still be of Java value if the value cannot be converted into an Arucas value, values like Strings, Numbers, Lists, etc... will be converted
- Example:
```kotlin
Java.valueOf([1, 2, 3]).toArucas();
```

## Static Methods

### `Java.arrayOf(values...)`
- Description: Creates a Java Object array with a given values, this will be the size of the array,
this cannot be used to create primitive arrays
- Parameter - Object (`values...`): the values to add to the array
- Returns - Java: the Java Object array
- Example:
```kotlin
Java.arrayOf(1, 2, 3, 'string!', false);
```

### `Java.booleanArray(size)`
- Description: Creates a Java boolean array with a given size, the array is filled with false
by default and can be filled with only booleans
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java boolean array
- Example:
```kotlin
Java.booleanArray(10);
```

### `Java.booleanOf(bool)`
- Description: Creates a Java value boolean, to be used in Java
- Parameter - Boolean (`bool`): the boolean to convert to a Java boolean
- Returns - Java: the boolean in Java wrapper
- Example:
```kotlin
Java.booleanOf(true);
```

### `Java.byteArray(size)`
- Description: Creates a Java byte array with a given size, the array is filled with 0's
by default and can be filled with only bytes
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java byte array
- Example:
```kotlin
Java.byteArray(10);
```

### `Java.byteOf(num)`
- Description: Creates a Java value byte, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java byte
- Returns - Java: the byte in Java wrapper
- Example:
```kotlin
Java.byteOf(1);
```

### `Java.callStaticMethod(className, methodName, parameters...)`
- Deprecated: You should use 'Java.classOf(name)' then call the static method
- Description: Calls a static method of a Java class.
This should be avoided and instead use 'classOf' to get the
instance of the class then call the static method on that
- Parameters:
  - String (`className`): the name of the class
  - String (`methodName`): the name of the method
  - Object (`parameters...`): any parameters to call the method with
- Returns - Java: the return value of the method wrapped in the Java wrapper
- Example:
```kotlin
Java.callStaticMethod('java.lang.Integer', 'parseInt', '123');
```

### `Java.charArray(size)`
- Description: Creates a Java char array with a given size, the array is filled with null characters's
by default and can be filled with only chars
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java char array
- Example:
```kotlin
Java.charArray(10);
```

### `Java.charOf(char)`
- Description: Creates a Java value char, to be used in Java
- Parameter - String (`char`): the char to convert to a Java char
- Returns - Java: the char in Java wrapper
- Example:
```kotlin
Java.charOf('a');
```

### `Java.classFromName(className)`
- Deprecated: You should use 'Java.classOf(name)' instead
- Description: Gets a Java class from the name of the class
- Parameter - String (`className`): the name of the class you want to get
- Returns - Java: the Java Class<?> value wrapped in the Java wrapper
- Example:
```kotlin
Java.classFromName('java.util.ArrayList');
```

### `Java.classOf(className)`
- Description: Gets a Java class from the name of the class
- Parameter - String (`className`): the name of the class you want to get
- Returns - JavaClass: the Java class value which can be used as a class reference
- Example:
```kotlin
Java.classOf('java.util.ArrayList');
```

### `Java.constructClass(className, parameters...)`
- Deprecated: You should use 'Java.classOf(name)' then call the result to construct the class
- Description: This constructs a Java class with specified class name and parameters.
This should be avoided and instead use 'classOf' to get the class
instance then call the constructor on that instance
- Parameters:
  - String (`className`): the name of the class
  - Object (`parameters...`): any parameters to pass to the constructor
- Returns - Java: the constructed Java Object wrapped in the Java wrapper
- Example:
```kotlin
Java.constructClass('java.util.ArrayList');
```

### `Java.consumerOf(function)`
- Description: Creates a Java Consumer object from a given function, it must have one
parameter and any return values will be ignored
- Parameter - Function (`function`): the function to be executed
- Returns - Java: the Java Consumer object
- Example:
```kotlin
Java.consumerOf(fun(something) {
    print(something);
});
```

### `Java.doubleArray(size)`
- Description: Creates a Java double array with a given size, the array is filled with 0's
by default and can be filled with only doubles
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java double array
- Example:
```kotlin
Java.doubleArray(10);
```

### `Java.doubleOf(num)`
- Description: Creates a Java value double, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java double
- Returns - Java: the double in Java wrapper
- Example:
```kotlin
Java.doubleOf(1.0);
```

### `Java.floatArray(size)`
- Description: Creates a Java float array with a given size, the array is filled with 0's
by default and can be filled with only floats
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java float array
- Example:
```kotlin
Java.floatArray(10);
```

### `Java.floatOf(num)`
- Description: Creates a Java value float, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java float
- Returns - Java: the float in Java wrapper
- Example:
```kotlin
Java.floatOf(1.0);
```

### `Java.functionOf(function)`
- Description: Creates a Java Function object from a given function
- Parameter - Function (`function`): the function to be executed, this must have one parameter and must return a value
- Returns - Java: the Java Function object
- Example:
```kotlin
Java.functionOf(fun(something) {
    return something;
});
```

### `Java.getStaticField(className, fieldName)`
- Deprecated: You should use 'Java.classOf(name)' then access the static field
- Description: Gets a static field Java value from a Java class
- Parameters:
  - String (`className`): the name of the class
  - String (`fieldName`): the name of the field
- Returns - Java: the Java value of the field wrapped in the Java wrapper
- Example:
```kotlin
Java.getStaticField('java.lang.Integer', 'MAX_VALUE');
```

### `Java.getStaticMethodDelegate(className, methodName, parameters)`
- Deprecated: You should use 'Java.classOf(name)' then wrap the static method
- Description: Gets a static method delegate from a Java class, this should
be avoided and instance use 'classOf' to get the class instance
and then call the method on that class instance. The parameter count
parameter is no longer used internally but remains for backwards compatibility
- Parameters:
  - String (`className`): the name of the class
  - String (`methodName`): the name of the method
  - Number (`parameters`): the number of parameters
- Returns - Function: the delegated Java method in an Arucas Function
- Example:
```kotlin
Java.getStaticMethodDelegate('java.lang.Integer', 'parseInt', 1);
```

### `Java.intArray(size)`
- Description: Creates a Java int array with a given size, the array is filled with 0's
by default and can be filled with only ints
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java int array
- Example:
```kotlin
Java.intArray(10);
```

### `Java.intOf(num)`
- Description: Creates a Java value int, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java int
- Returns - Java: the int in Java wrapper
- Example:
```kotlin
Java.intOf(1);
```

### `Java.longArray(size)`
- Description: Creates a Java long array with a given size, the array is filled with 0's
by default and can be filled with only longs
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java long array
- Example:
```kotlin
Java.longArray(10);
```

### `Java.longOf(num)`
- Description: Creates a Java value long, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java long
- Returns - Java: the long in Java wrapper
- Example:
```kotlin
Java.longOf(1);
```

### `Java.objectArray(size)`
- Description: Creates a Java Object array with a given size, the array is filled with null values
by default and can be filled with any Java values, this array cannot be expanded
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java Object array
- Example:
```kotlin
Java.arrayWithSize(10);
```

### `Java.predicateOf(function)`
- Description: Creates a Java Predicate object from a given function
- Parameter - Function (`function`): the function to be executed, this must have one parameter and must return a boolean
- Returns - Java: the Java Predicate object
- Example:
```kotlin
Java.predicateOf(fun(something) {
    return something == 'something';
});
```

### `Java.runnableOf(function)`
- Description: Creates a Java Runnable object from a given function, this must
have no paramters and any return values will be ignored
- Parameter - Function (`function`): the function to be executed
- Returns - Java: the Java Runnable object
- Example:
```kotlin
Java.runnableOf(fun() {
    print('runnable');
});
```

### `Java.setStaticField(className, fieldName, newValue)`
- Deprecated: You should use 'Java.classOf(name)' then assign the static field
- Description: Sets a static field in a Java class with a new value
- Parameters:
  - String (`className`): the name of the class
  - String (`fieldName`): the name of the field
  - Object (`newValue`): the new value
- Example:
```kotlin
// Obviously this won't work, but it's just an example
Java.setStaticField('java.lang.Integer', 'MAX_VALUE', Java.intOf(100));"
```

### `Java.shortArray(size)`
- Description: Creates a Java short array with a given size, the array is filled with 0's
by default and can be filled with only shorts
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java short array
- Example:
```kotlin
Java.shortArray(10);
```

### `Java.shortOf(num)`
- Description: Creates a Java value short, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java short
- Returns - Java: the short in Java wrapper
- Example:
```kotlin
Java.shortOf(1);
```

### `Java.supplierOf(function)`
- Description: Creates a Java Supplier object from a given function
- Parameter - Function (`function`): the function to be executed, this must have no parameters and must return (supply) a value
- Returns - Java: the Java Supplier object
- Example:
```kotlin
Java.supplierOf(fun() {
    return 'supplier';
});
```

### `Java.valueOf(value)`
- Description: Converts any Arucas value into a Java value then wraps it in the Java wrapper and returns it
- Parameter - Object (`value`): any value to get the Java value of
- Returns - Java: the Java wrapper value, null if argument was null
- Example:
```kotlin
Java.valueOf('Hello World!');
```



# JavaClass class
JavaClass class for Arucas

This class 'acts' as a Java class. You are able to call this class which
will invoke the Java class' constructor, and access and assign the static
fields of the class. This class cannot be instantiated or extended.
Import with `import JavaClass from util.Internal;`



# Json class
Json class for Arucas

This class allows you to create and manipulate JSON objects.
This class cannot be instantiated or extended
Import with `import Json from util.Json;`

## Methods

### `<Json>.getValue()`
- Description: This converts the Json back into an object
- Returns - Object: the Value parsed from the Json
- Example:
```kotlin
json.getValue();
```

### `<Json>.writeToFile(file)`
- Description: This writes the Json to a file
if the file given is a directory or cannot be
written to, an error will be thrown
- Parameter - File (`file`): the file that you want to write to
- Example:
```kotlin
json.writeToFile(new File('D:/cool/realDirectory'));
```

## Static Methods

### `Json.fromFile(file)`
- Description: This will read a file and parse it into a Json
- Parameter - File (`file`): the file that you want to parse into a Json
- Returns - Json: the Json parsed from the file
- Example:
```kotlin
Json.fromFile(new File('this/path/is/an/example.json'));
```

### `Json.fromList(list)`
- Description: This converts a list into a Json, an important thing to note is that
any values that are not Numbers, Booleans, Lists, Maps, or Null will use their
toString() member to convert them to a string
- Parameter - List (`list`): the list that you want to parse into a Json
- Returns - Json: the Json parsed from the list
- Example:
```kotlin
Json.fromList(['value', 1, true]);
```

### `Json.fromMap(map)`
- Description: This converts a map into a Json, an important thing to note is that
any values that are not Numbers, Booleans, Lists, Maps, or Null will use their
toString() member to convert them to a string
- Parameter - Map (`map`): the map that you want to parse into a Json
- Returns - Json: the Json parsed from the map
- Example:
```kotlin
Json.fromMap({'key': ['value1', 'value2']});
```

### `Json.fromString(string)`
- Description: This converts a string into a Json provided it is formatted correctly,
otherwise throwing an error
- Parameter - String (`string`): the string that you want to parse into a Json
- Returns - Json: the Json parsed from the string
- Example:
```kotlin
Json.fromString('{"key":"value"}');
```



# KeyBind class
KeyBind class for Arucas

This class allows you to create key binds that can be used, everything is
handled for you internally so you just need to regers the key bind and
the function you want to run when it is pressed.
Class does not need to be imported

## Constructors

### `new KeyBind(keyName)`
- Description: Creates a new key bind
- Parameter - String (`keyName`): the name of the key
- Example:
```kotlin
new KeyBind('MyKey');
```

## Methods

### `<KeyBind>.getKey()`
- Description: Gets the key bind's first key
- Returns - String: the key bind's key
- Example:
```kotlin
keyBind.getKey();
```

### `<KeyBind>.getKeys()`
- Description: Gets the all of the keys in the key bind
- Returns - List: list of strings of all the keys
- Example:
```kotlin
keybind.getKeys();
```

### `<KeyBind>.setCallback(callback)`
- Description: Sets the callback function for the key bind
- Parameter - Function (`callback`): the callback function
- Example:
```kotlin
keyBind.setCallback(fun() { print('My key was pressed'); });
```

### `<KeyBind>.setKey(keyName)`
- Description: Sets the key bind to a new key
- Parameter - String (`keyName`): the name of the key
- Example:
```kotlin
keyBind.setKey('f');
```

### `<KeyBind>.setKeys(keyNames...)`
- Description: Sets the key bind to new keys, you may also pass
in a list as the parameter, this is to keep compatability
- Parameter - String (`keyNames...`): the names of keys
- Example:
```kotlin
keyBind.setKeys('control', 'f');
```



# LineShape class
LineShape class for Arucas

This class allows you to create a line shape which can be used to draw lines in the world.
Import with `import LineShape from Minecraft;`

## Constructors

### `new LineShape(pos1, pos2)`
- Description: Creates a new line shape
- Parameters:
  - Pos (`pos1`): The starting position of the line
  - Pos (`pos2`): The ending position of the line
- Example:
```kotlin
new LineShape(new Pos(0, 0, 0), new Pos(1, 1, 1));
```

### `new LineShape(x1, y1, z1, x2, y2, z2)`
- Description: Creates a new line shape
- Parameters:
  - Number (`x1`): The x position of the starting position of the line
  - Number (`y1`): The y position of the starting position of the line
  - Number (`z1`): The z position of the starting position of the line
  - Number (`x2`): The x position of the ending position of the line
  - Number (`y2`): The y position of the ending position of the line
  - Number (`z2`): The z position of the ending position of the line
- Example:
```kotlin
new LineShape(0, 0, 0, 1, 1, 1);
```



# List class
List class for Arucas

This class is used for collections of ordered elements
Class does not need to be imported

## Constructors

### `new List()`
- Description: This creates a list, this cannot be called directly, only from child classes
- Example:
```kotlin
class ChildList: List {
    ChildList(): super();
}
```

## Methods

### `<List>.addAll(collection)`
- Description: This allows you to add all the values in another collection to the list
- Parameter - Collection (`collection`): the collection you want to add to the list
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].addAll(['foo', 'object']); // ['object', 81, 96, 'case', 'foo', 'object']
```

### `<List>.append(value)`
- Description: This allows you to append a value to the end of the list
- Parameter - Object (`value`): the value you want to append
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].append('foo'); // ['object', 81, 96, 'case', 'foo']
```

### `<List>.clear()`
- Description: This allows you to clear the list
- Example:
```kotlin
['object', 81, 96, 'case'].clear(); // []
```

### `<List>.contains(value)`
- Description: This allows you to check if the list contains a specific value
- Parameter - Object (`value`): the value you want to check
- Returns - Boolean: true if the list contains the value
- Example:
```kotlin
['object', 81, 96, 'case'].contains('case'); // true
```

### `<List>.containsAll(collection)`
- Description: This allows you to check if the list contains all the values in another collection
- Parameter - Collection (`collection`): the collection you want to check agains
- Returns - Boolean: true if the list contains all the values in the collection
- Example:
```kotlin
['object', 81, 96, 'case'].containsAll(['foo', 'object']); // false
```

### `<List>.filter(predicate)`
- Description: This filters the list using the predicate, a function that either returns
true or false, based on the element on whether it should be kept or not,
and returns a new list with the filtered elements
- Parameter - Function (`predicate`): a function that takes a value and returns Boolean
- Returns - List: the filtered collection
- Example:
```kotlin
(list = [1, 2, 3]).filter(fun(v) {
    return v > 1;
});
// list = [2, 3]
```

### `<List>.flatten()`
- Description: If there are any objects in the list that are collections they will
be expanded and added to the list. However collections inside those
collections will not be flattened, this is returned as a new list
- Returns - List: the flattened list
- Example:
```kotlin
(list = [1, 2, 3, [4, 5], [6, [7]]]).flatten();
// list = [1, 2, 3, 4, 5, 6, [7]]
```

### `<List>.get(index)`
- Description: This allows you to get the value at a specific index, alternative to bracket accessor,
this will throw an error if the index given is out of bounds
- Parameter - Number (`index`): the index of the value you want to get
- Returns - Object: the value at the index
- Example:
```kotlin
['object', 81, 96, 'case'].get(1); // 81
```

### `<List>.indexOf(value)`
- Description: This allows you to get the index of a specific value
- Parameter - Object (`value`): the value you want to get the index of
- Returns - Number: the index of the value
- Example:
```kotlin
['object', 81, 96, 'case', 81].indexOf(81); // 1
```

### `<List>.insert(value, index)`
- Description: This allows you to insert a value at a specific index, this will throw an error if the index is out of bounds
- Parameters:
  - Object (`value`): the value you want to insert
  - Number (`index`): the index you want to insert the value at
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].insert('foo', 1); // ['object', 'foo', 81, 96, 'case']
```

### `<List>.lastIndexOf(value)`
- Description: This allows you to get the last index of a specific value
- Parameter - Object (`value`): the value you want to get the last index of
- Returns - Number: the last index of the value
- Example:
```kotlin
['object', 81, 96, 'case', 96].lastIndexOf(96); // 4
```

### `<List>.map(mapper)`
- Description: This maps the list using the mapper, a function that takes a value and
returns a new value, and returns a new list with the mapped elements
- Parameter - Function (`mapper`): a function that takes a value and returns a new value
- Returns - List: the mapped collection
- Example:
```kotlin
(list = [1, 2, 3]).map(fun(v) {
    return v * 2;
});
// list = [2, 4, 6]
```

### `<List>.prepend(value)`
- Description: This allows you to prepend a value to the beginning of the list
- Parameter - Object (`value`): the value you want to prepend
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96].prepend('foo'); // ['foo', 'object', 81, 96]
```

### `<List>.reduce(reducer)`
- Description: This reduces the list using the reducer, a function that takes an
accumulated value and a new value and returns the next accumulated value
- Parameter - Function (`reducer`): a function that takes a value and returns a new value
- Returns - Object: the reduced value
- Example:
```kotlin
// a will start at 1 and b at 2
// next accumulator will be 3
// a will be 3 and b will be 3 = 6
(list = [1, 2, 3]).reduce(fun(a, b) {
    return a + b;
});
// 6
```

### `<List>.remove(index)`
- Description: This allows you to remove the value at a specific index, alternative to bracket assignment.
This will throw an error if the index is out of bounds
- Parameter - Number (`index`): the index of the value you want to remove
- Returns - Object: the value that was removed
- Example:
```kotlin
['object', 81, 96, 'case'].remove(1); // 81
```

### `<List>.removeAll(collection)`
- Description: This allows you to remove all the values in another collection from the list
- Parameter - Collection (`collection`): the collection you want to remove from the list
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].removeAll(['foo', 'object']); // [81, 96, 'case']
```

### `<List>.retainAll(list)`
- Description: This allows you to retain only the values that are in both lists
- Parameter - List (`list`): the list you want to retain values from
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].retainAll(['case', 'object', 54]); // ['object', 'case']
```

### `<List>.reverse()`
- Description: This allows you to reverse the list
- Returns - List: the reversed list
- Example:
```kotlin
['a', 'b', 'c', 'd'].reverse(); // ['d', 'c', 'b', 'a']
```

### `<List>.set(value, index)`
- Description: This allows you to set the value at a specific index, alternative to bracket assignment,
this will throw an erroor if the index given is out of bounds
- Parameters:
  - Object (`value`): the value you want to set
  - Number (`index`): the index you want to set the value at
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].set('foo', 1); // ['object', 'foo', 96, 'case']
```

### `<List>.shuffle()`
- Description: This allows you to shuffle the list
- Returns - List: the shuffled list
- Example:
```kotlin
['a', 'b', 'c', 'd'].shuffle(); // some random order ¯\_(ツ)_/¯
```

### `<List>.sort()`
- Description: This allows you to sort the list using the elements compare method
- Returns - List: the sorted list
- Example:
```kotlin
['d', 'a', 'c', 'b'].sort(); // ['a', 'b', 'c', 'd']
```

### `<List>.sort(comparator)`
- Description: This allows you to sort the list using a comparator function
- Parameter - Function (`comparator`): the comparator function
- Returns - List: the sorted list
- Example:
```kotlin
[6, 5, 9, -10].sort(fun(a, b) { return a - b; }); // [-10, 5, 6, 9]
```



# LivingEntity class
LivingEntity class for Arucas

This class extends Entity and so inherits all of their methods too,
LivingEntities are any entities that are alive, so all mobs
Import with `import LivingEntity from Minecraft;`

## Methods

### `<LivingEntity>.getHealth()`
- Description: This gets the LivingEntity's current health
- Returns - Number: the LivingEntity's health
- Example:
```kotlin
livingEntity.getHealth();
```

### `<LivingEntity>.getStatusEffects()`
- Description: This gets the LivingEntity's status effects, you can find
a list of all the ids of the status effects
[here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Effects)
- Returns - List: a list of status effects, may be empty
- Example:
```kotlin
livingEntity.getStatusEffects();
```

### `<LivingEntity>.isFlyFalling()`
- Description: This checks if the LivingEntity is fly falling (gliding with elytra)
- Returns - Boolean: true if the LivingEntity is fly falling
- Example:
```kotlin
livingEntity.isFlyFalling();
```



# Map class
Map class for Arucas

This class is used to create a map of objects, using keys and values.
This class cannot be directly instantiated, but can be extended to create a map of your own type.
Class does not need to be imported

## Constructors

### `new Map()`
- Description: This creates an empty map, this cannot be called directly, only from child classes
- Example:
```kotlin
class ChildMap: Map {
    ChildMap(): super();
}
```

## Methods

### `<Map>.clear()`
- Description: This allows you to clear the map of all the keys and values
- Example:
```kotlin
(map = {'key': 'value'}).clear(); // map = {}
```

### `<Map>.containsKey(key)`
- Description: This allows you to check if the map contains a specific key
- Parameter - Object (`key`): the key you want to check
- Returns - Boolean: true if the map contains the key, false otherwise
- Example:
```kotlin
{'key': 'value'}.containsKey('key'); // true
```

### `<Map>.containsValue(value)`
- Description: This allows you to check if the map contains a specific value
- Parameter - Object (`value`): the value you want to check
- Returns - Boolean: true if the map contains the value, false otherwise
- Example:
```kotlin
{'key': 'value'}.containsValue('foo'); // false
```

### `<Map>.get(key)`
- Description: This allows you to get the value of a key in the map
- Parameter - Object (`key`): the key you want to get the value of
- Returns - Object: the value of the key, will return null if non-existent
- Example:
```kotlin
{'key': 'value'}.get('key'); // 'value'
```

### `<Map>.getKeys()`
- Description: This allows you to get the keys in the map
- Returns - List: a complete list of all the keys
- Example:
```kotlin
{'key': 'value', 'key2': 'value2'}.getKeys(); // ['key', 'key2']
```

### `<Map>.getValues()`
- Description: This allows you to get the values in the map
- Returns - List: a complete list of all the values
- Example:
```kotlin
{'key': 'value', 'key2': 'value2'}.getValues(); // ['value', 'value2']
```

### `<Map>.map(remapper)`
- Description: This allows you to map the values in the map and returns a new map
- Parameter - Function (`remapper`): the function you want to map the values with
- Returns - Map: a new map with the mapped values
- Example:
```kotlin
map = {'key': 'value', 'key2': 'value2'}
map.map(fun(k, v) {
    return [v, k];
});
// map = {'value': 'key', 'value2': 'key2'}
```

### `<Map>.put(key, value)`
- Description: This allows you to put a key and value in the map
- Parameters:
  - Object (`key`): the key you want to put
  - Object (`value`): the value you want to put
- Returns - Object: the previous value associated with the key, null if none
- Example:
```kotlin
{'key': 'value'}.put('key2', 'value2'); // null
```

### `<Map>.putAll(another map)`
- Description: This allows you to put all the keys and values of another map into this map
- Parameter - Map (`another map`): the map you want to merge into this map
- Example:
```kotlin
(map = {'key': 'value'}).putAll({'key2': 'value2'}); // map = {'key': 'value', 'key2': 'value2'}
```

### `<Map>.putIfAbsent(key, value)`
- Description: This allows you to put a key and value in the map if it doesn't exist
- Parameters:
  - Object (`key`): the key you want to put
  - Object (`value`): the value you want to put
- Example:
```kotlin
(map = {'key': 'value'}).putIfAbsent('key2', 'value2'); // map = {'key': 'value', 'key2': 'value2'}
```

### `<Map>.remove(key)`
- Description: This allows you to remove a key and its value from the map
- Parameter - Object (`key`): the key you want to remove
- Returns - Object: the value associated with the key, null if none
- Example:
```kotlin
{'key': 'value'}.remove('key'); // 'value'
```

## Static Methods

### `Map.unordered()`
- Description: This function allows you to create an unordered map
- Returns - Map: an unordered map
- Example:
```kotlin
Map.unordered();
```



# Material class
Material class for Arucas

This class represents all possible item and block types
and allows you to convert them into instances of ItemStacks and Blocks
Import with `import Material from Minecraft;`

## Methods

### `<Material>.asBlock()`
- Description: This converts the material into a Block.
If it cannot be converted an error will be thrown
- Returns - Block: the Block representation of the material
- Example:
```kotlin
material.asBlock();
```

### `<Material>.asItemStack()`
- Description: This converts the material into an ItemStack.
If it cannot be converted an error will be thrown
- Returns - ItemStack: the ItemStack representation of the material
- Example:
```kotlin
material.asItemStack();
```

### `<Material>.getFullId()`
- Description: This returns the full id of the material, for example: 'minecraft:diamond'
- Returns - String: the full id representation of the material
- Example:
```kotlin
material.getFullId();
```

### `<Material>.getId()`
- Description: This returns the id of the material, for example: 'diamond'
- Returns - String: the id representation of the material
- Example:
```kotlin
material.getId();
```

### `<Material>.getTranslatedName()`
- Description: This gets the translated name of the ItemStack, for example: 
Material.DIAMOND_SWORD would return 'Diamond Sword' if your language is English
- Returns - String: the translated name of the Material
- Example:
```kotlin
material.getTranslatedName();
```

## Static Methods

### `Material.of(id)`
- Description: This converts a block or item id into a Material.
This method will throw an error if the id is invalid
- Parameter - String (`id`): the id of the block or item
- Returns - Material: the material instance from the id
- Example:
```kotlin
Material.of('diamond');
```



# Math class
Math class for Arucas

Provides many basic math functions. This is a utility class, and cannot be constructed
Class does not need to be imported

## Static Fields

### `Math.e`
- Description: The value of e
- Type: Number
- Assignable: false
- Example:
```kotlin
Math.e;
```
### `Math.pi`
- Description: The value of pi
- Type: Number
- Assignable: false
- Example:
```kotlin
Math.pi;
```
### `Math.root2`
- Description: The value of root 2
- Type: Number
- Assignable: false
- Example:
```kotlin
Math.root2;
```

## Static Methods

### `Math.abs(num)`
- Description: Returns the absolute value of a number
- Parameter - Number (`num`): the number to get the absolute value of
- Returns - Number: the absolute value of the number
- Example:
```kotlin
Math.abs(-3);
```

### `Math.arccos(num)`
- Description: Returns the arc cosine of a number
- Parameter - Number (`num`): the number to get the arc cosine of
- Returns - Number: the arc cosine of the number
- Example:
```kotlin
Math.arccos(Math.cos(Math.pi));
```

### `Math.arcsin(num)`
- Description: Returns the arc sine of a number
- Parameter - Number (`num`): the number to get the arc sine of
- Returns - Number: the arc sine of the number
- Example:
```kotlin
Math.arcsin(Math.sin(Math.pi));
```

### `Math.arctan(num)`
- Description: Returns the arc tangent of a number
- Parameter - Number (`num`): the number to get the arc tangent of
- Returns - Number: the arc tangent of the number
- Example:
```kotlin
Math.arctan(Math.tan(Math.pi));
```

### `Math.arctan2(y, x)`
- Description: Returns the angle theta of the polar coordinates (r, theta) that correspond to the rectangular
coordinates (x, y) by computing the arc tangent of the value y / x
- Parameters:
  - Number (`y`): the ordinate coordinate
  - Number (`x`): the abscissa coordinate
- Returns - Number: the theta component of the point (r, theta)
- Example:
```kotlin
Math.arctan2(Math.tan(Math.pi), Math.cos(Math.pi)); // -3.141592
```

### `Math.ceil(num)`
- Description: Rounds a number up to the nearest integer
- Parameter - Number (`num`): the number to round
- Returns - Number: the rounded number
- Example:
```kotlin
Math.ceil(3.5);
```

### `Math.clamp(value, min, max)`
- Description: Clamps a value between a minimum and maximum
- Parameters:
  - Number (`value`): the value to clamp
  - Number (`min`): the minimum
  - Number (`max`): the maximum
- Returns - Number: the clamped value
- Example:
```kotlin
Math.clamp(10, 2, 8);
```

### `Math.cos(num)`
- Description: Returns the cosine of a number
- Parameter - Number (`num`): the number to get the cosine of
- Returns - Number: the cosine of the number
- Example:
```kotlin
Math.cos(Math.pi);
```

### `Math.cosec(num)`
- Description: Returns the cosecant of a number
- Parameter - Number (`num`): the number to get the cosecant of
- Returns - Number: the cosecant of the number
- Example:
```kotlin
Math.cosec(Math.pi);
```

### `Math.cosh(num)`
- Description: Returns the hyperbolic cosine of a number
- Parameter - Number (`num`): the number to get the hyperbolic cosine of
- Returns - Number: the hyperbolic cosine of the number
- Example:
```kotlin
Math.cosh(1);
```

### `Math.cot(num)`
- Description: Returns the cotangent of a number
- Parameter - Number (`num`): the number to get the cotangent of
- Returns - Number: the cotangent of the number
- Example:
```kotlin
Math.cot(Math.pi);
```

### `Math.floor(num)`
- Description: Rounds a number down to the nearest integer
- Parameter - Number (`num`): the number to round
- Returns - Number: the rounded number
- Example:
```kotlin
Math.floor(3.5);
```

### `Math.lerp(start, end, delta)`
- Description: Linear interpolation between two numbers
- Parameters:
  - Number (`start`): the first number
  - Number (`end`): the second number
  - Number (`delta`): the interpolation factor
- Returns - Number: the interpolated number
- Example:
```kotlin
Math.lerp(0, 10, 0.5);
```

### `Math.ln(num)`
- Description: Returns the natural logarithm of a number
- Parameter - Number (`num`): the number to get the logarithm of
- Returns - Number: the natural logarithm of the number
- Example:
```kotlin
Math.ln(Math.e);
```

### `Math.log(base, num)`
- Description: Returns the logarithm of a number with a specified base
- Parameters:
  - Number (`base`): the base
  - Number (`num`): the number to get the logarithm of
- Returns - Number: the logarithm of the number
- Example:
```kotlin
Math.log(2, 4);
```

### `Math.log10(num)`
- Description: Returns the base 10 logarithm of a number
- Parameter - Number (`num`): the number to get the logarithm of
- Returns - Number: the base 10 logarithm of the number
- Example:
```kotlin
Math.log10(100);
```

### `Math.max(num1, num2)`
- Description: Returns the largest number
- Parameters:
  - Number (`num1`): the first number to compare
  - Number (`num2`): the second number to compare
- Returns - Number: the largest number
- Example:
```kotlin
Math.max(5, 2);
```

### `Math.min(num1, num2)`
- Description: Returns the smallest number
- Parameters:
  - Number (`num1`): the first number to compare
  - Number (`num2`): the second number to compare
- Returns - Number: the smallest number
- Example:
```kotlin
Math.min(5, 2);
```

### `Math.mod(num1, num2)`
- Description: Returns the modulus of a division
- Parameters:
  - Number (`num1`): the number to divide
  - Number (`num2`): the divisor
- Returns - Number: the modulus of the division
- Example:
```kotlin
Math.mod(5, 2);
```

### `Math.rem(num1, num2)`
- Description: Returns the remainder of a division
- Parameters:
  - Number (`num1`): the number to divide
  - Number (`num2`): the divisor
- Returns - Number: the remainder of the division
- Example:
```kotlin
Math.rem(5, 2);
```

### `Math.round(num)`
- Description: Rounds a number to the nearest integer
- Parameter - Number (`num`): the number to round
- Returns - Number: the rounded number
- Example:
```kotlin
Math.round(3.5);
```

### `Math.sec(num)`
- Description: Returns the secant of a number
- Parameter - Number (`num`): the number to get the secant of
- Returns - Number: the secant of the number
- Example:
```kotlin
Math.sec(Math.pi);
```

### `Math.signum(num)`
- Description: Returns the sign of a number, 1 if the number is positive,
-1 if the number is negative, and 0 if the number is 0
- Parameter - Number (`num`): the number to get the sign of
- Returns - Number: the sign of the number
- Example:
```kotlin
Math.signum(3);
```

### `Math.sin(num)`
- Description: Returns the sine of a number
- Parameter - Number (`num`): the number to get the sine of
- Returns - Number: the sine of the number
- Example:
```kotlin
Math.sin(Math.pi);
```

### `Math.sinh(num)`
- Description: Returns the hyperbolic sine of a number
- Parameter - Number (`num`): the number to get the hyperbolic sine of
- Returns - Number: the hyperbolic sine of the number
- Example:
```kotlin
Math.sinh(1);
```

### `Math.sqrt(num)`
- Description: Returns the square root of a number
- Parameter - Number (`num`): the number to square root
- Returns - Number: the square root of the number
- Example:
```kotlin
Math.sqrt(9);
```

### `Math.tan(num)`
- Description: Returns the tangent of a number
- Parameter - Number (`num`): the number to get the tangent of
- Returns - Number: the tangent of the number
- Example:
```kotlin
Math.tan(Math.pi);
```

### `Math.tanh(num)`
- Description: Returns the hyperbolic tangent of a number
- Parameter - Number (`num`): the number to get the hyperbolic tangent of
- Returns - Number: the hyperbolic tangent of the number
- Example:
```kotlin
Math.tanh(1);
```

### `Math.toDegrees(num)`
- Description: Converts a number from radians to degrees
- Parameter - Number (`num`): the number to convert
- Returns - Number: the number in degrees
- Example:
```kotlin
Math.toDegrees(Math.pi);
```

### `Math.toRadians(num)`
- Description: Converts a number from degrees to radians
- Parameter - Number (`num`): the number to convert
- Returns - Number: the number in radians
- Example:
```kotlin
Math.toRadians(90);
```



# MerchantScreen class
MerchantScreen class for Arucas

This class extends Screen and so inherits all of their methods too,
this class is used to add functionality to trading screens.
Import with `import MerchantScreen from Minecraft;`

## Methods

### `<MerchantScreen>.clearTrade()`
- Description: This clears the currently selected trade.
You must be inside the merchant GUI or an error will be thrown
- Example:
```kotlin
screen.clearTrade();
```

### `<MerchantScreen>.doesVillagerHaveTrade(materialLike)`
- Description: This checks if the villager has a trade for a certain item.
You must be inside the merchant GUI or an error will be thrown
- Parameter - Material (`materialLike`): the item or material to check for
- Returns - Boolean: true if the villager has a trade for the item, false otherwise
- Example:
```kotlin
screen.doesVillagerHaveTrade(Material.DIAMOND_PICKAXE);
```

### `<MerchantScreen>.getIndexOfTradeItem(material)`
- Description: This gets the index of a trade for a certain item.
You must be inside the merchant GUI or an error will be thrown
- Parameter - Material (`material`): the item to get the index of
- Returns - Number: the index of the trade
- Example:
```kotlin
screen.getIndexOfTradeItem(Material.DIAMOND_PICKAXE);
```

### `<MerchantScreen>.getPriceForIndex(index)`
- Description: This gets the price of a trade at a certain index.
You must be inside the merchant GUI or an error will be thrown
- Parameter - Number (`index`): the index of the trade
- Returns - Number: the price of the trade
- Example:
```kotlin
screen.getPriceForIndex(0);
```

### `<MerchantScreen>.getTradeItemForIndex(index)`
- Description: This gets the item stack of a trade at a certain index.
You must be inside the merchant GUI or an error will be thrown
- Parameter - Number (`index`): the index of the trade
- Returns - ItemStack: the item stack of the trade
- Example:
```kotlin
screen.getTradeItemForIndex(0);
```

### `<MerchantScreen>.getTradeList()`
- Description: This gets a list of all the merchant's trades
- Returns - List: the list of all the Trades
- Example:
```kotlin
screen.getTradeList();
```

### `<MerchantScreen>.getTradeListSize()`
- Description: This gets the size of all the trades available
- Returns - Number: the size of the trade list
- Example:
```kotlin
screen.getTradeListSize();
```

### `<MerchantScreen>.getVillagerLevel()`
- Description: This gets the level of the villager, this will
throw an error if you are not trading with a villager.
The level can be between 1 - 5 from Novice to Master
- Returns - Number: the level of the villager
- Example:
```kotlin
screen.getVillagerLevel();
```

### `<MerchantScreen>.getVillagerXp()`
- Description: This gets the amount of xp in the villagers xp bar,
The total amount of xp is hardcoded for each level.
Level 2 requires 10 xp, 3 requires 70 (60 xp from 2 -> 3),
4 requires 150 (80 xp from 3 -> 4), 5 requires 250
(100 xp from 4 -> 5). 250 is the max xp a villager can have
- Returns - Number: the amount of xp
- Example:
```kotlin
screen.getVillagerXpBar
```

### `<MerchantScreen>.isTradeDisabled(index)`
- Description: This returns true if a trade is disabled at an index
- Parameter - Number (`index`): the index of the trade
- Returns - Boolean: true if a trade is disabled
- Example:
```kotlin
screen.isTradeDisabled(1);
```

### `<MerchantScreen>.isTradeSelected()`
- Description: This returns true if a trade is selected
- Returns - Boolean: true if a trade is selected
- Example:
```kotlin
screen.isTradeSelected();
```

### `<MerchantScreen>.selectTrade(index)`
- Description: This selects the currently selected trade, as if you were to click it.
You must be inside the merchant GUI or an error will be thrown
- Parameter - Number (`index`): the index of the trade
- Example:
```kotlin
screen.selectTrade(0);
```

### `<MerchantScreen>.tradeIndex(index)`
- Description: This makes your player trade with the merchant at a certain index.
You must be inside the merchant GUI or an error will be thrown
- Parameter - Number (`index`): the index of the trade
- Example:
```kotlin
screen.tradeIndex(0);
```

### `<MerchantScreen>.tradeSelected()`
- Description: This trades the currently selected trade.
You must be inside the merchant GUI or an error will be thrown
- Example:
```kotlin
screen.tradeSelected();
```

### `<MerchantScreen>.tradeSelectedAndThrow()`
- Description: This trades the currently selected trade and throws the items that were traded.
You must be inside the merchant GUI or an error will be thrown
- Example:
```kotlin
screen.tradeSelectedAndThrow();
```



# MinecraftClient class
MinecraftClient class for Arucas

This allows for many core interactions with the MinecraftClient
Import with `import MinecraftClient from Minecraft;`

## Methods

### `<MinecraftClient>.addCommand(command)`
- Description: This allows you to register your own client side command in game
- Parameter - Map (`command`): a command map or a command builder
- Example:
```kotlin
client.addCommand({
    "name": "example",
    "subcommands": { },
    "arguments": { }
});
```

### `<MinecraftClient>.canSendScriptPacket()`
- Description: Returns whether the server supports client script packets
- Returns - Boolean: Whether the client can send packets to the server
- Example:
```kotlin
client.canSendScriptPacket()
```

### `<MinecraftClient>.clearChat()`
- Description: This will clear the chat hud
- Example:
```kotlin
client.clearChat();
```

### `<MinecraftClient>.editSign(position, string...)`
- Description: This allows you to edit sign at certain position with given string(lines), up to 4 lines.
This function does not check if sign is editable / is in position.
- Parameters:
  - Pos (`position`): the position of sign
  - String (`string...`): the lines for the sign, requires 1 string and up to 4 strings
- Example:
```kotlin
client.editSign(new Pos(0,0,0), '100', '101', 'this is third line', 'last line');
```

### `<MinecraftClient>.getClientRenderDistance()`
- Description: This returns the current render distance set on the client
- Returns - Number: the render distance
- Example:
```kotlin
client.getClientRenderDistance();
```

### `<MinecraftClient>.getCursorStack()`
- Description: This returns the item stack that is currently being held by the cursor
- Returns - ItemStack: the item stack, will be Air if there is nothing
- Example:
```kotlin
client.getCursorStack();
```

### `<MinecraftClient>.getEssentialClientValue(ruleName)`
- Description: This gets the value of the given client rule.
This will throw an error if the rule name is invalid
- Parameter - String (`ruleName`): the client rule
- Returns - Object: the value of the client rule
- Example:
```kotlin
client.getEssentialClientValue('overrideCreativeWalkSpeed');
```

### `<MinecraftClient>.getFps()`
- Description: This gets the current fps
- Returns - Number: the fps
- Example:
```kotlin
client.getFps();
```

### `<MinecraftClient>.getLatestChatMessage()`
- Description: This will return the latest chat message
- Returns - Text: the latest chat message, null if there is none
- Example:
```kotlin
client.getLatestChatMessage();
```

### `<MinecraftClient>.getModList()`
- Description: This gets a list of all the mod ids of the mods installed
- Returns - List: the mod ids
- Example:
```kotlin
client.getModList();
```

### `<MinecraftClient>.getPing()`
- Description: This gets the current connected server's ping.
This will throw an error if the client is not connected to a server
- Returns - Number: the server ping in milliseconds
- Example:
```kotlin
client.getPing();
```

### `<MinecraftClient>.getPlayer()`
- Description: This returns the current player on the client
- Returns - Player: the main player
- Example:
```kotlin
client.getPlayer();
```

### `<MinecraftClient>.getRunDirectory()`
- Description: Returns the directory where the client is running
- Returns - File: the Minecraft run directory
- Example:
```kotlin
client.getRunDirectory();
```

### `<MinecraftClient>.getScriptsPath()`
- Description: This gets the script directory path, this is where all scripts are stored
- Returns - String: the script directory path
- Example:
```kotlin
client.getScriptPath();
```

### `<MinecraftClient>.getServerIp()`
- Description: This will return the server ip
- Returns - String: The server ip, null if in single player
- Example:
```kotlin
client.getServerIp();
```

### `<MinecraftClient>.getServerName()`
- Description: This gets the current connected server's name that you have set it to in the multiplayer screen
- Returns - String: the server name
- Example:
```kotlin
client.getServerName();
```

### `<MinecraftClient>.getVersion()`
- Description: This returns the current version of Minecraft you are playing
- Returns - String: the version for example: '1.17.1'
- Example:
```kotlin
client.getVersion();
```

### `<MinecraftClient>.getWorld()`
- Description: This returns the world that is currently being played on
- Returns - World: the world
- Example:
```kotlin
client.getWorld();
```

### `<MinecraftClient>.holdKey(key, milliseconds)`
- Description: This allows you to simulate a key being held inside of Minecraft, this will press, hold, and release.
This will throw an error if the given key is unknown
- Parameters:
  - String (`key`): the key to hold
  - Number (`milliseconds`): the number of milliseconds you want it held for
- Example:
```kotlin
client.holdKey('f', 100);
```

### `<MinecraftClient>.isInSinglePlayer()`
- Description: This will return true if the client is in single player mode
- Returns - Boolean: true if the client is in single player mode
- Example:
```kotlin
client.isInSinglePlayer();
```

### `<MinecraftClient>.parseStringToNbt(string)`
- Description: This parses a string and turns it into a Nbt compound
- Parameter - String (`string`): the string to parse
- Returns - Object: the nbt compound as a value
- Example:
```kotlin
client.parseStringToNbt('{"test":"test"}');
```

### `<MinecraftClient>.playSound(soundId, volume, pitch)`
- Description: This plays the given sound with the given volume and pitch around the player
sound id's can be found [here](https://minecraft.fandom.com/wiki/Sounds.json#Sound_events)
- Parameters:
  - String (`soundId`): the sound id you want to play
  - Number (`volume`): the volume of the sound
  - Number (`pitch`): the pitch of the sound
- Example:
```kotlin
client.playSound('entity.lightning_bolt.thunder', 1, 1);
```

### `<MinecraftClient>.playerNameFromUuid(uuid)`
- Description: This will return the player name from the given uuid
- Parameter - String (`uuid`): the uuid as a string
- Returns - String: the player name, null if the uuid is not found
- Example:
```kotlin
client.playerNameFromUuid('d4fca8c4-e083-4300-9a73-bf438847861c');
```

### `<MinecraftClient>.pressKey(key)`
- Description: This allows you to simulate a key press inside of Minecraft, this will only press the key down.
This will throw an error if the key is unknown
- Parameter - String (`key`): the key to press
- Example:
```kotlin
client.pressKey('f');
```

### `<MinecraftClient>.releaseKey(key)`
- Description: This allows you to simulate a key release inside of Minecraft, this
is useful for keys that only work on release, for example `F3`, this
will throw an error if the key is unknown
- Parameter - String (`key`): the key to release
- Example:
```kotlin
client.releaseKey('f');
```

### `<MinecraftClient>.renderFloatingItem(material)`
- Description: This renders an item in front of the player using the totem of undying animation
- Parameter - Material (`material`): the material to render
- Example:
```kotlin
client.renderFloatingItem(Material.DIAMOND);
```

### `<MinecraftClient>.resetEssentialClientRule(ruleName)`
- Description: This resets the given client rule to its default value.
This will throw an error if the rule name is invalid
- Parameter - String (`ruleName`): the client rule
- Example:
```kotlin
client.resetEssentialClientRule('highlightLavaSources');
```

### `<MinecraftClient>.run(function)`
- Description: This runs the given function on the main thread
- Parameter - Function (`function`): the function to run
- Example:
```kotlin
client.run(fun() { print('Do something'); });
```

### `<MinecraftClient>.runOnMainThread(function)`
- Deprecated: Use 'client.run(task)' instead
- Description: This runs the given function on the main thread
- Parameter - Function (`function`): the function to run
- Example:
```kotlin
client.runOnMainThread(fun() { print('Do something'); });
```

### `<MinecraftClient>.screenshot()`
- Description: This makes the client take a screenshot
- Example:
```kotlin
client.screenshot();
```

### `<MinecraftClient>.screenshot(name)`
- Description: This makes the client take a screenshot and saves it with a given name
- Parameter - String (`name`): the name of the file
- Example:
```kotlin
client.screenshot('screenshot.png');
```

### `<MinecraftClient>.sendScriptPacket(values...)`
- Description: This sends a script packet to the server
You can send the follow types of values:
Boolean, Number, String, List (of numbers), Text, ItemStack, Pos, and NbtMaps
You can send byte, int, and long arrays by using the strings 'b', 'i', and 'l' at the start of the list
- Parameter - Object (`values...`): the data you want to send to the server
- Example:
```kotlin
client.sendScriptPacket('test', false, ['l', 9999, 0, 45]);
```

### `<MinecraftClient>.setClientRenderDistance(number)`
- Description: This sets the render distance on the client
- Parameter - Number (`number`): the render distance
- Example:
```kotlin
client.setClientRenderDistance(10);
```

### `<MinecraftClient>.setCursorStack(itemStack)`
- Deprecated: You should use 'fakeInventoryScreen.setCursorStack(item)' instead
- Description: This sets the item stack that is currently being held by the cursor, this does not work
in normal screens only in FakeScreens, this does not actually pick up an item just display like you have
- Parameter - ItemStack (`itemStack`): the item stack to set
- Example:
```kotlin
client.setCursorStack(Material.DIAMOND.asItemStack());
```

### `<MinecraftClient>.setEssentialClientRule(ruleName, value)`
- Description: This sets the given client rule to the given value.
This may throw an error if the name is invalid or the rule cannot be set
- Parameters:
  - String (`ruleName`): the client rule
  - Object (`value`): the new value for the rule
- Example:
```kotlin
client.setEssentialClientRule('highlightLavaSources', false);
```

### `<MinecraftClient>.stripFormatting(string)`
- Description: This strips the formatting from the given string
- Parameter - String (`string`): the string to strip
- Returns - String: the stripped string
- Example:
```kotlin
client.stripFormatting('§cHello§r');
```

### `<MinecraftClient>.syncToTick()`
- Description: Synchronizes the current thread in Arucas to the next game tick
- Example:
```kotlin
client.syncToTick();
```

### `<MinecraftClient>.tick()`
- Description: This ticks the client
- Example:
```kotlin
client.tick();
```

### `<MinecraftClient>.uuidFromPlayerName(name)`
- Description: This will return the uuid from the given player name
- Parameter - String (`name`): the player name
- Returns - String: the uuid, null if the player name is not found
- Example:
```kotlin
client.uuidFromPlayerName('senseiwells');
```

## Static Methods

### `MinecraftClient.get()`
- Description: Returns the MinecraftClient instance
- Returns - MinecraftClient: the MinecraftClient instance
- Example:
```kotlin
MinecraftClient.get();
```

### `MinecraftClient.getClient()`
- Description: Returns the MinecraftClient instance
- Returns - MinecraftClient: the MinecraftClient instance
- Example:
```kotlin
MinecraftClient.getClient();
```



# MinecraftTask class
MinecraftTask class for Arucas

This class is used to create tasks that can be chained and
run on the main Minecraft thread. This ensures that all
behaviors work as intended.
Class does not need to be imported

## Constructors

### `new MinecraftTask()`
- Description: This creates a new empty Minecraft task
- Example:
```kotlin
task = new MinecraftTask();
```

## Methods

### `<MinecraftTask>.run()`
- Description: This runs the task on the main Minecraft thread. It returns a future
which can be awaited, the last function in the chain will be used as
the return value for the future
- Returns - Future: the future value that can be awaited
- Example:
```kotlin
task = new MinecraftTask()
    .then(fun() print("hello"))
    .then(fun() print(" "))
    .then(fun() print("world"))
    .then(fun() 10);
f = task.run(); // prints 'hello world'
print(f.await()); // prints 10
```

### `<MinecraftTask>.waitThen(ticks, function)`
- Description: This adds a delay (in ticks) then runs the given task.
This delay is will also affect all following chained function
delays. If this is the last function in the chain, then the
return value will be determined by this function
- Parameters:
  - Number (`ticks`): the amount of ticks delay before the function runs
  - Function (`function`): the function to run after the delay
- Returns - MinecraftTask: the task, this allows for chaining
- Example:
```kotlin
task = new MinecraftTask()
    .then(fun() print("hello"))
    .waitThen(5, fun() print("world"));
task.run(); // prints 'hello', waits 5 ticks, prints 'world'
```



# Network class
Network class for Arucas

Allows you to do http requests. This is a utility class and cannot be constructed.
Import with `import Network from util.Network;`

## Static Methods

### `Network.downloadFile(url, file)`
- Description: Downloads a file from an url to a file
- Parameters:
  - String (`url`): the url to download from
  - File (`file`): the file to download to
- Returns - Boolean: whether the download was successful
- Example:
```kotlin
Network.downloadFile('https://arucas.com', new File('dir/downloads'));
```

### `Network.openUrl(url)`
- Description: Opens an url in the default browser
- Parameter - String (`url`): the url to open
- Example:
```kotlin
Network.openUrl('https://google.com');
```

### `Network.requestUrl(url)`
- Description: Requests an url and returns the response
- Parameter - String (`url`): the url to request
- Returns - String: the response from the url
- Example:
```kotlin
Network.requestUrl('https://google.com');
```



# Null class
Null class for Arucas

This class is used for the null object,
this cannot be instantiated or extended
Class does not need to be imported



# Number class
Number class for Arucas

This class cannot be constructed as it has a literal representation.
For math related functions see the Math class.
Class does not need to be imported

## Methods

### `<Number>.ceil()`
- Description: This allows you to round a number up to the nearest integer
- Returns - Number: the rounded number
- Example:
```kotlin
3.5.ceil();
```

### `<Number>.floor()`
- Description: This allows you to round a number down to the nearest integer
- Returns - Number: the rounded number
- Example:
```kotlin
3.5.floor();
```

### `<Number>.isInfinite()`
- Description: This allows you to check if a number is infinite
- Returns - Boolean: true if the number is infinite
- Example:
```kotlin
(1/0).isInfinite();
```

### `<Number>.isNaN()`
- Description: This allows you to check if a number is not a number
- Returns - Boolean: true if the number is not a number
- Example:
```kotlin
(0/0).isNaN();
```

### `<Number>.round()`
- Description: This allows you to round a number to the nearest integer
- Returns - Number: the rounded number
- Example:
```kotlin
3.5.round();
```



# Object class
Object class for Arucas

This is the base class for every other class in Arucas.
This class cannot be instantiated from, you can extend it
however every class already extends this class by default
Class does not need to be imported

## Methods

### `<Object>.copy()`
- Description: This returns a copy of the value if implemented.
Some objects that are immutable, such as Strings and Numbers
will not be copied, and will return the same instance.
Any object that has not implemented the copy method will also
return the same instance
- Returns - Object: a copy of the value
- Example:
```kotlin
[10, 11, 12].copy(); // [10, 11, 12]
```

### `<Object>.hashCode()`
- Description: This returns the hash code of the value, mainly used for maps and sets
the hash code of an object must remain consistent for objects to be able
to be used as keys in a map or set. If two objects are equal, they must
have the same hash code
- Returns - Number: the hash code of the value
- Example:
```kotlin
[10, 11, 12].hashCode(); // -1859087
```

### `<Object>.instanceOf(type)`
- Description: This returns true if the value is an instance of the given type
- Parameter - Type (`type`): the type to check against
- Returns - Boolean: true if the value is an instance of the given type
- Example:
```kotlin
[10, 11, 12].instanceOf(List.type); // true
```

### `<Object>.toString()`
- Description: This returns the string representation of the value
- Returns - String: the string representation of the value
- Example:
```kotlin
[10, 11, 12].toString(); // [10, 11, 12]
```

### `<Object>.uniqueHash()`
- Description: This returns the unique hash of the value, this is different for every instance of a value
- Returns - Number: the unique hash of the value
- Example:
```kotlin
'thing'.uniqueHash();
```



# OtherPlayer class
OtherPlayer class for Arucas

This class is used to represent all players, mainly other players,
this class extends LivingEntity and so inherits all of their methods too
Import with `import OtherPlayer from Minecraft;`

## Methods

### `<OtherPlayer>.getAbilities()`
- Description: This gets the abilities of the player in a map
For example:
`{"invulnerable": false, "canFly": true, "canBreakBlocks": true, "isCreative": true, "walkSpeed": 1.0, "flySpeed": 1.2}`
- Returns - Map: the abilities of the player
- Example:
```kotlin
otherPlayer.getAbilities();
```

### `<OtherPlayer>.getAllSlotsFor(materialLike)`
- Description: This gets all the slot numbers of the specified item in the players combined inventory
- Parameter - Material (`materialLike`): the item or material you want to get the slot of
- Returns - List: the slot numbers of the item, empty list if not found
- Example:
```kotlin
otherPlayer.getAllSlotsFor(Material.DIAMOND);
```

### `<OtherPlayer>.getAllSlotsFor(materialLike, inventoryType)`
- Description: This gets all the slot numbers of the specified item in the players combined inventory
- Parameters:
  - Material (`materialLike`): the item or material you want to get the slot of
  - String (`inventoryType`): all/combined -> includes external, player/main -> player slots, external/other -> excludes player inventory
- Returns - List: the slot numbers of the item, empty list if not found
- Example:
```kotlin
otherPlayer.getAllSlotsFor(Material.DIAMOND, 'player');
```

### `<OtherPlayer>.getCurrentSlot()`
- Description: This gets the players currently selected slot
- Returns - Number: the currently selected slot number
- Example:
```kotlin
otherPlayer.getCurrentSlot();
```

### `<OtherPlayer>.getFishingBobber()`
- Description: This gets the fishing bobber that the player has
- Returns - Entity: the fishing bobber entity, null if the player isn't fishing
- Example:
```kotlin
otherPlayer.getFishingBobber();
```

### `<OtherPlayer>.getGamemode()`
- Description: This gets the players gamemode, may be null if not known
- Returns - String: the players gamemode as a string, for example 'creative', 'survival', 'spectator'
- Example:
```kotlin
otherPlayer.getGamemode();
```

### `<OtherPlayer>.getHeldItem()`
- Description: This gets the players currently selected item, in their main hand
- Returns - ItemStack: the currently selected item
- Example:
```kotlin
otherPlayer.getHeldItem();
```

### `<OtherPlayer>.getHunger()`
- Description: This gets the hunger level of the player
- Returns - Number: the hunger level
- Example:
```kotlin
otherPlayer.getHunger();
```

### `<OtherPlayer>.getItemForPlayerSlot(slotNum)`
- Description: This gets the item in the specified slot, in the players inventory, not including inventories of open containers.
This will throw an error if the slot is out of bounds
- Parameter - Number (`slotNum`): the slot number you want to get
- Returns - ItemStack: the item in the specified slot
- Example:
```kotlin
otherPlayer.getItemForPlayerSlot(0);
```

### `<OtherPlayer>.getItemForSlot(slotNum)`
- Description: This gets the item in the specified slot, in the total players inventory, including inventories of open containers.
This will throw an error if the index is out of bounds
- Parameter - Number (`slotNum`): the slot number you want to get
- Returns - ItemStack: the item in the specified slot
- Example:
```kotlin
otherPlayer.getItemForSlot(0);
```

### `<OtherPlayer>.getLevels()`
- Description: This gets the number of experience levels the player has
- Returns - Number: the number of experience levels
- Example:
```kotlin
otherPlayer.getLevels();
```

### `<OtherPlayer>.getNextLevelExperience()`
- Description: This gets the number of experience required to level up for the player
- Returns - Number: the number required to next level
- Example:
```kotlin
otherPlayer.getNextLevelExperience();
```

### `<OtherPlayer>.getPlayerName()`
- Description: This gets the players name
- Returns - String: the players name
- Example:
```kotlin
otherPlayer.getPlayerName();
```

### `<OtherPlayer>.getSaturation()`
- Description: This gets the saturation level of the player
- Returns - Number: the saturation level
- Example:
```kotlin
otherPlayer.getSaturation();
```

### `<OtherPlayer>.getSlotFor(materialLike)`
- Description: This gets the slot number of the specified item in the players combined inventory
- Parameter - Material (`materialLike`): the item or material you want to get the slot of
- Returns - Number: the slot number of the item, null if not found
- Example:
```kotlin
otherPlayer.getSlotFor(Material.DIAMOND.asItemStack());
```

### `<OtherPlayer>.getTotalSlots()`
- Description: This gets the players total inventory slots
- Returns - Number: the players total inventory slots
- Example:
```kotlin
otherPlayer.getTotalSlots();
```

### `<OtherPlayer>.getXpProgress()`
- Description: This gets the number of experience progress the player has
- Returns - Number: the number of experience progress
- Example:
```kotlin
otherPlayer.getXpProgress();
```

### `<OtherPlayer>.isInventoryFull()`
- Description: This gets whether the players inventory is full
- Returns - Boolean: whether the inventory is full
- Example:
```kotlin
otherPlayer.isInventoryFull();
```

### `<OtherPlayer>.isPlayerSlot(slotNum)`
- Description: This gets inventory type (player / other) for given slot numbers.
This will throw an error if the index is out of bounds
- Parameter - Number (`slotNum`): the slot number you want to get
- Returns - Boolean: whether slot was player inventory or not
- Example:
```kotlin
otherPlayer.isPlayerSlot(0);
```



# OutlinedShape class
OutlinedShape class for Arucas

This class represents all shapes that can be outlined
Class does not need to be imported

## Methods

### `<OutlinedShape>.getOutlineBlue()`
- Description: This gets the outline blue value of the shape
- Returns - Number: the blue value of the outline
- Example:
```kotlin
shape.getOutlineBlue();
```

### `<OutlinedShape>.getOutlineGreen()`
- Description: This gets the outline green value of the shape
- Returns - Number: the green value of the outline
- Example:
```kotlin
shape.getOutlineGreen();
```

### `<OutlinedShape>.getOutlineRed()`
- Description: This gets the outline red value of the shape
- Returns - Number: the red value of the outline
- Example:
```kotlin
shape.getOutlineRed();
```

### `<OutlinedShape>.getOutlineWidth()`
- Description: This gets the outline width of the shape
- Returns - Number: the width of the outline
- Example:
```kotlin
shape.getOutlineWidth();
```

### `<OutlinedShape>.setOutlineBlue(blue)`
- Description: This sets the outline blue value of the shape, using a single value
- Parameter - Number (`blue`): the amount of blue between 0 - 255
- Example:
```kotlin
shape.setOutlineBlue(34);
```

### `<OutlinedShape>.setOutlineColour(colour)`
- Description: This sets the width of the shape, using a single value, this function
also has a sibling named `setOutlineColor()` that has the same functionality.
The colour generally should be hexadecimal in the form 0xRRGGBB
- Parameter - Number (`colour`): the colour you want to set
- Example:
```kotlin
shape.setOutlineColour(0xFF00FF);
```

### `<OutlinedShape>.setOutlineColour(red, green, blue)`
- Description: This sets the outline colour of the shape, using three values, this function
also has a sibling named `setOutlineColor()` that has the same functionality.
If the colours are not between 0 and 255 an error will be thrown
- Parameters:
  - Number (`red`): the amount of red 0 - 255
  - Number (`green`): the amount of green 0 - 255
  - Number (`blue`): the amount of blue 0 - 255
- Example:
```kotlin
shape.setOutlineColour(255, 0, 255);
```

### `<OutlinedShape>.setOutlineGreen(green)`
- Description: This sets the outline green value of the shape, using a single value
- Parameter - Number (`green`): the amount of green between 0 - 255
- Example:
```kotlin
shape.setOutlineGreen(34);
```

### `<OutlinedShape>.setOutlineRed(red)`
- Description: This sets the outline red value of the shape, using a single value
- Parameter - Number (`red`): the amount of red between 0 - 255
- Example:
```kotlin
shape.setOutlineRed(34);
```

### `<OutlinedShape>.setOutlineWidth(width)`
- Description: This sets the outline width of the shape, this should not be negative
- Parameter - Number (`width`): the width of the outline
- Example:
```kotlin
shape.setOutlineWidth(2);
```



# Player class
Player class for Arucas

This class is used to interact with the main player, this extends OtherPlayer
and so inherits all methods from that class.
Import with `import Player from Minecraft;`

## Methods

### `<Player>.anvil(predicate1, predicate2)`
- Description: This allows you to combine two items in an anvil
- Parameters:
  - Function (`predicate1`): a function determining whether the first ItemStack meets a criteria
  - Function (`predicate2`): a function determining whether the second ItemStack meets a criteria
- Returns - Future: whether the anvilling was successful, if the player doesn't have enough levels it will return the xp cost
- Example:
```kotlin
// Enchant a pickaxe with mending
player.anvil(
    // Predicate for pick
    fun(item) {
        // We want a netherite pickaxe without mending
        if (item.getItemId() == "netherite_pickaxe") {
            hasMending = item.getEnchantments().getKeys().contains("mending");
            return !hasMending;
        }
        return false;
    },
    // Predicate for book
    fun(item) {
        // We want a book with mending
        if (item.getItemId() == "enchanted_book") {
            hasMending = item.getEnchantments().getKeys().contains("mending");
            return hasMending;
        }
        return false;
    }
);
```

### `<Player>.anvil(predicate1, predicate2, take)`
- Description: This allows you to combine two items in an anvil
- Parameters:
  - Function (`predicate1`): a function determining whether the first ItemStack meets a criteria
  - Function (`predicate2`): a function determining whether the second ItemStack meets a criteria
  - Boolean (`take`): whether you should take the item after putting items in the anvil
- Returns - Future: whether the anvilling was successful, if the player doesn't have enough levels it will return the xp cost
- Example:
```kotlin
// Enchant a pickaxe with mending
player.anvil(
    // Predicate for pick
    fun(item) {
        // We want a netherite pickaxe without mending
        if (item.getItemId() == "netherite_pickaxe") {
            hasMending = item.getEnchantments().getKeys().contains("mending");
            return !hasMending;
        }
        return false;
    },
    // Predicate for book
    fun(item) {
        // We want a book with mending
        if (item.getItemId() == "enchanted_book") {
            hasMending = item.getEnchantments().getKeys().contains("mending");
            return hasMending;
        }
        return false;
    },
    false
);
```

### `<Player>.anvilRename(name, predicate)`
- Description: This allows you to name an item in an anvil
- Parameters:
  - String (`name`): the name you want to give the item
  - Function (`predicate`): whether the ItemStack meets a certain criteria
- Returns - Future: whether the anvilling was successful, if the player doesn't have enough levels it will return the xp cost
- Example:
```kotlin
// Rename any shulker box
player.anvilRename("Rocket Box",
    fun(item) {
        isShulker = item.getItemId().containsString("shulker_box"));
        return isShulker;
    }
);
```

### `<Player>.attack(action)`
- Description: This allows you to make your player attack, you must
pass 'hold', 'stop', or 'once' otherwise an error will be thrown
- Parameter - String (`action`): the type of action, either 'hold', 'stop', or 'once'
- Example:
```kotlin
player.attack('once');
```

### `<Player>.attackBlock(pos, direction)`
- Description: This allows you to attack a block at a position and direction
- Parameters:
  - Pos (`pos`): the position of the block
  - String (`direction`): the direction of the attack, e.g. 'up', 'north', 'east', etc.
- Example:
```kotlin
player.attackBlock(new Pos(0, 0, 0), 'up');
```

### `<Player>.attackBlock(x, y, z, direction)`
- Description: This allows you to attack a block at a position and direction
- Parameters:
  - Number (`x`): the x position
  - Number (`y`): the y position
  - Number (`z`): the z position
  - String (`direction`): the direction of the attack, e.g. 'up', 'north', 'east', etc.
- Example:
```kotlin
player.attackBlock(0, 0, 0, 'up');
```

### `<Player>.attackEntity(entity)`
- Description: This makes your player attack an entity without
having to be looking at it or clicking on the entity
- Parameter - Entity (`entity`): the entity to attack
- Example:
```kotlin
allEntities = client.getWorld().getAllEntities();
foreach (entity : allEntities) {
    if (entity.getId() == "villager" && player.getSquaredDistanceTo(entity) < 5) {
        player.attackEntity(entity);
        break;
    }
}
```

### `<Player>.breakBlock(pos)`
- Description: This breaks a block at a given position, if it is able to be broken
- Parameter - Pos (`pos`): the position of the block
- Returns - Future: the future will be completed when the block is broken
- Example:
```kotlin
player.breakBlock(new Pos(0, 0, 0));
```

### `<Player>.canPlaceBlockAt(pos)`
- Description: Checks block can be placed at given position
- Parameter - Pos (`pos`): the position to check
- Example:
```kotlin
player.canPlaceBlockAt(block, pos);
```

### `<Player>.canPlaceBlockAt(block, x, y, z)`
- Description: Checks block can be placed at given position
- Parameters:
  - Block (`block`): the block to check for
  - Number (`x`): the x coordinate of the position
  - Number (`y`): the y coordinate of the position
  - Number (`z`): the z coordinate of the position
- Example:
```kotlin
player.canPlaceBlockAt(block, 0, 0, 0);
```

### `<Player>.clickCreativeStack(itemStack, slot)`
- Description: This allows you to click Creative stack, but requires sync with server
- Parameters:
  - ItemStack (`itemStack`): Stack to click
  - Number (`slot`): the slot to click
- Example:
```kotlin
player.clickCreativeStack(Material.DIAMOND_SWORD.asItemStack(), 9);
```

### `<Player>.clickRecipe(recipe)`
- Description: This allows you to click a predefined recipe
- Parameter - Recipe (`recipe`): the recipe you want to select
- Example:
```kotlin
player.clickRecipe(Recipe.CHEST);
```

### `<Player>.clickRecipe(recipe, boolean)`
- Description: This allows you to click a predefined recipe
- Parameters:
  - Recipe (`recipe`): the recipe you want to select
  - Boolean (`boolean`): whether to shift click the recipe
- Example:
```kotlin
player.clickRecipe(Recipe.CHEST, true);
```

### `<Player>.clickSlot(slot, click, action)`
- Description: This allows you to click a slot with either right or left click
and a slot action, the click must be either 'left' or 'right' or a number (for swap).
The action must be either 'click', 'shift_click', 'swap', 'middle_click',
'throw', 'drag', or 'double_click' or an error will be thrown
- Parameters:
  - Number (`slot`): the slot to click
  - String (`click`): the click type, this should be either 'left' or 'right'
  - String (`action`): the action to perform
- Example:
```kotlin
player.clickSlot(9, 'left', 'double_click');
```

### `<Player>.closeScreen()`
- Description: This closes the current screen
- Example:
```kotlin
player.closeScreen();
```

### `<Player>.craft(recipe)`
- Description: This allows you to craft a recipe, this can be 2x2 or 3x3
The list you pass in must contain Materials or ItemStacks
Most of the time you should use craftRecipe instead. You must
be in an appropriate gui for the crafting recipe or an error will be thrown
- Parameter - List (`recipe`): a list of materials making up the recipe you want to craft including air
- Example:
```kotlin
chestRecipe = [
    Material.OAK_PLANKS, Material.OAK_PLANKS, Material.OAK_PLANKS,
    Material.OAK_PLANKS,    Material.AIR    , Material.OAK_PLANKS,
    Material.OAK_PLANKS, Material.OAK_PLANKS, Material.OAK_PLANKS
];
player.craft(chestRecipe);
```

### `<Player>.craftRecipe(recipe)`
- Description: This allows you to craft a predefined recipe
- Parameter - Recipe (`recipe`): the recipe you want to craft
- Example:
```kotlin
player.craftRecipe(Recipe.CHEST);
```

### `<Player>.craftRecipe(recipe, boolean)`
- Description: This allows you to craft a predefined recipe
- Parameters:
  - Recipe (`recipe`): the recipe you want to craft
  - Boolean (`boolean`): whether result should be dropped or not
- Example:
```kotlin
player.craftRecipe(Recipe.CHEST, true);
```

### `<Player>.dropAll(material)`
- Description: This drops all items of a given type in the player's inventory
- Parameter - Material (`material`): the item stack, or material type to drop
- Example:
```kotlin
player.dropAll(Material.DIRT.asItemStack());
```

### `<Player>.dropAllExact(itemStack)`
- Description: This drops all the items that have the same nbt as a given stack
- Parameter - ItemStack (`itemStack`): the stack with nbt to drop
- Example:
```kotlin
player.dropAllExact(Material.GOLD_INGOT.asItemStack());
```

### `<Player>.dropItemInHand(dropAll)`
- Description: This drops the item(s) in the player's main hand
- Parameter - Boolean (`dropAll`): if true, all items in the player's main hand will be dropped
- Example:
```kotlin
player.dropItemInHand(true);
```

### `<Player>.dropSlot(slot)`
- Description: This allows you to drop the items in a slot
- Parameter - Number (`slot`): the slot to drop
- Example:
```kotlin
player.dropSlot(9);
```

### `<Player>.fakeLook(yaw, pitch, direction, duration)`
- Description: This makes the player 'fake' looking in a direction, this can be
used to place blocks in unusual orientations without moving the camera
- Parameters:
  - Number (`yaw`): the yaw to look at
  - Number (`pitch`): the pitch to look at
  - String (`direction`): the direction to look at
  - Number (`duration`): the duration of the look in ticks
- Example:
```kotlin
player.fakeLook(90, 0, 'up', 100);
```

### `<Player>.getBlockBreakingSpeed(itemStack, block)`
- Description: This returns the block breaking speed of the player on a block including enchanements and effects
- Parameters:
  - ItemStack (`itemStack`): item to test with
  - Block (`block`): the block to get the speed of
- Example:
```kotlin
speed = player.getBlockBreakingSpeed(Material.NETHERITE_PICKAXE.asItem(), Material.GOLD_BLOCK.asBlock());
```

### `<Player>.getCurrentScreen()`
- Description: This gets the current screen the player is in
- Returns - Screen: the screen the player is in, if the player is not in a screen it will return null
- Example:
```kotlin
screen = player.getCurrentScreen();
```

### `<Player>.getLookingAtEntity()`
- Description: This gets the entity that the player is currently looking at
- Returns - Entity: the entity that the player is looking at
- Example:
```kotlin
player.getLookingAtEntity();
```

### `<Player>.getSwappableHotbarSlot()`
- Description: This will get the next empty slot in the hotbar starting from the current slot
going right, and if it reaches the end of the hotbar it will start from the beginning.
If there is no empty slot it will return any slot that doesn't have an item with
an enchantment that is in the hotbar, again going from the current slot
if there is no such slot it will return the current selected slot
- Returns - Number: the slot that is swappable
- Example:
```kotlin
player.getSwappableHotbarSlot();
```

### `<Player>.interactBlock(pos, direction)`
- Description: This allows you to interact with a block at a position and direction
- Parameters:
  - Pos (`pos`): the position of the block
  - String (`direction`): the direction of the interaction, e.g. 'up', 'north', 'east', etc.
- Example:
```kotlin
player.interactBlock(new Pos(0, 0, 0), 'up');
```

### `<Player>.interactBlock(pos, direction, hand)`
- Description: This allows you to interact with a block at a position, direction, and hand
- Parameters:
  - Pos (`pos`): the position of the block
  - String (`direction`): the direction of the interaction, e.g. 'up', 'north', 'east', etc.
  - String (`hand`): the hand to use, e.g. 'main_hand', 'off_hand'
- Example:
```kotlin
player.interactBlock(new Pos(0, 0, 0), 'up', 'off_hand');
```

### `<Player>.interactBlock(x, y, z, direction)`
- Description: This allows you to interact with a block at a position and direction
- Parameters:
  - Number (`x`): the x position
  - Number (`y`): the y position
  - Number (`z`): the z position
  - String (`direction`): the direction of the interaction, e.g. 'up', 'north', 'east', etc.
- Example:
```kotlin
player.interactBlock(0, 100, 0, 'up');
```

### `<Player>.interactBlock(pos, direction, blockPos, insideBlock)`
- Description: This allows you to interact with a block at a position and direction
This function is for very specific cases where there needs to be extra precision
like when placing stairs or slabs in certain directions, so the first set of
coords is the exact position of the block, and the second set of coords is the position
- Parameters:
  - Pos (`pos`): the exact position of the block
  - String (`direction`): the direction of the interaction, e.g. 'up', 'north', 'east', etc.
  - Pos (`blockPos`): the position of the block
  - Boolean (`insideBlock`): whether the player is inside the block
- Example:
```kotlin
player.interactBlock(new Pos(0, 15.5, 0), 'up', new Pos(0, 15, 0), true);
```

### `<Player>.interactBlock(pos, direction, hand, blockPos, insideBlock)`
- Description: This allows you to interact with a block at a position and direction
This function is for very specific cases where there needs to be extra precision
like when placing stairs or slabs in certain directions, so the first set of
coords is the exact position of the block, and the second set of coords is the position
- Parameters:
  - Pos (`pos`): the exact position of the block
  - String (`direction`): the direction of the interaction, e.g. 'up', 'north', 'east', etc.
  - String (`hand`): the hand to use, e.g. 'main_hand', 'off_hand'
  - Pos (`blockPos`): the position of the block
  - Boolean (`insideBlock`): whether the player is inside the block
- Example:
```kotlin
player.interactBlock(new Pos(0, 15.5, 0), 'up', new Pos(0, 15, 0), true, 'off_hand');
```

### `<Player>.interactBlock(x, y, z, direction, blockX, blockY, blockZ, insideBlock)`
- Description: This allows you to interact with a block at a position and direction
This function is for very specific cases where there needs to be extra precision
like when placing stairs or slabs in certain directions, so the first set of
coords is the exact position of the block, and the second set of coords is the position
- Parameters:
  - Number (`x`): the exact x position
  - Number (`y`): the exact y position
  - Number (`z`): the exact z position
  - String (`direction`): the direction of the interaction, e.g. 'up', 'north', 'east', etc.
  - Number (`blockX`): the x position of the block
  - Number (`blockY`): the y position of the block
  - Number (`blockZ`): the z position of the block
  - Boolean (`insideBlock`): whether the player is inside the block
- Example:
```kotlin
player.interactBlock(0, 100.5, 0, 'up', 0, 100, 0, true);
```

### `<Player>.interactItem(hand)`
- Description: This allows you to interact item with given Hand
- Parameter - String (`hand`):  Hand to use, either 'main' or 'offhand'
- Example:
```kotlin
player.interactItem('main');
```

### `<Player>.interactWithEntity(entity)`
- Description: This allows your player to interact with an entity without
having to be looking at it or clicking on the entity
- Parameter - Entity (`entity`): the entity to interact with
- Example:
```kotlin
allEntities = client.getWorld().getAllEntities();
foreach (entity : allEntities) {
    if (entity.getId() == "villager" && player.getSquaredDistanceTo(entity) < 5) {
        player.interactWithEntity(entity);
        break;
    }
}
```

### `<Player>.jump()`
- Description: This will make the player jump if they are on the ground
- Example:
```kotlin
player.jump();
```

### `<Player>.logout(message)`
- Description: This forces the player to leave the world
- Parameter - String (`message`): the message to display to the player on the logout screen
- Example:
```kotlin
player.logout('You've been lazy!');
```

### `<Player>.look(yaw, pitch)`
- Description: This sets the player's look direction
- Parameters:
  - Number (`yaw`): the yaw of the player's look direction
  - Number (`pitch`): the pitch of the player's look direction
- Example:
```kotlin
player.look(0, 0);
```

### `<Player>.lookAtPos(pos)`
- Description: This makes your player look towards a position
- Parameter - Pos (`pos`): the position to look at
- Example:
```kotlin
player.lookAtPos(pos);
```

### `<Player>.lookAtPos(x, y, z)`
- Description: This makes your player look towards a position
- Parameters:
  - Number (`x`): the x coordinate of the position
  - Number (`y`): the y coordinate of the position
  - Number (`z`): the z coordinate of the position
- Example:
```kotlin
player.lookAtPos(0, 0, 0);
```

### `<Player>.message(message)`
- Description: This allows you to send a message to your player, only they will see this, purely client side
- Parameter - Text (`message`): the message to send, can also be string
- Example:
```kotlin
player.message('Hello World!');
```

### `<Player>.messageActionBar(message)`
- Description: This allows you to set the current memssage displaying on the action bar
- Parameter - Text (`message`): the message to send, can also be string
- Example:
```kotlin
player.messageActionBar('Hello World!');
```

### `<Player>.openInventory()`
- Description: This opens the player's inventory
- Example:
```kotlin
player.openInventory();
```

### `<Player>.openScreen(screen)`
- Description: This opens a screen for the player, this cannot open server side screens.
This will throw an error if you are trying to open a handled screen
- Parameter - Screen (`screen`): the screen to open
- Example:
```kotlin
player.openScreen(new FakeScreen('MyScreen', 4));
```

### `<Player>.say(message)`
- Description: This allows you to make your player send a message in chat, this includes commands
- Parameter - String (`message`): the message to send
- Example:
```kotlin
player.say('/help');
```

### `<Player>.setSelectedSlot(slot)`
- Description: This allows you to set the slot number your player is holding.
If the number is not between 0 and 8 an error will be thrown
- Parameter - Number (`slot`): the slot number, must be between 0 - 8
- Example:
```kotlin
player.setSelectedSlot(0);
```

### `<Player>.setSneaking(sneaking)`
- Description: This sets the player's sneaking state
- Parameter - Boolean (`sneaking`): the sneaking state
- Example:
```kotlin
player.setSneaking(true);
```

### `<Player>.setSprinting(sprinting)`
- Description: This sets the player's sprinting state
- Parameter - Boolean (`sprinting`): the sprinting state
- Example:
```kotlin
player.setSprinting(true);
```

### `<Player>.setWalking(walking)`
- Description: This sets the player's walking state
- Parameter - Boolean (`walking`): the walking state
- Example:
```kotlin
player.setWalking(true);
```

### `<Player>.shiftClickSlot(slot)`
- Description: This allows you to shift click a slot
- Parameter - Number (`slot`): the slot to click
- Example:
```kotlin
player.shiftClickSlot(9);
```

### `<Player>.showTitle(title, subtitle)`
- Description: THis allows you to show a title and subtitle to the player
- Parameters:
  - Text (`title`): the title to show, can be string or null
  - Text (`subtitle`): the subtitle to show, can be string or null
- Example:
```kotlin
player.showTitle('Title!', 'Subtitle!');
```

### `<Player>.spectatorTeleport(entity)`
- Description: This allows you to teleport to any entity as long as you are in spectator mode
- Parameter - Entity (`entity`): the entity to teleport to
- Example:
```kotlin
player.spectatorTeleport(player.getLookingAtEntity());
```

### `<Player>.stonecutter(itemInput, itemOutput)`
- Description: This allows you to use the stonecutter
- Parameters:
  - Material (`itemInput`): the item or material you want to input
  - Material (`itemOutput`): the item or material you want to craft
- Returns - Future: whether the result was successful
- Example:
```kotlin
player.stonecutter(Material.STONE.asItemstack(), Material.STONE_BRICKS.asItemStack());
```

### `<Player>.swapHands()`
- Description: This will swap the player's main hand with the off hand
- Example:
```kotlin
player.swapHands();
```

### `<Player>.swapPlayerSlotWithHotbar(slot)`
- Description: This allows you to swap a slot in the player's inventory with the hotbar
- Parameter - Number (`slot`): the slot to swap
- Example:
```kotlin
player.swapPlayerSlotWithHotbar(15);
```

### `<Player>.swapSlots(slot1, slot2)`
- Description: The allows you to swap two slots with one another.
A note about slot order is that slots go from top to bottom.
This will throw an errof if the slots are out of bounds
- Parameters:
  - Number (`slot1`): the slot to swap with slot2
  - Number (`slot2`): the slot to swap with slot1
- Example:
```kotlin
player.swapSlots(13, 14);
```

### `<Player>.swingHand(hand)`
- Description: This will play the player's hand swing animation for a given hand
- Parameter - String (`hand`): the hand to swing, this should be either 'main_hand' or 'off_hand'
- Example:
```kotlin
player.swingHand('main_hand');
```

### `<Player>.updateBreakingBlock(pos)`
- Deprecated: Consider using other alternatives for breaking blocks, e.g. <Player>.breakBlock
- Description: This allows you to update your block breaking progress at a position
- Parameter - Pos (`pos`): the position of the block
- Example:
```kotlin
player.updateBreakingBlock(new Pos(0, 0, 0));
```

### `<Player>.updateBreakingBlock(x, y, z)`
- Deprecated: Consider using other alternatives for breaking blocks, e.g. <Player>.breakBlock
- Description: This allows you to update your block breaking progress at a position
- Parameters:
  - Number (`x`): the x position
  - Number (`y`): the y position
  - Number (`z`): the z position
- Example:
```kotlin
player.updateBreakingBlock(0, 0, 0);
```

### `<Player>.use(action)`
- Description: This allows you to make your player use, you must
pass 'hold', 'stop', or 'once' otherwise an error will be thrown
- Parameter - String (`action`): the type of action, either 'hold', 'stop', or 'once'
- Example:
```kotlin
player.use('hold');
```

## Static Methods

### `Player.get()`
- Description: This gets the main player
- Returns - Player: The main player
- Example:
```kotlin
player = Player.get();
```



# Pos class
Pos class for Arucas

This class is a wrapper for 3 coordinate points in Minecraft
Import with `import Pos from Minecraft;`

## Constructors

### `new Pos(list)`
- Description: Creates a new Pos object with the given coordinates in a list
- Parameter - List (`list`): the list containing three coordinates
- Example:
```kotlin
new Pos([1, 2, 3])
```

### `new Pos(x, y, z)`
- Description: This creates a new Pos with the given x, y, and z
- Parameters:
  - Number (`x`): the x position
  - Number (`y`): the y position
  - Number (`z`): the z position
- Example:
```kotlin
new Pos(100, 0, 96);
```

## Methods

### `<Pos>.add(pos)`
- Description: This returns a new Pos with the current pos x, y, and z added by the given pos x, y, and z
- Parameter - Pos (`pos`): the Pos to add by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.add(new Pos(2, 3, 5));
```

### `<Pos>.add(x, y, z)`
- Description: This returns a new Pos with the current pos x, y, and z added by the given x, y, and z
- Parameters:
  - Number (`x`): the x adder
  - Number (`y`): the y adder
  - Number (`z`): the z adder
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.add(2, 3, 5);
```

### `<Pos>.asCentre()`
- Description: This returns center value of the position
- Returns - Pos: the center of the position
- Example:
```kotlin
pos.asCentre();
```

### `<Pos>.crossProduct(pos)`
- Description: This returns the cross product of the current pos and the given pos
- Parameter - Pos (`pos`): the Pos to cross product with
- Returns - Pos: the cross product
- Example:
```kotlin
pos.crossProduct(new Pos(2, 3, 5));
```

### `<Pos>.distanceTo(other)`
- Description: This returns distance to other position
- Parameter - Pos (`other`): other position
- Returns - Number: distance to other position
- Example:
```kotlin
pos.distanceTo(new Pos(0, 0, 0));
```

### `<Pos>.distanceTo(x, y, z)`
- Description: This returns distance to other x, y, z position
- Parameters:
  - Number (`x`): other position x
  - Number (`y`): other position y
  - Number (`z`): other position z
- Returns - Number: distance to other position
- Example:
```kotlin
pos.distanceTo(0, 0, 0);
```

### `<Pos>.dotProduct(pos)`
- Description: This returns the dot product of the current pos and the given pos
- Parameter - Pos (`pos`): the Pos to dot product with
- Returns - Number: the dot product
- Example:
```kotlin
pos.dotProduct(new Pos(2, 3, 5));
```

### `<Pos>.down()`
- Description: This returns a new Pos with the current pos y decremented by 1
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.down();
```

### `<Pos>.down(number)`
- Description: This returns a new Pos with the current pos y decremented by the given number
- Parameter - Number (`number`): the number to decrement by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.down(2);
```

### `<Pos>.east()`
- Description: This returns a new Pos with the current pos x incremented by 1
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.east();
```

### `<Pos>.east(number)`
- Description: This returns a new Pos with the current pos x incremented by the given number
- Parameter - Number (`number`): the number to increment by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.east(2);
```

### `<Pos>.getSidePos(direction)`
- Description: This returns side position value of position
- Parameter - String (`direction`): the direction, can be: north, south, east, west, up, down
- Returns - Pos: the side of the position
- Example:
```kotlin
pos.getSidePos('east');
```

### `<Pos>.getX()`
- Description: This returns the x position of the Pos
- Returns - Number: the x position
- Example:
```kotlin
pos.getX();
```

### `<Pos>.getY()`
- Description: This returns the y position of the Pos
- Returns - Number: the y position
- Example:
```kotlin
pos.getY();
```

### `<Pos>.getZ()`
- Description: This returns the z position of the Pos
- Returns - Number: the z position
- Example:
```kotlin
pos.getZ();
```

### `<Pos>.isNear(entity)`
- Description: This returns whether position to entity is less than 4.5
- Parameter - Entity (`entity`): the entity you want to check
- Returns - Boolean: whether entity is within 4.5 block distance
- Example:
```kotlin
pos.isNear(Player.get());
```

### `<Pos>.isWithin(entity, distance)`
- Description: This returns whether position to entity is less than given distance
- Parameters:
  - Entity (`entity`): the entity you want to check
  - Number (`distance`): the distance you want to check
- Returns - Boolean: whether entity is within given distance
- Example:
```kotlin
pos.isNear(player, 8);
```

### `<Pos>.multiply(pos)`
- Description: This returns a new Pos with the current pos x, y, and z multiplied by the given pos x, y, and z
- Parameter - Pos (`pos`): the Pos to multiply by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.multiply(new Pos(2, 3, 5));
```

### `<Pos>.multiply(x, y, z)`
- Description: This returns a new Pos with the current pos x, y, and z multiplied by the given x, y, and z
- Parameters:
  - Number (`x`): the x multiplier
  - Number (`y`): the y multiplier
  - Number (`z`): the z multiplier
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.multiply(2, 3, 5);
```

### `<Pos>.normalize()`
- Description: Normalizes the vector to have a magnitude of 1
- Returns - Pos: the normalized position
- Example:
```kotlin
pos.normalize();
```

### `<Pos>.north()`
- Description: This returns a new Pos with the current pos z incremented by 1
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.north();
```

### `<Pos>.north(number)`
- Description: This returns a new Pos with the current pos z incremented by the given number
- Parameter - Number (`number`): the number to increment by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.north(2);
```

### `<Pos>.offset(direction)`
- Description: This returns a new Pos with the current pos x, y, and z offset by a direction
- Parameter - String (`direction`): the direction to offset by, must be one of: north, south, east, west, up, down
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.offset('north');
```

### `<Pos>.offset(direction, distance)`
- Description: This returns a new Pos with the current pos x, y, and z offset by a direction and a distance
- Parameters:
  - String (`direction`): the direction to offset by, must be one of: north, south, east, west, up, down
  - Number (`distance`): the distance to offset by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.offset('north', 2);
```

### `<Pos>.south()`
- Description: This returns a new Pos with the current pos z decremented by 1
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.south();
```

### `<Pos>.south(number)`
- Description: This returns a new Pos with the current pos z decremented by the given number
- Parameter - Number (`number`): the number to decrement by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.south(2);
```

### `<Pos>.subtract(pos)`
- Description: This returns a new Pos with the current pos x, y, and z subtracted by the given pos x, y, and z
- Parameter - Pos (`pos`): the Pos to subtract by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.subtract(new Pos(2, 3, 5));
```

### `<Pos>.subtract(x, y, z)`
- Description: This returns a new Pos with the current pos x, y, and z subtracted by the given x, y, and z
- Parameters:
  - Number (`x`): the x subtractor
  - Number (`y`): the y subtractor
  - Number (`z`): the z subtractor
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.subtract(2, 3, 5);
```

### `<Pos>.toBlockPos()`
- Description: This floors all of the positions values to the nearest block
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.toBlockPos();
```

### `<Pos>.toList()`
- Description: This returns the Pos as a List containing the x, y, and z positions in order
- Returns - List: the Pos as a List
- Example:
```kotlin
x, y, z = pos.toList();
```

### `<Pos>.up()`
- Description: This returns a new Pos with the current pos y incremented by 1
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.up();
```

### `<Pos>.up(number)`
- Description: This returns a new Pos with the current pos y incremented by the given number
- Parameter - Number (`number`): the number to increment by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.up(2);
```

### `<Pos>.west()`
- Description: This returns a new Pos with the current pos x decremented by 1
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.west();
```

### `<Pos>.west(number)`
- Description: This returns a new Pos with the current pos x decremented by the given number
- Parameter - Number (`number`): the number to decrement by
- Returns - Pos: the new Pos
- Example:
```kotlin
pos.west(2);
```



# Recipe class
Recipe class for Arucas

This class represents recipes in Minecraft.
Import with `import Recipe from Minecraft;`

## Methods

### `<Recipe>.getCraftingType()`
- Description: This returns the crafting type of the recipe
- Returns - String: the crafting type of the recipe, for example: 'crafting', 'smelting', 'blasting'
- Example:
```kotlin
recipe.getCraftingType()
```

### `<Recipe>.getFullId()`
- Description: This returns the full id of the recipe
- Returns - String: the full id of the recipe
- Example:
```kotlin
recipe.getFullId()
```

### `<Recipe>.getId()`
- Description: This returns the id of the recipe
- Returns - String: the id of the recipe
- Example:
```kotlin
recipe.getId()
```

### `<Recipe>.getIngredients()`
- Description: This returns all the possible ingredients of the recipe
- Returns - List: list of lists, each inner lists contains possible recipe items
- Example:
```kotlin
recipe.getIngredients()
```

### `<Recipe>.getOutput()`
- Description: This returns the output of the recipe
- Returns - ItemStack: the output of the recipe
- Example:
```kotlin
recipe.getOutput()
```

## Static Methods

### `Recipe.of(recipeId)`
- Description: This converts a recipe id into a Recipe if it's valid,
otherwise an error will be thrown
- Parameter - String (`recipeId`): the id of the recipe to convert to a Recipe
- Returns - Recipe: the recipe instance from the id
- Example:
```kotlin
Recipe.of('redstone_block')
```



# Screen class
Screen class for Arucas

This allows you to get information about the player's current screen.
Import with `import Screen from Minecraft;`

## Methods

### `<Screen>.getName()`
- Description: Gets the name of the specific screen
- Returns - String: the screen name, if you are in the creative menu it will return the name of the tab you are on
- Example:
```kotlin
screen.getName()
```

### `<Screen>.getTitle()`
- Description: Gets the title of the specific screen
- Returns - String: the screen title as text, this may include formatting, and custom names for the screen if applicable
- Example:
```kotlin
screen.getTitle()
```



# Set class
Set class for Arucas

Sets are collections of unique values. Similar to maps, without the values.
An instance of the class can be created by using `Set.of(values...)`
Class does not need to be imported

## Constructors

### `new Set()`
- Description: This creates an empty set
- Example:
```kotlin
new Set();
```

## Methods

### `<Set>.add(value)`
- Description: This allows you to add a value to the set
- Parameter - Object (`value`): the value you want to add to the set
- Returns - Boolean: whether the value was successfully added to the set
- Example:
```kotlin
Set.of().add('object');
```

### `<Set>.addAll(collection)`
- Description: This allows you to add all the values in a collection into the set
- Parameter - Collection (`collection`): the collection of values you want to add
- Returns - Set: the modified set
- Example:
```kotlin
Set.of().addAll(Set.of('object', 81, 96, 'case'));
```

### `<Set>.clear()`
- Description: This removes all values from inside the set
- Example:
```kotlin
Set.of('object').clear();
```

### `<Set>.contains(value)`
- Description: This allows you to check whether a value is in the set
- Parameter - Object (`value`): the value that you want to check in the set
- Returns - Boolean: whether the value is in the set
- Example:
```kotlin
Set.of('object').contains('object');
```

### `<Set>.containsAll(collection)`
- Description: This allows you to check whether a collection of values are all in the set
- Parameter - Collection (`collection`): the collection of values you want to check in the set
- Returns - Boolean: whether all the values are in the set
- Example:
```kotlin
Set.of('object').containsAll(Set.of('object', 81, 96, 'case'));
```

### `<Set>.filter(function)`
- Description: This allows you to filter the set
- Parameter - Function (`function`): the function you want to filter the set by
- Returns - Set: the filtered set
- Example:
```kotlin
Set.of(-9, 81, 96, 15).filter(fun(value) { return value > 80; });
```

### `<Set>.get(value)`
- Description: This allows you to get a value from in the set.
The reason this might be useful is if you want to retrieve something
from the set that will have the same hashcode but be in a different state
as the value you are passing in
- Parameter - Object (`value`): the value you want to get from the set
- Returns - Object: the value you wanted to get, null if it wasn't in the set
- Example:
```kotlin
Set.of('object').get('object');
```

### `<Set>.map(function)`
- Description: This allows you to map the set
- Parameter - Function (`function`): the function you want to map the set by
- Returns - Set: the mapped set
- Example:
```kotlin
Set.of(-9, 81, 96, 15).map(fun(value) { return value * 2; });
```

### `<Set>.reduce(function)`
- Description: This allows you to reduce the set
- Parameter - Function (`function`): the function you want to reduce the set by
- Returns - Object: the reduced set
- Example:
```kotlin
Set.of(-9, 81, 96, 15).reduce(fun(value, next) { return value + next; });
```

### `<Set>.remove(value)`
- Description: This allows you to remove a value from the set
- Parameter - Object (`value`): the value you want to remove from the set
- Returns - Boolean: whether the value was removed from the set
- Example:
```kotlin
Set.of('object').remove('object');
```

### `<Set>.removeAll(value)`
- Description: This allows you to remove all values in a collection from the set
- Parameter - Collection (`value`): the values you want to remove from the set
- Returns - Set: the set with the values removed
- Example:
```kotlin
Set.of('object', 'object').removeAll(Set.of('object'));
```

### `<Set>.toList()`
- Description: This returns a list of all the values in the set
- Returns - List: the list of values in the set
- Example:
```kotlin
Set.of('object', 81, 96, 'case').toList();
```

## Static Methods

### `Set.of(values...)`
- Description: This allows you to create a set with an arbitrary number of values
- Parameter - Object (`values...`): the values you want to add to the set
- Returns - Set: the set you created
- Example:
```kotlin
Set.of('object', 81, 96, 'case');
```

### `Set.unordered()`
- Description: This creates an unordered set
- Returns - Set: the unordered set
- Example:
```kotlin
Set.unordered();
```



# Shape class
Shape class for Arucas

This class is the base class for all shapes that can be rendered,
providing the base functionality for all shapes
Import with `import Shape from Minecraft;`

## Methods

### `<Shape>.getBlue()`
- Description: This returns the blue value of the shape
- Returns - Number: the blue value of the shape
- Example:
```kotlin
shape.getBlue();
```

### `<Shape>.getGreen()`
- Description: This returns the green value of the shape
- Returns - Number: the green value of the shape
- Example:
```kotlin
shape.getGreen();
```

### `<Shape>.getOpacity()`
- Description: This returns the opacity of the shape
- Returns - Number: the opacity of the shape
- Example:
```kotlin
shape.getOpacity();
```

### `<Shape>.getRGB()`
- Description: This returns the RGB value of the shape
- Returns - Number: the RGB value of the shape as a single number in the form 0xRRGGBB
- Example:
```kotlin
shape.getRGB();
```

### `<Shape>.getRGBAList()`
- Description: This returns the RGBA value of the shape as a list
- Returns - List: the RGBA value of the shape as a list in the form [red, green, blue, opacity]
- Example:
```kotlin
r, g, b, a = shape.getRGBAList();
```

### `<Shape>.getRGBList()`
- Description: This returns the RGB value of the shape as a list
- Returns - List: the RGB value of the shape as a list in the form [red, green, blue]
- Example:
```kotlin
r, g, b = shape.getRGBList();
```

### `<Shape>.getRed()`
- Description: This returns the red value of the shape
- Returns - Number: the red value of the shape
- Example:
```kotlin
shape.getRed();
```

### `<Shape>.getXScale()`
- Description: This gets the x scale of the shape
- Example:
```kotlin
shape.getXScale();
```

### `<Shape>.getXTilt()`
- Description: This gets the x tilt of the shape
- Example:
```kotlin
shape.getXTilt();
```

### `<Shape>.getYScale()`
- Description: This gets the y scale of the shape
- Example:
```kotlin
shape.getYScale();
```

### `<Shape>.getYTilt()`
- Description: This gets the y tilt of the shape
- Example:
```kotlin
shape.getYTilt();
```

### `<Shape>.getZScale()`
- Description: This gets the z scale of the shape
- Example:
```kotlin
shape.getZScale();
```

### `<Shape>.getZTilt()`
- Description: This gets the z tilt of the shape
- Example:
```kotlin
shape.getZTilt();
```

### `<Shape>.render()`
- Description: This sets the shape to be rendered indefinitely, the shape will only stop rendering when
the script ends or when you call the stopRendering() method
- Example:
```kotlin
shape.render();
```

### `<Shape>.setBlue(blue)`
- Description: This sets the blue value of the shape, using a single value.
If the colour is not between 0 and 255 an error will be thrown
- Parameter - Number (`blue`): the amount of blue between 0 - 255
- Example:
```kotlin
shape.setBlue(34);
```

### `<Shape>.setColour(colour)`
- Description: This sets the colour of the shape, using a single value, this
function also has a sibling named `setColor()` that has the same functionality.
The colour generally should be hexadecimal in the form 0xRRGGBB
- Parameter - Number (`colour`): the colour you want to set
- Example:
```kotlin
shape.setColour(0xFF0000);
```

### `<Shape>.setColour(red, green, blue)`
- Description: This sets the colour of the shape, using three values this function
also has a sibling named `setColor()` that has the same functionality.
If the colours are not between 0 and 255 an error will be thrown
- Parameters:
  - Number (`red`): the amount of red 0 - 255
  - Number (`green`): the amount of green 0 - 255
  - Number (`blue`): the amount of blue 0 - 255
- Example:
```kotlin
shape.setColour(34, 55, 0);
```

### `<Shape>.setGreen(green)`
- Description: This sets the green value of the shape, using a single value.
If the colour is not between 0 and 255 an error will be thrown
- Parameter - Number (`green`): the amount of green between 0 - 255
- Example:
```kotlin
shape.setGreen(34);
```

### `<Shape>.setOpacity(opacity)`
- Description: This sets the opacity of the shape, using a single value.
If the colour is not between 0 and 255 an error will be thrown
- Parameter - Number (`opacity`): the amount of opacity between 0 - 255
- Example:
```kotlin
shape.setOpacity(34);
```

### `<Shape>.setRed(red)`
- Description: This sets the red value of the shape, using a single value.
If the colour is not between 0 and 255 an error will be thrown
- Parameter - Number (`red`): the amount of red between 0 - 255
- Example:
```kotlin
shape.setRed(34);
```

### `<Shape>.setRenderThroughBlocks(boolean)`
- Description: This sets whether the shape should render through blocks
- Parameter - Boolean (`boolean`): whether the shape should render through blocks
- Example:
```kotlin
shape.setRenderThroughBlocks(true);
```

### `<Shape>.setScale(xScale, yScale, zScale)`
- Description: This sets the scale of the shape
- Parameters:
  - Number (`xScale`): the x scale of the shape
  - Number (`yScale`): the y scale of the shape
  - Number (`zScale`): the z scale of the shape
- Example:
```kotlin
shape.setScale(1.5, 2.5, 3.5);
```

### `<Shape>.setTilt(xTilt, yTilt, zTilt)`
- Description: This sets the tilt of the shape
- Parameters:
  - Number (`xTilt`): the x tilt
  - Number (`yTilt`): the y tilt
  - Number (`zTilt`): the z tilt
- Example:
```kotlin
shape.setTilt(100, 0, 80);
```

### `<Shape>.setXScale(xScale)`
- Description: This sets the x scale of the shape
- Parameter - Number (`xScale`): the x scale of the shape
- Example:
```kotlin
shape.setXScale(1.5);
```

### `<Shape>.setXTilt(xTilt)`
- Description: This sets the x tilt of the shape
- Parameter - Number (`xTilt`): the x tilt
- Example:
```kotlin
shape.setXTilt(100);
```

### `<Shape>.setYScale(yScale)`
- Description: This sets the y scale of the shape
- Parameter - Number (`yScale`): the y scale of the shape
- Example:
```kotlin
shape.setYScale(2.5);
```

### `<Shape>.setYTilt(yTilt)`
- Description: This sets the y tilt of the shape
- Parameter - Number (`yTilt`): the y tilt
- Example:
```kotlin
shape.setYTilt(0);
```

### `<Shape>.setZScale(zScale)`
- Description: This sets the z scale of the shape
- Parameter - Number (`zScale`): the z scale of the shape
- Example:
```kotlin
shape.setZScale(3.5);
```

### `<Shape>.setZTilt(zTilt)`
- Description: This sets the z tilt of the shape
- Parameter - Number (`zTilt`): the z tilt
- Example:
```kotlin
shape.setZTilt(80);
```

### `<Shape>.shouldRenderThroughBlocks()`
- Description: This returns whether the shape should render through blocks
- Returns - Boolean: whether the shape should render through blocks
- Example:
```kotlin
shape.shouldRenderThroughBlocks();
```

### `<Shape>.stopRendering()`
- Description: This stops the shape from rendering
- Example:
```kotlin
shape.stopRendering();
```



# SphereShape class
SphereShape class for Arucas

This class is used to create a sphere shape which can be rendered in the world.
Import with `import SphereShape from Minecraft;`

## Constructors

### `new SphereShape(pos)`
- Description: This creates a new sphere shape
- Parameter - Pos (`pos`): The position of the sphere
- Example:
```kotlin
new SphereShape(new Pos(0, 10, 0));
```

### `new SphereShape(x, y, z)`
- Description: This creates a new sphere shape
- Parameters:
  - Number (`x`): The x position of the sphere
  - Number (`y`): The y position of the sphere
  - Number (`z`): The z position of the sphere
- Example:
```kotlin
new SphereShape(0, 10, 0);
```

## Methods

### `<SphereShape>.getSteps()`
- Description: This gets the number of steps the sphere will take to render
- Returns - Number: The number of steps
- Example:
```kotlin
sphere.getSteps();
```

### `<SphereShape>.setSteps(steps)`
- Description: This sets the number of steps the sphere will take to render
- Parameter - Number (`steps`): The number of steps
- Example:
```kotlin
sphere.setSteps(30);
```



# String class
String class for Arucas

This class represents an array of characters to form a string.
This class cannot be instantiated directly, instead use the literal
by using quotes. Strings are immutable in Arucas.
Class does not need to be imported

## Constructors

### `new String()`
- Description: This creates a new string object, not from the string pool, with the given string.
This cannot be called directly, only from child classes
- Example:
```kotlin
class ChildString: String {
    ChildString(): super("example");
}
```

## Methods

### `<String>.capitalize()`
- Description: This returns the string in capitalized form
- Returns - String: the string in capitalized form
- Example:
```kotlin
'hello'.capitalize(); // 'Hello'
```

### `<String>.chars()`
- Description: This makes a list of all the characters in the string
- Returns - List: the list of characters
- Example:
```kotlin
'hello'.chars(); // ['h', 'e', 'l', 'l', 'o']
```

### `<String>.contains(string)`
- Description: This returns whether the string contains the given string
- Parameter - String (`string`): the string to check
- Returns - Boolean: whether the string contains the given string
- Example:
```kotlin
'hello'.contains('lo'); // true
```

### `<String>.endsWith(string)`
- Description: This returns whether the string ends with the given string
- Parameter - String (`string`): the string to check
- Returns - Boolean: whether the string ends with the given string
- Example:
```kotlin
'hello'.endsWith('lo'); // true
```

### `<String>.find(regex)`
- Description: This finds all matches of the regex in the string,
this does not find groups, for that use `<String>.findGroups(regex)`
- Parameter - String (`regex`): the regex to search the string with
- Returns - List: the list of all instances of the regex in the string
- Example:
```kotlin
'102i 1i'.find('([\\d+])i'); // ['2i', '1i']
```

### `<String>.findAll(regex)`
- Description: This finds all matches and groups of a regex in the matches in the string
the first group of each match will be the complete match and following
will be the groups of the regex, a group may be empty if it doesn't exist
- Parameter - String (`regex`): the regex to search the string with
- Returns - List: a list of match groups, which is a list containing matches
- Example:
```kotlin
'102i 1i'.findAll('([\\d+])i'); // [['2i', '2'], ['1i', '1']]
```

### `<String>.format(objects...)`
- Description: This formats the string using the given arguments.
This internally uses the Java String.format() method.
For how to use see here: https://www.javatpoint.com/java-string-format
- Parameter - Object (`objects...`): the objects to insert
- Returns - String: the formatted string
- Example:
```kotlin
'%s %s'.format('hello', 'world'); // 'hello world'
```

### `<String>.length()`
- Description: This returns the length of the string
- Returns - Number: the length of the string
- Example:
```kotlin
'hello'.length(); // 5
```

### `<String>.lowercase()`
- Description: This returns the string in lowercase
- Returns - String: the string in lowercase
- Example:
```kotlin
'HELLO'.lowercase(); // 'hello'
```

### `<String>.matches(regex)`
- Description: This returns whether the string matches the given regex
- Parameter - String (`regex`): the regex to match the string with
- Returns - Boolean: whether the string matches the given regex
- Example:
```kotlin
'foo'.matches('f.*'); // true
```

### `<String>.replaceAll(regex, replacement)`
- Description: This replaces all the instances of a regex with the replace string
- Parameters:
  - String (`regex`): the regex you want to replace
  - String (`replacement`): the string you want to replace it with
- Returns - String: the modified string
- Example:
```kotlin
'hello'.replaceAll('l', 'x'); // 'hexxo'
```

### `<String>.replaceFirst(regex, replacement)`
- Description: This replaces the first instance of a regex with the replace string
- Parameters:
  - String (`regex`): the regex you want to replace
  - String (`replacement`): the string you want to replace it with
- Returns - String: the modified string
- Example:
```kotlin
'hello'.replaceFirst('l', 'x'); // 'hexlo'
```

### `<String>.reverse()`
- Description: This returns the string in reverse
- Returns - String: the string in reverse
- Example:
```kotlin
'hello'.reverse(); // 'olleh'
```

### `<String>.split(regex)`
- Description: This splits the string into a list of strings based on a regex
- Parameter - String (`regex`): the regex to split the string with
- Returns - List: the list of strings
- Example:
```kotlin
'foo/bar/baz'.split('/');
```

### `<String>.startsWith(string)`
- Description: This returns whether the string starts with the given string
- Parameter - String (`string`): the string to check
- Returns - Boolean: whether the string starts with the given string
- Example:
```kotlin
'hello'.startsWith('he'); // true
```

### `<String>.strip()`
- Description: This strips the whitespace from the string
- Returns - String: the stripped string
- Example:
```kotlin
'  hello  '.strip(); // 'hello'
```

### `<String>.subString(from, to)`
- Description: This returns a substring of the string
- Parameters:
  - Number (`from`): the start index (inclusive)
  - Number (`to`): the end index (exclusive)
- Returns - String: the substring
- Example:
```kotlin
'hello'.subString(1, 3); // 'el'
```

### `<String>.toList()`
- Deprecated: Use '<String>.chars()' instead
- Description: This makes a list of all the characters in the string
- Returns - List: the list of characters
- Example:
```kotlin
'hello'.toList(); // ['h', 'e', 'l', 'l', 'o']
```

### `<String>.toNumber()`
- Description: This tries to convert the string to a number.
This method can convert hex or denary into numbers.
If the string is not a number, it will throw an error
- Returns - Number: the number
- Example:
```kotlin
'99'.toNumber(); // 99
```

### `<String>.uppercase()`
- Description: This returns the string in uppercase
- Returns - String: the string in uppercase
- Example:
```kotlin
'hello'.uppercase(); // 'HELLO'
```



# Task class
Task class for Arucas

This class is used to create tasks that can be chained and
run asynchronously. Tasks can be executed as many times as needed
and chained tasks will be executed in the order they are created.
Class does not need to be imported

## Constructors

### `new Task()`
- Description: This creates a new empty task
- Example:
```kotlin
task = new Task();
```

## Methods

### `<Task>.loopIf(boolSupplier)`
- Description: This loops the task, essentially just calling 'task.run', the
task will run async from the original task, the loop will continue
if the function provided returns true
- Parameter - Function (`boolSupplier`): the function to check if the loop should run
- Returns - Task: the task, this allows for chaining
- Example:
```kotlin
task = new Task()
    .then(fun() print("hello"))
    .then(fun() print(" "))
    .then(fun() print("world"))
    .loopIf(fun() true); // Always loop
```

### `<Task>.run()`
- Description: This runs the task asynchronously and returns a future which can be awaited.
The last function in the task will be used as the return value for the future
- Returns - Future: the future value that can be awaited
- Example:
```kotlin
task = new Task()
    .then(fun() print("hello"))
    .then(fun() print(" "))
    .then(fun() print("world"))
    .then(fun() 10);
f = task.run(); // prints 'hello world'
print(f.await()); // prints 10
```

### `<Task>.then(function)`
- Description: This adds a function to the end of the current task.
If this is the last function in the task then the return
value of the function will be the return value of the task.
- Parameter - Function (`function`): the function to run at the end of the task
- Returns - Task: the task, this allows for chaining
- Example:
```kotlin
task = new Task()
    .then(fun() print("hello"))
    .then(fun() print(" "))
    .then(fun() print("world"))
    .then(fun() 10);
f = task.run(); // prints 'hello world'
print(f.await()); // prints 10
```



# Text class
Text class for Arucas

This class is used to create formatted strings used inside Minecraft.
Import with `import Text from Minecraft;`

## Methods

### `<Text>.append(otherText)`
- Description: This allows you to append a text instance to another text instance
- Parameter - Text (`otherText`): the text instance to append to
- Returns - Text: the text instance with the appended text
- Example:
```kotlin
Text.of('Hello').append(Text.of(' world!'));
```

### `<Text>.format(formatting)`
- Description: This allows you to add a formatting to a text instance.
A list of formatting names can be found [here](https://minecraft.fandom.com/wiki/Formatting_codes).
This will throw an error if the formatting is invalid
- Parameter - String (`formatting`): the name of the formatting
- Returns - Text: the text instance with the formatting added
- Example:
```kotlin
text.format('DARK_RED').format('BOLD');
```

### `<Text>.withClickEvent(event, value)`
- Description: This allows you to add a click event to a text instance.
The possible events are: 'open_url', 'open_file', 'run_command', 'suggest_command', 'copy_to_clipboard', 'run_function'.
This will throw an error if the action is invalid
- Parameters:
  - String (`event`): the name of the event
  - String (`value`): the value associated with the event
- Returns - Text: the text instance with the click event
- Example:
```kotlin
text = Text.of("Hello World!");

// Examples of click events
text.withClickEvent("open_url", "https://youtu.be/dQw4w9WgXcQ");
text.withClickEvent("open_file", "C:/Users/user/Desktop/thing.txt");
text.withClickEvent("run_command", "/gamemode creative");
text.withClickEvent("suggest_command", "/gamemode survival");
text.withClickEvent("copy_to_clipboard", "Ooops!");
text.withClickEvent("run_function", fun() {
    print("Text was clicked!");
});
```

### `<Text>.withHoverEvent(event, value)`
- Description: This allows you to add a hover event to a text instance.
The possible events are: 'show_text', 'show_item', 'show_entity'.
This will throw an error if the event is invalid
- Parameters:
  - String (`event`): the name of the event
  - Object (`value`): the value associated with the event
- Returns - Text: the text instance with the hover event
- Example:
```kotlin
text = Text.of("Hello World!");

// Examples of hover events
text.withHoverEvent("show_text", Text.of("Hello world!"));
text.withHoverEvent("show_item", Material.DIAMOND_SWORD.asItemStack());
text.withHoverEvent("show_entity", Player.get());
```

## Static Methods

### `Text.of(string)`
- Description: This converts a string into a text instance
- Parameter - String (`string`): The string to convert into a text instance
- Returns - Text: the text instance from the string
- Example:
```kotlin
Text.of('Hello World!');
```

### `Text.parse(textJson)`
- Description: This converts a text json into a text instance
- Parameter - String (`textJson`): The string in json format, or a Json value itself
- Returns - Text: the text instance from the json
- Example:
```kotlin
Text.parse('{"text":"Hello World!","color":"white","italic":"true"}');
```



# Thread class
Thread class for Arucas

This class allows to to create threads for async executions.
This class cannot be instantiated or extended. To create a new
thread use the static method 'Thread.runThreaded()'
Class does not need to be imported

## Methods

### `<Thread>.freeze()`
- Description: This serves the same purpose as 'Thread.freeze()' however this works on the current
thread instance, unlike 'Thread.freeze()' this cannot throw an error.
- Example:
```kotlin
Thread.getCurrentThread().freeze()
```

### `<Thread>.getAge()`
- Description: This gets the age of the thread in milliseconds
- Returns - Number: the age of the thread
- Example:
```kotlin
Thread.getCurrentThread().getAge();
```

### `<Thread>.getName()`
- Description: This gets the name of the thread
- Returns - String: the name of the thread
- Example:
```kotlin
Thread.getCurrentThread().getName();
```

### `<Thread>.isAlive()`
- Description: This checks if the thread is alive (still running)
- Returns - Boolean: true if the thread is alive, false if not
- Example:
```kotlin
Thread.getCurrentThread().isAlive();
```

### `<Thread>.stop()`
- Description: This stops the thread from executing, anything that was running will be instantly stopped.
This method will fail if the thread is not alive
- Example:
```kotlin
Thread.getCurrentThread().stop();
```

### `<Thread>.thaw()`
- Description: This will thaw the thread from its frozen state, if the thread is not frozen then an
error will be thrown
- Example:
```kotlin
Thread.getCurrentThread().thaw();
```

## Static Methods

### `Thread.freeze()`
- Description: This freezes the current thread, stops anything else from executing on the thread.
This may fail if you try to freeze a non Arucas Thread in which case an error will be thrown
- Example:
```kotlin
Thread.freeze();
```

### `Thread.getCurrentThread()`
- Description: This gets the current thread that the code is running on,
this may throw an error if the thread is not safe to get,
which happens when running outside of Arucas Threads
- Returns - Thread: the current thread
- Example:
```kotlin
Thread.getCurrentThread();
```

### `Thread.runThreaded(function)`
- Description: This starts a new thread and runs a function on it, the thread will
terminate when it finishes executing the function, threads will stop automatically
when the program stops, you are also able to stop threads by using the Thread object
- Parameter - Function (`function`): the function you want to run on a new thread
- Returns - Thread: the new thread
- Example:
```kotlin
Thread.runThreaded(fun() {
    print("Running asynchronously!");
});
```

### `Thread.runThreaded(name, function)`
- Description: This starts a new thread with a specific name and runs a function on it
- Parameters:
  - String (`name`): the name of the thread
  - Function (`function`): the function you want to run on a new thread
- Returns - Thread: the new thread
- Example:
```kotlin
Thread.runThreaded("MyThread", fun() {
    print("Running asynchronously on MyThread");
});
```



# Trade class
Trade class for Arucas

This class represents a trade offer, and allows you to get information about it.
Import with `import Trade from Minecraft;`

## Methods

### `<Trade>.getAdjustedFirstBuyItem()`
- Description: Gets the first item that the merchant will buy, adjusted by the price multiplier
- Returns - ItemStack: the first item to buy
- Example:
```kotlin
trade.getAdjustedFirstBuyItem();
```

### `<Trade>.getFirstBuyItem()`
- Description: Gets the first item that the merchant will buy
- Returns - ItemStack: the first item to buy
- Example:
```kotlin
trade.getFirstBuyItem();
```

### `<Trade>.getMaxUses()`
- Description: Gets the maximum number of times the trade can be used
- Returns - Number: the maximum number of uses
- Example:
```kotlin
trade.getMaxUses();
```

### `<Trade>.getPriceMultiplier()`
- Description: Gets the price multiplier which is used to adjust the price of the first buy item
- Returns - Number: the price multiplier
- Example:
```kotlin
trade.getPriceMultiplier();
```

### `<Trade>.getSecondBuyItem()`
- Description: Gets the second item that the merchant will buy
- Returns - ItemStack: the second item to buy
- Example:
```kotlin
trade.getSecondBuyItem();
```

### `<Trade>.getSellItem()`
- Description: Gets the item that is being sold by the merchant
- Returns - ItemStack: the item for sale
- Example:
```kotlin
trade.getSellItem();
```

### `<Trade>.getSpecialPrice()`
- Description: This gets the special price which is used to adjust the price of the first buy item
- Returns - Number: the special price
- Example:
```kotlin
trade.getSpecialPrice();
```

### `<Trade>.getUses()`
- Description: Gets the number of times the trade has been used
- Returns - Number: the number of uses
- Example:
```kotlin
trade.getUses();
```

### `<Trade>.getXpReward()`
- Description: Returns the amount of xp the villager will get, which
goes towards them levelling up, from trading this offer
- Returns - Number: the amount of xp
- Example:
```kotlin
trade.getXpReward
```



# Type class
Type class for Arucas

This class lets you get the type of another class
Class does not need to be imported

## Methods

### `<Type>.getName()`
- Description: This gets the name of the type
- Returns - String: the name of the type
- Example:
```kotlin
String.type.getName();
```

### `<Type>.inheritsFrom(type)`
- Description: This checks whether a type is a subtype of another type
- Parameter - Type (`type`): the other type you want to check against
- Returns - Boolean: whether the type is of that type
- Example:
```kotlin
String.type.inheritsFrom(Number.type);
```

## Static Methods

### `Type.of(value)`
- Description: This gets the specific type of a value
- Parameter - Object (`value`): the value you want to get the type of
- Returns - Type: the type of the value
- Example:
```kotlin
Type.of(0);
```



# World class
World class for Arucas

This class represents worlds, and allows you to interact with things inside of them.
Import with `import World from Minecraft;`

## Methods

### `<World>.getAllEntities()`
- Description: This will get all entities in the world
- Returns - List: a list of all entities
- Example:
```kotlin
world.getAllEntities();
```

### `<World>.getAllOtherPlayers()`
- Deprecated: Use '<World>.getAllPlayers()' instead
- Description: This will get all other players in the world
- Returns - List: a list of all other players
- Example:
```kotlin
world.getAllOtherPlayers();
```

### `<World>.getAllPlayers()`
- Description: This function gets all players in the world that are loaded
- Returns - List: all players in the world
- Example:
```kotlin
world.getAllPlayers();
```

### `<World>.getArea(pos1, pos2)`
- Deprecated: This function is memory intensive, you should use `<World>.getPositions(pos1, pos2)`
- Description: This gets a list of all block positions between the two positions
- Parameters:
  - Pos (`pos1`): the first position
  - Pos (`pos2`): the second position
- Returns - List: the list of positions
- Example:
```kotlin
world.getArea(new Pos(0, 100, 0), new Pos(0, 100, 0));
```

### `<World>.getAreaOfBlocks(pos1, pos2)`
- Deprecated: This function is memory intensive, you should use `<World>.getBlocks(pos1, pos2)`
- Description: This gets a list of all blocks (with positions) between the two positions
- Parameters:
  - Pos (`pos1`): the first position
  - Pos (`pos2`): the second position
- Returns - List: the list of blocks
- Example:
```kotlin
world.getAreaOfBlocks(new Pos(0, 100, 0), new Pos(0, 100, 0));
```

### `<World>.getBiomeAt(pos)`
- Description: This function gets the biome at the given coordinates
- Parameter - Pos (`pos`): the position
- Returns - Biome: the biome at the given coordinates
- Example:
```kotlin
world.getBiomeAt(new Pos(0, 100, 0));
```

### `<World>.getBiomeAt(x, y, z)`
- Description: This function gets the biome at the given coordinates
- Parameters:
  - Number (`x`): the x coordinate
  - Number (`y`): the y coordinate
  - Number (`z`): the z coordinate
- Returns - Biome: the biome at the given coordinates
- Example:
```kotlin
world.getBiomeAt(0, 100, 0);
```

### `<World>.getBlockAt(pos)`
- Description: This function gets the block at the given coordinates
- Parameter - Pos (`pos`): the position
- Returns - Block: the block at the given coordinates
- Example:
```kotlin
world.getBlockAt(new Pos(0, 100, 0));
```

### `<World>.getBlockAt(x, y, z)`
- Description: This function gets the block at the given coordinates
- Parameters:
  - Number (`x`): the x coordinate
  - Number (`y`): the y coordinate
  - Number (`z`): the z coordinate
- Returns - Block: the block at the given coordinates
- Example:
```kotlin
world.getBlockAt(0, 100, 0);
```

### `<World>.getBlockLight(pos)`
- Description: Gets the block light at the given position ignoring sky light
- Parameter - Pos (`pos`): the position of the block
- Returns - Number: the light level between 0 - 15
- Example:
```kotlin
world.getBlockLight(new Pos(0, 0, 0));
```

### `<World>.getBlocks(pos1, pos2)`
- Description: This gets an iterator for all blocks (and positions) between two positions
- Parameters:
  - Pos (`pos1`): the first position
  - Pos (`pos2`): the second position
- Returns - Iterable: the iterator for the blocks
- Example:
```kotlin
foreach (block : world.getBlocks(new Pos(0, 100, 100), new Pos(0, 100, 0)));
```

### `<World>.getBlocksFromCentre(centre, xRange, yRange, zRange)`
- Description: This gets an iterator for all blocks (and positions) between two positions.
The iterator iterates from the centre outwards
- Parameters:
  - Pos (`centre`): the central position
  - Number (`xRange`): how far to iterate on the x axis
  - Number (`yRange`): how far to iterate on the y axis
  - Number (`zRange`): how far to iterate on the z axis
- Returns - Iterable: the iterator for the blocks
- Example:
```kotlin
foreach (block : world.getBlocksFromCentre(new Pos(0, 100, 100), 10, 5, 60));
```

### `<World>.getClosestPlayer(entity, maxDistance)`
- Description: This will get the closest player to another entity in the world
- Parameters:
  - Entity (`entity`): the entity to get the closest player to
  - Number (`maxDistance`): the maximum distance to search for a player in blocks
- Returns - Player: the closest player, null if not found
- Example:
```kotlin
world.getClosestPlayer(Player.get(), 100);
```

### `<World>.getDimensionName()`
- Deprecated: You should use 'world.getId()' instead
- Description: This will get the id of the world
- Returns - String: the id of the world, for example: 'overworld'
- Example:
```kotlin
world.getDimensionName();
```

### `<World>.getEmittedRedstonePower(pos, direction)`
- Description: Gets the emitted restone power at the given position and direction
- Parameters:
  - Pos (`pos`): the position of the block
  - String (`direction`): the direction to check, for example 'north', 'east', 'up', etc.
- Returns - Number: the emitted redstone power
- Example:
```kotlin
world.getEmittedRedstonePower(new Pos(0, 100, 0), 'north');
```

### `<World>.getEmittedRedstonePower(x, y, z, direction)`
- Description: Gets the emitted restone power at the given position and direction
- Parameters:
  - Number (`x`): the x position of the block
  - Number (`y`): the y position of the block
  - Number (`z`): the z position of the block
  - String (`direction`): the direction to check, for example 'north', 'east', 'up', etc.
- Returns - Number: the emitted redstone power
- Example:
```kotlin
world.getEmittedRedstonePower(0, 100, 0, 'north');
```

### `<World>.getEntityFromId(entityId)`
- Description: This will get an entity from the given entity id
- Parameter - Number (`entityId`): the entity id
- Returns - Entity: the entity, null if not found
- Example:
```kotlin
world.getEntityFromId(1);
```

### `<World>.getFullId()`
- Description: This will get the full id of the world
- Returns - String: the full id of the world, for example: 'minecraft:overworld'
- Example:
```kotlin
world.getFullId();
```

### `<World>.getId()`
- Description: This will get the id of the world
- Returns - String: the id of the world, for example: 'overworld'
- Example:
```kotlin
world.getId();
```

### `<World>.getLight(pos)`
- Description: Gets the light level at the given position, takes the max of either sky light of block light
- Parameter - Pos (`pos`): the position of the block
- Returns - Number: the light level between 0 - 15
- Example:
```kotlin
world.getLight(new Pos(0, 100, 0));
```

### `<World>.getLight(x, y, z)`
- Description: Gets the light level at the given position, takes the max of either sky light of block light
- Parameters:
  - Number (`x`): the x position of the block
  - Number (`y`): the y position of the block
  - Number (`z`): the z position of the block
- Returns - Number: the light level between 0 - 15
- Example:
```kotlin
world.getLight(0, 100, 0);
```

### `<World>.getOtherPlayer(username)`
- Deprecated: Use '<World>.getPlayer(name)' instead
- Description: This gets another player from the given username
- Parameter - String (`username`): the username of the other player
- Returns - Player: the other player, null if not found
- Example:
```kotlin
world.getOtherPlayer('senseiwells');
```

### `<World>.getPlayer(name)`
- Description: This function gets the player with the given name
- Parameter - String (`name`): the name of the player
- Returns - Player: the player with the given name
- Example:
```kotlin
world.getPlayer('player');
```

### `<World>.getPositions(pos1, pos2)`
- Description: This gets an iterator for all positions between two positions
- Parameters:
  - Pos (`pos1`): the first position
  - Pos (`pos2`): the second position
- Returns - Iterable: the iterator for the positions
- Example:
```kotlin
foreach (pos : world.getPositions(new Pos(0, 100, 100), new Pos(0, 100, 0)));
```

### `<World>.getPositionsFromCentre(centre, xRange, yRange, zRange)`
- Description: This gets an iterator for all positions between two positions.
The iterator iterates from the centre outwards
- Parameters:
  - Pos (`centre`): the central position
  - Number (`xRange`): how far to iterate on the x axis
  - Number (`yRange`): how far to iterate on the y axis
  - Number (`zRange`): how far to iterate on the z axis
- Returns - Iterable: the iterator for the positions
- Example:
```kotlin
foreach (pos : world.getPositionsFromCentre(new Pos(0, 100, 100), 10, 10, 10));
```

### `<World>.getSkyLight(pos)`
- Description: Gets the sky light at the given position ignoring block light
- Parameter - Pos (`pos`): the position of the block
- Returns - Number: the light level between 0 - 15
- Example:
```kotlin
world.getSkyLight(new Pos(0, 0, 0));
```

### `<World>.getTimeOfDay()`
- Description: This will get the time of day of the world
info on the time of day [here](https://minecraft.fandom.com/wiki/Daylight_cycle)
- Returns - Number: the time of day of the world, between 0 and 24000
- Example:
```kotlin
world.getTimeOfDay();
```

### `<World>.isAir(pos)`
- Description: Returns true if the block at the given position is air
- Parameter - Pos (`pos`): the position of the block
- Returns - Boolean: true if the block is air
- Example:
```kotlin
world.isAir(new Pos(0, 100, 0));
```

### `<World>.isAir(x, y, z)`
- Description: Returns true if the block at the given position is air
- Parameters:
  - Number (`x`): the x position of the block
  - Number (`y`): the y position of the block
  - Number (`z`): the z position of the block
- Returns - Boolean: true if the block is air
- Example:
```kotlin
world.isAir(0, 100, 0);
```

### `<World>.isLoaded(pos)`
- Description: This function returns loaded state of given coordinates(client side)
- Parameter - Pos (`pos`): the position
- Returns - Boolean: whether the block is loaded at the given coordinates
- Example:
```kotlin
world.isLoaded(new Pos(0, 100, 0));
```

### `<World>.isRaining()`
- Description: This will check if the world is currently raining
- Returns - Boolean: true if the world is currently raining
- Example:
```kotlin
world.isRaining();
```

### `<World>.isThundering()`
- Description: This will check if the world is currently thundering
- Returns - Boolean: true if the world is currently thundering
- Example:
```kotlin
world.isThundering();
```

### `<World>.renderParticle(particleId, pos)`
- Description: This will render a particle in the world, you can find a list of all
the particle ids [here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Particles),
this will throw an error if the id is invalid
- Parameters:
  - String (`particleId`): the id of the particle
  - Pos (`pos`): the position of the particle
- Example:
```kotlin
world.renderParticle('end_rod', pos);
```

### `<World>.renderParticle(particleId, x, y, z)`
- Description: This will render a particle in the world, you can find a list of all
the particle ids [here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Particles),
if the id is invalid it will throw an error
- Parameters:
  - String (`particleId`): the id of the particle
  - Number (`x`): the x position of the particle
  - Number (`y`): the y position of the particle
  - Number (`z`): the z position of the particle
- Example:
```kotlin
world.renderParticle('end_rod', 10, 10, 10);
```

### `<World>.renderParticle(particleId, pos, velX, velY, velZ)`
- Description: This will render a particle in the world with a velocity, you can find a list of all
the particle ids [here](https://minecraft.fandom.com/wiki/Java_Edition_data_values#Particles),
this will throw an error if the id is invalid
- Parameters:
  - String (`particleId`): the id of the particle
  - Pos (`pos`): the position of the particle
  - Number (`velX`): the velocity of the particle on the x axis
  - Number (`velY`): the velocity of the particle on the y axis
  - Number (`velZ`): the velocity of the particle on the z axis
- Example:
```kotlin
world.renderParticle('end_rod', pos, 0.5, 0.5, 0.5);
```

### `<World>.setGhostBlock(block, pos)`
- Deprecated: This function is dangerous, use at your own risk
- Description: This sets a ghost block in the world as if it were a real block, may cause issues
- Parameters:
  - Block (`block`): the block to set
  - Pos (`pos`): the position of the block
- Example:
```kotlin
world.setGhostBlock(Material.BEDROCK.asBlock(), new Pos(0, 100, 0));
```

### `<World>.setGhostBlock(block, x, y, z)`
- Deprecated: This function is dangerous, use at your own risk
- Description: This sets a ghost block in the world as if it were a real block, may cause issues
- Parameters:
  - Block (`block`): the block to set
  - Number (`x`): the x position of the block
  - Number (`y`): the y position of the block
  - Number (`z`): the z position of the block
- Example:
```kotlin
world.setGhostBlock(Material.BEDROCK.asBlock(), 0, 100, 0);
```

