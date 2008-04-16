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
import com.intellij.openapi.ui.DialogWrapper;
import com.maddyhome.idea.copyright.CopyrightModulePlugin;
import com.maddyhome.idea.copyright.options.Options;

import javax.swing.JComponent;

public class OptionsDlg extends DialogWrapper
{
    public OptionsDlg(Project project, Module module, Options options)
    {
        super(project, false);

        plugin = module.getComponent(CopyrightModulePlugin.class);
        this.options = options;
        panel = new OptionsPanel(project, options, true);

        setTitle("Copyright Settings - " + module.getName());

        init();
    }

    public Options getOptions()
    {
        return panel.getOptions();
    }

    protected JComponent createCenterPanel()
    {
        return panel.getMainComponent();
    }

    public boolean isOKActionEnabled()
    {
        return panel.isModified(options);
    }

    protected void doOKAction()
    {
        plugin.setOptions(getOptions());

        super.doOKAction();
    }

    private OptionsPanel panel;
    private Options options;
    private CopyrightModulePlugin plugin;
}