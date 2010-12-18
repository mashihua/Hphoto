// apply inline-box only for mozilla
if( jQuery.browser.mozilla ) {
	// do when DOM is ready
	$( function() {
		// search form, hide it, search labels to modify, filter classes nocmx and error
		$( 'form.hform' ).hide().find( 'p>label:not(.nocmx):not(.error)' ).each( function() {
			var $this = $(this);
			var labelContent = $this.html();
			var labelWidth = document.defaultView.getComputedStyle( this, '' ).getPropertyValue( 'width' );
			// create block element with width of label
			var labelSpan = $("<span>")
				.css("display", "block")
				.width(labelWidth)
				.html(labelContent);
			// change display to mozilla specific inline-box
			$this.css("display", "-moz-inline-box")
				// remove children
				.empty()
				// add span element
				.append(labelSpan);
		// show form again
		}).end().show();
	});
};

/*
 * Dual licensed under the MIT (http://www.opensource.org/licenses/mit-license.php)
 * and GPL (http://www.opensource.org/licenses/gpl-license.php) licenses.
 *
 * $LastChangedDate: 2007-03-27 16:29:43 -0500 (Tue, 27 Mar 2007) $
 * $Rev: 1601 $
 */

jQuery.fn._height = jQuery.fn.height;
jQuery.fn._width  = jQuery.fn.width;

/**
 * If used on document, returns the document's height (innerHeight)
 * If used on window, returns the viewport's (window) height
 * See core docs on height() to see what happens when used on an element.
 *
 * @example $("#testdiv").height()
 * @result 200
 *
 * @example $(document).height()
 * @result 800
 *
 * @example $(window).height()
 * @result 400
 *
 * @name height
 * @type Object
 * @cat Plugins/Dimensions
 */
jQuery.fn.height = function() {
	if ( this[0] == window )
		return self.innerHeight ||
			jQuery.boxModel && document.documentElement.clientHeight ||
			document.body.clientHeight;

	if ( this[0] == document )
		return Math.max( document.body.scrollHeight, document.body.offsetHeight );

	return this._height(arguments[0]);
};

/**
 * If used on document, returns the document's width (innerWidth)
 * If used on window, returns the viewport's (window) width
 * See core docs on height() to see what happens when used on an element.
 *
 * @example $("#testdiv").width()
 * @result 200
 *
 * @example $(document).width()
 * @result 800
 *
 * @example $(window).width()
 * @result 400
 *
 * @name width
 * @type Object
 * @cat Plugins/Dimensions
 */
jQuery.fn.width = function() {
	if ( this[0] == window )
		return self.innerWidth ||
			jQuery.boxModel && document.documentElement.clientWidth ||
			document.body.clientWidth;

	if ( this[0] == document )
		return Math.max( document.body.scrollWidth, document.body.offsetWidth );

	return this._width(arguments[0]);
};

/**
 * Returns the inner height value (without border) for the first matched element.
 * If used on document, returns the document's height (innerHeight)
 * If used on window, returns the viewport's (window) height
 *
 * @example $("#testdiv").innerHeight()
 * @result 800
 *
 * @name innerHeight
 * @type Number
 * @cat Plugins/Dimensions
 */
jQuery.fn.innerHeight = function() {
	return this[0] == window || this[0] == document ?
		this.height() :
		this.css('display') != 'none' ?
		 	this[0].offsetHeight - (parseInt(this.css("borderTopWidth")) || 0) - (parseInt(this.css("borderBottomWidth")) || 0) :
			this.height() + (parseInt(this.css("paddingTop")) || 0) + (parseInt(this.css("paddingBottom")) || 0);
};

/**
 * Returns the inner width value (without border) for the first matched element.
 * If used on document, returns the document's Width (innerWidth)
 * If used on window, returns the viewport's (window) width
 *
 * @example $("#testdiv").innerWidth()
 * @result 1000
 *
 * @name innerWidth
 * @type Number
 * @cat Plugins/Dimensions
 */
jQuery.fn.innerWidth = function() {
	return this[0] == window || this[0] == document ?
		this.width() :
		this.css('display') != 'none' ?
			this[0].offsetWidth - (parseInt(this.css("borderLeftWidth")) || 0) - (parseInt(this.css("borderRightWidth")) || 0) :
			this.height() + (parseInt(this.css("paddingLeft")) || 0) + (parseInt(this.css("paddingRight")) || 0);
};

