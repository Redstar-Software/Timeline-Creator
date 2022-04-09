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

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * A panel used to display the current timeline image.
 */
public class TimelineImagePanel extends JPanel {

    private final GUI gui;

    private BufferedImage image;


    // Initialization Methods

    /**
     * Constructs a TimelineImagePanel associated with a given GUI.
     * @param gui The GUI associated with the TimelineImagePanel.
     */
    public TimelineImagePanel(GUI gui) {
        this.gui = gui;
        initialize();
    }

    /**
     * Initializes the panel of the TimelineImagePanel.
     */
    private void initialize() {
        Border border = new LineBorder(gui.palette.BORDER_COLOR, 1);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBorder(border);
    }

    /**
     * Initializes the contents of the TimelineImagePanel.
     */
    public void populate() {
        this.removeAll();
        this.setBackground(gui.palette.GUI_BACKGROUND_COLOR);
        initializeImage();
        this.repaint();
        this.revalidate();
    }

    /**
     * Generates and displays the current timeline image.
     */
    private void initializeImage() {
        image = gui.getTimelineImage();
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        imageLabel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        this.add(imageLabel);
    }


    // Public Methods

    /**
     * Saves only the timeline image foreground to a PNG file. The background of the image is left as an alpha layer.
     */
    public void saveForegroundToFile() {
        Color backgroundColor = gui.getTimelineBackgroundColor();
        gui.setTimelineBackgroundColor(null);
        image = gui.getTimelineImage();
        saveImageToFile();
        gui.setTimelineBackgroundColor(backgroundColor);
        image = gui.getTimelineImage();
    }

    /**
     * Saves the current timeline image to a PNG file.
     */
    public void saveImageToFile() {
        String destination = pickDestinationFile();
        try {
            assert destination != null;
            String extension = getFileExtension(destination);
            if (extension == null || !extension.equals(".png")) { destination += ".png"; }
            File imageFile = new File(destination);
            ImageIO.write(image, "png", imageFile);
        }
        catch (IOException e) { gui.showErrorMessage("Invalid destination file."); }
        catch (Exception e) { e.printStackTrace(); }

    }

    /**
     * Prompts the user to choose a destination file for the timeline image. Returns the path to the chosen file.
     * @return The path to the chosen file. May be null.
     */
    private String pickDestinationFile() {
        JFileChooser fileChooser = new JFileChooser();
        int response = fileChooser.showSaveDialog(null);
        if (response == JFileChooser.APPROVE_OPTION) { return fileChooser.getSelectedFile().getAbsolutePath(); }
        return null;
    }

    /**
     * Returns the extension of a given file.
     * @param filePath The path to the given file.
     * @return The extension of the given file.
     */
    private String getFileExtension(String filePath) {
        String[] parsablePath = filePath.split("\\\\");
        String name = parsablePath[parsablePath.length - 1];
        String[] parsableName = name.split("\\.");
        if (parsableName.length < 2) { return null; }
        return "." + parsableName[parsableName.length - 1];
    }
}
