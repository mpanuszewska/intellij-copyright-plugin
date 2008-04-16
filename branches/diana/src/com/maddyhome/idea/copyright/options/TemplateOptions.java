package com.maddyhome.idea.copyright.options;

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

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;

public class TemplateOptions implements JDOMExternalizable, Cloneable
{
    public static final int MIN_SEPARATOR_LENGTH = 5;
    public static final int MAX_SEPARATOR_LENGTH = 300;
    public static final char DEFAULT_FILLER = ' ';

    public TemplateOptions()
    {
        setDefaults();
    }

    public void setDefaults()
    {
        setBlock(true);
        setPrefixLines(true);
        setSeparateBefore(false);
        setLenBefore(80);
        setSeparateAfter(false);
        setLenAfter(80);
        setBox(false);
        setFiller(DEFAULT_FILLER);
    }

    public boolean isBlock()
    {
        return block;
    }

    public void setBlock(boolean block)
    {
        this.block = block;
    }

    public boolean isSeparateBefore()
    {
        return separateBefore;
    }

    public void setSeparateBefore(boolean separateBefore)
    {
        this.separateBefore = separateBefore;
    }

    public boolean isSeparateAfter()
    {
        return separateAfter;
    }

    public void setSeparateAfter(boolean separateAfter)
    {
        this.separateAfter = separateAfter;
    }

    public boolean isPrefixLines()
    {
        return prefixLines;
    }

    public void setPrefixLines(boolean prefixLines)
    {
        this.prefixLines = prefixLines;
    }

    public int getLenBefore()
    {
        return lenBefore;
    }

    public void setLenBefore(int lenBefore)
    {
        this.lenBefore = lenBefore;
    }

    public int getLenAfter()
    {
        return lenAfter;
    }

    public void setLenAfter(int lenAfter)
    {
        this.lenAfter = lenAfter;
    }

    public boolean isBox()
    {
        return box;
    }

    public void setBox(boolean box)
    {
        this.box = box;
    }

    public char getFiller()
    {
        return filler;
    }

    public void setFiller(char filler)
    {
        this.filler = filler;
    }

    public void readExternal(Element element) throws InvalidDataException
    {
        DefaultJDOMExternalizer.readExternal(this, element);
    }

    public void writeExternal(Element element) throws WriteExternalException
    {
        DefaultJDOMExternalizer.writeExternal(this, element);
    }

    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final TemplateOptions that = (TemplateOptions)o;

        if (block != that.block)
        {
            return false;
        }
        if (box != that.box)
        {
            return false;
        }
        if (filler != that.filler)
        {
            return false;
        }
        if (lenAfter != that.lenAfter)
        {
            return false;
        }
        if (lenBefore != that.lenBefore)
        {
            return false;
        }
        if (prefixLines != that.prefixLines)
        {
            return false;
        }
        if (separateAfter != that.separateAfter)
        {
            return false;
        }
        return separateBefore == that.separateBefore;
    }

    public int hashCode()
    {
        int result;
        result = (block ? 1 : 0);
        result = 29 * result + (separateBefore ? 1 : 0);
        result = 29 * result + (separateAfter ? 1 : 0);
        result = 29 * result + (prefixLines ? 1 : 0);
        result = 29 * result + lenBefore;
        result = 29 * result + lenAfter;
        result = 29 * result + (box ? 1 : 0);
        result = 29 * result + (int)filler;
        return result;
    }

    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("TemplateOptions");
        sb.append("{block=").append(block);
        sb.append(", separateBefore=").append(separateBefore);
        sb.append(", separateAfter=").append(separateAfter);
        sb.append(", prefixLines=").append(prefixLines);
        sb.append(", lenBefore=").append(lenBefore);
        sb.append(", lenAfter=").append(lenAfter);
        sb.append(", box=").append(box);
        sb.append(", filler=").append(filler);
        sb.append('}');
        return sb.toString();
    }

    public TemplateOptions clone() throws CloneNotSupportedException
    {
        return (TemplateOptions)super.clone();
    }

    public void validate() throws ConfigurationException
    {
        if (lenBefore < MIN_SEPARATOR_LENGTH || lenBefore > MAX_SEPARATOR_LENGTH ||
            lenAfter < MIN_SEPARATOR_LENGTH || lenAfter > MAX_SEPARATOR_LENGTH)
        {
            throw new ConfigurationException("Separator length must be btween " + MIN_SEPARATOR_LENGTH + " and " +
                MAX_SEPARATOR_LENGTH);
        }
    }

    public boolean block;
    public boolean separateBefore;
    public boolean separateAfter;
    public boolean prefixLines;
    public int lenBefore;
    public int lenAfter;
    public boolean box;
    public char filler;
}