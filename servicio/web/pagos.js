var _esSf = window.navigator.appVersion.toLowerCase().indexOf('safari') > 0 ? true : false;
var _esWin = navigator.appVersion.toLowerCase().indexOf("windows") >= 0 ? true : false;
var _altBody = 0;
var _altEdi = 0;
var _anchBody = 0;
var _centroX = 0;
var _centroY = 0;
var _OBJ = null;
var _pX = 0;
var _pY = 0;
var _wOb = 0;
var _AUX = '';
var _AX_DET = '';
var _SBM = false;
var btn = new Boton();
var _AJAX = new Ajax();
var _AJAXT = new Ajax();
var axPg = 0;
var axNpg = 0;
window.onload = function () {
    pag_CargaConsulta();
};
function pag_CargaConsulta()
{
    _('div_contenedor').innerHTML = "";
    _AJAX.solicitud('FrmPagoRegistro', '', false, null);
}
function pag_ConsultarInstalacion(f)
{
    if (_('div_instalaciones') !== null) {
        _('div_instalaciones').innerHTML = '';
    }
    return _AJAX.enviarForm(f);
}
function pag_subirpago(id, i, id_prefactura)
{
    Ventana.crear('cmpdetallepago', 'Notificar Pago', "cerrar=true,bloqueo=true", '');
    _AJAX.solicitud('FrmPagoSubir', 'id=' + id + '&indice=' + i + '&id_prefactura=' + id_prefactura, false, 'cmpdetallepago_html');
}
function pag_subirpagoguardar()
{
    _C('cmpdetallepago');
    if (_('btn_reportar') !== null) {
        _('btn_reportar').disabled = true;
    }
}
function finTransferenciapago(e, m)
{
    _('btn_reportar').disabled = false;
    _R('jm_carg');
    if (e === 0) {
        Ventana.cerrar('cmpdetallepago');
        if (_('txt') !== null) {
            _('txt').value = '';
        }
        if (_('div_instalaciones') !== null) {
            _('div_instalaciones').innerHTML = '';
        }
    }
    alert(m);
}
function pag_formapago() {
    var op = _('canal').value;
    if (op === 'd' || op === 't') {
        _('div_banco').style.display = 'block';
        _('id_banco').required = true;
    } else {
        _('div_banco').style.display = 'none';
        _('id_banco').required = false;
    }
}