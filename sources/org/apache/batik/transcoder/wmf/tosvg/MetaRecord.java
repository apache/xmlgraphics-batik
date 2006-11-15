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

package org.apache.batik.transcoder.wmf.tosvg;

import java.util.Vector;

public class MetaRecord /*implements Serializable*/ {
    public int	functionId;
    public int	numPoints;

    private Vector ptVector;

    public MetaRecord() {
        ptVector = new Vector();
    }

    public void EnsureCapacity( int cc ) {
        ptVector.ensureCapacity( cc );
    }

    public void AddElement( Object obj ) {
        ptVector.addElement( obj );
    }

    public Integer ElementAt( int offset ) {
        return (Integer)ptVector.elementAt( offset );
    }

    /** A record that contain byte arrays elements.
     */
    public static class ByteRecord extends MetaRecord {
        public byte[] bstr;

        public ByteRecord(byte[] bstr) {
            this.bstr = bstr;
        }
    }

    public static class StringRecord extends MetaRecord /*implements Serializable*/ {
        public String text;

        public StringRecord( String newText ) {
            text = newText;
        }
    }
}


