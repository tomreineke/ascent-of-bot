# Ascent of Bot — Backlog

This document tracks all outstanding TODOs and FIXMEs from the codebase, organized by domain. Each item is cross-referenced to its source location and classified by priority/category.

---

## UI/Animation & Visual

### Animation Emissions (High Priority)
The action animation pipeline is incomplete. Actions should emit animation events to be picked up by the renderer, but several actions still hardcode placeholder delays or lack animation support.

- **[AdjacentActionInstance.kt:34](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/actions/AdjacentActionInstance.kt)** — Emit `MeleeAction` animation event when performing adjacent action
- **[GrapplingAttackAction.kt:183-192](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/actions/GrapplingAttackAction.kt)** — Emit `GroundMove` and `MeleeAction` animations for grappling sequence
- **[HomingShotAction.kt:55,61](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/actions/HomingShotAction.kt)** — Emit `ActorAim` and `ProjectileMove` animations
- **[RoundhouseKickAction.kt:64](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/actions/RoundhouseKickAction.kt)** — Implement `hitAnimation` for roundhouse kick

### ViewModel Selection State
- **[ViewModel.kt:192](core/src/main/kotlin/com/cerebrallychallenged/hypogean/view/ViewModel.kt)** — `ActivateActor` event causes action bar to forget that an action has been selected. May need to preserve action selection state across actor activation or emit separate events.

### Text & Image Rendering
- **[RichTextUtil.kt:71](core/src/main/kotlin/com/cerebrallychallenged/hypogean/view/util/RichTextUtil.kt)** — Image size in rich text should scale with font size instead of being fixed
- **[Report.kt:123](core/src/main/kotlin/com/cerebrallychallenged/hypogean/view/report/Report.kt)** — Create dedicated `Report` subclass for direct-speech dialogue rendering (currently mixed with regular reports)

### Menu Systems

**Radial Menu Status (Partially Implemented)**

- **PickupAction multi-select** ✅ DONE — When multiple items are in a cell, a vertical button menu appears at the cursor. Implemented in `RadialMenuView.kt` and triggered by `PickupRadialViewDisplay` event from `ViewModel.kt:324`.
- **[ActionButton.kt:54](core/src/main/kotlin/com/cerebrallychallenged/hypogean/view/actionbar/ActionButton.kt)** — Radial menu for other intransitive multi-option actions. Currently only PickupAction uses the menu pattern. If other intransitive actions with multiple variants exist, they should follow the same pattern (currently errors at `ViewModel.kt:327`).
- **[ContainerInventoryView.kt:18](core/src/main/kotlin/com/cerebrallychallenged/hypogean/view/modular/views/ContainerInventoryView.kt)** — Pattern could be reused for future `TradingView` UI component

---

## Graphics & Asset Management

### Item Icon Placeholders (Cosmetic)
The following utility items use a placeholder `EnergyShield` icon and need proper graphics:

- **[BluntDamageNegator.kt:16](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/items/utility/BluntDamageNegator.kt)**
- **[BasicFireProtector.kt:17](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/items/utility/BasicFireProtector.kt)**
- **[BasicExplosionProtector.kt:16](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/items/utility/BasicExplosionProtector.kt)**

---

## Game Mechanics & Simulation

### Movement & Terrain

- **[SimpleMovementGraph.kt:37](core/src/main/kotlin/com/cerebrallychallenged/hypogean/pathfinding/SimpleMovementGraph.kt)** — Reduce unit movement range when climbing terrain obstacles (e.g., moving up trench walls). Currently ignores terrain cost multipliers during pathfinding. Should consult a terrain-cost extractor to adjust available movement distance.

- **[GroundMovement.kt:20-21](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/rays/movement/GroundMovement.kt)** — Consider whether some actors can run over/crush smaller actors or obstacles. Also support higher default movement values for rough terrain variants.

### Weapon & Damage Effects

- **[BasicShotActionInstance.kt:137](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/actions/BasicShotActionInstance.kt)** — Inflict damage on entities in the line of fire (e.g., shooting through a glass window to hit targets behind it, or collateral damage to props/walls).

- **[EnrageArm.kt:39](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/items/melee/EnrageArm.kt)** — Reduce initiative cost. Currently not balanced relative to other melee weapons.

### Entity Destruction & Cascade Effects

- **[DestroyEntity.kt:25](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/cascade/DestroyEntity.kt)** — When an entity fills its cell and is destroyed, also check and destroy adjacent props (e.g., if a large brick wall collapses, it may crush adjacent obstacles).

