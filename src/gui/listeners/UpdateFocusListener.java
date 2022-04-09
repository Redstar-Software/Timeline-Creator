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

import gui.GUI;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * A FocusListener used to update the GUI after the user makes a change and mark the unsaved changes.
 */
public class UpdateFocusListener implements FocusListener {

    private final GUI gui;

    /**
     * Constructs an UpdateFocusListener associated with a given GUI.
     * @param gui The GUI associated with the UpdateFocusListener.
     */
    public UpdateFocusListener(GUI gui) { this.gui = gui; }

    @Override
    public void focusGained(FocusEvent e) { updateGUI(); }

    @Override
    public void focusLost(FocusEvent e) { updateGUI(); }

    /**
     * Updates the GUI and marks that the user has made a change to the timeline.
     */
    private void updateGUI() {
        gui.updateTimelinePanels();
        gui.markUnsavedChanges();
    }
}
