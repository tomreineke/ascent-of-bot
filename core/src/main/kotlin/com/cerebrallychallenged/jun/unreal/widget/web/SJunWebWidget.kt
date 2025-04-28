package com.cerebrallychallenged.jun.unreal.widget.web

//import com.cerebrallychallenged.jun.unreal.AnyRef
//import com.cerebrallychallenged.jun.unreal.TSharedRef
//import com.cerebrallychallenged.jun.unreal.widget.SCompoundWidget
//import com.cerebrallychallenged.jun.util.CPointer
//import com.cerebrallychallenged.jun.wrapSharedRef
//
//interface SJunWebWidget : SCompoundWidget {
//    companion object {
//        fun createBySNew(handler: JunWebWidgetHandler, isHudTransparent: Boolean, contentsToLoad: String? = null): TSharedRef<SJunWebWidget>
//                = createBySNewImpl(JunWebWidgetHandlerJNI(handler), isHudTransparent, contentsToLoad).wrapSharedRef()
//    }
//}
//
//interface JunWebWidgetHandler {
//    fun onLoadUrl(method: String, url: String): String?
//}
//
//private class JunWebWidgetHandlerJNI(private val handler: JunWebWidgetHandler) {
//    @Suppress("unused") // Called from JNI.
//    fun onLoadUrl(method: String, url: String): String? = handler.onLoadUrl(method, url)
//}
//
//private external fun createBySNewImpl(
//        handler: JunWebWidgetHandlerJNI,
//        isHudTransparent: Boolean,
//        contentsToLoad: String?
//): CPointer
//
//fun AnyRef<SJunWebWidget>.loadURL(url: String) {
//    loadURL(directPtr, url)
//}
//
//private external fun loadURL(ptr: CPointer, url: String)