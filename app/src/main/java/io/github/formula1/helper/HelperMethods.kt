package io.github.formula1.helper

import io.github.formula1.R
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.format
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun resolveTeamImage(teamName: String): Int {
    return when (teamName) {
        "Mercedes" -> R.drawable.mercedes
        "Aston Martin" -> R.drawable.aston_martin
        "Red Bull Racing" -> R.drawable.redbull
        "Ferrari" -> R.drawable.ferrari
        "McLaren" -> R.drawable.mclaren
        "Haas F1 Team" -> R.drawable.haas
        "Racing Bulls" -> R.drawable.racing_bulls
        "Alpine" -> R.drawable.alpine
        "Audi" -> R.drawable.audi
        "Williams" -> R.drawable.williams
        "Cadillac" -> R.drawable.cadillac
        else -> R.drawable.ic_launcher_foreground
    }
}

fun getCountryCode(countryName: String?): String {
    var isoCountryCodes: Array<String> = Locale.getISOCountries()
    isoCountryCodes.forEach { code ->
        var locale = Locale("", code)
        if (countryName.equals(locale.displayCountry, ignoreCase = true)) {
            return code
        }
    }
    return ""
}

fun countryCodeToEmojiFlag(countryCode: String): String {
    return countryCode
        .uppercase(Locale.US)
        .map { char ->
            Character.codePointAt("$char", 0) - 0x41 + 0x1F1E6
        }
        .map { codePoint ->
            Character.toChars(codePoint)
        }
        .joinToString(separator = "") { charArray ->
            String(charArray)
        }
}


fun ordinalOf(i: Int): String {
    if (Locale.getDefault().language == "es") {
        return spanishOrdinalOf(i)
    }
    val iAbs = i.absoluteValue // if you want negative ordinals, or just use i
    return "$i" + if (iAbs % 100 in 11..13) "th" else when (iAbs % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}

fun spanishOrdinalOf(i: Int): String {
    val iAbs = i.absoluteValue
    return "$i" +  when (iAbs) {
        1 -> "ro"
        2 -> "do"
        3 -> "ro"
        4 -> "to"
        5 -> "to"
        6 -> "to"
        7 -> "mo"
        8 -> "vo"
        9 -> "no"
        10 -> "mo"
        11 -> "mo"
        12 -> "vo"
        13 -> "vo"
        14 -> "vo"
        15 -> "vo"
        16 -> "vo"
        17 -> "mo"
        18 -> "vo"
        19 -> "no"
        20 -> "mo"
        21 -> "ro"
        22 -> "do"
        else -> ""
    }
}

fun convertLapTime(lapTime: Double?): String {
    return if (lapTime != null) Duration.parseIsoString(lapTime.seconds.toIsoString()).toString() else "DNP"
}

@OptIn(ExperimentalTime::class)
fun formatTrackTime(sessionTime: Instant, utcOffset: UtcOffset): String {
    return DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET.format {
        setDateTimeOffset(sessionTime, utcOffset)
    }
}