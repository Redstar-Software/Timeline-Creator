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

import gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * An object which stores and manipulates all events on the timeline, and by extension the timeline itself. Directly interacts with the GUI and ImageProcessor.
 */
public class EventHandler {

    private GUI gui;

    private final SimpleDateFormat formatter;
    private final ArrayList<Event> events;
    private final HashSet<String> eventNames;

    private Date earliestTime;
    private Date latestTime;
    private String currentFile;
    public boolean isTimelineSaved = true;
    private final String datetimeFormat;

    private final String TIMELINE_FILE_EXTENSION = "tmln";


    // Initialization Methods

    /**
     * Constructs an EventHandler provided with the default datetime format.
     */
    public EventHandler(){ this("HH:mm dd/MM/yyyy"); }

    /**
     * Constructs an EventHandler provided with the given datetime format.
     * @param datetimeFormat The datetime format with which the times of all events will be presented.
     */
    public EventHandler(String datetimeFormat){
        this.events = new ArrayList<>();
        this.eventNames = new HashSet<>();
        this.datetimeFormat = datetimeFormat;
        formatter = new SimpleDateFormat(datetimeFormat);
    }


    // Public Methods

    /**
     * Sets the GUI utilized by the handler to the given GUI.
     * @param gui The GUI to be utilized by the handler.
     */
    public void setGUI(GUI gui) { this.gui = gui; }

    /**
     * Creates an event of the given specifications and adds it to the timeline.
     * @param name The name of the event.
     * @param startingTime The time at which the event begins or occurs.
     * @param endingTime The time at which the event ends.
     * @param fontName The font with which the event's name is written on the timeline image.
     * @param foregroundColor The color of the event's name on the timeline image.
     * @param backgroundColor The color of the event on the timeline image.
     * @param isAboveTimelineBar Whether the event is to be drawn above or below the "bar" splitting the timeline image in two.
     */
    public void add(String name, Date startingTime, Date endingTime, String fontName,
                    Color foregroundColor, Color backgroundColor, boolean isAboveTimelineBar) {

        Event event = new Event(name, startingTime, endingTime, datetimeFormat, fontName,
                foregroundColor, backgroundColor, isAboveTimelineBar);
        addEventToTimeline(event);
    }

    /**
     * Removes the event bearing the specified name from the timeline if present.
     * @param eventName The name of the event to be removed.
     */
    public void remove(String eventName) {
        if (eventNames.contains(eventName)) {
            Event event = get(eventName);
            events.remove(event);
            eventNames.remove(eventName);
            updateTimeBoundaries();
            isTimelineSaved = false;
        }
    }

    /**
     * Returns the timeline event bearing the specified name if present.
     * @param eventName The name of the requested event.
     * @return The event bearing the requested name. Null if no such event exist.
     */
    public Event get(String eventName) {
        if (eventNames.contains(eventName)) {
            int index = getIndexOfEvent(eventName);
            return events.get(index);
        }
        return null;
    }

    /**
     * Completely resets the handler, as though it were reinitialized.
     */
    public void reset() {
        boolean performOperation = gui.canOperationProceedDespiteUnsavedChanges();
        if (performOperation) {
            clear();
            gui.resetTimelineDetails();
            gui.updateTimelinePanels();
            currentFile = null;
            isTimelineSaved = true;
        }
    }

    /**
     * Deletes all events managed by the handler, therefore wiping the associated timeline.
     */
    public void clear() {
        events.clear();
        eventNames.clear();
        updateTimeBoundaries();
    }

    /**
     * Adds an event to the timeline. If an event of the same name already exists, replaces the existing event with the given one.
     * @param event The event to be added to the timeline.
     */
    public void addEventToTimeline(Event event) {
        String name = event.name;

        if (eventNames.contains(name)) {
            int index = getIndexOfEvent(name);
            events.set(index, event);
        } else {
            events.add(event);
            eventNames.add(name);
        }
        Collections.sort(events);
        updateTimeBoundaries();
        isTimelineSaved = false;
    }

