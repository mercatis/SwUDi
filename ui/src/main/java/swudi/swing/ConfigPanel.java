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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import swudi.device.USBDisplay;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created: 02.12.11   by: Armin Haaf
 * <p/>
 * <p/>
 * <p/>
 * a panel to configure the usb display
 *
 * @author Armin Haaf
 */
public class ConfigPanel extends JPanel {

    private JSlider contrastSlider;
    private JCheckBox inversCheckbox;
    private JButton calibrateButton;
    private JButton resetButton;
    private JSlider backlightSlider;

    private USBDisplay usbDisplay;

    public ConfigPanel() {
        this(null);
    }

    public ConfigPanel(final USBDisplay pUSBDisplay) {
        initLayout();
        initUSBDisplay(pUSBDisplay);
        contrastSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                getUSBDisplay().setContrast(contrastSlider.getValue());
            }
        });

        inversCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                getUSBDisplay().setInverted(inversCheckbox.isSelected());
            }
        });

        backlightSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                getUSBDisplay().setBacklight(backlightSlider.getValue());
            }
        });

        calibrateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                getUSBDisplay().calibrateTouch();
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                getUSBDisplay().reset();
                getUSBDisplay().setInverted(inversCheckbox.isSelected());
                getUSBDisplay().clearScreen();
            }
        });

        initDefaults();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(final ComponentEvent e) {
                initDefaults();
            }
        });
    }

    private void initDefaults() {
        USBDisplay tUSBDisplay = getUSBDisplay();
        if (tUSBDisplay != null) {
            contrastSlider.setValue(getUSBDisplay().getContrast());
            backlightSlider.setValue(getUSBDisplay().getBacklight());
        }
    }

    private void initUSBDisplay(final USBDisplay pUSBDisplay) {
        usbDisplay = pUSBDisplay;
        if (usbDisplay != null) {
            contrastSlider.setValue(getUSBDisplay().getContrast());
            inversCheckbox.setSelected(getUSBDisplay().isInverted());
        }
    }

    public USBDisplay getUSBDisplay() {
        if (usbDisplay == null) {
            initUSBDisplay(SwUDiUtils.getDevice(this));
        }
        return usbDisplay;
    }

    // TODO translations
    private void initLayout() {
        setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,fill:d:grow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));

        final JLabel label1 = new JLabel();
        label1.setText("Contrast");
        CellConstraints cc = new CellConstraints();
        add(label1, cc.xy(1, 1));
        contrastSlider = new JSlider();
        contrastSlider.setPaintLabels(false);
        contrastSlider.setPaintTicks(true);
        add(contrastSlider, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label2 = new JLabel();
        label2.setText("Invers");
        add(label2, cc.xy(1, 5));
        inversCheckbox = new JCheckBox();
        inversCheckbox.setHideActionText(false);
        inversCheckbox.setSelected(true);
        inversCheckbox.setText("");
        add(inversCheckbox, cc.xy(3, 5));
        calibrateButton = new JButton();
        calibrateButton.setHorizontalAlignment(2);
        calibrateButton.setHorizontalTextPosition(2);
        calibrateButton.setText("Calibrate");
        add(calibrateButton, cc.xy(3, 7, CellConstraints.DEFAULT, CellConstraints.FILL));
        resetButton = new JButton();
        resetButton.setHorizontalAlignment(2);
        resetButton.setHorizontalTextPosition(2);
        resetButton.setText("Reset");
        add(resetButton, cc.xy(3, 9, CellConstraints.DEFAULT, CellConstraints.FILL));
        final JLabel label3 = new JLabel();
        label3.setText("Backlight");
        add(label3, cc.xy(1, 3));
        backlightSlider = new JSlider();
        backlightSlider.setPaintLabels(false);
        backlightSlider.setPaintTicks(true);
        add(backlightSlider, cc.xy(3, 3));

    }
}
