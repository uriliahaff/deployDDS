function habilitarMunicipio() {
    console.log("Entró en habilitarMunicipio");
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
            if (option.getAttribute('data-provincia-id') === provinciaSeleccionada) {
                option.style.display = '';
            } else {
                option.style.display = 'none';
            }
        });

        // Deshabilitar y habilitar el dropdown de municipios y localidades
        municipioDropdown.disabled = false;
        localidadDropdown.disabled = true;
    }
}

function habilitarLocalidad() {
    console.log("Entró en habilitarLocalidad");

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
            if (option.getAttribute('data-municipio-id') === municipioSeleccionado) {
                option.style.display = '';
            } else {
                option.style.display = 'none';
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