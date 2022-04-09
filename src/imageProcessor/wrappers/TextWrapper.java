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

package imageProcessor.wrappers;

import java.awt.*;


/**
 * A wrapper used to store a string of text to be drawn by the ImageProcessor to the timeline image.
 */
public class TextWrapper {

    public Integer x, y;
    /*
    IMPORTANT: The y value of a wrapper does not represent the y-position at
    which it is rendered, but rather its position relative to the timeline bar.
    This is done because the final height of the image, and therefore the
    final y position of each wrapper, is dependant upon the farthest distance
    an event reaches from the timeline bar.
    */

    public String text;
    public Font font;
    public Color color;

    /**
     * Constructs a TextWrapper of the given specifications.
     * @param text The text to be drawn on the timeline image.
     * @param font The font with which the text shall be drawn in the timeline image.
     * @param x The horizontal position of the text on the timeline image.
     * @param y The vertical position of the text on the timeline image relative to the "bar" splitting the timeline image in two.
     * @param color The color of the text to be drawn on the timeline image.
     */
    public TextWrapper(String text, Font font, int x, int y, Color color) {
        this.text = text;
        this.font = font;
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
