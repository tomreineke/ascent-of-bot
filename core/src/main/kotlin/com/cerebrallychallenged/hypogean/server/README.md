# Hypogean Server Package

This package contains the authoritative game server for the Hypogean game. It manages the game world state, processes client actions, and handles the simulation of game turns.

## Overview

The server acts as the source of truth for the game world. It maintains a `World` instance and uses a `Rulebook` to validate and execute actions. Game content and rules are typically provided by mods, such as the [Vanilla Package](../vanilla/README.md). It communicates with clients using a custom messaging system serialized with Kryo.

## Key Components

- **Server**: The main server class that manages the game loop, connected clients, and world state.
- **ClientRep**: Represents a connected client (player or observer) on the server side. It handles incoming messages and outgoing updates for a specific client.
- **SocketServer**: Provides a TCP socket interface for remote clients to connect to the server. It communicates with the corresponding [SocketClient](../client/README.md) on the client side.
- **Simulation**: The server manages the progression of game time through simulation steps, triggered by client actions or NPC behaviors.

## Message Handling

The server processes several types of `ClientToServerMessage`:
- `ClaimFaction`: A client claims a specific faction to control.
- `SubmitAction`: A client submits an action for an actor they control.
- `AckUpdate`: A client acknowledges receipt of a world update, allowing the server to proceed with further simulation.
- `AdminCommand`: Special commands for server management.

## Interaction with UI

While the server manages the game logic, the [Hypogean View Package](../view/README.md) handles the presentation of this state to the player. The server sends `WorldUpdate` messages which are then processed by the `ViewModel` in the view package to update the user interface.

## Threading

The server runs on its own dedicated thread (`server-thread-%d`) to ensure that game simulation and message processing do not block other system operations. Coroutines are used for asynchronous message handling and socket communication.
