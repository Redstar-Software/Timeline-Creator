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

package gui.panels;

import gui.GUI;
import eventHandler.Event;
import gui.listeners.*;
import gui.elements.DatetimeElement;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.ParseException;
import java.util.Date;


/**
 * A panel used to display and modify the details of timeline events.
 */
public class EventPanel extends JPanel {

    private final GUI gui;

    private JTextField nameField;
    private JComboBox<String> fontNameField;
    private JCheckBox isSpanOfTimeField;
    private DatetimeElement startingTimeField;
    private DatetimeElement endingTimeField;
    private JRadioButton aboveBarField;
    private JRadioButton belowBarField;
    private JButton foregroundColorButton;
    private JButton backgroundColorButton;

    private final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12);
    private final String[] FONTS = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();


    // Initialization Methods

    /**
     * Constructs an EventPanel associated with a given GUI.
     * @param gui The GUI associated with the EventPanel.
     */
    public EventPanel(GUI gui) {
        this.gui = gui;
        initialize();
    }

    /**
     * Initializes the panel of the EventPanel.
     */
    private void initialize() {
        this.setLayout(new GridBagLayout());
        Border border = new LineBorder(gui.palette.BORDER_COLOR, 1);
        this.setBorder(border);
        this.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
    }

    /**
     * Initializes and reinitializes the contents of the EventPanel. Displays the details of a specific event if given.
     * @param event The event the details of which are to be displayed. May be null.
     */
    public void populate(Event event) {
        this.removeAll();
        this.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
        initializeEventDetailsPanel();
        initializeAppearanceDetailsPanel();
        initializeButtons();
        if (event != null) { loadEvent(event); }
        this.revalidate();
        this.repaint();
    }

    /**
     * Initializes the entry fields relating to an event's placement on the timeline.
     */
    private void initializeEventDetailsPanel() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.ipady = 20;

        JPanel eventDetailsPanel = new JPanel();
        TitledBorder border = new TitledBorder(new LineBorder(gui.palette.BORDER_COLOR, 1), "Event Details");
        border.setTitleColor(gui.palette.BORDER_COLOR);
        eventDetailsPanel.setBorder(border);
        eventDetailsPanel.setLayout(new GridBagLayout());
        eventDetailsPanel.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
        this.add(eventDetailsPanel, constraints);

        constraints.gridwidth = 1;
        constraints.ipady = 5;

        JLabel nameLabel = new JLabel("Name: ");
        nameLabel.setForeground(gui.palette.TEXT_COLOR);
        eventDetailsPanel.add(nameLabel, constraints);
        constraints.gridx = 1;
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(180, 20));
        nameField.setToolTipText("The name of the event. May not use comas.");
        nameField.setBackground(gui.palette.GUI_FOREGROUND_COLOR);
        nameField.setForeground(gui.palette.TEXT_COLOR);
        eventDetailsPanel.add(nameField, constraints);

        constraints.gridy = 1;
        isSpanOfTimeField = new JCheckBox("Span of Time");
        isSpanOfTimeField.setFocusable(false);
        isSpanOfTimeField.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
        isSpanOfTimeField.setForeground(gui.palette.TEXT_COLOR);
        eventDetailsPanel.add(isSpanOfTimeField, constraints);

        constraints.gridy = 2;
        constraints.gridx = 0;
        JLabel startingDateLabel = new JLabel("Starting Date: ");
        startingDateLabel.setForeground(gui.palette.TEXT_COLOR);
        eventDetailsPanel.add(startingDateLabel, constraints);
        constraints.gridx = 1;
        startingTimeField = new DatetimeElement(gui);
        eventDetailsPanel.add(startingTimeField, constraints);

        constraints.gridy = 3;
        constraints.gridx = 0;
        JLabel endingDateLabel = new JLabel("Ending Date: ");
        endingDateLabel.setForeground(gui.palette.TEXT_COLOR);
        eventDetailsPanel.add(endingDateLabel, constraints);
        constraints.gridx = 1;
        endingTimeField = new DatetimeElement(gui);
        eventDetailsPanel.add(endingTimeField, constraints);
    }

    /**
     * Initializes the entry fields relating to an event's appearance on the timeline.
     */
    private void initializeAppearanceDetailsPanel() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.ipady = 10;

        JPanel appearanceDetailsPanel = new JPanel();
        TitledBorder border = new TitledBorder(new LineBorder(gui.palette.BORDER_COLOR, 1), "Appearance");
        border.setTitleColor(gui.palette.BORDER_COLOR);
        appearanceDetailsPanel.setBorder(border);
        appearanceDetailsPanel.setLayout(new GridBagLayout());
        appearanceDetailsPanel.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
        this.add(appearanceDetailsPanel, constraints);

        constraints.gridwidth = 1;
        constraints.ipady = 5;

        constraints.gridy = 0;
        JLabel fontLabel = new JLabel("Font: ");
        fontLabel.setForeground(gui.palette.TEXT_COLOR);
        appearanceDetailsPanel.add(fontLabel, constraints);
        constraints.gridx = 1;
        fontNameField = new JComboBox<>(FONTS);
        fontNameField.setFont(DEFAULT_FONT);
        fontNameField.setSelectedItem("Times New Roman");
        fontNameField.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
        fontNameField.setForeground(gui.palette.TEXT_COLOR);
        appearanceDetailsPanel.add(fontNameField, constraints);

        ButtonGroup radioButtonGroup = new ButtonGroup();

        constraints.gridy = 2;
        constraints.gridx = 1;
        aboveBarField = new JRadioButton("Above Bar");
        aboveBarField.setFocusable(false);
        aboveBarField.setSelected(true);
        aboveBarField.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
        aboveBarField.setForeground(gui.palette.TEXT_COLOR);
        radioButtonGroup.add(aboveBarField);
        appearanceDetailsPanel.add(aboveBarField, constraints);

        constraints.gridy = 3;
        constraints.gridx = 1;
        belowBarField = new JRadioButton("Below Bar");
        belowBarField.setFocusable(false);
        belowBarField.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
        belowBarField.setForeground(gui.palette.TEXT_COLOR);
        radioButtonGroup.add(belowBarField);
        appearanceDetailsPanel.add(belowBarField, constraints);

        constraints.gridy = 4;
        constraints.gridx = 0;
        JLabel textColorLabel = new JLabel("Text Color: ");
        textColorLabel.setForeground(gui.palette.TEXT_COLOR);
        appearanceDetailsPanel.add(textColorLabel, constraints);
        constraints.gridx = 1;
        foregroundColorButton = new JButton();
        foregroundColorButton.addActionListener(new EventForegroundColorPicker(this));
        foregroundColorButton.setFocusable(false);
        foregroundColorButton.setBackground(gui.palette.TIMELINE_BACKGROUND_COLOR);
        appearanceDetailsPanel.add(foregroundColorButton, constraints);

        constraints.gridy = 5;
        constraints.gridx = 0;
        JLabel backgroundColorLabel = new JLabel("Background Color: ");
        backgroundColorLabel.setForeground(gui.palette.TEXT_COLOR);
        appearanceDetailsPanel.add(backgroundColorLabel, constraints);
        constraints.gridx = 1;
        backgroundColorButton = new JButton();
        backgroundColorButton.addActionListener(new EventBackgroundColorPicker(this));
        backgroundColorButton.setFocusable(false);
        backgroundColorButton.setBackground(gui.palette.TIMELINE_FOREGROUND_COLOR);
        appearanceDetailsPanel.add(backgroundColorButton, constraints);
    }

    /**
     * Initializes the buttons used to interact with the timeline.
     */
    private void initializeButtons() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy = 2;
        constraints.weightx = 1;

        constraints.gridx = 0;
        JButton adderButton = new JButton("Save Event");
        adderButton.addActionListener(new EventAdder(this.gui));
        adderButton.setFocusable(false);
        adderButton.setPreferredSize(new Dimension(100, 40));
        adderButton.setBackground(gui.palette.GUI_FOREGROUND_COLOR);
        adderButton.setForeground(gui.palette.TEXT_COLOR);
        this.add(adderButton, constraints);

        constraints.gridx = 1;
        JButton deleterButton = new JButton("Delete Event");
        deleterButton.addActionListener(new EventDeleter(this.gui));
        deleterButton.setFocusable(false);
        deleterButton.setPreferredSize(new Dimension(100, 40));
        deleterButton.setBackground(gui.palette.GUI_FOREGROUND_COLOR);
        deleterButton.setForeground(gui.palette.TEXT_COLOR);
        this.add(deleterButton, constraints);
    }

    /**
     * Displays the details of a given event on the eventPanel.
     * @param event The event to be displayed.
     */
    private void loadEvent(Event event) {
        nameField.setText(event.name);
        fontNameField.setSelectedItem(event.fontName);
        startingTimeField.setDatetime(event.startingTime);
        foregroundColorButton.setBackground(event.foregroundColor);
        backgroundColorButton.setBackground(event.backgroundColor);
        aboveBarField.setSelected(event.isAboveTimelineBar);
        belowBarField.setSelected(!event.isAboveTimelineBar);
        if (event.isSpanOfTime) {
            endingTimeField.setDatetime(event.endingTime);
            isSpanOfTimeField.setSelected(true);
        }
        else { isSpanOfTimeField.setSelected(false); }
    }


    // Public Methods

    /**
     * Prompts the user to choose an event's foreground color.
     */
    public void pickEventForegroundColor() {
        Color color = JColorChooser.showDialog(null, "Foreground Color", Color.WHITE);
        if (color != null) { foregroundColorButton.setBackground(color); }
    }

    /**
     * Prompts the user to choose an event's background color.
     */
    public void pickEventBackgroundColor() {
        Color color = JColorChooser.showDialog(null, "Background Color", Color.BLACK);
        if (color != null) { backgroundColorButton.setBackground(color); }
    }


    // Information Requests

    public String getEventName() { return nameField.getText().replaceAll(",", ""); }

    public boolean getEventIsAboveBar() { return aboveBarField.isSelected(); }

    public boolean getEventIsSpanOfTime() {return isSpanOfTimeField.isSelected(); }

    /**
     * Returns the event starting time currently input within the eventPanel as a Date object.
     * @return The datetime currently entered into the 'startingTimeField' datetimeElement as a Date object.
     */
    public Date getEventStartingTime() {
        try { return startingTimeField.getDatetime(); }
        catch (ParseException e) { return null; }
    }

    /**
     * Returns the event ending time currently input within the eventPanel as a Date object.
     * @return The datetime currently entered into the 'endingTimeField' datetimeElement as a Date object.
     * @throws NullPointerException If the input event is marked as a span of time but no ending time is given, a NullPointerException is thrown.
     */
    public Date getEventEndingTime() {
        Date output;
        try { output =  endingTimeField.getDatetime(); }
        catch (ParseException e) { output = null; }

        if (output == null && getEventIsSpanOfTime()) {
            throw new NullPointerException("Attempted to create span of time with no ending time.");
        }
        else if (!getEventIsSpanOfTime()) { output = null; }

        return output;
    }

    public String getEventFontName() {
        int index = fontNameField.getSelectedIndex();
        return FONTS[index];
    }

    public Color getEventForegroundColor() { return foregroundColorButton.getBackground(); }

    public Color getEventBackgroundColor() { return backgroundColorButton.getBackground(); }

}
