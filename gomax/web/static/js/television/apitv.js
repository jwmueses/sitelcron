const ipPago = '192.168.217.16:8080';

function funcCerrarSesion(){
    document.getElementById('idCerrarSesion').addEventListener('click', function() {
        expirarCookie('jwt'); 
        expirarCookie('email');
        funcRedirigirPagina('../html/television.html');
    });
}

function funcGetCookie(name) {
    const cookies = document.cookie.split(';');
    for (let i = 0; i < cookies.length; i++) {
      const cookie = cookies[i].trim();
      // Check if this cookie is the one we're looking for
      if (cookie.startsWith(name + '=')) {
        return cookie.substring(name.length + 1); // Return the value of the cookie
      }
    }
    return null; // Return null if the cookie is not found
}

function funcGetFecha(fecha) {
    // Extract year, month, and day from the Date object
    const anio = fecha.getFullYear();
    const mes  = String(fecha.getMonth() + 1).padStart(2, '0'); // Add leading zero if necessary
    const dia = String(fecha.getDate()).padStart(2, '0'); // Add leading zero if necessary
  
    // Return the formatted date string
    return `${anio}-${mes}-${dia}`;
  }

// Function to extract page name from URL
function getPageName(url) {
    const pageName = url.substring(url.lastIndexOf('/') + 1);
    return pageName;
}

// Call the function only when the desired page is opened
window.onload = function() {
    const currentPage = getPageName(window.location.pathname);
    if (currentPage === 'television_planes.html') {
        funcObtenerPlanesTv();
        funcCerrarSesion();
        funcCargarHamburger();
    }
    if (currentPage === 'television_correo_verificacion.html') {
        funcVerificarCorreoTv();
        funcCerrarSesion();
        funcCargarHamburger();
    }
    if (currentPage === 'television_contratar_plan.html') {
        funcReenviarCorreoVerificar();
        funcCargarPlanAContratar();
        funcCargarHamburger();
        funcCerrarSesion();
    }
    if (currentPage === 'television_mi_perfil.html'){
        funcObtenerPlanContratadoTv();
        funcCargarDatosPerfil();
        funcCerrarSesion();
        funcCargarHamburger();
    }
    if(currentPage === 'television.html'){
        funcClaveSeguraEvent();
        funcObtenerPlanesTv();
    }
    if(currentPage === 'television_pago_exitoso.html'){
        funcFacturarTv();
        funcCerrarSesion();
        funcCargarHamburger();
    }
};

function selectPlan(event) {
    // Remove 'selected' class from all cards
    const cards = document.querySelectorAll('.plan');
    cards.forEach(card => {
        card.classList.remove('selected');
    });

    // Remove the ID from all hidden elements
    const hiddenElements = document.querySelectorAll('.plan-details h7[hidden]');
    hiddenElements.forEach(element => {
        element.removeAttribute('id');
    });

    const nombrePlan = document.querySelectorAll('.plan-titulo h3');
    nombrePlan.forEach(element => {
        element.removeAttribute('id');
    });

    // Add 'selected' class to the clicked card
    const selectedPlan = event.currentTarget;
    selectedPlan.classList.add('selected');

    // Retrieve the hidden ID element
    const idPlanEscogido = event.currentTarget.querySelector('.plan-details h7[hidden]');
    idPlanEscogido.id = 'idPlanEscogido';
    localStorage.setItem('idPlanEscogido', idPlanEscogido.textContent);

    const idPlanEscogidoNombre = event.currentTarget.querySelector('.plan-titulo h3');
    idPlanEscogidoNombre.id = 'idPlanEscogidoNombre';
    localStorage.setItem('idPlanEscogidoNombre', idPlanEscogidoNombre.textContent);

    const idCostoPlan = event.currentTarget.querySelector('.plan-details h6[hidden]');
    idCostoPlan.id = 'idCostoPlan';
    sessionStorage.setItem('idCostoPlan', idCostoPlan.textContent);
    
}

