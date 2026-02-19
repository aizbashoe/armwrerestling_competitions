package com.objectedge.artem.ai.poc.managers;

import com.objectedge.artem.ai.poc.models.TournamentState;
import com.objectedge.artem.ai.poc.helpers.MatchPanelFactory;
import com.objectedge.artem.ai.poc.models.Armwrestler;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Manages the display of tournament rounds
 */
public class RoundDisplayManager {
    private JPanel mainContentPanel;
    private TournamentState state;
    private MatchPanelFactory.MatchSelectionListener matchListener;
    private MatchPanelFactory.FinalMatchSelectionListener finalListener;

    public RoundDisplayManager(JPanel contentPanel, TournamentState state,
                             MatchPanelFactory.MatchSelectionListener matchListener,
                             MatchPanelFactory.FinalMatchSelectionListener finalListener) {
        this.mainContentPanel = contentPanel;
        this.state = state;
        this.matchListener = matchListener;
        this.finalListener = finalListener;
    }

    public void displayRound() {
        mainContentPanel.removeAll();

        // Round/Stage label
        String roundLabel = getRoundLabel();
        JLabel roundLabelComponent = new JLabel(roundLabel);
        roundLabelComponent.setFont(roundLabelComponent.getFont().deriveFont(18f));
        roundLabelComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContentPanel.add(roundLabelComponent);
        mainContentPanel.add(Box.createVerticalStrut(15));

        // Display sections based on current stage
        if (!state.isSemifinal() && !state.isFinal() && !state.isSuperFinal()) {
            displayTopSection();
        }

        if (state.getBottomSectionWrestlers().size() > 0 && !state.isFinal() && !state.isSuperFinal()) {
            displayBottomSection();
        }

        if ((state.isFinal() || state.isSuperFinal()) &&
            state.getTopSectionWrestlers().size() == 1 &&
            state.getBottomSectionWrestlers().size() == 1) {
            displayFinalMatch();
        }

        mainContentPanel.add(Box.createVerticalGlue());
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void highlightIncompletePairs() {
        // Find all pair wrapper panels and highlight those without winners
        boolean isSemifinal = state.isSemifinal();
        boolean isFinal = state.isFinal();
        boolean isSuperFinal = state.isSuperFinal();

        if (!isSemifinal && !isFinal && !isSuperFinal) {
            highlightIncompletePairsInSection(state.getTopSectionWrestlers(), state.getTopRoundOutcome());
            if (state.getBottomSectionWrestlers().size() > 0) {
                highlightIncompletePairsInSection(state.getBottomSectionWrestlers(), state.getBottomRoundOutcome());
            }
        } else if (isSemifinal) {
            highlightIncompletePairsInSection(state.getBottomSectionWrestlers(), state.getBottomRoundOutcome());
        } else if (isFinal || isSuperFinal) {
            highlightIncompleteFinal();
        }
    }

    private void highlightIncompletePairsInSection(List<Armwrestler> wrestlers, Map<Integer, Integer> roundOutcome) {
        // Find all pair wrapper panels and apply red border to incomplete pairs
        // Only highlight pairs with 2 wrestlers - skip bye pairs (1 wrestler)
        Component[] components = mainContentPanel.getComponents();

        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;

                // Check if this is a section panel (Top or Bottom)
                if (isSectionPanel(panel)) {
                    // Iterate through pair wrappers within this section
                    Component[] sectionComponents = panel.getComponents();
                    for (Component sectionComp : sectionComponents) {
                        if (sectionComp instanceof JPanel) {
                            JPanel possiblePairWrapper = (JPanel) sectionComp;

                            // Check if this is a pair wrapper (has titled border with "Pair")
                            if (isPairWrapper(possiblePairWrapper)) {
                                int pairIndex = extractPairNumber(possiblePairWrapper);
                                // Only highlight if this pair has 2 wrestlers and no winner selected
                                // Skip bye pairs (pairs with 1 wrestler)
                                if (pairIndex >= 0 && pairIndex < wrestlers.size()) {
                                    int startIdx = pairIndex * 2;
                                    int endIdx = startIdx + 1;

                                    // Only highlight if pair has 2 wrestlers (not a bye)
                                    if (endIdx < wrestlers.size() && !roundOutcome.containsKey(pairIndex)) {
                                        // This is a complete pair (2 wrestlers) with no winner - highlight it
                                        possiblePairWrapper.setBorder(BorderFactory.createLineBorder(new Color(255, 0, 0), 3));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void highlightIncompleteFinal() {
        // Find the final match panel and highlight if no winner selected
        if (!state.getTopRoundOutcome().containsKey(0)) {
            Component[] components = mainContentPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel) comp;
                    if (isFinalPanel(panel)) {
                        panel.setBorder(BorderFactory.createLineBorder(new Color(255, 0, 0), 3));
                    }
                }
            }
        }
    }

    private boolean isSectionPanel(JPanel panel) {
        return panel.getBorder() instanceof javax.swing.border.TitledBorder &&
               (getTitledBorderText(panel).contains("Top") ||
                getTitledBorderText(panel).contains("Bottom") ||
                getTitledBorderText(panel).contains("Pairs") ||
                getTitledBorderText(panel).contains("SEMIFINAL"));
    }

    private boolean isPairWrapper(JPanel panel) {
        return panel.getBorder() instanceof javax.swing.border.TitledBorder &&
               getTitledBorderText(panel).contains("Pair");
    }

    private boolean isFinalPanel(JPanel panel) {
        return panel.getBorder() instanceof javax.swing.border.TitledBorder &&
               (getTitledBorderText(panel).equals("FINAL") ||
                getTitledBorderText(panel).equals("SUPER-FINAL"));
    }

    private String getTitledBorderText(JPanel panel) {
        if (panel.getBorder() instanceof javax.swing.border.TitledBorder) {
            javax.swing.border.TitledBorder tb = (javax.swing.border.TitledBorder) panel.getBorder();
            return tb.getTitle();
        }
        return "";
    }

    private int extractPairNumber(JPanel panel) {
        String title = getTitledBorderText(panel);
        if (title.startsWith("Pair ")) {
            try {
                return Integer.parseInt(title.substring(5)) - 1;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    public void unhighlightIncompletePairs() {
        // Remove red highlighting from all pair wrappers and final panels
        unhighlightPanelsRecursively(mainContentPanel);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void unhighlightSpecificPair(int matchIndex, boolean isTopSection) {
        // Remove red highlight only for the specific pair where winner was selected
        List<Armwrestler> wrestlers = isTopSection ? state.getTopSectionWrestlers() : state.getBottomSectionWrestlers();
        Component[] components = mainContentPanel.getComponents();

        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;

                // Check if this is a section panel (Top or Bottom)
                if (isSectionPanel(panel)) {
                    // Iterate through pair wrappers within this section
                    Component[] sectionComponents = panel.getComponents();
                    for (Component sectionComp : sectionComponents) {
                        if (sectionComp instanceof JPanel) {
                            JPanel possiblePairWrapper = (JPanel) sectionComp;

                            // Check if this is a pair wrapper
                            if (isPairWrapper(possiblePairWrapper)) {
                                int pairIndex = extractPairNumber(possiblePairWrapper);
                                // If this is the pair that just had a winner selected
                                if (pairIndex == matchIndex) {
                                    // Remove red border if it exists
                                    if (hasRedBorder(possiblePairWrapper)) {
                                        String title = getTitledBorderText(possiblePairWrapper);
                                        possiblePairWrapper.setBorder(BorderFactory.createTitledBorder(title));
                                        possiblePairWrapper.revalidate();
                                        possiblePairWrapper.repaint();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void unhighlightSpecificFinal() {
        // Remove red highlight from final match panel
        Component[] components = mainContentPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (isFinalPanel(panel)) {
                    if (hasRedBorder(panel)) {
                        String title = getTitledBorderText(panel);
                        panel.setBorder(BorderFactory.createTitledBorder(title));
                        panel.revalidate();
                        panel.repaint();
                    }
                }
            }
        }
    }

    private void unhighlightPanelsRecursively(Container container) {
        Component[] components = container.getComponents();

        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;

                // Check if this panel has a red border and restore it
                if (hasRedBorder(panel)) {
                    String title = getTitledBorderText(panel);
                    // Always restore the border with the same title
                    panel.setBorder(BorderFactory.createTitledBorder(title));
                    panel.revalidate();
                    panel.repaint();
                }

                // Recursively check nested panels
                if (panel.getComponentCount() > 0) {
                    unhighlightPanelsRecursively(panel);
                }
            }
        }
    }

    private boolean hasRedBorder(JPanel panel) {
        if (panel.getBorder() instanceof javax.swing.border.LineBorder) {
            javax.swing.border.LineBorder lb = (javax.swing.border.LineBorder) panel.getBorder();
            Color lineColor = lb.getLineColor();
            // Check if the color is red (RGB: 255, 0, 0)
            return lineColor != null && lineColor.getRed() == 255 && lineColor.getGreen() == 0 && lineColor.getBlue() == 0;
        }
        return false;
    }

    private String getRoundLabel() {
        if (state.isSuperFinal()) {
            return "SUPER-FINAL";
        } else if (state.isFinal()) {
            return "FINAL";
        } else if (state.isSemifinal()) {
            return "SEMIFINAL";
        } else {
            return "Round " + state.getCurrentRound();
        }
    }

    private void displayTopSection() {
        String topTitle = state.getCurrentRound() == 1 ? "Pairs" : "Top";

        JPanel topSectionPanel = new JPanel();
        topSectionPanel.setLayout(new BoxLayout(topSectionPanel, BoxLayout.Y_AXIS));
        topSectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topSectionPanel.setBorder(BorderFactory.createTitledBorder(topTitle));

        state.getTopRoundOutcome().clear();
        List<Armwrestler> wrestlers = state.getTopSectionWrestlers();

        // Calculate if we need pair wrappers (only if more than 1 pair)
        int totalPairs = (int) Math.ceil(wrestlers.size() / 2.0);
        boolean needsPairWrapper = totalPairs > 1;

        for (int i = 0; i < wrestlers.size(); i += 2) {
            int pairNumber = (i / 2) + 1;

            if (i + 1 < wrestlers.size()) {
                Armwrestler w1 = wrestlers.get(i);
                Armwrestler w2 = wrestlers.get(i + 1);

                if (needsPairWrapper) {
                    // Create pair wrapper section when multiple pairs exist
                    JPanel pairWrapper = new JPanel();
                    pairWrapper.setLayout(new BoxLayout(pairWrapper, BoxLayout.Y_AXIS));
                    pairWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
                    pairWrapper.setBorder(BorderFactory.createTitledBorder("Pair " + pairNumber));

                    JPanel matchPanel = MatchPanelFactory.createMatchPanel(w1, w2, i / 2, true,
                                                                          state.getTopRoundOutcome(), matchListener);
                    pairWrapper.add(matchPanel);
                    topSectionPanel.add(pairWrapper);
                } else {
                    // Add match directly without wrapper when only 1 pair
                    JPanel matchPanel = MatchPanelFactory.createMatchPanel(w1, w2, i / 2, true,
                                                                          state.getTopRoundOutcome(), matchListener);
                    topSectionPanel.add(matchPanel);
                }
                topSectionPanel.add(Box.createVerticalStrut(10));
            } else {
                Armwrestler w1 = wrestlers.get(i);
                int matchIndex = i / 2;

                if (needsPairWrapper) {
                    // Create pair wrapper section for bye when multiple pairs exist
                    JPanel pairWrapper = new JPanel();
                    pairWrapper.setLayout(new BoxLayout(pairWrapper, BoxLayout.Y_AXIS));
                    pairWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
                    pairWrapper.setBorder(BorderFactory.createTitledBorder("Pair " + pairNumber));

                    JPanel byePanel = MatchPanelFactory.createByePanel(w1);
                    pairWrapper.add(byePanel);
                    topSectionPanel.add(pairWrapper);
                } else {
                    // Add bye directly without wrapper when only 1 pair
                    JPanel byePanel = MatchPanelFactory.createByePanel(w1);
                    topSectionPanel.add(byePanel);
                }
                topSectionPanel.add(Box.createVerticalStrut(10));
                state.getTopRoundOutcome().put(matchIndex, w1.getId());
                w1.incrementWins();
            }
        }

        mainContentPanel.add(topSectionPanel);
        mainContentPanel.add(Box.createVerticalStrut(15));
    }

    private void displayBottomSection() {
        String bottomTitle = state.isSemifinal() ? "SEMIFINAL" : "Bottom";

        JPanel bottomSectionPanel = new JPanel();
        bottomSectionPanel.setLayout(new BoxLayout(bottomSectionPanel, BoxLayout.Y_AXIS));
        bottomSectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomSectionPanel.setBorder(BorderFactory.createTitledBorder(bottomTitle));

        state.getBottomRoundOutcome().clear();
        List<Armwrestler> wrestlers = state.getBottomSectionWrestlers();

        // Calculate if we need pair wrappers (only if more than 1 pair)
        int totalPairs = (int) Math.ceil(wrestlers.size() / 2.0);
        boolean needsPairWrapper = totalPairs > 1;

        for (int i = 0; i < wrestlers.size(); i += 2) {
            int pairNumber = (i / 2) + 1;

            if (i + 1 < wrestlers.size()) {
                Armwrestler w1 = wrestlers.get(i);
                Armwrestler w2 = wrestlers.get(i + 1);

                if (needsPairWrapper) {
                    // Create pair wrapper section when multiple pairs exist
                    JPanel pairWrapper = new JPanel();
                    pairWrapper.setLayout(new BoxLayout(pairWrapper, BoxLayout.Y_AXIS));
                    pairWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
                    pairWrapper.setBorder(BorderFactory.createTitledBorder("Pair " + pairNumber));

                    JPanel matchPanel = MatchPanelFactory.createMatchPanel(w1, w2, i / 2, false,
                                                                          state.getBottomRoundOutcome(), matchListener);
                    pairWrapper.add(matchPanel);
                    bottomSectionPanel.add(pairWrapper);
                } else {
                    // Add match directly without wrapper when only 1 pair
                    JPanel matchPanel = MatchPanelFactory.createMatchPanel(w1, w2, i / 2, false,
                                                                          state.getBottomRoundOutcome(), matchListener);
                    bottomSectionPanel.add(matchPanel);
                }
                bottomSectionPanel.add(Box.createVerticalStrut(10));
            } else {
                Armwrestler w1 = wrestlers.get(i);
                int matchIndex = i / 2;

                if (needsPairWrapper) {
                    // Create pair wrapper section for bye when multiple pairs exist
                    JPanel pairWrapper = new JPanel();
                    pairWrapper.setLayout(new BoxLayout(pairWrapper, BoxLayout.Y_AXIS));
                    pairWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
                    pairWrapper.setBorder(BorderFactory.createTitledBorder("Pair " + pairNumber));

                    JPanel byePanel = MatchPanelFactory.createByePanel(w1);
                    pairWrapper.add(byePanel);
                    bottomSectionPanel.add(pairWrapper);
                } else {
                    // Add bye directly without wrapper when only 1 pair
                    JPanel byePanel = MatchPanelFactory.createByePanel(w1);
                    bottomSectionPanel.add(byePanel);
                }
                bottomSectionPanel.add(Box.createVerticalStrut(10));
                state.getBottomRoundOutcome().put(matchIndex, w1.getId());
                w1.incrementWins();
            }
        }

        mainContentPanel.add(bottomSectionPanel);
        mainContentPanel.add(Box.createVerticalStrut(15));
    }

    private void displayFinalMatch() {
        JPanel finalPanel = new JPanel();
        finalPanel.setLayout(new BoxLayout(finalPanel, BoxLayout.Y_AXIS));
        finalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String panelTitle = state.isSuperFinal() ? "SUPER-FINAL" : "FINAL";
        finalPanel.setBorder(BorderFactory.createTitledBorder(panelTitle));

        Armwrestler topChamp = state.getTopSectionWrestlers().get(0);
        Armwrestler bottomChamp = state.getBottomSectionWrestlers().get(0);

        JPanel finalMatchPanel = MatchPanelFactory.createFinalMatchPanel(topChamp, bottomChamp,
                                                                         state.getTopRoundOutcome(), finalListener);
        finalPanel.add(finalMatchPanel);
        finalPanel.add(Box.createVerticalStrut(10));

        mainContentPanel.add(finalPanel);
        mainContentPanel.add(Box.createVerticalStrut(15));
    }
}


