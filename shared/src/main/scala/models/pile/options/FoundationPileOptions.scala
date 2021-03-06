package models.pile.options

import models.card.Rank
import models.pile.constraints.Constraint
import models.rules._

object FoundationPileOptions {
  private[this] def getConstraints(lowRank: Rank, numRanks: Int, rules: FoundationRules) = {
    val dragFromConstraint = rules.canMoveFrom match {
      case FoundationCanMoveFrom.Always => Some(Constraint.topCardOnly)
      case FoundationCanMoveFrom.EmptyStock => Some(Constraint.pilesEmpty("stock"))
      case FoundationCanMoveFrom.Never => Some(Constraint.never)
    }

    val dragToConstraint = if (!rules.visible) {
      Some(Constraint.never)
    } else {
      Some(Constraint("foundation", (src, tgt, cards, gameState) => {
        val ret = if (cards.length == 1 && !rules.moveCompleteSequencesOnly) {
          if (rules.maxCards > 0 && tgt.cards.length >= rules.maxCards) {
            false
          } else {
            val firstCard = cards.headOption.getOrElse(throw new IllegalStateException())
            if (tgt.cards.isEmpty) {
              val siblings = gameState.pileSets.find(ps => ps.piles.exists(p => p.id == tgt.id)).map(_.piles.filterNot(_.id == tgt.id)).getOrElse {
                throw new IllegalStateException(s"Can't find pileset for [${tgt.id}].")
              }
              val rankOk = if (lowRank == Rank.Unknown) {
                siblings.flatMap(_.cards.headOption).headOption match {
                  case Some(card) => firstCard.r == card.r
                  case None => true
                }
              } else {
                firstCard.r == lowRank
              }
              rules.initialCardRestriction match {
                case Some(FoundationInitialCardRestriction.SpecificColorUniqueSuits(c)) =>
                  val suits = siblings.flatMap(_.cards.headOption.map(_.s))
                  rankOk && firstCard.s.color == c && !suits.contains(firstCard.s)
                case Some(FoundationInitialCardRestriction.SpecificSuit(s)) =>
                  rankOk && firstCard.s == s
                case Some(FoundationInitialCardRestriction.UniqueColors) =>
                  val colors = siblings.flatMap(_.cards.headOption.map(_.s.color))
                  rankOk && !colors.contains(firstCard.s.color)
                case Some(FoundationInitialCardRestriction.UniqueSuits) =>
                  val suits = siblings.flatMap(_.cards.headOption.map(_.s))
                  rankOk && !suits.contains(firstCard.s)
                case None =>
                  rankOk
              }
            } else {
              val target = tgt.cards.last
              val s = rules.suitMatchRule.check(target.s, firstCard.s)
              val r = rules.rankMatchRule.check(target.r, firstCard.r, lowRank, rules.wrap)
              s && r
            }
          }
        } else if (rules.moveCompleteSequencesOnly) {
          cards.length == numRanks
        } else {
          false
        }
        ret && src.pileSet.exists(x => rules.mayMoveToFrom.contains(x.behavior))
      }))
    }
    (dragFromConstraint, dragToConstraint)
  }

  def apply(rules: FoundationRules, deckOptions: DeckOptions) = {
    if (rules.lowRank == FoundationLowRank.Ascending) {
      (1 to rules.numPiles).map { i =>
        val nextRank = if (i == 1) { 14 } else { i }
        val (dragFromConstraint, dragToConstraint) = getConstraints(Rank.allByValue(nextRank), deckOptions.ranks.size, rules)
        PileOptions(
          cardsShown = Some(rules.cardsShown),
          dragFromConstraint = dragFromConstraint,
          dragToConstraint = dragToConstraint
        )
      }
    } else {
      val lowRank = rules.lowRank match {
        case FoundationLowRank.AnyCard => Rank.Unknown
        case FoundationLowRank.Ascending => throw new IllegalStateException()
        case FoundationLowRank.DeckHighRank => deckOptions.highRank
        case FoundationLowRank.DeckLowRank => deckOptions.lowRank
        case FoundationLowRank.SpecificRank(r) => r
      }

      val (dragFromConstraint, dragToConstraint) = getConstraints(lowRank, deckOptions.ranks.size, rules)
      Seq(PileOptions(
        cardsShown = Some(rules.cardsShown),
        dragFromConstraint = dragFromConstraint,
        dragToConstraint = dragToConstraint
      ))
    }
  }
}
