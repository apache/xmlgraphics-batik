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

function Board(boardGroup, moveGroup, winG, helpG) {
  this.doc = boardGroup.getOwnerDocument();
  this.root = this.doc.getRootElement();
  this.helpG = (helpG)?helpG:this.doc.getElementById("help");
  this.winG  = (winG)?winG:this.doc.getElementById("win");
  this.boardGroup = boardGroup;
  this.moveGroup = moveGroup;
  this.thePiles = new Array();
  this.moves = new Array();
  this.moveIndex = 0;
  this.numMoves = 0;
  this.isWon = false;

  this.root.addEventListener("keypress", this, false);

  if (helpG) {
    var board = this;
    this.helpG.addEventListener("click", function() { board.hideHelp(); }, false);
  }
}

Board.prototype.won = function() {
  if (!this.winG) return;
  var style = this.winG.style;
  if (style.getPropertyValue("display") == "inline") {
    this.unwon();
    return;
  }
  style.setProperty("display", "inline", "");
  this.isWon = true;
};
  
Board.prototype.unwon = function() {
  if (!this.winG) return;
  if (this.winG.style.getPropertyValue("display") != "none") {
    this.winG.style.setProperty("display", "none", "");
  }
};

Board.prototype.setNotifyMoveDone = function(moveDone) {
  this.moveDone = moveDone;
};

Board.prototype.notifyMoveDone = function() {
  if (this.moveDone) {
    this.moveDone();
  }
};

Board.prototype.saveMove = function(moveinfo) {
  this.moves[this.moveIndex++] = moveinfo;
  this.numMoves = this.moveIndex;
  this.isWon = false;
};

Board.prototype.setMoveInfo = function(moveinfo) {
  if (this.moveIndex == 0) return null;
  this.moves[this.moveIndex-1] = moveinfo;
};
  
Board.prototype.getMoveInfo = function() {
  if (this.moveIndex == 0) return null;
  return this.moves[this.moveIndex-1];
};

Board.prototype.undoMove = function() {
  if (this.moveIndex == 0) return;
  if (this.isWon)          this.unwon();

  this.moveIndex--;
  var mi = this.moves[this.moveIndex];
  mi.undo();
};

Board.prototype.redoMove = function() {
  if (this.moveIndex == this.numMoves) return;

  var mi = this.moves[this.moveIndex++];
  mi.redo();

  if (this.isWon && (this.moveIndex == this.numMoves)) 
    this.won();
};

Board.prototype.handleEvent = function(evt) {
  var keycode = evt.keyCode ? evt.keyCode :
  evt.charCode ? evt.charCode :
  evt.which ? evt.which : void 0;
  var key = String.fromCharCode(keycode);
  if ((key == "z") || (key == "Z")) {
    this.undoMove();
  } else if ((key == "r") || (key == "R")) {
    this.redoMove();
  } else if (key == "?") {
    this.showHelp();
  }
};

Board.prototype.showHelp = function() {
  if (!this.helpG) return;
  var style = this.helpG.style;
  if (style.getPropertyValue("display") == "inline") {
    this.hideHelp();
    return;
  }
  style.setProperty("display", "inline", "");
  style.setProperty("pointer-events", "fill", "");
  var board = this;
}

Board.prototype.hideHelp = function() {
  if (!this.helpG) return;
  this.helpG.style.setProperty("pointer-events", "none", "");
  this.helpG.style.setProperty("display", "none", "");
}

function MultiMoveInfo(mi1, mi2) {
  this.mi1 = mi1;
  this.mi2 = mi2;
  this.undo = function() {
    this.mi2.undo();
    this.mi1.undo();
  };
  this.redo = function() {
    this.mi1.redo();
    this.mi2.redo();
  };
}

function SimpleMoveInfo(fromCards, fromPile, toCards, toPile) {
  this.fromCards = fromCards;
  this.fromPile = fromPile;
  this.toCards = toCards;
  this.toPile = toPile;
  
  this.undo = function() {
    var len = fromCards.length;
    for (var i=0; i<len; i++) {
      fromPile.moveCardTo(fromCards[i], 40, 1);
    }
  };
  this.redo = function() {
    var len = toCards.length;
    for (var i=0; i<len; i++) {
      toPile.moveCardTo(toCards[i], 40, 1);
    }
  };
}
