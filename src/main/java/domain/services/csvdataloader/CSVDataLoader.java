package domain.services.csvdataloader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import domain.Repositorios.RepositorioEntidad;
import domain.Repositorios.RepositorioServicio;
import domain.Repositorios.RepositorioUsuario;
import domain.Usuarios.EntidadPrestadora;
import domain.Usuarios.OrganismoDeControl;
import domain.Usuarios.Usuario;
import domain.entidades.Entidad;
import domain.servicios.Servicio;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVDataLoader {
    public List<EntidadPrestadora> leerArchivoEntidades(InputStream inputStream) {
        List<EntidadPrestadora> entidadesPrestadoras = new ArrayList<>();
        RepositorioEntidad repositorioEntidad = new RepositorioEntidad();
        RepositorioUsuario repositorioUsuario = new RepositorioUsuario();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> records = csvReader.readAll();

            boolean primeraLinea = true;
            for (String[] record : records) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                String nombreEntidadPrestadora = record[0];
                String desripcionEntidadPrestadora = record[1];
                String mail = record[2];
                String username = record[3];
                String nombreEntidad = record[4];

                // Crea un objeto EntidadPrestadora y agrégalo a la lista
                EntidadPrestadora entidadPrestadora = new EntidadPrestadora();
               Usuario userEntidadPrestadora = repositorioUsuario.findUsuarioByUsername(username);
               Entidad entidad = repositorioEntidad.findEntidadByName(nombreEntidad);
                entidadPrestadora.setUsuario(userEntidadPrestadora);
                entidadPrestadora.setEntidad(entidad);
                entidadPrestadora.setNombre(nombreEntidadPrestadora);
                entidadPrestadora.setDescripcion(desripcionEntidadPrestadora);
                entidadPrestadora.setCorreoElectronicoResponsable(mail);
                if(userEntidadPrestadora == null || entidad == null){
                   entidadPrestadora = null;
                }


                entidadesPrestadoras.add(entidadPrestadora);
            }

        } catch (IOException | CsvException e) {
            throw new RuntimeException("Error al leer el archivo CSV", e);
        }


        return entidadesPrestadoras;

    }

    public List<OrganismoDeControl> leerArchivoOrganismo(InputStream inputStream) {
        List<OrganismoDeControl> organismosDeControl = new ArrayList<>();
        RepositorioServicio repositorioServicio = new RepositorioServicio();
        RepositorioUsuario repositorioUsuario = new RepositorioUsuario();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> records = csvReader.readAll();

            boolean primeraLinea = true;
            for (String[] record : records) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                String nombreOrganismoControl = record[0];
                String desripcionOrganismoControl = record[1];
                String mail = record[2];
                String username = record[3];
                String nombreServicio = record[4];

                // Crea un objeto ORganismoControl y agrégalo a la lista
                OrganismoDeControl organismoDeControl = new OrganismoDeControl();
                Usuario userServicio = repositorioUsuario.findUsuarioByUsername(username);
                Servicio servicio = repositorioServicio.findServicioByName(nombreServicio);
                organismoDeControl.setUsuario(userServicio);
                organismoDeControl.setServicio(servicio);
                organismoDeControl.setNombre(nombreOrganismoControl);
                organismoDeControl.setDescripcion(desripcionOrganismoControl);
                organismoDeControl.setCorreoElectronicoResponsable(mail);
                // Asigna los demás campos...

                organismosDeControl.add(organismoDeControl);
            }

        } catch (IOException | CsvException e) {
            throw new RuntimeException("Error al leer el archivo CSV", e);
        }


        return organismosDeControl;

    }
}

