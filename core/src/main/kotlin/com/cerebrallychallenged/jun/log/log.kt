package com.cerebrallychallenged.jun.log

//private const val UE_FATAL = 0
private const val UE_ERROR = 1
private const val UE_WARNING = 2
//private const val UE_DISPLAY = 3
private const val UE_LOG = 4
private const val UE_VERBOSE = 5
//private const val UE_VERY_VERBOSE = 6

@Suppress("ClassName")
class log {
    companion object {
        private const val LogLevel = UE_LOG

        const val isTraceEnabled: Boolean = LogLevel >= UE_VERBOSE

        const val isInfoEnabled: Boolean = LogLevel >= UE_LOG

        const val isWarnEnabled: Boolean = LogLevel >= UE_WARNING

        const val isErrorEnabled: Boolean = LogLevel >= UE_ERROR

        fun trace(message: String) {
            if (isTraceEnabled) {
                println(message)
                log(UE_VERBOSE, message)
            }
        }

        inline fun trace(f: () -> String) {
            if (isTraceEnabled) {
                trace(f())
            }
        }

        @JvmStatic
        fun info(message: String) {
            if (isInfoEnabled) {
                println(message)
                log(UE_LOG, message)
            }
        }

        inline fun info(f: () -> String) {
            if (isInfoEnabled) {
                info(f())
            }
        }

        @JvmStatic
        fun warn(message: String) {
            if (isWarnEnabled) {
                println(message)
                log(UE_WARNING, message)
            }
        }

        inline fun warn(f: () -> String) {
            if (isWarnEnabled) {
                warn(f())
            }
        }

        @JvmStatic
        fun error(message: String) {
            if (isErrorEnabled) {
                println(message)
                log(UE_ERROR, message)
            }
        }

        inline fun error(f: () -> String) {
            if (isErrorEnabled) {
                error(f())
            }
        }
    }
}

private external fun log(level: Int, message: String)
