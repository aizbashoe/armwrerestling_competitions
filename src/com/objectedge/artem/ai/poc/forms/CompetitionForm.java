package com.objectedge.artem.ai.poc.forms;

import com.objectedge.artem.ai.poc.models.Armwrestler;
import com.objectedge.artem.ai.poc.models.TournamentState;
import com.objectedge.artem.ai.poc.managers.RoundTabManager;
import com.objectedge.artem.ai.poc.managers.TournamentTableManager;
import com.objectedge.artem.ai.poc.managers.TournamentProgression;
import com.objectedge.artem.ai.poc.helpers.MatchPanelFactory;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Main form for the ArmWrestling Competition application.
 * Orchestrates the tournament using various manager classes.
 */
public class CompetitionForm extends JFrame {
    private TournamentState tournamentState;
    private RoundTabManager roundTabManager;
    private TournamentTableManager tableManager;
    private TournamentProgression tournamentProgression;

    private JPanel mainContentPanel;
    private ArmwrestlerForm armwrestlerForm;
    private JButton nextRoundButton;
    private JButton manageArmwrestlersButton;

    public CompetitionForm() {
        setTitle("ArmWrestling Competition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // Initialize state and managers
        tournamentState = new TournamentState();
        tableManager = new TournamentTableManager();
        tournamentProgression = new TournamentProgression(tournamentState, createProgressionListener());

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("ArmWrestling Competition");
        titleLabel.setFont(titleLabel.getFont().deriveFont(24f));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Top buttons
        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        manageArmwrestlersButton = new JButton("Manage Armwrestlers");
        manageArmwrestlersButton.addActionListener(e -> openArmwrestlerForm());
        topButtonPanel.add(manageArmwrestlersButton);
        mainPanel.add(topButtonPanel, BorderLayout.NORTH);

        // Initialize round tab manager
        roundTabManager = new RoundTabManager(tournamentState, createMatchListener(), createFinalListener());

        // Central content (rounds tabs) on left
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(roundTabManager.getTabbedPane(), BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Tournament panel on right
        JPanel tournamentPanel = new JPanel(new BorderLayout());
        tournamentPanel.setBorder(BorderFactory.createTitledBorder("Tournament Table"));
        JScrollPane tourScroll = new JScrollPane(tableManager.getTournamentTable());
        tourScroll.setPreferredSize(new Dimension(420, 600));
        tournamentPanel.add(tourScroll, BorderLayout.CENTER);
        mainPanel.add(tournamentPanel, BorderLayout.EAST);

        // Bottom next round
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        nextRoundButton = new JButton("Next Round");
        nextRoundButton.addActionListener(e -> tournamentProgression.proceedToNextRound());
        bottomButtonPanel.add(nextRoundButton);
        mainPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        add(mainPanel);


        tableManager.updateTable(tournamentState.getAllParticipants());
        updateNextRoundButtonVisibility();
    }

    private void openArmwrestlerForm() {
        armwrestlerForm = new ArmwrestlerForm(this);
        armwrestlerForm.setVisible(true);
    }

    public void displayCompetitionPairs(List<Armwrestler> wrestlersList) {
        tournamentState.setAllParticipants(new ArrayList<>(wrestlersList));
        tournamentState.initializeRound(wrestlersList);
        tableManager.updateTable(tournamentState.getAllParticipants());
        manageArmwrestlersButton.setVisible(false);

        // Create first round tab
        roundTabManager.createNewRound();
        updateNextRoundButtonVisibility();
    }

    private MatchPanelFactory.MatchSelectionListener createMatchListener() {
        return (winnerId, loserId, matchIndex, button1, button2, isTopSection) -> {
            List<Armwrestler> wrestlers = isTopSection ? tournamentState.getTopSectionWrestlers() : tournamentState.getBottomSectionWrestlers();
            Map<Integer, Integer> roundOutcome = isTopSection ? tournamentState.getTopRoundOutcome() : tournamentState.getBottomRoundOutcome();

            if (wrestlers == null || wrestlers.isEmpty()) return;

            Armwrestler winner = null, loser = null;
            for (Armwrestler w : wrestlers) {
                if (w != null) {
                    if (w.getId() == winnerId) winner = w;
                    if (w.getId() == loserId) loser = w;
                }
            }

            if (winner == null || loser == null) return;

            Integer prevWinnerId = roundOutcome.get(matchIndex);

            if (prevWinnerId != null && prevWinnerId != winnerId) {
                int idx1 = matchIndex * 2;
                int idx2 = matchIndex * 2 + 1;
                Armwrestler w1 = (idx1 < wrestlers.size()) ? wrestlers.get(idx1) : null;
                Armwrestler w2 = (idx2 < wrestlers.size()) ? wrestlers.get(idx2) : null;

                if (w1 != null && w2 != null) {
                    Armwrestler prevWinner = (prevWinnerId == w1.getId()) ? w1 : w2;
                    Armwrestler prevLoser = (prevWinnerId == w1.getId()) ? w2 : w1;
                    if (prevWinner != null && prevLoser != null) {
                        prevWinner.decrementWins();
                        prevLoser.decrementLosses();
                    }
                }
            }

            if (prevWinnerId != null && prevWinnerId == winnerId) return;

            winner.incrementWins();
            loser.incrementLosses();
            roundOutcome.put(matchIndex, winnerId);

            int idx1 = matchIndex * 2;
            int idx2 = matchIndex * 2 + 1;
            Armwrestler w1 = (idx1 < wrestlers.size()) ? wrestlers.get(idx1) : null;
            Armwrestler w2 = (idx2 < wrestlers.size()) ? wrestlers.get(idx2) : null;

            if (w1 != null && w2 != null) {
                updateButtonUI(button1, w1, w1.getId() == winnerId);
                updateButtonUI(button2, w2, w2.getId() == winnerId);
                // Only repaint the buttons themselves - no parent/panel repaints
                button1.revalidate();
                button1.repaint();
                button2.revalidate();
                button2.repaint();
            }

            // Update tournament table to reflect winner selection
            tableManager.updateTable(tournamentState.getAllParticipants());
        };
    }

    private MatchPanelFactory.FinalMatchSelectionListener createFinalListener() {
        return (winnerId, loserId, buttonWinner, buttonLoser) -> {
            // Store references BEFORE any state modifications
            Armwrestler topChamp = tournamentState.getTopSectionWrestlers().get(0);
            Armwrestler bottomChamp = tournamentState.getBottomSectionWrestlers().get(0);

            Armwrestler winner = (winnerId == topChamp.getId()) ? topChamp : bottomChamp;
            Armwrestler loser = (winner == topChamp) ? bottomChamp : topChamp;

            Integer prevWinnerId = tournamentState.getTopRoundOutcome().get(0);

            if (prevWinnerId != null && prevWinnerId != winnerId) {
                Armwrestler prevWinner = (prevWinnerId == topChamp.getId()) ? topChamp : bottomChamp;
                Armwrestler prevLoser = (prevWinnerId == topChamp.getId()) ? bottomChamp : topChamp;
                if (prevWinner != null && prevLoser != null) {
                    prevWinner.decrementWins();
                    prevLoser.decrementLosses();
                }
            }

            if (prevWinnerId != null && prevWinnerId == winnerId) return;

            winner.incrementWins();
            loser.incrementLosses();
            tournamentState.getTopRoundOutcome().put(0, winnerId);

            // Update button UI using SAME button objects passed in - never recreate
            // This ensures buttons stay in their original positions
            updateButtonUI(buttonWinner, winner, true);
            updateButtonUI(buttonLoser, loser, false);

            // Only repaint the buttons themselves - no parent/panel repaints
            buttonWinner.revalidate();
            buttonWinner.repaint();
            buttonLoser.revalidate();
            buttonLoser.repaint();

            // Update tournament table to reflect winner selection
            tableManager.updateTable(tournamentState.getAllParticipants());
        };
    }

    private TournamentProgression.TournamentProgressionListener createProgressionListener() {
        return new TournamentProgression.TournamentProgressionListener() {
            @Override
            public void onTournamentComplete(Armwrestler winner) {
                JOptionPane.showMessageDialog(CompetitionForm.this,
                    "Tournament Complete!\n\nWinner: " + winner.getName() + " " + winner.getSurname(),
                    "Tournament Over", JOptionPane.INFORMATION_MESSAGE);
                tableManager.updateTable(tournamentState.getAllParticipants());
                updateNextRoundButtonVisibility();
            }

            @Override
            public void onSuperFinalInitiated(Armwrestler finalist1, Armwrestler finalist2) {
                JOptionPane.showMessageDialog(CompetitionForm.this,
                    "SUPER-FINAL INITIATED!\n\n" + finalist1.getName() + " " + finalist1.getSurname() +
                    " (1 loss) vs " + finalist2.getName() + " " + finalist2.getSurname() + " (1 loss)\n\n" +
                    "Winner takes 1st place!",
                    "Super-Final", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void onRoundDisplayRequested() {
                tableManager.updateTable(tournamentState.getAllParticipants());
                roundTabManager.createNewRound();
                updateNextRoundButtonVisibility();
            }

            @Override
            public void onValidationFailed() {
                JOptionPane.showMessageDialog(CompetitionForm.this,
                    "Please select a winner for all matches before proceeding.",
                    "Incomplete Round", JOptionPane.WARNING_MESSAGE);
                // Highlight will be handled by the tab manager
            }
        };
    }

    private void updateButtonUI(JButton button, Armwrestler wrestler, boolean isWinner) {
        String baseName = wrestler.getName() + " " + wrestler.getSurname();
        String text;

        if (isWinner) {
            // Winner text with suffix
            text = baseName + " (WINNER)";
        } else {
            // Pad with spaces to maintain same width as when " (WINNER)" is added
            // " (WINNER)" is 9 characters, so we add 9 spaces
            text = baseName + "         ";
        }

        button.setText(text);

        if (isWinner) {
            button.setFont(button.getFont().deriveFont(java.awt.Font.BOLD, button.getFont().getSize()));
            button.setForeground(new Color(0, 128, 0));
        } else {
            button.setFont(button.getFont().deriveFont(java.awt.Font.PLAIN, button.getFont().getSize()));
            button.setForeground(UIManager.getColor("Button.foreground"));
        }
    }

    private void updateNextRoundButtonVisibility() {
        List<Armwrestler> participants = tournamentState.getAllParticipants();
        // If no participants yet (initial state), hide the button
        if (participants.isEmpty()) {
            nextRoundButton.setVisible(false);
            return;
        }

        long activeCount = tableManager.getActiveWrestlerCount(participants);
        // Hide button only if tournament is complete (1 or fewer active wrestlers)
        boolean tournamentOver = activeCount <= 1;
        nextRoundButton.setVisible(!tournamentOver);
    }
}


