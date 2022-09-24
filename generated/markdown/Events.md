# Events

Events are triggers for certain events that happen in the game.
ClientScript provides a way to hook into these triggers to be able to run your code.
You can register multiple functions to an event and they will all get called.
See [here]() on how to register and unregister events.
Each event will run async by default but you are able to run it on the main game thread.


## `"onAttackBlock"`
- This event is fired when the player attacks a block
- Parameter - Block (`block`): the block that was attacked
- Cancellable: true
```kotlin
new GameEvent("onAttackBlock", fun(block) {
    // Code
});
```


## `"onDimensionChange"`
- This event is fired when the player changes their dimension
- Parameter - World (`world`): the new world
- Cancellable: false
```kotlin
new GameEvent("onDimensionChange", fun(world) {
    // Code
});
```


## `"onSendMessage"`
- This event is fired when the player sends a message in chat
- Parameter - String (`message`): the message that was sent
- Cancellable: true
```kotlin
new GameEvent("onSendMessage", fun(message) {
    // Code
});
```


## `"onRespawn"`
- This event is fired when the player respawns
- Parameter - Player (`player`): the respawned player entity
- Cancellable: false
```kotlin
new GameEvent("onRespawn", fun(player) {
    // Code
});
```


## `"onPlayerLeave"`
- This event is fired when a player leaves the server
- Parameter - String (`name`): the player's name
- Cancellable: false
```kotlin
new GameEvent("onPlayerLeave", fun(name) {
    // Code
});
```


## `"onKeyPress"`
- This event is fired when a key is pressed
- Parameter - String (`key`): the key that was pressed
- Cancellable: true
```kotlin
new GameEvent("onKeyPress", fun(key) {
    // Code
});
```


## `"onCloseScreen"`
- This event is fired when the player closes a screen
- Parameter - Screen (`screen`): the screen that was just closed
- Cancellable: false
```kotlin
new GameEvent("onCloseScreen", fun(screen) {
    // Code
});
```


## `"onInteractItem"`
- This event is fired when the player interacts with an item
- Parameter - ItemStack (`itemStack`): the item stack that was interacted with
- Cancellable: true
```kotlin
new GameEvent("onInteractItem", fun(itemStack) {
    // Code
});
```


## `"onEntityRemoved"`
- This event is fired when an entity is removed
- Parameter - Entity (`entity`): the entity that was removed
- Cancellable: false
```kotlin
new GameEvent("onEntityRemoved", fun(entity) {
    // Code
});
```


## `"onClickSlot"`
- This event is fired when the player clicks on a slot in their inventory
- Parameter - Number (`slot`): the slot number that was clicked
- Parameter - String (`action`): ths action that was used
- Cancellable: true
```kotlin
new GameEvent("onClickSlot", fun(slot, action) {
    // Code
});
```


## `"onBlockBroken"`
- This event is fired when the player breaks a new block
- Parameter - Block (`block`): the block that was broken
- Cancellable: false
```kotlin
new GameEvent("onBlockBroken", fun(block) {
    // Code
});
```


## `"onInteractBlock"`
- This event is fired when the player interacts with a block
- Parameter - Block (`block`): the block the player is interacting with
- Parameter - ItemStack (`itemStack`): the item stack that was interacted with
- Cancellable: true
```kotlin
new GameEvent("onInteractBlock", fun(block, itemStack) {
    // Code
});
```


## `"onKeyRelease"`
- This event is fired when a key is released
- Parameter - String (`key`): the key that was released
- Cancellable: true
```kotlin
new GameEvent("onKeyRelease", fun(key) {
    // Code
});
```


## `"onClickRecipe"`
- This event is fired when the player clicks on a recipe in the recipe book
- Parameter - Recipe (`recipe`): the recipe that was clicked
- Cancellable: true
```kotlin
new GameEvent("onClickRecipe", fun(recipe) {
    // Code
});
```


## `"onConnect"`
- The event is fired when the player connects to a server
- Parameter - Player (`player`): the player entity
- Parameter - World (`world`): the world the player joined
- Cancellable: false
```kotlin
new GameEvent("onConnect", fun(player, world) {
    // Code
});
```


## `"onClientTick"`
- The event is fired on every client tick
- Cancellable: false
```kotlin
new GameEvent("onClientTick", fun() {
    // Code
});
```


## `"onHealthUpdate"`
- This event is fired when the player's health changes
- Parameter - Number (`health`): the new health
- Cancellable: false
```kotlin
new GameEvent("onHealthUpdate", fun(health) {
    // Code
});
```


## `"onMobSpawn"`
- This event is fired when a mob spawns
- Parameter - LivingEntity (`mob`): the mob that was spawned
- Cancellable: false
```kotlin
new GameEvent("onMobSpawn", fun(mob) {
    // Code
});
```


## `"onScriptPacket"`
- This event is fired when a script packet is received from scarpet on the server
- Parameter - List (`packet`): a list of data that was received
- Cancellable: false
```kotlin
new GameEvent("onScriptPacket", fun(packet) {
    // Code
});
```


## `"onAttack"`
- This event is fired when the player attacks
- Cancellable: true
```kotlin
new GameEvent("onAttack", fun() {
    // Code
});
```


## `"onPlayerLook"`
- This event is fired when the player changes their yaw and/or pitch
- Parameter - Number (`yaw`): the player's yaw
- Parameter - Number (`pitch`): the player's pitch
- Cancellable: true
```kotlin
new GameEvent("onPlayerLook", fun(yaw, pitch) {
    // Code
});
```


