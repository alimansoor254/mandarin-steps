# Mandarin Steps

Mandarin Steps is a native Kotlin Android app for beginner Mandarin. It pairs simplified Chinese with pinyin and English and lets you practice offline. This is a new implementation inspired by my university Chinese learning project.

[Try a sample question](#try-a-sample-question) · [Run the app](#run-the-app) · [Explore the code](#explore-the-code)

## Try a sample question

What does **谢谢** (xièxie) mean?

- A. Hello
- B. Thank you
- C. Goodbye

<details>
<summary>Reveal the answer</summary>

**B. Thank you.**

</details>

## What the app does

- Guides you through greetings, numbers 1–4, and café phrases.
- Plays pronunciation using an installed offline Mandarin (China) text-to-speech voice.
- Gives immediate feedback in quizzes and saves mastery on the device after a perfect score.

## Run the app

Open the repository folder in Android Studio. Use JDK 17 for Gradle and install Android SDK Platform 35 if prompted. Run the app module on an Android 6.0+ device or emulator. On Windows, build with `gradlew.bat assembleDebug`.

Lessons, quizzes, and saved progress work offline. To use pronunciation in airplane mode, first install an offline Mandarin (China) voice in your device's text-to-speech settings. The app requires no account or network permission.

## Explore the code

- [Lesson data](app/src/main/java/com/mandarinsteps/app/Lessons.kt) defines each phrase and its pinyin and English translation.
- [Main activity](app/src/main/java/com/mandarinsteps/app/MainActivity.kt) handles lesson navigation, quiz feedback, locally saved progress, and text-to-speech.

For a quick demo, open First greetings, play a phrase, and finish the quiz. Close and reopen the app in airplane mode to see that the lesson still opens and your mastery is saved.
