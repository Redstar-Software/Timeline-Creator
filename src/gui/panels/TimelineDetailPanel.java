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

package gui.panels;

import gui.GUI;
import gui.listeners.TimelineBackgroundColorPicker;
import gui.listeners.TimelineForegroundColorPicker;
import gui.listeners.UpdateActionListener;
import gui.listeners.UpdateFocusListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * A panel used to modify the appearance of the timeline as a whole.
 */
public class TimelineDetailPanel extends JPanel {

    private final GUI gui;

    private JTextField titleField;
    private JButton foregroundColorButton;
    private JButton backgroundColorButton;
    private JComboBox<String> fontNameField;

    public String selectedFont;
    public Color foregroundColor;
    public Color backgroundColor;

    public final String DEFAULT_TITLE = "Timeline Title";
    private final int DEFAULT_FONT_SIZE = 25;
    private final Font DEFAULT_WIDGET_FONT = new Font("Times New Roman", Font.PLAIN, DEFAULT_FONT_SIZE);
    private final String DEFAULT_SELECTED_FONT_NAME = "Times New Roman";
    private final String[] FONTS = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();


    // Initialization Methods

    /**
     * Constructs a TimelineDetailPanel associated with a given GUI.
     * @param gui The GUI associated with the TimelineDetailPanel.
     */
    public TimelineDetailPanel(GUI gui) {
        this.gui = gui;
        initialize();
    }

    /**
     * Initializes the panel of the TimelineDetailPanel.
     */
    private void initialize() {
        this.setLayout(new FlowLayout());
        Border border = new LineBorder(gui.palette.BORDER_COLOR, 1);
        this.setBorder(border);
        this.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
    }

    /**
     * Initializes and reinitializes the content of the TimelineDetailsPanel. Details of the timeline's appearance may be preserved across reinitializations.
     * @param resetDetails Whether the current details of the timeline are to reset to their default states.
     */
    public void populate(boolean resetDetails) {
        this.removeAll();
        this.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
        initializeTitleField(resetDetails);
        initializeButtons(resetDetails);
        initializeFontNameSelector(resetDetails);
        this.revalidate();
        this.repaint();
    }

    /**
     * Initializes the timeline title field of the TimelineDetailPanel.
     * @param resetDetails Whether the title filling the title field is to be set to its default value.
     */
    private void initializeTitleField(boolean resetDetails) {
        String title;
        if (titleField == null || resetDetails) {title = DEFAULT_TITLE; }
        else { title = titleField.getText(); }
        titleField = new JTextField(title);
        titleField.setFont(DEFAULT_WIDGET_FONT);
        titleField.setPreferredSize(new Dimension(375, 35));
        titleField.setHorizontalAlignment(JTextField.CENTER);
        titleField.addFocusListener(new UpdateFocusListener(gui));
        titleField.addActionListener(new UpdateActionListener(gui));
        titleField.setBackground(gui.palette.GUI_FOREGROUND_COLOR);
        titleField.setForeground(gui.palette.TEXT_COLOR);
        this.add(titleField);
    }

    /**
     * Initializes the buttons of the TimelineDetailPanel.
     * @param resetDetails Whether the foreground and background colors of the TimelineDetailPanel are to be set to their default colors.
     */
    private void initializeButtons(boolean resetDetails) {
        foregroundColorButton = new JButton("Foreground Color");
        setInitialForegroundColor(resetDetails);
        foregroundColorButton.setBackground(foregroundColor);
        foregroundColorButton.addActionListener(new TimelineForegroundColorPicker(this));
        foregroundColorButton.setPreferredSize(new Dimension(215, 35));
        foregroundColorButton.setForeground(getVisibleCaptionColor(gui.palette.TIMELINE_FOREGROUND_COLOR));
        foregroundColorButton.setFont(DEFAULT_WIDGET_FONT);
        foregroundColorButton.setFocusable(false);
        this.add(foregroundColorButton);

        backgroundColorButton = new JButton("Background Color");
        setInitialBackgroundColor(resetDetails);
        backgroundColorButton.setBackground(backgroundColor);
        backgroundColorButton.addActionListener(new TimelineBackgroundColorPicker(this));
        backgroundColorButton.setPreferredSize(new Dimension(220, 35));
        backgroundColorButton.setForeground(getVisibleCaptionColor(gui.palette.TIMELINE_BACKGROUND_COLOR));
        backgroundColorButton.setFont(DEFAULT_WIDGET_FONT);
        backgroundColorButton.setFocusable(false);
        this.add(backgroundColorButton);
    }

