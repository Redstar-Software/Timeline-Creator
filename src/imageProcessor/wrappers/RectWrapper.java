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
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;


/**
 * A wrapper used to store a rounded or un-rounded rectangle to be drawn by the ImageProcessor to the timeline image.
 */
public class RectWrapper implements Comparable<RectWrapper> {

    public Integer x, y, width, height;
    /*
    IMPORTANT: The y value of a wrapper does not represent the y-position at
    which it is rendered, but rather its position relative to the timeline bar.
    This is done because the final height of the image, and therefore the
    final y position of each wrapper, is dependant upon the farthest distance
    an event reaches from the timeline bar.
    */

    public boolean isRounded;
    public Color color;
    private final int roundedRectArc;

    /**
     * Constructs a RectWrapper of the given specifications.
     * @param x The horizontal position of the rectangle on the timeline image.
     * @param y The vertical position of the rectangle on the timeline image relative to the "bar" splitting the timeline image in two.
     * @param width The width of the rectangle to be drawn on the timeline image.
     * @param height The height of the rectangle to be drawn on the timeline image.
     * @param arc The arc of each corner of the rectangle if the rectangle is rounded.
     * @param isRounded Whether the rectangle has rounded corners.
     * @param color The color of the rectangle to be drawn on the timeline image.
     */
    public RectWrapper(int x, int y, int width, int height, int arc, boolean isRounded, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isRounded = isRounded;
        this.color = color;
        this.roundedRectArc = arc;
    }

    /**
     * Generates and returns a Java AWT Shape matching the specifications of the RectWrapper.
     * @return A Java AWT Rectangle2D.Double or RoundRectangle2D.Double object matching the specifications of the RectWrapper.
     */
    public Shape generateRect() {
        if (isRounded) { return new RoundRectangle2D.Double(x, y, width, height, roundedRectArc, roundedRectArc); }
        else { return new Rectangle2D.Double(x, y, width, height); }
    }

    /**
     * Compares 2 RectWrappers based on their vertical positions.
     * @param compared The RectWrapper to be compared with the RectWrapper on which this method was called.
     */
    @Override
    public int compareTo(RectWrapper compared) { return this.y.compareTo(compared.y); }
}
























