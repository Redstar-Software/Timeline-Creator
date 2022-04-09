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

import eventHandler.Event;
import gui.GUI;
import gui.listeners.EventSelector;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * A panel used to list all events in the current timeline in chronological order. Allows each event to be selected and altered.
 */
public class TimelineListPanel extends JPanel {

    private final GUI gui;

    private final int BUTTON_WIDTH = 350;
    private final int BUTTON_HEIGHT = 25;
    private final Dimension BUTTON_SIZE = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);


    // Initialization Methods

    /**
     * Constructs a TimelineList Panel associated with a given GUI.
     * @param gui The GUI associated with the TimelineListPanel.
     */
    public TimelineListPanel(GUI gui) {
        this.gui = gui;
        initialize();
    }

    /**
     * Initializes the panel of the TimelineListPanel.
     */
    private void initialize() {
        this.setPreferredSize(BUTTON_SIZE);
        FlowLayout layout = new FlowLayout();
        layout.setVgap(0);
        this.setLayout(layout);
        Border border = new LineBorder(gui.palette.BORDER_COLOR, 1);
        this.setBorder(border);
        this.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
    }

    /**
     * Initializes the contents of the TimelineListPanel.
     */
    public void populate() {
        this.removeAll();
        this.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
        if (gui.getHandler() != null) { updateTimeline(); }
        initializeNewEventButton();
        this.revalidate();
        this.repaint();
    }

    /**
     * Creates an EventSelector button for each event in the timeline in chronological order, with the earliest events at the top of the list.
     */
    private void updateTimeline() {
        EventPanel eventPanel = gui.getEventPanel();
        ArrayList<Event> events = gui.getTimelineEvents();
        this.setPreferredSize(new Dimension(BUTTON_WIDTH, (events.size() + 1) * BUTTON_HEIGHT));
        for (Event event:events) { initializeEventButton(event, eventPanel); }
    }

    /**
     * Creates an EventSelector button with no associated event. Used to reset the eventPanel for a new event to be added.
     */
    private void initializeNewEventButton() {
        EventPanel eventPanel = gui.getEventPanel();
        JButton newEventButton = new JButton("Add New Event");
        newEventButton.addActionListener(new EventSelector(null, eventPanel));
        newEventButton.setBackground(new Color(65, 150, 65));
        newEventButton.setForeground(gui.palette.TEXT_COLOR);
        newEventButton.setFocusable(false);
        newEventButton.setPreferredSize(BUTTON_SIZE);
        this.add(newEventButton);
    }

    /**
     * Creates an EventSelector button associated with a specific event.
     * @param event The event associated with the button.
     * @param eventPanel The eventPanel updated by the EventSelector button.
     */
    private void initializeEventButton(Event event, EventPanel eventPanel) {
        JButton eventButton = new JButton(event.toString());
        eventButton.addActionListener(new EventSelector(event, eventPanel));
        eventButton.setPreferredSize(BUTTON_SIZE);
        eventButton.setFocusable(false);
        eventButton.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
        eventButton.setForeground(gui.palette.TEXT_COLOR);
        this.add(eventButton);
    }

}
