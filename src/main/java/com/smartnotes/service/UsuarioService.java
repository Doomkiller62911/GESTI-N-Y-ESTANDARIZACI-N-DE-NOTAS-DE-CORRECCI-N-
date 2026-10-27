package com.smartnotes.service;

import com.smartnotes.domain.Usuario;
import com.smartnotes.repository.UsuarioRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    // El repositorio es final para asegurar la inmutabilidad
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
}
