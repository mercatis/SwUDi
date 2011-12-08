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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <!--
 * Created: 04.12.11   by: Armin Haaf
 * <p/>
 *
 *
 * A TestPanel showing some components
 *
 * @author Armin Haaf
 */
public class TestPanel extends JTabbedPane {

    public TestPanel() {
        this.addTab("1", createTreeDemo());
        this.addTab("2", createTableDemo());
        this.addTab("3", createColorChooserDemo());
        this.addTab("4", createCardLayoutDemo());
        this.addTab("5", createSliderDemo());
        this.addTab("6", createButtonDemo());
        //addTab("7", createMenuTest());
        addTab("7", createAnimatedGifPanel());
        addTab("8", createNumPadTest());
        this.addTab("9", new ConfigPanel());
    }

    private Component createAnimatedGifPanel() {
        return new JLabel(new ImageIcon(getClass().getClassLoader().getResource("baelle-1615.gif")));
    }

    private JPanel createNumPadTest() {
        JTextField tTextField = new JTextField();
        NumPad tNumPad = new NumPad();
        tNumPad.setToFill(tTextField);
        JPanel tPanel = new JPanel(new BorderLayout());
        tPanel.add(tTextField, BorderLayout.NORTH);
        tPanel.add(tNumPad);
        return tPanel;
    }

    private JPanel createMenuTest() {
        JMenuBar tMenuBar = new JMenuBar();
        JMenu tMenu = new JMenu("m1");
        JMenu tMenu11 = new JMenu("m11");
        tMenu11.add("m111");
        tMenu.add(tMenu11);
        tMenu.add("m12");
        tMenu.add("m13");
        tMenu.add("m14");
        tMenuBar.add(tMenu);
        JMenu tMenu2 = new JMenu("m2");
        tMenu2.add("m21");
        tMenu2.add("m22");
        tMenu2.add("m23");
        tMenuBar.add(tMenu2);

        JPanel tPanel = new JPanel(new BorderLayout());
        tPanel.add(tMenuBar, BorderLayout.NORTH);

        return tPanel;
    }

    private JPanel createButtonDemo() {
        JPanel tPanel = new JPanel(new GridLayout(0, 1));
        tPanel.add(new JCheckBox("checkbox"));
        tPanel.add(new JToggleButton("toggleButton"));
        tPanel.add(new JRadioButton("radioButton"));
        tPanel.add(new JButton("button"));

        return tPanel;
    }

    private JPanel createCardLayoutDemo() {
        final CardLayout tCardLayout = new CardLayout();
        final JPanel tCardLayoutTest = new JPanel(tCardLayout);
        JButton tCard1Button = new JButton("1");
        tCard1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                tCardLayout.show(tCardLayoutTest, "2");
            }
        });
        JButton tCard2Button = new JButton("2");
        tCard2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                tCardLayout.show(tCardLayoutTest, "1");
            }
        });
        tCardLayoutTest.add(tCard1Button, "1");
        tCardLayoutTest.add(tCard2Button, "2");
        return tCardLayoutTest;
    }

    private JColorChooser createColorChooserDemo() {
        return new JColorChooser();
    }

    private JScrollPane createTableDemo() {
        final DefaultTableModel tDm = new DefaultTableModel(4, 4);
        for (int column = 0; column < tDm.getColumnCount(); column++) {
            for (int row = 0; row < tDm.getRowCount(); row++) {
                tDm.setValueAt(row + ":" + column, row,column);
            }
        }
        return new JScrollPane(new JTable(tDm));
    }

    private JScrollPane createTreeDemo() {
        return new JScrollPane(new JTree());
    }

    private JComponent createSliderDemo() {
        JPanel tSliderPanel = new JPanel();
        final JSlider tSlider = new JSlider(1, 100, 50);
        final JLabel tValueLabel = new JLabel("" + tSlider.getValue());
        tSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                tValueLabel.setText("" + tSlider.getValue());
            }
        });
        tSliderPanel.add(tSlider);
        tSliderPanel.add(tValueLabel);

        return tSliderPanel;
    }

}
