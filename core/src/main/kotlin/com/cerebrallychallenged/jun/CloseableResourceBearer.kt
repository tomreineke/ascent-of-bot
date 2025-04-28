package com.cerebrallychallenged.jun

import com.cerebrallychallenged.jun.skiatree.CloseableResource
import java.lang.foreign.MemorySegment

abstract class CloseableResourceBearer(internal val resource: CloseableResource) : AutoCloseable by resource

internal val CloseableResourceBearer.address: MemorySegment
    get() = resource.address

internal val CloseableResourceBearer?.nullableAddress: MemorySegment
    get() = this?.resource?.address ?: MemorySegment.NULL
