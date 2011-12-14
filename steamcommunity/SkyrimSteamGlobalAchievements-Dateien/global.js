
function addEvent(el, ev, fn, useCapture)
{
	if(el.addEventListener)
	{
		el.addEventListener(ev, fn, useCapture);
	}
	else if(el.attachEvent)
	{
		var ret = el.attachEvent("on"+ev, fn);
		return ret;
	}
	else
	{
		el["on"+ev] = fn;
	}
}
function fixFloatHeight(floatDiv, fixedDiv, type, topDiv)
{
	floatDivEl = document.getElementById(floatDiv);
	fixedDivEl = document.getElementById(fixedDiv);
	if(floatDivEl && fixedDivEl)
	{
		floatHeight = floatDivEl.offsetHeight;
		fixedHeight = fixedDivEl.offsetHeight;
		if(type == 1)
		{
			if(topDiv)
			{
				floatHeight += document.getElementById(topDiv).offsetHeight;
			}
			if(floatHeight > fixedHeight)
			{
				fixedDivEl.style.height = floatHeight+'px';
			}
		}
		else
		{
			if(fixedHeight > floatHeight)
			{
				floatDivEl.style.height = fixedHeight+'px';
			}
		}
	}
}

function setCheck(checkDiv, checkField)
{
	realField = eval('document.loginForm.'+checkField);
	curVal = realField.value;
	if(curVal == 0)
	{
		document.getElementById(checkDiv).style.color = '#909090';
		realField.value = 1;
	}
	else
	{
		document.getElementById(checkDiv).style.color = '#000000';
		realField.value = 0;
	}
}

function createQuery2( postUrl, returnFn, postData )
{
	uid = Math.round(Math.random()*100000);
	rUid = "requester"+uid;
	eval(rUid+" = new xHttpQuery_Post();");
	eval(rUid+".postUrl = postUrl;");
	eval(rUid+".returnFn = returnFn;");
	eval(rUid+".postData = postData;");
	eval(rUid+".selfRef = \""+rUid+"\";");
	eval(rUid+".doRequest();");
}
	
var updateInProgress = false;
function xHttpQuery_Post()
{
	this.postUrl = '';
	this.selfRef = '';
	this.postData = '';
	this.dataEncoded = false;
	this.returnFn = false;
	this.doRequest = function()
	{
		if ( updateInProgress == true )
		{
			setTimeout( this.selfRef + ".doRequest()", 200 );
			return;
		}
		if ( this.dataEncoded == false )
		{
			var pairs = [];
			var regexp = /%20/g;
			for ( var name in this.postData )
			{
				var value = this.postData[name].toString();
				var pair = encodeURIComponent( name ).replace( regexp, '+' ) + '=' + encodeURIComponent( value ).replace( regexp, '+' );
				pairs.push( pair );
			}
			this.postData = pairs.join( '&' );
			this.dataEncoded = true;
		}
		updateInProgress = true;
		if ( window.XMLHttpRequest )
		{
			req = new XMLHttpRequest();
		}
		else if( window.ActiveXObject )
		{
			req = new ActiveXObject( "Microsoft.XMLHTTP" );
		}
		if ( req )
		{
			req.open( "POST", this.postUrl, true );
			req.onreadystatechange = this.returnFn;
			req.setRequestHeader( "Content-Type", "application/x-www-form-urlencoded" );
			req.setRequestHeader( "Content-Length", this.postData.length );
			req.send( this.postData );
		}
	}
}

function GetNameHistory( a_el )
{
	//createQuery2( profileURL + '/namehistory/', ReceiveNameHistory, { "json": 1 } );
	steamId = a_el.id.replace( /namehistory_link_/, '' );
//	alert( steamId );
	$('namehistory_'+steamId).show();
	//alert( a_el.id + "\n" + a_el.href );
}

