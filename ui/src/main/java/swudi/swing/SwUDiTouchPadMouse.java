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

import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * <!--
 * Created: 02.12.11   by: Armin Haaf
 * <p/>
 *
 * <p/>
 * Maps Touchpad Input to MouseEvents
 *
 * @author Armin Haaf
 */
public class SwUDiTouchPadMouse {

    /**
     * The current mouse position
     */
    protected final Point position = new Point(0, 0);

    /**
     * we need this to calculate moving event.
     */
    protected Point lastTouchPoint = null;

    /**
     * we need this to send a release event, if the mouse exists a component which got a pressed event
     */
    protected MouseEvent lastMousePressedEvent = null;

    /**
     * this is currently unused!
     */
    protected MouseEvent lastMouseReleasedEvent = null;

    /**
     * this is used to calculate click count
     */
    protected MouseEvent lastMouseClickEvent = null;

    /**
     * the aggregated clickcount to set in the events
     */
    protected int clickCount = 1;

    /**
     * this is needed to calculate the components under the mouse
     */
    protected Container rootContainer;

    /**
     * the time in which a press and release results in a click event
     */
    private long clickTimeoutMillis = 500;

    /**
     * the time in which clicks increases the click count -> double click...
     */
    private long multipleClickTimeoutMillis = 500;

    public SwUDiTouchPadMouse(final Container pRootContainer) {
        rootContainer = pRootContainer;
    }

    public void paintMousePointer(final Graphics pGraphics) {
        pGraphics.setColor(Color.BLACK);
        pGraphics.drawOval(position.x - 5, position.y - 5, 10, 10);
    }

    public void setClickTimeoutMillis(final long pClickTimeoutMillis) {
        clickTimeoutMillis = pClickTimeoutMillis;
    }

    public long getMultipleClickTimeoutMillis() {
        return multipleClickTimeoutMillis;
    }

    public void setMultipleClickTimeoutMillis(final long pMultipleClickTimeoutMillis) {
        multipleClickTimeoutMillis = pMultipleClickTimeoutMillis;
    }

    /**
     * this should be called in the event dispatch thread.
     *
     * @param tPoint
     */
    public void setTouchPosition(Point tPoint) {
        if (tPoint != null) {
            position.x = tPoint.x;
            position.y = tPoint.y;
        }

        final long tNow = System.currentTimeMillis();

        // do we need to recalc
        if (tPoint != lastTouchPoint) {

            // component under the mouse
            // TODO MENU support -> findComponentAt maybe search in layered pane
            Component tComponent = findComponentAt(rootContainer, position.x, position.y, true);

            // send MouseReleased, if a press event was active, but the mouse is now on another component
            if (tPoint == null && lastMousePressedEvent != null && tComponent != lastMousePressedEvent.getComponent()) {
                invokeMouseReleased(lastMousePressedEvent.getComponent(), lastMousePressedEvent);
                lastMousePressedEvent = null;
            }

            if (tComponent != null) {
                // a simple algorithm to simulate mouse press and release events

                // convert mouse coords into button coords
                Point tComponentPoint = SwingUtilities.convertPoint(rootContainer, position, tComponent);

                // create a drag event
                if (lastTouchPoint != null && tPoint != null) {
                    MouseEvent tDragEvent = new MouseEvent(tComponent, MouseEvent.MOUSE_DRAGGED, tNow, InputEvent.BUTTON1_MASK, tComponentPoint.x, tComponentPoint.y, clickCount, false);
                    invokeMouseDragged(tComponent, tDragEvent);
                }

                // touch
                if (lastTouchPoint == null && tPoint != null) {
                    // calculate click count
                    // a click is added, if the last mouse click is inside a timeframe on a touch down (Mouse pressed)
                    if (lastMouseClickEvent != null &&
                        lastMouseClickEvent.getComponent() == tComponent &&
                        (tNow - lastMouseClickEvent.getWhen() < multipleClickTimeoutMillis)) {
                        clickCount = lastMouseClickEvent.getClickCount() + 1;
                    } else {
                        clickCount = 1;
                    }

                    MouseEvent tMousePressedEvent = new MouseEvent(tComponent, MouseEvent.MOUSE_PRESSED, tNow,
                            InputEvent.BUTTON1_MASK, tComponentPoint.x, tComponentPoint.y, clickCount, false);
                    invokeMousePressed(tComponent, tMousePressedEvent);

                    lastMousePressedEvent = tMousePressedEvent;
                    lastMouseReleasedEvent = null;
                }

                //  touch release
                if (lastTouchPoint != null && tPoint == null) {
                    final MouseEvent tMouseReleasedEvent = new MouseEvent(tComponent, MouseEvent.MOUSE_RELEASED, tNow,
                            InputEvent.BUTTON1_MASK, tComponentPoint.x, tComponentPoint.y, clickCount, false);
                    invokeMouseReleased(tComponent, tMouseReleasedEvent);

                    // check for click event
                    // a click event is created, when a pressed event is inside a timeframe of press event and the press event was on the same component
                    if (lastMousePressedEvent != null
                        && lastMousePressedEvent.getComponent() == tComponent
                        && tNow - lastMousePressedEvent.getWhen() < clickTimeoutMillis) {

                        MouseEvent tMouseClickedEvent = new MouseEvent(tComponent, MouseEvent.MOUSE_CLICKED, tNow,
                                InputEvent.BUTTON1_MASK, tComponentPoint.x, tComponentPoint.y, clickCount, false);
                        invokeMouseClicked(tComponent, tMouseClickedEvent);
                        lastMouseClickEvent = tMouseClickedEvent;
                    }

                    lastMousePressedEvent = null;
                    lastMouseReleasedEvent = tMouseReleasedEvent;
                }
            }


        }

        lastTouchPoint = tPoint;
    }

