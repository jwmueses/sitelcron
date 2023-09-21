/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var _esN = window.navigator.appName=='Netscape' ? true : false;

function setFormulario(id)
{
    if ( document.getElementById('vta') == null) {
        var d = document.createElement('DIV');
        d.id = 'vta';
        d.className = 'ventana';
        document.body.appendChild(d);
        let html = "<form id='frmPostulacion' action='FrmOfertaLaboralPostularGuardar' onsubmit='postulacionGuardar(this.form)' enctype='multipart/form-data' method='post' autocomplete='off'>";        
            html += "<input type='hidden' id='idOfertaEmpleo' name='idOfertaEmpleo' value='"+id+"' />";
            html += "<div class='panel-top2' style='text-align:center'>";
            html += "<div class='H2'>Trabaja con nosotros</div>";
            html += "<div class='H4'>¡Nos alegra que estés interesado en formar parte de nosotros!</div>";
            html += "</div><hr style='margin-right:70px' />";
            
            html += "<p>&nbsp;</p>";
            
            html += "<table width='100%'>";
            html += "<tr><td>NOMBRES</td> <td><input type='text' id='nombre' name='nombre' required /></td>";
            html += "<td>APELLIDOS</td> <td><input type='text' id='apellido' name='apellido' required /></td></tr>";
            
            html += "<tr><td>CORREO ELECTRONICO</td> <td><input type='text' id='correo' name='correo' required /></td>";
            html += "<td>FECHA NACIMIENTO</td> <td><input type='date' id='fecha_nacimiento' name='fecha_nacimiento' required /></td></tr>";
            
            html += "<tr><td>CUIDAD</td> <td><input type='text' id='ciudad' name='ciudad' required /></td>";
            html += "<td>PROFESION</td> <td><input type='text' id='profesion' name='profesion' required /></td></tr>";
            
            html += "<tr><td>CELULAR</td> <td><input type='number' id='celular' name='celular' required /></td>";
            html += "<td>ASPIRACION SALARIAL</td> <td><input type='number' id='aspiracion' name='aspiracion' required /></td></tr>";
            
            html += "<tr><td>CARTA DE PRESENTACION</td> <td><textarea id='carta' name='carta' required></textarea></td>";
            html += "<td>HOJA DE VIDA (PDF)</td> <td><input type='file' id='hoja_vida' name='hoja_vida' accept=\".pdf\" required /></td></tr>";
            html += "</table>";
            
            html += "<div class='btn-oferta' style='padding-right:70px'><input class='boton2' type='button' value='Cerrar' onclick=\"_R('vta')\" /> &nbsp;&nbsp;&nbsp; <input class='boton' type='submit' value='Enviar' /></div>";
        
        d.innerHTML = html;
        document.getElementById('nombre').focus();
    }
}
function _(o)
{
    var v = (parseInt(window.navigator.appVersion)==4) ? true : false;
    return (document.getElementById) ? document.getElementById(o) : ((document.all) ? document.all[o] :((_esN && v) ? document.layers[o] : null));
}
function _R(b)
{
    var o = _(b);
    if(o!=null){
        if(_esN){
            o.parentNode.removeChild(o);
        }else{
            o.removeNode(true);
        }
    }
}