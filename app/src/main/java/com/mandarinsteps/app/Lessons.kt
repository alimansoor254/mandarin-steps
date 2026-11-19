package com.mandarinsteps.app

data class Phrase(val chinese: String, val pinyin: String, val english: String)
data class Lesson(val id: String, val title: String, val summary: String, val phrases: List<Phrase>)

val lessons = listOf(
    Lesson("greetings", "First greetings", "Say hello, thank someone, and say goodbye.", listOf(
        Phrase("你好", "nǐ hǎo", "Hello"),
        Phrase("谢谢", "xièxie", "Thank you"),
        Phrase("再见", "zàijiàn", "Goodbye"),
        Phrase("对不起", "duìbuqǐ", "I'm sorry")
    )),
    Lesson("numbers", "Numbers 1–4", "Recognize and say your first four numbers.", listOf(
        Phrase("一", "yī", "One"),
        Phrase("二", "èr", "Two"),
        Phrase("三", "sān", "Three"),
        Phrase("四", "sì", "Four")
    )),
    Lesson("cafe", "At a café", "Practice ordering something to drink.", listOf(
        Phrase("我想喝水", "wǒ xiǎng hē shuǐ", "I'd like to drink water"),
        Phrase("一杯茶", "yì bēi chá", "A cup of tea"),
        Phrase("一杯咖啡", "yì bēi kāfēi", "A cup of coffee"),
        Phrase("多少钱？", "duōshao qián?", "How much is it?")
    ))
)