    private void invokeMouseReleased(final Component pComponent, final MouseEvent pMouseEvent) {
        for (MouseListener tMouseListener : pComponent.getMouseListeners()) {
            tMouseListener.mouseReleased(pMouseEvent);
        }
    }

    private void invokeMousePressed(final Component pComponent, final MouseEvent pMouseEvent) {
        for (MouseListener tMouseListener : pComponent.getMouseListeners()) {
            tMouseListener.mousePressed(pMouseEvent);
        }
    }

    private void invokeMouseClicked(final Component pComponent, final MouseEvent pMouseEvent) {
        for (MouseListener tMouseListener : pComponent.getMouseListeners()) {
            tMouseListener.mouseClicked(pMouseEvent);
        }
    }

    private void invokeMouseDragged(final Component pComponent, final MouseEvent pMouseEvent) {
        for (MouseMotionListener tMouseListener : pComponent.getMouseMotionListeners()) {
            tMouseListener.mouseDragged(pMouseEvent);
        }
    }

    private static Component findComponentAt(Container pContainer, int x, int y, boolean pIgnoreEnabled) {
        if (!(pContainer.contains(x, y))) {
            return null;
        }

        if (pContainer instanceof RootPaneContainer) {
            return findComponentAt(((RootPaneContainer) pContainer).getContentPane(), x, y, pIgnoreEnabled);
        }


        synchronized (pContainer.getTreeLock()) {
            for (int i = 0; i < pContainer.getComponentCount(); i++) {
                Component tComponent = pContainer.getComponent(i);
                if (tComponent != null && tComponent.isVisible()) {
                    if (tComponent instanceof Container) {
                        tComponent = findComponentAt((Container) tComponent, x - tComponent.getX(), y - tComponent.getY(), pIgnoreEnabled);
                    } else {
                        tComponent = tComponent.getComponentAt(x - tComponent.getX(), y - tComponent.getY());
                    }
                    if (tComponent != null && (pIgnoreEnabled || tComponent.isEnabled())) {
                        return tComponent;
                    }
                }
            }
        }
        return pContainer;
    }


    public Point getPosition() {
        return position;
    }

}
