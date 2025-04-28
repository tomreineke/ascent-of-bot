package com.cerebrallychallenged.jun.unreal.widget.web

//import com.cerebrallychallenged.jun.Convenience
//import com.cerebrallychallenged.jun.LifeTime
//import com.cerebrallychallenged.jun.unreal.AnyRef
//import com.cerebrallychallenged.jun.unreal.DirectRef
//import com.cerebrallychallenged.jun.util.CPointer
//
//interface IWebBrowserSingleton {
//    companion object {
//        @Convenience
//        fun get(lifeTime: LifeTime): DirectRef<IWebBrowserSingleton> = DirectRef(get(), lifeTime)
//    }
//}
//
//var AnyRef<IWebBrowserSingleton>.isDevToolsShortcutEnabled: Boolean
//    get() = getDevToolsShortcutEnabled(directPtr)
//    set(value) {
//        setDevToolsShortcutEnabled(directPtr, value)
//    }
//
//private external fun get(): CPointer
//
//private external fun getDevToolsShortcutEnabled(ptr: CPointer): Boolean
//
//private external fun setDevToolsShortcutEnabled(ptr: CPointer, value: Boolean)