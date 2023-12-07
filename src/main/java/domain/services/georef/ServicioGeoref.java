package domain.services.georef;

import domain.services.georef.entities.ListadoDeLocalidades;
import domain.services.georef.entities.ListadoDeMunicipios;
import domain.services.georef.entities.ListadoDeProvincias;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class ServicioGeoref {
    private static ServicioGeoref instacia = null;
    private static final String urlAPI = "https://apis.datos.gob.ar/georef/api/";
    private Retrofit retrofit;
    private ServicioGeoref(){
        this.retrofit = new Retrofit.Builder()
                .baseUrl(urlAPI)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static ServicioGeoref getInstancia(){
        if(instacia==null){
            instacia = new ServicioGeoref();
        }
        return instacia;
    }

    public ListadoDeProvincias listadoDeProvincias() throws IOException {
        GeorefService georefService = this.retrofit.create(GeorefService.class);
        Call<ListadoDeProvincias> requestListadoProvinciasArg = georefService.provincias();
        Response<ListadoDeProvincias> responseListadoProvinciasArg = requestListadoProvinciasArg.execute();
        return responseListadoProvinciasArg.body();
    }

    public ListadoDeMunicipios listadoDeMunicipiosDeProvincia(long id) throws IOException {
        GeorefService georefService = this.retrofit.create(GeorefService.class);
        Call<ListadoDeMunicipios> requestListadoMunicipiosDeProvincia = georefService.municipios(id);
        Response<ListadoDeMunicipios> responseListadoMunicipiosDeProvincia = requestListadoMunicipiosDeProvincia.execute();
        return responseListadoMunicipiosDeProvincia.body();
    }

    public ListadoDeLocalidades listadoDeLocalidadesDeMunicipio(long id) throws IOException {
        GeorefService georefService = this.retrofit.create(GeorefService.class);
        Call<ListadoDeLocalidades> requestListadoLocalidadesDeMunicipio = georefService.localidades(id);
        Response<ListadoDeLocalidades> responseListadoLocalidadesDeMunicipio = requestListadoLocalidadesDeMunicipio.execute();
        return responseListadoLocalidadesDeMunicipio.body();
    }

}