function winDim(wh, vs)
{
	if(window.innerWidth) // most browsers - ff, safari, etc
	{
		return (wh == 'w' ? (vs == 'v' ? window.innerWidth : window.pageXOffset) : (vs == 'v' ? window.innerHeight : window.pageYOffset));
	}
	else if(document.documentElement && document.documentElement.clientWidth) // ie strict
	{
		return (wh == 'w' ? (vs == 'v' ? document.documentElement.clientWidth : document.documentElement.scrollLeft) : (vs == 'v' ? document.documentElement.clientHeight : document.documentElement.scrollTop));
	}
	else // ie normal
	{
		return (wh == 'w' ? (vs == 'v' ? document.body.clientWidth : document.body.scrollLeft) : (vs == 'v' ? document.body.clientHeight : document.body.scrollTop));
	}
}

function getGoodElement(el,nn,cn,next)
{
	if(next == 1)
	{
		el = el.parentNode;
	}
	while( el.nodeName && el.nodeName.toLowerCase() != nn && el.nodeName.toLowerCase() != "body")
	{
		el = el.parentNode;
	}
	thisClass = ' '+el.className+' ';
	if( el.nodeName && el.nodeName.toLowerCase() != "body" && thisClass.indexOf(' '+cn+' ') == -1)
	{
		return getGoodElement(el,nn,cn,1);
	}
	else if(thisClass.indexOf(' '+cn+' ') != -1)
	{
		return el;
	}
	return false;
}
function addGameActions()
{
	if(!document.getElementsByTagName)
	{
		return;
	}
	var pageDivs = document.getElementsByTagName("div");
	for(var x = 0; x < pageDivs.length; x++)
	{
		tempClassName = " "+pageDivs[x].className+" ";
		tempParentClassName = " "+pageDivs[x].parentNode.className+" ";
		if(tempClassName.indexOf(" gameContainer ") != -1 || tempParentClassName.indexOf(" gameContainer ") != -1)
		{
			addEvent(pageDivs[x], "mouseover", listItem_hilite, false);
			addEvent(pageDivs[x], "mouseout", listItem_lolite, false);
			addEvent(pageDivs[x], "click", listItem_toggle, false);
		}
	}
}

function getPopPos(e, pw, ph, offset)
{
	w = winDim('w','v');
	h = winDim('h','v');
	sl = winDim('w','s');
	st = winDim('h','s');
	// mouse x/y within viewport
	vmX = e.clientX;
	vmY = e.clientY;
	// mouse x/y within document
	smX = vmX + sl;
	smY = vmY + st;
	l = (pw > vmX) ? (smX + offset) : (smX - pw - offset);
	t = (ph > vmY) ? (smY + offset) : (smY - ph - offset);
	popTL = new Array(t, l);
	return popTL;
}

var keepTooltip = false;
function tooltipCreate(tipEl, e)
{
	ttEl = document.getElementById('tooltip');
	if(ttEl)
	{
		ttEl.parentNode.removeChild(ttEl);
	}
	ttEl = document.createElement('div');
	ttEl.id = 'tooltip';
	ttEl.style.position = 'absolute';
	ttEl.appendChild(tipEl);
	document.getElementsByTagName('body')[0].appendChild(ttEl);
	tipTL = getPopPos(e, ttEl.clientWidth, ttEl.clientHeight, 6);
	ttEl.style.top = tipTL[0] + 'px';
	ttEl.style.left = tipTL[1] + 'px';
}

function tooltipDestroy(go)
{
	if ( go != 1 )
	{
		setTimeout( "tooltipDestroy(1)", 10 );
	}
	else
	{
		ttEl = document.getElementById('tooltip');
		if(ttEl)
		{
			ttEl.parentNode.removeChild(ttEl);
		}
	}
}

function getElement( elementId )
{
	var elem;
	if ( document.getElementById ) // standard compliant method
		elem = document.getElementById( elementId )
	else if ( document.all ) // old msie versions
		elem = document.all[ elementId ]
	else
		elem = false;

	return elem;
}

function setImage( elementId, strImage )
{
	var imageElem = getElement( elementId );
	if ( !imageElem ) 
		return;

	imageElem.src = strImage;
}

function iSwapFullURL( imgID, newImg )
{
	newImgPath = newImg;
	setImage( imgID, newImgPath );
}


function iSwap( imgID, newImg )
{
	newImgPath = "http://cdn.steamcommunity.com/public/images/" + newImg;
	setImage( imgID, newImgPath );
}