function funcObtenerPlanesTv(){

    // Event listeners to handle plan selection
    const cards = document.querySelectorAll('.plan');
    cards.forEach(card => {
        card.addEventListener('click', selectPlan);
    });

    fetch('http://'+ipPago+'/gomax/gomaxtv/planestv')
            .then(response => response.json()) // Parse the JSON response
            .then(plan => {
                const tableBody = document.querySelector('.plan-table tbody');
    
                // Iterate over each item in the data array and create table rows
                plan.forEach(plan => {
                    const card = document.createElement('div');
                    card.className ='plan';
                    card.innerHTML = `
                            <div class="plan-titulo">
                                <h3 class="color-primario">${plan.nombrePlan}</h3><br>
                            </div>
                            <div class="plan-details">
                                <h7 hidden><span class="color-secundario"><strong></span> ${plan.idPlanGomax}</strong></h7><br>
                                <h7><span class="color-secundario"><strong></span> ${plan.descripcion}</strong></h7><br>
                                <h7><span class="color-secundario"><strong></span> ${plan.meses} mes(es)</strong></h3><br>
                                <h7><span class="color-secundario"><strong></span> $ ${plan.costo}</strong> (más impuestos)</h3><br>
                                <h6 hidden><span class="color-secundario"><strong></span> $ ${plan.costo}</strong></h3><br>
                            </div>    
                    `;
                    tableBody.appendChild(card);

                    // Add event listener to newly created card
                    card.addEventListener('click', selectPlan);
                });
            })
            .catch(error => console.error('Error fetching data:', error));

}

function funcObtenerPlanContratadoTv(){

    fetch('http://'+ipPago+'/gomax/gomaxtv/planestv')
            .then(response => response.json())
            .then(plan => {
                const nombrePlan = plan.find(plan => plan.idPlanGomax === sessionStorage.getItem('idPlanGomax'));
                if (nombrePlan) {
                    sessionStorage.setItem('nombrePlan', nombrePlan.nombrePlan);
                } else {
                    sessionStorage.setItem('nombrePlan', 'Sin plan');
                }
            })
            .catch(error => console.error('Error fetching data:', error));
}

function funcContratarPlanTv(){

    funcDeshabilitarBtn('idContratarBtn', true);
    funcMostrarSpinner('idSpinnerContratar');

    const idCostoPlan = sessionStorage.getItem('idCostoPlan').replace('$', '');
    const idClienteSuscripcionGomax = funcGetCookie('idClienteSuscripcionGomax');
    const idPlan = document.getElementById('idPlanEscogido').textContent;
    const idRazonSocialInput = document.getElementById('idRazonSocialInput').value;
    const idRucInput = document.getElementById('idRucInput').value;
    const idDireccionInput = document.getElementById('idDireccionInput').value;
    const idCelularInput = document.getElementById('idCelularInput').value;


    const datosContratarPlanTv = {
        idPlanGomax:parseInt(idPlan),
        correoCuenta: funcGetCookie('email'),
        fechaSuscripcion: funcGetFecha(new Date()),
        ruc: idRucInput,
        razonSocial: idRazonSocialInput,
        direccion: idDireccionInput,
        movilClaro: idCelularInput
    };

    const datosCliente = {
        costoPlan: parseFloat(idCostoPlan),
        idClienteSuscripcionGomax: parseInt(idClienteSuscripcionGomax)
    };

    const jsonDatosContratarPlanTv = JSON.stringify(datosContratarPlanTv);
    // const datosClientePlanTv = JSON.stringify(datosCliente);

    console.log(datosContratarPlanTv);
    console.log(datosCliente);
    console.log("GENERAR A");
    const apiUrl = 'http://'+ipPago+'/gomax/gomaxtv/suscripciones/suscribirseplan/';
    console.log("GENERAR B");

    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'saitel-tv-jwt': funcGetCookie('jwtSuscripcion')
        },
        body: jsonDatosContratarPlanTv
    };
    console.log("GENERAR C");

    console.log(funcGetCookie('jwtSuscripcion'));

    fetch(apiUrl, requestOptions)
        .then(response => {
            if (!response.ok) {
                console.log(response);
                throw new Error('Error al contratar plan');
            }
            // console.log(response);
            return response;
        })
        .then(()=>{
            console.log("GENERAR PAGO");
            funcGenerarLinkPago(datosContratarPlanTv, datosCliente);
        })
        .catch(error => {
            console.error('Error', error);
        });
}

