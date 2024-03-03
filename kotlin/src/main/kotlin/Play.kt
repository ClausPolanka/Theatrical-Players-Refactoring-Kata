import kotlin.math.*

open class Play(val name: String, val type: String) {
    open fun amountFor(audience: Int): Int {
        TODO("Not yet implemented")
    }
    open fun credits(audience: Int) : Int {
        return max(audience - 30, 0)
    }
}

class Tragedy(name: String, type: String): Play(name, type) {
    override fun amountFor(audience: Int): Int {
        val generalTragedyAmount = 40000
        val largeAudienceBonus = when {
            audience > 30 -> 1000 * (audience - 30)
            else -> 0
        }
        return generalTragedyAmount + largeAudienceBonus
    }
}
class Comedy(name: String, type: String): Play(name, type) {
    override fun amountFor(audience: Int): Int {
        val generalComedyAmount = 30000 + 300 * audience
        val largeAudienceBonus = when {
            audience > 20 -> 10000 + 500 * (audience - 20)
            else -> 0
        }
        return generalComedyAmount + largeAudienceBonus
    }

    override fun credits(audience: Int): Int {
        return max(audience - 30, 0) + floor((audience / 5).toDouble()).toInt()
    }
}
