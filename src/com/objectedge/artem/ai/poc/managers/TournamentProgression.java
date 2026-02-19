package com.objectedge.artem.ai.poc.managers;

import com.objectedge.artem.ai.poc.models.Armwrestler;
import com.objectedge.artem.ai.poc.models.TournamentState;
import java.util.*;

/**
 * Handles tournament progression logic between rounds
 */
public class TournamentProgression {
    private TournamentState state;
    private TournamentProgressionListener listener;

    public interface TournamentProgressionListener {
        void onTournamentComplete(Armwrestler winner);
        void onSuperFinalInitiated(Armwrestler finalist1, Armwrestler finalist2);
        void onRoundDisplayRequested();
        void onValidationFailed();
    }

    public TournamentProgression(TournamentState state, TournamentProgressionListener listener) {
        this.state = state;
        this.listener = listener;
    }

    public void proceedToNextRound() {
        if (!validateRoundComplete()) {
            listener.onValidationFailed();
            return;
        }

        List<Armwrestler> topWinners = new ArrayList<>();
        List<Armwrestler> topLosers = new ArrayList<>();
        List<Armwrestler> bottomWinners = new ArrayList<>();
        List<Armwrestler> bottomLosers = new ArrayList<>();

        buildWinnersAndLosers(topWinners, topLosers, bottomWinners, bottomLosers);

        if (state.isSemifinal() && !state.isFinal()) {
            handleSemifinalCompletion(bottomWinners, topLosers);
            return;
        }

        if (state.isFinal() && !state.isSuperFinal()) {
            handleFinalCompletion(topWinners, topLosers);
            return;
        }

        if (state.isSuperFinal()) {
            handleSuperFinalCompletion();
            return;
        }

        int potentialBottomCount = bottomWinners.size() + topLosers.size();
        if (topWinners.size() == 1 && potentialBottomCount == 2) {
            goToSemifinal(topWinners, bottomWinners, topLosers);
            return;
        }

        advanceToNextRound(topWinners, bottomWinners, topLosers);
    }

    private boolean validateRoundComplete() {
        if (state.isSemifinal()) {
            int bottomPairsCount = (int) Math.ceil(state.getBottomSectionWrestlers().size() / 2.0);
            int outcomes = state.getBottomRoundOutcome().size();
            return outcomes >= bottomPairsCount;
        } else if (state.isFinal() || state.isSuperFinal()) {
            return state.getTopRoundOutcome().containsKey(0);
        } else {
            List<Armwrestler> topWrestlers = state.getTopSectionWrestlers();
            List<Armwrestler> bottomWrestlers = state.getBottomSectionWrestlers();

            int topPairsCount = (int) Math.ceil(topWrestlers.size() / 2.0);
            int bottomPairsCount = bottomWrestlers.isEmpty() ? 0 : (int) Math.ceil(bottomWrestlers.size() / 2.0);

            int topOutcomes = state.getTopRoundOutcome().size();
            int bottomOutcomes = state.getBottomRoundOutcome().size();

            boolean topComplete = topOutcomes >= topPairsCount;
            boolean bottomComplete = bottomWrestlers.isEmpty() || bottomOutcomes >= bottomPairsCount;

            return topComplete && bottomComplete;
        }
    }

    private void buildWinnersAndLosers(List<Armwrestler> topWinners, List<Armwrestler> topLosers,
                                      List<Armwrestler> bottomWinners, List<Armwrestler> bottomLosers) {
        if (!state.isSemifinal()) {
            buildTopSectionResults(topWinners, topLosers);
        }
        buildBottomSectionResults(bottomWinners, bottomLosers);
    }

    private void buildTopSectionResults(List<Armwrestler> topWinners, List<Armwrestler> topLosers) {
        List<Armwrestler> byeWinners = new ArrayList<>();

        for (int i = 0; i < state.getTopSectionWrestlers().size(); i += 2) {
            if (i + 1 < state.getTopSectionWrestlers().size()) {
                Armwrestler w1 = state.getTopSectionWrestlers().get(i);
                Armwrestler w2 = state.getTopSectionWrestlers().get(i + 1);
                int matchIndex = i / 2;
                Integer winnerId = state.getTopRoundOutcome().get(matchIndex);

                Armwrestler winner = (winnerId != null && winnerId == w1.getId()) ? w1 : w2;
                Armwrestler loser = (winner == w1) ? w2 : w1;

                if (!winner.isEliminated()) topWinners.add(winner);
                if (!loser.isEliminated()) topLosers.add(loser);
            } else {
                Armwrestler w1 = state.getTopSectionWrestlers().get(i);
                if (!w1.isEliminated()) byeWinners.add(w1);
            }
        }

        topWinners.addAll(0, byeWinners);
    }