### Weapon Representation

- **[MediumRangeMissileLauncher.kt:41](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/items/ranged/missile/MediumRangeMissileLauncher.kt)** — Currently modeled using minigun mechanics; a missile launcher should have distinct properties (slower fire rate, larger blast radius, different targeting).

### Reconnaissance & Vision System

**Extended Recon System** — Several TODOs related to advanced vision/detection mechanics:

- **[Recon.kt:113](core/src/main/kotlin/com/cerebrallychallenged/hypogean/model/Recon.kt)** — Support flying actors with different movement rules (3D pathfinding, height-based line-of-sight adjustments).

- **[Recon.kt:114](core/src/main/kotlin/com/cerebrallychallenged/hypogean/model/Recon.kt)** — Support variable sight strength per actor type (e.g., radar-equipped units have enhanced detection range).

- **[Recon.kt:128](core/src/main/kotlin/com/cerebrallychallenged/hypogean/model/Recon.kt)** — Implement surveillance camera detection system (stationary vision emitters that reveal enemy positions).

### Tool Handling

- **[MoveAction.kt:152](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/actions/MoveAction.kt)** — Consider tool handling state after each path segment (e.g., if a unit is carrying an item, movement may be constrained or partially animated).

- **[PickupAction.kt:145](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/actions/PickupAction.kt)** — Same as MoveAction—consider tool/inventory state transitions during multi-step pickup sequences.

- **[RepairAction.kt:58](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/actions/RepairAction.kt)** — Wield/equip the repair arm before starting repair action (currently not modeled).

### Robot Interactions & Blocking

- **[ManipulatorRobot.kt:74](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/actors/ManipulatorRobot.kt)** — Check for blockers (obstacles, walls) before allowing arm rotation, to prevent clipping through geometry.

- **[ManipulatorRobotBehavior.kt:26, 46, 59](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/behavior/ManipulatorRobotBehavior.kt)** — Refactor robot interactions to be modeled as `Action` instances rather than direct `NpcInteractions`. This improves composability with the action system and makes behavior data-driven.

### Melee Combat

- **[GrapplingAttackAction.kt:89](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/actions/GrapplingAttackAction.kt)** — Define clear rules for what counts as a melee obstruction (walls, props, height differences) and apply them to grapple range validation.

### NPC Decision Making

- **[NpcContextExtensions.kt:243](core/src/main/kotlin/com/cerebrallychallenged/hypogean/npc/NpcContextExtensions.kt)** — Factor energy and initiative consequences into NPC behavior decisions. Currently, decisions may not account for post-action fatigue or initiative changes.

---

## AI & Behavior

### Event-Triggered Dialogue

- **[ActiveDialog.kt:81](core/src/main/kotlin/com/cerebrallychallenged/hypogean/activestate/ActiveDialog.kt)** — Dialogues are currently only initiated by the active actor via `TalkAction` or `HackingAction`. Extend to support event-triggered dialogues (e.g., death events, mine explosions). Clarify domain model: `initiatingEntity` can be the active actor (from action) or an external event source.

### Pathfinding & Movement Submission

- **[CheckPointSeekingBehavior.kt:59](core/src/main/kotlin/com/cerebrallychallenged/hypogean/vanilla/behavior/CheckPointSeekingBehavior.kt)** — Submit `MoveActionInstance` into the action queue after computing checkpoint path (currently path is computed but not submitted to action system).

---

## Code Quality & Architecture

### Type System & Registries

- **[EffectKinds.kt:6](core/src/main/kotlin/com/cerebrallychallenged/hypogean/model/effect/EffectKinds.kt)** — `EffectKinds` is a temporary construct. Once registries become static/singleton objects, consolidate effect registry access here.

### Performance & Optimization

- **[AttributeStore.kt:15](core/src/main/kotlin/com/cerebrallychallenged/hypogean/model/attribute/AttributeStore.kt)** — Determine optimal initial capacity size. Currently a placeholder. Profile typical entity and attribute counts to set appropriate pre-allocation.

- **[MemoryUtils.kt:9](core/src/main/kotlin/com/cerebrallychallenged/jun/util/MemoryUtils.kt)** — Replace byte-slicing logic with JDK >= 13 native `slice(index, length)` method once minimum Java version is bumped. Currently hand-manages byte order for compatibility.

### Security & Validation

- **[Server.kt:129](core/src/main/kotlin/com/cerebrallychallenged/hypogean/server/Server.kt)** — Validate that client is authorized to move/interact with a given item (ownership, permissions). Currently this check is missing.
