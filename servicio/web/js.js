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
    cargarmenu();
};
function cargarmenu()
{
    _('div_menu').innerHTML = "";
    _AJAX.solicitud('FrmMenuPlanes', '', false, null);
    _AJAXT.solicitud('FrmPlanes', '', false, null);
}

function abrir_detalle_plan(id_plan)
{
    Ventana.crear('cmpdetalleplan', 'Información acerca del plan', "cerrar=true", '');
    _AJAX.solicitud('FrmDetallePlan', 'id_plan=' + id_plan, false, 'cmpdetalleplan_html');
}

function cargarplanesfiltro(id)
{
    _('div_contenedor').innerHTML = "";
    _AJAXT.solicitud('FrmPlanes', 'id=' + id, false, null);
}

