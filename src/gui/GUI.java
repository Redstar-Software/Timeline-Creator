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

package gui;

import eventHandler.Event;
import eventHandler.EventHandler;
import gui.elements.PaletteElement;
import gui.listeners.*;
import gui.panels.EventPanel;
import gui.panels.TimelineDetailPanel;
import gui.panels.TimelineImagePanel;
import gui.panels.TimelineListPanel;
import imageProcessor.ImageProcessor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * An object used to interact with and facilitate communication between GUI elements. Directly interacts with the EventHandler and ImageProcessor.
 */
public class GUI {

    private EventHandler handler;
    private ImageProcessor imageProcessor;

    private JFrame frame;
    private TimelineListPanel timelineListPanel;
    private TimelineDetailPanel timelineDetailPanel;
    private TimelineImagePanel timelineImagePanel;
    private EventPanel eventPanel;
    public PaletteElement palette;
    public String paletteName;

    private final String ICON_PATH = "assets/logo.png";
    private final String PALETTE_PATH = "assets/palette.txt";
    private final Color DEFAULT_BACKGROUND_COLOR = new Color(238, 238, 238);

    private final PaletteElement LIGHT_PALETTE = new PaletteElement(DEFAULT_BACKGROUND_COLOR, Color.WHITE,  Color.WHITE,
            Color.BLACK,  Color.LIGHT_GRAY,  Color.LIGHT_GRAY, Color.BLACK);

    private final PaletteElement DARK_PALETTE = new PaletteElement(Color.DARK_GRAY, Color.GRAY,  Color.BLACK,
            Color.WHITE,  Color.GRAY,  Color.GRAY, Color.LIGHT_GRAY);

    private final PaletteElement HIGH_CONTRAST_PALETTE = new PaletteElement(Color.BLACK, Color.GRAY,  Color.DARK_GRAY,
            Color.LIGHT_GRAY,  Color.DARK_GRAY,  Color.WHITE, Color.WHITE);

    public final ArrayList<Color> DEFAULT_TIMELINE_BACKGROUNDS = new ArrayList<>(Arrays.asList(
            LIGHT_PALETTE.TIMELINE_BACKGROUND_COLOR,
            DARK_PALETTE.TIMELINE_BACKGROUND_COLOR,
            HIGH_CONTRAST_PALETTE.TIMELINE_BACKGROUND_COLOR
    ));

    public final ArrayList<Color> DEFAULT_TIMELINE_FOREGROUNDS = new ArrayList<>(Arrays.asList(
            LIGHT_PALETTE.TIMELINE_BACKGROUND_COLOR,
            DARK_PALETTE.TIMELINE_BACKGROUND_COLOR,
            HIGH_CONTRAST_PALETTE.TIMELINE_BACKGROUND_COLOR
    ));


    // Initialization Methods

    /**
     * Constructs a new GUI and initializes all associated components.
     */
    public GUI() {
        loadPalettePreference();
        initializeFrame();
        initializePanels();
        addPanelsToFrame();
        initializeMenuBar();
    }

    /**
     * Populates each panel of the GUI.
     */
    public void populate() {
        timelineListPanel.populate();
        timelineDetailPanel.populate(false);
        timelineImagePanel.populate();
        eventPanel.populate(null);
    }

