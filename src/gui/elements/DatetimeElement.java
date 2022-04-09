/*
Redstar Timeline Creator - A tool for easily creating visual timelines of events.
Copyright (C) 2022 Xavier Mercerweiss

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

Redstar may be contacted for inquiry at: <redstar.software.official@gmail.com>
 */

package gui.elements;

import gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A panel which collects an input datetime from the user.
 */
public class DatetimeElement extends JPanel {

    private final GUI gui;

    private final SimpleDateFormat formatter = new SimpleDateFormat("mm HH dd MM yyyy");
    private JTextField minuteField;
    private JTextField hourField;
    private JTextField dayField;
    private JTextField monthField;
    private JTextField yearField;


    // Initialization Methods

    /**
     * Constructs and initializes a DatetimeElement associated with a given GUI.
     * @param gui The GUI associated with the DatetimeElement.
     */
    public DatetimeElement(GUI gui) {
        this.gui = gui;
        initialize();
        initializeLabels();
        initializeEntryFields();
    }

    /**
     * Initializes the panel of the DatetimeElement.
     */
    private void initialize() {
        this.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
        this.setLayout(new GridBagLayout());
    }

    /**
     * Initializes the labels of the DatetimeElement.
     */
    private void initializeLabels() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.gridy = 0;

        constraints.gridx = 1;
        JLabel colonLabel = new JLabel(":");
        colonLabel.setForeground(gui.palette.TEXT_COLOR);
        this.add(colonLabel, constraints);

        constraints.gridx = 3;
        JLabel spaceLabel = new JLabel("  ");
        spaceLabel.setForeground(gui.palette.TEXT_COLOR);
        this.add(spaceLabel, constraints);

        constraints.gridx = 5;
        JLabel slashLabel1 = new JLabel("/");
        slashLabel1.setForeground(gui.palette.TEXT_COLOR);
        this.add(slashLabel1, constraints);

        constraints.gridx = 7;
        JLabel slashLabel2 = new JLabel("/");
        slashLabel2.setForeground(gui.palette.TEXT_COLOR);
        this.add(slashLabel2, constraints);
    }

    /**
     * Initializes the text entry fields of the DatetimeElement.
     */
    private void initializeEntryFields() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.gridy = 0;

        constraints.gridx = 0;
        hourField = new JTextField("HH");
        hourField.setToolTipText("A specified hour in 24-hour time.");
        hourField.setBackground(gui.palette.GUI_FOREGROUND_COLOR);
        hourField.setForeground(gui.palette.TEXT_COLOR);
        this.add(hourField, constraints);

        constraints.gridx = 2;
        minuteField = new JTextField("mm");
        minuteField.setToolTipText("A specified minute.");
        minuteField.setBackground(gui.palette.GUI_FOREGROUND_COLOR);
        minuteField.setForeground(gui.palette.TEXT_COLOR);
        this.add(minuteField, constraints);

        constraints.gridx = 4;
        dayField = new JTextField("DD");
        dayField.setToolTipText("A specified day.");
        dayField.setBackground(gui.palette.GUI_FOREGROUND_COLOR);
        dayField.setForeground(gui.palette.TEXT_COLOR);
        this.add(dayField, constraints);

        constraints.gridx = 6;
        monthField = new JTextField("MM");
        monthField.setToolTipText("A specified month.");
        monthField.setBackground(gui.palette.GUI_FOREGROUND_COLOR);
        monthField.setForeground(gui.palette.TEXT_COLOR);
        this.add(monthField, constraints);

        constraints.gridx = 8;
        yearField = new JTextField("YYYY");
        yearField.setToolTipText("A specified year.");
        yearField.setBackground(gui.palette.GUI_FOREGROUND_COLOR);
        yearField.setForeground(gui.palette.TEXT_COLOR);
        this.add(yearField, constraints);
    }


    // Public Methods

    /**
     * Inputs a specific datetime into the DatetimeElement.
     * @param datetime The datetime to be input into the DatetimeElement.
     */
    public void setDatetime(Date datetime) {
        String[] parsable = getParsableDatetime(datetime);
        minuteField.setText(parsable[0]);
        hourField.setText(parsable[1]);
        dayField.setText(parsable[2]);
        monthField.setText(parsable[3]);
        yearField.setText(parsable[4]);
    }

    /**
     * Returns the datetime current input into the DatetimeElement.
     * @return A Date object of the input datetime.
     * @throws ParseException If an invalid datetime is input into the DatetimeElement, a ParseException is thrown.
     */
    public Date getDatetime() throws ParseException {
        String minute = minuteField.getText();
        String hour = hourField.getText();
        String day = dayField.getText();
        String month = monthField.getText();
        String year = yearField.getText();
        String formattedString =  String.format("%s %s %s %s %s", minute, hour, day, month, year);
        return formatter.parse(formattedString);
    }

    /**
     * Splits a given datetime into a parsable list, by unit of time.
     * @param datetime The given datetime to be parsed.
     * @return An array of strings containing the parsable datetime, by unit of time.
     */
    private String[] getParsableDatetime(Date datetime) {
        String formatted = formatter.format(datetime);
        return formatted.split(" ");
    }

}
