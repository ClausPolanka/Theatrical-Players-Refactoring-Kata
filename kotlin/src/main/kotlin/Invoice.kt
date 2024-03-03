data class Invoice(val customer: String, val performances: List<Performance>) {
    fun creditsFor(plays: Map<String, Play>): Int {
        return performances
            .map { perf ->
                val play = plays.getValue(perf.playID)
                play.credits(perf.audience)
            }
            .sum()
    }

    fun statementDataFor(plays: Map<String, Play>) =
        performances.map { perf ->
            val play = plays.getValue(perf.playID)
            val amt = play.amountFor(perf.audience)
            StatementData(play.name, amt, perf.audience)
        }
}
