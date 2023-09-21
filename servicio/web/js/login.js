$(document).ready(function () {
    $('.signup-slider').slick({
        dots: true,
        arrows: false,
        autoplay: true,
        autoplaySpeed: 2000
    });
    $("img").height($(".main-box").height());
    $(".to-signin").on("click", function () {
        $(this)
                .addClass("top-active-button")
                .siblings()
                .removeClass("top-active-button");
        _('div_restablecer').style.display = 'none';
        $(".form-signup").slideUp(500);
        $(".form-signin").slideDown(500);
    });
    $(".to-signup").on("click", function () {
        $(this)
                .addClass("top-active-button")
                .siblings()
                .removeClass("top-active-button");
        _('div_restablecer').style.display = 'none';
        $(".form-signin").slideUp(500);
        $(".form-signup").slideDown(500);
    });
    $(".to-signin-link").on("click", function () {
        $(".to-signin")
                .addClass("top-active-button")
                .siblings()
                .removeClass("top-active-button");
        $(".form-signup").slideUp(200);
        $(".form-signin").slideDown(200);
    });
    $(".to-signup-link").on("click", function () {
        $(".to-signup")
                .addClass("top-active-button")
                .siblings()
                .removeClass("top-active-button");
        $(".form-signin").slideUp(200);
        $(".form-signup").slideDown(200);
    });
});
//functiones
document.getElementById("defaultOpen").click();
function opentabs(evt, id) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    document.getElementById(id).style.display = "block";
    evt.currentTarget.className += " active";
}

var _AJAX = new Ajax();
var ruc = document.getElementById("ruc");
ruc.addEventListener("keyup", function (event) {
    if (event.keyCode === 13) {
        buscarcliente();
    }
});
function buscarcliente(s, o) {
    s = s || 's';
    o = o || 'ruc';
    if (_(o) !== null) {
        if (_(o).value === '') {
            alert('Ingrese el RUC - CÉDULA - PASAPORTE');
            return false;
        }
        _AJAX.solicitud('FrmBuscarCliente', 'ruc=' + _(o).value + '&tok=' + s, true, 'body');
    }
}
function limpiar(s) {
    s = s || 's';
    if (s === 's') {
        if (_('existe') !== null) {
            _('ruc').value = '';
            _('ruc').focus();
            _('existe').style.display = 'none';
            _('es').value = '';
            _('id').value = '';
            _('idi').value = '';
            _('cliente').value = ' r';
            _('email').value = 'c';
            _('clave').required = _('clave1').required = _('id').required = _('cliente').required = _('email').required = false;
            _('btn_registro').type = 'button';
            _('condiciones').checked = false;
        }
    } else {
        _('esnu').value = '';
        _('idnu').value = '';
        _('runu').value = '';
        _('rsnu').value = '';
        _('tenu').value = '';
        _('te_clnu').value = '';
        _('emailnu').value = '';
        _('direccionnu').value = '';
        _('runu').readonly = _('rsnu').readonly = _('tenu').readonly = _('te_clnu').readonly = _('emailnu').readonly = _('direccionnu').readonly = false;
        _('condicionesnu').checked = false;
    }
}
function existe_nuevo(es, i, r, t, c, e, d) {
    _('esnu').value = es;
    _('idnu').value = i;
    _('rsnu').value = r;
    _('tenu').value = t;
    _('te_clnu').value = c;
    _('emailnu').value = e;
    _('direccionnu').value = d;
    _('runu').readonly = _('rsnu').readonly = _('emailnu').readonly = _('direccionnu').readonly = true;
}
function existe(e, i, ii, r, c) {
    if (_('existe') !== null) {
        _('existe').style.display = 'block';
        _('es').value = e;
        _('id').value = i;
        _('idi').value = ii;
        _('cliente').value = r;
        _('email').value = c;
        _('clave').required = _('clave1').required = _('es').required = _('id').required = _('cliente').required = _('email').required = true;
        _('btn_registro').type = 'submit';
        _('btn_registro').focus();
    }
}
function seg_ingresanuevo(f) {
    var condiciones = _('condiciones').checked;
    if (!condiciones) {
        alert('PARA ACCEDER A ESTE SERVICIO ACEPTE LAS CONDICIONES');
        return false;
    }
    var a = _('clave').value;
    var b = _('clave1').value;
    if (a.length < 8) {
        alert('LAS CONTRASEÑA INGRESADA DEBE TENER ALMENOS 8 DIGITOS');
        return false;
    }
    if (a !== b) {
        alert('LAS CONTRASEÑAS INGRESADAS NO SON IGUALES ');
        return false;
    }
    return _AJAX.enviarForm(f, true);
}
function seg_ingresanuevocliente(f) {
    var td = _('tipo_documentonu').value;
    var ru = _('runu').value;
    if (td != '06') {
        if (td == '04') {
            if (ru.length != 13) {
                alert('El número del RUC debe tener 13 dígitos');
                return false;
            }
        }
        if (td == '05') {
            if (ru.length != 10) {
                alert('El número de cédula debe tener 10 dígitos');
                return false;
            }
        }
        if (td != '06') {
            if (!esDocumento('runu')) {
                return false;
            }
        }
    }
    var condiciones = _('condicionesnu').checked;
    if (!condiciones) {
        alert('PARA ACCEDER A ESTE SERVICIO ACEPTE LAS CONDICIONES');
        return false;
    }
    return _AJAX.enviarForm(f, true);
}
function ocultarlogin(i) {
    i = i || '';
    if (i === '') {
        _('div_restablecer').style.display = 'block';
        _('div_login').style.display = 'none';
    } else {
        _('div_restablecer').style.display = 'none';
        _('div_login').style.display = 'block';
        _('usuarior').value = '';
        _('emailr').value = '';
    }

}
function seg_restablecerclave(f) {
    return _AJAX.enviarForm(f, true);
}


