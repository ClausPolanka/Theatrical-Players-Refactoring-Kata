import java.text.NumberFormat
import java.util.Locale
import kotlin.math.floor
import kotlin.math.max

val format = { number: Long -> NumberFormat.getCurrencyInstance(Locale.US).format(number) }

class StatementPrinter {

    fun print(invoice: Invoice, plays: Map<String, Play>): String {
        val statementData = createStatementData(invoice, plays)
        val totalAmount = statementData.sumBy { t -> t.amount }
        val volumeCredits = calculateVolumeCredits(invoice, plays)
        return createStatement(invoice, statementData, totalAmount, volumeCredits)
    }

    private fun createStatementData(
        invoice: Invoice,
        plays: Map<String, Play>
    ) = invoice.performances.map { perf ->
        val play = plays.getValue(perf.playID)
        val amt = calculateAmountFor(play, perf)
        StatementData(play.name, amt, perf.audience)
    }

    private fun createStatement(
        invoice: Invoice,
        statementData: List<StatementData>,
        totalAmount: Int,
        volumeCredits: Int
    ) = """Statement for ${invoice.customer}
             |${statementData.statementLines()}
             |Amount owed is ${format((totalAmount / 100).toLong())}
             |You earned $volumeCredits credits
             |""".trimMargin()

    private fun List<StatementData>.statementLines() =
        joinToString(separator = "\n") { statement ->
            "  ${statement.playName}: ${format((statement.amount / 100).toLong())} (${statement.audience} seats)"
        }

    private fun calculateVolumeCredits(invoice: Invoice, plays: Map<String, Play>): Int {
        val creditData = invoice.performances.map { perf ->
            val play = plays.getValue(perf.playID)
            Pair(play.type, perf.audience)
        }
        val creditsAllPlayTypes = creditData
            .map { p -> max(p.second - 30, 0) }
            .sum()
        val comedyCredits = creditData
            .filter { it.first == "comedy" }
            .map { floor((it.second / 5).toDouble()).toInt() }
            .sum()
        return creditsAllPlayTypes + comedyCredits
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
