<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width,initial-scale=1">
        <link href="css/login.css" rel="stylesheet" type="text/css"/>
        <title>SAITEL</title>
        <link rel="icon" type="image/png" href="img/saitel.png" />
        <script src="js/jquery-3.4.1.min.js" type="text/javascript"></script>
        <link href="css/slick.css" rel="stylesheet" type="text/css"/>
        <script src="js/slick.js" type="text/javascript"></script>
        <link href="lib/alerfity/css/alertify.min.css" rel="stylesheet" type="text/css"/>
        <script src="nucleo/Nucleo.js" type="text/javascript"></script>
    </head>
    <body>

        <div class="main-box">
            <div class="slider-cont">
                <div class="signup-slider">
                    <div class="img-txt">
                        <div class="img-layer"></div>
                        <h1>Contrate nuestros servicios </h1>
                        <img src="img/contrato.jpg" class="imgslide"/>
                    </div>
                    <div class="img-txt">
                        <div class="img-layer"></div>
                        <h1>Reporte sus pagos realizados</h1>
                        <img src="img/pagos.jpg" class="imgslide"/>
                    </div>
                    <div class="img-txt">
                        <div class="img-layer"></div>
                        <h1>Contacte con soporte soporte técnico</h1>
                        <img src="img/soporte.jpg" class="imgslide"/>
                    </div>
                    <div class="img-txt">
                        <div class="img-layer"></div>
                        <h1></h1>
                        <img src="img/servicios.jpg" class="imgslide"/>
                    </div>
                </div>
            </div>


            <div class="form-cont">

                <div class="top-buttons">
                    <button class="to-signup top-active-button">
                        Registrate
                    </button>
                    <button class="to-signin" id="btn_iniciar_sesion">
                        Iniciar Sesión
                    </button>
                </div>
                <div class="form form-signup">
                    <div class="tab">
                        <button class="tablinks" onclick="opentabs(event, 'essocio')"  id="defaultOpen">Ya eres socio</button>
                        <button class="tablinks" onclick="opentabs(event, 'nucliente')">Soy nuevo</button>
                    </div>
                    <div id="essocio" class="tabcontent">

                        <form id="FrmClienteNuevo" action="FrmUsuarioNuevoGuardar" onsubmit="return seg_ingresanuevo(this);" autocomplete="off">
                            <input id="es" name="es" value="" type="hidden"  />
                            <input id="id" name="id" value="" type="hidden"  />
                            <input id="idi" name="idi" value="" type="hidden"  />
                            <lable>RUC - CÉDULA - PASAPORTE</lable>
                            <div class="row"><div class="col-md-8"><input type="text" id="ruc" name="ruc" placeholder="INGRESE SU RUC - CÉDULA - PASAPORTE"></div><input type="button" class="form-btn" onclick="buscarcliente();"   value="Buscar"/></div>
                            <div id="existe" style="display: none;">
                                <lable>CLIENTE</lable>
                                <input type="text" id="cliente" name="cliente" readonly >
                                <lable>CORREO ELECTRÓNICO</lable>
                                <input type="email"  id="email" name="email" readonly >
                                <lable>INGRESE UNA CONTRASEÑA</lable>
                                <input type="password"  id="clave" name="clave" placeholder="INGRESE UNA CONTRASEÑA">
                                <lable>REPETIR LA CONTRASEÑA</lable>
                                <input type="password"  id="clave1" name="clave1" placeholder="REPETIR LA CONTRASEÑA">
                                <p class="terms">
                                    <input type="checkbox" id="condiciones" name="condiciones">
                                    Acepto todas las declaraciones en 
                                    <a href="http://saitelapp.ec/anexos/terminos/terminos%20y%20condiciones.pdf" target="_blank" class="lined-link">términos de servicio</a>
                                </p>
                                <input type="button"  id='btn_registro' class="form-btn" value="Registrarse"/>
                                <input type="button"  class="form-btn" value="Limpiar" onclick="limpiar();"/>
                                <!--<a href="#" class="lined-link to-signin-link">I'm already member</a>-->
                            </div>
                        </form>
                    </div>
                    <div id="nucliente" class="tabcontent">
                        <form id="FrmClientesNuevo" action="FrmClienteNuevoGuardar" onsubmit="return seg_ingresanuevocliente(this);" autocomplete="off">
                            <input id="esnu" name="esnu" value="" type="hidden"  />
                            <input id="idnu" name="idnu" value="" type="hidden"  />
                            <lable>Tipo de documento</lable>
                            <select id="tipo_documentonu" name="tipo_documentonu">
                                <option value="04">RUC</option>
                                <option selected value="05">Cédula</option>
                                <option value="06">Pasaporte</option>
                            </select>
                            <lable>Cédula o RUC</lable>
                            <input id="runu" name="runu" type="text"  maxlength="13" value="" onkeypress="_DNI(event)" onblur="buscarcliente('n', 'runu');" placeholder="INGRESE SU RUC - CÉDULA - PASAPORTE"  required />
                            <input type="checkbox" id="vf" checked="checked" disabled="" style="display: none;" />
                            <lable>Apellidos y Nombres o Institución </lable>
                            <input id="rsnu" name="rsnu" type="text"  maxlength="100" value="" onkeypress="_sinEspeciales(event)" onblur="this.value = this.value._trim();" placeholder="INGRESE SUS APELLIDOS Y NOMBRES O INSTITUCIÓN " required />
                            <lable>Teléfono</lable>
                            <input id="tenu" name="tenu" type="text"  onkeypress="_evaluar(event, '0123456789-/ ');" minlength="6" maxlength="9" onblur="this.value = this.value._trim();" placeholder="INGRESE SU TELEFONO" required />
                            <lable>Teléfono celular</lable>
                            <input id="te_clnu" name="te_clnu" type="text"  maxlength="10" onkeypress="_numero(event);" minlength="10" maxlength="10" placeholder="INGRESE SU TELEFONO CELULAR " required/>
                            <lable>Correo electrónico</lable>
                            <input type="email"  id="emailnu" name="emailnu" placeholder="INGRESE SU CORREO ELECTRÓNICO"  required />
                            <lable>Dirección</lable>
                            <textarea maxlength="300" id="direccionnu" name="direccionnu" placeholder="INGRESE SU DIRECCION DE DOMICILIO " required></textarea>
                            <p class="terms">
                                <input type="checkbox" id="condicionesnu" name="condicionesnu">
                                Acepto todas las declaraciones en 
                                <a href="http://saitelapp.ec/anexos/terminos/terminos%20y%20condiciones.pdf" target="_blank" class="lined-link">términos de servicio</a>
                            </p>
                            <input type="submit"  id='btn_registronu' class="form-btn" value="Registrarse"/>
                            <input type="button"  class="form-btn" value="Limpiar" onclick="limpiar('n');"/>
                        </form>
                    </div>
                </div>

                <div class="form form-signin" id="div_login">
                    <form id="FrmClienteAcceso" action="FrmInicioSesion"  method="post" enctype="application/x-www-form-urlencoded">
                        <lable>USUARIO</lable>
                        <input type="text" id="email" name="email" required placeholder="INGRESE SU USUARIO">
                        <lable>CONTRASEÑA</lable>
                        <input type="password" id="clave" name="clave"  required placeholder="INGRESE SU CONTRASEÑA">
                        <input type="submit" class="form-btn" value="INICIAR SESIÓN"/>
                        <a href="javascript:void(0);" class="lined-link" onclick="ocultarlogin();" >¿Has olvidado tu contraseña?</a>
                    </form>
                </div>
                <div id="div_restablecer" class="form" style="display: none;">
                    <form id="FrmClienteRestablecer" action="FrmRestablecerClave"  onsubmit="return seg_restablecerclave(this);" autocomplete="off">
                        <lable>USUARIO</lable>
                        <input type="text" id="usuarior" name="usuarior" autofocus required placeholder="INGRESE SU USUARIO ACTUAL">
                        <lable>CORREO ELECTRÓNICO</lable>
                        <input type="email" id="emailr" name="emailr" required placeholder="INGRESE SU CORREO ELECTRÓNICO REGISTRADO">
                        <input type="submit" class="form-btn" value="RESTABLECER CONTRASEÑA"/>
                    </form>
                </div>
            </div>
            <div class="clear-fix"></div>
        </div>
        <script src="js/login.js" type="text/javascript"></script>
        <script src="lib/alerfity/alertify.min.js" type="text/javascript"></script>
    </body>
</html>
<%
    String msg = request.getParameter("msg") != null ? request.getParameter("msg") : "";
    if (msg.trim().compareTo("") != 0 && msg.trim().compareTo("null") != 0) {
        out.print("<script language=\"JavaScript\" type=\"text/javascript\">");
        out.print("alertify.error('" + msg + "');");
        out.print("</script>");
    }
%>