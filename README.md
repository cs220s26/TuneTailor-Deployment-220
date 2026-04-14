
# TuneTailor Discord Bot

**Author:** Ryan Demarest, Andrew Kelleman, Clannys Alvarez
**Course:** CSCI 220 – Introduction To DevOps
**Institution:** Moravian University

---

### CI Status

![Testing](https://github.com/cs220s26/TuneTailor-Deployment-220/actions/workflows/run_tests.yml/badge.svg)


## Project Description

TuneTailor is a Discord bot that delivers personalized music recommendations based on a short interactive survey. Users may complete the survey individually (solo mode) or with another user (pair mode). Based on each participant’s responses, the system analyzes mood and returns a curated list of recommended artists.

The bot is fully interactive within Discord, supports pause and resume functionality, and is backed by Redis with automatic in-memory fallback for reliability. The project applies object-oriented design, test-driven development (TDD), clean code principles, and persistent storage using Redis. (more to add here ...)

---

## System Diagram (need to be updated)

![TuneTailor System Architecture Diagram](system-diagram.png)

*Figure 1: TuneTailor system architecture and class relationships.*

---

## Major Components

- **TuneTailorBot** – Application entry point and Discord startup
- **TuneTailorResponder** – Handles Discord events and user input
- **TuneTailorResponses** – Centralized message formatting

- **SurveyGame** – Core business logic and survey flow controller
- **SurveyStorage (Interface)** – Abstract storage contract
- **RedisSurveyStorage / MemorySurveyStorage** – Concrete storage implementations
- **MoodAnalyzer** – Determines mood from survey answers
- **ArtistRecommender** – Selects artists based on mood
- **SurveyQuestions** – Static question and allowed-answer definitions
---


## Architecture Justification

- **Separation of Obligation:** Discord interaction, business logic, storage, and formatting are isolated into independent layers.

- **Testability:** The SurveyStorage interface allows complete mocking and memory-based testing.

- **Scalability:** Redis enables persistent multi-user survey sessions across servers.

- **Backup Usage Of Memory:** Automatic backup of memory prevents crashes if Redis becomes unavailable.

- **Maintainability:** Loose coupling allows for the system logic, storage, and Discord handling to change independently.

---

## Redis Data Storage Design

TuneTailor uses **Redis as a key–value data store** to persist all active survey states. Redis is the primary storage layer during runtime, while an in-memory implementation is used as a fallback for fault tolerance and testing.

On the Java side, the storage API is defined by the `SurveyStorage` interface and implemented by:

- **RedisSurveyStorage** – Primary persistent data store
- **MemorySurveyStorage** – In-memory fallback used when Redis is unavailable

All Redis operations are wrapped in safety methods so that failures automatically switch to memory without crashing the system.

---

### Data Model and Redis Types

This project uses two Redis data types:

- **String keys** – Used for flags, counters, turn tracking, and user IDs
- **Hash Maps (`HSET` / `HGETALL`)** – Used to store indexed survey answers


- HSET → writes one field-value pair into a Redis hash **

- HGETALL → reads all field-value pairs from that same hash

All numeric values are stored as strings and parsed into integers in Java.  
Boolean values are stored as `"1"` (true) or as missing keys (false).

---

### Solo Survey Key Structure

Each solo survey is stored using the Discord user ID:

- `solo:active:<userId>` – Active Indicator
- `solo:paused:<userId>` – Paused Indicator 
- `solo:index:<userId>` – Current question index
- `solo:answers:<userId>` – Hash of question index → answer

Answers are reconstructed into ordered lists using:

```
SurveyQuestions.retrieveAnswersInOrder(...)
```

---

### Pair Survey Key Structure

Only **one global pair survey** is allowed at a time. These keys are used:

- `pair:active` – Pair active flag
- `pair:paused` – Pair paused flag
- `pair:index` – Current question index
- `pair:turn` – Whose turn it is (`"1"` or `"2"`)
- `pair:user1`, `pair:user2` – Discord user IDs
- `pair:answers:1`, `pair:answers:2` – Hashes of answers for each user

Each user’s answers are stored in separate Redis hash maps and reconstructed into ordered lists for mood analysis.

---

### Persistence!!!

Redis is the **primary persistent data store** throughout runtime.  
All survey state transitions are written to Redis immediately.

If Redis becomes unavailable:

- The system automatically switches to `MemorySurveyStorage`
- The bot continues running without crashing
- Unit tests run fully without Redis
- Business logic remains unchanged

During normal operation, both Redis and memory are updated to maintain synchronization and prevent data loss.


---

## How to Run the Bot (more to add here, local & prod.)

*Local:* Todo:

1. Create a `.env` file in the project root:
- Make Sure to put only your "DISCORD_TOKEN = "


2. Ensure Redis is running:



3. Run the main class:

*Production* Todo:

---

## Discord Commands
- `!help` – pulls up a page with (these) commands 
- `!survey` – Start a solo survey
- `!pairsurvey` – Host a pair survey
- `!join` – Join a pair survey
- `!pause solo` / `!resume solo`
- `!pause pair` / `!resume pair`
- `!stop` – Cancel all surveys
- `!help` – Display help menu

---
## Spotify Artist Getter (separate) (should not be needed, but if you wanted a fresh list in the .txt)

** if you wanted to use this, you need to put these in your `.env`

`SPOTIFY_CLIENT_ID `= "your client id"

`SPOTIFY_CLIENT_SECRET` = "client secret code"

- Then just run the script 
---

