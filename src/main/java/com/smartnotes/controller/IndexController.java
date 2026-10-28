package com.smartnotes.controller;

import com.smartnotes.service.UsuarioService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final UsuarioService usuarioService;

    @GetMapping("/")
    public String index(Principal principal, Model model) {
        // Datos del usuario que inició sesión para mostrar su nombre y perfil
        var usuario = usuarioService.getUsuarioPorUsername(principal.getName());
        model.addAttribute("usuario", usuario.orElse(null));
        return "index";
    }
}
