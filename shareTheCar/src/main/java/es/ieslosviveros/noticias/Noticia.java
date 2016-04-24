package es.ieslosviveros.noticias;

/**
 * Created by papa on 02/04/2016.
 */
public class Noticia {
    public String titulo, fecha, enlace;
    public Noticia(String titulo, String fecha, String enlace){
        this.fecha=fecha;
        this.enlace=enlace;
        this.titulo=titulo;
    }
}
