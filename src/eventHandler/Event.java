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


package eventHandler;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An event to be placed on the timeline.
 */
public class Event implements Comparable<Event> {

    private final SimpleDateFormat formatter;

    public final String name;
    public final boolean isSpanOfTime;
    public final Date startingTime;
    public final Date endingTime;
    public final String datetimeFormat;
    public final String fontName;
    public final Color foregroundColor;
    public final Color backgroundColor;
    public final boolean isAboveTimelineBar;


    /**
     * Constructs an event of the given specifications. Produces a single event if the "endingTime" param is left as null, and produces a span of time otherwise.
     * @param name The name of the event.
     * @param startingTime The time at which the event begins or occurs.
     * @param endingTime The time at which the event ends.
     * @param datetimeFormat The format with which the time(s) of the event will be displayed.
     * @param fontName The font with which the event's name is written on the timeline image.
     * @param foregroundColor The color of the event's name on the timeline image.
     * @param backgroundColor The color of the event on the timeline image.
     * @param isAboveTimelineBar Whether the event is to be drawn above or below the "bar" splitting the timeline image in two.
     */
    public Event(String name, Date startingTime, Date endingTime, String datetimeFormat,String fontName,
                 Color foregroundColor, Color backgroundColor, boolean isAboveTimelineBar) {
        this.name = name;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.fontName = fontName;
        this.datetimeFormat = datetimeFormat;
        this.formatter = new SimpleDateFormat(datetimeFormat);
        this.isSpanOfTime = (endingTime != null);
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.isAboveTimelineBar = isAboveTimelineBar;
    }

    @Override
    public int compareTo(Event comparedEvent) { return this.startingTime.compareTo(comparedEvent.startingTime); }

    @Override
    public String toString() {
        String output = String.format("%s: %s", name, formatter.format(startingTime));
        if (isSpanOfTime) { output += " - " + formatter.format(endingTime); }
        return output;
    }
}