function setTimezoneCookies()
{
	var now = new Date();
	var expire = new Date();

	// One year expiration, this way we don't need to wait at least one page
	// load to have accurate timezone info each session, but only each time the user
	// comes with cleared cookies
	expire.setTime( now.getTime() + 3600000*24*365 );
	tzOffset = now.getTimezoneOffset() * -1 * 60;
	isDST = 0;
	document.cookie = "timezoneOffset=" + tzOffset + "," + isDST + ";expires="+expire.toGMTString() + ";path=/";
}


// We always want to have the timezone cookie set for PHP to use
setTimezoneCookies();

function toggleAbuse()
{
	abuseDiv = document.getElementById( 'reporter' );
	if ( abuseDiv.style.display != 'block' )
	{
		abuseDiv.style.display = 'block';
	}
	else
	{
		abuseDiv.style.display = 'none';
	}
}

function setupSteamLinks()
{
	anchors = document.getElementsByTagName( 'a' );
	for( x = 0; x < anchors.length; x++ )
	{
		thisA = anchors[x];
		tmpClass = ' ' + thisA.className + ' ';
		if ( tmpClass.indexOf( ' steamLink ' ) != -1 )
		{
			addEvent( thisA, 'mouseover', showSteamTooltip, false );
			addEvent( thisA, 'mouseout', tooltipDestroy, false );
		}
	}
}

function showSteamTooltip(e)
{
	var srcEl = window.event ? window.event.srcElement : e ? e.target : null;
	if(!srcEl)
	{
		return;
	}
	linkA = getGoodElement(srcEl, 'a', 'steamLink', 0);
	if(!linkA)
	{
		return;
	}
	thisTip = document.createElement('div');
	thisTip.className = 'steamTooltip';
	thisTip.style.width = '32px';
	thisTip.style.height = '32px';
	steamImg = document.createElement( 'img' );
	steamImg.src = 'http://cdn.steamcommunity.com/public/images/skin_1/steamTooltip.jpg';
	steamImg.style.border = 'none';
	steamImg.style.width = '32px';
	steamImg.style.height = '32px';
	thisTip.appendChild(steamImg);

	tooltipCreate(thisTip, e);
}

function setupFriendBlockHoverIE6()
{
	if ( vIE() != 6 )
	{
		return;
	}
	if ( document.getElementById( 'friendBlocks' ) )
	{
		allDivs = document.getElementById( 'friendBlocks' ).getElementsByTagName( 'div' );
		for( x = 0; x < allDivs.length; x++ )
		{
			div = allDivs[x];
			if ( div.className.indexOf( 'friendBlock_' ) != -1 )
			{
				addEvent( div, 'mouseover', friendBlockHighlightIE6, false );
				addEvent( div, 'mouseout', friendBlockLolightIE6, false );
			}
		}
	}
}

var currentHigh = false;
var waitEl = false;
function friendBlockHighlightIE6()
{
	var srcEl = window.event ? window.event.srcElement : null;
	if(!srcEl)
	{
		return;
	}
	var friendClasses = new Array( 'friendBlock_offline', 'friendBlock_online', 'friendBlock_in-game', 'friendBlock_ignored' );
	for ( x = 0; x < friendClasses.length; x++ )
	{
		var thisClass = friendClasses[x];
		div = getGoodElement( srcEl, 'div', thisClass, 0 );
		if ( div )
		{
			break;
		}
	}
	if(!div)
	{
		return;
	}
	div.className = thisClass + ' friendHighlight';
	currentHigh = div;
}

function friendBlockLolightIE6( )
{
	var srcEl = window.event ? window.event.srcElement : null;
	if(!srcEl)
	{
		return;
	}
	var friendClasses = new Array( 'friendBlock_offline', 'friendBlock_online', 'friendBlock_in-game', 'friendBlock_ignored' );
	for ( x = 0; x < friendClasses.length; x++ )
	{
		var thisClass = friendClasses[x];
		div = getGoodElement( srcEl, 'div', thisClass, 0 );
		if ( div )
		{
			break;
		}
	}
	if(!div)
	{
		return;
	}
	div.className = thisClass;
}