    private void buildBottomSectionResults(List<Armwrestler> bottomWinners, List<Armwrestler> bottomLosers) {
        List<Armwrestler> byeWinners = new ArrayList<>();

        for (int i = 0; i < state.getBottomSectionWrestlers().size(); i += 2) {
            if (i + 1 < state.getBottomSectionWrestlers().size()) {
                Armwrestler w1 = state.getBottomSectionWrestlers().get(i);
                Armwrestler w2 = state.getBottomSectionWrestlers().get(i + 1);
                int matchIndex = i / 2;
                Integer winnerId = state.getBottomRoundOutcome().get(matchIndex);

                Armwrestler winner = (winnerId != null && winnerId == w1.getId()) ? w1 : w2;
                Armwrestler loser = (winner == w1) ? w2 : w1;

                if (!winner.isEliminated()) bottomWinners.add(winner);
                if (!loser.isEliminated()) bottomLosers.add(loser);
            } else {
                Armwrestler w1 = state.getBottomSectionWrestlers().get(i);
                if (!w1.isEliminated()) byeWinners.add(w1);
            }
        }

        bottomWinners.addAll(0, byeWinners);
    }

    private void handleSemifinalCompletion(List<Armwrestler> bottomWinners, List<Armwrestler> topLosers) {
        Armwrestler bottomWinner = bottomWinners.get(0);
        Armwrestler topWinner = state.getTopSectionWrestlers().get(0);

        state.setSemifinal(false);
        state.setFinal(true);
        state.setTopSectionWrestlers(new ArrayList<>(List.of(topWinner)));
        state.setBottomSectionWrestlers(new ArrayList<>(List.of(bottomWinner)));
        state.setCurrentRound(state.getCurrentRound() + 1);
        state.clearRoundOutcomes();
        listener.onRoundDisplayRequested();
    }

    private void handleFinalCompletion(List<Armwrestler> topWinners, List<Armwrestler> topLosers) {
        Armwrestler topFinalWinner = state.getTopSectionWrestlers().get(0);
        Armwrestler bottomFinalWinner = state.getBottomSectionWrestlers().get(0);

        Integer finalWinnerId = state.getTopRoundOutcome().get(0);
        if (finalWinnerId == null) return;

        Armwrestler finalWinner = (finalWinnerId == topFinalWinner.getId()) ? topFinalWinner : bottomFinalWinner;
        Armwrestler finalLoser = (finalWinner == topFinalWinner) ? bottomFinalWinner : topFinalWinner;

        if (finalWinner == bottomFinalWinner && bottomFinalWinner.getLosses() == 1) {
            listener.onSuperFinalInitiated(finalWinner, finalLoser);
            state.setFinal(false);
            state.setSuperFinal(true);
            state.setTopSectionWrestlers(new ArrayList<>(List.of(finalWinner)));
            state.setBottomSectionWrestlers(new ArrayList<>(List.of(finalLoser)));
            state.setCurrentRound(state.getCurrentRound() + 1);
            state.clearRoundOutcomes();
            listener.onRoundDisplayRequested();
        } else {
            listener.onTournamentComplete(finalWinner);
        }
    }

    private void handleSuperFinalCompletion() {
        Armwrestler winner = state.getTopRoundOutcome().get(0) == state.getTopSectionWrestlers().get(0).getId() ?
                            state.getTopSectionWrestlers().get(0) : state.getBottomSectionWrestlers().get(0);
        listener.onTournamentComplete(winner);
    }

    private void goToSemifinal(List<Armwrestler> topWinners, List<Armwrestler> bottomWinners, List<Armwrestler> topLosers) {
        state.setSemifinal(true);
        state.setTopSectionWrestlers(new ArrayList<>(topWinners));
        state.setBottomSectionWrestlers(new ArrayList<>());
        state.getBottomSectionWrestlers().addAll(bottomWinners);
        state.getBottomSectionWrestlers().addAll(topLosers);
        state.setCurrentRound(state.getCurrentRound() + 1);
        state.clearRoundOutcomes();
        listener.onRoundDisplayRequested();
    }

    private void advanceToNextRound(List<Armwrestler> topWinners, List<Armwrestler> bottomWinners, List<Armwrestler> topLosers) {
        state.setTopSectionWrestlers(new ArrayList<>(topWinners));
        state.setBottomSectionWrestlers(new ArrayList<>());
        state.getBottomSectionWrestlers().addAll(bottomWinners);
        state.getBottomSectionWrestlers().addAll(topLosers);
        state.setCurrentRound(state.getCurrentRound() + 1);
        state.resetAllFlags();
        state.clearRoundOutcomes();
        listener.onRoundDisplayRequested();
    }
}

