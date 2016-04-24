package es.ieslosviveros.chat;

/**
 * Created by papa on 03/04/2016.
 */
public interface IObserver {
    void addObserver (IObserver observer);
    void removeObserver(IObserver observer);
    void update();
}
