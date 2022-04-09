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

package imageProcessor;

import eventHandler.EventHandler;
import eventHandler.Event;
import gui.GUI;
import imageProcessor.wrappers.RectWrapper;
import imageProcessor.wrappers.TextWrapper;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import static java.lang.Math.abs;


/**
 * An object used to generate an image representing the current timeline.
 */
public class ImageProcessor {

    private final EventHandler handler;
    private final GUI gui;

    private BufferedImage image;
    private Graphics2D graphics;

    private Long startingTimeInMilliseconds;
    private Long endingTimeInMilliseconds;
    private Long millisecondsPerPixel;
    private Long timelineLengthInMilliseconds;
    private Integer imageWidth;
    private Integer timelineBarYPosition;
    private Integer timelineBarHeight;
    private Integer eventHeight;
    private Integer shortEventHeight;
    private Integer eventTextSize;
    private Integer eventTextOffset;
    private Integer eventStemWidth;
    private Integer roundedRectArc;
    private Integer titleYPosition;
    private Font titleFont;

    private final ArrayList<Integer[]> rangesOccupiedAboveTimelineBar = new ArrayList<>();
    private final ArrayList<Integer[]> rangesOccupiedBelowTimelineBar = new ArrayList<>();
    private final ArrayList<RectWrapper> rects = new ArrayList<>();
    private final ArrayList<TextWrapper> text = new ArrayList<>();

    private final Color ALPHA_LAYER = new Color(0x0000000, true);
    private final int DEFAULT_IMAGE_WIDTH = 1265;
    private final int DEFAULT_IMAGE_HEIGHT = 970;
    private final int DEFAULT_EVENT_HEIGHT = 35;
    private final long MILLISECONDS_IN_A_DAY = 86400000L;

    private final int[] TIMELINE_DENSITIES_IN_DAYS = {3650000, 365000, 36500, 18250, 9125, 3650, 1825, 365, 186, 93, 31, 7, 3, 1};
    // 10 millennia, 1 millennium, 1 century, 50 years, 25 years, 1 decade, 5 years, 1 year, 6 months, 3 months, 1 month, 1 week, 3 days, 1 day


    // Initialization Methods

    /**
     * Constructs an ImageProcessor associated with the given EventHandler and GUI.
     * @param handler The EventHandler associated with the ImageProcessor.
     * @param gui The GUI associated with the ImageProcessor.
     */
    public ImageProcessor(EventHandler handler, GUI gui) {
        this.handler = handler;
        this.gui = gui;
    }


    // Public Methods

    /**
     * Generates an image representing the current timeline.
     * @return The visual representation of the current timeline as a BufferedImage.
     */
    public BufferedImage generateImage() {
        setupImage();
        renderImage();
        return image;
    }

    /**
     * Prepares the visual elements of the timeline to be rendered and creates a new BufferedImage object.
     */
    private void setupImage() {
        reinitialize();
        setTimeBoundaries();
        sketchTimelineEvents();
        createImageElement();
    }

    /**
     * Renders the visual elements to the BufferedImage.
     */
    private void renderImage() {
        RenderingHints rendering = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHints(rendering);

        renderBackground();
        renderEvents();
        renderForeground();
    }


    // Setup Methods

    /**
     * Clears all existing visual elements from the ImageProcessor and resets attributes used during creation of a new BufferedImage object.
     */
    private void reinitialize() {
        rangesOccupiedAboveTimelineBar.clear();
        rangesOccupiedBelowTimelineBar.clear();
        rects.clear();
        text.clear();
        startingTimeInMilliseconds = null;
        endingTimeInMilliseconds = null;
        millisecondsPerPixel = null;
        timelineLengthInMilliseconds = null;
        image = null;
    }

    /**
     * Establishes time-based information necessary for the creation of a new BufferedImage object.
     */
    private void setTimeBoundaries() {
        if (doTimeBoundariesExist()) {
            startingTimeInMilliseconds = handler.getEarliestTime().getTime();
            endingTimeInMilliseconds = handler.getLatestTime().getTime();
        } else {
            startingTimeInMilliseconds = 0L;
            endingTimeInMilliseconds = MILLISECONDS_IN_A_DAY;
        }
        timelineLengthInMilliseconds = endingTimeInMilliseconds - startingTimeInMilliseconds;
        calculateMillisecondsPerPixel();
        int calibratedWidth = (int) (timelineLengthInMilliseconds / millisecondsPerPixel);
        imageWidth = Math.max(calibratedWidth, DEFAULT_IMAGE_WIDTH);
    }

