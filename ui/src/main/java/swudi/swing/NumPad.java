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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created: 02.12.11   by: Armin Haaf
 * <p/>
 *
 *
 * @author Armin Haaf
 */
public class NumPad extends JPanel implements ActionListener {
    public static final String BACKSPACE = "BACKSPACE";
    public static final String ENTER = "ENTER";

    private final JButton buttonNum0 = createButton("0");
    private final JButton buttonNum1 = createButton("1");
    private final JButton buttonNum2 = createButton("2");
    private final JButton buttonNum3 = createButton("3");
    private final JButton buttonNum4 = createButton("4");
    private final JButton buttonNum5 = createButton("5");
    private final JButton buttonNum6 = createButton("6");
    private final JButton buttonNum7 = createButton("7");
    private final JButton buttonNum8 = createButton("8");
    private final JButton buttonNum9 = createButton("9");
    private final JButton backButton = createButton("\u21A4");
    private final JButton enterButton = createButton("\u21B2");

    private JTextComponent toFill;

    public NumPad() {
        setLayout(new FormLayout("fill:20px:grow," +
                                 "left:4dlu:noGrow," +
                                 "fill:20px:grow," +
                                 "left:4dlu:noGrow," +
                                 "fill:20px:grow",
                "center:20px:grow," +
                "top:3dlu:noGrow," +
                "center:20px:grow," +
                "top:3dlu:noGrow," +
                "center:20px:grow," +
                "top:3dlu:noGrow," +
                "center:20px:grow"));
        CellConstraints cc = new CellConstraints();
        add(buttonNum1, cc.xy(1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        add(buttonNum4, cc.xy(1, 3, CellConstraints.DEFAULT, CellConstraints.FILL));
        add(buttonNum7, cc.xy(1, 5, CellConstraints.DEFAULT, CellConstraints.FILL));
        add(buttonNum2, cc.xy(3, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        add(buttonNum3, cc.xy(5, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        add(buttonNum5, cc.xy(3, 3, CellConstraints.DEFAULT, CellConstraints.FILL));
        add(buttonNum6, cc.xy(5, 3, CellConstraints.DEFAULT, CellConstraints.FILL));
        add(buttonNum8, cc.xy(3, 5, CellConstraints.DEFAULT, CellConstraints.FILL));
        add(buttonNum9, cc.xy(5, 5, CellConstraints.DEFAULT, CellConstraints.FILL));
        backButton.setActionCommand(BACKSPACE);
        add(backButton, cc.xy(1, 7, CellConstraints.DEFAULT, CellConstraints.FILL));
        add(buttonNum0, cc.xy(3, 7, CellConstraints.DEFAULT, CellConstraints.FILL));
        enterButton.setActionCommand(ENTER);
        add(enterButton, cc.xy(5, 7, CellConstraints.DEFAULT, CellConstraints.FILL));
    }

    protected JButton createButton(String pText) {
        final JButton tButton = new AutoRepeatButton(pText);
        tButton.setFont(tButton.getFont().deriveFont(48f));
        tButton.addActionListener(this);
        return tButton;
    }

    public void addEnterClickedListener(ActionListener pListener) {
        enterButton.addActionListener(pListener);
    }

    public void setToFill(final JTextComponent pToFill) {
        toFill = pToFill;
    }

    public void actionPerformed(ActionEvent pEvent) {
        if (toFill != null) {
            if (pEvent.getSource() != enterButton) {
                if (pEvent.getSource() == backButton) {
                    if (toFill.getDocument().getLength() > 0) {
                        try {
                            toFill.getDocument().remove(toFill.getDocument().getLength() - 1, 1);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        toFill.getDocument().insertString(toFill.getDocument().getLength(), pEvent.getActionCommand(), null);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}

