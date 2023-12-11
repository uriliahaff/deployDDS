function habilitarEstablecimiento() {
    console.log("Entró en habilitarEstablecimiento");
    var entidadDropdown = document.getElementById('entidad');
    var establecimientoDropdown = document.getElementById('establecimiento');
    var servicioDropdown = document.getElementById('servicio');

    var entidadSeleccionada = entidadDropdown.value;

    console.log("Entidad seleccionada:", entidadSeleccionada);

    if (entidadSeleccionada !== '') {
        // Resetear los campos de establecimiento y servicio al cambiar la provincia
        establecimientoDropdown.value = '';
        servicioDropdown.value = '';

        var establecimientos = document.querySelectorAll('#establecimiento option');
        console.log("Total de establecimientos encontrados:", establecimientos.length);

        establecimientos.forEach(function (option) {
            if (option.getAttribute('data-entidad-id') === entidadSeleccionada) {
                option.style.display = '';
            } else {
                option.style.display = 'none';
            }
        });

        // Deshabilitar y habilitar el dropdown de municipios y localidades
        establecimientoDropdown.disabled = false;
        servicioDropdown.disabled = true;
    }
}

function habilitarServicio() {
    console.log("Entró en habilitarServicio");

    var establecimientoDropdown = document.getElementById('establecimiento');
    var servicioDropdown = document.getElementById('servicio');

    var establecimientoSeleccionado = establecimientoDropdown.value;

    console.log("Establecimiento seleccionado:", establecimientoSeleccionado);

    if (establecimientoSeleccionado !== '') {
        // Resetear el campo de localidad al cambiar el municipio
        servicioDropdown.value = '';

        var servicios = document.querySelectorAll('#servicio option');
        console.log("Total de servicios encontrados:", servicios.length);

        servicios.forEach(function (option) {
            if (option.getAttribute('data-establecimiento-id') === establecimientoSeleccionado) {
                option.style.display = '';
            } else {
                option.style.display = 'none';
            }
        });

        // Deshabilitar y habilitar el dropdown de localidades
        servicioDropdown.disabled = false;
    } else {
        // Si no se ha seleccionado un municipio, deshabilitar y reiniciar el dropdown de localidades
        servicioDropdown.innerHTML = '<option value="">Primero seleccione un Establecimiento</option>';
        servicioDropdown.disabled = true;
    }
}