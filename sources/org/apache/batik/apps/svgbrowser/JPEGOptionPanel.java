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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSlider;

import org.apache.batik.util.gui.ExtendedGridBagConstraints;

/**
 * This class represents a panel to control jpeg encoding quality.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class JPEGOptionPanel extends OptionPanel {
    /**
     * The jpeg encoding quality.
     */
    protected JSlider quality;

    /**
     * Creates a new panel.
     */
    public JPEGOptionPanel() {
	super(new GridBagLayout());

	ExtendedGridBagConstraints constraints = 
	    new ExtendedGridBagConstraints();

	
	constraints.insets = new Insets(5, 5, 5, 5);

	constraints.weightx = 0;
	constraints.weighty = 0;
	constraints.fill = GridBagConstraints.NONE;
	constraints.setGridBounds(0, 0, 1, 1);
	add(new JLabel(resources.getString("JPEGOptionPanel.label")), 
	    constraints);

	quality = new JSlider();
	quality.setMinimum(0);
	quality.setMaximum(100);
	quality.setMajorTickSpacing(10);
	quality.setMinorTickSpacing(5);
	quality.setPaintTicks(true);
	quality.setPaintLabels(true);
	quality.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
	Hashtable labels = new Hashtable();
	for (int i=0; i < 100; i+=10) {
	    labels.put(new Integer(i), new JLabel("0."+i/10));
	}
	labels.put(new Integer(100), new JLabel("1"));
	quality.setLabelTable(labels);

	Dimension dim = quality.getPreferredSize();
	quality.setPreferredSize(new Dimension(350, dim.height));

	constraints.weightx = 1.0;
	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.setGridBounds(1, 0, 1, 1);
	add(quality, constraints);
    }

    /**
     * Returns the jpeg quality.
     */
    public float getQuality() {
	return quality.getValue()/100f;
    }

    /**
     * Shows a dialog to choose the jpeg encoding quality and return
     * the quality as a float.  
     */
    public static float showDialog(Component parent) {
        String title = resources.getString("JPEGOptionPanel.dialog.title");
        JPEGOptionPanel panel = new JPEGOptionPanel();
	Dialog dialog = new Dialog(parent, title, panel);
	dialog.pack();
	dialog.show();
	return panel.getQuality();
    }
}