function vIE()
{
	return (navigator.appName=='Microsoft Internet Explorer') ? parseFloat( ( new RegExp( "MSIE ([0-9]{1,}[.0-9]{0,})" ) ).exec( navigator.userAgent )[1] ) : -1;
}

function checkAbuseSub()
{
	if ( !document.getElementById( 'contentType2' ).checked && !document.getElementById( 'contentType3' ).checked && !document.getElementById( 'contentType4' ).checked && !document.getElementById( 'contentType13' ).checked  )
	{
		alert( 'Please select a reason for reporting abuse' );
		return false;
	}
	document.getElementById( 'abuseForm' ).submit();
}

// register some events to dismiss popup (ie, user clicking elsewhere on the window, escape)
//   cleans up event binds afterwards.  clicks to children of "elemIgnore" will not dismiss popup
function RegisterPopupDismissal( dismissFunc, elemIgnore, bNoGuard )
{
	var dismissHandler = {
		guard: bNoGuard ? 0 : 1,
		dismiss: function( event ) {
			if ( this.elemIgnore )
			{
				var elem = Event.element( event );
				if ( elem.up( '#' + elemIgnore.id ) )
					return;
			}
			// ignore the first click- assume it's the one starting the popup
			if ( this.guard-- > 0 )
				return;
			this.regFunc();
			this.unregister();
		},
		unregister: function() {
			Event.stopObserving( document, 'click', this.boundHandler );
			Event.stopObserving( document, 'keydown', this.boundHandler );
		}
	};
	dismissHandler.regFunc = dismissFunc;
	dismissHandler.elemIgnore = elemIgnore || null;
	dismissHandler.boundHandler = dismissHandler.dismiss.bindAsEventListener( dismissHandler );
	Event.observe( document, 'click', dismissHandler.boundHandler );
	Event.observe( document, 'keydown', dismissHandler.boundHandler );

	return dismissHandler;

}

function ShowMenu( elemLink, elemPopup, align, valign )
{
	var elemLink = $(elemLink);
	var elemPopup = $(elemPopup);

	AlignMenu( elemLink, elemPopup, align, valign );

	ShowWithFade( elemPopup );
	Element.addClassName.defer(elemLink,'focus');
	RegisterPopupDismissal( function() { HideWithFade( elemPopup ); elemLink.removeClassName('focus'); }, elemPopup );
}

function HideMenu( elemLink, elemPopup )
{
	HideWithFade( elemPopup );
	elemLink.removeClassName( 'focus' );
	if ( elemLink.dismissHandler )
		elemLink.dismissHandler.unregister();
}

function RegisterFlyout( elemLink, elemPopup, align, valign )
{
	Event.observe( elemLink, 'mouseover', function(event) { FlyoutMenu( elemLink, elemPopup, align, valign ); } );

	Event.observe( elemLink, 'mouseout', HideFlyoutMenu.bindAsEventListener( null, elemLink, elemPopup ) );
	Event.observe( elemPopup, 'mouseout', HideFlyoutMenu.bindAsEventListener( null, elemLink, elemPopup ) );

}

function FlyoutMenu( elemLink, elemPopup, align, valign )
{
	var elemLink = $(elemLink);
	var elemPopup = $(elemPopup);

	if ( !elemPopup.visible() || elemPopup.hiding )
	{
		AlignMenu( elemLink, elemPopup, align, valign );
		ShowWithFade( elemPopup );
		elemLink.addClassName('focus');
	}

}

function HideFlyoutMenu( event, elemLink, elemPopup )
{
	var elemLink = $(elemLink);
	var elemPopup = $(elemPopup);
	var reltarget = (event.relatedTarget) ? event.relatedTarget : event.toElement;
	if ( !reltarget || ( $(reltarget).up( '#' + elemLink.id ) || $(reltarget).up( '#' + elemPopup.id )  ) )
		return;

	// start hiding in a little bit, have to let the fade in animation start before we can cancel it
	window.setTimeout( HideWithFade.bind( null, elemPopup ), 33 );
	elemLink.removeClassName('focus');
}


function BShouldSuppressFades()
{
	if ( Prototype.Browser.IE )
	{
		var ieVer =parseInt(navigator.userAgent.substring(navigator.userAgent.indexOf("MSIE")+5));
		return ieVer < 9;
	}
	return false;
}

