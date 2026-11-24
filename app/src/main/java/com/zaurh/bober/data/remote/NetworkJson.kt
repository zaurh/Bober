package com.zaurh.bober.data.remote

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

/**
 * JSON configuration shared by Retrofit and the chat socket. It must match what the Ktor backend sends:
 *  - the backend adds fields over time, so unknown keys are ignored instead of failing the whole response;
 *  - `null` for a field with a default falls back to that default.
 */
val NetworkJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    isLenient = true
}

/** The backend serializes message timestamps as strings (`"1719842400000"`); accept both forms. */
object FlexibleLongSerializer : KSerializer<Long> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleLong", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Long) = encoder.encodeLong(value)

    override fun deserialize(decoder: Decoder): Long {
        if (decoder !is JsonDecoder) return decoder.decodeLong()
        val primitive = decoder.decodeJsonElement().jsonPrimitive
        return primitive.longOrNull ?: primitive.content.toLongOrNull()
            ?: throw SerializationException("Expected a timestamp but was '${primitive.content}'")
    }
}
