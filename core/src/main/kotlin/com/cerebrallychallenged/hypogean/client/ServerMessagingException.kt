package com.cerebrallychallenged.hypogean.client

/**
 * Is thrown when there is a problem with the messages received from the server.
 */
class ServerMessagingException(message: String) : RuntimeException(message)