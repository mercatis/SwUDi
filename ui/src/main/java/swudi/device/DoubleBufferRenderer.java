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

import swudi.swing.DisplayRenderer;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <!--
 * Created: 04.12.11   by: Armin Haaf
 * <p/>
 *
 *
 * @author Armin Haaf
 */
public class DoubleBufferRenderer implements DisplayRenderer {

    /**
     * the image swing is painting into
     */
    private final BufferedImage offScreenImage;

    /**
     * the image to paint to the display
     */
    private final BufferedImage displayImage;

    private final AtomicBoolean rendering = new AtomicBoolean(false);

    private final USBDisplay usbDisplay;

    private final ExecutorService rendererThread = Executors.newSingleThreadExecutor();

    public DoubleBufferRenderer(final USBDisplay pUsbDisplay) {
        offScreenImage = pUsbDisplay.createOffScreenBuffer();
        usbDisplay = pUsbDisplay;
        displayImage = new BufferedImage(offScreenImage.getColorModel(), offScreenImage.getRaster().createCompatibleWritableRaster(), offScreenImage.getColorModel().isAlphaPremultiplied(), null);
    }

    @Override
    public BufferedImage getOffScreenImage() {
        return offScreenImage;
    }

    // check if we need to copy data to display
    private boolean isBufferDifferent(DataBuffer pBuffer1, DataBuffer pBuffer2) {
        long tStart = System.currentTimeMillis();

        try {
            for (int i = pBuffer1.getSize() - 1; i >= 0; i--) {
                if (pBuffer1.getElem(i) != pBuffer2.getElem(i)) {
                    return true;
                }
            }
            return false;
        } finally {
            System.out.println("calc different " + (System.currentTimeMillis() - tStart));
        }
    }

    /**
     * dirty rectangle detection -> we did not support floating point buffer types!
     */
    private Rectangle calcDiffRectangle(BufferedImage pImage1, BufferedImage pImage2) {
        long tStart = System.currentTimeMillis();
        int tFirstChangedY = Integer.MAX_VALUE;
        int tLastChangedY = 0;
        int tFirstChangedX = Integer.MAX_VALUE;
        int tLastChangedX = 0;

        int[] iData1 = null;
        int[] iData2 = null;

        // performance could be better (5ms for 320x200),better algorithm maybe?
        for (int y = pImage1.getHeight() - 1; y >= 0; y--) {
            iData1 = pImage1.getRaster().getPixels(0, y, pImage1.getWidth(), 1, iData1);
            iData2 = pImage2.getRaster().getPixels(0, y, pImage1.getWidth(), 1, iData2);
            for (int x = pImage1.getWidth() - 1; x >= 0; x--) {
                if (iData1[x] != iData2[x]) {
                    tFirstChangedX = Math.min(x, tFirstChangedX);
                    tLastChangedX = Math.max(x, tLastChangedX);
                    tFirstChangedY = Math.min(y, tFirstChangedY);
                    tLastChangedY = Math.max(y, tLastChangedY);
                }
            }
        }

        System.out.println("calc diff rectangle " + (System.currentTimeMillis() - tStart) +  "  " + new Rectangle(tFirstChangedX, tFirstChangedY, tLastChangedX - tFirstChangedX + 1, tLastChangedY - tFirstChangedY + 1));
        if (tFirstChangedX != Integer.MAX_VALUE || tFirstChangedY != Integer.MAX_VALUE || tLastChangedY != 0 || tLastChangedX != 0) {
            return new Rectangle(tFirstChangedX, tFirstChangedY, tLastChangedX - tFirstChangedX + 1, tLastChangedY - tFirstChangedY + 1);
        } else {
            return null;
        }
    }

    @Override
    public void render(final List<Rectangle> pDirtyRegions) {
//        renderAllIfDirty(pDirtyRegions);
        renderDirtyRegion(pDirtyRegions);
    }

    public void renderAllIfDirty(final List<Rectangle> pDirtyRegions) {
        // TODO only refresh dirty parts -> problem maybe to much USB transactions
        try {
            // check if render thread has done, if not we drop a frame
            if (!rendering.getAndSet(true)) {

                // displayImage is used for checking, if buffer is changed and for painting the buffer outside the event thread

                // detect, if buffer has changed
                if (isBufferDifferent(offScreenImage.getRaster().getDataBuffer(), displayImage.getRaster().getDataBuffer())) {

                    // copy offscreen image to renderImage
                    displayImage.getRaster().setRect(offScreenImage.getRaster());

                    // copy data to display outsize of event thread
                    rendererThread.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                usbDisplay.paint(displayImage, 0, 0, displayImage.getWidth(), displayImage.getHeight());
                            } finally {
                                rendering.set(false);
                            }
                        }
                    });
                } else {
                    rendering.set(false);
                }
            } else {
                System.out.println("drop frame");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void renderDirtyRegion(final List<Rectangle> pDirtyRegions) {
        try {
            // check if render thread has done, if not we drop a frame
            if (!rendering.getAndSet(true)) {

                final Rectangle tDirtyRegion = calcDiffRectangle(offScreenImage, displayImage);
                if (tDirtyRegion != null) {

//                    System.out.println("dirty region " + tDirtyRegion);

                    // tddo copy offscreen image to renderImage -> dirty region would be enough
//                    displayImage.getRaster().setRect(tDirtyRegion.x, tDirtyRegion.y, tImageToRender.getRaster());
                    displayImage.getRaster().setRect(offScreenImage.getRaster());

                    // copy data to display outsize of event thread
                    rendererThread.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                usbDisplay.paint(offScreenImage, tDirtyRegion.x, tDirtyRegion.y, tDirtyRegion.width, tDirtyRegion.height);
                            } finally {
                                rendering.set(false);
                            }
                        }
                    });
                } else {
                    rendering.set(false);
                }
            } else {
                // todo write some statistics
                System.out.println("drop frame");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
