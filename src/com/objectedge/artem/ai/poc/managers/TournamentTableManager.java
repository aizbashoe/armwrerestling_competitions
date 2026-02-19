package com.objectedge.artem.ai.poc.managers;

import com.objectedge.artem.ai.poc.models.Armwrestler;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;

/**
 * Manages the tournament table display and updates
 */
public class TournamentTableManager {
    private JTable tournamentTable;
    private DefaultTableModel tournamentTableModel;

    public TournamentTableManager() {
        // Initialize table model with columns
        tournamentTableModel = new DefaultTableModel(new Object[]{"Rank", "Name", "Wins", "Losses", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tournamentTable = new JTable(tournamentTableModel);
    }

    public JTable getTournamentTable() {
        return tournamentTable;
    }

    public DefaultTableModel getTableModel() {
        return tournamentTableModel;
    }

    public void updateTable(List<Armwrestler> allParticipants) {
        // Create a copy and sort
        List<Armwrestler> sortedWrestlers = new ArrayList<>(allParticipants);

        // Sort by: active first, then by wins desc, then by losses asc, then by surname
        sortedWrestlers.sort((a, b) -> {
            // Active wrestlers first
            if (a.isEliminated() != b.isEliminated()) {
                return a.isEliminated() ? 1 : -1;
            }
            // Then by wins descending
            if (a.getWins() != b.getWins()) {
                return Integer.compare(b.getWins(), a.getWins());
            }
            // Then by losses ascending
            if (a.getLosses() != b.getLosses()) {
                return Integer.compare(a.getLosses(), b.getLosses());
            }
            // Finally by surname
            return a.getSurname().compareTo(b.getSurname());
        });

        // Clear table
        tournamentTableModel.setRowCount(0);
        int rank = 1;
        for (Armwrestler w : sortedWrestlers) {
            String status = w.isEliminated() ? "ELIMINATED" : "ACTIVE";
            tournamentTableModel.addRow(new Object[]{
                rank,
                w.getName() + " " + w.getSurname(),
                w.getWins(),
                w.getLosses(),
                status
            });
            rank++;
        }
    }

    public long getActiveWrestlerCount(List<Armwrestler> allParticipants) {
        return allParticipants.stream().filter(p -> !p.isEliminated()).count();
    }
}

