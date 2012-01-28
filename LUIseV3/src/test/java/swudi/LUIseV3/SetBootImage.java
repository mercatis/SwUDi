package swudi.LUIseV3;

import com.ftdi.FTD2XXException;
import com.ftdi.FTDevice;
import swudi.device.USBDisplay.State;
import swudi.swing.SwUDiWindow;
import swudi.swing.plaf.BlackAndWhiteTheme;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * <!--
 * Created: 16.01.12   by: Armin Haaf
 *
 * @author Armin Haaf
 */
public class SetBootImage {

    public static void main(String[] args) throws Exception {

        String tIconPath = null;
        if (args.length != 1) {
            JFileChooser tFileChooser = new JFileChooser();
            if ( tFileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION ) {
                tIconPath = tFileChooser.getSelectedFile().getPath();
            }
        } else {
            tIconPath = args[0];
        }

        if ( tIconPath==null ) {
            System.exit(0);
        }

        UIManager.setLookAndFeel(new MetalLookAndFeel());
        MetalLookAndFeel.setCurrentTheme(new BlackAndWhiteTheme());

        JFrame tFrame = new JFrame("LUIseV3Test");
        final JPanel tDevicePanel = new JPanel(new GridLayout(0, 1));
        final JButton tButton = new JButton("Exit");
        tButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                System.exit(0);
            }
        });
        tFrame.add(tDevicePanel);
        tFrame.add(tButton, BorderLayout.SOUTH);

        tFrame.setVisible(true);


        final List<FTDevice> tDevices = FTDevice.getDevices();
        if (tDevices.size() == 0) {
            tButton.setText("no device found, press to exit");
        } else {
            tButton.setText("found " + tDevices.size() + " device(s), press to exit");
        }

        for (FTDevice tDevice : tDevices) {
            if ("LCD-USB-Interface V3".equals(tDevice.getDevDescription())) {
                try {
                    Thread.sleep(100);
                    final JLabel tInfoLabel =   new JLabel("LUIseV3 " + tDevice.getDevSerialNumber());
                    tDevicePanel.add(tInfoLabel);
                    tFrame.pack();
                    startDevice(tDevice, tIconPath);
                    tInfoLabel.setText("LUIseV3 " + tDevice.getDevSerialNumber() + " bootimage changed");
                } catch (Exception ex) {
                    tDevicePanel.add(new JLabel("LUIseV3 " + tDevice.getDevSerialNumber() + " but cannot activate it: " + ex.getMessage()));
                    ex.printStackTrace();
                }
            } else {
                tDevicePanel.add(new JLabel("unknown device " + tDevice.getDevSerialNumber()));
            }
        }

    }

    private static AbstractLUIseV3USBDisplay startDevice(final FTDevice tDevice, final String pImagePath) throws FTD2XXException {
        final AbstractLUIseV3USBDisplay tDisplay = new PollingLUIseV3USBDisplay(tDevice);

        final SwUDiWindow tSwUDiWindow = new SwUDiWindow(tDisplay);

        tSwUDiWindow.add(new JLabel(new ImageIcon(pImagePath)));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        tDisplay.sendCommand("ConfigBootScreen (2);");
                        tDisplay.sendCommand("StoreAsBootScreen;");
                    }
                });
            }
        }).start();

        tDisplay.setBacklight(100);

        tSwUDiWindow.setVisible(true);

        return tDisplay;
    }

}