function funcIniciarSesionTv(){
    // const iniciarSesionTvForm = document.getElementById('iniciar-sesion-tv-form');

    // iniciarSesionTvForm.addEventListener('submit', function (event) {
    //     event.preventDefault();

    const emailInput = document.getElementById('emailInput').value;
    const passwordInput = document.getElementById('passwordInput').value;

    const datosIniciarSesionTv = {
        correoCuenta: emailInput,
        claveCuenta: passwordInput
    };

    // const formData = new FormData(registroTvForm);
    const jsonDatosIniciarSesionTv = JSON.stringify(datosIniciarSesionTv);

    const apiUrl = 'http://'+ipPago+'/gomax/gomaxtv/seguridad/';

    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type':'application/json'
        },
        body: jsonDatosIniciarSesionTv
    };

    fetch(apiUrl, requestOptions)
        .then(response => {
            console.log(response);
            if (!response.ok) {
                throw new Error("Error al iniciar sesion");
            }
            return response.json();
        })
        .then(data => {
            console.log(data);
            document.cookie = `jwt=${data.jwt}; path=/; SameSite=None;`;// Secure`;
            document.cookie = `email=${data.correoCuenta}; path=/; SameSite=None;`;// Secure`;
            sessionStorage.setItem('fechaSuscripcion', data.fechaSuscripcion);
            sessionStorage.setItem('fechaTermino', data.fechaTermino);
            sessionStorage.setItem('debitoAutomatico', data.debitoAutomatico);
            sessionStorage.setItem('correoConfirmado', data.correoConfirmado);
            sessionStorage.setItem('ruc', data.ruc);
            sessionStorage.setItem('razonSocial', data.razonSocial);
            sessionStorage.setItem('idPlanGomax', data.idPlanGomax);
            sessionStorage.setItem('idClienteSuscripcionGomax', data.idClienteSuscripcionGomax);
            
            if(data.idPlanGomax !== 0){
                window.location.href = '../html/television_mi_perfil.html';
            }
            else{
                window.location.href = '../html/television_planes.html';
            }

            console.log(data.jwt);
            console.log(data.correoCuenta);
        })
        .catch(error => {
            const errorCard = document.getElementById('errorCard');
            errorCard.innerHTML = '<div class="error-message"> <h7 class="color-error-txt"><strong> Email o contraseña incorrectos.</h7></div>';
            errorCard.style.display = 'flex'; // Show the error card
            // Hide the error card after 2 seconds (2000 milliseconds)
            setTimeout(() => {
                errorCard.style.display = 'none'; // Hide the error card
            }, 4000);        });
}

function funcReenviarCorreoVerificar(){
    const correoConfirmado = sessionStorage.getItem('correoConfirmado');

    if(correoConfirmado === 'false'){
        const errorCard = document.getElementById('errorCard');
        errorCard.innerHTML = `
            <div class="error-message">
                <h6> Primero tienes que verificar tu correo.</h6>
                <div class="p-05"></div>
        
                <button class="btn1 borde-circular" type="button" onclick="funcRedirigirPagina('../html/television_planes.html')">
                    <span><i class="fa fa-arrow-left"></i></span>
                    Cancelar
                </button> 
                <button id="idContratarBtn" class="btn1 borde-circular" type="button">
                    <span id="idSpinnerContratar" style="display: none;">
                        <i class="fa fa-spinner fa-spin"></i>
                    </span>
                    Reenviar correo
                <span><i class="fa fa-arrow-right"></i></span>
                </button> 
            </div> 
        `;
        errorCard.style.display = 'flex';
    }
}


