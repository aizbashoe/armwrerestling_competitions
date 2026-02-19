package com.objectedge.artem.ai.poc.models;

import java.util.*;

/**
 * Manages the state of the tournament including round information and wrestler lists
 */
public class TournamentState {
    private List<Armwrestler> topSectionWrestlers;
    private List<Armwrestler> bottomSectionWrestlers;
    private Map<Integer, Integer> topRoundOutcome;
    private Map<Integer, Integer> bottomRoundOutcome;
    private int currentRound;
    private boolean isFinal;
    private boolean isSemifinal;
    private boolean isSuperFinal;
    private List<Armwrestler> allParticipants;

    public TournamentState() {
        this.topSectionWrestlers = new ArrayList<>();
        this.bottomSectionWrestlers = new ArrayList<>();
        this.topRoundOutcome = new HashMap<>();
        this.bottomRoundOutcome = new HashMap<>();
        this.currentRound = 1;
        this.isFinal = false;
        this.isSemifinal = false;
        this.isSuperFinal = false;
        this.allParticipants = new ArrayList<>();
    }

    // Getters
    public List<Armwrestler> getTopSectionWrestlers() { return topSectionWrestlers; }
    public List<Armwrestler> getBottomSectionWrestlers() { return bottomSectionWrestlers; }
    public Map<Integer, Integer> getTopRoundOutcome() { return topRoundOutcome; }
    public Map<Integer, Integer> getBottomRoundOutcome() { return bottomRoundOutcome; }
    public int getCurrentRound() { return currentRound; }
    public boolean isFinal() { return isFinal; }
    public boolean isSemifinal() { return isSemifinal; }
    public boolean isSuperFinal() { return isSuperFinal; }
    public List<Armwrestler> getAllParticipants() { return allParticipants; }

    // Setters
    public void setTopSectionWrestlers(List<Armwrestler> wrestlers) { this.topSectionWrestlers = wrestlers; }
    public void setBottomSectionWrestlers(List<Armwrestler> wrestlers) { this.bottomSectionWrestlers = wrestlers; }
    public void setCurrentRound(int round) { this.currentRound = round; }
    public void setFinal(boolean value) { this.isFinal = value; }
    public void setSemifinal(boolean value) { this.isSemifinal = value; }
    public void setSuperFinal(boolean value) { this.isSuperFinal = value; }
    public void setAllParticipants(List<Armwrestler> participants) { this.allParticipants = participants; }

    public void clearRoundOutcomes() {
        this.topRoundOutcome.clear();
        this.bottomRoundOutcome.clear();
    }

    public void resetAllFlags() {
        this.isSemifinal = false;
        this.isFinal = false;
        this.isSuperFinal = false;
    }

    public void initializeRound(List<Armwrestler> wrestlers) {
        this.topSectionWrestlers = new ArrayList<>(wrestlers);
        this.bottomSectionWrestlers = new ArrayList<>();
        this.currentRound = 1;
        this.topRoundOutcome.clear();
        this.bottomRoundOutcome.clear();
        this.isFinal = false;
        this.isSemifinal = false;
        this.isSuperFinal = false;
    }
}