## `"onBlockUpdate"`
- This event is fired when a block update is recieved on the client
- Parameter - Block (`block`): the block that was updated
- Cancellable: false
```kotlin
new GameEvent("onBlockUpdate", fun(block) {
    // Code
});
```


## `"onInteractEntity"`
- This event is fired when the player interacts with an entity
- Parameter - Entity (`entity`): the entity that was interacted with
- Parameter - ItemStack (`itemStack`): the item stack that was interacted with
- Cancellable: true
```kotlin
new GameEvent("onInteractEntity", fun(entity, itemStack) {
    // Code
});
```


## `"onDisconnect"`
- This event is fired when the player disconnects from a server
- Cancellable: false
```kotlin
new GameEvent("onDisconnect", fun() {
    // Code
});
```


## `"onOpenScreen"`
- This event is fired when the player opens a screen
- Parameter - Screen (`screen`): the screen that was just opened
- Cancellable: false
```kotlin
new GameEvent("onOpenScreen", fun(screen) {
    // Code
});
```


## `"onGamemodeChange"`
- This event is fired when the player changes their gamemode
- Parameter - String (`gamemode`): the new gamemode
- Cancellable: false
```kotlin
new GameEvent("onGamemodeChange", fun(gamemode) {
    // Code
});
```


## `"onDeath"`
- This event is fired when the player dies
- Parameter - Entity (`entity`): the entity that killed the player, may be null
- Parameter - Text (`message`): the death message
- Cancellable: false
```kotlin
new GameEvent("onDeath", fun(entity, message) {
    // Code
});
```


## `"onPickBlock"`
- This event is fired when the player picks a block with middle mouse
- Parameter - ItemStack (`itemStack`): the item stack that was picked
- Cancellable: true
```kotlin
new GameEvent("onPickBlock", fun(itemStack) {
    // Code
});
```


## `"onUse"`
- This event is fired when the player uses
- Cancellable: true
```kotlin
new GameEvent("onUse", fun() {
    // Code
});
```


## `"onMouseScroll"`
- This event is fired when the player scrolls
- Parameter - Number (`direction`): either -1 or 1 depending on the scroll direction
- Cancellable: true
```kotlin
new GameEvent("onMouseScroll", fun(direction) {
    // Code
});
```


## `"onDropItem"`
- This event is fired when the player tries to drop an item
- Parameter - ItemStack (`itemStack`): the item that is trying to be dropped
- Cancellable: true
```kotlin
new GameEvent("onDropItem", fun(itemStack) {
    // Code
});
```


## `"onPlayerJoin"`
- This event is fired when a player joins the server
- Parameter - String (`name`): the player's name
- Cancellable: false
```kotlin
new GameEvent("onPlayerJoin", fun(name) {
    // Code
});
```


## `"onAttackEntity"`
- This event is fired when the player attacks an entity
- Parameter - Entity (`entity`): the entity that was attacked
- Cancellable: true
```kotlin
new GameEvent("onAttackEntity", fun(entity) {
    // Code
});
```


## `"onEntitySpawn"`
- This event is fired when an entity spawns, this doesn't include mobs
- Parameter - Entity (`entity`): the entity that was spawned
- Cancellable: false
```kotlin
new GameEvent("onEntitySpawn", fun(entity) {
    // Code
});
```


## `"onPickUpItem"`
- This event is fired when the player picks up an item
- Parameter - ItemStack (`itemStack`): the item
- Cancellable: false
```kotlin
new GameEvent("onPickUpItem", fun(itemStack) {
    // Code
});
```


## `"onEat"`
- This event is fired when the player eats something
- Parameter - ItemStack (`food`): the item stack that was eaten
- Cancellable: false
```kotlin
new GameEvent("onEat", fun(food) {
    // Code
});
```


## `"onReceiveMessage"`
- This event is fired when the player receives a message in chat
- Parameter - String (`uuid`): the sender's UUID
- Parameter - String (`message`): the message that was received
- Cancellable: true
```kotlin
new GameEvent("onReceiveMessage", fun(uuid, message) {
    // Code
});
```


## `"onFishBite"`
- This event is fired when a fish bites the player's fishing rod
- Parameter - Entity (`entity`): the fishing bobber entity
- Cancellable: false
```kotlin
new GameEvent("onFishBite", fun(entity) {
    // Code
});
```


## `"onAnvil"`
- This event is fired when the player anvils an item
- Parameter - ItemStack (`first`): the first input
- Parameter - ItemStack (`second`): the second input
- Parameter - ItemStack (`result`): the result of the first and second input
- Parameter - String (`newName`): the new name of the item stack
- Parameter - Number (`levelCost`): the amount of xp required
- Cancellable: false
```kotlin
new GameEvent("onAnvil", fun(first, second, result, newName, levelCost) {
    // Code
});
```


## `"onTotem"`
- This event is fired when the player uses a totem
- Cancellable: false
```kotlin
new GameEvent("onTotem", fun() {
    // Code
});
```


## `"onBlockPlaced"`
- This event is fired when the player places a block
- Parameter - Block (`block`): the block that was placed
- Cancellable: false
```kotlin
new GameEvent("onBlockPlaced", fun(block) {
    // Code
});
```


## `"onScriptEnd"`
- This event is fired when the script is ends
- Cancellable: false
```kotlin
new GameEvent("onScriptEnd", fun() {
    // Code
});
```
