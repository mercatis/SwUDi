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

package swudi.device;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

/**
 * Created: 02.12.11   by: Armin Haaf
 * <p/>
 * <p/>
 * <p/>
 * a USBDisplay to show in a JLabel
 *
 * @author Armin Haaf
 */
public class BufferedUSBDisplay extends JLabel implements USBDisplay {

    private BufferedImage offScreenImage;

    private int refreshRate = 10;

    private State state = State.ON;

    // the current touch position. a TouchEvent is fired, when the currentTouch changes
    private Point currentTouch;

    private TouchEventHandler touchEventHandler;

    private final Timer refreshTimer = new Timer(1000 / refreshRate, new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
            ImageIcon tIcon = new ImageIcon();
            tIcon.setImage(offScreenImage);
            setIcon(tIcon);
        }
    });

    public BufferedUSBDisplay(final BufferedImage pPOffScreenImage) {
        offScreenImage = pPOffScreenImage;

        final Dimension tScreenSize = new Dimension(offScreenImage.getWidth(), offScreenImage.getHeight());
        super.setSize(tScreenSize);
        super.setMaximumSize(tScreenSize);
        super.setPreferredSize(tScreenSize);
        super.setMinimumSize(tScreenSize);

        refreshTimer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                setCurrentTouch(e.getPoint());
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                setCurrentTouch(null);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(final MouseEvent e) {
                setCurrentTouch(e.getPoint());
            }
        });

        clearScreen();
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(final int pRefreshRate) {
        refreshRate = pRefreshRate;
        refreshTimer.setDelay(1000 / refreshRate);
    }

    private synchronized void setCurrentTouch(final Point pPoint) {
        if (!((pPoint == null) ? (currentTouch == null) : pPoint.equals(currentTouch))) {
            // if no equals, send MouseEvent...
            currentTouch = pPoint;
            if (touchEventHandler != null) {
                touchEventHandler.onTouchEvent(currentTouch);
            }
        }
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
    public void setTouchEventHandler(final TouchEventHandler pTouchEventHandler) {
        touchEventHandler = pTouchEventHandler;
    }

    @Override
    public BufferedImage createOffScreenBuffer() {
        return offScreenImage;
    }

    @Override
    public void clearScreen() {
        offScreenImage.getGraphics().setColor(Color.WHITE);
        offScreenImage.getGraphics().fillRect(0, 0, offScreenImage.getWidth(), offScreenImage.getHeight());
    }

    @Override
    public void setContrast(final int pContrast) {
    }

    @Override
    public int getContrast() {
        return 100;
    }

    @Override
    public void setInverted(final boolean pInverted) {
    }

    @Override
    public boolean isInverted() {
        return false;
    }

    @Override
    public void calibrateTouch() {
    }

    @Override
    public void paint(final BufferedImage pBufferedImage, final int x, final int y, final int pWidth, final int pHeight) {
        if (pBufferedImage != offScreenImage) {
            offScreenImage.getRaster().setRect(x, y, pBufferedImage.getRaster());
        }
    }

    @Override
    public void close() {
    }

    @Override
    public void setBacklight(final int pBrightness) {
    }

    @Override
    public int getBacklight() {
        return 100;
    }

    @Override
    public void reset() {
    }

    @Override
    public void setState(final State pState) {
        state = pState;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setOutput(final int pBitMask) {
    }
}
