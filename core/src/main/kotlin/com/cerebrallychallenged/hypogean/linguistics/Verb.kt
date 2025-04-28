package com.cerebrallychallenged.hypogean.linguistics

import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.attribute.attribute

class Verb(val infinitive: String, val thirdPersonSingular: String, val thirdPersonPlural: String)

var Item.verb: Verb by attribute(Verb("target", "targets", "target"))
