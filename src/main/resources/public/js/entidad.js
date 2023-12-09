
    function habilitarMunicipio() {
        console.log("Entró en habilitarMunicipioPropio");
        var provinciaSeleccionada = document.getElementById('provincia').value;
        var municipioDropdown = document.getElementById('municipio');
        var localidadDropdown = document.getElementById('localidad');


        console.log("Provincia seleccionada:", provinciaSeleccionada);

        if (provinciaSeleccionada !== '') {
            var municipios = document.querySelectorAll('#municipio option');
            console.log("Total de municipios encontrados:", municipios.length);
            municipioDropdown.innerHTML = '<option value="">Primero seleccione una Provincia</option>';
            municipioDropdown.disabled = true;
            localidadDropdown.disabled = true;
            municipios.forEach(function (option) {
                if (option.getAttribute('data-provincia-id') === provinciaSeleccionada) {
                    console.log("Agregando municipio:", option.value);

                    municipioDropdown.appendChild(option.cloneNode(true));
                }
            });

            // Habilitar el dropdown de municipios
            municipioDropdown.disabled = false;
        }
}

    function habilitarLocalidad() {
        console.log("Entró en habilitarLocalidadPropio");

        var municipioSeleccionado = document.getElementById("municipio").value;
        var localidadDropdown = document.getElementById("localidad");

        console.log("Municipio seleccionado:", municipioSeleccionado);

        if (municipioSeleccionado !== '') {
            var localidades = document.querySelectorAll('#localidad option');
            console.log("Total de localidades encontradas:", localidades.length);

            localidadDropdown.innerHTML = '<option value="">Primero seleccione un Municipio</option>';
            localidadDropdown.disabled = true;

            localidades.forEach(function (option) {
                if (option.getAttribute('data-municipio-id') === municipioSeleccionado) {
                    console.log("Agregando localidad:", option.value);
                    localidadDropdown.appendChild(option.cloneNode(true));
                }
            });

            // Habilitar el dropdown de localidades
            localidadDropdown.disabled = false;
        } else {
            // Si no se ha seleccionado un municipio, deshabilitar y reiniciar el dropdown de localidades
            localidadDropdown.innerHTML = '<option value="">Primero seleccione un Municipio</option>';
            localidadDropdown.disabled = true;
        }
}
