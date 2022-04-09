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
import gui.elements.PaletteElement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * An ActionListener used to change the palette of the GUI.
 */
public class PaletteSetter implements ActionListener {

    public final GUI gui;

    public final PaletteElement palette;
    public final String paletteName;

    /**
     * Constructs a PaletteSetter associated with a given GUI and a specified palette.
     * @param gui The GUI associated with the PaletteSetter.
     * @param palette The palette the PaletteSetter is to change the GUI into.
     * @param paletteName The name of the given palette.
     */
    public PaletteSetter(GUI gui, PaletteElement palette, String paletteName) {
        this.gui = gui;
        this.palette = palette;
        this.paletteName = paletteName;
    }

    /**
     * Sets the palette of the GUI as the palette associated with the PaletteSetter, then updates the GUI.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        gui.palette = palette;
        gui.paletteName = paletteName;
        gui.populate();
        gui.initializeMenuBar();
    }
}