function funcRegistroTv(){

    // const registroTvForm = document.getElementById('registro-tv-form');
    // registroTvForm.addEventListener('submit', function (event) {
        // event.preventDefault();

        funcDeshabilitarBtn('idRegistrarseBtn', true);
        funcMostrarSpinner('idSpinnerRegistrarse');

        const emailInput = document.getElementById('emailInput').value;
        const passwordInput = document.getElementById('passwordInput').value;
    
        const apiUrl = 'http://'+ipPago+'/gomax/gomaxtv/registro/';
    
        const datosRegistroTv = {
            correoCuenta: emailInput,
            claveCuenta: passwordInput
        };
    
        // const formData = new FormData(registroTvForm);
        const jsonDatosRegistroTv = JSON.stringify(datosRegistroTv);

        if( funcClaveSegura(datosRegistroTv.claveCuenta) !== true){
            const errorCard = document.getElementById('errorCard');
            errorCard.innerHTML = '<div class="error-message"> <h7 class="color-error-txt"><strong> Contraseña no segura.</h7></div>';
            errorCard.style.display = 'flex';
            setTimeout(() => {
                errorCard.style.display = 'none'; 
            }, 4000);
            funcOcultarSpinner('idSpinnerRegistrarse');
            funcDeshabilitarBtn('idRegistrarseBtn', false);
            return;
        }

        const requestOptions = {
            method: 'POST',
            headers: {
                'Content-Type':'application/json'
            },
            body: jsonDatosRegistroTv
        };
    
        fetch(apiUrl, requestOptions)
            .then(response => {
                console.log(response);
                if (!response.ok) {
                    const errorCard = document.getElementById('errorCard');
                    errorCard.innerHTML = '<div class="error-message"> <h7 class="color-error-txt"><strong> El correo '+ datosRegistroTv.correoCuenta +' ya se encuentra registrado.</h7></div>';
                    errorCard.style.display = 'flex';
                    setTimeout(() => {
                        errorCard.style.display = 'none'; 
                    }, 4000);
                    return response;
                }
                // funcIngresarInicioTvTemp(jsonDatosRegistroTv);
                funcIngresarInicioTv(jsonDatosRegistroTv);
                console.log(response);
                return response;

            })
            .then(() => {
                // funcDeshabilitarBtn('idContratarBtn', false);
                funcOcultarSpinner('idSpinnerRegistrarse');
            })
            // .catch(() => {
            // })
            .finally(() => {
                funcOcultarSpinner('idSpinnerRegistrarse');
                funcDeshabilitarBtn('idRegistrarseBtn', false);
            });
}

function funcIngresarInicioTv(jsonDatosUsuario){

    const apiUrl = 'http://'+ipPago+'/gomax/gomaxtv/seguridad/';

    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type':'application/json'
        },
        body: jsonDatosUsuario
    };

    fetch(apiUrl, requestOptions)
        .then(response => {
            console.log(response);
            if (!response.ok) {
                throw new Error("Error inicio de usuario");
            }
            return response.json();
        })
        .then(data => {
            document.cookie = `jwt=${data.jwt}; path=/; SameSite=None;`;// Secure`;
            document.cookie = `email=${data.correoCuenta}; path=/; SameSite=None;`;// Secure`;
            document.cookie = `idClienteSuscripcionGomax=${data.idClienteSuscripcionGomax}; path=/; SameSite=None;`;// Secure`;
            window.location.href = '../html/television_planes.html';
            sessionStorage.setItem('fechaSuscripcion', data.fechaSuscripcion);
            sessionStorage.setItem('fechaTermino', data.fechaTermino);
            sessionStorage.setItem('debitoAutomatico', data.debitoAutomatico);
            sessionStorage.setItem('correoConfirmado', data.correoConfirmado);
            sessionStorage.setItem('ruc', data.ruc);
            sessionStorage.setItem('razonSocial', data.razonSocial);
            sessionStorage.setItem('idPlanGomax', data.idPlanGomax);
            sessionStorage.setItem('idClienteSuscripcionGomax', data.idClienteSuscripcionGomax);
            // console.log(data.jwt);
            // console.log(data.correoCuenta);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function funcVerificarCorreoTv() {

    const values = funcExtraerEmYToken();

    const datosVerificarCorreo= {
        correoCuenta: values.em,
        jwt: values.jwt
    };

    const jsonDatosVerificarCorreo = JSON.stringify(datosVerificarCorreo);

    const apiUrl = 'http://'+ipPago+'/gomax/gomaxtv/registro';

    const requestOptions = {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'saitel-tv-jwt': funcGetCookie('jwt')
        },
        body: jsonDatosVerificarCorreo
    };

    fetch(apiUrl, requestOptions)
        .then(response => {
            console.log(response);
            if (!response.ok) {
                throw new Error("Error al verificar correo");
            }
            // return response.json();
        })
        .then(() => {
            // document.cookie = `jwt=${data.jwt}; path=/; SameSite=None; Secure`;
//            funcGetTokenGomax(values.em);
            const msj = document.getElementById('idMsjVerificarCorreo');
            msj.innerHTML = `
                <h4 class="color-secundario">Correo verificado <span><i class="fa fa-check-square"></i></span></h4>
                <div class="p-05"></div>
                <p> Ir a la paginal principal</p>
                <form action="television_iniciar_sesion.html">
                    <button class="btn1" type="submit">
                        <span>Inicio</span> 
                        <span><i class="fa fa-home"></i></span>
                    </button>
                </form>
            `;
//            sessionStorage.setItem('correoConfirmado', true);
//            document.cookie = `jwt=${values.jwt}; path=/; SameSite=None;`; //<!-- Secure`;>
//            document.cookie = `email=${values.em}; path=/; SameSite=None;`;// Secure`;
//            document.cookie = `idClienteSuscripcionGomax=${values.idClienteSusGo}; path=/; SameSite=None;`; // Secure`;
        })
        .catch(error => {
            const msj = document.getElementById("idMsjVerificarCorreo");
            msj.innerHTML = `
                <h4 class="color-secundario">Enlace expirado <span><i class="fa fa-clock"></i></span></h4>
                <div class="p-05"></div>
                <button class="btn1" onlick="">
                    <span>Reenviar correo</span> 
                    <span><i class="fa fa-arrow-right"></i></span>
                </button>
            `;

            console.error('Error:', error);
        });
}

