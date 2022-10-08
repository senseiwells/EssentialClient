# EssentialClient

[![Discord](https://badgen.net/discord/online-members/7R9SfktZxH?icon=discord&label=Discord&list=what)](https://discord.gg/7R9SfktZxH)
[![GitHub downloads](https://img.shields.io/github/downloads/senseiwells/essentialclient/total?label=Github%20downloads&logo=github)](https://github.com/senseiwells/essentialclient/releases)

EssentialClient is a client side only mod originally forked
from [Carpet Client for 1.15.2](https://github.com/gnembon/carpet-client) that implements new client side features.

This mod is currently supporting 1.17.1, and 1.18.2, and 1.19.2

1.16.5 requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) v0.35.1+
and [Carpet Mod](https://www.curseforge.com/minecraft/mc-mods/carpet) v1.4.44+.

1.17.1 requires [Carpet Mod](https://www.curseforge.com/minecraft/mc-mods/carpet) v1.4.57+.

1.18.2 requires [Carpet Mod](https://www.curseforge.com/minecraft/mc-mods/carpet) v1.4.69+.

1.19.2 requires [Carpet Mod](https://www.curseforge.com/minecraft/mc-mods/carpet) v1.4.78+

To access the Essential Client menu you must join a world, then it will be accessible to you when you press
`ESC`, you can enable menu access from the title screen by enabling [essentialClientButton](#essentialclientbutton).

## Here is a link to my YouTube video about the mod

[![Image](https://cdn.discordapp.com/attachments/559400132710236160/899739577995108372/EssentialClient480.jpg)](https://youtu.be/lmMkC102T24)

# Features

## Client Scripting

This feature provides an API for the Minecraft allowing for scripts to be run while playing the game. The API uses
Arucas a programming language made by myself.

Documentation and how to use can be found: [here](https://github.com/senseiwells/EssentialClient/blob/main/docs/Full.md)

![Imgur](https://imgur.com/1aPjhMp.gif)

## ChunkDebug

ChunkDebug is a useful tool allowing you to monitor currently loaded chunks in the world. See
the [ChunkDebug wiki](https://github.com/senseiwells/ChunkDebug/wiki) for more information.

![2021-12-28_00 16 50](https://user-images.githubusercontent.com/66843746/147515139-4c2d4bbb-d8e4-416c-9933-eecc2a957a91.png)
![2021-12-28_00 17 04](https://user-images.githubusercontent.com/66843746/147515143-7b08b16f-e5de-412e-a31f-b2c7f60af582.png)

## CarpetClient

CarpetClient allows you to modify Carpet rules with a GUI. This only works if you have carpet installed on the server
you are playing on, and you have the appropriate permissions, descriptions of rules are provided through Crec0's rule
database which can be found [here](https://carpet-rules.crec.dev/).

![Image](https://cdn.discordapp.com/attachments/967529963106492448/981711993574674432/2022-06-02_01.11.39.png)

## GameruleScreen

This is a GUI that allows you to modify gamerules in singleplayer, and on servers (with EssentialAddons installed), it
is accessible from the Essential Client menu.

![Image](https://cdn.discordapp.com/attachments/967529963106492448/981711992995856414/2022-06-02_01.11.56.png)
