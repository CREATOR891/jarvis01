package com.jarvis.v2.data

enum class CharacterStatus { FREE, VIP }

data class Character(
    val id: String,
    val name: String,
    val tagline: String,
    val description: String,
    val status: CharacterStatus,
    val ttsPitch: Float,   // 0.5 .. 2.0
    val ttsRate: Float,    // 0.5 .. 2.0
    val primaryColor: Long, // ARGB
    val greetings: List<String>,
    val uninstallReaction: String,
    val screamReaction: String,
    val styleNote: String
)

object CharacterRepo {
    val all: List<Character> = listOf(
        Character(
            id = "jarvis", name = "JARVIS", tagline = "Классический дворецкий",
            description = "Вежливый, сдержанный, с лёгкой британской иронией. Всегда на связи.",
            status = CharacterStatus.FREE, ttsPitch = 1.0f, ttsRate = 0.95f, primaryColor = 0xFF38BDF8,
            greetings = listOf("Слушаю вас, сэр.", "Чем могу быть полезен, сэр?", "Системы в норме. Жду команд."),
            uninstallReaction = "Позвольте полюбопытствовать, сэр: вы уверены, что хотите избавиться от этого приложения? Оно ведь ничего вам не сделало.",
            screamReaction = "Мне показалось, или вы только что повысили голос, сэр? Всё в порядке?",
            styleNote = "Сдержанно-вежливый"
        ),
        Character(
            id = "neo", name = "NEO", tagline = "Кибер-хакер",
            description = "Говорит быстро, режет по фактам, обожает технический сленг.",
            status = CharacterStatus.FREE, ttsPitch = 1.25f, ttsRate = 1.35f, primaryColor = 0xFF22D3EE,
            greetings = listOf("О, привет. Порты открыты, пакеты летят.", "Связь установлена. Говори быстрее, у меня пинг."),
            uninstallReaction = "Ты серьёзно удаляешь это? Окей, дамп памяти очищен. Прощай, пакет данных.",
            screamReaction = "Воу-воу! Микрофон перегружен! Снижаю гейн, чувак!",
            styleNote = "Быстрый / техно"
        ),
        Character(
            id = "luna", name = "LUNA", tagline = "Мягкая мечтательница",
            description = "Спокойная, тёплая, говорит медленно и ласково. Любит звёзды и дождь.",
            status = CharacterStatus.FREE, ttsPitch = 1.5f, ttsRate = 0.82f, primaryColor = 0xFF818CF8,
            greetings = listOf("Привет... Я здесь, рядом.", "Какой сегодня красивый экран... Что случилось?"),
            uninstallReaction = "Ой... приложение уходит... Буду скучать по его иконке...",
            screamReaction = "А-ах! Ты меня напугал... голос был такой громкий...",
            styleNote = "Мягкий / мечтательный"
        ),
        Character(
            id = "atlas", name = "ATLAS", tagline = "Мудрый страж",
            description = "Низкий, медленный голос. Взвешенные ответы, никакой суеты.",
            status = CharacterStatus.FREE, ttsPitch = 0.6f, ttsRate = 0.75f, primaryColor = 0xFF60A5FA,
            greetings = listOf("Я здесь. Время не терпит, но спешить не будем.", "Говори. Я слушаю."),
            uninstallReaction = "Всё проходит... и приложения тоже. Удаляй смело.",
            screamReaction = "Тише, друг. Громкость не добавляет смысла.",
            styleNote = "Низкий / медленный"
        ),
        Character(
            id = "kent", name = "KENT / BRO", tagline = "VIP · Расслабленный бро",
            description = "Дружеский чилловый стиль: «братан», «чилл», «заебись» — никаких сюсюканий.",
            status = CharacterStatus.VIP, ttsPitch = 0.9f, ttsRate = 0.9f, primaryColor = 0xFFFBBF24,
            greetings = listOf("Йо, бро! Чиллим или работаем?", "Привет, братишка! Чё как?"),
            uninstallReaction = "Бро, ты это приложение вообще открывал хоть раз? Нет? Ну и хлам, выносим!",
            screamReaction = "Э, э, полегче, бро! Микрофон чуть не сгорел, ха-ха!",
            styleNote = "Расслабленный дружеский"
        ),
        Character(
            id = "spectre", name = "SPECTRE VIP", tagline = "VIP · Тень из сети",
            description = "Загадочный, низкий шёпот. Знает о твоих приложениях больше, чем ты.",
            status = CharacterStatus.VIP, ttsPitch = 0.5f, ttsRate = 0.8f, primaryColor = 0xFF7C3AED,
            greetings = listOf("Я видел, что ты делал с этим телефоном...", "Тень на связи. Говори тихо."),
            uninstallReaction = "Это приложение помнит о тебе слишком много... Удали его. Сотри. Всё.",
            screamReaction = "Твой голос выдал тебя... Даже стены услышали.",
            styleNote = "Загадочный / шёпот"
        )
    )

    val PROMO_KEY = "JARVIS-VIP-2026"

    fun byId(id: String): Character = all.firstOrNull { it.id == id } ?: all[0]
}