/**
 * Returns the outer height value (including border) for the first matched element.
 * Cannot be used on document or window.
 *
 * @example $("#testdiv").outerHeight()
 * @result 1000
 *
 * @name outerHeight
 * @type Number
 * @cat Plugins/Dimensions
 */
jQuery.fn.outerHeight = function() {
	return this[0] == window || this[0] == document ?
		this.height() :
		this.css('display') != 'none' ?
			this[0].offsetHeight :
			this.height() + (parseInt(this.css("borderTopWidth")) || 0) + (parseInt(this.css("borderBottomWidth")) || 0)
				+ (parseInt(this.css("paddingTop")) || 0) + (parseInt(this.css("paddingBottom")) || 0);
};

/**
 * Returns the outer width value (including border) for the first matched element.
 * Cannot be used on document or window.
 *
 * @example $("#testdiv").outerWidth()
 * @result 1000
 *
 * @name outerWidth
 * @type Number
 * @cat Plugins/Dimensions
 */
jQuery.fn.outerWidth = function() {
	return this[0] == window || this[0] == document ?
		this.width() :
		this.css('display') != 'none' ?
			this[0].offsetWidth :
			this.height() + (parseInt(this.css("borderLeftWidth")) || 0) + (parseInt(this.css("borderRightWidth")) || 0)
				+ (parseInt(this.css("paddingLeft")) || 0) + (parseInt(this.css("paddingRight")) || 0);
};

/**
 * Returns how many pixels the user has scrolled to the right (scrollLeft).
 * Works on containers with overflow: auto and window/document.
 *
 * @example $("#testdiv").scrollLeft()
 * @result 100
 *
 * @name scrollLeft
 * @type Number
 * @cat Plugins/Dimensions
 */
jQuery.fn.scrollLeft = function() {
	if ( this[0] == window || this[0] == document )
		return self.pageXOffset ||
			jQuery.boxModel && document.documentElement.scrollLeft ||
			document.body.scrollLeft;

	return this[0].scrollLeft;
};

/**
 * Returns how many pixels the user has scrolled to the bottom (scrollTop).
 * Works on containers with overflow: auto and window/document.
 *
 * @example $("#testdiv").scrollTop()
 * @result 100
 *
 * @name scrollTop
 * @type Number
 * @cat Plugins/Dimensions
 */
jQuery.fn.scrollTop = function() {
	if ( this[0] == window || this[0] == document )
		return self.pageYOffset ||
			jQuery.boxModel && document.documentElement.scrollTop ||
			document.body.scrollTop;

	return this[0].scrollTop;
};

/**
 * Returns the location of the element in pixels from the top left corner of the viewport.
 *
 * For accurate readings make sure to use pixel values for margins, borders and padding.
 *
 * @example $("#testdiv").offset()
 * @result { top: 100, left: 100, scrollTop: 10, scrollLeft: 10 }
 *
 * @example $("#testdiv").offset({ scroll: false })
 * @result { top: 90, left: 90 }
 *
 * @example var offset = {}
 * $("#testdiv").offset({ scroll: false }, offset)
 * @result offset = { top: 90, left: 90 }
 *
 * @name offset
 * @param Object options A hash of options describing what should be included in the final calculations of the offset.
 *                       The options include:
 *                           margin: Should the margin of the element be included in the calculations? True by default.
 *                                   If set to false the margin of the element is subtracted from the total offset.
 *                           border: Should the border of the element be included in the calculations? True by default.
 *                                   If set to false the border of the element is subtracted from the total offset.
 *                           padding: Should the padding of the element be included in the calculations? False by default.
 *                                    If set to true the padding of the element is added to the total offset.
 *                           scroll: Should the scroll offsets of the parent elements be included in the calculations?
 *                                   True by default. When true, it adds the total scroll offsets of all parents to the
 *                                   total offset and also adds two properties to the returned object, scrollTop and
 *                                   scrollLeft. If set to false the scroll offsets of parent elements are ignored.
 *                                   If scroll offsets are not needed, set to false to get a performance boost.
 * @param Object returnObject An object to store the return value in, so as not to break the chain. If passed in the
 *                            chain will not be broken and the result will be assigned to this object.
 * @type Object
 * @cat Plugins/Dimensions
 * @author Brandon Aaron (brandon.aaron@gmail.com || http://brandonaaron.net)
 */
