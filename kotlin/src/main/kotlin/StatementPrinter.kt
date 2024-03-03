import java.lang.System.*
import java.text.*
import java.util.*

private val format = { number: Long -> NumberFormat.getCurrencyInstance(Locale.US).format(number) }

class StatementPrinter {

    fun print(invoice: Invoice, plays: Map<String, Play>): String =
        createStatement(
            invoice.customer,
            invoice.statementDataFor(plays),
            invoice.creditsFor(plays)
        )

    private fun createStatement(
        customer: String,
        statementData: List<StatementData>,
        volumeCredits: Int
    ) = """Statement for $customer
          |${statementData.statementLines()}
          |Amount owed is ${format(toDollar(statementData.sumBy { it.amountInCents }))}
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

