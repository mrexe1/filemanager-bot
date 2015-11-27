# File Manager - Telegram Bot
A Telegram Bot to explore files and download them from any computer

### Build Status ###
[![Build Status](https://travis-ci.org/shiblymeeran/filemanager-bot.svg)](https://travis-ci.org/shiblymeeran/filemanager-bot)

### Configuration for Bot Owners ###
Create a file named **easy-tbot-config.json** and add the following:

```json
[
	{
		"botLauncherclassName": "me.shib.java.app.telegram.bot.filemanager.main.FileManagerBotLaucher",
		"botApiToken": "YourBotApiTokenGoesHere",
		"commandList": ["/start","/help","/status","/scr"],
		"threadCount": 4,
		"adminIdList": [000000000, 000000000],
		"reportIntervalInSeconds": 604800,
		"constants": {
			"homeDirPath": "D:\\Music",
			"maxEntriesPerView": "20",
			"sendDir": "true",
			"fileExtensionsToShow": "mp3,wav"
		}
	}
]
```
* **botLauncherclassName** - The fully qualified class name of the bot (You don't have to change what's given above).
* **botApiToken** - The API token that you receive when you create a bot with [@BotFather](https://telegram.me/BotFather).
* **commandList** - The list of supported commands.
* **threadCount** - The number of threads the bot should have. This bot is restricted to 7 threads.
* **adminIdList** - Use [@GO_Robot](https://telegram.me/GO_Robot) to find your telegram ID and add it to admin list.
* **reportIntervalInSeconds** - The intervals at which the Bot reports the Admins the status (To know if it is alive). 
* **homeDirPath** - The root directory for the Bot that marks the start of navigation and navigation above which is restricted.
* **maxEntriesPerView** - The number of items to be listed per view.
* **sendDir** - To send the entire directory's contents.
* **fileExtensionsToShow** - Limits the files that needs to be shown based on the mentioned extensions.

### Downloads [(Releases)](https://github.com/shiblymeeran/filemanager-bot/releases) ###
* File Manager - Telegram Bot Executable JAR **(jar-with-dependencies)**