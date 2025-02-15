package gm.tareas.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "tarea")
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarea")
    private Integer idTarea;

    @Column(name = "nombre_tarea")
    private String nombreTarea;

    @Column(name = "responsable")
    private String responsable;

    @Column(name = "estatus")
    private String estatus;
}
