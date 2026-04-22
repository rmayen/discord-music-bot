# Discord Music Bot

A Discord bot built in Java using JDA (Java Discord API) and LavaPlayer that lets users play, pause, skip, and identify music tracks directly in a Discord server.

## Overview

This project is a music-playing Discord bot that connects to voice channels and streams audio. Users interact with the bot through simple text commands prefixed with `!`. The bot manages a queue system so multiple tracks can be lined up and played in sequence.

## Purpose

Built to learn how to integrate third-party APIs (Discord, audio streaming) into a Java application while exploring event-driven programming, audio processing, and real-time user interaction in a chat platform.

## Technologies Used

- **Java** - Core programming language
- **JDA (Java Discord API)** - Discord bot framework for handling events and messages
- **LavaPlayer** - Audio player library for loading and streaming music tracks
- **Maven/Gradle** - Dependency management

## Features

- **Play music** - Load and play audio tracks from a URL using `!play <url>`
- **Playlist support** - Loading a playlist URL queues the first track automatically
- **Pause/Resume** - Toggle playback with `!pause`
- **Skip tracks** - Skip the current track with `!skip` or `!next`
- **Queue system** - Tracks are queued and played in order
- **Identify track** - See what's currently playing with `!identify`
- **Secure token handling** - Bot token is read from the `DISCORD_TOKEN` environment variable, not hardcoded

## How to Run

### Prerequisites
- Java JDK 8 or higher
- A Discord bot token ([Discord Developer Portal](https://discord.com/developers/applications))
- JDA and LavaPlayer dependencies resolved through Maven, Gradle, or manually placed on the classpath

### Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/rmayen/discord-music-bot.git
   cd discord-music-bot
   ```
2. Copy `.env.example` and export your bot token into your shell environment:
   ```bash
   export DISCORD_TOKEN=your_bot_token_here
   ```
   On Windows (PowerShell):
   ```powershell
   $env:DISCORD_TOKEN = "your_bot_token_here"
   ```
3. Add the required dependencies (JDA and LavaPlayer) to your build tool or classpath.
4. Compile and run:
   ```bash
   javac SimpleBot.java
   java SimpleBot
   ```
5. Invite the bot to your Discord server and use `!play <url>` to start playing music.

## Commands

| Command | Description |
|---------|-------------|
| `!play <url>` | Loads and plays a track from the given URL |
| `!pause` | Pauses or resumes the current track |
| `!skip` | Skips to the next track in the queue |
| `!next` | Plays the next queued track |
| `!identify` | Displays the title of the currently playing track |

## Project Structure

```
discord-music-bot/
├── SimpleBot.java    # Main bot class, command routing, and embedded TrackScheduler
├── .env.example      # Template for the DISCORD_TOKEN environment variable
├── .gitignore        # Git ignore rules
└── README.md
```

## My Role

I developed this bot as a personal project to explore the Discord API ecosystem in Java. I designed the command handler, implemented the track queue system using a `BlockingQueue`, handled playlist loading and load failures through the full `AudioLoadResultHandler` interface, and integrated LavaPlayer for audio streaming.

## Lessons Learned

- Gained experience with event-driven architecture through JDA's `ListenerAdapter`
- Learned how to manage concurrent audio playback using blocking queues
- Understood how to implement every method of a third-party callback interface (`AudioLoadResultHandler`) to handle playlists, missing matches, and load failures
- Practiced moving secrets out of source code and into environment variables
