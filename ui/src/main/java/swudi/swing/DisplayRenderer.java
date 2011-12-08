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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * <!--
 * Created: 04.12.11   by: Armin Haaf
 * <p/>
 *
 *
 * @author Armin Haaf
 */
public interface DisplayRenderer {

    /**
     * this method is called from event thread, after all components are painted.
     *
     * @param pDirtyRegions is currently unused
     */
    void render(List<Rectangle> pDirtyRegions);

    /**
     * the image swing components should paint into
     * @return
     */
    BufferedImage getOffScreenImage();
}
