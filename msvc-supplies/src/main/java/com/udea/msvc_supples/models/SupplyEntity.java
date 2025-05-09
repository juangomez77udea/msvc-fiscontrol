package com.udea.msvc_supples.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "supply")
public class SupplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String suppliesName;

    @NotBlank
    private String presentation;

    @NotNull
    private Integer suppliesQuantity;

    @NotNull
    private Double suppliesPrice;

    @NotNull
    private LocalDate suppliesDate;

    // tipo de insumo
    @Enumerated(EnumType.STRING)
    @NotNull
    private ESupplyType type;

    // etapa de producción (opcional)
    private String stage;

    // Validación para la presentación del alimento
    public void setPresentation(String presentation) {
        if (this.type == ESupplyType.FOOD && !("40kg".equals(presentation) || !("20kg".equals(presentation)))) {
            throw new IllegalArgumentException("La presentación del alimento debe ser '40kg' o '20kg'.");
        }
        this.presentation = presentation;
    }

}
