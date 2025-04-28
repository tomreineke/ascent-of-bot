package com.cerebrallychallenged.hypogean.util.stream

import com.cerebrallychallenged.hypogean.modding.IdRegistry
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.EntityType
import com.cerebrallychallenged.hypogean.model.RulebookContext
import com.cerebrallychallenged.hypogean.model.WorldContext
import com.cerebrallychallenged.hypogean.model.attribute.Attribute
import com.cerebrallychallenged.hypogean.model.containment.ContainerPosition
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.math.geo.readVec2i
import com.cerebrallychallenged.jun.math.geo.readVec4f
import com.cerebrallychallenged.jun.math.geo.writeVec2i
import com.cerebrallychallenged.jun.math.geo.writeVec4f
import com.cerebrallychallenged.jun.stream.readString
import com.cerebrallychallenged.jun.stream.writeString
import com.cerebrallychallenged.jun.unreal.UObject
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import java.io.DataInput
import java.io.DataOutput
import java.io.IOException
import kotlin.reflect.jvm.jvmName

fun DataOutput.writeEntity(entity: Entity) = writeInt(entity.id)

context(WorldContext)
inline fun <reified T : Entity> DataInput.readEntity(): T = world.byId(readInt())

fun DataOutput.writeOptionalEntity(entity: Entity?) = writeInt(entity?.id ?: 0)

context(WorldContext)
inline fun <reified T: Entity> DataInput.readOptionalEntity(): T? {
    val id = readInt()
    if (id == 0) return null
    val entity = world.entityById(id)
    return entity as? T ?: throw IOException("Read entity $entity is no ${T::class.simpleName}")
}

fun DataOutput.writeEntityType(entityType: EntityType<*>) {
    writeString(entityType.id)
}

fun DataOutput.writeEntityType(entity: Entity) {
    writeString(entity::class.jvmName)
}

context(RulebookContext)
inline fun <reified T : Entity> DataInput.readEntityType(): EntityType<T> {
    val id = readString()
    val entityType: EntityType<Entity> = rulebook.entityTypes[id]
    return entityType.asSubTypeOf() ?: throw IOException("Entity type '$id' is no subclass of ${T::class}")
}

fun DataOutput.writeAttribute(attribute: Attribute<*>) {
    writeInt(attribute.id)
}

context(RulebookContext)
fun DataInput.readAttribute(): Attribute<*> = rulebook.attributes[readInt()]

fun DataOutput.writeContainerPosition(containerPosition: ContainerPosition) {
    writeEntity(containerPosition.container)
    writeVec2i(containerPosition.boxPosition)
}

context(WorldContext)
fun DataInput.readContainerPosition(): ContainerPosition = ContainerPosition(
    readEntity(),
    readVec2i()
)

fun <T : Any> DataOutput.writeImplementor(value: T, feature: IdRegistry<T>) {
    writeString(feature.idForItem(value))
}

fun <T : Any> DataInput.readImplementor(feature: IdRegistry<T>): T = feature.itemForId(readString())


fun DataOutput.writeUnrealRef(unrealRef: UnrealRef<*>) {
    writeString(unrealRef.path)
}

fun <T : UObject> DataInput.readUnrealRef(): UnrealRef<T> = UnrealRef(readString())

fun DataOutput.writeLinearColor(color: FLinearColor) {
    writeVec4f(color.rgba)
}

fun DataInput.readLinearColor(): FLinearColor = FLinearColor.rgba(readVec4f())
