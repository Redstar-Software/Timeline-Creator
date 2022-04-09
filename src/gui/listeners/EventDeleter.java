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

import eventHandler.EventHandler;
import gui.GUI;
import gui.panels.EventPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An ActionListener used to delete an event from the timeline.
 */
public class EventDeleter implements ActionListener {

    private final GUI gui;

    private final EventPanel inputPanel;
    private final EventHandler handler;

    /**
     * Constructs an EventDeleter ActionListener associated with a given GUI.
     * @param gui The GUI associated with the EventDeleter.
     */
    public EventDeleter(GUI gui) {
        this.gui = gui;
        this.inputPanel = gui.getEventPanel();
        this.handler = gui.getHandler();
    }

    /**
     * Deletes an event of the currently input name from the timeline if present.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String name = inputPanel.getEventName();
        handler.remove(name);
        gui.updateTimelinePanels();
        inputPanel.populate(null);
    }
}
