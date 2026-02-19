package com.objectedge.artem.ai.poc.helpers;

import com.objectedge.artem.ai.poc.models.Armwrestler;
import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Factory class for creating match panels with wrestler buttons
 */
public class MatchPanelFactory {

    public static JPanel createMatchPanel(Armwrestler wrestler1, Armwrestler wrestler2, int matchIndex,
                                         boolean isTopSection, Map<Integer, Integer> roundOutcome,
                                         MatchSelectionListener listener) {
        JPanel matchPanel = new JPanel(new java.awt.GridLayout(2, 1, 0, 5));
        matchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        matchPanel.setMaximumSize(new Dimension(300, 70));
        matchPanel.setPreferredSize(new Dimension(300, 70));
        matchPanel.setMinimumSize(new Dimension(300, 70));

        Integer winnerId = roundOutcome.get(matchIndex);

        JButton button1 = new JButton();
        JButton button2 = new JButton();

        // Set fixed size to prevent resizing - strict constraints
        Dimension buttonSize = new Dimension(280, 30);
        button1.setPreferredSize(buttonSize);
        button1.setMinimumSize(buttonSize);
        button1.setMaximumSize(buttonSize);
        button1.setSize(buttonSize);

        button2.setPreferredSize(buttonSize);
        button2.setMinimumSize(buttonSize);
        button2.setMaximumSize(buttonSize);
        button2.setSize(buttonSize);

        updateWrestlerButton(button1, wrestler1, winnerId != null && winnerId == wrestler1.getId());
        updateWrestlerButton(button2, wrestler2, winnerId != null && winnerId == wrestler2.getId());

        button1.addActionListener(e -> {
            listener.onWinnerSelected(wrestler1.getId(), wrestler2.getId(), matchIndex, button1, button2, isTopSection);
            // Immediately update button appearances without layout recalculation
            button1.revalidate();
            button2.revalidate();
        });
        button2.addActionListener(e -> {
            listener.onWinnerSelected(wrestler2.getId(), wrestler1.getId(), matchIndex, button1, button2, isTopSection);
            // Immediately update button appearances without layout recalculation
            button1.revalidate();
            button2.revalidate();
        });

        matchPanel.add(button1);
        matchPanel.add(button2);

        return matchPanel;
    }

    public static JPanel createByePanel(Armwrestler wrestler) {
        JPanel byePanel = new JPanel();
        byePanel.setLayout(new BoxLayout(byePanel, BoxLayout.Y_AXIS));
        byePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        byePanel.setMaximumSize(new Dimension(300, 50));
        byePanel.setPreferredSize(new Dimension(300, 50));
        byePanel.setMinimumSize(new Dimension(300, 50));

        JButton byeButton = new JButton();
        // Set fixed size to prevent resizing - strict constraints
        Dimension buttonSize = new Dimension(280, 30);
        byeButton.setPreferredSize(buttonSize);
        byeButton.setMinimumSize(buttonSize);
        byeButton.setMaximumSize(buttonSize);
        byeButton.setSize(buttonSize);

        updateWrestlerButton(byeButton, wrestler, true);
        byePanel.add(byeButton);
        byeButton.setEnabled(true);

        return byePanel;
    }

    public static JPanel createFinalMatchPanel(Armwrestler topChamp, Armwrestler bottomChamp,
                                              Map<Integer, Integer> roundOutcome,
                                              FinalMatchSelectionListener listener) {
        JPanel finalMatchPanel = new JPanel();
        finalMatchPanel.setLayout(new BoxLayout(finalMatchPanel, BoxLayout.Y_AXIS));
        finalMatchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        finalMatchPanel.setMaximumSize(new Dimension(300, 70));
        finalMatchPanel.setPreferredSize(new Dimension(300, 70));
        finalMatchPanel.setMinimumSize(new Dimension(300, 70));

        Integer finalWinnerId = roundOutcome.get(0);

        JButton buttonTopChamp = new JButton();
        JButton buttonBottomChamp = new JButton();

        // Set fixed size to prevent resizing - strict constraints
        Dimension buttonSize = new Dimension(280, 30);
        buttonTopChamp.setPreferredSize(buttonSize);
        buttonTopChamp.setMinimumSize(buttonSize);
        buttonTopChamp.setMaximumSize(buttonSize);
        buttonTopChamp.setSize(buttonSize);
        buttonTopChamp.setAlignmentX(Component.LEFT_ALIGNMENT);

        buttonBottomChamp.setPreferredSize(buttonSize);
        buttonBottomChamp.setMinimumSize(buttonSize);
        buttonBottomChamp.setMaximumSize(buttonSize);
        buttonBottomChamp.setSize(buttonSize);
        buttonBottomChamp.setAlignmentX(Component.LEFT_ALIGNMENT);

        updateWrestlerButton(buttonTopChamp, topChamp, finalWinnerId != null && finalWinnerId == topChamp.getId());
        updateWrestlerButton(buttonBottomChamp, bottomChamp, finalWinnerId != null && finalWinnerId == bottomChamp.getId());

        buttonTopChamp.addActionListener(e -> {
            listener.onFinalWinnerSelected(topChamp.getId(), bottomChamp.getId(), buttonTopChamp, buttonBottomChamp);
            buttonTopChamp.revalidate();
            buttonBottomChamp.revalidate();
        });
        buttonBottomChamp.addActionListener(e -> {
            listener.onFinalWinnerSelected(bottomChamp.getId(), topChamp.getId(), buttonTopChamp, buttonBottomChamp);
            buttonTopChamp.revalidate();
            buttonBottomChamp.revalidate();
        });

        finalMatchPanel.add(buttonTopChamp);
        finalMatchPanel.add(Box.createVerticalStrut(5));
        finalMatchPanel.add(buttonBottomChamp);

        return finalMatchPanel;
    }

    private static void updateWrestlerButton(JButton button, Armwrestler wrestler, boolean isWinner) {
        String baseName = wrestler.getName() + " " + wrestler.getSurname();
        String text;

        if (isWinner) {
            text = baseName + " (WINNER)";
        } else {
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

    // Listener interfaces
    public interface MatchSelectionListener {
        void onWinnerSelected(int winnerId, int loserId, int matchIndex, JButton button1, JButton button2, boolean isTopSection);
    }

    public interface FinalMatchSelectionListener {
        void onFinalWinnerSelected(int winnerId, int loserId, JButton buttonWinner, JButton buttonLoser);
    }
}