jQuery.fn.offset = function(options, returnObject) {
	var x = 0, y = 0, elem = this[0], parent = this[0], absparent=false, relparent=false, op, sl = 0, st = 0, options = jQuery.extend({ margin: true, border: true, padding: false, scroll: true }, options || {});
	do {
		x += parent.offsetLeft || 0;
		y += parent.offsetTop  || 0;

		// Mozilla and IE do not add the border
		if (jQuery.browser.mozilla || jQuery.browser.msie) {
			// get borders
			var bt = parseInt(jQuery.css(parent, 'borderTopWidth')) || 0;
			var bl = parseInt(jQuery.css(parent, 'borderLeftWidth')) || 0;

			// add borders to offset
			x += bl;
			y += bt;

			// Mozilla removes the border if the parent has overflow property other than visible
			if (jQuery.browser.mozilla && parent != elem && jQuery.css(parent, 'overflow') != 'visible') {
				x += bl;
				y += bt;
			}
			
			// Mozilla does not include the border on body if an element isn't positioned absolute and is without an absolute parent
			if (jQuery.css(parent, 'position') == 'absolute') absparent = true;
			// IE does not include the border on the body if an element is position static and without an absolute or relative parent
			if (jQuery.css(parent, 'position') == 'relative') relparent = true;
		}

		if (options.scroll) {
			// Need to get scroll offsets in-between offsetParents
			op = parent.offsetParent;
			do {
				sl += parent.scrollLeft || 0;
				st += parent.scrollTop  || 0;

				parent = parent.parentNode;

				// Mozilla removes the border if the parent has overflow property other than visible
				if (jQuery.browser.mozilla && parent != elem && parent != op && jQuery.css(parent, 'overflow') != 'visible') {
					x += parseInt(jQuery.css(parent, 'borderLeftWidth')) || 0;
					y += parseInt(jQuery.css(parent, 'borderTopWidth')) || 0;
				}
			} while (op && parent != op);
		} else
			parent = parent.offsetParent;

		if (parent && (parent.tagName.toLowerCase() == 'body' || parent.tagName.toLowerCase() == 'html')) {
			// Safari and IE Standards Mode doesn't add the body margin for elments positioned with static or relative
			if ((jQuery.browser.safari || (jQuery.browser.msie && jQuery.boxModel)) && jQuery.css(elem, 'position') != 'absolute') {
				x += parseInt(jQuery.css(parent, 'marginLeft')) || 0;
				y += parseInt(jQuery.css(parent, 'marginTop'))  || 0;
			}
			// Mozilla does not include the border on body if an element isn't positioned absolute and is without an absolute parent
			// IE does not include the border on the body if an element is positioned static and without an absolute or relative parent
			if ( (jQuery.browser.mozilla && !absparent) || 
			     (jQuery.browser.msie && jQuery.css(elem, 'position') == 'static' && (!relparent || !absparent)) ) {
				x += parseInt(jQuery.css(parent, 'borderLeftWidth')) || 0;
				y += parseInt(jQuery.css(parent, 'borderTopWidth'))  || 0;
			}
			break; // Exit the loop
		}
	} while (parent);

	if ( !options.margin) {
		x -= parseInt(jQuery.css(elem, 'marginLeft')) || 0;
		y -= parseInt(jQuery.css(elem, 'marginTop'))  || 0;
	}

	// Safari and Opera do not add the border for the element
	if ( options.border && (jQuery.browser.safari || jQuery.browser.opera) ) {
		x += parseInt(jQuery.css(elem, 'borderLeftWidth')) || 0;
		y += parseInt(jQuery.css(elem, 'borderTopWidth'))  || 0;
	} else if ( !options.border && !(jQuery.browser.safari || jQuery.browser.opera) ) {
		x -= parseInt(jQuery.css(elem, 'borderLeftWidth')) || 0;
		y -= parseInt(jQuery.css(elem, 'borderTopWidth'))  || 0;
	}

	if ( options.padding ) {
		x += parseInt(jQuery.css(elem, 'paddingLeft')) || 0;
		y += parseInt(jQuery.css(elem, 'paddingTop'))  || 0;
	}

	// Opera thinks offset is scroll offset for display: inline elements
	if (options.scroll && jQuery.browser.opera && jQuery.css(elem, 'display') == 'inline') {
		sl -= elem.scrollLeft || 0;
		st -= elem.scrollTop  || 0;
	}

	var returnValue = options.scroll ? { top: y - st, left: x - sl, scrollTop:  st, scrollLeft: sl }
	                                 : { top: y, left: x };

	if (returnObject) { jQuery.extend(returnObject, returnValue); return this; }
	else              { return returnValue; }
};


