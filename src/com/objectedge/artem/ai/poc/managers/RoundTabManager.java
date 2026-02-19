package com.objectedge.artem.ai.poc.managers;

import com.objectedge.artem.ai.poc.forms.RoundTab;
import com.objectedge.artem.ai.poc.models.Armwrestler;
import com.objectedge.artem.ai.poc.models.TournamentState;
import com.objectedge.artem.ai.poc.helpers.MatchPanelFactory;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Manages round tabs and provides navigation between rounds
 */
public class RoundTabManager {
    private JTabbedPane tabbedPane;
    private Map<Integer, RoundTab> roundTabs;
    private TournamentState state;
    private MatchPanelFactory.MatchSelectionListener matchListener;
    private MatchPanelFactory.FinalMatchSelectionListener finalListener;
    private RoundDisplayManager roundDisplayManager;
    private int nextRoundNumber = 1;

    public RoundTabManager(TournamentState state,
                          MatchPanelFactory.MatchSelectionListener matchListener,
                          MatchPanelFactory.FinalMatchSelectionListener finalListener) {
        this.tabbedPane = new JTabbedPane();
        this.roundTabs = new LinkedHashMap<>();
        this.state = state;
        this.matchListener = matchListener;
        this.finalListener = finalListener;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void setRoundDisplayManager(RoundDisplayManager roundDisplayManager) {
        this.roundDisplayManager = roundDisplayManager;
    }

    public void createNewRound() {
        String roundLabel = getRoundLabel();
        RoundTab tab = new RoundTab(nextRoundNumber, state, roundLabel, matchListener, finalListener);
        roundTabs.put(nextRoundNumber, tab);

        tabbedPane.addTab(roundLabel, tab);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        // Display round content in the tab
        displayRoundInTab(tab);

        nextRoundNumber++;
    }

    private void displayRoundInTab(RoundTab tab) {
        JPanel contentPanel = tab.getContentPanel();
        contentPanel.removeAll();


        // Display sections based on current stage
        if (!state.isSemifinal() && !state.isFinal() && !state.isSuperFinal()) {
            displayTopSection(contentPanel);
        }

        if (state.getBottomSectionWrestlers().size() > 0 && !state.isFinal() && !state.isSuperFinal()) {
            displayBottomSection(contentPanel);
        }

        if ((state.isFinal() || state.isSuperFinal()) &&
            state.getTopSectionWrestlers().size() == 1 &&
            state.getBottomSectionWrestlers().size() == 1) {
            displayFinalMatch(contentPanel);
        }

        contentPanel.add(Box.createVerticalGlue());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void displayTopSection(JPanel panel) {
        List<Armwrestler> wrestlers = state.getTopSectionWrestlers();
        Map<Integer, Integer> roundOutcome = state.getTopRoundOutcome();

        // Create section panel with header for Round 2+ (but not Round 1)
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        if (state.getCurrentRound() > 1) {
            sectionPanel.setBorder(BorderFactory.createTitledBorder("Top"));
        }

        // Calculate if we need pair wrappers
        int totalPairs = (int) Math.ceil(wrestlers.size() / 2.0);
        boolean needsPairWrapper = totalPairs > 1;

        for (int i = 0; i < wrestlers.size(); i += 2) {
            int pairNumber = (i / 2) + 1;

            if (i + 1 < wrestlers.size()) {
                Armwrestler w1 = wrestlers.get(i);
                Armwrestler w2 = wrestlers.get(i + 1);

                if (needsPairWrapper) {
                    JPanel pairWrapper = new JPanel();
                    pairWrapper.setLayout(new BoxLayout(pairWrapper, BoxLayout.Y_AXIS));
                    pairWrapper.setAlignmentX(JPanel.LEFT_ALIGNMENT);
                    pairWrapper.setBorder(BorderFactory.createTitledBorder("Pair " + pairNumber));

                    JPanel matchPanel = MatchPanelFactory.createMatchPanel(w1, w2, i / 2, true,
                                                                          roundOutcome, matchListener);
                    pairWrapper.add(matchPanel);
                    sectionPanel.add(pairWrapper);
                } else {
                    JPanel matchPanel = MatchPanelFactory.createMatchPanel(w1, w2, i / 2, true,
                                                                          roundOutcome, matchListener);
                    sectionPanel.add(matchPanel);
                }
                sectionPanel.add(Box.createVerticalStrut(10));
            } else {
                Armwrestler w1 = wrestlers.get(i);
                int matchIndex = i / 2;

                if (needsPairWrapper) {
                    JPanel pairWrapper = new JPanel();
                    pairWrapper.setLayout(new BoxLayout(pairWrapper, BoxLayout.Y_AXIS));
                    pairWrapper.setAlignmentX(JPanel.LEFT_ALIGNMENT);
                    pairWrapper.setBorder(BorderFactory.createTitledBorder("Pair " + pairNumber));

                    JPanel byePanel = MatchPanelFactory.createByePanel(w1);
                    pairWrapper.add(byePanel);
                    sectionPanel.add(pairWrapper);
                } else {
                    JPanel byePanel = MatchPanelFactory.createByePanel(w1);
                    sectionPanel.add(byePanel);
                }
                sectionPanel.add(Box.createVerticalStrut(10));

                // Auto-add bye pair outcome (wrestler with no pair gets automatic win)
                if (!roundOutcome.containsKey(matchIndex)) {
                    roundOutcome.put(matchIndex, w1.getId());
                    w1.incrementWins();
                }
            }
        }

        panel.add(sectionPanel);
        panel.add(Box.createVerticalStrut(15));
    }

    private void displayBottomSection(JPanel panel) {
        List<Armwrestler> wrestlers = state.getBottomSectionWrestlers();
        Map<Integer, Integer> roundOutcome = state.getBottomRoundOutcome();

        // Create section panel with header for Round 2+ (but not Round 1 or SEMIFINAL)
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        if (state.getCurrentRound() > 1 && !state.isSemifinal()) {
            sectionPanel.setBorder(BorderFactory.createTitledBorder("Bottom"));
        }

        // ...existing code...
        int totalPairs = (int) Math.ceil(wrestlers.size() / 2.0);
        boolean needsPairWrapper = totalPairs > 1;

        for (int i = 0; i < wrestlers.size(); i += 2) {
            int pairNumber = (i / 2) + 1;

            if (i + 1 < wrestlers.size()) {
                Armwrestler w1 = wrestlers.get(i);
                Armwrestler w2 = wrestlers.get(i + 1);

                if (needsPairWrapper) {
                    JPanel pairWrapper = new JPanel();
                    pairWrapper.setLayout(new BoxLayout(pairWrapper, BoxLayout.Y_AXIS));
                    pairWrapper.setAlignmentX(JPanel.LEFT_ALIGNMENT);
                    pairWrapper.setBorder(BorderFactory.createTitledBorder("Pair " + pairNumber));

                    JPanel matchPanel = MatchPanelFactory.createMatchPanel(w1, w2, i / 2, false,
                                                                          roundOutcome, matchListener);
                    pairWrapper.add(matchPanel);
                    sectionPanel.add(pairWrapper);
                } else {
                    JPanel matchPanel = MatchPanelFactory.createMatchPanel(w1, w2, i / 2, false,
                                                                          roundOutcome, matchListener);
                    sectionPanel.add(matchPanel);
                }
                sectionPanel.add(Box.createVerticalStrut(10));
            } else {
                Armwrestler w1 = wrestlers.get(i);
                int matchIndex = i / 2;

                if (needsPairWrapper) {
                    JPanel pairWrapper = new JPanel();
                    pairWrapper.setLayout(new BoxLayout(pairWrapper, BoxLayout.Y_AXIS));
                    pairWrapper.setAlignmentX(JPanel.LEFT_ALIGNMENT);
                    pairWrapper.setBorder(BorderFactory.createTitledBorder("Pair " + pairNumber));

                    JPanel byePanel = MatchPanelFactory.createByePanel(w1);
                    pairWrapper.add(byePanel);
                    sectionPanel.add(pairWrapper);
                } else {
                    JPanel byePanel = MatchPanelFactory.createByePanel(w1);
                    sectionPanel.add(byePanel);
                }
                sectionPanel.add(Box.createVerticalStrut(10));

                // Auto-add bye pair outcome only if not already set (wrestler with no pair gets automatic win)
                if (!roundOutcome.containsKey(matchIndex)) {
                    roundOutcome.put(matchIndex, w1.getId());
                    w1.incrementWins();
                }
            }
        }

        panel.add(sectionPanel);
        panel.add(Box.createVerticalStrut(15));
    }

    private void displayFinalMatch(JPanel panel) {
        Armwrestler topChamp = state.getTopSectionWrestlers().get(0);
        Armwrestler bottomChamp = state.getBottomSectionWrestlers().get(0);

        JPanel finalMatchPanel = MatchPanelFactory.createFinalMatchPanel(topChamp, bottomChamp,
                                                                         state.getTopRoundOutcome(), finalListener);
        panel.add(finalMatchPanel);
        panel.add(Box.createVerticalStrut(15));
    }

    private String getRoundLabel() {
        if (state.isSuperFinal()) {
            return "SUPER-FINAL";
        } else if (state.isFinal()) {
            return "FINAL";
        } else if (state.isSemifinal()) {
            return "SEMIFINAL";
        } else {
            return "Round " + nextRoundNumber;
        }
    }

    public void refreshCurrentTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex >= 0) {
            Component selectedComponent = tabbedPane.getComponentAt(selectedIndex);
            if (selectedComponent instanceof RoundTab) {
                RoundTab tab = (RoundTab) selectedComponent;
                displayRoundInTab(tab);
            }
        }
    }
}


