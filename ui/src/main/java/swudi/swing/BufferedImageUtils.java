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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * <!--
 * Created: 06.12.11   by: Armin Haaf
 * <p/>
 *
 *
 * @author Armin Haaf
 */
public class BufferedImageUtils {
    /**
     * Returns a bitmap String representation of a black and white BufferedImage
     * @param pBufferedImage
     * @param pX
     * @param pY
     * @param pWidth
     * @param pHeight
     * @return
     */
    public static String getBitmapString(final BufferedImage pBufferedImage, final int pX, final int pY, final int pWidth, final int pHeight) {
        // we support only byte aligned clips
        final int x = pX & ~7;
        final int y = pY & ~7;
        final int width = ((pX + pWidth + 7) & ~7) - x;
        final int height = ((pY + pHeight + 7) & ~7) - y;

        final DataBufferByte tImageBuffer = (DataBufferByte) pBufferedImage.getRaster().getDataBuffer();
        final byte[] tBuffer = tImageBuffer.getData();

        final StringBuffer tBitmapString = new StringBuffer();
        // paint bits
        for (int row = y; row < height; row++) {
            for (int col = x / 8; col < width / 8; col++) {
                tBitmapString.append(String.format("%1$8s0", Integer.toBinaryString(tBuffer[row * pBufferedImage.getWidth() / 8 + col])).replace(' ', '0'));
            }
            tBitmapString.append("\n");
        }
        return tBitmapString.toString();
    }
}
