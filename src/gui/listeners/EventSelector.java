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
import gui.panels.EventPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An ActionListener used to load the details of a specific event to the eventPanel.
 */
public class EventSelector implements ActionListener {

    private final Event event;
    private final EventPanel panel;

    /**
     * Constructs an EventLoader associated with both a given event and the eventPanel.
     * @param event The event to be displayed on the eventPanel.
     * @param panel The eventPanel associated with the EventLoader.
     */
    public EventSelector(Event event, EventPanel panel) {
        this.event = event;
        this.panel = panel;
    }

    /**
     * Populates the eventPanel with the details of a given event.
     */
    @Override
    public void actionPerformed(ActionEvent e) { panel.populate(event); }
}
