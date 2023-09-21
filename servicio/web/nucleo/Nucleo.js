var _esN = window.navigator.appName == 'Netscape' ? true : false;
var _esIE = window.navigator.appName == 'Microsoft Internet Explorer' ? true : false;
window.moveTo(0, 0);
if (_esN) {
    window.outerHeight = window.screen.availHeight;
    window.outerWidth = window.screen.availWidth;
} else {
    window.resizeTo(window.screen.availWidth, window.screen.availHeight);
}
document.onmousedown = function (e) {
    if (_esN && (e.which == 3 || e.which == 2)) {
        return false;
    } else if (_esIE) {
        if (event.button == 2) {
            return false;
        }
    }
    return true;
};
document.oncontextmenu = new Function("return false;");
function _inhibir(e)
{
    if (_esN) {
        e.preventDefault();
    } else {
        window.event.returnValue = false;
        window.event.cancelBubble = true;
    }
}
function _getKeyCode(e)
{
    if (_esN) {
        return e.which;
    } else {
        return event.keyCode;
    }
}
function _NE(e, f)
{
    if (_esN) {
        document.body.addEventListener(e, f, false);
    } else {
        document.body.attachEvent('on' + e, f);
    }
}
function _RE(e, f1)
{
    var f = f1 || 'mousemove';
    if (_esN) {
        document.body.removeEventListener(e, f, false);
    } else {
        document.body.detachEvent("on" + e, f);
    }
}
function _numero(e)
{
    var c = _getKeyCode(e);
    if ((c < 48 || c > 57) && c != 0 && c != 8 && c != 9 && c != 13) {
        _inhibir(e);
    }
}
function _letra(e)
{
    var c = _getKeyCode(e);
    if ((c < 65 || c > 90 && c < 97 || c > 122 || c == 39) && c != 0 && c != 8 && c != 9 && c != 32 && c != 37 && c != 38 && c != 40 && c != 46 && c != 193 && c != 201 && c != 205 && c != 211 && c != 218 && c != 225 && c != 233 && c != 237 && c != 243 && c != 250 && c != 241 && c != 209 && c != 13) {
        _inhibir(e);
    }
}
function _alfanumerico(e)
{
    var c = _getKeyCode(e);
    if ((c < 48 || c > 57 && c < 65 || c > 90 && c < 97 || c > 122 || c == 39) && c != 0 && c != 8 && c != 9 && c != 32 && c != 37 && c != 38 && c != 40 && c != 41 && c != 44 && c != 45 && c != 46 && c != 193 && c != 201 && c != 205 && c != 211 && c != 218 && c != 225 && c != 233 && c != 237 && c != 243 && c != 250 && c != 241 && c != 209 && c != 13) {
        _inhibir(e);
    }
}
function _evaluar(e, co)
{
    var c = _getKeyCode(e);
    var ca = String.fromCharCode(c);
    if (co.indexOf(ca) == -1 && c != 0 && c != 8 && c != 9 && c != 13) {
        _inhibir(e);
    }
}
function _esFecha(o)
{
    var obj = _(o);
    var fechav = obj.value;
    //var RegExp = /\d{1,2}\/\d{1,2}\/\d\d\d\d/;
    var RegExp = /\d\d\d\d-\d{1,2}-\d{1,2}/;
    var vector = new Array();
    if (fechav != '') {
        if (fechav.search(RegExp) == 0) {
            vector = fechav.split('/');
            if (vector[0] > 31 || vector[1] > 12) {
                _MS('Error: Fecha no válida', obj);
                return false;
            }
        } else {
            _MS('Error: Fecha no válida', obj);
            return false;
        }
    }
    return true;
}
function _esHora(ob)
{
    var o = _(ob);
    var h = o.value;
    if (h != '') {
        if (h.search(/\d{1,2}:\d{1,2}/) == 0 || h.search(/\d{1,2}:\d{1,2}:\d{1,2}/) == 0) {
            var h1 = h.search(/\d{1,2}:\d{1,2}/) == 0 ? h + ':00' : h;
            var v = h1.split(':');
            if (parseInt(v[0]) > 23 || parseInt(v[1]) > 59 || parseInt(v[2]) > 59) {
                _MS('Número de hora, minutos y/o segundos fuera de rango.', o);
                return false;
            }
        } else {
            _MS('Formato de hora no válida.', o);
            return false;
        }
    }
    return true;
}
function esDocumento(ob)
{
    var obj = document.getElementById(ob);
    if (obj.value == '') {
        return false;
    }
    numero = obj.value;
    var suma = 0;
    var residuo = 0;
    var pri = false;
    var pub = false;
    var nat = false;
    var numeroProvincias = 24;
    var modulo = 11;
    var ok = 1;
    for (i = 0; i < numero.length && ok == 1; i++) {
        var n = parseInt(numero.charAt(i));
        if (isNaN(n))
            ok = 0;
    }
    if (ok == 0) {
        _MS('Debe ingresar solo caracteres numéricos', obj);
        return false;
    }

    if (numero.length < 10) {
        _MS('El número de documento ingresado no es válido', obj);
        return false;
    }
    provincia = numero.substr(0, 2);
    if (provincia < 1 || provincia > numeroProvincias) {
        _MS('El código de la provincia (dos primeros dígitos) del número de documento es inválido', obj);
        return false;
    }
    d1 = numero.substr(0, 1);
    d2 = numero.substr(1, 1);
    d3 = numero.substr(2, 1);
    d4 = numero.substr(3, 1);
    d5 = numero.substr(4, 1);
    d6 = numero.substr(5, 1);
    d7 = numero.substr(6, 1);
    d8 = numero.substr(7, 1);
    d9 = numero.substr(8, 1);
    d10 = numero.substr(9, 1);
    if (d3 == 7 || d3 == 8) {
        _MS('El tercer dígito del número de documento ingresado es inválido', obj);
        return false;
    }
    if (d3 < 6) {
        nat = true;
        p1 = d1 * 2;
        if (p1 >= 10)
            p1 -= 9;
        p2 = d2 * 1;
        if (p2 >= 10)
            p2 -= 9;
        p3 = d3 * 2;
        if (p3 >= 10)
            p3 -= 9;
        p4 = d4 * 1;
        if (p4 >= 10)
            p4 -= 9;
        p5 = d5 * 2;
        if (p5 >= 10)
            p5 -= 9;
        p6 = d6 * 1;
        if (p6 >= 10)
            p6 -= 9;
        p7 = d7 * 2;
        if (p7 >= 10)
            p7 -= 9;
        p8 = d8 * 1;
        if (p8 >= 10)
            p8 -= 9;
        p9 = d9 * 2;
        if (p9 >= 10)
            p9 -= 9;
        modulo = 10;
    } else if (d3 == 6) {
        pub = true;
        p1 = d1 * 3;
        p2 = d2 * 2;
        p3 = d3 * 7;
        p4 = d4 * 6;
        p5 = d5 * 5;
        p6 = d6 * 4;
        p7 = d7 * 3;
        p8 = d8 * 2;
        p9 = 0;
    } else if (d3 == 9) {
        pri = true;
        p1 = d1 * 4;
        p2 = d2 * 3;
        p3 = d3 * 2;
        p4 = d4 * 7;
        p5 = d5 * 6;
        p6 = d6 * 5;
        p7 = d7 * 4;
        p8 = d8 * 3;
        p9 = d9 * 2;
    }
    suma = p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
    residuo = suma % modulo;
    digitoVerificador = residuo == 0 ? 0 : modulo - residuo;
    if (pub == true) {
        if (digitoVerificador != d9) {
            _MS('El ruc de la empresa del sector público es incorrecto.', obj);
            return false;
        }
        if (numero.substr(9, 4) != '0001') {
            _MS('El ruc de la empresa del sector público debe terminar con 0001', obj);
            return false;
        }
    } else if (pri == true) {
        if (digitoVerificador != d10) {
            _MS('El ruc de la empresa del sector privado es incorrecto.', obj);
            return false;
        }
        if (numero.substr(10, 3) != '001') {
            _MS('El ruc de la empresa del sector privado debe terminar con 001', obj);
            return false;
        }
    } else if (nat == true) {
        if (digitoVerificador != d10) {
            _MS('El número de cédula o ruc de la persona natural es incorrecto.', obj);
            return false;
        }
        if (numero.length > 10 && numero.substr(10, 3) != '001') {
            _MS('El ruc de la persona natural debe terminar con 001', obj);
            return false;
        }
    }
    return true;
}
function _DNI(e)
{
    var c = _getKeyCode(e);
    if ((c < 48 || c > 57 && c < 65 || c > 90 && c < 97 || c > 122) && c != 0 && c != 8) {
        _inhibir(e);
    }
}
function _sinEspeciales(e)
{
    var c = _getKeyCode(e);
    if ((c == 34 || c == 39 || c == 124 || c == 92) && c != 0 && c != 8) {
        _inhibir(e);
    }
}
function _getKeyCode(e)
{
    if(_esN) {
        return e.which;
    } else {
        return event.keyCode;
    }
}
function _esMail(ob)
{
    var o = _(ob);
    var m = o.value;
    if (m != '') {
        if (m.search(/[\D\d]+@[\D]+\.[\D]+/) != 0) {
            _MS('Formato de Dirección de correo electrónico no válido.', o);
            return false;
        }
    }
    return true;
}
function _RD(v)
{
    return (Math.round(parseFloat(v) * Math.pow(10, 2)) / Math.pow(10, 2));
}
function _RDD(v, d)
{
    return (Math.round(parseFloat(v) * Math.pow(10, d)) / Math.pow(10, d));
}
function _CEIL(v, d)
{
    return (Math.ceil(parseFloat(v) * Math.pow(10, d)) / Math.pow(10, d));
}
function _FLOOR(v, d)
{
    return (Math.floor(parseFloat(v) * Math.pow(10, d)) / Math.pow(10, d));
}
function _(o)
{
    var v = (parseInt(window.navigator.appVersion) == 4) ? true : false;
    return (document.getElementById) ? document.getElementById(o) : ((document.all) ? document.all[o] : ((_esN && v) ? document.layers[o] : null));
}
function _R(b)
{
    var o = _(b);
    if (o != null) {
        if (_esN) {
            o.parentNode.removeChild(o);
        } else {
            o.removeNode(true);
        }
    }
}
function _MS(m, o)
{
    alert(m);
    if (o != '' && o != undefined) {
        o.focus();
        o.select();
    }
}
function _T(b, v)
{
    var o = _(b);
    if (o != null) {
        o.style.opacity = v / 100;
        if (o != null) {
            if (_esN) {
                o.style.MozOpacity = v / 100;
            } else {
                o.style.filter = "alpha(opacity=" + v + ")";
            }
        }
    }
}
function _B(b, v)
{
    var o = _(b);
    if (o != null) {
        o.style.webkitBorderRadius = v + "px";
        o.style.borderRadius = v + "px";
        if (o != null) {
            if (_esN) {
                o.style.MozBorderRadius = v + "px";
            }
            if (_esIE) {
                o.style.msBorderRadius = v + "px";
            }
        }
    }
}
function _S(b)
{
    var o = _(b);
    if (o != null) {
        o.style.WebkitBoxShadow = ".0em .0em .9em #000000";
        if (o != null) {
            if (_esN) {
                o.style.MozBoxShadow = ".0em .0em .9em #000000";
            }
            if (_esIE) {
                o.style.filter = "shadow(color=#000000, direction=0, strength=9)";
            }
        }
    }
}
function _M(z, id, obj)
{
    obj = obj || '';
    var i = id || 'jm_bloqueador';
    if (_(i) == null) {
        var b = document.createElement('DIV');
        b.id = i;
        b.style.backgroundColor = '#FFFFFF';
        b.style.position = 'absolute';
        b.style.left = '0px';
        b.style.top = '0px';
        b.style.width = (parseInt(document.body.clientWidth) + parseInt(document.body.scrollLeft)) + 'px';
        b.style.height = (parseInt(document.body.clientHeight) + parseInt(document.body.scrollTop)) + 'px';
        b.style.zIndex = z;
        if (obj !== '') {
            document.getElementById(obj).appendChild(b);
        } else {
            document.body.appendChild(b);
        }
        _T(i, 50);
    }
}
function _C(obj)
{
    obj = obj || '';
    if (_('jm_carg') == null) {
        i = document.createElement('IMG');
        i.id = 'jm_carg';
        i.src = 'img/cargando.gif';
        i.style.position = 'absolute';
        i.style.zIndex = 30;
        i.style.top = parseInt(document.body.clientHeight / 2) + 'px';
        i.style.left = parseInt(document.body.clientWidth / 2) + 'px';
        if (obj !== '') {
            document.getElementById(obj).appendChild(i);
        } else {
            document.body.appendChild(i);
        }
    }
}
function _getTop(o)
{
    if (o == null) {
        return 0;
    } else {
        var obj = o.offsetParent;
        if (o.style.top) {
            return o.style.top + _getTop(obj);
        }
        if (o.style.pixelTop) {
            return o.style.pixelTop + _getTop(obj);
        }
        if (o.offsetTop) {
            return o.offsetTop + _getTop(obj);
        }
        return (null);
    }
}
function _getIzq(o)
{
    if (o == null) {
        return 0;
    } else {
        var obj = o.offsetParent;
        if (o.style.left) {
            return o.style.left + _getIzq(obj);
        }
        if (o.style.pixelLeft) {
            return o.style.pixelLeft + _getIzq(obj);
        }
        if (o.offsetLeft) {
            return o.offsetLeft + _getIzq(obj);
        }
        return (null);
    }
}
function _enMatrizJSON(v, x, p)
{
    for (var i = 0; i < v.tbl.length; i++) {
        if (v.tbl[i][p] == x) {
            return i;
        }
    }
    return -1;
}
function _enMatrizJSONClaves(v, x, p)
{
    var pos = -1;
    for (var i = 0; i < v.tbl.length; i++) {
        var cont = 0;
        for (var j = 0; j < x.length; j++) {
            if (v.tbl[ i ][ p[j] ] == x[j]) {
                cont++;
            }
        }
        if (cont == x.length) {
            pos = i;
            break;
        }
    }
    return pos;
}
String.prototype._codificarURI = function () {
    var c = this;
    c = c.replace(/á/g, '_^4;');
    c = c.replace(/é/g, '_^5;');
    c = c.replace(/í/g, '_^6;');
    c = c.replace(/ó/g, '_^7;');
    c = c.replace(/ú/g, '_^8;');
    c = c.replace(/Á/g, '_^9;');
    c = c.replace(/É/g, '_^10;');
    c = c.replace(/Í/g, '_^11;');
    c = c.replace(/Ó/g, '_^12;');
    c = c.replace(/Ú/g, '_^13;');
    c = c.replace(/Ñ/g, '_^14;');
    c = c.replace(/ñ/g, '_^15;');

    c = c.replace(/&/g, '_^0;');
    c = c.replace(/\+/g, '_^1;');
    c = c.replace(/\%/g, '_^2;');
    c = c.replace(/\'/g, '_^3;');
    //c=c.replace(/\//g, '^');

    c = c._trim();
    return c;
};
String.prototype._trim = function () {
    var r = this;
    r = r.replace(/[ ]+/g, ' ');
    return r.replace(/^\s*|^.*|\s*$/g, '');
};
String.prototype._toQueryParams = function () {
    var r = new Array();
    var m = this.split('^');
    for (i = 0; i < m.length; i++) {
        var v = m[i].split('»');
        r[v[0]] = v[1];
    }
    return r;
};
Date.prototype._fecha = function (t) {
    var d = this;
    var ms = d.getMonth() + 1;
    ms = ms < 10 ? '0' + ms : ms;
    var dy = d.getDate();
    dy = dy < 10 ? '0' + dy : dy;
    /*var f = t || 'ISO';
     if(f.toUpperCase()=='SQL'){
     return d.getDate()+'/'+(d.getMonth()+1)+'/'+d.getFullYear();
     }*/
    return d.getFullYear() + '-' + ms + '-' + dy;
};
Date.prototype._hora = function () {
    return this.getHours() + ':' + this.getMinutes();
};
Array.prototype._enArreglo = function (c) {
    v = this;
    for (i = 0; i < v.length; i++) {
        if (v[i] == c) {
            return i;
        }
    }
    return -1;
};
Array.prototype._enArregloBin = function (x) {
    v = this;
    a = v.length - 1;
    b = c = 0;
    while (b <= a) {
        c = Math.ceil((a + b) / 2);
        if (v[c] == x) {
            return c;
        } else if (x < v[c]) {
            a = c - 1;
        } else {
            b = c + 1;
        }
    }
    return -1;
};
var Ajax = new Object();
Ajax = function (pg, p, m, c) {
    this.t = this.getTransporte();
    if (pg != '' && pg != undefined && p != undefined) {
        this.solicitud(pg, p, m, c);
    }
};
Ajax.prototype = {
    getTransporte: function () {
        return _esN ? new XMLHttpRequest() : (new ActiveXObject('Msxml2.XMLHTTP') || new ActiveXObject('Microsoft.XMLHTTP') || false);
    },
    solicitud: function (pg, p, m, c) {
        var obj = this;
        this.t.onreadystatechange = function () {
            if (obj.t.readyState == 1) {
                if (m != undefined && m) {
                    _M(29);
                }
                if (_(c) != null && _(c) != undefined) {
                    _(c).innerHTML = '<span id="jm_carg" style="background-color:#FF0000;z-index:30;">&nbsp; Cargando... &nbsp;</span>';
                } else if (c == 'body') {
                    _C();
                }
            }
            if (obj.t.readyState == 4) {
                _R('jm_bloqueador');
                _R('jm_carg');
                if (_(c) != null) {
                    _(c).innerHTML = '&nbsp;';
                }
                if (obj.t.status == 200) {
                    obj.procesar(obj.t.responseText);
                } else {
                    _AJAX = null;
                    //alert("\nERROR DE ESTADO: " + obj.t.status);
                    _AJAX = new Ajax();
                }
            }
        };
        this.t.open('POST', pg, true);
        this.t.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        this.t.send(p);
    },
    enviarForm: function (f, bl) {
        bl = bl || false;
        var p = 'WHERE=' + _WR + '&p=' + (_objT != null ? _objT.pg : 0) + '&';
        var i = 0;
        while (f.elements[i] != null) {
            if (f.elements[i].type != 'reset' && f.elements[i].type != 'image' && f.elements[i].type != undefined && f.elements[i].type != 'fieldset') {
                if (f.elements[i].type == 'checkbox') {
                    p += f.elements[i].name + '=' + f.elements[i].checked + '&';
                } else if (f.elements[i].type == 'radio') {
                    if (f.elements[i].checked) {
                        p += f.elements[i].name + '=' + f.elements[i].value._codificarURI() + '&';
                    }
                } else {
                    p += f.elements[i].name + '=' + f.elements[i].value._codificarURI() + '&';
                }
            }
            i++;
        }
        p = p.substr(0, p.length - 1);
        this.solicitud(f.action, p, bl, 'body');
        return false;
    },
    procesar: function (t) {
        var p = t._toQueryParams();
        if (_(p['obj']) != null) {
            if (p['frm'] != '' && p['frm'] != undefined) {
                _(p['obj']).innerHTML = p['frm'];
                if (_(p['foc']) != null) {
                    _(p['foc']).focus();
                }
            }
        }
        if (p['err'] != '' && p['err'] != undefined) {
            if (p['err'] == '0') {
                if (p['vta'] != '' && p['vta'] != undefined) {
                    Ventana.cerrar(p['vta']);
                    _R('bloq_' + p['vta']);
                    _R(p['vta']);
                }
            }
        }
        if (p['tbl'] != '' && p['tbl'] != undefined) {
            _objT.setDatos(p['tbl']);
            _objT.marcarFila(_FS);
        }
        if (p['msg'] != '' && p['msg'] != undefined) {
            //alert(p['msg']);
            alertify.warning(p['msg']);
        }
        if (p['id'] == '-1' && p['id'] != undefined) {
            _('d_21').innerHTML = '&nbsp;';
        }
        if (p['fun'] != '' && p['fun'] != undefined) {
            window.setTimeout(p['fun'], 1);
        }
    }
};
var _m = '';
var _mA = '';
function _jmBTMclick(i) {
    if (i != _m) {
        _mA = _m;
        _m = i;
        _(i).className = 'jmBTMS';
        if (_(_mA) != null) {
            _(_mA).className = 'jmBTMF';
        }

    }
}
function _jmBTMsobre(i) {
    if (i != _m) {
        _(i).className = 'jmBTMS';
    }
}
function _jmBTMfuera(i) {
    if (i != _m) {
        _(i).className = 'jmBTMF';
    }
}
var Boton = new Object();
Boton = function ()
{
    this._btn = 0;
};
Boton.prototype = {
    icono: function (x, y, w, h, t, a, op) {
        var o = _(op) || document.body;
        var nBtn = 'btn_' + this._btn++;
        var nBtn0 = '0' + nBtn;
        var b = '<div id="' + nBtn0 + '" class="jmBTF"><div id="' + nBtn + '" class="jmBT" style="width:' + w + 'px;height:' + h + 'px;" onmouseover="_(\'' + nBtn0 + '\').className=\'jmFPBT jmBorde\';" onmouseout="_(\'' + nBtn0 + '\').className=\'jmBTF\';" onClick="' + a + '" alt="' + t + '" title="' + t + '">&nbsp;</div></div>';
        o.innerHTML += b;
        _(nBtn).style.backgroundPosition = (x) + 'px ' + (y) + 'px';
        _B(nBtn0, 4);
    },
    buton: function (c, t, a, op, d, l, cl) {
        var o = _(op) || document.body;
        l = l || '';
        d = d || '';
        cl = cl || 'info';
        var nBtn = 'btn_' + this._btn++;
        var nBtn0 = '0' + nBtn;
        var b = (d !== '' ? '<div class="margen_icono" >' : '')
                + '<button type="button" class="btn btn-flat btn-outline-' + cl + ' btn-sm mb-3" onclick="' + a + '" alt="' + t + '" title="' + t + '"><i class="' + c + '"></i>' + (l !== '' ? ' <span>' + t + '</span>' : '') + '</button>'
                + (d !== '' ? '</div>' : '');
        o.innerHTML += b;
    },
    separador: function (op) {
        var o = _(op) || document.body;
        o.innerHTML += '<div id="jmSep">&nbsp;</div>';
    },
    modulo: function (x, y, w, h, t, op, bq) {
        var o = _(op) || document.body;
        var t2 = t;
        var bqu = bq || '';
        var l = (t.substring(0, 14).indexOf('&') >= 0) ? 21 : 14;
        if (t.length > l) {
            t2 = t.substring(0, l) + '...';
        }
        if (_esN) {
            h -= 4;
        }
        var b = '<div class="jmBTME" style="cursor:pointer;" onClick="_(\'' + op + bqu + '\').style.display=_(\'' + op + bqu + '\').style.display==\'none\'?\'block\':\'none\';"><div class="jmICOM" style="display:table-cell;background-position:' + x + 'px ' + y + 'px;width:' + w + 'px;height:' + h + 'px;">&nbsp;</div><div style="display:table-cell;font-weight:bold;height:' + h + 'px;padding-top:5px;">' + t2 + '</div></div>';
        o.innerHTML += b;
    },
    menu: function (id, x, y, w, h, t, a, op) {
        var o = _(op) || document.body;
        var t2 = t;
        var l = (t.substring(0, 12).indexOf('&') >= 0) ? 19 : 12;
        if (t.length > l) {
            t2 = t.substring(0, l) + '...';
        }
        if (_esN) {
            h -= 4;
        }
        var b = '<div id="' + id + '" class="jmBTMF" onmouseover="_jmBTMsobre(\'' + id + '\');" onmouseout="_jmBTMfuera(\'' + id + '\');" onClick="_jmBTMclick(\'' + id + '\');' + a + '" alt="' + t + '" title="' + t + '"><div class="jmICOM" style="display:table-cell;background-position:' + x + 'px ' + y + 'px;width:' + w + 'px;height:' + h + 'px;">&nbsp;</div><div style="display:table-cell;padding-top:5px;padding-left:8px;height:' + h + 'px;font-size:12px;">' + t2 + '</div></div>';
        o.innerHTML += b;
    }
};
var _Fch = true;
var SelectorFecha = {
    r: '',
    f: '',
    u: '',
    i: new Array('Hoy', 'Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa', 'ene', 'feb', 'mar', 'abr', 'may', 'jun', 'jul', 'ago', 'sep', 'oct', 'nov', 'dic', 'Cerrar'),
    a: 2010,
    m: 1,
    d: 1,
    crear: function (re, fo, fu) {
        _Fch = true;
        if (_esN) {
            window.addEventListener('mousedown', SelectorFecha.remEvt, false);
        } else {
            document.body.attachEvent('onmousedown', SelectorFecha.remEvt);
        }
        if (_('Fech_' + this.r) == null) {
            this.r = re || 'unknown';
            this.f = fo || 'iso';
            this.u = fu || '';
            SelectorFecha.desplegar();
        }
    },
    remEvt: function () {
        if (_Fch) {
            _R('Fech_' + SelectorFecha.r);
            if (_esN) {
                window.removeEventListener('mousedown', SelectorFecha.remEvt, false);
            } else {
                document.body.detachEvent('onmousedown', SelectorFecha.remEvt);
            }
        }
    },
    hoy: function () {
        var f = new Date();
        this.a = f.getFullYear();
        this.m = f.getMonth() + 1;
        this.d = f.getDate();
    },
    desplegar: function () {
        var o = _(this.r);
        if (this.r == 'unknown' || o == null) {
            alert('No se ha especificado el campo donde incrustar la fecha.');
            return false;
        }
        if (o.value.length >= 8) {
            if (o.value.indexOf('/') > 0) {
                var f = o.value.split('/');
                this.a = parseInt(f[2], 10);
                this.d = parseInt(f[0], 10);
            } else {
                var f = o.value.split('-');
                this.a = parseInt(f[0], 10);
                this.d = parseInt(f[2], 10);
            }
            this.m = parseInt(f[1], 10);
        } else {
            SelectorFecha.hoy();
        }
        var d = document.createElement('DIV');
        d.id = 'Fech_' + this.r;
        d.onmouseover = function () {
            _Fch = false;
        };
        d.onmouseout = function () {
            _Fch = true;
        };
        d.className = 'jm_selectorFecha';
        d.innerHTML = '<table width="100%" cellpadding="0" cellspacing="0"><tr><td><a href="javascript:void(0);" class="jm_hoy" onclick="SelectorFecha.hoy();SelectorFecha.imprimir();' + this.u + '">&nbsp;' + this.i[0] + '&nbsp;</a></td><td align="right"><a href="javascript:void(0);" class="jm_hoy" onmousedown="SelectorFecha.cerrar();">&nbsp;' + this.i[20] + '&nbsp;</a></td></tr></table>';
        d.innerHTML += '<table class="jm_tablaFecha" cellpadding="0" cellspacing="0"><tr valign="top"><td id="jm_anios"></td><td id="jm_meses"></td><td id="jm_dias" align="center"></td></tr></table>';
        if (_esIE) {
            var r = _(this.r).size;
            var x = (r == '' || r == 0) ? 160 : parseInt(r + (Math.E * r * 2) / 0.79);
            document.body.appendChild(d);
            d.style.top = event.y + 10;
            d.style.left = event.x - x;
        } else {
            o.parentNode.appendChild(d);
        }
        SelectorFecha.setDias();
        SelectorFecha.setMeses();
        SelectorFecha.setAnios();
        _S('Fech_' + this.r);
        _B('Fech_' + this.r, 3);
        return true;
    },
    numDiasMes: function (a, m) {
        switch (parseInt(m)) {
            case 2:
                if (((a % 100 == 0) && (a % 400 == 0)) || ((a % 100 != 0) && (a % 4 == 0))) {
                    return 29;
                } else {
                    return 28;
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
                break;
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
                break;
        }
        return 0;
    },
    setDias: function () {
        var f = new Date(this.a, this.m - 1);
        var ds = f.getDay() - 1;
        var nd = SelectorFecha.numDiasMes(this.a, this.m);
        var h = '<table class="jm_tablaDias" cellpadding="0" cellspacing="3">';
        h += '<tr align="center"><th class="jm_tituloDias">' + this.i[1] + '</th><th class="jm_tituloDias">' + this.i[2] + '</th><th class="jm_tituloDias">' + this.i[3] + '</th><th class="jm_tituloDias">' + this.i[4] + '</th><th class="jm_tituloDias">' + this.i[5] + '</th><th class="jm_tituloDias">' + this.i[6] + '</th><th class="jm_tituloDias">' + this.i[7] + '</th></tr><tr align="right">';
        var i = 0;
        do {
            if (i % 7 == 0) {
                h += '</tr><tr align="right">';
            }
            var dm = (i - ds == this.d) ? ' class="jm_diaMarcado"' : '';
            h += (i > ds) ? '<td' + dm + '><a href="javascript:void(0);" class="jm_dia" onclick="SelectorFecha.setDia(' + (i - ds) + ');' + this.u + '">' + (i - ds) + '</a></td>' : '<td>&nbsp;</td>';
            i++;
        } while (i <= ds + nd);
        h += '</tr></table>';
        _('jm_dias').innerHTML = h;
    },
    setDia: function (d) {
        this.d = d;
        SelectorFecha.imprimir();
    },
    setMeses: function () {
        var h = '<select size="12" class="jm_mes" id="mes_' + this.r + '" onclick="SelectorFecha.setMes();">';
        for (var i = 8; i < 20; i++) {
            var se = (i == this.m + 7) ? 'selected' : '';
            h += '<option class="jm_mes" ' + se + ' value="' + (i - 7) + '">' + this.i[i] + '</option>';
        }
        h += '</select>';
        _('jm_meses').innerHTML = h;
    },
    setMes: function () {
        this.m = _('mes_' + this.r).value;
        SelectorFecha.setDias();
    },
    setAnios: function (t) {
        var h = '<select size="12" class="jm_mes" id="anio_' + this.r + '" onclick="SelectorFecha.setAnio();">\n';
        t = (t != undefined) ? t : this.a;
        h += '<option class="jm_mes" value="-">&nbsp;&nbsp;<< </option>\n';
        for (var i = t - 9; i <= t; i++) {
            var se = (this.a == i) ? 'selected' : '';
            h += '<option class="jm_mes" ' + se + ' value="' + i + '">' + i + '</option>\n';
        }
        h += '<option class="jm_mes" value="+">&nbsp;&nbsp;>> </option>\n';
        h += '</select>';
        _('jm_anios').innerHTML = h;
    },
    setAnio: function () {
        var a = _('anio_' + this.r);
        this.a = a.value;
        if (this.a == '-') {
            this.a = a.options[1].value;
            SelectorFecha.setAnios(a.options[1].value);
        }
        if (this.a == '+') {
            this.a = a.options[10].value;
            SelectorFecha.setAnios(parseInt(a.options[10].value) + 9);
        }
        SelectorFecha.setDias();
    },
    imprimir: function () {
        if (this.f.toLowerCase() == 'sql') {
            _(this.r).value = this.d + '/' + this.m + '/' + this.a;
        } else {
            _(this.r).value = this.a + '-' + this.m + '-' + this.d;
        }
        SelectorFecha.cerrar();
    },
    cerrar: function () {
        _Fch = true;
        SelectorFecha.remEvt();
        _R('Fech_' + this.r);
    },
    getTimestamp: function (f1, t1) {
        var t = t1 || '00:00';
        T = t.split(':');
        var f = null;
        if (f1.indexOf('/') > 0) {
            F = f1.split('/');
            f = new Date(parseInt(F[2], 10), parseInt(F[1], 10) - 1, parseInt(F[0], 10), parseInt(T[0], 10), parseInt(T[1], 10), 0);
        } else {
            F = f1.split('-');
            f = new Date(parseInt(F[0], 10), parseInt(F[1], 10) - 1, parseInt(F[2], 10), parseInt(T[0], 10), parseInt(T[1], 10), 0);
        }
        return (f.getTime());
    }
};
var _Vtns = new Array();
var _rX = 0;
var _rY = 0;
var _activa = -1;
var Ventana = {
    an: '200',
    al: '200',
    c: false,
    m: false,
    md: 'lg',
    bl: 'false',
    parametros: function (pr) {
        var p = pr.split(',');
        for (var i = 0; i < p.length; i++) {
            var vl = p[i].split('=');
            if (p[i].toLowerCase().indexOf('ancho') != -1) {
                this.an = vl[1];
            }
            if (p[i].toLowerCase().indexOf('alto') != -1) {
                this.al = vl[1];
            }
            if (p[i].toLowerCase().indexOf('cerrar') != -1) {
                this.c = vl[1];
            }
            if (p[i].toLowerCase().indexOf('modal') != -1) {
                this.m = vl[1];
            }
            if (p[i].toLowerCase().indexOf('model') != -1) {
                this.md = vl[1];
            }
            if (p[i].toLowerCase().indexOf('bloqueo') != -1) {
                this.bl = vl[1];
            }
        }
    },
    crear: function (i, t, p, h, f)
    {
        this.md = 'lg';
        this.an = '200';
        this.al = '200';
        this.bl = 'false';
        Ventana.parametros(p);
        if (_(i) == null) {
            var vl = _Vtns.length;
            var ht = h || '';
            var fn = f || '';
            var v = document.createElement("div");
            v.id = i;
            v.setAttribute("class", "modal fade");
            v.setAttribute("role", "dialog");
            v.setAttribute("tabindex", "-1");
            v.setAttribute("aria-hidden", "true");
            document.body.appendChild(v);
            var cr = (this.c == 'true') ? '<button type="button" class="close" data-dismiss="modal" onclick="Ventana.cerrar(\'' + i + '\');">&times;</button>' : '';
            var myestilo = 'style="';
            if (this.an !== '200') {
                myestilo += 'width: ' + this.an + '% !important; ';
            }
            if (this.al !== '200') {
                myestilo += 'height:  ' + this.al + '% !important; ';
            }
            myestilo += '"';
            var cu = '<div class="modal-dialog modal-' + this.md + '" ' + myestilo + '>';
            cu += '<div class="modal-content">';
            cu += '<div class="modal-header"><h4 class="modal-title" align="center"> ' + t + '</h4>' + cr + ' </div>';
            cu += '<div id="' + i + '_html">' + ht + '</div>';
            cu += '</div>';
            cu += '</div>';
            v.innerHTML = cu;
            var v = new Array(i, vl, true);
            _Vtns.push(v);
            _activa++;
        }
        Ventana.activar(i);
        return false;
    },
    cerrar: function (o)
    {
        if (_(o) !== null) {
            $('#' + o).modal('hide');
            $("#" + o + '_bloqueo').remove();
            _R(o);
            ////limipamos el objeto q se elimna
            for (var i = 0; i < _Vtns.length; i++) {
                if (_Vtns[i][0] == o) {
                    _Vtns.splice(i, 1);
                    break;
                }
            }
            var m = -1;
            ///buscamos el ultimo objeto activo y agregamos a la variable activa
            for (i = 0; i < _Vtns.length; i++) {
                if (_Vtns[i][1] > m) {
                    m = _Vtns[i][1];
                    _activa = i;
                }
            }
            ////agregamos nuevamente el evento scroll ala ultima ventana
            if (i > 0) {
                if (_(_Vtns[_activa][0]) !== null) {
                    $("#" + _Vtns[_activa][0]).css("overflow-y", "auto");
                }
            }
            ///sisq el artreglo es 0 activa sera -1
            if (_Vtns.length === 0) {
                _activa = -1;
            }

        }
    },
    activar: function (o)
    {
        if (_(o) !== null) {
            if (this.bl === 'true') {
                $('#' + o).modal(
                        {backdrop: 'static', keyboard: false}
                );
            }
            $('#' + o).modal('show');
            $(".modal-backdrop").attr('id', o + '_bloqueo');
        }
    }
};
function popup(t, an, al)
{
    var v = document.createElement("DIV");
    v.id = 'ppp';
    v.className = 'jm_ventana';
    var tp = (_getTop(_('pie')) - al) / 2;
    var iz = (document.body.clientWidth - an) / 2;
    v.style.top = (tp > 0 ? tp : 0) + 'px';
    v.style.left = (iz > 0 ? iz : 0) + 'px';
    v.style.width = an + 'px';
    v.style.height = al + 'px';
    v.style.zIndex = 100;
    document.body.appendChild(v);
    v.innerHTML = '<div class="jm_titulo"><div class="jm_txtTitulo">&nbsp;' + t + '</div><div class="jm_cerrar" onclick="_R(\'ppp\');" alt="Cerrar" title="Cerrar"></div></div><div id="ppp_html"></div>';
    _('ppp_html').style.height = (al - 40) + 'px';
}
function putcardbody(html1) {
    var html = '';
    html += "<div class=\"col-md-12\">";
    html += "<div class=\"card\">";
    html += "<div class=\"card-body\">";
    html += "<div class=\"row\">";
    html += "<div class=\"col-md-12\">";
    html += html1;
    html += "</div>";
    html += "</div>";
    html += "</div>";
    html += "</div>";
    html += "</div>";
    return html.toString();
}
var _FS = -1;
var _WR = '';
var _objT = null;
var Tabla = new Object();
Tabla = function (oc, id, s, t, nc, c, an, al, f, w, p) {
    this.oc = oc;
    this.id = id;
    this.s = s || 'FrmGetTabla';
    this.T = t;
    this.nc = nc;
    this.c = c;
    this.an = an;
    this.al = al;
    this.f = f;
    this.w = w;
    this.pg = p || 0;
    this.npg = 0;
    _objT = this;
    this.insertar();
};
Tabla.prototype = {
    insertar: function () {
        var ax = new Ajax();
        var t = ax.getTransporte();
        _FS = -1;
        t.onreadystatechange = function () {
            if (t.readyState == 4) {
                if (t.status == 200) {
                    _objT.setDatos(t.responseText);
                } else {
                    alert("\nSTATUS: " + t.status);
                }
            }
        };
        t.open('POST', this.s, true);
        t.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        t.send('t=' + this.T + '&c=' + this.c + '&w=' + this.w + '&p=' + this.pg);
    },
    setDatos: function (r) {
        if (_('btnFiltro') != null) {
            window.setTimeout("_('btnFiltro').disabled=false", 1000);
        }
        if (_('btnFiltro1') != null) {
            window.setTimeout("_('btnFiltro1').disabled=false", 1000);
        }
        if (_('btnFiltro2') != null) {
            window.setTimeout("_('btnFiltro2').disabled=false", 1000);
        }
        var nc = _objT.nc.split(',');
        var c = _objT.c.split(',');
        var an = _objT.an.split(',');
        if (r != null) {
            var vc = r.split('|');
            if (vc[1] != '{tbl:]}' && vc[1] != '{tbl:[') {
                var h = '<div class=\"table-responsive\">';
                h += '<table id="hd' + _objT.id + '" class=\"table\">';
                h += '<thead class=\" text-primary\"><th><input id="chT_' + _objT.id + '" type="checkbox" onclick="_objT.marcarTodo();"></th>';
                var s = 0;
                for (i = 1; i < c.length; i++) {
                    h += '<th>' + nc[i - 1] + '</th>';
                    s += parseInt(an[i]);
                }

                if (_objT.oc == 'd_11') {
                    _(_objT.oc).style.width = (s + 50) + 'px';
                    _('d_fila').style.width = (parseInt(_('d_11').style.width) + parseInt(_('d_21').style.width) + 40) + 'px';
                }
                h += '</thead><tbody>';
                var JSON = eval('(' + vc[1] + ')');
                for (i = 0; i < JSON.tbl.length; i++) {
                    var e = _objT.f.replace('^', "'" + JSON.tbl[i][0] + "'");
                    var fi = (i % 2 == 0) ? 'jm_filaPar' : 'jm_filaImp';
                    h += '<tr id="' + _objT.id + 'F' + i + '"><td><input id="chT_' + _objT.id + i + '" type="checkbox"><input id="' + _objT.id + i + '0" value="' + JSON.tbl[i][0] + '" type="hidden"></td>';
                    for (var j = 1; j < c.length; j++) {
                        h += '<td id="' + _objT.id + i + j + '"  style="cursor:pointer;" onclick="' + e + ';" >' + JSON.tbl[i][j].replace("~", '"') + '</td>';
                    }
                    h += '</tr>';
                }
                h += '</tbody></table></div>';
                _(_objT.oc).innerHTML = putcardbody(h);
                //_('hd' + _objT.id).style.width = (s + 25) + 'px';
                _('hd' + _objT.id).style.width = 'width: 100%;px';

                _objT.npg = vc[0];
                if (parseInt(vc[0]) > 0 && _('d_nav_pag') == null) {
                    btn.buton('fa fa-arrow-left', 'Anterior', "if(parseInt(_objT.pg)>0){_objT.pg--;_('d_nav_pag').value=_objT.pg;_objT.insertar();}", 'd_herra', '0');
                    _('d_herra').innerHTML += '<div class="d_nav"> &nbsp; <input id="d_nav_pag" size="3" value="0" onfocus="this.select();" onkeypress="_numero(event);if(_getKeyCode(event)==13){_objT.pg=parseInt(this.value);_objT.insertar();}" style="margin-top:3px" /> / <span id="d_nav_pags">' + (parseInt(vc[0])) + '</span> &nbsp; </div>';
                    btn.buton('fa fa-arrow-right', 'Siguiente', "if( parseInt(_objT.pg) < parseInt(_objT.npg) ){_objT.pg++;_('d_nav_pag').value=_objT.pg;_objT.insertar();}", 'd_herra', '0');
                } else if (parseInt(vc[0]) >= 0 && _('d_nav_pag') != null) {
                    _('d_nav_pags').innerHTML = vc[0];
                    if (parseInt(_('d_nav_pag').value) > parseInt(vc[0])) {
                        _objT.pg = _('d_nav_pag').value = 0;
                    }
                }
            } else {
                _(_objT.oc).innerHTML = putcardbody('NO SE HAN ENCONTRADO DATOS PARA MOSTRAR');
            }
        }
    },
    filaSobre: function (i) {
        if (_FS != i && _(_objT.id + 'F' + i) != null) {
            _(_objT.id + 'F' + i).className = 'jm_filaSobre';
        }
    },
    filaFuera: function (i) {
        if (_FS != i && _(_objT.id + 'F' + i) != null) {
            _(_objT.id + 'F' + i).className = (i % 2 == 0) ? 'jm_filaPar' : 'jm_filaImp';
        }
    },
    marcarFila: function (i) {
        if (_(_objT.id + 'F' + _FS) != null) {
            _(_objT.id + 'F' + _FS).className = (i % 2 == 0) ? 'jm_filaPar' : 'jm_filaImp';
        }
        if (i >= 0 && _(_objT.id + 'F' + i) != null) {
            _(_objT.id + 'F' + i).className = 'jm_filaSel';
            _FS = i;
        }
    },
    marcarTodo: function () {
        var f = _('chT_' + _objT.id).checked;
        var i = 0;
        var o = _('chT_' + _objT.id + i);
        while (o != null) {
            i++;
            o.checked = f;
            o = _('chT_' + _objT.id + i);
        }
    },
    eliminar: function (ms, st) {
        var m = ms || '';
        var s = st || 'EliminacionLog';
        var c = _objT.c.split(',');
        var p = 't=' + _objT.T + '&cpk=' + c[0] + '&c=';
        var i = 0;
        var o = _('chT_' + _objT.id + i);
        var a = '';
        var b = false;
        while (o != null) {
            if (o.checked) {
                if (i == _FS) {
                    b = true;
                }
                a += _(_objT.id + i + '0').value + ',';
            }
            i++;
            o = _('chT_' + _objT.id + i);
        }
        if (a.indexOf(',') > 0) {
            a = a.substr(0, a.length - 1);
            if (confirm(m + "\n¿Está seguro de querer eliminar el/los registros marcados?")) {
                if (b) {
                    _('d_21').innerHTML = '&nbsp;';
                }
                _AJAX.solicitud(s, p + a, false, null);
            }
        } else {
            alert('Debe marcar por lo menos un registro para poder eliminar.');
        }
    }
};
var _TbA = -1;
var _objTb = null;
var Tab = new Object();
Tab = function (o, w, h) {
    this.w = w || 150;
    this.h = h || 150;
    _(o).innerHTML = '<div class="grupoTab" style="width:' + (w + 10) + 'px;"><div id="tabR0"></div><div id="tabR1"></div></div>';
    _objTab = this;
};
Tab.prototype = {
    nuevo: function (t, m) {
        var i = 0;
        while (_('tab0' + i) != null) {
            i++;
        }
        _('tabR0').innerHTML += '<a id="tab0' + i + '" href="javascript:void(0);" class="jm_tab" onclick="_objTab.seleccionar(' + i + ');">' + t + '</a>';
        _('tabR1').innerHTML += '<div id="tab1' + i + '" class="jm_tabHTML" style="display:none;width:' + _objTab.w + 'px;height:' + _objTab.h + 'px;">' + m + '</div>';
    },
    seleccionar: function (i) {
        if (_('tab0' + _TbA) != null && _('tab1' + _TbA) != null) {
            _('tab0' + _TbA).className = 'jm_tab';
            _('tab1' + _TbA).style.display = 'none';
        }
        _('tab0' + i).className = 'jm_tabAct';
        _('tab1' + i).style.display = 'block';
        _TbA = i;
    }
};