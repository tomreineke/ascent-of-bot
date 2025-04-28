package com.cerebrallychallenged.hypogean.view

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.CompositeParameter

class CompositeAssets : SimpleObjectRegistry<CompositeAsset>()

class CompositeParameters : SimpleObjectRegistry<CompositeParameter<*>>()
