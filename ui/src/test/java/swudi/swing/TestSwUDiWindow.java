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

package swudi.swing;

import swudi.device.BufferedUSBDisplay;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

/**
 * <!--
 * Created: 04.12.11   by: Armin Haaf
 * <p/>
 *
 * <p/>
 * This is as trial to use the swing rendering infrastrukture to transfer the rootContainer content to the display -> that mean the repaint and layouting
 * the problem is
 *
 * @author Armin Haaf
 */
public class TestSwUDiWindow {

    public static void main(String[] args) {


            JFrame tBWFrame = buildTestFrame(new BufferedImage(320, 240, BufferedImage.TYPE_BYTE_BINARY));
            tBWFrame.setLocation(0, 0);


            JFrame tColorFrame = buildTestFrame(new BufferedImage(640, 480, BufferedImage.TYPE_4BYTE_ABGR));
            tColorFrame.setLocation(320, 0);
    }

    private static JFrame buildTestFrame(BufferedImage pBufferedImage) {

        JFrame tFrame = new JFrame("Test Buffered Device");
        tFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final BufferedUSBDisplay tBufferedUSBDisplay = new BufferedUSBDisplay(pBufferedImage);

        SwUDiWindow tSwUDi = new SwUDiWindow(tBufferedUSBDisplay);

        tSwUDi.add(new TestPanel());
        // this is needed, because the rootContainer is not layouted otherwise
        tSwUDi.setVisible(true);

        tFrame.add(tBufferedUSBDisplay);
        tFrame.add(new JButton("test"), BorderLayout.SOUTH);
        tFrame.pack();
        tFrame.setVisible(true);
        return tFrame;
    }

}
