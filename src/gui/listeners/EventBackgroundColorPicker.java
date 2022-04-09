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

import gui.panels.EventPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An ActionListener used to prompt the user to choose an event's background color.
 */
public class EventBackgroundColorPicker implements ActionListener {

    private final EventPanel eventPanel;

    public EventBackgroundColorPicker(EventPanel eventPanel) { this.eventPanel = eventPanel; }

    @Override
    public void actionPerformed(ActionEvent e) { eventPanel.pickEventBackgroundColor(); }
}
