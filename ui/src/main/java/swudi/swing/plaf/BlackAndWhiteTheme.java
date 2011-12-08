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

package swudi.swing.plaf;

import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 * Created: 02.12.11   by: Armin Haaf
 * <p/>
 *
 *
 * @author Armin Haaf
 */
public class BlackAndWhiteTheme extends DefaultMetalTheme {

    protected ColorUIResource getPrimary1() {
        return getBlack();
    }

    protected ColorUIResource getPrimary2() {
        return getWhite();
    }

    protected ColorUIResource getPrimary3() {
        return getWhite();
    }

    public ColorUIResource getPrimaryControlHighlight() {
        return getWhite();
    }

    protected ColorUIResource getSecondary2() {
        return getWhite();
    }

    protected ColorUIResource getSecondary3() {
        return getWhite();
    }

    public ColorUIResource getControlHighlight() {
        return getWhite();
    }

    public ColorUIResource getFocusColor() {
        return getBlack();
    }

    public ColorUIResource getTextHighlightColor() {
        return getBlack();
    }

    public ColorUIResource getHighlightedTextColor() {
        return getBlack();
    }

    public ColorUIResource getMenuSelectedBackground() {
        return getWhite();
    }

    public ColorUIResource getMenuSelectedForeground() {
        return getWhite();
    }

    public ColorUIResource getAcceleratorForeground() {
        return getBlack();
    }

    public ColorUIResource getAcceleratorSelectedForeground() {
        return getWhite();
    }

    public String getName() {
        return "BlackAndWhite";
    }

    public void addCustomEntriesToTable(UIDefaults table) {

        Border blackLineBorder = new BorderUIResource(new LineBorder(getBlack()));

        table.put("TextField.border", blackLineBorder);
        table.put("PasswordField.border", blackLineBorder);
        table.put("TextArea.border", blackLineBorder);
        table.put("Button.border", blackLineBorder);
    }
}
