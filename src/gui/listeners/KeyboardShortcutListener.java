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

import eventHandler.EventHandler;
import gui.GUI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * A KeyListener used to implement keyboard shortcuts.
 */
public class KeyboardShortcutListener implements KeyListener {

    private final GUI gui;

    private boolean isControlHeld = false;

    /**
     * Constructs a KeyboardShortcutListener associated with a given GUI.
     * @param gui The GUI associated with the KeyboardShortcutListener.
     */
    public KeyboardShortcutListener(GUI gui) { this.gui = gui; }

    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Used to determine both if a keyboard shortcut has been executed and whether the control key has been held by the user.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (isControlHeld && !isKeyControl(e)) {
            int pressedCharacterCode = e.getKeyCode();
            switch (pressedCharacterCode) {
                case KeyEvent.VK_S: saveToFile(); break;
                case KeyEvent.VK_D: gui.exportImage(); break;
                case KeyEvent.VK_F: gui.exportForeground(); break;
            }
        }
        else if (isKeyControl(e)) { isControlHeld = true; }
    }

    /**
     * Used to determine whether the control key has been released by the user.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (isKeyControl(e)) { isControlHeld = false; }
    }

    private boolean isKeyControl(KeyEvent e) { return e.getKeyCode() == KeyEvent.VK_CONTROL; }

    private void saveToFile() {
        EventHandler handler = gui.getHandler();
        if (handler != null) { handler.saveToFile(false); }
    }
}
