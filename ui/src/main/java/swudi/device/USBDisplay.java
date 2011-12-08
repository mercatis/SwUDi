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

import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * Created: 02.12.11   by: Armin Haaf
 * <p/>
 *
 *
 * @author Armin Haaf
 */
public interface USBDisplay {

    public BufferedImage createOffScreenBuffer();

    public void clearScreen();

    public void setDisplayOn(boolean pOn);

    public void setContrast(int pContrast);

    public int getContrast();

    public void setInverted(boolean pInverted);

    public boolean isInverted();

    public void calibrateTouch();

    public Point getTouch();

    /**
     * a synchronized access is guaranteed
     */
    public void paint(BufferedImage pBufferedImage, int x, int y, final int pWidth, final int pHeight);

    public void close();

    public void setBacklight(final boolean pOn);

    public void reset();

}
