package br.com.senai.SoulUp.controller;

import br.com.senai.SoulUp.model.SupportTicket;
import br.com.senai.SoulUp.model.Usuario;
import br.com.senai.SoulUp.repository.SupportTicketRepository;
import br.com.senai.SoulUp.service.AuthenticatedUserService;
import br.com.senai.SoulUp.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    private final SupportTicketRepository ticketRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final EmailService emailService;
    private final String supportAddress;

    public SupportController(SupportTicketRepository ticketRepository,
                             AuthenticatedUserService authenticatedUserService,
                             EmailService emailService,
                             @Value("${app.support.email:}") String supportAddress) {
        this.ticketRepository = ticketRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.emailService = emailService;
        this.supportAddress = supportAddress;
    }

    @PostMapping("/tickets")
    public ResponseEntity<SupportTicket> createTicket(@RequestBody SupportTicket ticket, Authentication authentication) {
        Usuario usuario = authenticatedUserService.getRequiredUser(authentication);
        ticket.setId(null);
        ticket.setUsuario(usuario);
        SupportTicket savedTicket = ticketRepository.save(ticket);

        if (!supportAddress.isBlank()) {
            emailService.sendSimpleMessage(
                    supportAddress,
                    "Novo ticket: " + savedTicket.getSubject(),
                    savedTicket.getMessage()
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedTicket);
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<List<SupportTicket>> getMyTickets(Authentication authentication) {
        Usuario usuario = authenticatedUserService.getRequiredUser(authentication);
        return ResponseEntity.ok(ticketRepository.findByUsuarioId(usuario.getId().longValue()));
    }
}
