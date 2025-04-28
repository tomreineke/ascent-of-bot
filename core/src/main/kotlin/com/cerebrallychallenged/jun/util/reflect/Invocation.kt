package com.cerebrallychallenged.jun.util.reflect

@PublishedApi
internal fun Class<*>.invokeDynamic(
        obj: Any?,
        methodName: String,
        paramTypes: Array<Class<*>>,
        params: Array<Any?>
): Any? = getDeclaredMethod(methodName, *paramTypes).invoke(obj, *params)

fun Any.invokeDynamic(methodName: String): Any? = javaClass.invokeDynamic(this, methodName, arrayOf(), arrayOf())

inline fun <reified P1 : Any?> Any.invokeDynamic(methodName: String, param1: P1): Any? =
        javaClass.invokeDynamic(this, methodName, arrayOf(P1::class.java), arrayOf(param1))

inline fun <reified P1 : Any?, reified P2: Any?> Any.invokeDynamic(
        methodName: String,
        param1: P1,
        param2: P2
): Any? = javaClass.invokeDynamic(
        this,
        methodName,
        arrayOf(P1::class.java, P2::class.java),
        arrayOf(param1, param2)
)

inline fun <reified P1 : Any?, reified P2: Any?, reified P3: Any?> Any.invoke(
        methodName: String,
        param1: P1,
        param2: P2,
        param3: P3
): Any? = javaClass.invokeDynamic(
        this,
        methodName,
        arrayOf(P1::class.java, P2::class.java, P3::class.java),
        arrayOf(param1, param2, param3)
)

fun Class<*>.invokeDynamic(methodName: String): Any? = invokeDynamic(null, methodName, arrayOf(), arrayOf())

inline fun <reified P1 : Any?> Class<*>.invokeDynamic(methodName: String, param1: P1): Any? =
        invokeDynamic(null, methodName, arrayOf(P1::class.java), arrayOf(param1))

inline fun <reified P1 : Any?, reified P2 : Any?> Class<*>.invokeDynamic(
        methodName: String,
        param1: P1,
        param2: P2
): Any? = invokeDynamic(null, methodName, arrayOf(P1::class.java, P2::class.java), arrayOf(param1, param2))

inline fun <reified P1 : Any?, reified P2 : Any?, reified P3: Any?> Class<*>.invokeDynamic(
        methodName: String,
        param1: P1,
        param2: P2,
        param3: P3
): Any? = invokeDynamic(
        null,
        methodName,
        arrayOf(P1::class.java, P2::class.java, P3::class.java),
        arrayOf(param1, param2, param3)
)