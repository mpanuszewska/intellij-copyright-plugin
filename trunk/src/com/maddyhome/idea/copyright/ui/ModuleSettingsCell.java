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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ComboboxWithBrowseButton;
import com.maddyhome.idea.copyright.options.Options;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ModuleSettingsCell extends JPanel
{
    public ModuleSettingsCell()
    {
        super(new BorderLayout());

        setupControls();
    }

    public void setData(Project project, Module module, Options options)
    {
        this.project = project;
        this.module = module;

        setOptions(options);
    }

    public JComponent getMainComponent()
    {
        return this;
    }

    public boolean isModified(Options options)
    {
        return !getOptions().equals(options);
    }

    public void setOptions(Options options)
    {
        try
        {
            this.options = options.clone();
        }
        catch (CloneNotSupportedException e)
        {
        }

        box.getComboBox().setSelectedIndex(options.getState());
    }

    public Options getOptions()
    {
        return options;
    }

    private void setupControls()
    {
        add(box, BorderLayout.CENTER);
        //box.getComboBox().setRenderer(new EditorComboBoxRenderer(box.getComboBox().getEditor()));

        box.getComboBox().addItem(Options.getStateString(Options.STATE_MODULE));
        box.getComboBox().addItem(Options.getStateString(Options.STATE_PROJECT));
        box.getComboBox().addItem(Options.getStateString(Options.STATE_DISABLE));

        box.getComboBox().addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                options.setState(box.getComboBox().getSelectedIndex());
            }
        });

        box.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                options.setState(box.getComboBox().getSelectedIndex());
                OptionsDlg dlg = new OptionsDlg(project, module, options);
                dlg.show();
                if (dlg.isOK())
                {
                    setOptions(dlg.getOptions());
                }
            }
        });
    }

    private ComboboxWithBrowseButton box = new ComboboxWithBrowseButton();
    private Options options = null;
    private Project project = null;
    private Module module = null;
}