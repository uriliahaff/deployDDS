function habilitarMunicipioPropio() {
    console.log("Entró en habilitarMunicipioPropio");
    var provinciaDropdown = document.getElementById('provincia');
    var municipioDropdown = document.getElementById('municipio');
    var localidadDropdown = document.getElementById('localidad');

    var provinciaSeleccionada = provinciaDropdown.value;

    console.log("Provincia seleccionada:", provinciaSeleccionada);

    if (provinciaSeleccionada !== '') {
        // Resetear los campos de municipio y localidad al cambiar la provincia
        municipioDropdown.value = '';
        localidadDropdown.value = '';

        var municipios = document.querySelectorAll('#municipio option');
        console.log("Total de municipios encontrados:", municipios.length);

        municipios.forEach(function (option) {
            option.style.display = 'none';
            if (option.getAttribute('data-provincia-id') === provinciaSeleccionada) {
                console.log("Mostrando municipio:", option.value);
                option.style.display = '';
            }
        });

        // Deshabilitar y habilitar el dropdown de municipios y localidades
        municipioDropdown.disabled = false;
        localidadDropdown.disabled = true;
    }
}

function habilitarLocalidadPropio() {
    console.log("Entró en habilitarLocalidadPropio");
    var municipioDropdown = document.getElementById("municipio");
    var localidadDropdown = document.getElementById("localidad");

    var municipioSeleccionado = municipioDropdown.value;

    console.log("Municipio seleccionado:", municipioSeleccionado);

    if (municipioSeleccionado !== '') {
        // Resetear el campo de localidad al cambiar el municipio
        localidadDropdown.value = '';

        var localidades = document.querySelectorAll('#localidad option');
        console.log("Total de localidades encontradas:", localidades.length);

        localidades.forEach(function (option) {
            option.style.display = 'none';
            if (option.getAttribute('data-municipio-id') === municipioSeleccionado) {
                console.log("Mostrando localidad:", option.value);
                option.style.display = '';
            }
        });

        // Deshabilitar y habilitar el dropdown de localidades
        localidadDropdown.disabled = false;
    } else {
        // Si no se ha seleccionado un municipio, deshabilitar y reiniciar el dropdown de localidades
        localidadDropdown.innerHTML = '<option value="">Primero seleccione un Municipio</option>';
        localidadDropdown.disabled = true;
    }
}

function habilitarMunicipio(id) {
    console.log("Entró en habilitarMunicipio");
    var provinciaDropdown = document.getElementById("provincia" + id);
    var municipioDropdown = document.getElementById("municipio" + id);
    var localidadDropdown = document.getElementById("localidad" + id);

    var provinciaSeleccionada = provinciaDropdown.value;

    console.log("Provincia seleccionada:", provinciaSeleccionada);

    if (provinciaSeleccionada !== '') {
        // Resetear los campos de municipio y localidad al cambiar la provincia
        municipioDropdown.value = '';
        localidadDropdown.value = '';

        var municipios = document.querySelectorAll('#municipio' + id + ' option');
        console.log("Total de municipios encontrados:", municipios.length);

        municipios.forEach(function (option) {
            option.style.display = 'none';
            if (option.getAttribute('data-provincia-id') === provinciaSeleccionada) {
                console.log("Mostrando municipio:", option.value);
                option.style.display = '';
            }
        });

        // Deshabilitar y habilitar el dropdown de municipios y localidades
        municipioDropdown.disabled = false;
        localidadDropdown.disabled = true;
    }
}

function habilitarLocalidad(id) {
    console.log("Entró en habilitarLocalidad");
    var municipioDropdown = document.getElementById("municipio" + id);
    var localidadDropdown = document.getElementById("localidad" + id);

    var municipioSeleccionado = municipioDropdown.value;

    console.log("Municipio seleccionado:", municipioSeleccionado);

    if (municipioSeleccionado !== '') {
        // Resetear el campo de localidad al cambiar el municipio
        localidadDropdown.value = '';

        var localidades = document.querySelectorAll('#localidad' + id + ' option');
        console.log("Total de localidades encontradas:", localidades.length);

        localidades.forEach(function (option) {
            option.style.display = 'none';
            if (option.getAttribute('data-municipio-id') === municipioSeleccionado) {
                console.log("Mostrando localidad:", option.value);
                option.style.display = '';
            }
        });

        // Deshabilitar y habilitar el dropdown de localidades
        localidadDropdown.disabled = false;
    } else {
        // Si no se ha seleccionado un municipio, deshabilitar y reiniciar el dropdown de localidades
        localidadDropdown.innerHTML = '<option value="">Primero seleccione un Municipio</option>';
        localidadDropdown.disabled = true;
    }
}