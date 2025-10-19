package com.automotiva.estetica.rick.api_agendamento_servicos.service.observer;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class OrdemServicoSubject {
    private List<OrdemServicoObserver> observers = new ArrayList<>();

    public void subscribe(OrdemServicoObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(OrdemServicoObserver observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(OrdemServicoEntity ordemServico) {
        for (OrdemServicoObserver observer : observers) {
            observer.update(ordemServico);
        }
    }
}