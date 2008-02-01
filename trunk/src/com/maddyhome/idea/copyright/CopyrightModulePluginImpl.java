package com.maddyhome.idea.copyright;

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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.maddyhome.idea.copyright.options.Options;
import com.maddyhome.idea.copyright.ui.OptionsPanel;
import org.jdom.Element;

import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * The IDEA component for this plugin.
 */
public class CopyrightModulePluginImpl implements CopyrightModulePlugin
{
    public CopyrightModulePluginImpl(Project project)
    {
        this.project = project;
        URL resource = getClass().getResource("/copyright18x18.png");
        if (resource != null)
        {
            icon = new ImageIcon(resource);
        }

        CopyrightProjectPlugin plugin = project.getComponent(CopyrightProjectPlugin.class);
        try
        {
            options = plugin.getOptions().clone();
        }
        catch (CloneNotSupportedException e)
        {
        }
    }

    public void projectOpened()
    {
    }

    public void projectClosed()
    {
    }

    public void moduleAdded()
    {
    }

    public String getComponentName()
    {
        return "copyright";
    }

    public void initComponent()
    {
    }

    public void disposeComponent()
    {
    }

    public String getDisplayName()
    {
        return "Copyright";
    }

    public Icon getIcon()
    {
        return icon;
    }

    public String getHelpTopic()
    {
        return null;
    }

    public JComponent createComponent()
    {
        logger.info("createComponent()");
        if (optionsPanel == null)
        {
            optionsPanel = new OptionsPanel(project, options, true);
        }

        return optionsPanel.getMainComponent();
    }

    public boolean isModified()
    {
        logger.info("isModified()");
        boolean res = false;
        if (optionsPanel != null)
        {
            res = optionsPanel.isModified(options);
        }

        logger.info("isModified() = " + res);

        return res;
    }

    public void apply() throws ConfigurationException
    {
        logger.info("apply()");
        if (optionsPanel != null)
        {
            optionsPanel.validate();
            options = optionsPanel.getOptions();
            logger.debug("options=" + options);
        }
    }

    public void reset()
    {
        logger.info("reset()");
        if (optionsPanel != null)
        {
            optionsPanel.setOptions(options);
        }
    }

    public void disposeUIResources()
    {
        optionsPanel = null;
    }

    public void readExternal(Element element) throws InvalidDataException
    {
        logger.info("readExternal()");
        options.readExternal(element);
    }

    public void writeExternal(Element element) throws WriteExternalException
    {
        logger.info("writeExternal()");
        options.writeExternal(element);
    }

    public Options getOptions()
    {
        return options;
    }

    public Options getUsableOptions()
    {
        CopyrightProjectPlugin plugin = project.getComponent(CopyrightProjectPlugin.class);
        Options projOpts = plugin.getOptions();
        if (projOpts.getState() == Options.STATE_DISABLE)
        {
            return projOpts;
        }
        else if (options.getState() == Options.STATE_PROJECT)
        {
            return projOpts;
        }
        else
        {
            return options;
        }
    }

    public void setOptions(Options options)
    {
        this.options = options;
    }

    private Project project;
    private OptionsPanel optionsPanel = null;
    private Options options = new Options();
    private Icon icon = null;

    private static Logger logger = Logger.getInstance(CopyrightModulePluginImpl.class.getName());
}