//function funcGetTokenGomax(correo){
//
//    const apiUrl = 'http://'+ipPago+'/gomax/planestv/token/'+correo;
//
//    fetch(apiUrl)
//        .then(response => {
//            console.log(response);
//            if (!response.ok) {
//                throw new Error("Error al obtener token");
//            }
//            return response.text();
//        })
//        .then(data => {
//            if (data.trim() !== '') {
//                document.cookie = `tokenSuscripcion=${data}; path=/; SameSite=None;`;// Secure`;
//            } else {
//                throw new Error("Token vacio");
//            }
//        })
//        .catch(error => {
//            console.error('Error:', error);
//        });
//}

function funcExtraerEmYToken() {
    var url = window.location.href;
    console.log(url);
     
    var parts = url.split('?');
    
    var emValue = '';
    var jwtValue = '';
    var idClienteSusGo;
    
    if (parts.length > 1) {
        var queryString = parts[1];
        
        var params = queryString.split('&');
        
        for (var i = 0; i < params.length; i++) {
            var keyValue = params[i].split('=');
            
            if (keyValue[0] === 'em') {
                emValue = keyValue[1];
            }
            
            if (keyValue[0] === 'jwt') {
                jwtValue = keyValue[1];
            }
            
            if (keyValue[0] === 'idClienteSusGo') {
                idClienteSusGo = keyValue[1];
            }
        }
         // Return the values
         return {
           em: emValue,
           jwt: jwtValue,
           idClienteSusGo: idClienteSusGo
         };
    }
 }

 function funcGenerarLinkPago(datosContratarPlanTv, datosCliente){
    // console.log((datosCliente.costoPlan * 1.15).toFixed(2));
    console.log(datosContratarPlanTv);
    console.log(datosCliente);

    const datosLinkPago= {
        integration: true,
        third: {
            document: datosContratarPlanTv.ruc ,
            document_type: "05",
            name: datosContratarPlanTv.razonSocial,
            email: datosContratarPlanTv.correoCuenta,
            phones: datosContratarPlanTv.movilClaro,
            address: datosContratarPlanTv.direccion,
            type: "Individual"
        },
        generate_invoice: 0,
        description: "PAGO DEL SERVICIO DE TV SAITEL. TV"+ datosCliente.idClienteSuscripcionGomax,
        amount: 1.06, // datosCliente.costoPlan, //1.06,
        amount_with_tax: 0.5, // (datosCliente.costoPlan * 1.15).toFixed(2), //0.5,
        amount_without_tax: 0.5, // 0.5,
        tax_value:0.06, //1.15, //0.06,
        settings: [],
        notify_url: 'http://138.185.137.117:8080/gomax/gomaxtv/pagomedio/actualizarpagomedio/',
        custom_value: datosCliente.idClienteSuscripcionGomax,
        has_cash: 0,
        has_cards: 1
    };


    const jsonDatosLinkPago = JSON.stringify(datosLinkPago);
    console.log(jsonDatosLinkPago);
    const apiUrl = 'http://'+ipPago+'/gomax/gomaxtv/pagomedio/generarpagomedio/';

    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'saitel-tv-jwt': funcGetCookie('jwt')
        },
        body: jsonDatosLinkPago
    };

    fetch(apiUrl, requestOptions)
        .then(response => {
            console.log(response);
            if (!response.ok) {
                throw new Error("Ha ocurrido un error generando en link de pago");
            }
            return response.text();
        })
        .then(data => {
            // console.log(data)
            // Check if the response indicates success
            if (data.trim() !== '') {
                // Redirect to the URL provided in the response
                funcRedirigirPagina(data);
            } else {
                throw new Error("Ha ocurrido un error generando en link de pago");
            }
        })
        .then(()=>{
            // funcDeshabilitarBtn('idContratarBtn', false);
            funcOcultarSpinner('idSpinnerContratar');
        })
        .catch(error => {
            console.error(error);
        });
 }

