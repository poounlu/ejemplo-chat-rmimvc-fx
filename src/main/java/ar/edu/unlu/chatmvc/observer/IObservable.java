package ar.edu.unlu.chatmvc.observer;

public interface IObservable {
    void agregarObservador(IObservador observador);
    void removerObservador(IObservador observador);
    void notificarObservadores(Object evento);
    void notificarObservadores();
}
