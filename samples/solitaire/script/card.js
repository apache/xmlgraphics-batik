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
var svgns   = "http://www.w3.org/2000/svg";
var xlinkns = "http://www.w3.org/1999/xlink";

var SUITE_DIAMOND = 1;
var SUITE_CLUB    = 2;
var SUITE_HEART   = 3;
var SUITE_SPADE   = 4;

var MOVING = new Array();

function Card(doc, value, suite, deck, faceup) {
  this.value = value;
  this.suite = suite;
  this.deck  = deck;
  
  this.elem = doc.createElementNS(svgns, "g");
  this.svg = doc.createElementNS(svgns, "svg");
  this.svg.setAttribute("x", "0");
  this.svg.setAttribute("y", "0");
  this.svg.setAttribute("width", "100");
  this.svg.setAttribute("height", "100");
  this.elem.appendChild(this.svg);

  var id;
  switch(suite) {
  case SUITE_DIAMOND: id = "d"; break;
  case SUITE_CLUB:    id = "c"; break;
  case SUITE_HEART: id = "h"; break;
  case SUITE_SPADE: id = "s"; break;
  }
  id += value;
  this.id = id;
  this.card = doc.createElementNS(svgns, "use");
  this.faceup = faceup;
  if (this.faceup) {
    this.card.setAttributeNS(xlinkns, "xlink:href",
                             deck + "#" + id);
  } else {
    this.card.setAttributeNS(xlinkns, "xlink:href",
                             deck + "#card-back");
  }
  this.card.setAttribute("x", "0");
  this.card.setAttribute("y", "0");
  this.card.setAttribute("width", "100%");
  this.card.setAttribute("height", "100%");
  this.svg.appendChild(this.card);
  this.rect = doc.createElementNS(svgns, "rect");
  this.rect.setAttribute("x", "0");
  this.rect.setAttribute("y", "0");
  this.rect.setAttribute("width", "100%");
  this.rect.setAttribute("height", "100%");
  this.rect.setAttribute("style", "visibility:hidden; pointer-events:fill");
  this.svg.appendChild(this.rect);
  
  return this;
}

Card.prototype.flipCard = function(faceup) {
  if (this.faceup == faceup) return;
  if (faceup) {
    this.card.setAttributeNS(xlinkns, "xlink:href",
                             this.deck + "#" + this.id);
  } else {
    this.card.setAttributeNS(xlinkns, "xlink:href",
                             this.deck + "#card-back");
  }
  this.faceup = faceup;
}

Card.prototype.getValue = function() {
  return this.value;
}

Card.prototype.getSuite = function() {
  return this.suite;
}

Card.prototype.getElem = function() {
  return this.elem;
}

Card.prototype.suiteMatch = function(c) {
  return c.suite == this.suite;
}

Card.prototype.isRed = function() {
  return ((this.suite == SUITE_HEART) || (this.suite == SUITE_DIAMOND));
}
Card.prototype.isBlack = function() {
  return ((this.suite == SUITE_CLUB) || (this.suite == SUITE_SPADE));
}

Card.prototype.colorMatch = function(c) {
  return c.isRed() == this.isRed();
}

Card.prototype.valueMatch = function(c) {
  return c.value == this.value;
}

Card.prototype.valueOneHigher = function(c) {
  return c.value-1 == this.value;
}

Card.prototype.valueOneLower = function(c) {
  return c.value+1 == this.value;
}

Card.prototype.asString = function() {
  return this.id;
}

Card.prototype.setPos = function(x, y) {
  this.x = x;
  this.y = y;
  this.elem.setAttribute("transform", "translate("+x+","+y+")");
}

Card.prototype.setSize = function(w, h) {
  this.w = w;
  this.h = h;
  this.svg.setAttribute("width",  ""+w);
  this.svg.setAttribute("height", ""+h);
}

Card.prototype.moveTo = function(x, y, step) {
  this.destX = x;
  this.destY = y;
  this.dx = (x - this.x);
  this.dy = (y - this.y);
  var dist = Math.sqrt(this.dx*this.dx+this.dy*this.dy);
  var steps = Math.floor(dist/step);
  if (steps < 1) steps = 1;
  this.dx /= steps;
  this.dy /= steps;
  if (this.moving)
    return;

  if (!this.updateDisplay())
    return;

  this.moving = true;
  MOVING.push(this);
  if (MOVING.length == 1) {
    setTimeout("moveCards()", 10);
  }
}

Card.prototype.updateDisplay = function() {
  var deltaX = (this.destX-this.x);
  var newX   = this.x + this.dx;
  var register = false;
  if (deltaX < 0) {
    if (((deltaX*1.01) <= this.dx) &&
        ((deltaX*0.99) >= this.dx)) {
      newX = this.destX;
    } else {
      register = true;
    }
  } else {
    if (((deltaX*1.01) >= this.dx) &&
        ((deltaX*0.99) <= this.dx)) {
      newX = this.destX;
    } else {
      register = true;
    }
  }
  var deltaY = (this.destY-this.y);
  var newY   = this.y + this.dy;
  if (deltaY < 0) {
    if (((deltaY*1.01) <= this.dy) &&
        ((deltaY*0.99) >= this.dy)) {
      newY = this.destY;
    } else {
      register = true;
    }
  } else {
    if (((deltaY*1.01) >= this.dy) &&
        ((deltaY*0.99) <= this.dy)) {
      newY = this.destY;
    } else {
      register = true;
    }
  }
  this.setPos(newX, newY);
  if (!register) {
    var nc = this.pile.nextCard(this);
    while (nc) {
      var ncelem = nc?nc.elem:null;
      if (ncelem.parentNode == this.pile.g) {
        this.pile.g.insertBefore(this.elem, ncelem);
        break;
      }
      nc = this.pile.nextCard(nc);
    }
    if (!nc) 
      this.pile.g.appendChild(this.elem);
  }
  this.moving = register;
  return register;
}

function moveCards() {
  var register = false;
  var num = MOVING.length;
  for (var i=0; i<num; i++) {
    var card = MOVING.shift();
    if (card.updateDisplay()) {
      MOVING.push(card);
      register = true;
    } else {
    }
  }
  if (register) {
    setTimeout("moveCards()", 10);
  }
}
