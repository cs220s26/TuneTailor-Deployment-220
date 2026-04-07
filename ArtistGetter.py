#This is a script that when runs utilizes a spotify api key, checks it and then goes out and
# gets "emotion:artist" which then it stores it in "artists.txt"

#This is from the prior semester we should only need it if we wanted an updatedlist
# due to spotify being more strict on their api calls

#-Ryan Demarest
"""
import os
import requests
from collections import OrderedDict
from dotenv import load_dotenv
import base64
import time

#
load_dotenv()

CLIENT_ID = os.getenv("SPOTIFY_CLIENT_ID")
CLIENT_SECRET = os.getenv("SPOTIFY_CLIENT_SECRET")

if not CLIENT_ID or not CLIENT_SECRET:
    raise RuntimeError("Missing Spotify credentials in .env file")



def get_access_token():
    auth_string = f"{CLIENT_ID}:{CLIENT_SECRET}"
    auth_base64 = base64.b64encode(auth_string.encode()).decode()

    url = "https://accounts.spotify.com/api/token"
    headers = {
        "Authorization": f"Basic {auth_base64}",
        "Content-Type": "application/x-www-form-urlencoded"
    }
    data = {"grant_type": "client_credentials"}

    r = requests.post(url, headers=headers, data=data)
    r.raise_for_status()
    return r.json()["access_token"]

ACCESS_TOKEN = get_access_token()


MOOD_QUERIES = {
    "happy": [
        "happy pop",
        "dance pop",
        "feel good hits",
        "summer pop",
        "party pop"
    ],
    "sad": [
        "sad acoustic",
        "sad indie",
        "sad pop",
        "breakup songs",
        "sad piano"
    ],
    "calm": [
        "calm ambient",
        "chill lo-fi",
        "relaxing indie",
        "sleep music",
        "acoustic calm"
    ],
    "energetic": [
        "energetic edm",
        "workout hip hop",
        "party edm",
        "high energy pop",
        "pump up music"
    ]
}

TARGET_PER_MOOD = 1000
OUTPUT_FILE = "artists_by_mood_real.txt"


def spotify_search(query, offset):
    headers = {"Authorization": f"Bearer {ACCESS_TOKEN}"}
    params = {
        "q": query,
        "type": "track",
        "limit": 50,
        "offset": offset,
        "market": "US"
    }

    r = requests.get(
        "https://api.spotify.com/v1/search",
        headers=headers,
        params=params
    )

    if r.status_code == 401:
        raise RuntimeError("Spotify 401 – Token expired or invalid")

    r.raise_for_status()
    return r.json()


def collect_artists_for_mood(mood, queries):
    artists = OrderedDict()

    print(f"Collecting {mood} artists via multi-search...")

    for query in queries:
        offset = 0

        while offset <= 950 and len(artists) < TARGET_PER_MOOD:
            data = spotify_search(query, offset)

            tracks = data.get("tracks", {}).get("items", [])
            if not tracks:
                break

            for track in tracks:
                for artist in track.get("artists", []):
                    name = artist.get("name")
                    if name:
                        artists.setdefault(name, True)

            offset += 50
            time.sleep(0.1)  # avoid rate limits

        if len(artists) >= TARGET_PER_MOOD:
            break

    return list(artists.keys())

def main():
    lines = []

    for mood in MOOD_QUERIES:
        artists = collect_artists_for_mood(mood, MOOD_QUERIES[mood])

        if len(artists) < TARGET_PER_MOOD:
            print(f"⚠️ Only {len(artists)} artists found for {mood}")
        else:
            artists = artists[:TARGET_PER_MOOD]

        for artist in artists:
            lines.append(f"{mood}:{artist}")

        lines.append("")

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        f.write("\n".join(lines))

    print(f"\n✅ DONE — Wrote to {OUTPUT_FILE}")
    print(f"Total lines: {len(lines)}")

if __name__ == "__main__":
    main()
"""