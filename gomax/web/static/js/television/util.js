function funcMostrarClave() {
    var x = document.getElementById("passwordInput");
    var icon = document.getElementById("eyeIcon");

    if (x.type === "password") {
      x.type = "text";
      icon.classList.remove("fa-eye");
      icon.classList.add("fa-eye-slash");
    } else {
      x.type = "password";
      icon.classList.remove("fa-eye-slash");
      icon.classList.add("fa-eye");
    }
  }

function funcClaveSegura(clave){
  const caracterRegex = /[.+\-*/!?#$&=@|"]/;
  const longitudMinima = 8;
  const mayusculaRegex = /[A-Z]/;
  const minusculaRegex = /[a-z]/;
  const numeroRegex = /\d/;
  let claveSegura = false;

  const strengthText = document.getElementById('idClaveSegura');
  let msn = '<p> La contraseña debe tener: </p>';

  if (clave.length >= longitudMinima) {
      msn += '<p style="color:green"> 8 caracteres <span class = "fa fa-check-circle"></span> </p>';
  } else {
      msn += '<p style="color:red">8 caracteres </p>';
  }
  if (mayusculaRegex.test(clave)) {
      msn +='<p style="color:green">Al menos una mayuscula <span class = "fa fa-check-circle"></span></p>';
  } else {
      msn += '<p style="color:red">Al menos una mayuscula</p>';
  }
  if (minusculaRegex.test(clave)) {
      msn += '<p style="color:green">Al menos una minuscula <span class = "fa fa-check-circle"></span></p>';
  } else {
      msn += '<p style="color:red">Al menos una minuscula</p>';
  }
  if (numeroRegex.test(clave)) {
      msn += '<p style="color:green">Al menos un numero <span class = "fa fa-check-circle"></span></p>';
  } else {
      msn += '<p style="color:red">Al menos un numero</span></p>';
  }
  if (caracterRegex.test(clave)) {
      msn += '<p style="color:green">Al menos un caracter especial .+\-*/!?#$&=@|" <span class = "fa fa-check-circle"></span></p>';
  } else {
      msn += '<p style="color:red">Al menos un caracter especial .+\-*/!?#$&=@|" </p>';
  }

  if(clave.length >= longitudMinima && numeroRegex.test(clave)&& mayusculaRegex.test(clave) && caracterRegex.test(clave) && minusculaRegex.test(clave)){
    msn = '<p style="color:green"> Contraseña segura <span class = "fa fa-check-circle"></span></p>';
    claveSegura = true;
  }
  if(clave === ''){
    msn = '<p></p>'
  }

  strengthText.innerHTML = msn;
  return claveSegura;
  // Update text content
  // if (requirements.every(req => req.includes('green'))) {
  //     strengthText.style.color = 'green';
  //     strengthText.innerHTML = 'Contraseña segura';
  // } else {
  //     strengthText.style.color = 'red';
  //     strengthText.textContent = 'La clave debe tener: ';
  //     const ul = document.createElement('ul');
  //     ul.style.listStyleType = 'none';
  //     requirements.forEach(req => {
  //         const li = document.createElement('li');
  //         li.innerHTML = req;
  //         ul.appendChild(li);
  //     });
  //     strengthText.appendChild(ul);
  // }

  // return clave.length >= longitudMinima && mayusculaRegex.test(clave) && caracterRegex.test(clave) && minusculaRegex.test(clave);
}

function funcClaveSeguraEvent(){
    const passwordInput = document.getElementById('passwordInput');
    passwordInput.addEventListener('input', function(event) {
      const password = event.target.value;
      funcClaveSegura(password);
    });

}

function funcRedirigirPaginaPago(pagina) {
  var idPlan = localStorage.getItem('idPlanEscogido');
  var idPlanNombre = localStorage.getItem('idPlanEscogidoNombre');

  var url = '?id=' + encodeURIComponent(idPlan) +'&plan=' + encodeURIComponent(idPlanNombre);

  window.location.href = pagina+ url;
}

function funcRedirigirPagina(pagina) {
  window.location.href = pagina;
}

function funcDeshabilitarBtn(id, habilitado) {
  document.getElementById(id).disabled = habilitado;
}

function funcMostrarSpinner(id) {
  document.getElementById(id).style.display = 'inline-block';
}

function funcOcultarSpinner(id) {
  document.getElementById(id).style.display = 'none';
}

function expirarCookie(name) {
  document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
}

function funcCargarHamburger(){
  const hamburger = document.querySelector('.hamburger');
  const navItems = document.querySelector('.nav-items');

  // Agrega un evento clic al ícono de hamburguesa
  hamburger.addEventListener('click', () => {
    // Alterna la clase 'active' en el elemento de menú
    navItems.classList.toggle('active');
  });
}




