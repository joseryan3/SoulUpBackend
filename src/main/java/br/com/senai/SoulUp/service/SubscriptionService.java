package br.com.senai.SoulUp.service;

import br.com.senai.SoulUp.model.Payment;
import br.com.senai.SoulUp.model.Usuario;
import br.com.senai.SoulUp.repository.PaymentRepository;
import br.com.senai.SoulUp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SubscriptionService {

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public void ativarAssinaturaPremium(Long usuarioId, Double valor, String metodo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 1. Cria o registro de pagamento
        Payment payment = new Payment();
        payment.setUsuario(usuario);
        payment.setAmount(valor);
        payment.setStatus("APROVADO");
        payment.setPaymentMethod(metodo);
        paymentRepository.save(payment);

        // 2. Atualiza o status do usuário
        usuario.setPremium(true);
        usuario.setPlanoTipo("MENSAL");
        usuario.setDataExpiracaoAssinatura(LocalDateTime.now().plusMonths(1));

        usuarioRepository.save(usuario);
    }
}