    /**
     *  Prepares the visual elements of the timeline to be rendered.
     */
    private void sketchTimelineEvents() {
        calculateEventHeight();
        for (Event event:handler.getAllEvents()) {
            if (event.isSpanOfTime) { drawSpanOfTimeEvent(event); }
            else { drawSingleEvent(event); }
        }
    }

    /**
     * Creates a new BufferedImage object.
     */
    private void createImageElement() {
        sketchTitle();
        int height = calculateImageHeight();
        image = new BufferedImage(imageWidth, height, BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
    }

    /**
     * Determines whether the time boundaries of the timeline are not null.
     * @return Whether the time boundaries of the timeline are not null.
     */
    private boolean doTimeBoundariesExist() {
        return handler.getEarliestTime() != null && handler.getLatestTime() != null;
    }

    /**
     * Calculates how many milliseconds of time will be represented by each pixel of the image, a unit used to specific moments in time into positions on the image.
     */
    private void calculateMillisecondsPerPixel() {
        int densityInDays = findTimelineDensityInDays();
        millisecondsPerPixel = (densityInDays * MILLISECONDS_IN_A_DAY) / DEFAULT_IMAGE_WIDTH;
    }

    /**
     * Prepares the visual elements used to represent a single event.
     * @param event The event to be drawn.
     */
    private void drawSingleEvent(Event event) {
        Font eventFont = new Font(event.fontName, Font.PLAIN, eventTextSize);
        int eventWidth = calculateSingleEventWidth(event, eventFont);
        int x = calculateEventXCoordinate(event, eventWidth);
        int y = calculateEventYCoordinate(x, eventWidth, event.isAboveTimelineBar);
        drawSingleEventStem(event, y);
        drawSingleEventBody(event, x, y, eventWidth, eventFont);
    }

    /**
     * Prepares the visual elements used to represent a span of time.
     * @param event The event to be drawn.
     */
    private void drawSpanOfTimeEvent(Event event) {
        Font eventFont = new Font(event.fontName, Font.PLAIN, eventTextSize);
        int eventWidth = calculateSpanOfTimeWidth(event);
        int textWidth = getTextWidth(event.name, eventFont);
        int x = calculateEventXCoordinate(event, eventWidth);
        int y = calculateEventYCoordinate(x, eventWidth, event.isAboveTimelineBar);
        drawSpanOfTimeBody(event, x, y, eventWidth, textWidth, eventFont);
    }

    /**
     * Calculates the appropriate height for the body of each event, then uses this height as the basis for the size of several related visual elements.
     */
    private void calculateEventHeight() {
        eventHeight = DEFAULT_EVENT_HEIGHT;
        shortEventHeight = eventHeight / 5;
        eventTextOffset = eventHeight / 9;
        eventTextSize = (int) (eventHeight * 0.8);
        roundedRectArc = (int) (eventHeight * 0.60);
        timelineBarHeight = eventStemWidth = eventHeight / 10;
        if (timelineBarHeight % 2 != 0) {timelineBarHeight++;}
    }

    /**
     * Calculates measurements related to the title visual element.
     */
    private void sketchTitle() {
        String titleFontName = gui.getTimelineTileFontName();
        int titleFontSize = imageWidth / 20;
        titleFont = new Font(titleFontName, Font.BOLD, titleFontSize);
        titleYPosition = (int) (titleFontSize * 1.5);
    }

    /**
     * Calculates the appropriate height of the image based on the distance of events from the "bar" of the timeline.
     * @return The height of the image.
     */
    private int calculateImageHeight() {
        int titleSpace = titleFont.getSize() + titleYPosition;
        int topHalfHeight = (DEFAULT_IMAGE_HEIGHT  / 2) - titleSpace;
        int bottomHalfHeight = titleYPosition;
        for (Integer[] range:rangesOccupiedAboveTimelineBar) {
            int distanceFromTimelineBar = range[2];
            topHalfHeight = Math.max(distanceFromTimelineBar, topHalfHeight);
        }
        for (Integer[] range:rangesOccupiedBelowTimelineBar) {
            int distanceFromTimelineBar = range[2] + titleYPosition;
            bottomHalfHeight = Math.max(distanceFromTimelineBar, bottomHalfHeight);
        }
        timelineBarYPosition = topHalfHeight + titleSpace;
        int calibratedHeight = topHalfHeight + bottomHalfHeight + titleSpace;
        return Math.max(calibratedHeight, DEFAULT_IMAGE_HEIGHT);
    }

    /**
     * Calculates the appropriate size of the image depending on the timeline's length, or "density" of the timeline.
     * @return The "density" of the timeline.
     */
    private int findTimelineDensityInDays() {
        long timelineLengthInDays = timelineLengthInMilliseconds / MILLISECONDS_IN_A_DAY;
        for (int timelineDensity:TIMELINE_DENSITIES_IN_DAYS) {
            if (timelineDensity <= timelineLengthInDays) { return timelineDensity; }
        }
        return TIMELINE_DENSITIES_IN_DAYS[TIMELINE_DENSITIES_IN_DAYS.length - 1];
    }

    /**
     * Calculates the appropriate width of the body of a single event to be drawn.
     * @param event The event to be drawn.
     * @param font The font with which the name of the event is written.
     * @return The width of the event body.
     */
    private int calculateSingleEventWidth(Event event, Font font) {
        int textWidth = getTextWidth(event.name, font);
        return textWidth + (eventTextOffset * 2);
    }

    /**
     * Calculates the X coordinate of an event to be drawn based on the time at which it occurs.
     * @param event The event to be drawn.
     * @param width The width of the body of the event to be drawn.
     * @return The X coordinate of the drawn event.
     */
    private int calculateEventXCoordinate(Event event, int width) {
        long eventTimeInMilliseconds = event.startingTime.getTime();
        int x = (int) ((eventTimeInMilliseconds - startingTimeInMilliseconds) / millisecondsPerPixel);
        if (event.isSpanOfTime) { return x; }

        if (x - (width / 2) < 0) {return 0;}
        else if (x + (width / 2) > imageWidth) { return imageWidth - width; }
        else { return x - (width / 2); }
    }

    /**
     * Calculates the Y coordinate of an event to be drawn depending on whether the event collides with another event.
     * @param x The X coordinate of the drawn event.
     * @param width The width of the drawn event's body.
     * @return The Y coordinate of the drawn event.
     */
    private int calculateEventYCoordinate(int x, int width, boolean isAboveTimelineBar) {
        int y;
        if (isAboveTimelineBar) { y = -(eventHeight + (2 * timelineBarHeight)); }
        else { y = (2 * timelineBarHeight); }
        y += calculateHeightNeededToAvoidCollision(x, width, isAboveTimelineBar);
        return y;
    }

    /**
     * Draws the "stem" of a single event.
     * @param event The single event to be drawn.
     * @param y The Y coordinate of the drawn event.
     */
    private void drawSingleEventStem(Event event, int y) {
        int stemHeight = abs(y);
        int stemY;
        if (event.isAboveTimelineBar) { stemY = -stemHeight; }
        else {
            stemHeight += eventHeight;
            stemY = 0;
        }
        int stemX = calculateEventXCoordinate(event, eventStemWidth);
        drawRect(stemX, stemY, eventStemWidth, stemHeight, event.backgroundColor);
    }

    /**
     * Draws the "body" of a single event.
     * @param event The event to be drawn.
     * @param x The X coordinate of the drawn event.
     * @param y The Y coordinate of the drawn event.
     * @param width The width of the drawn event's body.
     * @param font The font with which the name of the event is written.
     */
    private void drawSingleEventBody(Event event, int x, int y, int width, Font font) {
        drawRoundedRect(x, y, width, eventHeight, event.backgroundColor);
        drawText(event.name, font, x + eventTextOffset, y + eventTextSize, event.foregroundColor);
        occupyArea(x, y, width, event.isAboveTimelineBar);
    }

    /**
     * Calculates the appropriate width of the body of a span of time to be drawn.
     * @param event The event to be drawn.
     * @return The width of the event body.
     */
    private int calculateSpanOfTimeWidth(Event event) {
        long startingTimeInMilliseconds = event.startingTime.getTime();
        long endingTimeInMilliseconds = event.endingTime.getTime();
        return (int) ((endingTimeInMilliseconds - startingTimeInMilliseconds) / millisecondsPerPixel);
    }

    /**
     * Calculates the width of a string of text written with a given font.
     * @param text The string og text to be written.
     * @param font The font used to write the text.
     * @return The width of the text written in the given font.
     */
    private int getTextWidth(String text, Font font) {
        graphics.setFont(font);
        FontMetrics metrics = graphics.getFontMetrics();
        return metrics.stringWidth(text);
    }

    /**
     * Draws the "body" of a single event.
     * @param event The event to be drawn.
     * @param x The X coordinate of the drawn event.
     * @param y The Y coordinate of the drawn event.
     * @param eventWidth The width of the body of the event to be drawn.
     * @param textWidth The width of the event's name.
     * @param eventFont The font with which the event's name is written.
     */
    private void drawSpanOfTimeBody(Event event, int x, int y, int eventWidth, int textWidth, Font eventFont) {
        int occupiedWidth, height;
        Color textColor;
        if (eventWidth > textWidth) {
            occupiedWidth = eventWidth;
            height = eventHeight;
            textColor = event.foregroundColor;
        } else {
            occupiedWidth = textWidth;
            height = shortEventHeight;
            textColor = event.backgroundColor;
        }
        drawRect(x, y, eventWidth, height, event.backgroundColor);
        drawText(event.name, eventFont, x + eventTextOffset, y + eventTextSize, textColor);
        occupyArea(x, y, occupiedWidth, event.isAboveTimelineBar);
    }

    /**
     * Adds a rectangle of the given specifications to the visual elements to be drawn.
     * @param x The X coordinate at which the rectangle will be drawn.
     * @param y The Y coordinate at which the rectangle will be drawn.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     * @param color The color of the rectangle.
     */
    private void drawRect(int x, int y, int width, int height, Color color) {
        rects.add(new RectWrapper(x, y, width, height, roundedRectArc, false, color));
    }

    /**
     * Adds a rectangle of the given specifications to the visual elements to be drawn.
     * @param x The X coordinate at which the rectangle will be drawn.
     * @param y The Y coordinate at which the rectangle will be drawn.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     * @param color The color of the rectangle.
     */
    private void drawRoundedRect(int x, int y, int width, int height, Color color) {
        rects.add(new RectWrapper(x, y, width, height, roundedRectArc, true, color));
    }

    /**
     * Adds a string of text of the given specifications ot the visual elements to be drawn.
     * @param text The text to be written.
     * @param font The font with which the text will be written.
     * @param x The X coordinate at which the text will be written.
     * @param y The Y coordinate at which the text will be written.
     * @param color The color of the text.
     */
    private void drawText(String text, Font font, int x, int y, Color color) {
        this.text.add(new TextWrapper(text, font, x, y, color));
    }

    /**
     * Lists a given area of the image as occupied by an event.
     * @param x The X coordinate at which the event is drawn.
     * @param y The Y coordinate at which the event is drawn.
     * @param width The width of the event's body.
     * @param isAboveTimelineBar Whether the event is above the "bar" running through the center of the image.
     */
    private void occupyArea(int x, int y, int width, boolean isAboveTimelineBar) {
        int distanceFromTimelineBar = abs(y);
        ArrayList<Integer[]> areaToOccupy;
        if (isAboveTimelineBar) { areaToOccupy = rangesOccupiedAboveTimelineBar; }
        else {
            areaToOccupy = rangesOccupiedBelowTimelineBar;
            distanceFromTimelineBar += eventHeight;
        }
        Integer[] range = {x, x + width + 1, distanceFromTimelineBar};
        areaToOccupy.add(range);
    }

    /**
     * Calculates the minimum distance from the timeline "bar" at which an event won't collide with any other event.
     * @param x The X coordinate at which the event is drawn.
     * @param width The width of the event's body.
     * @param isAboveTimelineBar Whether the event is above the "bar" running through the center of the image.
     * @return The minimum distance from the timeline "bar" at which an event of the given measurements won't collide with any other event.
     */
    private int calculateHeightNeededToAvoidCollision(int x, int width, boolean isAboveTimelineBar) {
        int modifier = 1;
        if (isAboveTimelineBar) { modifier = -1; }

        TreeSet<Integer> occupiedHeights = getAllOccupiedHeightsAtX(x, width, isAboveTimelineBar);
        occupiedHeights = getSortedSet(occupiedHeights);
        return findLowestUnoccupiedHeight(occupiedHeights) * modifier;
    }

    /**
     * Returns a set containing all the occupied heights within a given range of X.
     * @param x The X coordinate at which the event is drawn.
     * @param width The width of the event's body.
     * @param isAboveTimelineBar Whether the event is above the "bar" running through the center of the image.
     * @return A set containing all the distances from the timeline "bar" within a certain range which are occupied by an event.
     */
    private TreeSet<Integer> getAllOccupiedHeightsAtX(int x, int width, boolean isAboveTimelineBar) {
        ArrayList<Integer[]> occupiedRanges;
        int midpoint = x + (width / 2);
        if (isAboveTimelineBar) { occupiedRanges = rangesOccupiedAboveTimelineBar; }
        else { occupiedRanges = rangesOccupiedBelowTimelineBar; }

        TreeSet<Integer> occupiedHeights = new TreeSet<>();
        for (Integer[] range:occupiedRanges) {
            int startOfRange = range[0];
            int endOfRange = range[1];
            int distanceFromTimelineBar = range[2];
            if (isNumInRange(x, startOfRange, endOfRange) || isNumInRange(midpoint, startOfRange, endOfRange)) {
                occupiedHeights.add(distanceFromTimelineBar);
            }
        }
        return occupiedHeights;
    }

    /**
     * Returns a sorted set.
     * @param set The set to be sorted.
     * @return A new TreeSet containing the sorted contents of the given set.
     */
    private TreeSet<Integer> getSortedSet(TreeSet<Integer> set) {
        ArrayList<Integer> list = new ArrayList<>(set);
        Collections.sort(list);
        return new TreeSet<>(list);
    }

    /**
     * Finds the minimum height excluded from a set of occupied heights.
     * @param occupiedHeights A given set of occupied heights.
     * @return The minimum distance from the timeline "bar" unoccupied by any event.
     */
    private int findLowestUnoccupiedHeight(TreeSet<Integer> occupiedHeights) {
        try {
            final int DIFFERENCE = eventHeight + (2 * timelineBarHeight);
            int height = 0;
            while (occupiedHeights.contains(height + DIFFERENCE)) { height += DIFFERENCE; }
            return height;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Determines whether a given number is within a specific range, start included, end not included.
     */
    private boolean isNumInRange(int num, int start, int end) { return num >= start && num < end; }


    // Render Methods

    /**
     * Renders the background of the image.
     */
    private void renderBackground() {
        Color backgroundColor = gui.getTimelineBackgroundColor();
        if (backgroundColor != null) { graphics.setColor(backgroundColor); }
        else { graphics.setColor(ALPHA_LAYER); }
        graphics.fill(new Rectangle2D.Double(0, 0, image.getWidth(), image.getHeight()));
    }

    /**
     * Renders all the visual elements used to represent events to the image.
     */
    private void renderEvents() {
        renderRects();
        renderText();
    }

    /**
     * Renders the foreground of the image, the timeline title and timeline "bar" splitting the image in two.
     */
    private void renderForeground() {
        Color foregroundColor = gui.getTimelineForegroundColor();
        graphics.setColor(foregroundColor);

        graphics.fill(new Rectangle2D.Double(0, timelineBarYPosition, image.getWidth(), timelineBarHeight));
        String title = gui.getTimelineTitle();
        int titleX = (image.getWidth() - getTextWidth(title, titleFont)) / 2;
        graphics.setFont(titleFont);
        graphics.drawString(title, titleX, titleYPosition);
    }

    /**
     * Renders every drawn rectangle to the image.
     */
    private void renderRects() {
        Collections.sort(rects);
        for (RectWrapper wrapper:rects) {
            graphics.setColor(wrapper.color);
            wrapper.y += timelineBarYPosition;
            Shape rect = wrapper.generateRect();
            graphics.fill(rect);
        }
    }

    /**
     * Renders every written string of text to the image.
     */
    private void renderText() {
        for (TextWrapper wrapper:text) {
            graphics.setColor(wrapper.color);
            graphics.setFont(wrapper.font);
            int y = wrapper.y + timelineBarYPosition;
            graphics.drawString(wrapper.text, wrapper.x, y);
        }
    }
}
