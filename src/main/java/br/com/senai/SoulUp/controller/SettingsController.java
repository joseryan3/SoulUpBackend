package br.com.senai.SoulUp.controller;

import br.com.senai.SoulUp.model.UserSettings;
import br.com.senai.SoulUp.model.Usuario;
import br.com.senai.SoulUp.repository.UserSettingsRepository;
import br.com.senai.SoulUp.service.AuthenticatedUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final UserSettingsRepository settingsRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public SettingsController(UserSettingsRepository settingsRepository,
                              AuthenticatedUserService authenticatedUserService) {
        this.settingsRepository = settingsRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @GetMapping
    public ResponseEntity<UserSettings> getSettings(Authentication authentication) {
        Usuario usuario = authenticatedUserService.getRequiredUser(authentication);
        return ResponseEntity.ok(settingsRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> createDefaultSettings(usuario)));
    }

    @PutMapping
    public ResponseEntity<UserSettings> updateSettings(@RequestBody UserSettings request,
                                                        Authentication authentication) {
        Usuario usuario = authenticatedUserService.getRequiredUser(authentication);
        UserSettings settings = settingsRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> createDefaultSettings(usuario));
        settings.setLanguage(request.getLanguage());
        settings.setPublicProfile(request.isPublicProfile());
        settings.setEmailNotifications(request.isEmailNotifications());
        settings.setPushNotifications(request.isPushNotifications());
        return ResponseEntity.ok(settingsRepository.save(settings));
    }

    private UserSettings createDefaultSettings(Usuario usuario) {
        UserSettings settings = new UserSettings();
        settings.setUsuario(usuario);
        return settingsRepository.save(settings);
    }
}
