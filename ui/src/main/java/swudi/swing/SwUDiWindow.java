/*
 * Copyright 2011 mercatis technologies AG
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package swudi.swing;

import swudi.device.DoubleBufferRenderer;
import swudi.device.SwUDiRepaintManager;
import swudi.device.USBDisplay;

import javax.swing.JWindow;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.TimerTask;

/**
 * Created: 05.12.11   by: Armin Haaf
 * <p/>
 * <p/>
 * <p/>
 * Uses a window to manage the swing repaints and events. The window should be in an unvisible area (setLocation(-10000,-10000).
 * However Popups are always rendered in the visible area -> so it is better to not use popups, as they just occur on the
 * screen and are not rendered on the usb display
 *
 * @author Armin Haaf
 */
public class SwUDiWindow extends JWindow implements USBDeviceFrame {

    static final SwUDiRepaintManager REPAINT_MANAGER = new SwUDiRepaintManager();

    static {
        // install the SwUDi repaint manager to track dirty windows
        RepaintManager.setCurrentManager(REPAINT_MANAGER);
    }

    private final USBDisplay usbDisplay;

    private BufferedImage offScreenBuffer;

    private SwUDiTouchPadMouse mouse = new SwUDiTouchPadMouse(this);

    private int paintRate = 10;

    private DisplayRenderer displayRefresh;

    // paint should be done in event thread
    private final Timer paintTimer;

    private final Point lastPaintedMousePosition = new Point();

    {
        paintTimer = new Timer(1000 / paintRate, new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                // TODO mouse pointer painting did not work

                if (REPAINT_MANAGER.isDirty(SwUDiWindow.this) || (paintMousePointer && !mouse.getPosition().equals(lastPaintedMousePosition))) {
                    if (paintMousePointer) {
                        // use xor it before and after to temporarily draw the mouse, however it did not work...
                        getGraphics().setXORMode(Color.WHITE);
                        mouse.paintMousePointer(getGraphics());
                    }
                    displayRefresh.render(null);
                    if (paintMousePointer) {
                        getGraphics().setXORMode(Color.BLACK);
                        mouse.paintMousePointer(getGraphics());
                        getGraphics().setPaintMode();
                    }
                    lastPaintedMousePosition.setLocation(mouse.getPosition());
                    REPAINT_MANAGER.resetDirtyFlag(SwUDiWindow.this);
                }
            }
        });
    }

    private boolean paintMousePointer = false;
    private int mouseRefreshRate = 50;

    // use a timer outside eventdispatching to get the touch info
    // -> howver mouse events must be send inside the eventdispatching thread
    private java.util.TimerTask mouseRefreshTimer = new java.util.TimerTask() {
        @Override
        public void run() {
            pollTouch();
        }
    };

    // TODO use executor
    static java.util.Timer TIMER = new java.util.Timer("SWUDI_MOUSE_REFRESH_TIMER");


    public SwUDiWindow(USBDisplay pUSBDisplay) throws HeadlessException {
        usbDisplay = pUSBDisplay;

        displayRefresh = new DoubleBufferRenderer(usbDisplay);
        offScreenBuffer = displayRefresh.getOffScreenImage();

        final Dimension tScreenSize = new Dimension(displayRefresh.getOffScreenImage().getWidth(), displayRefresh.getOffScreenImage().getHeight());
        super.setBounds(0, 0, tScreenSize.width, tScreenSize.height);
        super.setMaximumSize(tScreenSize);
        super.setPreferredSize(tScreenSize);
        super.setMinimumSize(tScreenSize);

        usbDisplay.clearScreen();

        paintTimer.start();
        TIMER.scheduleAtFixedRate(mouseRefreshTimer, 1000 / mouseRefreshRate, 1000 / mouseRefreshRate);

        setAlwaysOnTop(false);
        setIgnoreRepaint(true);

        setLocation(-10000, -10000);
    }

    public void setPaused(boolean pPaused) {
        usbDisplay.setPaused(pPaused);
    }

    @Override
    /**
     * thats the magic -> everything is painted in this graphics, so need need to do the paint twice
     */
    public Graphics getGraphics() {
        return offScreenBuffer.getGraphics();
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public boolean isShowing() {
        return true;
    }

    @Override
    public void setSize(final int width, final int height) {
        // ignore
    }

    @Override
    public void setPreferredSize(final Dimension preferredSize) {
        // ignore
    }

    @Override
    public void setMaximumSize(final Dimension maximumSize) {
        // ignore
    }

    @Override
    public void setMinimumSize(final Dimension minimumSize) {
        // ignore
    }

    @Override
    public int getMouseRefreshRate() {
        return mouseRefreshRate;
    }

    @Override
    public void setMouseRefreshRate(final int pMouseRefreshRate) {
        mouseRefreshRate = pMouseRefreshRate;
        mouseRefreshTimer.cancel();
        mouseRefreshTimer = new TimerTask() {
            @Override
            public void run() {
                pollTouch();
            }
        };
        TIMER.schedule(mouseRefreshTimer, 1000 / mouseRefreshRate, 1000 / mouseRefreshRate);
    }


    @Override
    public int getPaintRate() {
        return paintRate;
    }

    @Override
    public void setPaintRate(final int pPaintRate) {
        paintRate = pPaintRate;
        paintTimer.setDelay(1000 / paintRate);
    }

    @Override
    public boolean isPaintMousePointer() {
        return paintMousePointer;
    }

    @Override
    public void setPaintMousePointer(final boolean pPaintMousePointer) {
        paintMousePointer = pPaintMousePointer;
    }

    @Override
    public void paint(final Graphics g) {
        super.paint(g);
        if (paintMousePointer) {
            mouse.paintMousePointer((Graphics2D) g);
        }
    }

    private void pollTouch() {
        try {
            if (!usbDisplay.isPaused()) {
                final Point tTouchPoint = usbDisplay.getTouch();
                // update the mouse in the event thread -> we do not need to make more events than event dispatching can handle, so invokeAndWait is ok
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        mouse.setTouchPosition(tTouchPoint != null ? SwingUtilities.convertPoint(SwUDiWindow.this, tTouchPoint, SwUDiWindow.this) : null);
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public USBDisplay getUSBDisplay() {
        return usbDisplay;
    }
}