function ShowWithFade( elem )
{
	var elem = $(elem);

	if ( !elem.visible() || elem.hiding )
	{
		elem.hiding = false;
		if ( elem.effect )
			elem.effect.cancel();
		
		if ( BShouldSuppressFades() )
		{
			elem.addClassName( 'suppress_shadow' );
			elem.effect = new Effect.Appear( elem, { duration: 0.2, afterFinish: function() { elem.removeClassName( 'suppress_shadow' ); } } );
		}
		else
		{
			elem.effect = new Effect.Appear( elem, { duration: 0.2 } );
		}
	}
}

function HideWithFade( elem )
{
	var elem = $(elem);
	
	if ( elem.visible() && !elem.hiding )
	{
		if ( elem.effect && !elem.hiding )
			elem.effect.cancel();
		elem.hiding = true;

		if ( BShouldSuppressFades() )
		{
			elem.addClassName( 'suppress_shadow' );
		}
		elem.effect = new Effect.Fade( elem, { duration: 0.2 } );
	}
}


function AlignMenu( elemLink, elemPopup, align, valign )
{
	var align = align ? align : 'left';

	if ( !valign )
	{
		//if there's not enough room between our spot and the top of the document, we definitely want to drop down
		if ( document.viewport.getScrollOffsets().top + elemLink.viewportOffset().top < nPopupHeight )
			valign = 'bottom';
		else
		{
			// add a little bit of padding so we don't position it flush to an edge if possible
			var nPopupHeight = elemPopup.getHeight() + 8;
			var nSpaceAbove = elemLink.viewportOffset().top;
			var nSpaceBelow = document.viewport.getHeight() - elemLink.viewportOffset().top;
			//otherwise we only want to drop down if we've got enough space below us (measured based on view area)
			// or if there's not enough space above to pop in either direction and there's more space below
			if ( nSpaceBelow > nPopupHeight || ( nSpaceAbove < nPopupHeight && nSpaceBelow > nSpaceAbove ) )
				valign = 'bottom';
			else
				valign = 'top';

		}
	}

	if ( align == 'left' )
	{
		elemPopup.style.left = ( elemLink.positionedOffset()[0] - 12 ) + 'px';
	}
	else if ( align == 'right' )
	{
		elemPopup.style.left = ( elemLink.positionedOffset()[0] + elemLink.getWidth() - elemPopup.getWidth() + 13 ) + 'px';
	}
	else if ( align == 'leftsubmenu' )
	{
		elemPopup.style.left = ( elemLink.positionedOffset()[0] - elemPopup.getWidth() + 12 ) + 'px';
	}
	else if ( align == 'rightsubmenu' )
	{
		elemPopup.style.left = ( elemLink.positionedOffset()[0] + elemLink.getWidth() - 12 ) + 'px';
	}

	if ( valign == 'bottom' )
	{
		elemPopup.style.top = ( elemLink.positionedOffset()[1] + elemLink.getHeight() - 12 ) + 'px';
	}
	else if ( valign == 'top' )
	{
		elemPopup.style.top = ( elemLink.positionedOffset()[1] - elemPopup.getHeight() + 12 ) + 'px';
	}
	else if ( valign == 'bottomsubmenu' )
	{
		elemPopup.style.top = ( elemLink.positionedOffset()[1] - 12 ) + 'px';
	}
}

function abuseSSDescripCheck()
{
	chkd_inap = $('abuseType1').checked;
	chkd_cprt = $('abuseType5').checked;
	if ( chkd_inap )
	{
		$('abuseDescriptionLabel').setStyle( { color: '#777777', fontStyle: 'italic' } );
		$('abuseDescriptionArea').disable();
		$('abuseDescriptionArea').clear();
	}
	else if ( chkd_cprt )
	{
		$('abuseDescriptionLabel').setStyle( { color: '#898989', fontStyle: 'normal' } );
		$('abuseDescriptionArea').enable();
		$('abuseDescriptionArea').focus();
	}
}

