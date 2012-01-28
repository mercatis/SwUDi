package swudi.LUIseV3;

import com.ftdi.FTD2XXException;
import com.ftdi.FTDevice;
import swudi.device.USBDisplayExceptionHandler;

import java.awt.Point;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created: 26.01.12   by: Armin Haaf
 *
 * This uses the autotouch feature of newer firmwares >= 3.0.3.2
 *
 * @author Armin Haaf
 */
public class AutoTouchLUIseV3USBDisplay extends AbstractLUIseV3USBDisplay {

    private static Pattern AUTOTOUCH_PATTERN = Pattern.compile("AutoTouch: ([0-9]),([0-9]+),([0-9]+)");

    private final BlockingQueue<String> commandBuffer = new ArrayBlockingQueue<String>(1);

    private final DeviceReader deviceReader;

    private int commandPollTimeout = 1000;

    public AutoTouchLUIseV3USBDisplay(final FTDevice pFTDevice) throws FTD2XXException {
        this(pFTDevice, null);
    }

    public AutoTouchLUIseV3USBDisplay(final FTDevice pFTDevice, final USBDisplayExceptionHandler<FTD2XXException> pExceptionHandler) throws FTD2XXException {
        super(pFTDevice, pExceptionHandler);


        deviceReader = new DeviceReader();
        deviceReader.start();

        sendCommand("AutoSendTouch (1);");
    }

    @Override
    protected Pattern getTouchReplyPattern() {
        return AUTOTOUCH_PATTERN;
    }

    protected String readReply() {
        try {
            return commandBuffer.poll(commandPollTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }


    class DeviceReader extends Thread {

        DeviceReader() {
            super("SwUDi Reader Thread of " + ftDevice.getDevSerialNumber());
        }

        private String readCommand() throws FTD2XXException {
            // this is strange
            // TODO there is a strange windows behaviour. It seems not possible to read and write at the same time!!
            // so on windows use PollingLUIseV3USBDisplay
            // todo read into a buffer would be good, however, we do not get a result, because ftdevice blocks (it waits for the buffer to fill)
            final StringBuilder tReadBuffer = new StringBuilder();

            int tReadByte;
            int tReceivedCount = 0;
            while ((tReadByte = ftDevice.read()) >= 0) {
                tReceivedCount++;
                if ((char) tReadByte == ';') {
                    break;
                }
                tReadBuffer.append((char) tReadByte);
            }
            transferStatistics.addBytesReceived(tReceivedCount);
            return tReadBuffer.length() > 0 ? tReadBuffer.toString() : null;
        }


        @Override
        public void run() {
            while (true) {
                try {
                    String tCommand = readCommand();
                    // check if it is a autotouch command
                    if (tCommand != null) {
                        if (tCommand.startsWith("AutoTouch:")) {
                            handleTouchEvent(tCommand);
                        } else {
                            commandBuffer.clear();
                            commandBuffer.offer(tCommand, 100, TimeUnit.MILLISECONDS);
                        }
                    }
                } catch (FTD2XXException ex) {
                    ex.printStackTrace();
                    justSleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    justSleep(1000);
                }
            }
        }

        private void justSleep(long pMillis) {
            try {
                Thread.sleep(pMillis);
            } catch (InterruptedException e) {
            }
        }

    }
}
