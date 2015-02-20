package models.game

object Deck {
  def fresh = {
    val cards = for {
      suit <- Suit.all
      rank <- Rank.all.reverse
    } yield Card(r = rank, s = suit)
    Deck(cards.toList)
  }
}

case class Deck(var cards: List[Card]) {
  def dealCardsTo(pile: Pile, numCards: Int = this.cards.size) = {
    this.cards.take(numCards).foreach { c =>
      pile.cards = c :: pile.cards
    }
    this.cards = this.cards.drop(numCards)
  }
}
