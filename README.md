# TUN IPTV — تطبيق IPTV حقيقي (Android هاتف + Android TV)

# TUN IPTV — a real IPTV player app (Android phone + Android TV)

---

## 🇹🇳 ما هو التطبيق؟ / What is this app?

**TUN IPTV** هو تطبيق IPTV حقيقي مبني بـ Kotlin و ExoPlayer (androidx.media3).
يشغّل قوائم M3U من الإنترنت: قنوات رياضية وغيرها، مع تصنيفات، بحث، مفضّلة،
وواجهة تلفاز (Leanback) كاملة للتحكم بالريموت.

**TUN IPTV** is a real IPTV player built with Kotlin and ExoPlayer (androidx.media3).
It plays M3U playlists from the internet: sports channels and more, with
categories, search, favorites, and a full Leanback TV interface for remote control.

- **الحزمة / Package:** `com.tuniptv.app` — **الإصدار / Version:** 1.0.0 (versionCode 1)
- **التصميم / Design:** Premium Dark — أسود `#05070C` / `#0A0E14` وأزرق `#1E88E5`
- **المشغّل / Player:** ExoPlayer (HLS + DASH + MP4/M3U8 تلقائياً)
- **قوائم افتراضية / Default playlists:**
  - Sports → `https://raw.githubusercontent.com/locagogps-dev/iptv-playlists/main/popular-sports-iptv.m3u`
  - Playlist → `https://raw.githubusercontent.com/locagogps-dev/iptv-playlists/main/playlist.m3u`

---

## ✨ المميزات / Features

| الميزة / Feature | التفاصيل / Details |
|---|---|
| تحليل M3U / M3U parser | `#EXTINF` : `tvg-id`, `tvg-name`, `tvg-logo`, `group-title` + رابط البث |
| قوائم من الإنترنت / HTTP playlists | تحميل عبر OkHttp، مع حالة تحميل وخطأ وإعادة محاولة |
| قائمة قنوات / Channel list | RecyclerView + شعارات القنوات (Coil) + تصنيفات أفقية |
| بحث / Search | فلترة فورية بالاسم أو التصنيف |
| مفضّلة / Favorites | نجمة ⭐ محفوظة في SharedPreferences |
| قائمة مخصصة / Custom playlist | زر "+ Playlist" — أضف أي رابط M3U خاص بك |
| مشغّل الهاتف / Phone player | ملء الشاشة، تشغيل/إيقاف، قناة سابقة/تالية، تغيير الحجم (fit/zoom) |
| Android TV | واجهة Leanback: صفوف حسب التصنيف + بطاقات + مشغّل ExoPlayer بالريموت (D-pad) |
| تحديث / Refresh | إعادة تحميل القائمة من زر إعادة المحاولة أو بتغيير القائمة |

---

## 🏗️ بناء APK تلقائياً عبر GitHub Actions / Automatic APK build

لا حاجة لـ Android Studio لبناء التطبيق. كل دفع (push) إلى فرع `main`
— أو تشغيل يدوي — يبني APK تلقائياً:

You do **not** need Android Studio to build the app. Every push to `main`
— or a manual run — builds the APK automatically:

1. ارفع المشروع إلى GitHub / Push this project to GitHub:
   ```bash
   git init
   git add .
   git commit -m "TUN IPTV 1.0.0"
   git branch -M main
   git remote add origin https://github.com/<username>/tun-iptv.git
   git push -u origin main
   ```
2. افتح تبويب **Actions** في المستودع / Open the **Actions** tab in the repo.
3. اختر تشغيل **Build APK** / Select the **Build APK** run.
4. نزّل الملف من قسم **Artifacts** في أسفل صفحة التشغيل: **TUN-IPTV-APK**
   يحتوي على `app-debug.apk` / Download **TUN-IPTV-APK** from the
   **Artifacts** section at the bottom of the run page — it contains `app-debug.apk`.

