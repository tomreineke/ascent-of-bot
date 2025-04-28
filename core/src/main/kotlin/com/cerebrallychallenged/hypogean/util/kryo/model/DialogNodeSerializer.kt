package com.cerebrallychallenged.hypogean.util.kryo.model

import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

internal class DialogNodeSerializer : Serializer<Dialog.NodeOrEnd>() {
    override fun write(kryo: Kryo, output: Output, obj: Dialog.NodeOrEnd) {
        kryo.writeClassAndObject(output, obj.dialog)
        output.writeInt(if (obj is Dialog.Node) {
            obj.index
        } else {
            -1
        })
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out Dialog.NodeOrEnd>): Dialog.NodeOrEnd {
        val dialog = kryo.readClassAndObject(input) as Dialog
        val id = input.readInt()
        return if (id == -1) {
            dialog.end
        } else {
            dialog.nodeByIndex(id)
        }
    }
}
