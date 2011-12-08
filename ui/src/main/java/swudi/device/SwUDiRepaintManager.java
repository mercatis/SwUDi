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

import swudi.swing.SwUDiWindow;
import swudi.swing.USBDeviceFrame;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Window;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <!--
 * Created: 07.12.11   by: Armin Haaf
 * <p/>
 *
 *
 * This Repaintmanager is used to track dirty SwUDiWindows -> we do not want to paint in a timer the whole screen.
 * This may collide with the swingx RepaintManager
 *
 * @author Armin Haaf
 */
public class SwUDiRepaintManager extends RepaintManager {

    private final Map<Component, Boolean> dirtySwUDiFrames = Collections.synchronizedMap(new HashMap<Component, Boolean>());

    @Override
    public void addDirtyRegion(final JComponent c, final int x, final int y, final int w, final int h) {
        addDirtySwUDiComponent(c);
        super.addDirtyRegion(c, x, y, w, h);
    }

    @Override
    public void addDirtyRegion(final Window window, final int x, final int y, final int w, final int h) {
        if (window instanceof SwUDiWindow) {
            dirtySwUDiFrames.put(window, Boolean.TRUE);
        }
        super.addDirtyRegion(window, x, y, w, h);
    }

    @Override
    public void markCompletelyDirty(final JComponent aComponent) {
        addDirtySwUDiComponent(aComponent);
        super.markCompletelyDirty(aComponent);
    }

    private void addDirtySwUDiComponent(final JComponent c) {
        final Component tRoot = SwingUtilities.getRoot(c);
        if (tRoot instanceof SwUDiWindow) {
            dirtySwUDiFrames.put(tRoot, Boolean.TRUE);
        }
    }

    public void resetDirtyFlag(final SwUDiWindow pSwUDiComponent) {
        dirtySwUDiFrames.remove(pSwUDiComponent);
    }

    public boolean isDirty(final SwUDiWindow pSwUDiWindow) {
        return dirtySwUDiFrames.containsKey(pSwUDiWindow);
    }
}

