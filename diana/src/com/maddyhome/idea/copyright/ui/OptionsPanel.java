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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.SupportCode;
import com.maddyhome.idea.copyright.CopyrightProjectPlugin;
import com.maddyhome.idea.copyright.options.ExternalOptionHelper;
import com.maddyhome.idea.copyright.options.Options;
import com.maddyhome.idea.copyright.util.FileTypeUtil;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 */
public class OptionsPanel
{
    /**
     * @param project
     * @param config configuration parameters.
     * @param isModule
     */
    public OptionsPanel(Project project, Options config, boolean isModule)
    {
        this.project = project;
        this.isModule = isModule;
        setupControls();

        setOptions(config);
    }

    public JComponent getMainComponent()
    {
        return mainPanel;
    }

    public void validate() throws ConfigurationException
    {
        if (isModule && stateComboBox.getSelectedIndex() != Options.STATE_MODULE)
        {
            return;
        }

        int tab = 0;
        try
        {
            templatePanel.getOptions().validate();
            tab = -1;
            for (int i = 0; i < langTabs.length; i++, tab--)
            {
                ConfigTab langTab = langTabs[i];
                langTab.getOptions().validate();
            }
        }
        finally
        {
            if (tab >= 0)
            {
                tabs.setSelectedIndex(tab);
            }
            else
            {
                tab = Math.abs(tab) - 1;
                if (tab < fileTypeTabs.getTabCount())
                {
                    tabs.setSelectedIndex(1);
                    fileTypeTabs.setSelectedIndex(tab);
                }
            }
        }
    }

    /**
     * @param config configuration parameters.
     */
    public void setOptions(Options config)
    {
        logger.debug("setOptions");
        templatePanel.setOptions(config.getTemplateOptions());
        for (ConfigTab langTab : langTabs)
        {
            langTab.setOptions(config.getOptions(langTab.getName()));
        }
        if (isModule)
        {
            stateComboBox.setSelectedIndex(config.getState());
        }
    }

    /**
     * @return the configuration.
     */
    public Options getOptions()
    {
        logger.debug("getOptions");

        Options res = new Options();

        res.setTemplateOptions(templatePanel.getOptions());

        for (ConfigTab langTab : langTabs)
        {
            res.setOptions(langTab.getName(), langTab.getOptions());
        }
        if (isModule)
        {
            res.setState(stateComboBox.getSelectedIndex());
        }

        return res;
    }

    public boolean isModified(Options options)
    {
        logger.debug("isModified");

        if (isModule && options.getState() != stateComboBox.getSelectedIndex())
        {
            return true;
        }

        if (templatePanel.isModified(options.getTemplateOptions()))
        {
            return true;
        }

        for (ConfigTab langTab : langTabs)
        {
            if (langTab.isModified(options.getOptions(langTab.getName())))
            {
                return true;
            }
        }

        return false;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enable)
    {
        if (enable == isEnabled()) return;

        if (!enable) lastTab = tabs.getSelectedIndex();
        for (int i = 0; i < tabs.getTabCount() - 1; i++)
        {
            tabs.setEnabledAt(i, enable);
        }

        if (!enable)
        {
            tabs.setSelectedIndex(tabs.getTabCount() - 1);
        }
        else
        {
            tabs.setSelectedIndex(lastTab);
        }

        btnProjectExternal.setEnabled(enable);

        enabled = enable;
    }

