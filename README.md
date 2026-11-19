# Mandarin Steps

Mandarin Steps is a small native Kotlin Android app for beginner Mandarin. It is a new implementation inspired by the Chinese Learning Application described in Ali Mansoor's résumé.

## Try it

Install the accompanying debug APK on an Android 6.0 or newer test device. For source development, open this folder in Android Studio, select JDK 17 for Gradle, install Android SDK Platform 35 when prompted, and run the app module on a device or emulator. On Windows, the command-line build is `gradlew.bat assembleDebug`.

The app includes greetings, numbers, and café phrases. Each lesson shows simplified Chinese, pinyin, and English. Each quiz gives immediate feedback. A perfect score marks the lesson as mastered and saves that status on the device.

Lessons, quizzes, and saved progress work offline. Pronunciation uses the device's text-to-speech engine and requires an installed offline Mandarin (China) voice. Download the voice through your device's text-to-speech settings before going offline. The app requires no account or network permission.

## Demo walkthrough

Open First greetings, play a phrase, and complete the quiz. Return to All lessons and confirm the mastery count increased. Close and reopen the app, then enable airplane mode and repeat a lesson to demonstrate local content, progress, and pronunciation with the voice installed.
