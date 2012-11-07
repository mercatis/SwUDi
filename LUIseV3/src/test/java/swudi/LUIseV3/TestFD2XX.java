package swudi.LUIseV3;

import com.ftdi.FTD2XXException;
import com.ftdi.FTDevice;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created: 26.01.12   by: Armin Haaf
 *
 * @author Armin Haaf
 */
public class TestFD2XX {
    protected static final Charset US_ASCII = Charset.forName("US-ASCII");

    final JPanel mainPanel = new JPanel();
    final JTextField inputTextField = new JTextField();
    final JTextArea output = new JTextArea();
    final JButton sendButton = new JButton("Send");
    final DefaultListModel sentCommandsModel = new DefaultListModel();
    final JList sentCommands = new JList(sentCommandsModel);
    final FTDevice ftDevice;

    public TestFD2XX(FTDevice pFTDevice) throws FTD2XXException {
        ftDevice = pFTDevice;
        ftDevice.open();

        ftDevice.write("SetMinAnswerLength (20);".getBytes(US_ASCII));

        sentCommands.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sentCommands.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if ( !e.getValueIsAdjusting() && !sentCommands.isSelectionEmpty() ) {
                    final String tSelectedValue = (String) sentCommands.getSelectedValue();
                    sentCommands.clearSelection();
                    sendCommand(tSelectedValue);
                }
            }
        });

        final ActionListener tSendActionListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String tCommand = inputTextField.getText();

                sendCommand(tCommand);
            }
        };
        sendButton.addActionListener(tSendActionListener);

        inputTextField.setColumns(40);
        inputTextField.addActionListener(tSendActionListener);

        output.setColumns(40);
        output.setRows(10);

        mainPanel.add(new JLabel(ftDevice.getDevSerialNumber()));
        mainPanel.add(inputTextField);
        mainPanel.add(sendButton);
        mainPanel.add(new JScrollPane(output));
        mainPanel.add(new JScrollPane(sentCommands));

        DeviceReader tDeviceReader = new DeviceReader();
        tDeviceReader.start();
    }

    private void sendCommand(String pCommand) {
        try {
            ftDevice.write(pCommand.getBytes(US_ASCII));
            sentCommandsModel.removeElement(pCommand);
            sentCommandsModel.insertElementAt(pCommand, 0);
        } catch (FTD2XXException e1) {
            e1.printStackTrace();
        }
    }

    public JPanel getComponent() {
        return mainPanel;
    }

    class DeviceReader extends Thread {

        byte[] buffer = new byte[20];

        DeviceReader() {
            super("Reader Thread of " + ftDevice.getDevSerialNumber());
        }

        private String readCommand() throws FTD2XXException {
            // this is strange
            // TODO there is a strange windows behaviour. It seems not possible to read and write at the same time!!
            // so on windows use PollingLUIseV3USBDisplay
            // todo read into a buffer would be good, however, we do not get a result, because ftdevice blocks (it waits for the buffer to fill)

            while (ftDevice.read(buffer) == 0);
            int tFirstSemikolon = 0;
            for (int i = 0; i < buffer.length; i++) {
                if (buffer[i] == ';') {
                    tFirstSemikolon = i;
                    break;
                }

            }
            return new String(buffer, 0, tFirstSemikolon, US_ASCII);
        }


        @Override
        public void run() {
            while (true) {
                try {
                    String tCommand = readCommand();
                    // check if it is a autotouch command
                    if (tCommand != null) {
                        output.insert(tCommand + "\n", 0);
                    }
                } catch (FTD2XXException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    public static void main(String[] args) throws Exception {
        JFrame tFrame = new JFrame();
        tFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        final List<FTDevice> tDevices = FTDevice.getDevices();
        for (FTDevice tDevice : tDevices) {
            TestFD2XX tTestFD2XX = new TestFD2XX(tDevice);
            tFrame.add(tTestFD2XX.getComponent());
        }


        tFrame.setSize(800, 600);
        tFrame.setVisible(true);


    }
}
