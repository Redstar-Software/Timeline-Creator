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

package gui.listeners;

import eventHandler.Event;
import eventHandler.EventHandler;
import gui.GUI;
import gui.panels.EventPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * An ActionListener used to save an event to the timeline.
 */
public class EventAdder implements ActionListener {

    private final GUI gui;

    private final EventPanel inputPanel;
    private final EventHandler handler;

    /**
     * Constructs an EventAdder ActionListener associated with a given GUI.
     * @param gui The GUI associated with the EventAdder.
     */
    public EventAdder(GUI gui) {
        this.gui = gui;
        this.inputPanel = gui.getEventPanel();
        this.handler = gui.getHandler();
    }

    /**
     * Collects the event information input into the EventPanel and adds such an event to the timeline.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        String eventName = inputPanel.getEventName();
        String fontName = inputPanel.getEventFontName();
        boolean isAboveBar = inputPanel.getEventIsAboveBar();
        Color foregroundColor = inputPanel.getEventForegroundColor();
        Color backgroundColor = inputPanel.getEventBackgroundColor();
        Event preExistingEvent = handler.get(eventName);
        // Given that the new event may be replacing an existing event, the original is kept to revert side effects if necessary.

        try {
            Date startingTime = inputPanel.getEventStartingTime();
            Date endingTime = null;
            endingTime = inputPanel.getEventEndingTime();
            handler.add(eventName, startingTime, endingTime, fontName, foregroundColor, backgroundColor, isAboveBar);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            gui.showErrorMessage("Invalid event details. Cannot add event to timeline.");
            if (preExistingEvent != null) { handler.addEventToTimeline(preExistingEvent); }
            else {handler.remove(eventName);}
        }
        gui.updateTimelinePanels();
    }

}
