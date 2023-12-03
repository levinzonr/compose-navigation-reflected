package com.levinzonr.reflected.core

interface DestinationSerializer<T : Destination> {
    fun serialize(destination: T): SerializedDestination
    fun deserialize(serializedDestination: SerializedDestination): T
}
