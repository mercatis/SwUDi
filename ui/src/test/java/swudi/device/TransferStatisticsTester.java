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

import javax.swing.JFrame;
import javax.swing.JSlider;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <!--
 * Created: 05.12.11   by: Armin Haaf
 * <p/>
 *
 *
 * @author Armin Haaf
 */
public class TransferStatisticsTester {

    public static void main(String[] args) throws InterruptedException {
        final TransferStatistics tTransferStatistics = new TransferStatistics(10000, 1000);


        JFrame tFrame = new JFrame();
        final JSlider tSlider = new JSlider(0, 1000);
        tFrame.add(tSlider);


        Timer tTimer = new Timer();
        tTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (TransferStatistics.StatisticsEntry tSentStatistic : tTransferStatistics.getSentStatistics()) {
                    System.out.println(((tSentStatistic.getMillis() % 10000) / 1000) + " " + tSentStatistic.getAddCount() + ":" + tSentStatistic.getByteCount());
                }
                System.out.println(tTransferStatistics.getSentsPerSecond(5000));
                System.out.println(tTransferStatistics.getSentsPerSecond(5000));
            }
        }, 1000, 1000);

        tFrame.pack();
        tFrame.setVisible(true);

        while (true) {
            Thread.sleep(tSlider.getValue());
            tTransferStatistics.addBytesSent(2);
        }
    }
}
