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

import java.awt.*;

/**
 * A palette of colors associated with the specific elements of the GUI. Used to easily create 'themes' for the GUI.
 */
public class PaletteElement {

    public final Color GUI_BACKGROUND_COLOR;
    public final Color GUI_FOREGROUND_COLOR;
    public final Color TIMELINE_BACKGROUND_COLOR;
    public final Color TIMELINE_FOREGROUND_COLOR;
    public final Color MENU_BAR_COLOR;
    public final Color BORDER_COLOR;
    public final Color TEXT_COLOR;

    /**
     * Constructs a new theme palette.
     * @param gui_bg The background color of each GUI panel.
     * @param gui_fg The color of certain components on the GUI.
     * @param timeline_bg The default background color of the timeline image.
     * @param timeline_fg The default foreground color of the timeline image.
     * @param menu_bar The color of the GUI's menu bar.
     * @param border The color of the border of each panel of the GUI.
     * @param text The color of text placed upon the GUI.
     */
    public PaletteElement(Color gui_bg, Color gui_fg, Color timeline_bg, Color timeline_fg,
                          Color menu_bar, Color border, Color text) {

        GUI_BACKGROUND_COLOR = gui_bg;
        GUI_FOREGROUND_COLOR = gui_fg;
        TIMELINE_BACKGROUND_COLOR = timeline_bg;
        TIMELINE_FOREGROUND_COLOR = timeline_fg;
        MENU_BAR_COLOR = menu_bar;
        BORDER_COLOR = border;
        TEXT_COLOR = text;
    }
}
