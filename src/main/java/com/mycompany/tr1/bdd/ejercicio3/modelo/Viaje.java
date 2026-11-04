package com.mycompany.tr1.bdd.ejercicio3.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Viaje {

    private String id;
    private Ruta ruta;
    private LocalDateTime fechaHoraSalida;
    private int capacidad;
    private double tarifaBase;
    private TipoViaje tipo;
    private List<Boleto> boletos;

    public Viaje(String id, Ruta ruta, LocalDateTime fechaHoraSalida,
                 int capacidad, double tarifaBase, TipoViaje tipo) {
        this.id = id;
        this.ruta = ruta;
        this.fechaHoraSalida = fechaHoraSalida;
        this.capacidad = capacidad;
        this.tarifaBase = tarifaBase;
        this.tipo = tipo;
        this.boletos = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public double getTarifaBase() {
        return tarifaBase;
    }

    public TipoViaje getTipo() {
        return tipo;
    }

    public List<Boleto> getBoletos() {
        return boletos;
    }

    public void agregarBoleto(Boleto boleto) {
        boletos.add(boleto);
    }
    
    
    
    
    
}