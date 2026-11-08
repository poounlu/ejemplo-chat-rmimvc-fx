package ar.edu.unlu.chatmvc.observer;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable implements IObservable {
    private final List<IObservador> observadores = new ArrayList<>();

    @Override
    public void agregarObservador(IObservador observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void removerObservador(IObservador observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores(Object evento) {
        for (IObservador observador : new ArrayList<>(observadores)) {
            observador.actualizar(this, evento);
        }
    }

    @Override
    public void notificarObservadores() {
        notificarObservadores(null);
    }
}
