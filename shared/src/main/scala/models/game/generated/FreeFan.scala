package models.game.generated

import models.game._
import models.game.rules._

// scalastyle:off
object FreeFan extends GameRules(
  id = "freefan",
  title = "FreeFan",
  description = "An easy variation of ^fan^ with cells. Invented by Gregg Seelhoff.",
  victoryCondition = VictoryCondition.AllOnFoundation,
  cardRemovalMethod = CardRemovalMethod.BuildSequencesOnFoundation,
  deckOptions = DeckOptions(
    numDecks = 1,
    suits = Seq(Suit.Hearts, Suit.Spades, Suit.Diamonds, Suit.Clubs),
    ranks = Seq(Rank.Two, Rank.Three, Rank.Four, Rank.Five, Rank.Six, Rank.Seven, Rank.Eight, Rank.Nine, Rank.Ten, Rank.Jack, Rank.Queen, Rank.King, Rank.Ace),
    lowRank = Some(Rank.Ace)
  ),
  stock = Some(
    Stock(
      name = "Stock",
      dealTo = StockDealTo.Waste,
      maximumDeals = Some(1),
      cardsDealt = StockCardsDealt.Count(1),
      stopAfterPartialDeal = true,
      createPocketWhenEmpty = false,
      galleryMode = false
    )
  ),
  waste = None,
  foundations = Seq(
    FoundationSet(
      name = "Foundation",
      numPiles = 4,
      lowRank = FoundationLowRank.DeckLowRank,
      initialCards = InitialCards.Count(0),
      suitMatchRule = SuitMatchRule.SameSuit,
      rankMatchRule = RankMatchRule.Up,
      wrapFromKingToAce = true,
      moveCompleteSequencesOnly = false,
      maxCards = -1,
      canMoveFrom = FoundationCanMoveFrom.Never,
      mayMoveToFrom = Seq("Stock", "Pyramid", "Waste", "Pocket", "Reserve", "Cell", "Foundation", "Tableau"),
      offscreen = false,
      autoMoveCards = true,
      autoMoveFrom = Seq("Stock", "Pyramid", "Waste", "Pocket", "Reserve", "Cell", "Foundation", "Tableau")
    )
  ),
  tableaus = Seq(
    TableauSet(
      name = "Fan",
      numPiles = 18,
      initialCards = InitialCards.RestOfDeck,
      cardsFaceDown = TableauFaceDownCards.Count(0),
      suitMatchRuleForBuilding = SuitMatchRule.SameSuit,
      rankMatchRuleForBuilding = RankMatchRule.Down,
      wrapFromKingToAce = false,
      suitMatchRuleForMovingStacks = SuitMatchRule.None,
      rankMatchRuleForMovingStacks = RankMatchRule.Down,
      autoFillEmptyFrom = TableauAutoFillEmptyFrom.Nowhere,
      emptyFilledWith = TableauFillEmptyWith.Kings,
      mayMoveToNonEmptyFrom = Seq("Stock", "Pyramid", "Waste", "Pocket", "Reserve", "Cell", "Foundation", "Tableau"),
      mayMoveToEmptyFrom = Seq("Stock", "Pyramid", "Waste", "Pocket", "Reserve", "Cell", "Foundation", "Tableau"),
      maxCards = 0,
      actionDuringDeal = PileAction.None,
      actionAfterDeal = PileAction.None,
      pilesWithLowCardsAtBottom = 0
    )
  ),
  cells = Some(
    CellSet(

      name = "Cell",

      pluralName = "Cells",

      numPiles = 2,

      canMoveFrom = Seq("Stock", "Pyramid", "Waste", "Pocket", "Reserve", "Cell", "Foundation", "Tableau"),

      initialCards = 0,

      numEphemeral = 0

    )
  ),
  reserves = None,
  pyramids = Nil
)
// scalastyle:on
