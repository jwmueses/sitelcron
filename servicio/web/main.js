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
function _getFecha(tipo, dia1, date) {
    date = date || '';
    var F = new Date();
    if (date !== '') {
        F = date;
    }
    var anio = F.getFullYear();
    var mes = (F.getMonth() + 1);
    tipo = tipo || '';
    dia1 = dia1 || '';
    var dia = F.getDate();
    if (dia1 !== '') {
        dia = dia1;
    }
    var axMes = mes < 10 ? "0" + mes : mes;
    var axDia = dia < 10 ? "0" + dia : dia;
    var cad = anio + "-" + axMes + "-" + axDia;
    if (tipo.toUpperCase() === "SQL") {
        cad = axDia + "/" + axMes + "/" + anio;
    }
    return cad;
}
function _imprimir(p)
{
    window.open(p, '_blank', 'top=50,left=50,width=750,height=500');
}
window.onload = function () {
    ini_cargar();
};
function imprimirdiv(d) {
    var pdf = new jsPDF('p', 'pt', 'letter');
    source = $('#' + d)[0];

    specialElementHandlers = {
        '#bypassme': function (element, renderer) {
            return true
        }
    };
    margins = {
        top: 80,
        bottom: 60,
        left: 40,
        width: 522
    };

    pdf.fromHTML(
            source,
            margins.left, // x coord
            margins.top, {// y coord
                'width': margins.width,
                'elementHandlers': specialElementHandlers
            },
            function (dispose) {
                pdf.save('Prueba.pdf');
            }, margins
            );
}
function imprimirdivcss(d, t) {
    if (_('btn_imprmir') !== null) {
        _('btn_imprmir').disabled = true;
    }
    var x = x || 211;
    var y = y || 298;
    t = t || 'a4';
    if (t === 'a5') {
        x = 145;
        y = 145;
    }
    const filename = 'documento.pdf';
    html2canvas(document.querySelector('#' + d)).then(canvas => {
        let pdf = new jsPDF('p', 'mm', t);
        pdf.addImage(canvas.toDataURL('image/png'), 'PNG', 0, 0, x, y);
        pdf.save(filename);
        if (_('btn_imprmir') !== null) {
            _('btn_imprmir').disabled = false;
        }
    });
}
function encerar() {

    if (_('div_contenedor') !== null) {
        _('div_contenedor').innerHTML = '';
        if (_('div_total_contenedor') !== null) {
            $('#div_total_contenedor').perfectScrollbar('update');
        }
    }
    if (_('div_filtro') !== null) {
        _('div_filtro').innerHTML = '';
    }
    if (_('d_herra') !== null) {
        _('d_herra').innerHTML = '';
    }
}
function seg_cambiarclave()
{
    Ventana.crear('cmpclave', 'CAMBIO DE CONTRASEÑA', "model=sm,cerrar=true,bloqueo=true", '');
    _AJAX.solicitud('FrmCambiarClave', '', false, 'cmpclave_html');
}
function seg_cambiarclaveguardar(f)
{
    if (f.clave_nueva.value !== f.clave_nueva1.value) {
        alert('Las nuevas contraseñas son diferentes verifique');
        return false;
    }
    return _AJAX.enviarForm(f);
}
function seg_perfilcliente()
{
    encerar();
    _AJAX.solicitud('FrmPerfilCliente', '', true, 'body');
}
function ini_cargar()
{

    _('div_menu').innerHTML = "";
    _AJAX.solicitud('FrmMenu', '', true, 'body');
}
function pag_PagosPendiente(t)
{
    encerar();
    _AJAX.solicitud('FrmInstalacionReporte', 'tipo=' + t, true, 'body');
}
function pag_subirpago(id, i, id_prefactura, p)
{
    p = p || '0';
    Ventana.crear('cmpdetallepago', 'Notificar Pago', "cerrar=true,bloqueo=true", '');
    _AJAX.solicitud('FrmPagoSubir', 'id=' + id + '&indice=' + i + '&id_prefactura=' + id_prefactura + '&proviene=' + p, false, 'cmpdetallepago_html');
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
        if (_('div_contenedor') !== null) {
            pag_PagosPendiente('p');
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
function pag_consultarpago() {
//    encerar();
//    _AJAX.solicitud('FrmFiltro', 'op=1&fun=pag_buscarpagorealiados();', false, '');
    pag_buscarpagorealiados();

}
function pag_buscarpagorealiados()
{
    var w = 'where id_cliente=' + _('id_cliente').value;
    w += " and fecha_registro between '" + _('fi').value + "' and '" + _('ff').value + "' ";
    _WR = encodeURI(w);
    new Tabla('div_reportados', 'jmTbl', '', 'vta_documento_sitio', 'DIRECCION INSTALACION,ESTADO SERVICIO,CANAL PAGO,ESTADO TRAMITE', 'id_documento_sitio ,direccion_instalacion,txt_estado_servicio,txtcanal_pago,txtestado_tramite', '0,0,0,0,0', _altEdi, 'pag_abrirpago(^);', _WR);
}
function pag_abrirpago(id)
{
    Ventana.crear('cmppago', 'Informacion acerca del pago', "cerrar=true,bloqueo=true", '');
    _AJAX.solicitud('FrmPagoInfo', 'id=' + id, false, 'cmppago_html');
}
function rec_reportarreclamo(id, i)
{
    Ventana.crear('cmpreclamo', 'Notificar un soporte tecnico', "cerrar=true,bloqueo=true", '');
    _AJAX.solicitud('FrmReclamo', 'id=' + id + '&indice=' + i, false, 'cmpreclamo_html');
}
function rec_consultaropciones(obj, id) {
    if (_('div_encuenta') !== null) {
        _('div_encuenta').innerHTML = '';
        _('div_encuenta').style.display = 'block';
    }
    if (id !== '') {
        _AJAX.solicitud('FrmReclamoOpcion', 'obj=' + obj + '&id=' + id, false, '');
    }
    if (id === '1' || id === '3') {
        if (_('divimagen') !== null) {
            _('divimagen').style.display = 'block';
            _('file').required = true;
        }
    } else {
        if (_('divimagen') !== null) {
            _('divimagen').style.display = 'none';
            _('file').required = false;
        }
    }
}
function rec_guardareclamo()
{
    _C('cmpreclamo');
    if (_('btn_reportar') !== null) {
        _('btn_reportar').disabled = true;
    }
}
function finTransferenciareclamo(e, m)
{
    _('btn_reportar').disabled = false;
    _R('jm_carg');
    if (e === 0) {
        Ventana.cerrar('cmpreclamo');
        rec_buscarreclamosrealiados();
    }
    alert(m);
}
//function rec_consultarreclamos() {
//    encerar();
//    _AJAX.solicitud('FrmFiltro', 'op=1&fun=rec_buscarreclamosrealiados();', false, '');
//
//}
function gen_cargarinstalacion(t, o, id, i, es) {
    t = t || '';
    o = o || '';
    id = id || '';
    i = i || '';
    es = es || '';
    if (_(o) !== null) {
        _(o).innerHTML = '';
    }
    _AJAX.solicitud('FrmConsultaInstalacion', 'op=' + t + '&obj=' + o + '&id_instalacion=' + id + '&i=' + i + '&estado=' + es, true, 'body');
}
function rec_buscarreclamosrealiados()
{
    var w = 'where id_clientei=' + _('id_cliente').value;
    w += ' and id_instalacion=' + _('id_instalacion').value;
    w += " and fecha_llamada between '" + _('fi').value + "' and '" + _('ff').value + "' ";
    _WR = encodeURI(w);
    console.log(w);
    new Tabla('div_reportados', 'jmTbl', '', 'vta_soporte', 'N° SOPORTE, DIRECCION INSTALACIÓN, ESTADO SERVICIO,FECHA LLAMADA, ESTADO RECLAMO', 'id_soporte ,numero_soportei,direccion_instalacion ,txt_estado_servicio,fecha_llamada,txt_estado', '0,0,0,0,0', _altEdi, 'rec_abrirrreclamo(^);', _WR);
}
function rec_abrirrreclamo(id)
{
    Ventana.crear('cmpreclamo', 'Informacion acerca del soporte', "cerrar=true,bloqueo=true", '');
    _AJAX.solicitud('FrmReclamoInfo', 'id=' + id, false, 'cmpreclamo_html');
}
function sus_servicio(id, idi)
{
    Ventana.crear('cmpsuspencion', 'SUSPENCION TEMPORAL', "cerrar=true,bloqueo=true", '');
    _AJAX.solicitud('FrmSuspencion', 'id=' + id + '&id_instalacion=' + idi, false, 'cmpsuspencion_html');
}
function sus_menorFecha(e)
{
    if (_(e) !== null) {
        var fecha_ingreso = new Date(_(e).value);
        var fecha_actual = new Date(_fecha_sistema);
        var fecha_mas = new Date(_fecha_sistema);
        fecha_mas.setMonth(fecha_mas.getMonth() + 1);
        fecha_mas.setDate(1);
        if (fecha_ingreso <= fecha_actual) {
            _(e).value = _fecha_sistema;
        }
    }
}
function sus_suspencionguardar(f) {
    _C('cmpsuspencion');
    if (_('btn_reportar') !== null) {
        _('btn_reportar').disabled = true;
    }
    return _AJAX.enviarForm(f);
}
//function sus_buscarsuspenciones() {
//    encerar();
//    _AJAX.solicitud('FrmFiltro', 'op=1&fun=sus_buscarsuspencioneshechas();', false, '');
//
//}
function sus_buscarsuspencioneshechas()
{
    var w = 'where id_cliente0=' + _('id_cliente').value;
    w += ' and id_instalacion=' + _('id_instalacion').value;
    w += " and fecha_solicitud between '" + _('fi').value + "' and '" + _('ff').value + "' ";
    _WR = encodeURI(w);
    new Tabla('div_suspendidos', 'jmTbl', '', 'vta_instalacion_suspension', 'SUCURSAL,SECTOR,DIRECCION INSTALACION,ESTADO SERVICIO,FECHA INICIO, FECHA FIN,ESTADO SUSPENCION', 'id_instalacion_suspension,txt_sucursal0,sector0,direccion_instalacion0,txt_estado_servicio0,fecha_inicio,fecha_termino,estado_suspencion0', '0,0,0,0,0,0,0', _altEdi, 'sus_servicio(^);', _WR);
}
//contratos
function con_contratarcarga()
{
    encerar();
    _AJAX.solicitud('FrmContratar', '', true, 'body');
}
function con_informacioncontratar(i, s)
{
    var id_sucursal = '';
    var id_sector = '';
    if (_(i) !== null) {
        id_sucursal = _(i).value;
    }
    if (_(s) !== null) {
        id_sector = _(s).value;
    }
    _('planes_sector').innerHTML = '';
    _AJAX.solicitud('FrmConsultaContratar', 'op=1&id_sucursal=' + id_sucursal + '&obj=datos_sucursal&id_sector=' + id_sector, true, 'body');
}
function con_informacionplanes(i, s)
{
    var id_sucursal = '';
    var id_sector = '';
    if (_(i) !== null) {
        id_sucursal = _(i).value;
    }
    if (_(s) !== null) {
        id_sector = _(s).value;
    }
    _AJAXT.solicitud('FrmConsultaContratar', 'op=2&obj=planes_sector&id_sector=' + id_sector + '&id_sucursal=' + id_sucursal, true, 'body');
}
function abrir_detalle_plan(id_plan)
{
    Ventana.crear('cmpdetalleplan', 'Información acerca del plan', "cerrar=true", '');
    _AJAX.solicitud('FrmDetallePlan', 'id_plan=' + id_plan, false, 'cmpdetalleplan_html');
}
function getUbicacion(o, cb, p, w)
{
    var idP = _(p) != null ? _(p).value : 1;
    if (_pX != '' || _pX != 0) {
        _pX = _pX.replace(/:/g, "'");
    }
    if (_pY != '' || _pY != 0) {
        _pY = _pY.replace(/:/g, "'");
    }
    _AJAX.solicitud("FrmConsultaContratar", "op=3&obj=" + o + '&comb=' + cb + "&pdr=" + idP + "&an=" + w + '&onCh=' + _pX + '&fun=' + _pY, false, o);
    _pX = 0;
    _pY = 0;
}
function con_contratarplanilla(is, se, ip)
{
    encerar();
    _AJAX.solicitud('FrmContratarPlanilla', 'id_sucursal=' + is + '&id_sector=' + se + '&id_plan=' + ip, true, 'body');
}
function con_llamarcontrato() {
    var w = '';

    _AJAX.solicitud('FrmContratarPlanilla', w, true, 'body');
}
function fac_setPromocionInstalacion()
{

    var jSP = eval('(' + _('axprec').innerHTML + ')');
    var iva_vigente = parseFloat(_('iva_vigente').value);
    var valor_sector = parseFloat(jSP.tbl[0][3]);
    valor_sector = ((valor_sector * iva_vigente) / 100) + valor_sector;
    var valor_sucursal = parseFloat(jSP.tbl[0][7]);
    valor_sucursal = ((valor_sucursal * iva_vigente) / 100) + valor_sucursal;
    var tiempo_permanecia = jSP.tbl[0][8];
    var valor_instalacion = 0;
    var valor_factura = 0;
    var valor_pendiente = 0;
    if (valor_sector > valor_sucursal) {
        valor_instalacion = valor_sector;
        valor_factura = valor_sector;
    } else {
        valor_instalacion = valor_sucursal;
        valor_factura = valor_sector;
        valor_pendiente = valor_sucursal - valor_sector;
    }
    var condiciones = '';
    condiciones += '<p>CONDICIONES DE CONTRATO';
    condiciones += '<br>TIEMPO MINIMO DE PERMANENCIA ' + tiempo_permanecia + ' MESES';
    condiciones += '<br>COSTO DE INSTALACION: ' + _RD(valor_instalacion) + ' USD, COSTO FACTURADO: ' + _RD(valor_factura) + ' USD, COSTO BENEFICIO PENDIENTE: ' + _RD(valor_pendiente) + ' USD';
    condiciones += '<br>NOTA: SI EL CLIENTE SE RETIRA ANTES DEL TIEMPO MINIMO DE PERMANECIA DEBERA PAGAR EL COSTO BENEFICIO PENDIENTE: ' + _RD(valor_pendiente) + ' USD </p>';
    if (_('nompro') !== null) {
        _('nompro').value = '';
        _('benpro').value = '';
        _('tiepro').value = '';
        _('idprom').value = '';
    }
    if (_('axPromociones') !== null) {
        if (_('axPromociones').innerHTML !== '' && _('axPromociones').innerHTML !== '{tbl:[' && _('axPromociones').innerHTML !== '{tbl:]}') {
            var jS = eval('(' + _('axPromociones').innerHTML + ')');
            var id = '-1';
            var nums = document.getElementsByName('setPromo');
            for (var i = 0; i < nums.length; i++) {
                if (document.getElementsByName('setPromo')[i].checked) {
                    id = document.getElementsByName('setPromo')[i].value;
                }
            }
            var x = _enMatrizJSON(jS, id, 0);
            var coPa = '<select id="convenio_pago" name="convenio_pago" class=\"form-control\" onchange=\"fac_instSetDatosFactura();\">';
            coPa += '<option value=""> SELECCIONE </option>';
            if (x != -1) {
                if (jS.tbl[x][10] == 't') {
                    coPa += '<option value="0">Prepago</option>';
                }
                if (jS.tbl[x][11] == 't') {
                    coPa += '<option value="1">Postpago</option>';
                }
                valor_instalacion = valor_sucursal;
                if (valor_sector > valor_sucursal) {
                    valor_instalacion = valor_sector;
                }
                var porcentaje_promocion = parseFloat(jS.tbl[x][9]);
                tiempo_permanecia = jS.tbl[x][12];
                valor_factura = valor_instalacion - (valor_instalacion * porcentaje_promocion) / 100;
                valor_pendiente = valor_instalacion - valor_factura;
                condiciones = '';
                condiciones += '<p>CONDICIONES PARA APLICAR A ESTA PROMOCION';
                condiciones += '<br>TIEMPO MINIMO DE PERMANENCIA ' + tiempo_permanecia + ' MESES';
                condiciones += '<br>COSTO DE INSTALACION: ' + _RD(valor_instalacion) + ' USD, COSTO FACTURADO: ' + _RD(valor_factura) + ' USD, COSTO BENEFICIO PENDIENTE: ' + _RD(valor_pendiente) + ' USD';
                condiciones += '<br>NOTA: SI EL CLIENTE SE RETIRA ANTES DEL TIEMPO MINIMO DE PERMANECIA DEBERA PAGAR EL COSTO BENEFICIO PENDIENTE: ' + _RD(valor_pendiente) + ' USD </p>';
                if (_('nompro') !== null) {
                    _('nompro').value = jS.tbl[x][1];
                    _('benpro').value = jS.tbl[x][9];
                    _('tiepro').value = jS.tbl[x][12];
                    _('idprom').value = id;
                }
            } else {
                coPa += '<option value="0">Prepago</option><option value="1">Postpago</option>';
            }
            coPa += '</select>';
            _('axConvenioPago').innerHTML = coPa;
        }
    }
    _('detalle_promocion').innerHTML = condiciones;
    _('tiempo_permanencia').value = tiempo_permanecia;
    _('costo_facturado').value = _RD(valor_factura);
    fac_instSetDatosFactura();
}
function fac_instSetDatosFactura()
{
    var descPro = 0;
    var pDescPro = 0;
    var id = '-1';
    if (_('axPromociones') !== null) {
        if (_('axPromociones').innerHTML !== '' && _('axPromociones').innerHTML !== '{tbl:[' && _('axPromociones').innerHTML !== '{tbl:]}') {
            var okPlan = false;
            var jSPlanes = eval('(' + _('axPromocionesPlanes').innerHTML + ')');

            var jS1 = eval('(' + _('axPromociones').innerHTML + ')');
            var nums = document.getElementsByName('setPromo');
            for (var i = 0; i < nums.length; i++) {
                if (document.getElementsByName('setPromo')[i].checked) {
                    id = document.getElementsByName('setPromo')[i].value;
                }
            }
            var x = _enMatrizJSON(jS1, id, 0);
            if (x != -1 && _('id_plan') != null) {
                okPlan = _enMatrizJSONClaves(jSPlanes, new Array(id, _('id_plan').value), new Array(0, 1)) >= 0;
            }
            if (x != -1 && okPlan) {
                if (jS1.tbl[x][8] == 't' && parseFloat(jS1.tbl[x][9]) <= 100) {
                    pDescPro = parseFloat(jS1.tbl[x][9]);
                }
                if (jS1.tbl[x][8] == 'f' && parseFloat(jS1.tbl[x][9]) > 0) {
                    descPro = parseFloat(jS1.tbl[x][9]);
                }
            }
        }
    }
    var idS = _('id_sector').value;
    if (idS !== '-0') {
        console.log('entra aser acalculo' + descPro + '  ' + pDescPro);
        var r = 0;
        var jS = eval('(' + _('axprec').innerHTML + ')');
        var i = 0;
        var piva = parseFloat(_('iva_vigente').value);
        var valor_sector = parseFloat(jS.tbl[0][3]);
        var valor_sucursal = parseFloat(jS.tbl[0][7]);
        var valor_factura = valor_sector;
        console.log('ver: ' + valor_factura + '  ' + id);
        if (id !== '-1') {
            valor_factura = valor_sucursal;
            if (valor_sector > valor_sucursal) {
                valor_factura = valor_sector;
            }
        }
        var sub0 = valor_factura;
        var des0 = pDescPro > 0 ? _RD((sub0 * pDescPro) / 100) : descPro;
        var iva0 = _RD((sub0 - des0) * piva / 100);
        var tot0 = _RD(sub0 - des0 + iva0);
        _('sub0').value = sub0;
        _('des0').value = des0;
        _('iva0').value = iva0;
        _('tot0').value = tot0;
        var sub1 = 0;
        var des1 = 0;
        var iva1 = 0;
        var tot1 = 0;
        _('ite1').style.visibility = 'hidden';
        if (_('convenio_pago').value === '0') {
            _('ite1').style.visibility = 'visible';
            sub1 = parseFloat(jS.tbl[i][1]);
            des1 = 0;
            iva1 = _RD((sub1 - des1) * piva / 100);
            tot1 = _RD(sub1 - des1 + iva1);
            _('sub1').value = sub1;
            _('des1').value = des1;
            _('iva1').value = iva1;
            _('tot1').value = tot1;
            r++;
        }
        if (_('itemh') !== null) {
            _('itemh').value = r;
        }
        _('fsub').value = (sub0) + (sub1);
        _('fdes').value = des0 + des1;
        _('fiva').value = iva0 + iva1;
        _('ftot').value = tot0 + tot1;
    }
}
function con_guardarcontrato(f) {
    var id_sector = _('id_sector').value;
    var id_plan = _('id_plan').value;
    var iva_vigente = _('iva_vigente').value;
    var id_sucursal = _('id_sucursal').value;
    if (id_sector === '' || id_plan === '' || iva_vigente === '' || id_sucursal === '') {
        alert('HA OCURRIDO UN ERROR REPITA NUEVAMENTE EL PROCESO DE CONTRATACIóN');
        return false;
    }

    var prv = _('prv').value;
    var ci = _('ci').value;
    var prr = _('prr').value;
    if (_('prv_detalle') !== null) {
        _('prv_detalle').value = (prv.trim() !== '' ? _('prv').options[_('prv').selectedIndex].innerText : '');
        _('ci_detalle').value = (ci.trim() !== '' ? _('ci').options[_('ci').selectedIndex].innerText : '');
        _('prr_detalle').value = (prr.trim() !== '' ? _('prr').options[_('prr').selectedIndex].innerText : '');
    }
    var direccion_inmuble = _('direccion_inmuble').value;
    var file = _('file').value;
    if (prv === '-0' || ci === '-0' || (prr === '-0' || prr === '') || direccion_inmuble.trim() === '' || file.trim() === '') {
        alert('TODOS LOS CAMPOS DE DIRECCION INMUEBLE QUE RECIBE EL SERVICIO SON OBLIGATORIOS');
        return false;
    }
    var convenio_pago = _('convenio_pago').value;
    if (convenio_pago.trim() === '' || convenio_pago === '-0') {
        alert('POR FAVOR ELIGA LA MODALIDAD DE CONVENIO DE COBRO');
        return false;
    }
    var acepta_contrato = _('acepta_contrato').checked;
    if (!acepta_contrato) {
        alert('PARA PODER CONTRATAR NUESTROS SERVICIOS DEBE ACEPTAR NUESTRAS CONDICIONES.');
        return false;
    }
    var ok = confirm("ESTE SEGURO DE CONTINUAR CON LA CONTRACIÓN DE NUESTROS SERVICIOS  !");
    if (!ok) {
        return false;
    } else {
        _C();
        if (_('btn_contratar') !== null) {
            _('btn_contratar').disabled = true;
        }
        f.submit();
    }
}

function finTransferenciacontratacion(e, m)
{
    _('btn_contratar').disabled = false;
    _R('jm_carg');
    if (e === 0) {
        encerar();
        con_buscarcontratoshechos('c');
    }
    alert(m);
}
function con_buscarcontratoshechos(t)
{
    encerar();
    _AJAX.solicitud('FrmInstalacionContrato', 'tipo=' + t, true, 'body');
}
function con_instalacion(id)
{
    Ventana.crear('cmpdetalleinstalacion', 'Contrato de Instalacion', "cerrar=true,bloqueo=true", '');
    _AJAX.solicitud('FrmInstalacion', 'id=' + id, false, 'cmpdetalleinstalacion_html');
}
/////facturacion 
function fac_documentoselectronicos()
{
    encerar();
    _AJAX.solicitud('FrmDocumentoElectronico', '', true, 'body');
}
function fac_buscardocumentoselectronicos(t)
{
    var p = 'op=' + t;
    if (t === 'f' || t === 'n') {
        var fi = _('fecha_ini' + t).value;
        var ff = _('fecha_fin' + t).value;
        var numero = _('numero_' + t).value;
        var obj = 'div_' + t;
        p += '&fi=' + fi + '&ff=' + ff + '&numero=' + numero + '&obj=' + obj;
    }
    _AJAXT.solicitud('FrmConsultaDocumento', p, true, 'body');
}
function fac_genXmlSri(ca, id, fo, ti) {
    if (ti === 'f') {
        if (fo === 'pdf') {
            window.open('http://138.185.136.142/html/js/procesos/generarPdf.php?claveFac=' + ca + '&id=' + id, '_blank');
        } else if (fo === 'xml') {
            window.open('http://138.185.136.142/html/js/procesos/generarXml.php?idFac=' + ca + '&id=' + id, '_blank');
        }
    }
    if (ti === 'n') {
        if (fo === 'pdf') {
            window.open('http://138.185.136.142/html/js/procesos/generarPdf.php?claveFac=' + ca + '&id=' + id, '_blank');
        } else if (fo === 'xml') {
            window.open('http://138.185.136.142/html/js/procesos/generarXmlNotas.php?idFac=' + ca + '&id=' + id, '_blank');
        }
    }

}