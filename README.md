![The WumpusRumpus Banner](https://github.com/Arc-blroth/WumpusRumpus/blob/master/wumpusrumpuslogo.png "The WumpusRumpus Banner")
<div align="center">
  <img src="https://img.shields.io/static/v1.svg?label=A&&message=DiscordBot&color=blue" style="margin-right:10px;"></img>
  <img src="https://img.shields.io/badge/Discord%20Hack%20Week-2019-%23000000.svg"></img></br>
  <i>a game of silly situations!</i>
</div>


## Includes
* A fully-featured yet compact game for 2-8 players
* Saving things and attacking things
* Various loot. And Hamsters!
* A plot! (kinda)
* Ascii art _and_ emojis
* Opportunites to sabatage your friends!
* Funsies!

## Screenshots
![The Map](https://github.com/Arc-blroth/WumpusRumpus/blob/master/screenshots/theMap.png)<br/>
![Server Racks](https://github.com/Arc-blroth/WumpusRumpus/blob/master/screenshots/foundRack.png)<br/>
![Bugs](https://github.com/Arc-blroth/WumpusRumpus/blob/master/screenshots/bug.png)<br/>
![Loot](https://github.com/Arc-blroth/WumpusRumpus/blob/master/screenshots/loot.png)<br/>
![Winning](https://github.com/Arc-blroth/WumpusRumpus/blob/master/screenshots/gameOver.png)<br/>

# Getting Started

## How to Build
1. In order to get this bot up and running, you'll first have to download it! Make sure you have [git](https://git-scm.com/) installed, then run `git clone https://github.com/Arc-blroth/WumpusRumpus.git` to clone this repository.
2. Next, you'll have to make and add a bot token to this bot so it can work. Head over to https://discordapp.com/developers/applications/, create a new application, build a new bot, and copy its bot token.
3. Go to `src/main/res/config/bot.config` and replace `[TOKEN HERE]` with your bot token.
4. Finally, go to the root of the project, and build the source code by running `gradlew build` and then `gradlew run`.
5. If all goes well then you should see `INFO: Starting the Rumpus` at the console. Good job!

## How to Add the Bot
To add the bot to your server, go to https://discordapp.com/developers/applications/, select the WumpusRumpus bot, and go to the Oauth2 section. Check the Bot scope box and then the Administrator bot permission box. Next, go to the url that gets generated, and add the bot to your server!

## Getting Help
Basic help is provided in `!wr help` by default. Most commands are explained in-game when neccessary.

If you need more help, feel free to PM Arc'blroth#3638 on Discord.

## Strings
If you want to change a command's description or even translate the entire bot into another language, you can edit the `strings.config` file in `src/main/res/config`. You can add or edit loot entries in `src/main/res/config/loot.config.json`. Don't rename any of these config files ;)

# The Game

_It was an ordinary night at the Discordapp Servers..._

As usual, the Wumpuses were working hard fixing things and the Quantum Hamsters were coding things.

Suddenly, a loud boom came out of nowhere! Before the Wumpuses knew it, the server racks had begun to collapse!

At that rate, Discord would be offline in a matter of minutes! Someone had to stop the mess!
<hr></hr>
To start a new game, type !wr new_game in any channel. A new channel will be created for that game.

Games can have 2-8 players, including yourself, and last for 10 turns.

To join a new game, type !wr join in the game's channel. Once all players have joined, type in !wr start_game to start the game and let the rumpus begin!

**The Game**: All Wumpuses (Players) begin by spawning on a map of the Discord Servers. Each turn, Wumpuses can navigate to a different adjacent room. In each room, there wil either be a server rack, a router, a bug, a hamster, loot, or nothing. Wumpuses can choose to try to save servers, routers, and hamsters. If saving is successful, then yay! If saving is unsuccessful, servers and racks will blow up forever :﻿(. If a Wumpus encounters a bug, they will be forced to try attacking it. The game lasts for 10 turns.

**Saving Servers**: Wumpuses start out with a 50% chance of success at saving a server. Each attempt at saving a server will increase the chance of success. In addition, certain pieces of loot can also affect the chance of success.

**Bug Battles**: Similarily to server saving, Wumpuses begin with a 40% chance of winning against a bug. This chance can go up as a Wumpus battles more, and certain pieces of loot also affect the battle chance.

**Sabatage?**: Bugs aren't the only things you can attack. Attacking servers and routers that other players have already saved will result in pure sabatage!

**Getting Points**: Wumpuses earn points depending on how many saved servers they have saved. Saving hamsters and defeating bugs counts for extra points!

**The Goal**: To repair the network of routers and servers and to earn as many points as possible!

## Commands
`!wr help` - Provides in-bot help.

`!wr instructions (or !wr directions)` - Instructions for the game.

`!wr new_game` - Creates a new discord channel to host a new WumpusRumpus game. The person who executes this command is the "host" for the game.

`!wr join` - Join a WumpusRumpus game. This command and every command below can only be used in a WumpusRumpus game channel.

`!wr start_game` - Once all players who want to play have joined, the host player can execute this to start the game. The instructions automatically pop up for new players to read. (It's quite long, so give players some time to read it before moving)

`!wr end_game` - Once the game is over, this command deletes the game channel.

`!wr map` - Displays the map if needed.

`!wr move [direction]` - The current player uses this to move 1 grid space up, down, left, or right on the map.

`!wr stats` - Displays the current player's game stats.

`!wr inventory` - Shows what's in the current player's inventory.

`!wr skip` - Skips the current player's turn.

`!wr yes`/`!wr no` - Confirms attempting to save a server or router.

`!wr attack` - If the player is currently in a battle with a bug, this will cause the player to try attacking the bug. Players can also attack servers and routers to destroy them, or even other players...

# Credits & Licenses

WumpusRumpus was conjured up and developed by Arc'blroth for the 2019 Discord HackWeek that ran from June 24 to June 28.

Thanks to Discord for making such a great platform to hang out _and_ to develop for.<br/>

**Three Great Pieces of Open-Source Software were used in the making of this bot. Please see OPEN&#8209;SOURCE&#8209;LICENSES.md for each of their licenses.**

**This software is licensed under the GNU GPL. See LICENSE.md for the full license.**

_Disclaimer: Wumpus is the true hero as well as the mascot of Discord Inc. Discord and all other registered trademarks are (c) Discord Inc._

_This bot and all of its components are open source. See github.com/Arc-blroth/WumpusRumpus for the source code. (c) Arc'blroth 2019. All Rights Reserved._

## A Note on Git
This repo's git history is a hectic one, to say the least. Being an amateur of git, I've had to reset and filter-branch this repository multiple times :)
