package com.mandarinsteps.app

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private val ink = Color.rgb(29, 48, 39)
    private val muted = Color.rgb(73, 92, 81)
    private val paper = Color.rgb(244, 246, 235)
    private val green = Color.rgb(26, 83, 58)
    private val pale = Color.rgb(222, 236, 216)
    private val progress by lazy { getSharedPreferences("lesson_progress", MODE_PRIVATE) }
    private lateinit var body: LinearLayout
    private var speech: TextToSpeech? = null
    private var audioReady = false
    private var lesson: Lesson? = null
    private var phraseIndex = 0
    private var questionIndex = 0
    private var score = 0
    private var selectedAnswer: Int? = null
    private var screen = "home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lesson = lessons.find { it.id == savedInstanceState?.getString("lesson") }
        phraseIndex = savedInstanceState?.getInt("phrase") ?: 0
        questionIndex = savedInstanceState?.getInt("question") ?: 0
        score = savedInstanceState?.getInt("score") ?: 0
        selectedAnswer = savedInstanceState?.getInt("answer", -1)?.takeIf { it >= 0 }
        speech = TextToSpeech(this) { status -> if (status == TextToSpeech.SUCCESS) audioReady = true }
        val current = lesson
        when {
            current == null -> showHome()
            savedInstanceState?.getString("screen") == "quiz" -> showQuiz(current)
            savedInstanceState?.getString("screen") == "result" -> showResult(current)
            else -> showPhrase(current)
        }
    }

    override fun onSaveInstanceState(out: Bundle) {
        out.putString("lesson", lesson?.id)
        out.putString("screen", screen)
        out.putInt("phrase", phraseIndex)
        out.putInt("question", questionIndex)
        out.putInt("score", score)
        out.putInt("answer", selectedAnswer ?: -1)
        super.onSaveInstanceState(out)
    }

    override fun onBackPressed() {
        if (screen == "home") super.onBackPressed() else showHome()
    }

    override fun onDestroy() {
        speech?.stop()
        speech?.shutdown()
        super.onDestroy()
    }

    private fun showHome() {
        screen = "home"
        speech?.stop()
        lesson = null
        page()
        label("Mandarin Steps", 38, ink, bold = true)
        label("Learn everyday Mandarin, one phrase at a time.", 17, muted)
        val completed = lessons.count { progress.getBoolean(it.id, false) }
        label("$completed of ${lessons.size} lessons mastered", 15, green, bold = true)
        lessons.forEach { item ->
            val panel = card()
            label(item.title, 23, ink, panel, true)
            label(item.summary, 16, muted, panel)
            val done = progress.getBoolean(item.id, false)
            action(if (done) "Review ${item.title}" else "Start ${item.title}", panel) {
                lesson = item
                phraseIndex = 0
                showPhrase(item)
            }
        }
    }

    private fun showPhrase(item: Lesson) {
        screen = "lesson"
        page()
        action("All lessons", primary = false, onClick = ::showHome)
        label(item.title, 32, ink, bold = true)
        label("Phrase ${phraseIndex + 1} of ${item.phrases.size}", 15, green, bold = true)
        val phrase = item.phrases[phraseIndex]
        val panel = card()
        label(phrase.chinese, 42, ink, panel, true)
        label(phrase.pinyin, 23, green, panel)
        label(phrase.english, 19, muted, panel)
        action("Listen in Mandarin", panel, primary = false) { speak(phrase.chinese) }
        action(if (phraseIndex == item.phrases.lastIndex) "Start quiz" else "Next phrase") {
            speech?.stop()
            if (phraseIndex == item.phrases.lastIndex) startQuiz(item)
            else {
                phraseIndex++
                showPhrase(item)
            }
        }
        if (phraseIndex > 0) action("Previous phrase", primary = false) {
            speech?.stop()
            phraseIndex--
            showPhrase(item)
        }
    }

    private fun startQuiz(item: Lesson) {
        questionIndex = 0
        score = 0
        selectedAnswer = null
        showQuiz(item)
    }

    private fun showQuiz(item: Lesson) {
        screen = "quiz"
        page()
        action("All lessons", primary = false, onClick = ::showHome)
        label("Practice", 34, ink, bold = true)
        label("Question ${questionIndex + 1} of ${item.phrases.size}", 15, green, bold = true)
        val phrase = item.phrases[questionIndex]
        val panel = card()
        label("What does this mean?", 17, muted, panel)
        label(phrase.chinese, 36, ink, panel, true)
        label(phrase.pinyin, 21, green, panel)
        val offset = 1
        val answer = selectedAnswer
        item.phrases.indices.forEach { position ->
            val option = (position + offset) % item.phrases.size
            val button = action(item.phrases[option].english, panel, primary = false) {
                selectedAnswer = option
                if (option == questionIndex) score++
                showQuiz(item)
            }
            button.isEnabled = answer == null
        }
        if (answer != null) {
            val feedback = if (answer == questionIndex) "Correct!" else "Correct answer: ${phrase.english}"
            label(feedback, 18, ink, bold = true).apply {
                accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE
                announceForAccessibility(feedback)
            }
            val last = questionIndex == item.phrases.lastIndex
            action(if (last) "See results" else "Next question") {
                if (last) {
                    if (score == item.phrases.size) progress.edit().putBoolean(item.id, true).apply()
                    showResult(item)
                } else {
                    questionIndex++
                    selectedAnswer = null
                    showQuiz(item)
                }
            }
        }
    }

    private fun showResult(item: Lesson) {
        screen = "result"
        page()
        label("Quiz results", 34, ink, bold = true)
        val panel = card()
        label("$score / ${item.phrases.size} correct", 28, ink, panel, true)
        label(if (score == item.phrases.size) "Lesson mastered. Progress saved."
            else "Review the phrases or try the quiz again.", 17, muted, panel)
        action("All lessons", onClick = ::showHome)
        action("Review phrases", primary = false) {
            phraseIndex = 0
            showPhrase(item)
        }
        action("Try quiz again", primary = false) { startQuiz(item) }
    }

    private fun speak(text: String) {
        val engine = speech
        if (engine == null || !audioReady) {
            toast("Mandarin audio is unavailable. Check your device’s text-to-speech settings.")
            return
        }
        val voice = engine.voices.orEmpty().find {
            it.locale.language == "zh" && it.locale.country == "CN" &&
                !it.isNetworkConnectionRequired && "notInstalled" !in it.features
        }
        if (voice == null || engine.setVoice(voice) != TextToSpeech.SUCCESS ||
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, text) != TextToSpeech.SUCCESS) {
            toast("Install an offline Mandarin (China) voice in your device’s text-to-speech settings.")
        }
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    private fun page() {
        val scroll = ScrollView(this).apply {
            setBackgroundColor(paper)
            clipToPadding = false
            isFillViewport = true
        }
        body = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(20), dp(22), dp(24))
        }
        scroll.setOnApplyWindowInsetsListener { view, insets ->
            val bars = if (android.os.Build.VERSION.SDK_INT >= 30) {
                val modern = insets.getInsets(android.view.WindowInsets.Type.systemBars() or
                    android.view.WindowInsets.Type.displayCutout())
                android.graphics.Rect(modern.left, modern.top, modern.right, modern.bottom)
            } else {
                @Suppress("DEPRECATION")
                android.graphics.Rect(insets.systemWindowInsetLeft, insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
            }
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        scroll.addView(body)
        setContentView(scroll)
        scroll.requestApplyInsets()
    }

    private fun card(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = rounded(Color.WHITE, 22)
        setPadding(dp(18), dp(14), dp(18), dp(10))
        body.addView(this, row(9))
    }

    private fun label(
        text: String, size: Int, color: Int, parent: LinearLayout = body, bold: Boolean = false
    ): TextView = TextView(this).apply {
        this.text = text
        textSize = size.toFloat()
        setTextColor(color)
        if (bold) setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        parent.addView(this, row(7))
    }

    private fun action(
        text: String, parent: LinearLayout = body, primary: Boolean = true, onClick: () -> Unit
    ): Button = Button(this).apply {
        this.text = text
        isAllCaps = false
        textSize = 16f
        minimumHeight = dp(50)
        val foreground = if (primary) Color.WHITE else green
        val shape = rounded(if (primary) green else pale, 16)
        background = android.graphics.drawable.RippleDrawable(
            ColorStateList.valueOf(0x332e7351), shape, null
        )
        setTextColor(foreground)
        setOnClickListener { onClick() }
        parent.addView(this, row(5))
    }

    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
    }

    private fun row(gap: Int) = LinearLayout.LayoutParams(-1, -2).apply {
        topMargin = dp(gap)
        bottomMargin = dp(gap)
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density + 0.5f).toInt()
}
