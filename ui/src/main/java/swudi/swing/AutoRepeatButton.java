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

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <!--
 * Created: 02.12.11   by: Armin Haaf
 * <p/>
 *
 *
 * a button which fires actionevents as long mouse is pressed on it.
 *
 * @author Armin Haaf
 */
public class AutoRepeatButton extends JButton {

    private final Timer autoRepeatTimer = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(final ActionEvent e) {
            AutoRepeatButton.this.getModel().setPressed(true);
            AutoRepeatButton.this.getModel().setPressed(false);

            final int tDelay = Math.max(minDelay, autoRepeatTimer.getDelay() - decreaseDelayMillis);
            autoRepeatTimer.setDelay(tDelay);
        }
    });

    private int initialDelayMillis = 300;

    private int delayMillis = 250;

    private int decreaseDelayMillis = 7;

    private int minDelay = 10;

    {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                autoRepeatTimer.setDelay(delayMillis);
                autoRepeatTimer.setInitialDelay(initialDelayMillis);
                autoRepeatTimer.start();
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                autoRepeatTimer.stop();
            }
        });
    }

    public AutoRepeatButton() {
    }

    public AutoRepeatButton(final Icon icon) {
        super(icon);
    }

    public AutoRepeatButton(final String text) {
        super(text);
    }

    public AutoRepeatButton(final Action a) {
        super(a);
    }

    public AutoRepeatButton(final String text, final Icon icon) {
        super(text, icon);
    }

    public int getInitialDelayMillis() {
        return initialDelayMillis;
    }

    public void setInitialDelayMillis(final int pInitialDelayMillis) {
        initialDelayMillis = pInitialDelayMillis;
    }

    public int getDelayMillis() {
        return delayMillis;
    }

    public void setDelayMillis(final int pDelayMillis) {
        delayMillis = pDelayMillis;
    }

    public int getDecreaseDelayMillis() {
        return decreaseDelayMillis;
    }

    public void setDecreaseDelayMillis(final int pDecreaseDelayMillis) {
        decreaseDelayMillis = pDecreaseDelayMillis;
    }

    public int getMinDelay() {
        return minDelay;
    }

    public void setMinDelay(final int pMinDelay) {
        minDelay = pMinDelay;
    }
}