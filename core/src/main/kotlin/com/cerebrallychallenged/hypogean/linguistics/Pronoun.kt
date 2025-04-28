package com.cerebrallychallenged.hypogean.linguistics

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.attribute.attribute

class Pronoun(val nominative: String, val accusative: String)

var Entity.pronoun: Pronoun by attribute(Pronoun("it", "it"))