/*
 * jQuery corner plugin
 *
 * version 1.7 (1/26/2007)
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 */

/**
 * The corner() method provides a simple way of styling DOM elements.  
 *
 * corner() takes a single string argument:  $().corner("effect corners width")
 *
 *   effect:  The name of the effect to apply, such as round or bevel. 
 *            If you don't specify an effect, rounding is used.
 *
 *   corners: The corners can be one or more of top, bottom, tr, tl, br, or bl. 
 *            By default, all four corners are adorned. 
 *
 *   width:   The width specifies the width of the effect; in the case of rounded corners this 
 *            will be the radius of the width. 
 *            Specify this value using the px suffix such as 10px, and yes it must be pixels.
 *
 * For more details see: http://methvin.com/jquery/jq-corner.html
 * For a full demo see:  http://malsup.com/jquery/corner/
 *
 *
 * @example $('.adorn').corner();
 * @desc Create round, 10px corners 
 *
 * @example $('.adorn').corner("25px");
 * @desc Create round, 25px corners 
 *
 * @example $('.adorn').corner("notch bottom");
 * @desc Create notched, 10px corners on bottom only
 *
 * @example $('.adorn').corner("tr dog 25px");
 * @desc Create dogeared, 25px corner on the top-right corner only
 *
 * @example $('.adorn').corner("round 8px").parent().css('padding', '4px').corner("round 10px");
 * @desc Create a rounded border effect by styling both the element and its parent
 * 
 * @name corner
 * @type jQuery
 * @param String options Options which control the corner style
 * @cat Plugins/Corner
 * @return jQuery
 * @author Dave Methvin (dave.methvin@gmail.com)
 * @author Mike Alsup (malsup@gmail.com)
 */
