package com.cerebrallychallenged.jun.unreal.widget.web

//import com.cerebrallychallenged.jun.unreal.AnyRef
//import com.cerebrallychallenged.jun.unreal.TSharedRef
//import com.cerebrallychallenged.jun.unreal.widget.SCompoundWidget
//import com.cerebrallychallenged.jun.util.CPointer
//import com.cerebrallychallenged.jun.wrapSharedRef
//
//interface SWebBrowser : SCompoundWidget {
//    companion object {
//        fun createBySNew(): TSharedRef<SWebBrowser> = createBySNewImpl().wrapSharedRef()
//    }
//}
//
//fun AnyRef<SWebBrowser>.loadURL(url: String) {
//    loadURL(directPtr, url)
//}
//
//private external fun createBySNewImpl(): CPointer
//
//private external fun loadURL(ptr: CPointer, url: String)
