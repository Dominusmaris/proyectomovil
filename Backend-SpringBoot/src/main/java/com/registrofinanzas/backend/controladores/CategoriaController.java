package com.registrofinanzas.backend.controladores;

import com.registrofinanzas.backend.entidades.Categoria;
import com.registrofinanzas.backend.repositorios.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerPorId(@PathVariable Integer id) {
        Optional<Categoria> categoria = categoriaRepository.findById(id);
        return categoria.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Categoria> obtenerPorUsuario(@PathVariable Integer usuarioId) {
        return categoriaRepository.findByUsuarioId(usuarioId);
    }

    @PostMapping
    public Categoria crear(@RequestBody Categoria categoria) {
        categoria.setFechaCreacion(new Date());
        // Asignamos estado activo por defecto
        categoria.setEstado("A");
        return categoriaRepository.save(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(@PathVariable Integer id,
                                              @RequestBody Categoria categoriaActualizada) {
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);

        if (categoriaExistente.isPresent()) {
            Categoria categoria = categoriaExistente.get();
            categoria.setNombre(categoriaActualizada.getNombre());
            categoria.setDescripcion(categoriaActualizada.getDescripcion());
            categoria.setTipo(categoriaActualizada.getTipo());

            return ResponseEntity.ok(categoriaRepository.save(categoria));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (categoriaRepository.existsById(id)) {
            categoriaRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}