package com.ejemplo.laboratorio11.dto;

public class VentaRequest {
    private Long productoId;
    private Integer cantidad;
    // Solo para demostrar el ROLLBACK; nunca usar esta bandera en produccion.
    private Boolean simularError = false;

    public VentaRequest() { }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Boolean getSimularError() { return simularError; }
    public void setSimularError(Boolean simularError) { this.simularError = simularError; }
}
