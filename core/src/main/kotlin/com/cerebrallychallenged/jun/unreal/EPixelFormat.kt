package com.cerebrallychallenged.jun.unreal

@Suppress("unused")
enum class EPixelFormat {
    Unknown,
    A32B32G32R32F,
    B8G8R8A8,
    G8,
    G16,
    DXT1,
    DXT3,
    DXT5,
    UYVY,
    FLOAT_RGB,
    FloatRGBA,
    DepthStencil,
    ShadowDepth,
    R32_FLOAT,
    G16R16,
    G16R16F,
    G16R16F_FILTER,
    G32R32F,
    A2B10G10R10,
    A16B16G16R16,
    D24,
    R16F,
    R16F_FILTER,
    BC5,
    V8U8,
    A1,
    FloatR11G11B10,
    A8,
    R32_UINT,
    R32_SINT,
    PVRTC2,
    PVRTC4,
    R16_UINT,
    R16_SINT,
    R16G16B16A16_UINT,
    R16G16B16A16_SINT,
    R5G6B5_UNORM,
    R8G8B8A8,
    A8R8G8B8, // Only used for legacy loading; do NOT use!
    BC4,
    R8G8,
    ATC_RGB,
    ATC_RGBA_E,
    ATC_RGBA_I,
    X24_G8,	// Used for creating SRVs to alias a DepthStencil buffer to read Stencil. Don't use for creating textures.
    ETC1,
    ETC2_RGB,
    ETC2_RGBA,
    R32G32B32A32_UINT,
    R16G16_UINT,
    ASTC_4x4,	// 8.00 bpp
    ASTC_6x6,	// 3.56 bpp
    ASTC_8x8,	// 2.00 bpp
    ASTC_10x10,	// 1.28 bpp
    ASTC_12x12,	// 0.89 bpp
    BC6H,
    BC7,
    R8_UINT,
    L8,
    XGXR8,
    R8G8B8A8_UINT,
    R8G8B8A8_SNORM,
    R16G16B16A16_UNORM,
    R16G16B16A16_SNORM,
    PLATFORM_HDR_0,
    PLATFORM_HDR_1,	// Reserved.
    PLATFORM_HDR_2,	// Reserved.
    NV12,
    R32G32_UINT,
    MAX
}