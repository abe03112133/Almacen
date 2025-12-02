package com.PPA.procesadora.service;

import com.PPA.procesadora.entity.Destino;
import com.PPA.procesadora.repository.DestinoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DestinoService {

    private final DestinoRepository destinoRepository;

    public List<Destino> obtenerTodos() {
        return destinoRepository.findAll();
    }

    public Optional<Destino> obtenerPorId(Integer id) {
        return destinoRepository.findById(id);
    }

    public Optional<Destino> obtenerPorCodigo(String codigo) {
        return Optional.ofNullable(destinoRepository.findByCodigo(codigo));
    }

    public Optional<Destino> obtenerPorNombre(String nombre) {
        return Optional.ofNullable(destinoRepository.findByNombre(nombre));
    }

    public Destino crear(Destino destino) {
        return destinoRepository.save(destino);
    }

    public Destino actualizar(Integer id, Destino destino) {
        Destino existente = destinoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destino no encontrado"));

        existente.setNombre(destino.getNombre());
        existente.setCodigo(destino.getCodigo());

        return destinoRepository.save(existente);
    }

    public void eliminar(Integer id) {
        Destino existente = destinoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destino no encontrado"));

        destinoRepository.delete(existente);
    }
    public List<Destino> obtenerActivos() {
        return destinoRepository.findByActivoTrue();
    }

}
