import java.text.NumberFormat
import java.util.Locale
import kotlin.math.floor
import kotlin.math.max

val format = { number: Long -> NumberFormat.getCurrencyInstance(Locale.US).format(number) }

class StatementPrinter {

    fun print(invoice: Invoice, plays: Map<String, Play>): String {
        val statementData = invoice.performances.map { perf ->
            val play = plays.getValue(perf.playID)
            val amt = calculateAmountFor(play, perf)
            StatementData(play.name, amt, perf.audience)
        }
        val totalAmount = statementData.sumBy { t -> t.amount }
        val volumeCredits = calculateVolumeCredits(invoice, plays)
        return createStatement(invoice, statementData, totalAmount, volumeCredits)
    }

    private fun createStatement(
        invoice: Invoice,
        statementData: List<StatementData>,
        totalAmount: Int,
        volumeCredits: Int
    ): String {
        val header = "Statement for ${invoice.customer}\n"
        val lines =
            statementData.map { t -> "  ${t.playName}: ${format((t.amount / 100).toLong())} (${t.audience} seats)\n" }
        val amountOwned = "Amount owed is ${format((totalAmount / 100).toLong())}\n"
        val earnedCredits = "You earned $volumeCredits credits\n"
        return header + lines.joinToString(separator = "") + amountOwned + earnedCredits
    }

    private fun calculateVolumeCredits(invoice: Invoice, plays: Map<String, Play>): Int {
        var volumeCredits = 0
        invoice.performances.forEach { perf ->
            val play = plays.getValue(perf.playID)

            volumeCredits += max(perf.audience - 30, 0)

            // add extra credit for every ten comedy attendees
            volumeCredits += when (play.type) {
                "comedy" -> floor((perf.audience / 5).toDouble()).toInt()
                else -> 0
            }
        }
        return volumeCredits
    }

    private fun calculateAmountFor(play: Play, perf: Performance): Int {
        return when (play.type) {
            "tragedy" -> tragedyAmountFor(perf)
            "comedy" -> comedyAmountFor(perf)
            else -> throw Error("unknown type: {play.type}")
        }
    }

    private fun comedyAmountFor(perf: Performance): Int {
        val generalComedyAmount = 30000 + 300 * perf.audience
        val largeAudienceBonus = when {
            perf.audience > 20 -> 10000 + 500 * (perf.audience - 20)
            else -> 0
        }
        return generalComedyAmount + largeAudienceBonus
    }

    private fun tragedyAmountFor(perf: Performance): Int {
        val generalTragedyAmount = 40000
        val largeAudienceBonus = when {
            perf.audience > 30 -> 1000 * (perf.audience - 30)
            else -> 0
        }
        return generalTragedyAmount + largeAudienceBonus
    }

}

data class StatementData(val playName: String, val amount: Int, val audience: Int)
