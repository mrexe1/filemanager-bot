# File Manager - Telegram Bot
[![Build Status](https://travis-ci.org/shibme/filemanager-bot.svg)](https://travis-ci.org/shibme/filemanager-bot)
[![Dependency Status](https://www.versioneye.com/user/projects/56adffd57e03c7003db69636/badge.svg?style=flat)](https://www.versioneye.com/user/projects/56adffd57e03c7003db69636)
[![Percentage of issues still open](http://isitmaintained.com/badge/open/shibme/filemanager-bot.svg)](http://isitmaintained.com/project/shibme/filemanager-bot "Percentage of issues still open")

A Telegram Bot to explore files and download them from any PC or server

### Build Status

### Configuration for Bot Owners
Create a file named `jbots-config.json` and add the following
```json
[
	{
		"botApiToken": "YourBotApiTokenGoesHere",
		"botModelClassName": "me.shib.java.app.telegram.bot.filemanager.main.FileManagerBot",
		"commandList": ["/start","/help","/status","/scr"],
		"threadCount": 4,
		"adminIdList": [1111111111111, 2222222222222],
		"reportIntervalInSeconds": 604800,
		"constants": {
			"homeDirPath": "D:\\Music",
			"maxEntriesPerView": "20",
			"sendDir": "false",
			"fileExtensionsToShow": "mp3,wav"
		}
	}
]
```
* `botApiToken` - The API token that you receive when you create a bot with [@BotFather](https://telegram.me/BotFather).
* `botModelClassName` - The fully qualified class name of the bot (You don't have to change what's given above).
* `commandList` - The list of supported commands.
* `threadCount` - The number of threads the bot should have. This bot is restricted to 7 threads.
* `adminIdList` - Use [@GO_Robot](https://telegram.me/GO_Robot) to find your telegram ID and add it to admin list.
* `reportIntervalInSeconds` - The intervals at which the Bot reports the Admins the status (To know if it is alive). 
* `homeDirPath` - The root directory for the Bot that marks the start of navigation and navigation above which is restricted.
* `maxEntriesPerView` - The number of items to be listed per view.
* `sendDir` - To send the entire directory's contents.
* `fileExtensionsToShow` - Limits the files that needs to be shown based on the mentioned extensions.