function funcCargarPlanAContratar(){
    const urlParams = new URLSearchParams(window.location.search);
    const idPlanEscogidoNombre = urlParams.get('plan');
    const idPlanEscogido= urlParams.get('id');

    document.getElementById("idPlanEscogidoNombre").innerHTML = 'Plan a contratar : ' + idPlanEscogidoNombre.toUpperCase();
    document.getElementById("idPlanEscogido").innerHTML = idPlanEscogido;
}

function funcCargarDatosPerfil(){
    document.getElementById("idNombrePlanPerfil").innerHTML = '<h7> <span class="fa fa-user color-secundario">Plan: </span> ' + sessionStorage.getItem('nombrePlan') + '</h7>';
    document.getElementById("idNombrePerfil").innerHTML = '<h7> <span class="fa fa-user color-secundario">Razon social: </span> ' + sessionStorage.getItem('razonSocial') + '</h7>';
    document.getElementById("idRucPerfil").innerHTML = '<h7> <span class="fa fa-barcode color-secundario">Ruc/CI: </span> ' + sessionStorage.getItem('ruc')+ '</h7>';
    document.getElementById("idCorreoPerfil").innerHTML = '<h7> <span class="fa fa-envelope color-secundario"> Email: </span> ' + funcGetCookie('email')+ '</h7>';
    document.getElementById("idFechaSusPerfil").innerHTML = '<h7> <span class="fa fa-calendar color-secundario"> Fecha suscripcion: </span> ' +sessionStorage.getItem('fechaSuscripcion')+ '</h7>';
    document.getElementById("idFechaTermPerfil").innerHTML = '<h7> <span class="fa fa-calendar color-secundario"> Fecha termino: </span> ' + sessionStorage.getItem('fechaTermino')+ '</h7>';
    const debitoAut = sessionStorage.getItem('debitoAutomatico') === 'false'? 
    'No <button class="card-button btn1 borde-circular2" type="button" onclick="funcIngresarDatosPerfil()"> Activar </button> </h7> ' : 
    'Si <button class="card-button btn1 borde-circular2" type="button" onclick="funcIngresarDatosPerfil()"> Desactivar </button> </h7> ';
    document.getElementById("idDebitoAuPerfil").innerHTML = '<h7> <span class="fa fa-credit-card color-secundario"> Debito automatico: </span> '+ debitoAut + '</h7>';
}

function funcIngresarDatosPerfil(){
    document.getElementById("idNombrePerfil").innerHTML = '<h7> <span class="fa fa-user color-secundario">Razon social: </span> <input id = "nombreInput"> </h7>';
    document.getElementById("idRucPerfil").innerHTML = '<h7> <span class="fa fa-barcode color-secundario">Ruc/CI: </span> <input id = "rucInput"> </h7>';
    document.getElementById("idCorreoPerfil").innerHTML = '<h7> <span class="fa fa-envelope color-secundario"> Email: </span> <input id = "correoInput"> </h7>';
    document.getElementById("idEditarDatosBtn").textContent = 'Cancelar';
    document.getElementById("idEditarDatosBtn").removeEventListener("click", funcIngresarDatosPerfil);
    document.getElementById("idEditarDatosBtn").addEventListener("click", funcIngresarDatosPerfilCancelar);
    funcEscucharIngresoDatosPerfil();
}