الملف المسؤول عن البناء: `.github/workflows/build-apk.yml`
(JDK 17 + Android SDK + `./gradlew assembleDebug` + رفع الـ APK).

The workflow file is `.github/workflows/build-apk.yml`
(JDK 17 + Android SDK + `./gradlew assembleDebug` + APK upload).

---

## 📱 التثبيت على هاتف Android / Install on an Android phone

1. انقل `app-debug.apk` إلى الهاتف / Copy `app-debug.apk` to the phone.
2. افتح الملف من مدير الملفات / Open it from the file manager.
3. اسمح بـ **"التثبيت من مصادر غير معروفة"** عند الطلب /
   Allow **"Install unknown apps"** when asked.
4. افتح **TUN IPTV** وشاهد / Open **TUN IPTV** and watch.

## 📺 التثبيت على Android TV / Install on Android TV

الطريقة الأسهل هي تطبيق **Downloader** (أو **Send Files to TV**):

The easiest way is the **Downloader** app (or **Send Files to TV**):

1. ثبّت **Downloader** من متجر Google Play على التلفاز /
   Install **Downloader** from Google Play on the TV.
2. افتح Downloader وأدخل رابط الـ APK (أو استخدم **Send Files to TV**
   لنقل الملف من هاتفك) / Open Downloader and enter the APK link
   (or use **Send Files to TV** to transfer it from your phone).
3. ثبّت التطبيق / Install the app.
4. ستظهر **TUN IPTV** في التطبيقات مع شعارها، وتتحكم بالريموت /
   **TUN IPTV** appears in the apps with its banner; control it with the remote.
   (USB ممكن أيضاً: انسخ الـ APK على USB وافتحه من مدير ملفات التلفاز /
   USB also works: copy the APK to a USB stick and open it from the TV file manager.)

---

## 🔧 تغيير قائمة التشغيل الافتراضية / Change the default playlist URL

**من داخل التطبيق / From inside the app:**
زر **"+ Playlist"** → الصق رابط M3U → **Save**. تُحفظ القائمة وتظهر
في منتقي القوائم باسم "Custom".

Tap **"+ Playlist"** → paste an M3U URL → **Save**. It is stored and
appears in the playlist selector as "Custom".

**من الكود / From the code:**
عدّل `app/src/main/java/com/tuniptv/app/data/PlaylistRepository.kt` —
القائمة `DEFAULT_PLAYLISTS` (الرابطان الافتراضيان)، ثم أعد البناء.

Edit `app/src/main/java/com/tuniptv/app/data/PlaylistRepository.kt` —
the `DEFAULT_PLAYLISTS` list (the two default URLs) — then rebuild.

---

## 🗂️ بنية المشروع / Project structure

```
tun-iptv/
├── settings.gradle.kts / build.gradle.kts / gradle.properties
├── gradlew / gradlew.bat / gradle/wrapper/
├── .github/workflows/build-apk.yml
├── README.md
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/tuniptv/app/
        │   ├── TunIptvApp.kt
        │   ├── model/Channel.kt
        │   ├── data/{M3uParser, PlaylistRepository, FavoritesManager, Session}.kt
        │   ├── ui/{MainActivity, MainViewModel, ChannelAdapter, CategoryAdapter, PlayerActivity}.kt
        │   └── tv/{TvMainActivity, TvBrowseFragment, TvCardPresenter, TvPlayerActivity, TvVideoFragment}.kt
        └── res/{values, layout, drawable, mipmap-anydpi-v26}/
```

## 🛠️ بناء محلي (اختياري) / Local build (optional)

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

يتطلب JDK 17 و Android SDK (API 34). / Requires JDK 17 and the Android SDK (API 34).

## ⚠️ ملاحظة / Note

التطبيق يشغّل القوائم التي تضيفها أنت. تأكد من أن لديك الحق في مشاهدة
القنوات التي تضيفها. / The app plays the playlists you add yourself.
Make sure you have the right to watch the channels you add.
