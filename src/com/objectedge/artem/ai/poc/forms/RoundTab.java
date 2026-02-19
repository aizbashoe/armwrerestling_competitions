package com.objectedge.artem.ai.poc.forms;

import com.objectedge.artem.ai.poc.helpers.MatchPanelFactory;
import com.objectedge.artem.ai.poc.models.TournamentState;
import javax.swing.*;
import java.util.*;

/**
 * Represents a single round tab in the tabbed interface
 */
public class RoundTab extends JPanel {
    private int roundNumber;
    private TournamentState state;
    private JPanel contentPanel;
    private MatchPanelFactory.MatchSelectionListener matchListener;
    private MatchPanelFactory.FinalMatchSelectionListener finalListener;
    private String roundLabel;

    public RoundTab(int roundNumber, TournamentState state, String roundLabel,
                   MatchPanelFactory.MatchSelectionListener matchListener,
                   MatchPanelFactory.FinalMatchSelectionListener finalListener) {
        this.roundNumber = roundNumber;
        this.state = state;
        this.roundLabel = roundLabel;
        this.matchListener = matchListener;
        this.finalListener = finalListener;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setAlignmentX(LEFT_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        add(scrollPane);
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getRoundLabel() {
        return roundLabel;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public void refresh() {
        contentPanel.removeAll();
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}