// doesn't properly handle cookies with ; in them (needs to look for escape char)
function GetCookie( strCookieName )
{
	var rgMatches = document.cookie.match( '(^|; )' + strCookieName + '=([^;]*)' );
	if ( rgMatches && rgMatches[2] )
		return rgMatches[2];
	else
		return null;
}

function SetCookie( strCookieName, strValue, expiryInDays, path )
{
	if ( !expiryInDays )
		expiryInDays = 0;
	if ( !path )
		path = '/';
	
	var dateExpires = new Date();
	dateExpires.setTime( dateExpires.getTime() + 1000 * 60 * 60 * 24 * expiryInDays );
	document.cookie = strCookieName + '=' + strValue + '; expires=' + dateExpires.toGMTString() + ';path=' + path;
}

function v_trim( str )
{
	if ( str.trim )
		return str.trim();
	else
	{
		return str.replace(/^\s+/, '').replace(/\s+$/, '');
	}
}

// takes an integer
function v_numberformat( n )
{
	var str = '' + ( n ? n : 0 );
	var len = str.length;
	var out = '';
	for ( var i = 0; i < len; i++ )
	{
		out += str.charAt(i);
		if ( i < len - 1 && (len - i - 1) % 3 == 0 )
			out += ',';
	}

	return out;
}

var g_rgCommentThreads = {};
function InitializeCommentThread( name, rgCommentData, url )
{
	g_rgCommentThreads[name] = new CCommentThread( name, rgCommentData, url );
}

