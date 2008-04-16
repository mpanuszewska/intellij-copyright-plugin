package com.maddyhome.idea.copyright.ui;

/*
 * Copyright - Copyright notice updater for IDEA
 * Copyright (C) 2004-2005 Rick Maddy. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.SupportCode;
import com.maddyhome.idea.copyright.options.TemplateOptions;
import com.maddyhome.idea.copyright.util.FileTypeUtil;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;

public class TemplateOptionsPanel extends JPanel
{
    public TemplateOptionsPanel()
    {
        setLayout(new BorderLayout());
        add(mainComponent, BorderLayout.CENTER);

        ButtonGroup group = new ButtonGroup();
        group.add(rbBlockComment);
        group.add(rbLineComment);

        txtLengthBefore.setRange(TemplateOptions.MIN_SEPARATOR_LENGTH, TemplateOptions.MAX_SEPARATOR_LENGTH);
        txtLengthBefore.setValue(80);
        txtLengthAfter.setRange(TemplateOptions.MIN_SEPARATOR_LENGTH, TemplateOptions.MAX_SEPARATOR_LENGTH);
        txtLengthAfter.setValue(80);

        rbBlockComment.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                cbPrefixLines.setEnabled(rbBlockComment.isSelected());
                fireChangeEvent();
            }
        });

        rbLineComment.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                cbPrefixLines.setEnabled(rbBlockComment.isSelected());
                fireChangeEvent();
            }
        });

        cbPrefixLines.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                fireChangeEvent();
            }
        });

        cbSeparatorBefore.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                lblLengthBefore.setEnabled(cbSeparatorBefore.isSelected());
                txtLengthBefore.setEnabled(cbSeparatorBefore.isSelected());
                if (cbSeparatorBefore.isSelected())
                {
                    txtLengthBefore.requestFocus();
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            txtLengthBefore.selectAll();
                        }
                    });
                }
                updateBox();
                fireChangeEvent();
            }
        });

        cbSeparatorAfter.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                lblLengthAfter.setEnabled(cbSeparatorAfter.isSelected());
                txtLengthAfter.setEnabled(cbSeparatorAfter.isSelected());
                if (cbSeparatorAfter.isSelected())
                {
                    txtLengthAfter.requestFocus();
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            txtLengthAfter.selectAll();
                        }
                    });
                }
                updateBox();
                fireChangeEvent();
            }
        });

        txtLengthBefore.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent)
            {
                if ("value".equals(propertyChangeEvent.getPropertyName()))
                {
                    updateBox();
                    fireChangeEvent();
                }
            }
        });

        txtLengthAfter.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent)
            {
                if ("value".equals(propertyChangeEvent.getPropertyName()))
                {
                    updateBox();
                    fireChangeEvent();
                }
            }
        });

        txtFiller.getDocument().addDocumentListener(new DocumentListener()
        {
            public void insertUpdate(DocumentEvent e)
            {
                fireChangeEvent();
            }

            public void removeUpdate(DocumentEvent e)
            {
                fireChangeEvent();
            }

            public void changedUpdate(DocumentEvent e)
            {
                fireChangeEvent();
            }
        });

        cbBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                fireChangeEvent();
            }
        });

        lblLengthBefore.setLabelFor(txtLengthBefore);
        lblLengthAfter.setLabelFor(txtLengthAfter);
        lblFiller.setLabelFor(txtFiller);
    }

    public void setFileType(FileType fileType)
    {
        allowBlock = FileTypeUtil.hasBlockComment(fileType);
    }

    public void setOptions(TemplateOptions options)
    {
        boolean isBlock = options.isBlock();
        if (isBlock)
        {
            rbBlockComment.setSelected(true);
        }
        else
        {
            rbLineComment.setSelected(true);
        }

        cbPrefixLines.setSelected(!allowBlock || options.isPrefixLines());
        cbSeparatorAfter.setSelected(options.isSeparateAfter());
        cbSeparatorBefore.setSelected(options.isSeparateBefore());
        txtLengthBefore.setValue(options.getLenBefore());
        txtLengthAfter.setValue(options.getLenAfter());
        txtFiller.setText(
            options.getFiller() == TemplateOptions.DEFAULT_FILLER ? "" : Character.toString(options.getFiller()));
        cbBox.setSelected(options.isBox());
        setEnabled(isEnabled());
    }

    public TemplateOptions getOptions()
    {
        TemplateOptions res = new TemplateOptions();
        res.setBlock(rbBlockComment.isSelected());
        res.setPrefixLines(!allowBlock || cbPrefixLines.isSelected());
        res.setSeparateAfter(cbSeparatorAfter.isSelected());
        res.setSeparateBefore(cbSeparatorBefore.isSelected());
        Object val = txtLengthBefore.getValue();
        if (val instanceof Number)
        {
            res.setLenBefore(((Number)val).intValue());
        }
        val = txtLengthAfter.getValue();
        if (val instanceof Number)
        {
            res.setLenAfter(((Number)val).intValue());
        }
        res.setBox(cbBox.isSelected());

        String filler = txtFiller.getText();
        if (filler.length() > 0)
        {
            res.setFiller(filler.charAt(0));
        }
        else
        {
            res.setFiller(TemplateOptions.DEFAULT_FILLER);
        }

        return res;
    }

    public void setEnabled(boolean enable)
    {
        super.setEnabled(enable);

        if (enable)
        {
            rbBlockComment.setEnabled(true);
            rbLineComment.setEnabled(true);
            cbPrefixLines.setEnabled(allowBlock);
            cbSeparatorBefore.setEnabled(true);
            cbSeparatorAfter.setEnabled(true);
            lblLengthBefore.setEnabled(cbSeparatorBefore.isSelected());
            txtLengthBefore.setEnabled(cbSeparatorBefore.isSelected());
            lblLengthAfter.setEnabled(cbSeparatorAfter.isSelected());
            txtLengthAfter.setEnabled(cbSeparatorAfter.isSelected());
            updateBox();
        }
        else
        {
            rbBlockComment.setEnabled(false);
            rbLineComment.setEnabled(false);
            cbPrefixLines.setEnabled(false);
            cbSeparatorBefore.setEnabled(false);
            cbSeparatorAfter.setEnabled(false);
            lblLengthBefore.setEnabled(false);
            txtLengthBefore.setEnabled(false);
            lblLengthAfter.setEnabled(false);
            txtLengthAfter.setEnabled(false);
            cbBox.setEnabled(false);
            lblFiller.setEnabled(false);
            txtFiller.setEnabled(false);
        }
    }

    public boolean isModified(TemplateOptions options)
    {
        return !getOptions().equals(options);
    }

    public void addOptionChangeListener(TemplateOptionsPanelListener listener)
    {
        listeners.add(TemplateOptionsPanelListener.class, listener);
    }

    public void removeOptionChangeListener(TemplateOptionsPanelListener listener)
    {
        listeners.remove(TemplateOptionsPanelListener.class, listener);
    }

    private void fireChangeEvent()
    {
        Object[] fires = listeners.getListenerList();
        for (int i = fires.length - 2; i >= 0; i -= 2)
        {
            if (fires[i] == TemplateOptionsPanelListener.class)
            {
                ((TemplateOptionsPanelListener)fires[i + 1]).optionChanged();
            }
        }
    }

    private void updateBox()
    {
        boolean enable = true;
        if (!cbSeparatorBefore.isSelected())
        {
            enable = false;
        }
        else if (!cbSeparatorAfter.isSelected())
        {
            enable = false;
        }
        else
        {
            if (!txtLengthBefore.getValue().equals(txtLengthAfter.getValue()))
            {
                enable = false;
            }
        }

        boolean either = cbSeparatorBefore.isSelected() || cbSeparatorAfter.isSelected();

        cbBox.setEnabled(enable);
        lblFiller.setEnabled(either);
        txtFiller.setEnabled(either);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR call it in your
     * code!
     */
    private void $$$setupUI$$$()
    {
        mainComponent = new JPanel();
        mainComponent.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), 0, 0));
        mainComponent
            .setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Formatting Options"));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainComponent.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null,
            null));
        rbBlockComment = new JRadioButton();
        rbBlockComment.setText("Use Block Comment");
        rbBlockComment.setMnemonic(85);
        SupportCode.setDisplayedMnemonicIndex(rbBlockComment, 0);
        panel1.add(rbBlockComment, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        cbPrefixLines = new JCheckBox();
        cbPrefixLines.setText("Prefix Each Line");
        cbPrefixLines.setMnemonic(72);
        SupportCode.setDisplayedMnemonicIndex(cbPrefixLines, 10);
        panel1.add(cbPrefixLines, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        rbLineComment = new JRadioButton();
        rbLineComment.setText("Use Line Comment");
        rbLineComment.setMnemonic(76);
        SupportCode.setDisplayedMnemonicIndex(rbLineComment, 4);
        mainComponent.add(rbLineComment, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 5, new Insets(0, 0, 0, 0), 3, -1));
        mainComponent.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null,
            null));
        cbSeparatorBefore = new JCheckBox();
        cbSeparatorBefore.setText("Separator Before");
        cbSeparatorBefore.setMnemonic(66);
        SupportCode.setDisplayedMnemonicIndex(cbSeparatorBefore, 10);
        panel2.add(cbSeparatorBefore, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        cbSeparatorAfter = new JCheckBox();
        cbSeparatorAfter.setText("Separator After");
        cbSeparatorAfter.setMnemonic(80);
        SupportCode.setDisplayedMnemonicIndex(cbSeparatorAfter, 2);
        panel2.add(cbSeparatorAfter, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        lblLengthBefore = new JLabel();
        lblLengthBefore.setText("Length: ");
        panel2.add(lblLengthBefore, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST,
            GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null,
            null));
        lblLengthAfter = new JLabel();
        lblLengthAfter.setText("Length: ");
        panel2.add(lblLengthAfter, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST,
            GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null,
            null));
        txtLengthBefore = new IntegerTextField();
        txtLengthBefore.setColumns(3);
        panel2.add(txtLengthBefore, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null,
            null));
        txtLengthAfter = new IntegerTextField();
        txtLengthAfter.setColumns(3);
        panel2.add(txtLengthAfter, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null,
            null));
        cbBox = new JCheckBox();
        cbBox.setText("Box");
        cbBox.setMnemonic(88);
        SupportCode.setDisplayedMnemonicIndex(cbBox, 2);
        panel2.add(cbBox, new GridConstraints(0, 3, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        lblFiller = new JLabel();
        lblFiller.setText("Filler:");
        lblFiller.setDisplayedMnemonic(70);
        SupportCode.setDisplayedMnemonicIndex(lblFiller, 0);
        panel2.add(lblFiller, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        txtFiller = new JTextField();
        txtFiller.setColumns(1);
        panel2.add(txtFiller, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
    }

    private EventListenerList listeners = new EventListenerList();
    private boolean allowBlock = false;

    private JPanel mainComponent;
    private JCheckBox cbSeparatorAfter;
    private JCheckBox cbSeparatorBefore;
    private JRadioButton rbLineComment;
    private JCheckBox cbPrefixLines;
    private JRadioButton rbBlockComment;
    private IntegerTextField txtLengthBefore;
    private IntegerTextField txtLengthAfter;
    private JLabel lblLengthBefore;
    private JLabel lblLengthAfter;
    private JCheckBox cbBox;
    private JTextField txtFiller;
    private JLabel lblFiller;

}