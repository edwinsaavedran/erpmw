package com.miempresa.erpmw.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompraDTO {

    private Integer idOrdenCompra; // Para futuras ediciones

    @NotNull(message = "La fecha de la orden es obligatoria.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") // Para el binding con input type datetime-local
    @PastOrPresent(message = "La fecha de la orden no puede ser futura.")
    private LocalDateTime fecha;

    @NotBlank(message = "El tipo de pago no puede estar vacío.")
    @Size(max = 80)
    private String tipoPago;

    @NotBlank(message = "La moneda no puede estar vacía.")
    @Size(max = 80)
    private String moneda;

    @Size(max = 400)
    private String observaciones;

    private BigDecimal totalCalculado; // Para mostrar en la vista, calculado en el backend

    @Valid // Para que se validen los DTOs de detalle anidados
    @NotEmpty(message = "La orden de compra debe tener al menos un detalle.")
    private List<DetalleOrdenCompraDTO> detalles = new ArrayList<>();

    // Getters y Setters
    public Integer getIdOrdenCompra() {
        return idOrdenCompra;
    }

    public void setIdOrdenCompra(Integer idOrdenCompra) {
        this.idOrdenCompra = idOrdenCompra;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public BigDecimal getTotalCalculado() {
        return totalCalculado;
    }

    public void setTotalCalculado(BigDecimal totalCalculado) {
        this.totalCalculado = totalCalculado;
    }

    public List<DetalleOrdenCompraDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleOrdenCompraDTO> detalles) {
        this.detalles = detalles;
    }

    // Método helper para añadir detalles desde el formulario si es necesario (aunque Spring suele hacerlo bien)
    public void addDetalle(DetalleOrdenCompraDTO detalle) {
        if (this.detalles == null) {
            this.detalles = new ArrayList<>();
        }
        this.detalles.add(detalle);
    }
}