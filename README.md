# Photos Habesha

**Your memories, on the Ethiopian calendar.**

A local-first Android photo gallery that organizes the photos and videos already on your phone using the Ethiopian calendar.

- No account
- No cloud upload
- No backend
- Reads only what is already on the device via Android MediaStore

## Features

| Tab | What it does |
|-----|----------------|
| **Photos** | Dense timeline of your media grouped by Ethiopian date (መስከረም 12, 2019 …) |
| **Calendar** | Visual photo-map of the Ethiopian month — days with photos show a thumbnail |
| **Folders** | Device albums (Camera, Screenshots, WhatsApp, Instagram, …) |
| **Search** | Find by Ethiopian date, folder name, or “Videos” |

Tap any photo → full-screen viewer with Ethiopian + Gregorian metadata and favourite toggle.

## Requirements

- Android 8.0+ (API 26)
- Permission: Photos & videos (READ_MEDIA_IMAGES / READ_MEDIA_VIDEO on Android 13+)

## Build with GitHub Actions

1. Push this repository to GitHub.
2. Go to **Actions** → **Build APK** → the workflow runs automatically on every push to `main`.
3. Download the **PhotosHabesha-debug** artifact.
4. Transfer the `.apk` to your phone and install (enable “Install from unknown sources” if needed).

You can also trigger a build manually: **Actions → Build APK → Run workflow**.

## Build locally

```bash
# needs JDK 17+
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## Project structure

```
app/src/main/java/com/habesha/photos/
├── data/          MediaItem, MediaRepository (MediaStore), ViewModel
├── util/          EthiopianCalendar conversion
├── ui/
│   ├── theme/     Dark gallery colours
│   ├── components BottomNav, PhotoGrid, Viewer, Permission
│   └── screens/   Photos, Calendar, Folders, Search
└── MainActivity.kt
```

## Ethiopian calendar

Gregorian `DATE_TAKEN` from MediaStore is converted to Ethiopian year / month / day using a standard civil calendar algorithm. The original files on disk are never modified — the app only builds an in-memory index.

## Privacy

All processing stays on the device. The app does not request internet permission and does not upload media.

## Roadmap (next versions)

- Accurate weekday alignment for the calendar grid
- On-this-day memories
- Multi-select / share / trash
- Room database index for faster reloads
- Proper adaptive launcher icon assets

---

Built to match the interactive HTML prototype design.
