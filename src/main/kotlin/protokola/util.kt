package protokola

inline fun <T, R> T.runWith(block: (T) -> R): R = run(block)
inline fun <T> T.applyWith(block: (T) -> Unit): T = apply(block)

inline val <T> T.println get() = println(this)

inline fun <R> demo(text: String? = null,
                    block: () -> R): R {
    if (text != null) {
        println("--demo: $text")
    }
    return run(block)
}
