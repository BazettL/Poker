package poker;

import java.util.*;

public class Table {
    private enum ROUND {PREFLOP, FLOP, TURN, RIVER, INTERIM}
    private ROUND round;
    private Deck deck;
    //Map the list of playerBets to their current bet in the round
    private Map<Player, Integer> playerBets;
    private Map<Player, Boolean> playersInRound;
    private List<Card> commonCards;
    private int potSize;
    private int dealerIndex;
    private int currentPlayerIndex;

    private final int smallBlind;
    private final int bigBlind;

    public Table(List<Player> players){
        playerBets = new HashMap<>();
        playersInRound = new HashMap<>();
        if(players != null) {
            for (Player player : players) {
                playerBets.put(player, 0);
                playersInRound.put(player,true);
            }
        }
        dealerIndex = 0;
        commonCards = new ArrayList<>();
        potSize = 0;
        round = ROUND.PREFLOP;
        deck = new Deck();
        smallBlind = 1;
        bigBlind = 2;
    }

    public Table(){
        this(null);
    }

    public void addPlayer(Player p){
        playerBets.put(p,0);
        if(round != ROUND.INTERIM){
            playersInRound.put(p,false);
        } else {
            playersInRound.put(p, true);
        }
    }
    /*
     * Start a round of poker by setting dealer, posting small blind, posting big blind, then telling the next player it's their turn
     * Currently assumes both blinds will accept. Needs functionality to give next player chance to post blinds if the first one folds
     */
    public void startRound(){
        dealerIndex++;
        Player[] players = playersInRound.keySet().toArray(new Player[0]);
        //Tell player left of dealer he may fold or post the small blind
        players[dealerIndex+1].setRequiredBet(smallBlind);
        Turn lastTurn = players[dealerIndex+1].playTurn(
                new TurnNotification(Arrays.asList(Turn.PlayerAction.FOLD, Turn.PlayerAction.CALL),
                        0, smallBlind));
        receivePlayerTurn(lastTurn);
        players[dealerIndex+2].setRequiredBet(bigBlind);
        lastTurn = players[dealerIndex+2].playTurn(
                new TurnNotification(Arrays.asList(Turn.PlayerAction.FOLD, Turn.PlayerAction.CALL),
                        0, bigBlind));
        receivePlayerTurn(lastTurn);

        for(Player p : playersInRound.keySet()){
            p.drawHole(deck);
            System.out.println(p.getName() +  ", your hand is: " + p.getHoleAsString() + ". You have " + p.getChips() + " chips.");
        }

        System.out.println("POT AMOUNT: " + potSize);

        currentPlayerIndex = dealerIndex + 3;
        lastTurn = players[currentPlayerIndex%players.length].playTurn(
                new TurnNotification(Arrays.asList(Turn.PlayerAction.FOLD, Turn.PlayerAction.CALL, Turn.PlayerAction.RAISE),
                        playerBets.get(players[currentPlayerIndex - 1]), 0));
        receivePlayerTurn(lastTurn);

    }

    private void receivePlayerTurn(Turn t){
        potSize += t.getBetAmount();
        playerBets.put(t.getPlayer(), t.getBetAmount());
        System.out.println("TABLE RECEIVED ACTION OF\n\t " + t.toString());
        Player[] players = playersInRound.keySet().toArray(new Player[0]);
        currentPlayerIndex++;

        //if we have passed the blinds and players have received their cards
        //and we are still in the same stage (haven't passed dealer)
        if(players[currentPlayerIndex%players.length].getHole()[0] != null && currentPlayerIndex > dealerIndex+3 && currentPlayerIndex <= dealerIndex+players.length){
            receivePlayerTurn(players[currentPlayerIndex%players.length].playTurn(
                    new TurnNotification(Arrays.asList(Turn.PlayerAction.FOLD, Turn.PlayerAction.CALL, Turn.PlayerAction.RAISE),
                            playerBets.get(players[(currentPlayerIndex - 1)%players.length]), 0)));

        }
        //go to next round once the dealer plays his turn
        if(currentPlayerIndex == dealerIndex+players.length+1){
            switch(round){
                case PREFLOP: round = ROUND.FLOP; break;
                case FLOP: round = ROUND.TURN; break;
                case TURN: round = ROUND.RIVER; break;
                case RIVER: round = ROUND.INTERIM; break;
            }
        }

    }


}