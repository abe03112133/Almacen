package com.PPA.procesadora.service;

import com.PPA.procesadora.entity.Rol;
import com.PPA.procesadora.entity.Usuario;
import com.PPA.procesadora.repository.RolRepository;
import com.PPA.procesadora.repository.UsuarioRepository;
import com.PPA.procesadora.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Valida credenciales y retorna usuario si son correctas
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> validarCredenciales(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioOpt.get();

        // Validar que está activo
        if (!usuario.getActivo()) {
            throw new IllegalArgumentException("Usuario inactivo");
        }

        // Validar contraseña
        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            return Optional.empty();
        }

        return Optional.of(usuario);
    }

    /**
     * Obtiene un usuario por ID
     */
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return mapearADTO(usuario);
    }

    /**
     * Obtiene un usuario por username
     */
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return mapearADTO(usuario);
    }

    /**
     * Obtiene todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo usuario
     */
    @Transactional
    public UsuarioDTO crearUsuario(CrearUsuarioDTO dto) {
        // Validar que username no exista
        if (usuarioRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        // Obtener rol
        Rol rol = rolRepository.findByTipo(dto.getRolTipo())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + dto.getRolTipo()));

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setFechaIngreso(DateUtils.ahora());

        Usuario guardado = usuarioRepository.save(usuario);
        return mapearADTO(guardado);
    }
    /**
     * Obtiene un usuario por username (retorna Optional<Usuario>)
     * Para usar en Spring Security
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
    /**
     * Actualiza un usuario
     */
    @Transactional
    public UsuarioDTO actualizarUsuario(Integer id, ActualizarUsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (dto.getNombreCompleto() != null && !dto.getNombreCompleto().isBlank()) {
            usuario.setNombreCompleto(dto.getNombreCompleto());
        }

        if (dto.getRolTipo() != null && !dto.getRolTipo().isBlank()) {
            Rol rol = rolRepository.findByTipo(dto.getRolTipo())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
            usuario.setRol(rol);
        }

        if (dto.getActivo() != null) {
            usuario.setActivo(dto.getActivo());
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        return mapearADTO(actualizado);
    }

    /**
     * Cambia contraseña de un usuario
     */
    @Transactional
    public void cambiarPassword(Integer id, String passwordAntigua, String passwordNueva) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validar contraseña antigua
        if (!passwordEncoder.matches(passwordAntigua, usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        usuario.setPasswordHash(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }

    /**
     * Desactiva un usuario
     */
    @Transactional
    public void desactivarUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    /**
     * Mapea entidad a DTO
     */
    private UsuarioDTO mapearADTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getRol().getTipo(),
                usuario.getActivo(),
                DateUtils.formatear(usuario.getFechaIngreso())
        );
    }

    // DTOs
    public static class UsuarioDTO {
        public Integer id;
        public String username;
        public String nombreCompleto;
        public String rolTipo;
        public Boolean activo;
        public String fechaIngreso;

        public UsuarioDTO(Integer id, String username, String nombreCompleto, String rolTipo, Boolean activo, String fechaIngreso) {
            this.id = id;
            this.username = username;
            this.nombreCompleto = nombreCompleto;
            this.rolTipo = rolTipo;
            this.activo = activo;
            this.fechaIngreso = fechaIngreso;
        }

        public Integer getId() { return id; }
        public String getUsername() { return username; }
        public String getNombreCompleto() { return nombreCompleto; }
        public String getRolTipo() { return rolTipo; }
        public Boolean getActivo() { return activo; }
        public String getFechaIngreso() { return fechaIngreso; }
    }

    public static class CrearUsuarioDTO {
        public String username;
        public String nombreCompleto;
        public String password;
        public String rolTipo;

        public String getUsername() { return username; }
        public String getNombreCompleto() { return nombreCompleto; }
        public String getPassword() { return password; }
        public String getRolTipo() { return rolTipo; }
    }

    public static class ActualizarUsuarioDTO {
        public String nombreCompleto;
        public String rolTipo;
        public Boolean activo;

        public String getNombreCompleto() { return nombreCompleto; }
        public String getRolTipo() { return rolTipo; }
        public Boolean getActivo() { return activo; }
    }
}