    /**
     * Loads the user's palette preference from a text file and assumes the appropriate theme.
     */
    private void loadPalettePreference() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(PALETTE_PATH));
            paletteName = reader.readLine();
            reader.close();
        } catch (Exception e) { e.printStackTrace(); }

        if (paletteName == null) { paletteName = "light"; }

        switch (paletteName) {
            case "light": palette = LIGHT_PALETTE; break;
            case "dark": palette = DARK_PALETTE; break;
            case "high contrast": palette = HIGH_CONTRAST_PALETTE; break;
        }
    }

    /**
     * Initializes the frame of the GUI.
     */
    private void initializeFrame() {
        frame = new JFrame();
        frame.setSize(1700, 950);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Redstar Timeline Creator");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addKeyListener(new KeyboardShortcutListener(this));
        setFrameIcon();
        setWindowCloser();
        frame.setVisible(true);
    }

    /**
     * Initializes each panel of the GUI.
     */
    private void initializePanels() {
        timelineListPanel = new TimelineListPanel(this);
        timelineDetailPanel = new TimelineDetailPanel(this);
        timelineImagePanel = new TimelineImagePanel(this);
        eventPanel = new EventPanel(this);
    }

    /**
     * Adds each of the panels to the frame.
     */
    private void addPanelsToFrame() {
        JScrollPane listScrollPane = new JScrollPane(timelineListPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.add(listScrollPane, BorderLayout.WEST);

        frame.add(eventPanel, BorderLayout.EAST);
        frame.add(timelineDetailPanel, BorderLayout.NORTH);

        JScrollPane imageScrollPane = new JScrollPane(timelineImagePanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        frame.add(imageScrollPane, BorderLayout.CENTER);
    }

    /**
     * Initializes the menu bar of the GUI.
     */
    public void initializeMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(palette.MENU_BAR_COLOR);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(palette.TEXT_COLOR);
        menuBar.add(fileMenu);

        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(new FileResetter(this));
        newItem.setBackground(palette.MENU_BAR_COLOR);
        newItem.setForeground(palette.TEXT_COLOR);
        fileMenu.add(newItem);

        JMenuItem loadItem = new JMenuItem("Open...");
        loadItem.addActionListener(new FileLoader(this));
        loadItem.setBackground(palette.MENU_BAR_COLOR);
        loadItem.setForeground(palette.TEXT_COLOR);
        fileMenu.add(loadItem);

        JMenuItem saveItem = new JMenuItem("Save       Ctrl+S");
        saveItem.addActionListener(new FileSaver(this, false));
        saveItem.setBackground(palette.MENU_BAR_COLOR);
        saveItem.setForeground(palette.TEXT_COLOR);
        fileMenu.add(saveItem);

        JMenuItem saveAsItem = new JMenuItem("Save as...");
        saveAsItem.addActionListener(new FileSaver(this, true));
        saveAsItem.setBackground(palette.MENU_BAR_COLOR);
        saveAsItem.setForeground(palette.TEXT_COLOR);
        fileMenu.add(saveAsItem);

        JMenu exportMenu = new JMenu("Export");
        exportMenu.setForeground(palette.TEXT_COLOR);
        menuBar.add(exportMenu);

        JMenuItem exportImageItem = new JMenuItem("Export Image             Ctrl+D");
        exportImageItem.addActionListener(new ImageSaver(this, false));
        exportImageItem.setBackground(palette.MENU_BAR_COLOR);
        exportImageItem.setForeground(palette.TEXT_COLOR);
        exportMenu.add(exportImageItem);

        JMenuItem exportForegroundItem = new JMenuItem("Export Foreground   Ctrl+F");
        exportForegroundItem.addActionListener(new ImageSaver(this, true));
        exportForegroundItem.setBackground(palette.MENU_BAR_COLOR);
        exportForegroundItem.setForeground(palette.TEXT_COLOR);
        exportMenu.add(exportForegroundItem);

        JMenu appearanceMenu = new JMenu("Appearance");
        appearanceMenu.setForeground(palette.TEXT_COLOR);
        menuBar.add(appearanceMenu);

        JMenuItem lightThemeItem = new JMenuItem("Light Theme");
        lightThemeItem.addActionListener(new PaletteSetter(this, LIGHT_PALETTE, "light"));
        lightThemeItem.setBackground(palette.MENU_BAR_COLOR);
        lightThemeItem.setForeground(palette.TEXT_COLOR);
        appearanceMenu.add(lightThemeItem);

        JMenuItem darkThemeItem = new JMenuItem("Dark Theme");
        darkThemeItem.addActionListener(new PaletteSetter(this, DARK_PALETTE, "dark"));
        darkThemeItem.setBackground(palette.MENU_BAR_COLOR);
        darkThemeItem.setForeground(palette.TEXT_COLOR);
        appearanceMenu.add(darkThemeItem);

        JMenuItem highContrastThemeItem = new JMenuItem("High Contrast Theme");
        highContrastThemeItem.addActionListener(new PaletteSetter(this, HIGH_CONTRAST_PALETTE, "high contrast"));
        highContrastThemeItem.setBackground(palette.MENU_BAR_COLOR);
        highContrastThemeItem.setForeground(palette.TEXT_COLOR);
        appearanceMenu.add(highContrastThemeItem);

        frame.setJMenuBar(menuBar);
    }

    /**
     * Loads program's icon from the assets folder.
     */
    private void setFrameIcon() {
        File iconFile = new File(ICON_PATH);
        try {
            Image icon = ImageIO.read(iconFile);
            frame.setIconImage(icon);
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Sets the closing operation of the window.
     */
    private void setWindowCloser() {
        WindowAdapter windowCloser = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                boolean performOperation = canOperationProceedDespiteUnsavedChanges();
                if (performOperation) {
                    savePalettePreference();
                    System.exit(0);
                }
            }
        };
        frame.addWindowListener(windowCloser);
    }

    /**
     * Saves the currently selected palette to a text file so that the preference is remembered.
     */
    private void savePalettePreference() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(PALETTE_PATH));
            writer.write(paletteName);
            writer.close();
        } catch (Exception e) { e.printStackTrace(); }
    }


    // Public Methods

    /**
     * Sets the handler utilized by the GUI to the given EventHandler.
     * @param handler The given EventHandler.
     */
    public void setHandler(EventHandler handler) { this.handler = handler; }

    /**
     * Sets the image processor utilized by the GUI to the given ImageProcessor.
     * @param imageProcessor The given ImageProcessor.
     */
    public void setImageProcessor(ImageProcessor imageProcessor) { this.imageProcessor = imageProcessor; }

    /**
     * Updates the panels which must consistently reflect the current timeline.
     */
    public void updateTimelinePanels() {
        timelineListPanel.populate();
        timelineImagePanel.populate();
    }

    /**
     * Resets the timeline image's colors and font to their default values.
     */
    public void resetTimelineDetails() {
        timelineDetailPanel.populate(true);
        timelineDetailPanel.setTimelineTitle(timelineDetailPanel.DEFAULT_TITLE);
    }

    /**
     * Sets the title of the timeline.
     * @param title The new timeline title.
     */
    public void setTimelineTitle(String title) { timelineDetailPanel.setTimelineTitle(title); }

    /**
     * Sets the foreground color of the timeline.
     * @param color The new foreground color.
     */
    public void setTimelineForegroundColor(Color color) { timelineDetailPanel.setTimelineForegroundColor(color); }

    /**
     * Sets the background color of the timeline.
     * @param color The new background color.
     */
    public void setTimelineBackgroundColor(Color color) { timelineDetailPanel.setTimelineBackgroundColor(color); }

    /**
     * Sets the font of the timeline title.
     * @param font The name of the new timeline title font.
     */
    public void setTimelineTileFont(String font) { timelineDetailPanel.setSelectedFont(font); }

    /**
     * Marks that the user has made changes to the timeline.
     */
    public void markUnsavedChanges() { handler.isTimelineSaved = false; }

    /**
     * Exports the current timeline image to a PNG file.
     */
    public void exportImage() { timelineImagePanel.saveImageToFile(); }

    /**
     * Exports the foreground of the current timeline image to a PNG file. The Background is replaced with an alpha layer.
     */
    public void exportForeground() { timelineImagePanel.saveForegroundToFile(); }

    /**
     * Prompts the user to save their timeline to a TIMELINE file before performing an operation.
     * @return Whether to proceed with the operation performed by the user before the prompt was given.
     */
    public boolean canOperationProceedDespiteUnsavedChanges() {
        boolean performOperationOnDialogueClose;
        if (!handler.isTimelineSaved) {
            int response = JOptionPane.showConfirmDialog(frame,
                    "Your timeline has unsaved changes. Save before exiting?",
                    "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);

            if (response == JOptionPane.YES_OPTION) { handler.saveToFile(false); }
            performOperationOnDialogueClose = (response == JOptionPane.YES_OPTION || response == JOptionPane.NO_OPTION);

        } else { performOperationOnDialogueClose = true; }
        return performOperationOnDialogueClose;
    }

    /**
     * Displays an error message to the user.
     * @param errorMessage The error message to be displayed.
     */
    public void showErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(frame, errorMessage, "An error has occurred.", JOptionPane.ERROR_MESSAGE);
    }


    // Information Requests

    /**
     * Returns the EventHandler of the GUI.
     */
    public EventHandler getHandler() { return handler; }

    /**
     * Returns the EventPanel of the GUI.
     */
    public EventPanel getEventPanel() { return eventPanel; }

    /**
     * Returns all the events within the timeline as an ArrayList of Event objects.
     */
    public ArrayList<Event> getTimelineEvents() { return handler.getAllEvents(); }

    /**
     * Returns the title of the timeline.
     */
    public String getTimelineTitle() { return timelineDetailPanel.getTimelineTitle(); }

    /**
     * Returns the foreground color of the timeline.
     */
    public Color getTimelineForegroundColor() { return timelineDetailPanel.foregroundColor; }

    /**
     * Returns the background color of the timeline.
     */
    public Color getTimelineBackgroundColor() { return timelineDetailPanel.backgroundColor; }

    /**
     * Returns the name of the font of the timeline's title.
     */
    public String getTimelineTileFontName() { return timelineDetailPanel.selectedFont; }

    /**
     * Generates and returns an image representing the current timeline.
     */
    public BufferedImage getTimelineImage() { return imageProcessor.generateImage(); }

}
