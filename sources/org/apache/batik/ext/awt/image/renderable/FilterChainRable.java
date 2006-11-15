/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.geom.Rectangle2D;

/**
 * Implements a filter operation.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface FilterChainRable extends Filter {
    /**
     * Returns the resolution along the X axis.
     */
    public int getFilterResolutionX();

    /**
     * Sets the resolution along the X axis, i.e., the maximum
     * size for intermediate images along that axis.
     * The value should be greater than zero to have an effect.
     */
    public void setFilterResolutionX(int filterResolutionX);
    
    /**
     * Returns the resolution along the Y axis.
     */
    public int getFilterResolutionY();

    /**
     * Sets the resolution along the Y axis, i.e., the maximum
     * size for intermediate images along that axis.
     * The value should be greater than zero to have an effect.
     */
    public void setFilterResolutionY(int filterResolutionY);
    
    /**
     * Sets the filter output area, in user space. 
     */
    public void setFilterRegion(Rectangle2D filterRegion);

    /**
     * Returns the filter output area, in user space
     */
    public Rectangle2D getFilterRegion();

    /**
     * Sets the source for this chain. Should not be null
     */
    public void setSource(Filter src);

    /**
     * Returns this filter's source.
     */
    public Filter getSource();
}
