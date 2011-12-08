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

package swudi.LUIseV3;

import com.ftdi.FTD2XXException;
import com.ftdi.FTDevice;
import swudi.device.TransferStatistics;
import swudi.swing.SwUDiWindow;
import swudi.swing.TestPanel;
import swudi.swing.plaf.BlackAndWhiteTheme;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created: 03.12.11   by: Armin Haaf
 * <p/>
 *
 *
 * @author Armin Haaf
 */
public class LUIseV3Test {
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(new MetalLookAndFeel());
        MetalLookAndFeel.setCurrentTheme(new BlackAndWhiteTheme());

        JFrame tFrame = new JFrame("LUIseV3Test");
        final JLabel tInfoLabel = new JLabel();
        final JButton tButton = new JButton("Exit");
        tButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                System.exit(0);
            }
        });
        tFrame.add(tInfoLabel);
        tFrame.add(tButton, BorderLayout.SOUTH);

        final List<FTDevice> tDevices = FTDevice.getDevices();
        if (tDevices.size() == 0) {
            tButton.setText("no device found, press to exit");
        } else {
            tButton.setText("found " + tDevices.size() + " device(s), press to exit");
        }

        StringBuilder tStringBuilder = new StringBuilder("<html><ol>");
        for (FTDevice tDevice : tDevices) {
            if ("LCD-USB-Interface V3".equals(tDevice.getDevDescription())) {
                tStringBuilder.append("<li>LUIseV3 " + tDevice.getDevSerialNumber());
                try {
                    startDevice(tDevice);
                } catch (Exception ex) {
                    tStringBuilder.append(" but cannot activate it: " + ex.getMessage());
                    ex.printStackTrace();
                }
                tStringBuilder.append("</li>");
            } else {
                tStringBuilder.append("<li>unknown device " + tDevice.getDevSerialNumber() + "</li>");
            }
            tStringBuilder.append("</ol>");
        }
        tInfoLabel.setText(tStringBuilder.toString());

        tFrame.pack();
        tFrame.setVisible(true);
    }

    private static void startDevice(final FTDevice tDevice) throws FTD2XXException {
        LUIseV3USBDisplay tDisplay = new LUIseV3USBDisplay(tDevice);
        final TransferStatistics tTransferStatistics = tDisplay.getTransferStatistics();

        // both have problems with menu
        final SwUDiWindow tSwUDiWindow = new SwUDiWindow(tDisplay);
        tSwUDiWindow.setLocation(-10000, -10000);

        JPanel tPanel = new JPanel(new GridLayout(0, 2));

        final JLabel tMouseLabel = new JLabel("mouse");
        final JLabel tPaintLabel = new JLabel("paint");

        final JSlider tMouseRefreshRateSlider = new JSlider(1, 100, tSwUDiWindow.getMouseRefreshRate());
        tMouseRefreshRateSlider.setPaintLabels(true);
        tMouseRefreshRateSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                tMouseLabel.setText("mouse " + tMouseRefreshRateSlider.getValue());
                tSwUDiWindow.setMouseRefreshRate(tMouseRefreshRateSlider.getValue());
            }
        });
        final JSlider tPaintRateSlider = new JSlider(1, 100, tSwUDiWindow.getPaintRate());
        tPaintRateSlider.setPaintLabels(true);
        tPaintRateSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                tPaintLabel.setText("paint " + tPaintRateSlider.getValue());
                tSwUDiWindow.setPaintRate(tPaintRateSlider.getValue());
            }
        });

        tPanel.add(tMouseLabel);
        tPanel.add(tMouseRefreshRateSlider);
        tPanel.add(tPaintLabel);
        tPanel.add(tPaintRateSlider);

        TestPanel tTestPanel = new TestPanel();

        tTestPanel.addTab("0", tPanel);

        tSwUDiWindow.add(tTestPanel);

        final JLabel tInfoLabel = new JLabel();
        tInfoLabel.setFont(tInfoLabel.getFont().deriveFont(9));
        Timer tInfoTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                tInfoLabel.setText("recv:" + tTransferStatistics.getOverallReceived() / 1024
                                   + " rr:" + tTransferStatistics.getReceivedBytesPerSeconds(1000)
                                   + " rc:" + tTransferStatistics.getReceivesPerSeconds(1000)
                                   + " sent:" + tTransferStatistics.getOverallSent() / 1024
                                   + " sr:" + tTransferStatistics.getSentBytesPerSecond(1000)
                                   + " sc:" + tTransferStatistics.getSentsPerSecond(1000)
                                  );
                tInfoLabel.revalidate();
            }
        });
        tInfoTimer.start();
        tSwUDiWindow.add(tInfoLabel, BorderLayout.SOUTH);

        tSwUDiWindow.setVisible(true);
    }
}
