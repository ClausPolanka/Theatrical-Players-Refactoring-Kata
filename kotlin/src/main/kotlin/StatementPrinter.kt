import java.text.*
import java.util.*

val format = { number: Long -> NumberFormat.getCurrencyInstance(Locale.US).format(number) }

class StatementPrinter {

    fun print(invoice: Invoice, plays: Map<String, Play>): String {
        val statementData = invoice.statementDataFor(plays)
        val totalAmount = statementData.sumBy { it.amount }
        val volumeCredits = invoice.creditsFor(plays)
        return createStatement(invoice, statementData, totalAmount, volumeCredits)
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
}

