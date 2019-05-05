package poker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 * Card
 * Define an abstraction to store and return the information shown on a card
 */
public class Card implements Comparable<Card>{
    private final Suit suit;

    private final Rank rank;

    // Creates a cache of possible cards
    private final static Map<String, Card> CARD_CACHE = initCache();

    private static Map<String, Card> initCache() {
        final Map<String, Card> cache = new HashMap<>();
        for(final Suit suit : Suit.values()) {
            for(final Rank rank : Rank.values()) {
                cache.put(cardKey(rank, suit),  new Card(rank, suit));
            }
        }
        return Collections.unmodifiableMap(cache);
    }

    // Method used for cache creation
    private static String cardKey(final Rank rank,
                                  final Suit suit) {
        return rank + " of " + suit;
    }

    // private constructor for cache creation
    private Card(final Rank rank,
                 final Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    // This is what will be used to get any card that we may need
    public static Card getCard(final Rank rank,
                               final Suit suit) {
        final Card card = CARD_CACHE.get(cardKey(rank, suit));
        if(card != null ) {
            return card;
        }
        throw new RuntimeException("Invalid card! " + rank + " " + suit);
    }

    /**
     * @param o the Card to be compared
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(final Card o) {
        final int rankComparison = Integer.compare(this.rank.getRankValue(), o.rank.getRankValue());

        if(rankComparison != 0) {
            return rankComparison;
        }

        return Integer.compare(this.suit.getSuitValue(), o.suit.getSuitValue());
    }

    public String toString() {
        return rank + " of " + suit;
    }

    // Enumerations with value correspondences
    enum Suit {
        DIAMONDS(1),
        CLUBS(2),
        HEARTS(3),
        SPADES(4);

        private final int suitValue;

        Suit(final int suitValue) {
            this.suitValue = suitValue;
        }

        public int getSuitValue() {
            return suitValue;
        }
    }

    enum Rank {
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13),
        ACE(14);

        private final int rankValue;
        Rank(int rankValue) {
            this.rankValue = rankValue;
        }

        public int getRankValue() {
            return rankValue;
        }
    }

}
