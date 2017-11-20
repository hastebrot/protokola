package protokola

inline fun <T, R> T.runWith(block: (T) -> R): R = run(block)
inline fun <T> T.applyWith(block: (T) -> Unit): T = apply(block)

inline val <T> T.println get() = println(this)
