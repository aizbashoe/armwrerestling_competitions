package com.objectedge.artem.ai.poc;

import com.objectedge.artem.ai.poc.forms.CompetitionForm;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CompetitionForm form = new CompetitionForm();
            form.setVisible(true);
        });
    }
}

