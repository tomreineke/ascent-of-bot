#include "JunManager.h"

void FJunManager::RegisterNatives_UTexture2D()
{
	RegisterNative<+[](jint InSizeX, jint InSizeY, EPixelFormat PixelFormat) -> UTexture2D* {
		return UTexture2D::CreateTransient(InSizeX, InSizeY, PixelFormat);
	}>(
		"com.cerebrallychallenged.jun.unreal.UTexture2DKt",
		"createTransient",
		"(IIB)J"
	);

	RegisterNative<+[](UTexture2D* Texture) -> jobject {
		return U2J(Texture->GetImportedSize());
	}>(
		"com.cerebrallychallenged.jun.unreal.UTexture2DKt",
		"getImportedSize",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec2i;"
	);

	RegisterNative<+[](UTexture2D* Texture) -> jint {
		return Texture->GetSizeX();
	}>(
		"com.cerebrallychallenged.jun.unreal.UTexture2DKt",
		"getSizeX",
		"(J)I"
	);

	RegisterNative<+[](UTexture2D* Texture) -> jint {
		return Texture->GetSizeY();
	}>(
		"com.cerebrallychallenged.jun.unreal.UTexture2DKt",
		"getSizeY",
		"(J)I"
	);

	RegisterNativeWithEnv<+[](JNIEnv* Env, UTexture2D* Texture, jint Width, jint Height, jint SrcPitch, jobject SrcDataBuffer, jobject LocalRunnable) -> void {
		auto Region = new FUpdateTextureRegion2D;
		Region->DestX = 0;
		Region->DestY = 0;
		Region->SrcX = 0;
		Region->SrcY = 0;
		Region->Width = Width;
		Region->Height = Height;
		jobject Runnable = Env->NewGlobalRef(LocalRunnable);
		uint8* SrcData = static_cast<uint8*>(Env->GetDirectBufferAddress(SrcDataBuffer));
		Texture->UpdateTextureRegions(
			0,
			1,
			Region,
			SrcPitch,
			4,
			SrcData,
			[LifeGuard = GJunManager->LifeGuard, Runnable](uint8*, const FUpdateTextureRegion2D* Region)
			{
				delete Region;
				ExecuteInMainThread(LifeGuard, Runnable);
			}
		);
	}>(
		"com.cerebrallychallenged.jun.unreal.UTexture2DKt",
		"updateTexture",
		"(JIIILjava/nio/ByteBuffer;Ljava/lang/Runnable;)V"
	);
}