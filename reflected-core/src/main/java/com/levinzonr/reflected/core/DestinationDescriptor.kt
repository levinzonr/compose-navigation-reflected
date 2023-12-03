package com.levinzonr.reflected.core

/**
 * Complete information that is used to describe the given destination of type T
 */
interface DestinationDescriptor<T : Destination> {
    fun route(): String
    fun arguments(): List<ArgumentDescriptor>
    fun serializer(): DestinationSerializer<T>
}
