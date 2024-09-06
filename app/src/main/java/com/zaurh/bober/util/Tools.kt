package com.zaurh.bober.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


fun lastSeenDay(timestamp: Long?): String{
    val millisecondsInOneDay = 24 * 60 * 60 * 1000

    val currentDate = System.currentTimeMillis()
    val yesterdayDate = currentDate.minus(millisecondsInOneDay)

    val date = Date(timestamp ?: 0)
    val dateDay = SimpleDateFormat("MMMM d", Locale.getDefault())
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    if (date != Date(0)){
        val day = when (dateDay.format(date)){
            dateDay.format(currentDate) -> "today"
            dateDay.format(yesterdayDate) -> "yesterday"
            else -> dateDay.format(date)
        }
        return "last seen $day at ${dateFormat.format(date)}"
    }else{
        return ""
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun localDateToString(localDate: LocalDate): String {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    return localDate.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun stringToLocalDate(dateString: String): LocalDate {
    if (dateString.isNotEmpty()){
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        return LocalDate.parse(dateString, formatter)
    }else{
        return LocalDate.of(2000, 1,1)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateAge(dateOfBirth: String): Int {
    if (dateOfBirth.isNotEmpty()){
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val dob = LocalDate.parse(dateOfBirth, formatter)
        val currentDate = LocalDate.now()
        val period = Period.between(dob, currentDate)

        return period.years
    }else{
        return 0
    }
}


object DateSerializer : KSerializer<Date> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(dateFormat.format(value))
    }

    override fun deserialize(decoder: Decoder): Date {
        return dateFormat.parse(decoder.decodeString()) ?: throw SerializationException("Invalid date format")
    }
}

fun Context.sendMail() {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "vnd.android.cursor.item/email"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("zaurway@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Bober")
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "No email application found.", Toast.LENGTH_SHORT).show()
    } catch (t: Throwable) {
        Toast.makeText(this, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
    }
}