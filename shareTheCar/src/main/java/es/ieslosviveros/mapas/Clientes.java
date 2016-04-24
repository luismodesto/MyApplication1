package es.ieslosviveros.mapas;

/**
 * Created by papa on 10/04/2016.
 */
public class Clientes {
    public String user_id,email,curso,lat,lng;
    public Clientes(String user_id,String email,String curso,String lat,String lng){
        this.curso=curso;
        this.email=email;
        this.lat=lat;
        this.lng=lng;
        this.user_id=user_id;

    }
}
