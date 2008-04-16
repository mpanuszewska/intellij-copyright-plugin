package com.maddyhome.idea.copyright.actions;

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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.maddyhome.idea.copyright.ui.ModuleDlg;
import com.maddyhome.idea.copyright.ui.RecursionDlg;
import com.maddyhome.idea.copyright.util.FileTypeUtil;
import com.maddyhome.idea.copyright.util.FileUtil;
import com.maddyhome.idea.copyright.util.ModuleUtil;

import java.util.ArrayList;
import java.util.List;

public class UpdateCopyrightAction extends AnAction
{
    public void update(AnActionEvent event)
    {
        Presentation presentation = event.getPresentation();
        DataContext context = event.getDataContext();
        Project project = DataKeys.PROJECT.getData(context);
        if (project == null)
        {
            presentation.setEnabled(false);
            return;
        }

        VirtualFile[] files = DataKeys.VIRTUAL_FILE_ARRAY.getData(context);
        Editor editor = DataKeys.EDITOR.getData(context);
        if (editor != null)
        {
            PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
            if (file == null || !FileTypeUtil.getInstance().isSupportedFile(file))
            {
                presentation.setEnabled(false);
                return;
            }
        }
        else if (files != null && FileUtil.areFiles(files))
        {
            for (VirtualFile vfile : files)
            {
                PsiFile file = PsiManager.getInstance(project).findFile(vfile);
                if (file == null || !FileTypeUtil.getInstance().isSupportedFile(file.getVirtualFile()))
                {
                    presentation.setEnabled(false);
                    return;
                }
            }

            presentation.setEnabled(true);
            return;
        }
        else if ((files == null || files.length != 1) && DataKeys.MODULE_CONTEXT.getData(context) == null &&
            DataKeys.PROJECT_CONTEXT.getData(context) == null)
        {
            PsiElement elem = DataKeys.PSI_ELEMENT.getData(context);
            if (elem == null)
            {
                presentation.setEnabled(false);
                return;
            }

            if (!(elem instanceof PsiDirectory))
            {
                PsiFile file = elem.getContainingFile();
                if (file == null || !FileTypeUtil.getInstance().isSupportedFile(file.getVirtualFile()))
                {
                    presentation.setEnabled(false);
                    return;
                }
            }
        }

        presentation.setEnabled(ModuleUtil.isModuleActive(context));
    }

    public void actionPerformed(AnActionEvent event)
    {
        DataContext context = event.getDataContext();
        Project project = DataKeys.PROJECT.getData(context);
        Module module = DataKeys.MODULE.getData(context);
        PsiDocumentManager.getInstance(project).commitAllDocuments();

        VirtualFile[] files = DataKeys.VIRTUAL_FILE_ARRAY.getData(context);
        Editor editor = DataKeys.EDITOR.getData(context);

        PsiFile file = null;
        PsiDirectory dir;
        if (editor != null)
        {
            file = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
            if (file == null)
            {
                return;
            }
            dir = file.getContainingDirectory();
        }
        else
        {
            if (FileUtil.areFiles(files))
            {
                ReadonlyStatusHandler.OperationStatus operationstatus =
                    ReadonlyStatusHandler.getInstance(project).ensureFilesWritable(files);
                if (!operationstatus.hasReadonlyFiles())
                {
                    (new UpdateCopyrightProcessor(project, null, FileUtil.convertToPsiFiles(files, project))).run();
                }

                return;
            }
            Module modCtx = DataKeys.MODULE_CONTEXT.getData(context);
            if (modCtx != null)
            {
                ModuleDlg dlg = new ModuleDlg(project, module);
                dlg.show();
                if (!dlg.isOK())
                {
                    return;
                }

                (new UpdateCopyrightProcessor(project, module)).run();

                return;
            }

            PsiElement psielement = DataKeys.PSI_ELEMENT.getData(context);
            if (psielement == null)
            {
                return;
            }

            if (psielement instanceof PsiPackage)
            {
                dir = ((PsiPackage)psielement).getDirectories()[0];
            }
            else if (psielement instanceof PsiDirectory)
            {
                dir = (PsiDirectory)psielement;
            }
            else
            {
                file = psielement.getContainingFile();
                if (file == null)
                {
                    return;
                }
                dir = file.getContainingDirectory();
            }
        }

        RecursionDlg recDlg = new RecursionDlg(project, file != null ? file.getVirtualFile() : dir.getVirtualFile());
        recDlg.show();
        if (!recDlg.isOK())
        {
            return;
        }

        if (recDlg.isAll())
        {
            if (!ensureFilesWritable(dir)) return;

            (new UpdateCopyrightProcessor(project, module, dir, recDlg.includeSubdirs())).run();
        }

        else
        {
            (new UpdateCopyrightProcessor(project, module, file)).run();
        }
    }

    private boolean ensureFilesWritable(PsiDirectory dir)
    {
        final List<VirtualFile> contents = new ArrayList<VirtualFile>();

        ProjectRootManager.getInstance(dir.getProject()).getFileIndex().iterateContentUnderDirectory(dir.getVirtualFile(), new ContentIterator()
        {
            public boolean processFile(VirtualFile virtualFile)
            {
                contents.add(virtualFile);

                return true;
            }
        });

        final VirtualFile[] contentsArray = contents.toArray(new VirtualFile[contents.size()]);

        return !ReadonlyStatusHandler.getInstance(dir.getProject()).ensureFilesWritable(contentsArray).hasReadonlyFiles();
    }
}