
import org.approvaltests.Approvals.*
import org.junit.jupiter.api.*

class StatementPrinterTests {

    @Test
    internal fun exampleStatement() {

        val plays = mapOf(
            "hamlet" to Tragedy("Hamlet", "tragedy"),
            "as-like" to Comedy("As You Like It", "comedy"),
            "othello" to Tragedy("Othello", "tragedy")
        )

        val invoice = Invoice(
            "BigCo", listOf(
                Performance("hamlet", 55),
                Performance("as-like", 35),
                Performance("othello", 40)
            )
        )

        val statementPrinter = StatementPrinter()
        val result = statementPrinter.print(invoice, plays)

        verify(result)
    }
}