    private void setupControls()
    {
        logger.debug("setupControls");

        if (isModule)
        {
            stateComboBox.addItem(Options.getStateString(Options.STATE_MODULE));
            stateComboBox.addItem(Options.getStateString(Options.STATE_PROJECT));
            stateComboBox.addItem(Options.getStateString(Options.STATE_DISABLE));
            stateComboBox.setSelectedIndex(-1);

            stateComboBox.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    boolean enable = stateComboBox.getSelectedIndex() == Options.STATE_MODULE;
                    setEnabled(enable);
                    btnCopyProject.setEnabled(stateComboBox.getSelectedIndex() != Options.STATE_DISABLE);
                    btnModuleExternal.setEnabled(stateComboBox.getSelectedIndex() != Options.STATE_DISABLE);
                }
            });

            btnCopyProject.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    CopyrightProjectPlugin plugin = project.getComponent(CopyrightProjectPlugin.class);
                    Options projOpts = plugin.getOptions();
                    projOpts.setState(Options.STATE_MODULE);
                    setOptions(projOpts);
                }
            });

            btnModuleExternal.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    copyExternalOptions();
                }
            });

            lblState.setLabelFor(stateComboBox);
        }
        else
        {
            btnProjectExternal.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    copyExternalOptions();
                }
            });
        }

        templatePanel = new TemplateCommentPanel(null, null, null);
        tabs.addTab("Template", templatePanel.getMainComponent());

        fileTypeTabs = new JTabbedPane();

        FileType[] types = FileTypeUtil.getInstance().getSupportedTypes();
        Arrays.sort(types, new FileTypeUtil.SortByName());
        langTabs = new ConfigTab[types.length];
        for (int i = 0; i < types.length; i++)
        {
            FileType type = types[i];
            String[] names = FileTypeUtil.getInstance().getMappedNames(type);
            StringBuffer label = new StringBuffer();
            for (int j = 0; j < names.length; j++)
            {
                String name = names[j];
                if (j > 0)
                {
                    label.append('/');
                }
                label.append(name);
            }
            langTabs[i] = ConfigTabFactory.createConfigTab(type, templatePanel);
            fileTypeTabs.addTab(label.toString(), type.getIcon(), langTabs[i].getMainComponent());
        }
        fileTypeTabs.setSelectedIndex(0);

        tabs.addTab("File Types", fileTypeTabs);
        tabs.addTab("Overview", overviewTab.getMainComponent());

        moduleStatePanel.setVisible(isModule);
        projectStatePanel.setVisible(!isModule);
    }

    private void copyExternalOptions()
    {
        Options external = ExternalOptionHelper.getExternalOptions(project);
        if (external != null)
        {
            setOptions(external);
            JOptionPane.showMessageDialog(null,
                "The copyright settings have been successfully imported.",
                "Import Complete",
                JOptionPane.INFORMATION_MESSAGE);
        }
        else
        {
            JOptionPane.showMessageDialog(null,
                "The selected file did not contain any copyright settings.",
                "Import Failure",
                JOptionPane.WARNING_MESSAGE);
        }
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(3, 1, new Insets(5, 5, 5, 5), -1, -1));
        tabs = new JTabbedPane();
        mainPanel.add(tabs, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
        moduleStatePanel = new JPanel();
        moduleStatePanel.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(moduleStatePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null,
            null));
        lblState = new JLabel();
        lblState.setText("Copyright Notice:");
        lblState.setDisplayedMnemonic(71);
        SupportCode.setDisplayedMnemonicIndex(lblState, 6);
        moduleStatePanel.add(lblState, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null,
            null));
        stateComboBox = new JComboBox();
        moduleStatePanel.add(stateComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
            null, null, null));
        btnCopyProject = new JButton();
        btnCopyProject.setText("Copy Project Settings");
        btnCopyProject.setMnemonic(74);
        SupportCode.setDisplayedMnemonicIndex(btnCopyProject, 8);
        moduleStatePanel.add(btnCopyProject, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null,
            null));
        btnModuleExternal = new JButton();
        btnModuleExternal.setText("Import Settings...");
        btnModuleExternal.setMnemonic(73);
        SupportCode.setDisplayedMnemonicIndex(btnModuleExternal, 0);
        moduleStatePanel.add(btnModuleExternal, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null,
            null));
        projectStatePanel = new JPanel();
        projectStatePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(projectStatePanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null,
            null));
        btnProjectExternal = new JButton();
        btnProjectExternal.setText("Import Settings...");
        btnProjectExternal.setMnemonic(73);
        SupportCode.setDisplayedMnemonicIndex(btnProjectExternal, 0);
        projectStatePanel.add(btnProjectExternal, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null,
            null));
    }

    private JPanel mainPanel;
    private JTabbedPane tabs;
    private JTabbedPane fileTypeTabs;
    private JComboBox stateComboBox;
    private JPanel moduleStatePanel;
    private JButton btnCopyProject;
    private JButton btnModuleExternal;
    private JButton btnProjectExternal;
    private JPanel projectStatePanel;
    private JLabel lblState;

    private Project project;
    private boolean isModule;
    private TemplateCommentPanel templatePanel;
    private int lastTab;
    private boolean enabled = true;

    private ConfigTab[] langTabs;
    private OverviewTab overviewTab = new OverviewTab();

    private static Logger logger = Logger.getInstance(OptionsPanel.class.getName());
}