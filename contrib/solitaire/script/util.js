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
var isAdobe = false;
try {
  var vStr = getSVGViewerVersion();
  var idx = vStr.indexOf("; ");
  if (idx != -1) {
    var ver = parseFloat(vStr.substring(idx+2));
    if (ver <= 3)
      isAdobe = true;
  }
} catch (x) { }

  function screenCTM(elem) {
    if (!isAdobe) 
    return elem.getScreenCTM();
    if (elem == root) {
      var scale = root.currentScale;
      var trans = root.currentTranslate;
      var ret = root.createSVGMatrix();
      ret.scale(scale);
      ret.translate(trans.x, trans.y);
      return ret;
    }

    var pMat = screenCTM(elem.parentNode);
    
    var eMat = elem.getCTM();
    if (eMat == null) return pMat;
    eMat = eMat.multiply(pMat);
    return eMat;
  }

function transformToElement(from, to) {
  if (!isAdobe) 
    return from.getTransformToElement(to);
  var m1 = screenCTM(from);
  var m2 = screenCTM(to);
  return m1.multiply(m2.inverse());
}

// Transform screen x/y to elem's coordinate system.
// returns an SVGPoint object.
function localPt(elem, x, y) {
    var mat = screenCTM(elem);
    var imat = mat.inverse();
    var cPt     = document.getRootElement().createSVGPoint();
    cPt.x = x;
    cPt.y = y;
    cPt   = cPt.matrixTransform(imat);
    return cPt;
}

function ForwardMouseDown(obj) {
  this.obj = obj;
  this.handleEvent = function(evt) {
    obj.mousedown(evt);
  }
}

function ForwardMouseMove(obj) {
  this.obj = obj;
  this.handleEvent = function(evt) {
    obj.mousemove(evt);
  }
}

function ForwardMouseUp(obj) {
  this.obj = obj;
  this.handleEvent = function(evt) {
    obj.mouseup(evt);
  }
}