    /**
     * Initializes the font selector combo box of the TimelineDetailPanel.
     * @param resetDetails Whether the 'fontNameField' is to have its selected font set to its default value.
     */
    private void initializeFontNameSelector(boolean resetDetails) {
        fontNameField = new JComboBox<>(FONTS);
        fontNameField.setFont(DEFAULT_WIDGET_FONT);
        if (selectedFont == null || resetDetails) { selectedFont = DEFAULT_SELECTED_FONT_NAME; }
        fontNameField.setSelectedItem(selectedFont);

        ActionListener fontUpdater = e -> {
            int index = fontNameField.getSelectedIndex();
            selectedFont = FONTS[index];
        };
        fontNameField.addActionListener(new UpdateActionListener(gui));
        fontNameField.addActionListener(fontUpdater);
        fontNameField.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
        fontNameField.setForeground(gui.palette.TEXT_COLOR);
        this.add(fontNameField);
    }

    /**
     * Returns a color which contrasts the given color to a suitable degree for a legible caption.
     * @param color The color to be contrasted.
     * @return The color of suitable contrast.
     */
    private Color getVisibleCaptionColor(Color color) {
        if (color == null) {return Color.BLACK; }
        int sum = color.getRed() + color.getGreen() + color.getBlue();
        if (sum < 256) { return Color.WHITE; }
        else { return Color.BLACK; }
    }

    /**
     * Sets the color of the timeline foreground upon initialization.
     * @param resetDetails Whether the foreground color is to be reset regardless of external factors.
     */
    private void setInitialForegroundColor(boolean resetDetails) {
        if (foregroundColor == null || gui.DEFAULT_TIMELINE_FOREGROUNDS.contains(foregroundColor) || resetDetails) {
            foregroundColor = gui.palette.TIMELINE_FOREGROUND_COLOR;
        }
    }

    /**
     * Sets the color of the timeline background upon initialization.
     * @param resetDetails Whether the background color is to be reset regardless of external factors.
     */
    private void setInitialBackgroundColor(boolean resetDetails) {
        if (backgroundColor == null || gui.DEFAULT_TIMELINE_BACKGROUNDS.contains(backgroundColor) || resetDetails) {
            backgroundColor = gui.palette.TIMELINE_BACKGROUND_COLOR;
        }
    }


    // Public Methods

    /**
     * Sets the timeline title to the given string.
     */
    public void setTimelineTitle(String title) { titleField.setText(title); }

    /**
     * Sets the timeline foreground color to the given color.
     */
    public void setTimelineForegroundColor(Color color) {
        foregroundColor = color;
        foregroundColorButton.setBackground(color);
        foregroundColorButton.setForeground(getVisibleCaptionColor(color));
        gui.updateTimelinePanels();
    }

    /**
     * Sets the timeline background color to the given color.
     */
    public void setTimelineBackgroundColor(Color color) {
        backgroundColor = color;
        backgroundColorButton.setBackground(color);
        backgroundColorButton.setForeground(getVisibleCaptionColor(color));
        gui.updateTimelinePanels();
    }

    /**
     * Prompts the user to choose the timeline foreground color.
     */
    public void pickTimelineForegroundColor() {
        Color color = JColorChooser.showDialog(null, "Foreground Color", gui.palette.TIMELINE_FOREGROUND_COLOR);
        if (color != null) {
            setTimelineForegroundColor(color);
            gui.markUnsavedChanges();
        }
    }

    /**
     * Prompts the user to choose the timeline background color.
     */
    public void pickTimelineBackgroundColor() {
        Color color = JColorChooser.showDialog(null, "Background Color", gui.palette.TIMELINE_BACKGROUND_COLOR);
        if (color != null) {
            setTimelineBackgroundColor(color);
            gui.markUnsavedChanges();
        }
    }

    /**
     * Sets the font selected by the 'fontNameField' combo box to the given font.
     */
    public void setSelectedFont(String fontName) {
        try {
            fontNameField.setSelectedItem(fontName);
            selectedFont = fontName;
        }
        catch (Exception e) {
            e.printStackTrace();
            gui.showErrorMessage("Attempted to set invalid font.");
        }
    }


    // Information Requests

    public String getTimelineTitle() { return titleField.getText(); }

}