jQuery.fn.corner = function(o) {
    function hex2(s) {
        var s = parseInt(s).toString(16);
        return ( s.length < 2 ) ? '0'+s : s;
    };
    function gpc(node) {
        for ( ; node && node.nodeName.toLowerCase() != 'html'; node = node.parentNode  ) {
            var v = jQuery.css(node,'backgroundColor');
            if ( v.indexOf('rgb') >= 0 ) { 
                rgb = v.match(/\d+/g); 
                return '#'+ hex2(rgb[0]) + hex2(rgb[1]) + hex2(rgb[2]);
            }
            if ( v && v != 'transparent' )
                return v;
        }
        return '#ffffff';
    };
    function getW(i) {
        switch(fx) {
        case 'round':  return Math.round(width*(1-Math.cos(Math.asin(i/width))));
        case 'cool':   return Math.round(width*(1+Math.cos(Math.asin(i/width))));
        case 'sharp':  return Math.round(width*(1-Math.cos(Math.acos(i/width))));
        case 'bite':   return Math.round(width*(Math.cos(Math.asin((width-i-1)/width))));
        case 'slide':  return Math.round(width*(Math.atan2(i,width/i)));
        case 'jut':    return Math.round(width*(Math.atan2(width,(width-i-1))));
        case 'curl':   return Math.round(width*(Math.atan(i)));
        case 'tear':   return Math.round(width*(Math.cos(i)));
        case 'wicked': return Math.round(width*(Math.tan(i)));
        case 'long':   return Math.round(width*(Math.sqrt(i)));
        case 'sculpt': return Math.round(width*(Math.log((width-i-1),width)));
        case 'dog':    return (i&1) ? (i+1) : width;
        case 'dog2':   return (i&2) ? (i+1) : width;
        case 'dog3':   return (i&3) ? (i+1) : width;
        case 'fray':   return (i%2)*width;
        case 'notch':  return width; 
        case 'bevel':  return i+1;
        }
    };
    o = (o||"").toLowerCase();
    var keep = /keep/.test(o);                       // keep borders?
    var cc = ((o.match(/cc:(#[0-9a-f]+)/)||[])[1]);  // corner color
    var sc = ((o.match(/sc:(#[0-9a-f]+)/)||[])[1]);  // strip color
    var width = parseInt((o.match(/(\d+)px/)||[])[1]) || 10; // corner width
    var re = /round|bevel|notch|bite|cool|sharp|slide|jut|curl|tear|fray|wicked|sculpt|long|dog3|dog2|dog/;
    var fx = ((o.match(re)||['round'])[0]);
    var edges = { T:0, B:1 };
    var opts = {
        TL:  /top|tl/.test(o),       TR:  /top|tr/.test(o),
        BL:  /bottom|bl/.test(o),    BR:  /bottom|br/.test(o)
    };
    if ( !opts.TL && !opts.TR && !opts.BL && !opts.BR )
        opts = { TL:1, TR:1, BL:1, BR:1 };
    var strip = document.createElement('div');
    strip.style.overflow = 'hidden';
    strip.style.height = '1px';
    strip.style.backgroundColor = sc || 'transparent';
    strip.style.borderStyle = 'solid';
    return this.each(function(index){
        var pad = {
            T: parseInt(jQuery.css(this,'paddingTop'))||0,     R: parseInt(jQuery.css(this,'paddingRight'))||0,
            B: parseInt(jQuery.css(this,'paddingBottom'))||0,  L: parseInt(jQuery.css(this,'paddingLeft'))||0
        };

        if (jQuery.browser.msie) this.style.zoom = 1; // force 'hasLayout' in IE
        if (!keep) this.style.border = 'none';
        strip.style.borderColor = cc || gpc(this.parentNode);
        var cssHeight = jQuery.curCSS(this, 'height');

        for (var j in edges) {
            var bot = edges[j];
            strip.style.borderStyle = 'none '+(opts[j+'R']?'solid':'none')+' none '+(opts[j+'L']?'solid':'none');
            var d = document.createElement('div');
            var ds = d.style;

            bot ? this.appendChild(d) : this.insertBefore(d, this.firstChild);

            if (bot && cssHeight != 'auto') {
                if (jQuery.css(this,'position') == 'static')
                    this.style.position = 'relative';
                ds.position = 'absolute';
                ds.bottom = ds.left = ds.padding = ds.margin = '0';
                if (jQuery.browser.msie)
                    ds.setExpression('width', 'this.parentNode.offsetWidth');
                else
                    ds.width = '100%';
            }
            else {
                ds.margin = !bot ? '-'+pad.T+'px -'+pad.R+'px '+(pad.T-width)+'px -'+pad.L+'px' : 
                                    (pad.B-width)+'px -'+pad.R+'px -'+pad.B+'px -'+pad.L+'px';                
            }

            for (var i=0; i < width; i++) {
                var w = Math.max(0,getW(i));
                var e = strip.cloneNode(false);
                e.style.borderWidth = '0 '+(opts[j+'R']?w:0)+'px 0 '+(opts[j+'L']?w:0)+'px';
                bot ? d.appendChild(e) : d.insertBefore(e, d.firstChild);
            }
        }
    });
};

if(!hphoto){
	var hphoto={};
	(function(){
		var prams={};
		var o={};
		var _n = $("#sufs");
		function l(){var lct=document.location;return lct.href.substr(0,lct.href.indexOf("?")!=-1?lct.href.indexOf("?"):lct.href.length);}
		function i(){var h=document.location.search;if(h.length<3)return;h=h.substr(1);var p=h.split(/&/);for(i=0;i<p.length;i++){var parm=p[i].split(/=/);prams[parm[0]]=parm[1];}}
		function s(n){return prams[n];}
		function w(c,a){if(c=="script"){document.write('<script src="'+a+'" type="text/javascript"><\/script>');}else if(c=="css"){document.write('<link href="'+a+'" type="text/css" rel="stylesheet"></link>')}}
		function e(c,a){var b=c.split(/\./);var d=window;for(var e=0;e<b.length-1;e++){d=d[b[e]]}d[b[b.length-1]]=a}
		function x(e){var x=e.pageX||(e.clientX+(document.documentElement.scrollLeft||document.body.scrollLeft))||0;var y=e.pageY||(e.clientY+(document.documentElement.scrollTop||document.body.scrollTop))||0;return{x:x,y:y};}
		function m(e,c){var p=x(e);if((p.x>c.l&&p.x<c.r)&&(p.y>c.t&&p.y<c.b)){return true;}return false;}
		function t(){if(arguments[0]){_n=$(arguments[0]);}else{throw new Error("no arguments!")}$(document).bind('mousemove',a);}
		function u(){$(document).unbind('mousemove',a);}
		function a(e){var h=false;for(j in o){if(m(e,o[j])){h=false;break;}else{h=true;}}if(h){_n.hide();u();}else{_n.show();}}
		function b(i){o=i;}
		function sc(n,v){var av=arguments;var ac=arguments.length;var exp=(ac>2)?av[2]:null;var p=(ac>3)?av[3]:null;var d=(ac>4)?av[4]:null;var s=(ac>5)?av[5]:false;document.cookie=n+"="+v+"; expires="+(exp?exp:"")+(s?("; domain="+d):"")+(p?("; path="+p):"")+(s?"; secure":"");}
		function gc(n){var arg=n+"=";var alen=arg.length;var clen=document.cookie.length;var i=0;while(i<clen){var j=i+alen;if(document.cookie.substring(i,j)==arg){return gv(j);}i=document.cookie.indexOf(" ",i)+1;if(i==0)break;}return "";}
		function gv(o){var e=document.cookie.indexOf(";",o);if(e==-1){e=document.cookie.length;}return unescape(document.cookie.substring(o,e));}function dc(n,p,d){if(gc(n)){document.cookie=n+"="+(p?"; path="+p:"")+(d?"; domain="+d:"")+"; expires=Thu, 01-Jan-70 00:00:01 GMT";}}
		function cl(f){return $(f)}
		function nt(d){
			$(function() {
				if(gc(d.cookie) != "false"){
					var options = $(d.element).offset({scroll: true});				
					var s = '<div class="generic_dialog contextual_dialog"><div class="generic_dialog_popup"><div class="contextual_arrow_rev" style="background-position: 95% 50%;"><span>^_^Hehe</span></div><div class="contextual_dialog_content"><h2><span>'
					+(d.title||"^_^Hehe")+'</span></h2><div class="dialog_content"><div class="dialog_body">'
					+(d.content||'^_^Hehe')
					+'</div><div id="dialog_buttons" class="dialog_buttons"><input type="button" id="dialog_button" value="Okay" class="inputsubmit"/></div></div></div></div></div>';
					$(s).appendTo("body");
					var _this = $(".generic_dialog_popup");
					options.top = options.top + $(d.element).outerHeight() + 3;
					options.left = $("body").outerWidth() - _this.outerWidth() - 10;				
					_this.css({top:options.top+"px",left:options.left+"px"});
					function c(){
							sc(d.cookie,"false",new Date(2030,1,1),"/");
							$(".contextual_dialog").remove();
					}
					$("#dialog_button").click(c);
					$(d.element).click(c);
				}	
			});
		}
		e("hphoto.notify",t);
		e("hphoto.getLocalcation",l);
		e("hphoto.getQuery",s);
		e("hphoto.getCookie",gc);
		e("hphoto.setCookie",sc);
		e("hphoto.delteCookie",dc);
		e("hphoto.appendScript",w);
		e("hphoto.setNotifyArea",b);
		e("hphoto.destroyNotify",u);
		e("hphoto.callBack",cl);
		e("hphoto.tip",nt);
		e("symbolTable",e);	
		i();
	})()
}

$(function() {
	$("a").filter(function(index) {
  		return $(this).attr("href") == "#" && $(this).attr("id") != "infoToogle";
	}).click( function() { alert("Not completed!"); return false;} );	
});



		
		