package domain.services.georef;

import domain.services.georef.entities.ListadoDeLocalidades;
import domain.services.georef.entities.ListadoDeMunicipios;
import domain.services.georef.entities.ListadoDeProvincias;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeorefService {
    @GET("provincias")
    Call<ListadoDeProvincias> provincias();

    @GET("provincias")
    Call<ListadoDeProvincias> provincias(@Query("campos")String campos);

    @GET("municipios")
    Call<ListadoDeMunicipios> municipios(@Query("provincia")long idProvincia);

    @GET("localidades")
    Call<ListadoDeLocalidades> localidades();

    @GET("localidades")
    Call<ListadoDeLocalidades> localidades(@Query("municipio")long idMunicipio);

    @GET("municipios")
    Call<ListadoDeMunicipios> municipios(@Query("provincia")long idProvincia, @Query("campos") String campos, @Query("max") int max);
}
