package crudconconexionOracle.com.example.crud_con_Oracle.modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
@Entity
@Table(name = "EMPLEADOS") // ✅ Sin schema
@Schema(description = "Entidad que representa un empleado en el sistema")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "empleado_seq")
    @SequenceGenerator(
            name = "empleado_seq",
            sequenceName = "EMPLEADO_SEQ", // Oracle lo guardará como EMPLEADO_SEQ (mayúsculas)
            allocationSize = 1
    )
    @Schema(description = "ID único del empleado (generado automáticamente)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(name = "NOMBRE", nullable = false, length = 100)
    @Schema(description = "Nombre completo del empleado", example = "Juan Pérez García", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Size(max = 50, message = "El área no puede exceder 50 caracteres")
    @Column(name = "AREA", length = 50)
    @Schema(description = "Área o departamento del empleado", example = "Desarrollo de Software")
    private String area;

    @Min(value = 18, message = "La edad mínima es 18 años")
    @Max(value = 100, message = "La edad máxima es 100 años")
    @Column(name = "EDAD")
    @Schema(description = "Edad del empleado", example = "30", minimum = "18", maximum = "100")
    private Integer edad;
    @NotBlank(message = "el correo debe ser vallido")
    @Email(message = "El correo electrónico debe ser válido")
    @Column(name = "CORREO_ELECTRONICO",nullable = false, length = 100)
    @Schema(description = "Correo electrónico corporativo", example = "juan.perez@empresa.com", format = "email")
    private String correoElectronico;

    @DecimalMin(value = "0.0", message = "El sueldo no puede ser negativo")
    @Column(name = "SUELDO")
    @Schema(description = "Sueldo mensual del empleado", example = "50000.00", minimum = "0")
    private Double sueldo;
}