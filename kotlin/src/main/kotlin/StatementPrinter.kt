import java.text.NumberFormat
import java.util.Locale
import kotlin.math.floor
import kotlin.math.max

val format = { number: Long -> NumberFormat.getCurrencyInstance(Locale.US).format(number) }

class StatementPrinter {

    fun print(invoice: Invoice, plays: Map<String, Play>): String {
        var totalAmount = 0
        var volumeCredits = 0
        var result = "Statement for ${invoice.customer}\n"


        invoice.performances.forEach { perf ->
            val play = plays.getValue(perf.playID)
            val thisAmount = calculateAmountFor(play, perf)

            // add volume credits
            volumeCredits += max(perf.audience - 30, 0)
            // add extra credit for every ten comedy attendees
            if ("comedy" == play.type) volumeCredits += floor((perf.audience / 5).toDouble()).toInt()

            // print line for this order
            result += "  ${play.name}: ${format((thisAmount / 100).toLong())} (${perf.audience} seats)\n"

            totalAmount += thisAmount
        }
        result += "Amount owed is ${format((totalAmount / 100).toLong())}\n"
        result += "You earned $volumeCredits credits\n"
        return result
    }

    private fun calculateAmountFor(play: Play, perf: Performance): Int {
        return when (play.type) {
            "tragedy" -> tragedyAmountFor(perf)
            "comedy" -> comedyAmountFor(perf)
            else -> throw Error("unknown type: {play.type}")
        }
    }

    private fun comedyAmountFor(perf: Performance): Int {
        var amount = 30000
        if (perf.audience > 20) {
            amount += 10000 + 500 * (perf.audience - 20)
        }
        amount += 300 * perf.audience
        return amount
    }

    private fun tragedyAmountFor(perf: Performance): Int {
        var amount = 40000
        if (perf.audience > 30) {
            amount += 1000 * (perf.audience - 30)
        }
        return amount
    }

}