function funcIngresarDatosPerfilCancelar(){
    
    document.getElementById("idNombrePerfil").innerHTML = '<h7> <span class="fa fa-user color-secundario">Razon social: </span> ' + sessionStorage.getItem('razonSocial') + '</h7>';
    document.getElementById("idRucPerfil").innerHTML = '<h7> <span class="fa fa-barcode color-secundario">Ruc/CI: </span> ' + sessionStorage.getItem('ruc')+ '</h7>';
    document.getElementById("idCorreoPerfil").innerHTML = '<h7> <span class="fa fa-envelope color-secundario"> Email: </span> ' + funcGetCookie('email')+ '</h7>';
    document.getElementById("idEditarDatosBtn").textContent = 'Editar';
    document.getElementById("idEditarDatosBtn").removeEventListener("click", funcIngresarDatosPerfilCancelar);
    document.getElementById("idEditarDatosBtn").addEventListener("click", funcIngresarDatosPerfil);

}

function funcEscucharIngresoDatosPerfil() {
    // Get the input elements
    var nombreInput = document.getElementById("nombreInput");
    var rucInput = document.getElementById("rucInput");
    var correoInput = document.getElementById("correoInput");

    // Get the button element
    var editarDatosBtn = document.getElementById("idEditarDatosBtn");

    // Function to check if all inputs are filled
    function checkInputsFilled() {
        return nombreInput.value.trim() !== '' && rucInput.value.trim() !== '' && correoInput.value.trim() !== '';
    }

    // Function to handle button state based on inputs
    function handleButtonState() {
        if (checkInputsFilled()) {
            editarDatosBtn.textContent = "Guardar"; // Enable the button
        } else {
            editarDatosBtn.textContent = "Editar"; // Disable the button
        }
    }

    // Add event listeners to input elements
    nombreInput.addEventListener('input', handleButtonState);
    rucInput.addEventListener('input', handleButtonState);
    correoInput.addEventListener('input', handleButtonState);
}


function funcFacturarTv() {
    const isFunctionExecuted = sessionStorage.getItem('funcFacturarTvExecuted');
    const urlParams = new URLSearchParams(window.location.search);
    sessionStorage.setItem('jwt', urlParams.get('jwt'));

    if (isFunctionExecuted === null) {
        
        const idSus = urlParams.get('idSus');

        const apiUrl = 'http://'+ipPago+'/gomax/gomaxtv/suscripciones/facturarplan';
        const rawText = idSus;

        const requestOptions = {
            method: 'PUT', 
            body: rawText,
            headers: {
              'Content-Type': 'text/plain',
              'saitel-tv-jwt': sessionStorage.getItem('jwt')
            }
        };

        fetch(apiUrl, requestOptions)
        .then(response => {
            if (response.ok) {
                return response.text(); 
            }
            throw new Error('Error al facturar.');
        })
        .then(data => {
            console.log(data);
            // Set flag indicating the function has been executed
            sessionStorage.setItem('funcFacturarTvExecuted', 'true');
            // Show the success message and hide the spinner
            // document.getElementById('idPagoexitoso').removeAttribute('hidden');
            // document.getElementById('idPagoExitosoSpinner').style.display = 'none';
        })
        .catch(error => {
            sessionStorage.setItem('funcFacturarTvExecuted', 'false');
            console.error(error);
        });
    } else {
        
        if (!isFunctionExecuted) {
            
            const urlParams = new URLSearchParams(window.location.search);
            const idSus = urlParams.get('idSus');

            const apiUrl = 'http://'+ipPago+'/gomax/gomaxtv/suscripciones/facturarplan';
            const rawText = idSus;

            const requestOptions = {
                method: 'POST', 
                body: rawText,
                headers: {
                  'Content-Type': 'text/plain',
                  'saitel-tv-jwt': funcGetCookie('jwt')
                }
            };

            fetch(apiUrl, requestOptions)
            .then(response => {
                if (response.ok) {
                    return response.text(); 
                }
                throw new Error('Network response was not ok.');
            })
            .then(data => {
                console.log(data);
                // Set flag indicating the function has been executed
                sessionStorage.setItem('funcFacturarTvExecuted', 'true');
                // Show the success message and hide the spinner
                // document.getElementById('idPagoexitoso').removeAttribute('hidden');
                // document.getElementById('idPagoExitosoSpinner').style.display = 'none';
            })
            .catch(error => {
                console.error('Error:', error);
            });
        }
    }
}