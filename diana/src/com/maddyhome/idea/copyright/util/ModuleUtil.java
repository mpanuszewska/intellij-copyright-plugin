package com.maddyhome.idea.copyright.util;

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

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.module.Module;
import com.maddyhome.idea.copyright.CopyrightModulePlugin;
import com.maddyhome.idea.copyright.options.Options;

public class ModuleUtil
{
    public static boolean isModuleActive(DataContext context)
    {
        Module module = DataKeys.MODULE.getData(context);
        if (module == null)
        {
            return false;
        }

        CopyrightModulePlugin plugin = module.getComponent(CopyrightModulePlugin.class);
        Options options = plugin.getUsableOptions();

        return options.isActive();
    }

    private ModuleUtil()
    {
    }
}