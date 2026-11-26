package br.com.senai.SoulUp.controller;

import br.com.senai.SoulUp.service.SubscriptionService;
import br.com.senai.SoulUp.service.AuthenticatedUserService;
import br.com.senai.SoulUp.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/payments")

public class PaymentController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @PostMapping("/checkout/simular")
    public ResponseEntity<String> simularPagamento(Authentication authentication) {
        Usuario usuario = authenticatedUserService.getRequiredUser(authentication);
        // Simulando um pagamento aprovado de R$ 29,90 via Pix
        subscriptionService.ativarAssinaturaPremium(usuario.getId().longValue(), 29.90, "PIX");
        return ResponseEntity.ok("Assinatura Premium ativada com sucesso!");
    }
}
