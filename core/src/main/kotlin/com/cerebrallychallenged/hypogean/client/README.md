# Hypogean Client Package

This package contains the client-side implementation for connecting to and interacting with a Hypogean [Server](../server/README.md). It handles communication, world update processing, and local state management for the player.

## Overview

The client package provides the infrastructure for a player to join a game, claim a faction, and participate in the world simulation. It maintains a local view of the [World](../model/README.md) and synchronizes it with the authoritative server.

## Key Components

- **Client**: The primary class representing the player's connection. It manages message routing, faction claiming, and world update flows.
- **SocketClient**: Implements the network layer using Ktor sockets. It handles the low-level serialization and transmission of byte arrays over TCP.
- **ClientConnector**: A bridge between the high-level `Client` logic and the low-level `SocketClient` transport.

## Workflow

1. **Connection**: The `SocketClient` establishes a TCP connection to the server.
2. **Handshake**: Upon connection, the server sends a `HelloClient` message, to which the client responds by claiming a `Faction`.
3. **World Updates**: The client receives `WorldUpdate` messages from the server, containing `ChangeScheduleDto` payloads. These are emitted through the `worldUpdates` flow to be consumed by the [UI/ViewModel](../view/README.md).
4. **Action Expansion**: When a player explores possible actions, the client can request additional details from the server using `ExpandPartialAction` and waits for an `ExpandedAction` response.

## Integration with View

The client package serves as the data provider for the [View Package](../view/README.md). The `ViewModel` typically subscribes to the `worldUpdates` flow provided by the `Client` to keep the UI in sync with the server's authoritative state.

## Error Handling

Communication errors or unexpected server responses result in a `ServerMessagingException`, which should be handled by the consuming layer (usually the UI) to inform the player of connection issues.
