package com.mycompany.tr1.bdd.ejercicio3.servicio;

public class TransporteServicio {

    private ArchivoServicio archivoServicio;
    private RutaServicio rutaServicio;
    private PasajeroServicio pasajeroServicio;
    private ViajeServicio viajeServicio;
    private BoletoServicio boletoServicio;

    public TransporteServicio() {

        archivoServicio = new ArchivoServicio();

        rutaServicio = new RutaServicio(archivoServicio);
        pasajeroServicio = new PasajeroServicio(archivoServicio);
        viajeServicio = new ViajeServicio(archivoServicio, rutaServicio);

boletoServicio = new BoletoServicio( archivoServicio,viajeServicio, pasajeroServicio
);}

    public RutaServicio getRutaServicio() {
        return rutaServicio;
    }

    public PasajeroServicio getPasajeroServicio() {
        return pasajeroServicio;
    }

    public ViajeServicio getViajeServicio() {
        return viajeServicio;
    }

    public BoletoServicio getBoletoServicio() {
        return boletoServicio;
    }
}