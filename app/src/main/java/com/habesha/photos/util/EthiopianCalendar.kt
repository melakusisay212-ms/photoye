package com.habesha.photos.util

/**
 * Ethiopian (Ge'ez) calendar conversion utilities.
 *
 * Ethiopian year is ~7–8 years behind Gregorian.
 * 12 months of 30 days + Pagume (5 or 6 days).
 * New Year (Enkutatash / Meskerem 1) falls on 11 Sep (or 12 Sep in Gregorian leap years).
 *
 * This is the real conversion layer that will be used by the MediaStore index.
 */
object EthiopianCalendar {

    val MONTH_NAMES = listOf(
        "መስከረም", "ጥቅምት", "ኅዳር", "ታኅሣሥ", "ጥር", "የካቲት",
        "መጋቢት", "ሚያዝያ", "ግንቦት", "ሰኔ", "ሐምሌ", "ነሐሴ", "ጳጉሜ"
    )

    val MONTH_NAMES_LATIN = listOf(
        "Meskerem", "Tikimt", "Hidar", "Tahsas", "Tir", "Yekatit",
        "Megabit", "Miazia", "Ginbot", "Sene", "Hamle", "Nehasse", "Pagume"
    )

    data class EthDate(
        val year: Int,
        val month: Int, // 1–13
        val day: Int
    ) {
        fun monthNameAmharic(): String = MONTH_NAMES.getOrElse(month - 1) { "?" }
        fun monthNameLatin(): String = MONTH_NAMES_LATIN.getOrElse(month - 1) { "?" }
        fun displayAmharic(): String = "${monthNameAmharic()} $day, $year"
        fun displayFull(): String = "${monthNameAmharic()} $day, $year ዓ.ም."
        fun key(): String = "$year-$month-$day"
    }

    /** Convert Gregorian (year, month 1–12, day) → Ethiopian date. */
    fun fromGregorian(year: Int, month: Int, day: Int): EthDate {
        // JD for Gregorian date (proleptic)
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        val jdn = day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045

        // Ethiopian epoch: Meskerem 1, 1 EE ≈ 11 Aug 8 CE (Julian) → JDN 1724221
        // Using standard offset used by most Ethiopian converters
        val ethJdnOffset = 1723856 // widely used for civil Ethiopian calendar
        val r = jdn - ethJdnOffset

        val ethYear = (4 * r + 3) / 1461
        val rem = r - (1461 * ethYear) / 4
        val ethMonth = rem / 30 + 1
        val ethDay = rem % 30 + 1

        return EthDate(ethYear, ethMonth, ethDay)
    }

    /** Convert from java.util.Calendar / epoch millis. */
    fun fromEpochMillis(millis: Long): EthDate {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = millis }
        return fromGregorian(
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }

    fun daysInMonth(year: Int, month: Int): Int {
        return if (month == 13) {
            // Pagume: 6 days in leap year (year % 4 == 3)
            if (year % 4 == 3) 6 else 5
        } else 30
    }

    /** Simple holiday map for Meskerem (extend later). */
    fun holidayName(eth: EthDate): String? = when {
        eth.month == 1 && eth.day == 1 -> "Enkutatash"
        eth.month == 1 && eth.day == 17 -> "Meskel"
        eth.month == 4 && eth.day == 29 -> "Genna"
        eth.month == 5 && eth.day == 11 -> "Timket"
        else -> null
    }
}
