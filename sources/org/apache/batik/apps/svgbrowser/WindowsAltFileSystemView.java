/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.apps.svgbrowser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Vector;

import javax.swing.filechooser.FileSystemView;

/**
 * Work around FileSystemView implementation bug on the Windows 
 * platform. See:
 *
 * <a href="http://forums.java.sun.com/thread.jsp?forum=38&thread=71491">
 * Using JFileChooser in WebStart-deployed application</a>
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */

// This class is necessary due to an annoying bug on Windows NT where
// instantiating a JFileChooser with the default FileSystemView will
// cause a "drive A: not ready" error every time. I grabbed the
// Windows FileSystemView impl from the 1.3 SDK and modified it so
// as to not use java.io.File.listRoots() to get fileSystem roots.
// java.io.File.listRoots() does a SecurityManager.checkRead() which
// causes the OS to try to access drive A: even when there is no disk,
// causing an annoying "abort, retry, ignore" popup message every time
// we instantiate a JFileChooser!
//
// Instead of calling listRoots() we use a straightforward alternate
// method of getting file system roots.

class WindowsAltFileSystemView extends FileSystemView {
    public static final String EXCEPTION_CONTAINING_DIR_NULL
        = "AltFileSystemView.exception.containing.dir.null";

    public static final String EXCEPTION_DIRECTORY_ALREADY_EXISTS
        = "AltFileSystemView.exception.directory.already.exists";

    public static final String NEW_FOLDER_NAME = 
        " AltFileSystemView.new.folder.name";

    public static final String FLOPPY_DRIVE = 
        "AltFileSystemView.floppy.drive";

    private static final Object[] noArgs = {};
    private static final Class[] noArgTypes = {};
    
    private static Method listRootsMethod = null;
    private static boolean listRootsMethodChecked = false;
    
    /**
     * Returns true if the given file is a root.
     */
    public boolean isRoot(File f) {
        if(!f.isAbsolute()) {
            return false;
        }
        
        String parentPath = f.getParent();
        if(parentPath == null) {
            return true;
        } else {
            File parent = new File(parentPath);
            return parent.equals(f);
        }
    }
    
    /**
     * creates a new folder with a default folder name.
     */
    public File createNewFolder(File containingDir) throws
        IOException {
        if(containingDir == null) {
            throw new IOException(Resources.getString(EXCEPTION_CONTAINING_DIR_NULL));
        }
        File newFolder = null;
        // Using NT's default folder name
        newFolder = createFileObject(containingDir, 
                                     Resources.getString(NEW_FOLDER_NAME));
        int i = 2;
        while (newFolder.exists() && (i < 100)) {
            newFolder = createFileObject
                (containingDir, Resources.getString(NEW_FOLDER_NAME) + " (" + i + ")");
            i++;
        }
        
        if(newFolder.exists()) {
            throw new IOException
                (Resources.formatMessage(EXCEPTION_DIRECTORY_ALREADY_EXISTS,
                                         new Object[]{newFolder.getAbsolutePath()}));
        } else {
            newFolder.mkdirs();
        }
        
        return newFolder;
    }
    
    /**
     * Returns whether a file is hidden or not. On Windows
     * there is currently no way to get this information from
     * io.File, therefore always return false.
     */
    public boolean isHiddenFile(File f) {
        return false;
    }
    
    /**
     * Returns all root partitians on this system. On Windows, this
     * will be the A: through Z: drives.
     */
    public File[] getRoots() {
        
        Vector rootsVector = new Vector();
        
        // Create the A: drive whether it is mounted or not
        FileSystemRoot floppy = new FileSystemRoot(Resources.getString(FLOPPY_DRIVE)
                                                   + "\\");
        rootsVector.addElement(floppy);
        
        // Run through all possible mount points and check
        // for their existance.
        for (char c = 'C'; c <= 'Z'; c++) {
            char device[] = {c, ':', '\\'};
            String deviceName = new String(device);
            File deviceFile = new FileSystemRoot(deviceName);
            if (deviceFile != null && deviceFile.exists()) {
                rootsVector.addElement(deviceFile);
            }
        }
        File[] roots = new File[rootsVector.size()];
        rootsVector.copyInto(roots);
        return roots;
    }
    
    class FileSystemRoot extends File {
        public FileSystemRoot(File f) {
            super(f, "");
        }
        
        public FileSystemRoot(String s) {
            super(s);
        }
        
        public boolean isDirectory() {
            return true;
        }
    }
    
}

