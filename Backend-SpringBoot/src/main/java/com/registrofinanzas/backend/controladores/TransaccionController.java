package com.registrofinanzas.backend.controladores;

import com.registrofinanzas.backend.dto.TransaccionResponse;
import com.registrofinanzas.backend.entidades.Transaccion;
import com.registrofinanzas.backend.repositorios.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transacciones")
@CrossOrigin(origins = "*")
public class TransaccionController {

    @Autowired
    private TransaccionRepository transaccionRepository;

    @GetMapping
    public List<TransaccionResponse> obtenerTodas() {
        return transaccionRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaccion> obtenerPorId(@PathVariable Integer id) {
        Optional<Transaccion> transaccion = transaccionRepository.findById(id);
        return transaccion.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<TransaccionResponse> obtenerPorUsuario(@PathVariable Integer usuarioId) {
        return transaccionRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @PostMapping
    public Transaccion crear(@RequestBody Transaccion transaccion) {
        transaccion.setFechaCreacion(new Date());
        // Asignamos estado activo por defecto
        transaccion.setEstado("A");
        return transaccionRepository.save(transaccion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaccion> actualizar(@PathVariable Integer id,
                                                 @RequestBody Transaccion transaccionActualizada) {
        Optional<Transaccion> transaccionExistente = transaccionRepository.findById(id);

        if (transaccionExistente.isPresent()) {
            Transaccion transaccion = transaccionExistente.get();
            transaccion.setMonto(transaccionActualizada.getMonto());
            transaccion.setDescripcion(transaccionActualizada.getDescripcion());
            transaccion.setTipo(transaccionActualizada.getTipo());
            transaccion.setFechaTransaccion(transaccionActualizada.getFechaTransaccion());

            return ResponseEntity.ok(transaccionRepository.save(transaccion));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (transaccionRepository.existsById(id)) {
            transaccionRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    private TransaccionResponse convertirAResponse(Transaccion t) {
        TransaccionResponse response = new TransaccionResponse();
        response.setId(t.getId());
        response.setMonto(t.getMonto());
        response.setDescripcion(t.getDescripcion());
        response.setTipo(t.getTipo());
        response.setFechaTransaccion(t.getFechaTransaccion());
        response.setCategoriaNombre(t.getCategoria() != null ? t.getCategoria().getNombre() : "Sin categoría");
        response.setUsuarioNombre(t.getUsuario() != null ? t.getUsuario().getNombre() : "Sin usuario");
        response.setEstado(t.getEstado());
        return response;
    }
}