var CCommentThread = Class.create( {

	m_strName: null,
	m_rgCommentData: null,
	m_strActionURL: null,
	m_elTextArea: null,
	m_cEntryLength: Number.MAX_VALUE,
	m_cPageSize: null,

	m_cTotalCount: 0,
	m_iCurrentPage: 0,
	m_cMaxPages: 0,
	m_bLoading: false,


	initialize: function( name, rgCommentData, url )
	{
		this.m_strName = name;
		this.m_rgCommentData = rgCommentData;
		this.m_strActionURL = url;

		this.m_cTotalCount = rgCommentData['total_count'];
		this.m_iCurrentPage = 0;
		this.m_cPageSize = rgCommentData['pagesize'];
		this.m_cMaxPages = Math.ceil( this.m_cTotalCount / this.m_cPageSize );

		var strPrefix = 'commentthread_' + this.m_strName;
		this.m_elTextArea = $( strPrefix + '_textarea');

		if ( this.m_elTextArea )
		{
			this.m_elTextArea.observe( 'keyup', this.OnTextInput.bind( this ) );
			this.m_elTextArea.observe( 'blur', this.OnTextInput.bind( this ) );
			this.m_elTextArea.observe( 'click', this.OnTextInput.bind( this ) );
			this.m_elTextArea.observe( 'paste', this.OnPasteText.bind( this ) );
			this.m_elTextArea.observe( 'cut', this.OnPasteText.bind( this ) );

			this.OnTextInput();
		}

		$(strPrefix + '_pagebtn_prev').observe( 'click', this.PrevPage.bind( this ) );
		$(strPrefix + '_fpagebtn_prev').observe( 'click', this.PrevPage.bind( this ) );
		$(strPrefix + '_pagebtn_next').observe( 'click', this.NextPage.bind( this ) );
		$(strPrefix + '_fpagebtn_next').observe( 'click', this.NextPage.bind( this ) );

		var elForm = $( strPrefix + '_form');
		if ( elForm )
		{
			elForm.observe( 'submit', this.OnSubmit.bind( this ) );
		}

		var elSubmit = $(strPrefix + '_submit');
		if ( elSubmit )
		{
			elSubmit.observe( 'click', this.OnSubmit.bind( this ) );
		}

		this.UpdatePagingDisplay();
	},

	OnPasteText: function()
	{
		this.OnTextInput.bind(this).defer();
	},

	OnTextInput: function()
	{
		var iMinHeight = 40;
		var cNewLength = this.m_elTextArea.value.length;
		// force a resize
		if ( cNewLength < this.m_cEntryLength )
			this.m_elTextArea.style.height = iMinHeight + 'px';
		
		if ( this.m_elTextArea.scrollHeight != this.m_elTextArea.getHeight() )
		{
			var iHeight = Math.max( this.m_elTextArea.scrollHeight, iMinHeight );
			this.m_elTextArea.style.height = iHeight + 'px';
		}

		if ( cNewLength > 0 )
			$('commentthread_' + this.m_strName + '_submit_container').show();
		else
			$('commentthread_' + this.m_strName + '_submit_container').hide();

		this.m_cEntryLength = cNewLength;
	},

	GetActionURL: function( action )
	{
		var url = this.m_strActionURL + action + '/';
		url += this.m_rgCommentData['owner'] + '/';
		url += this.m_rgCommentData['feature'] + '/';
		return url;
	},

	OnSubmit: function()
	{
		if ( this.m_bLoading )
			return;
		
		this.m_bLoading = true;
		new Ajax.Request( this.GetActionURL( 'post' ), {
			method: 'post',
			parameters: { comment: this.m_elTextArea.value },
			onSuccess: this.OnResponseAddComment.bind( this ),
			onFailure: this.OnFailureDisplayError.bind( this ),
			onComplete: this.OnAJAXComplete.bind( this )
		} );
		return false;
	},

	DeleteComment: function( gidComment )
	{
		if ( this.m_bLoading )
			return;

		this.m_bLoading = true;
		new Ajax.Request( this.GetActionURL( 'delete' ), {
			method: 'post',
			parameters: { gidcomment: gidComment, start: this.m_cPageSize * this.m_iCurrentPage },
			onSuccess: this.OnResponseDeleteComment.bind( this ),
			onFailure: this.OnFailureDisplayError.bind( this ),
			onComplete: this.OnAJAXComplete.bind( this )
		} );
	},

	OnAJAXComplete: function()
	{
		this.m_bLoading = false;
	},

	NextPage: function()
	{
		if ( this.m_iCurrentPage < this.m_cMaxPages - 1 )
			this.GoToPage( this.m_iCurrentPage + 1 );
	},

	PrevPage: function()
	{
		if ( this.m_iCurrentPage > 0 )
			this.GoToPage( this.m_iCurrentPage - 1 );
	},

	GoToPage: function( iPage )
	{
		if ( this.m_bLoading || iPage >= this.m_cMaxPages || iPage < 0 || iPage == this.m_iCurrentPage )
			return;

		this.m_bLoading = true;
		new Ajax.Request( this.GetActionURL( 'render' ), {
			method: 'get',
			parameters: { start: this.m_cPageSize * iPage, count: this.m_cPageSize, totalcount: this.m_cTotalCount },
			onSuccess: this.OnResponseRenderComments.bind( this ),
			onComplete: this.OnAJAXComplete.bind( this )
		});
	},

	OnResponseAddComment: function( transport )
	{
		if ( transport.responseJSON && transport.responseJSON.success)
		{
			$('commentthread_' + this.m_strName + '_entry_error').hide();
			this.m_elTextArea.value='';
			this.OnTextInput(); // reset the input box size
			this.OnResponseRenderComments( transport, true );	//display the updated comment thread
		}
		else
		{
			this.OnFailureDisplayError( transport );
		}
	},

	OnResponseDeleteComment: function( transport )
	{
		if ( transport.responseJSON && transport.responseJSON.success )
			this.OnResponseRenderComments( transport );
		else
			this.OnFailureDisplayError( transport );
	},

	OnFailureDisplayError: function( transport )
	{
		var strMessage = 'Sorry, some kind of error has occurred: ';
		if ( transport.responseJSON && transport.responseJSON.error )
			strMessage += transport.responseJSON.error;
		else
			strMessage += 'There was an error communicating with the network. Please try again later.';

		$('commentthread_' + this.m_strName + '_entry_error').update( strMessage );
		$('commentthread_' + this.m_strName + '_entry_error').show();
	},

	OnResponseRenderComments: function( transport, bNewPost )
	{
		if ( transport.responseJSON )
		{
			var response = transport.responseJSON;
			this.m_cTotalCount = response.total_count;
			this.m_cMaxPages = Math.ceil( response.total_count / response.pagesize );
			this.m_iCurrentPage = Math.floor( response.start / response.pagesize );

			if ( this.m_cTotalCount <= response.start )
			{
				// this page is no logner valid, flip back a page (deferred so that the AJAX handler exits and reset m_bLoading)
				this.GoToPage.bind( this, this.m_iCurrentPage - 1 ).defer();
				return;
			}

			var elPosts = $('commentthread_' + this.m_strName + '_posts' );
			var elContainer = $('commentthread_' + this.m_strName + '_postcontainer' );
			elContainer.style.height = elContainer.getHeight() + 'px';

			if ( bNewPost && this.m_cTotalCount <= this.m_cPageSize )
			{
				elContainer.style.position = 'relative';
				elPosts.style.position = 'absolute';
				elPosts.style.bottom = '0px'
				elPosts.style.left = '0px'
				elPosts.style.right = '0px';
			}
			else
			{
				elPosts.style.position = 'static';
			}

			elPosts.update( response.comments_html );

			if ( elContainer.effect )
				elContainer.effect.cancel();
			elContainer.effect = new Effect.Morph( elContainer, { style: 'height: ' + elPosts.getHeight() + 'px', duration: 0.25 } );

			this.UpdatePagingDisplay();
		}
	},

	UpdatePagingDisplay: function()
	{
		var strPrefix = 'commentthread_' + this.m_strName;
		$(strPrefix + '_totalcount').update( v_numberformat( this.m_cTotalCount ) );

		var rgPagingControls = [ strPrefix + '_page', strPrefix + '_fpage' ];
		for ( var i = 0; i < rgPagingControls.length; i++ )
		{
			var strPagePrefix = rgPagingControls[i];
			if ( this.m_cMaxPages <= 1 )
			{
				$(strPagePrefix + 'controls').hide();
			}
			else
			{
				$(strPagePrefix + 'controls').show();
				if ( this.m_iCurrentPage > 0 )
					$(strPagePrefix + 'btn_prev').removeClassName('disabled');
				else
					$(strPagePrefix + 'btn_prev').addClassName('disabled');

				if ( this.m_iCurrentPage < this.m_cMaxPages - 1 )
					$(strPagePrefix + 'btn_next').removeClassName('disabled');
				else
					$(strPagePrefix + 'btn_next').addClassName('disabled');

				var elPageLinks = $(strPagePrefix + 'links');
				elPageLinks.update('');
				// we always show first, last, + 3 page links closest to current page
				var cPageLinksAheadBehind = 2;
				var firstPageLink = Math.max( this.m_iCurrentPage - cPageLinksAheadBehind, 1 );
				var lastPageLink = Math.min( this.m_iCurrentPage + (cPageLinksAheadBehind*2) + ( firstPageLink - this.m_iCurrentPage ), this.m_cMaxPages - 2 );

				if ( lastPageLink - this.m_iCurrentPage < cPageLinksAheadBehind )
					firstPageLink = Math.max( this.m_iCurrentPage - (cPageLinksAheadBehind*2) + ( lastPageLink - this.m_iCurrentPage ), 1 );

				this.AddPageLink( elPageLinks, 0 );
				if ( firstPageLink != 1 )
					elPageLinks.insert( ' ... ' );

				for ( var iPage = firstPageLink; iPage <= lastPageLink; iPage++ )
				{
					this.AddPageLink( elPageLinks, iPage );
				}

				if ( lastPageLink != this.m_cMaxPages - 2 )
					elPageLinks.insert( ' ... ' );
				this.AddPageLink( elPageLinks, this.m_cMaxPages - 1 );
			}
		}
	},

	AddPageLink: function( elPageLinks, iPage )
	{
		var el = new Element( 'span', {'class': 'commentthread_pagelink' } );
		el.update( (iPage + 1) + ' ' );

		if ( iPage == this.m_iCurrentPage )
			el.addClassName( 'active' );
		else
			el.observe( 'click', this.GoToPage.bind( this, iPage ) );
		
		elPageLinks.insert( el );
	}

} );

// static accessor
CCommentThread.DeleteComment = function( id, gidcomment )
{
	if ( g_rgCommentThreads[id] )
		g_rgCommentThreads[id].DeleteComment( gidcomment );
};


