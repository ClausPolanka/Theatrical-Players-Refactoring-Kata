import java.lang.System.*
import java.text.*
import java.util.*

private val format = { number: Long -> NumberFormat.getCurrencyInstance(Locale.US).format(number) }

class StatementPrinter {

    fun print(invoice: Invoice, plays: Map<String, Play>): String {
        val statementData = invoice.statementDataFor(plays)
        val totalAmountInCents = statementData.sumBy { it.amountInCents }
        val volumeCredits = invoice.creditsFor(plays)
        return createStatement(
            invoice,
            statementData,
            totalAmountInCents,
            volumeCredits
        )
    }

    private fun createStatement(
        invoice: Invoice,
        statementData: List<StatementData>,
        totalAmountInCents: Int,
        volumeCredits: Int
    ) = """Statement for ${invoice.customer}
             |${statementData.statementLines()}
             |Amount owed is ${format(toDollar(totalAmountInCents))}
             |You earned $volumeCredits credits
             |""".trimMargin()

    private fun List<StatementData>.statementLines() =
        joinToString(separator = lineSeparator()) { statement ->
            "  ${statement.playName}: " +
                "${format(toDollar(statement.amountInCents))} " +
                "(${statement.audience} seats)"
        }

    private fun toDollar(amount: Int) = (amount / 100).toLong()
}

