package com.tenbis.support.models

import android.os.Parcel
import android.os.Parcelable
import com.tenbis.support.consts.CardType
import java.util.*

/**
 * @param cardNumber 6 - 16 digit card number. All numbers, no spaces.
 * @param expiryMonth two digits {January=1, ..., December=12}
 * @param expiryYear 2 digits year
 * @param cvv 3 - 4 character security code.
 */
data class CreditCard(
    var cardNumber: String = "",
    var expiryMonth: Int = 0,
    var expiryYear: Int = 0,
    var cvv: String = ""
) : Parcelable {

    val cardType: CardType
        get() = CardType.fromCardNumber(cardNumber)

    /**
     * @return A string suitable for display, with spaces inserted for readability.
     */
    val formattedCardNumber: String
        get() {
            val numberBuilder = StringBuilder()

            for (i in 0 until cardNumber.length) {
                numberBuilder.append(cardNumber[i])

                if (i in cardType.cardSpacingPattern) {
                    numberBuilder.append(" ")
                }
            }
            return numberBuilder.toString()
        }

    /**
     * @return `true` indicates a current, valid date.
     */
    val isExpiryValid: Boolean
        get() {
            if (expiryMonth < 1 || LATEST_POSSIBLE_MONTH < expiryMonth) return false

            val now = Calendar.getInstance()
            val thisYear = now.get(Calendar.YEAR)
            val thisMonth = now.get(Calendar.MONTH) + 1

            if (expiryYear < thisYear) return false
            if (expiryYear == thisYear && expiryMonth < thisMonth) return false

            return expiryYear <= thisYear + YEARS_FROM_NOW
        }

    constructor(src: Parcel) : this() {
        cardNumber = src.readString() ?: ""
        expiryMonth = src.readInt()
        expiryYear = src.readInt()
        cvv = src.readString() ?: ""
    }

    /**
     * @return The last four digits of the card number
     */
    fun getLastFourDigitsOfCardNumber(): String {
        if (cardNumber.isEmpty()) return ""
        val available = Math.min(LAST_FOUR_DIGITS_MIN, cardNumber.length)
        return cardNumber.substring(cardNumber.length - available)
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(cardNumber)
        dest.writeInt(expiryMonth)
        dest.writeInt(expiryYear)
        dest.writeString(cvv)
    }

    companion object {
        private const val LAST_FOUR_DIGITS_MIN = 4
        private const val YEARS_FROM_NOW = 15
        private const val LATEST_POSSIBLE_MONTH = 12

        @JvmField
        val CREATOR: Parcelable.Creator<CreditCard> = object : Parcelable.Creator<CreditCard> {
            override fun createFromParcel(parcel: Parcel): CreditCard = CreditCard(parcel)

            override fun newArray(size: Int): Array<CreditCard?> = arrayOfNulls(size)
        }
    }
}