    /**
     * Returns the index of an event bearing a specified name on the timeline.
     * @param eventName The name of the requested event.
     * @return The position of the specified event within the 'events' ArrayList.
     */
    private int getIndexOfEvent(String eventName) {
        for (int index = 0; index < events.size(); index++) {
            if (events.get(index).name.equals(eventName)) { return index; }
        }
        throw new NullPointerException();
    }

    /**
     * Updates the 'earliestTime' and 'latestTime' attributes of the handler to reflect the current timeline.
     */
    private void updateTimeBoundaries() {
        Collections.sort(events);
        try {
            Event earliestEvent = events.get(0);
            earliestTime = earliestEvent.startingTime;

            latestTime = earliestTime;
            for (Event event:events) {
                if (event.isSpanOfTime && event.endingTime.compareTo(latestTime) > 0) { latestTime = event.endingTime; }
                else if (event.startingTime.compareTo(latestTime) > 0) { latestTime = event.startingTime; }
            }
        } catch (IndexOutOfBoundsException e) {
            earliestTime = null;
            latestTime = null;
        }
    }


    // Save timeline to file

    /**
     * Saves the current timeline to a TIMELINE file.
     * @param promptFileName Whether the name of the destination file is to be prompted from the user regardless of external factors.
     */
    public void saveToFile(boolean promptFileName) {
        if (currentFile == null || promptFileName) { pickDestinationFile(); }
        if (currentFile != null) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile));
                writeFileHeader(writer);
                writeFileEvents(writer);
                writer.close();
                isTimelineSaved = true;
            }
            catch (IOException e) { gui.showErrorMessage("Invalid destination file."); }
        }
    }

    /**
     * Prompts the user for a destination file to save the current timeline into.
     */
    private void pickDestinationFile() {
        JFileChooser fileChooser = new JFileChooser();
        int response = fileChooser.showSaveDialog(null);
        if (response == JFileChooser.APPROVE_OPTION) { formatFilePath(fileChooser.getSelectedFile().getAbsolutePath()); }
        else { currentFile = null; }
    }

    /**
     * Writes the timeline header to the destination file.
     * @param writer The writer used to write to the destination file.
     * @throws IOException In the event of an error in the writing process, an IOException is thrown.
     */
    private void writeFileHeader(BufferedWriter writer) throws IOException {
        String timelineTitle = gui.getTimelineTitle();
        String titleFont = gui.getTimelineTileFontName();
        Color foregroundColor = gui.getTimelineForegroundColor();
        Color backgroundColor = gui.getTimelineBackgroundColor();
        int fgR = foregroundColor.getRed();
        int fgG = foregroundColor.getGreen();
        int fgB = foregroundColor.getBlue();
        int bgR = backgroundColor.getRed();
        int bgG = backgroundColor.getGreen();
        int bgB = backgroundColor.getBlue();
        writer.write(String.format("%s\n%s\n", timelineTitle, titleFont));
        writer.write(String.format("%d,%d,%d,%d,%d,%d\n", fgR, fgG, fgB, bgR, bgG, bgB));
    }

    /**
     * Writes the events of the timeline to the destination file.
     * @param writer The writer used to write to the destination file.
     * @throws IOException In the event of an error in the writing process, an IOException is thrown.
     */
    private void writeFileEvents(BufferedWriter writer) throws IOException {
        for (Event event:events) { writeEventToFile(event, writer); }
    }

    /**
     * Formats the name of a given destination file into the TIMELINE file format.
     * @param filePath The path to the destination file.
     */
    private void formatFilePath(String filePath) {
        String extension = getFileExtension(filePath);
        if (!extension.equals(TIMELINE_FILE_EXTENSION)) { filePath += "." + TIMELINE_FILE_EXTENSION; }
        currentFile = filePath;
    }

    /**
     * Writes the details of a given event to a single line in the destination file.
     * @param event The event to be written.
     * @param writer The writer used to write to the destination file.
     * @throws IOException In the event of an error in the writing process, an IOException is thrown.
     */
    private void writeEventToFile(Event event, BufferedWriter writer) throws IOException {

        String startingTime = formatter.format(event.startingTime);
        int foregroundRed = event.foregroundColor.getRed();
        int foregroundGreen = event.foregroundColor.getGreen();
        int foregroundBlue = event.foregroundColor.getBlue();
        int backgroundRed = event.backgroundColor.getRed();
        int backgroundGreen = event.backgroundColor.getGreen();
        int backgroundBlue = event.backgroundColor.getBlue();
        int isAboveTimelineBar = event.isAboveTimelineBar ? 1 : 0;

        String eventLine;
        if (event.isSpanOfTime) {
            String endingTime = formatter.format(event.endingTime);
            eventLine = String.format("%s,%d,%d,%s,%d,%d,%d,%d,%d,%d,%s,%s\n",
                    event.name,1,isAboveTimelineBar,event.fontName,
                    foregroundRed,foregroundGreen,foregroundBlue,
                    backgroundRed,backgroundGreen,backgroundBlue,
                    startingTime,endingTime);
        } else {
            eventLine = String.format("%s,%d,%d,%s,%d,%d,%d,%d,%d,%d,%s\n",
                    event.name,0,isAboveTimelineBar,event.fontName,
                    foregroundRed,foregroundGreen,foregroundBlue,
                    backgroundRed,backgroundGreen,backgroundBlue,
                    startingTime);
        }
        writer.write(eventLine);
    }

    /**
     * Returns file extension of a given file.
     * @param filePath The path to the given file.
     * @return The extension of the given file.
     */
    private String getFileExtension(String filePath) {
        String[] parsable = filePath.split("\\.");
        String extension = parsable[parsable.length - 1];
        extension = extension.toLowerCase();
        return extension;
    }


    // Load timeline from file

    /**
     * Loads the contents of a TIMELINE file selected through the GUI.
     */
    public void loadFileFromGUI() {
        boolean performOperation = gui.canOperationProceedDespiteUnsavedChanges();
        if (performOperation) {
            try {
                pickSourceFile();
                if (currentFile != null) { loadFromFile(); }
            }
            catch (IOException e) {
                e.printStackTrace();
                gui.showErrorMessage("Invalid source file.");
                isTimelineSaved = true;
                reset();
            }
        }
    }

    /**
     * Loads the contents of a TIMELINE file passed as a command line argument.
     * @param filePath The path to a given source file.
     */
    public void loadFileOnStartup(String filePath) {
        try {
            validateTimelineFileExtension(filePath);
            currentFile = filePath;
            loadFromFile();
        }
        catch (IOException e) { showInvalidSourceFileError(e); }
    }

    /**
     * Loads the contents of the current source file.
     */
    private void loadFromFile() {
        try {
            clear();
            ArrayList<String> lines = readCurrentFileIntoList();
            readFileHeader(lines);
            readFileEvents(lines);
            gui.updateTimelinePanels();
            isTimelineSaved = true;
        }
        catch (Exception e) { showInvalidSourceFileError(e); }
    }

    /**
     * Prompts the user for a source file to load the contents of.
     * @throws IOException In the event of an error while reading the source file, an IOException is thrown.
     */
    private void pickSourceFile() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        int response = fileChooser.showOpenDialog(null);
        if (response == JFileChooser.APPROVE_OPTION) {
            String sourceFile = fileChooser.getSelectedFile().getAbsolutePath();
            validateTimelineFileExtension(sourceFile);
            currentFile = sourceFile;
        }
    }

    /**
     * Creates and returns an ArrayList containing the full contents of the current source file, broken up by line.
     * @return An ArrayList containing each line of the current source file as a contiguous string.
     * @throws IOException In the event of an error while reading the source file, an IOException is thrown.
     */
    private ArrayList<String> readCurrentFileIntoList() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        ArrayList<String> lines = new ArrayList<>();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) { lines.add(line); }
        reader.close();
        return lines;
    }

    /**
     * Reads the header of the current source file and applies the appropriate changes to the GUI.
     * @param lines The contents of the current source file, broken up by line.
     */
    private void readFileHeader(ArrayList<String> lines) throws ParseException {
        /* Header format:

        Timeline title
        Title font
        fgR, fgG, fgB, bgR, bgG, bgB
         */
        String timelineTitle = lines.get(0);
        gui.setTimelineTitle(timelineTitle);

        String titleFont = lines.get(1);
        gui.setTimelineTileFont(titleFont);

        String colorLine = lines.get(2);
        String[] parsableColors = colorLine.split(",");
        Color foregroundColor = toColor(parsableColors[0], parsableColors[1], parsableColors[2]);
        Color backgroundColor = toColor(parsableColors[3], parsableColors[4], parsableColors[5]);
        gui.setTimelineForegroundColor(foregroundColor);
        gui.setTimelineBackgroundColor(backgroundColor);
    }

    /**
     * Adds the events contained within the current source file to the timeline.
     * @param lines The contents of the current source file, broken up by line.
     * @throws ParseException In the event of an error while parsing each event, a ParseException is thrown.
     */
    private void readFileEvents(ArrayList<String> lines) throws ParseException {
        ArrayList<String> eventLines = new ArrayList<>(lines.subList(3, lines.size()));
        for (String line:eventLines) { addEventFromLine(line); }
    }

    /**
     * Validates that the given file is of the TIMELINE file format.
     * @throws IOException In the event that the current source file is not of the TIMELINE file format, an IOException is thrown.
     */
    private void validateTimelineFileExtension(String filePath) throws IOException {
        String extension = getFileExtension(filePath);
        if (!extension.equals(TIMELINE_FILE_EXTENSION)) { throw new IOException(); }
    }

    /**
     * Alerts the user that an error occurred while loading the current source file.
     * @param e The exception caught during the file loading process.
     */
    private void showInvalidSourceFileError(Exception e) {
        e.printStackTrace();
        gui.showErrorMessage("File cannot be parsed. Invalid contents.");
        isTimelineSaved = true;
        reset();
    }

    /**
     * Parses a given line from the current source file for event details, then adds such an event to the timeline.
     * @param line The line to be parsed.
     * @throws ParseException In the event of an error while parsing the event details, a ParseException is thrown.
     */
    private void addEventFromLine(String line) throws ParseException {
        String[] parsable = line.split(",");
        // Line format: eventName,isSpanOfTime(int),isAboveTimelineBar(int),fontName,fgR,fgG,fgB,bgR,bgG,bgB,start,end(if isSpanOfTime == true)

        String name = parsable[0];
        boolean isSpanOfTime = parsable[1].equals("1");
        boolean isAboveTimelineBar = parsable[2].equals("1");
        String fontName = parsable[3];
        Color foregroundColor = toColor(parsable[4], parsable[5], parsable[6]);
        Color backgroundColor = toColor(parsable[7], parsable[8], parsable[9]);
        Date startingTime = formatter.parse(parsable[10]);
        Date endingTime = null;
        if (isSpanOfTime) { endingTime = formatter.parse(parsable[11]); }

        add(name, startingTime, endingTime, fontName, foregroundColor, backgroundColor, isAboveTimelineBar);
    }

    /**
     * Converts 3 strings containing integer values into a Color object.
     * @param r The red value of the produced color.
     * @param g The green value of the produced color.
     * @param b The blue value of the produced color.
     * @return The produced color.
     * @throws ParseException In the event an error occurs while producing a color from the specified strings, a ParseException is thrown.
     */
    private Color toColor(String r, String g, String b) throws ParseException {
        try {
            int redValue = Integer.parseInt(r);
            int greenValue = Integer.parseInt(g);
            int blueValue = Integer.parseInt(b);
            return new Color(redValue, greenValue, blueValue);
        }
        catch (Exception e) { throw new ParseException("Attempted to create a color from an invalid RGB value string.", 0); }
    }


    // Information requests

    public Date getEarliestTime() { return earliestTime; }

    public Date getLatestTime() { return latestTime; }

    public ArrayList<Event> getAllEvents() { return new ArrayList<>(events); }

}
