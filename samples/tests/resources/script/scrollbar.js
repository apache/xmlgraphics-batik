/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

var svgNS = "http://www.w3.org/2000/svg";
var xlinkNS = "http://www.w3.org/1999/xlink";

function ScrollBar(contentId, 
                   steps,
                   scrollBarX,
                   scrollBarY,
                   scrollBarHeight,
                   scrolledHeight,
                   arrowWidth,
                   upArrowId, downArrowId, 
                   cursorId, cursorHeight){
    this.content = document.getElementById(contentId);
    this.steps = steps;
    this.curStep = 0;
    this.scrollBarHeight = scrollBarHeight;
    this.scrolledHeight = scrolledHeight;
    this.arrowWidth = arrowWidth;
    this.cursorHeight = cursorHeight;

    //
    // First, add the elements which make up the scroll bar
    //
    var insertAt = this.content.getParentNode().getParentNode();

    //
    // Add the up arrow
    //
    var upArrow = document.createElementNS(svgNS, 'use');
    upArrow.setAttributeNS(xlinkNS, 'href', upArrowId);
    upArrow.setAttributeNS(null, 'x', scrollBarX);
    upArrow.setAttributeNS(null, 'y', scrollBarY);
    insertAt.appendChild(upArrow);
        
    //
    // Add the down arrow
    //
    var downArrow = document.createElementNS(svgNS, 'use');
    downArrow.setAttributeNS(xlinkNS, 'href', downArrowId);
    downArrow.setAttributeNS(null, 'x', scrollBarX);
    downArrow.setAttributeNS(null, 'y', scrollBarY + scrollBarHeight - arrowWidth);
    insertAt.appendChild(downArrow);

    //
    // Add the cursor
    //
    var cursor = document.createElementNS(svgNS, 'use');
    cursor.setAttributeNS(xlinkNS, 'href', cursorId);
    cursor.setAttributeNS(null, 'x', scrollBarX);
    cursor.setAttributeNS(null, 'y', scrollBarY + arrowWidth);

    this.cursor = document.createElementNS(svgNS, 'g');
    this.cursor.appendChild(cursor);
    insertAt.appendChild(this.cursor);

    //
    // Now, add event handling to scroll the content 
    //

    // Scrolling down means moving the cursor up and 
    // content down (i.e., towards the positive side
    // of the Y axis).          


    this.scrollToStep = function(newStep){
        //
        // Compute new position for cursor
        //
        var cursorPos = newStep*((this.scrollBarHeight - 2*this.arrowWidth - this.cursorHeight) / this.steps);
        this.cursor.setAttributeNS(null, "transform", "translate(0," + cursorPos + ")");
       
        //
        // Compute new content position
        //
        var contentPos = - newStep*(this.scrolledHeight / this.steps);
        this.content.setAttributeNS(null, "transform", "translate(0," + contentPos + ")");

        this.curStep = newStep;
    }


    // Scrolling up means moving the cursor down and
    // the content up (i.e., towards the negative side
    // of the Y axis).
    this.handleScrollUp = function(evt) {
        if (this.curStep < (this.steps - 1)){
            this.scrollToStep(this.curStep + 1);
        }
    }

    this.handleScrollDown = function(evt) {
        if (this.curStep > 0){
            this.scrollToStep(this.curStep - 1);
        }
    }

    this.getHandleScrollDownFunction = function() {
        var thisObject = this;
        return function(evt) { thisObject.handleScrollDown(evt); }
    }

    this.getHandleScrollUpFunction = function() {
        var thisObject = this;
        return function(evt) { thisObject.handleScrollUp(evt); }
    }

    upArrow.addEventListener('click', this.getHandleScrollDownFunction(), false);
    downArrow.addEventListener('click', this.getHandleScrollUpFunction(), false);

}

