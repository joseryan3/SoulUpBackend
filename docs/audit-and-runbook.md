# Auditoria e execução do SoulUp

## Relação entre os arquivos recebidos

- `src.zip` contém apenas o código-fonte de um frontend React, sem `package.json` ou diretório público.
- `SoulUp-unificado-corrigido.zip` contém o backend Spring Boot, a IA em Python e um protótipo estático em `frontend/`.
- A versão corrigida unifica os dois: o React de `src.zip` foi colocado em `frontend/src`, recebeu a estrutura de execução necessária e substitui o papel do protótipo estático.

## Causas encontradas e correções

| Área | Causa | Correção |
| --- | --- | --- |
| Login | O endpoint apenas comparava senha e retornava dados; não persistia autenticação no Spring Security. | O login cria e salva uma sessão HTTP com a role do usuário. O React envia cookies com `credentials: include`. |
| Segurança | Endpoints de vídeo, cadastro de qualquer rota de usuário e CORS estavam amplamente abertos ou duplicados. | Apenas login, cadastro e recuperação de senha são públicos. CORS é centralizado em `SoulSecurity` e configurável por variável de ambiente. |
| Vídeos | Upload usava usuário `null`; histórico e suporte usavam o ID `1`; operações não verificavam proprietário. | Upload, histórico, streaming, download e exclusão usam o usuário da sessão e validam proprietário (ou administrador). |
| Cadastro | E-mail não era normalizado nem tinha garantia de unicidade no modelo; uma corrida podia gerar erro interno. | E-mail é normalizado, há índice único e o conflito é tratado no registro. Senhas continuam com BCrypt e validação de força. |
| Recuperação de senha | O e-mail apontava para uma rota backend inexistente para o usuário final; não havia tela para consumir token. | O link usa `APP_FRONTEND_URL/resetarsenha?token=...` e a tela React conclui a redefinição. Tokens anteriores do usuário são substituídos. |
| SMTP e segredos | Havia senhas, credenciais OAuth e chave Gemini no arquivo versionado. | Todas as credenciais foram removidas. O SMTP e as integrações usam variáveis de ambiente, documentadas em `.env.example`. |
| Frontend | Faltavam metadados de dependências e a rota pós-login apontava para uma tela de upload chamada de histórico. | Foram criados `package.json` e `public/index.html`; o pós-login e o plano gratuito direcionam para `/upload`. |

## Arquivos principais modificados

- `src/main/java/br/com/senai/SoulUp/security/SoulSecurity.java`
- `src/main/java/br/com/senai/SoulUp/controller/{LoginController,UserController,PasswordController,VideoController,SettingsController,SupportController,PaymentController}.java`
- `src/main/java/br/com/senai/SoulUp/service/{AuthenticatedUserService,PasswordService,EmailService}.java`
- `src/main/java/br/com/senai/SoulUp/{model,repository,DTO}/...`
- `src/main/resources/application.properties` e `src/test/resources/application-test.properties`
- `frontend/package.json`, `frontend/public/index.html`, `frontend/src/services/api.js`, `frontend/src/App.js` e telas de login, recuperação, preços e upload.

## Como executar

1. Copie `.env.example` para um arquivo de variáveis do seu ambiente e preencha apenas os serviços que usará.
2. Inicie MySQL e configure `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`.
3. Para e-mails em desenvolvimento, execute um SMTP local como MailHog em `localhost:1025`; para produção, informe host, porta e credenciais SMTP por variáveis de ambiente.
4. Com JDK 17 ou superior: `mvnw.cmd test` e depois `mvnw.cmd spring-boot:run`.
5. Em `frontend/`: `npm install` e `npm start`. Use `REACT_APP_API_URL` se a API não estiver em `http://localhost:8080`.

## Validação realizada

- Frontend: build de produção concluído com sucesso.
- Frontend: teste `src/App.test.js` concluído com sucesso.
- Backend: a validação foi preparada com H2 pelo perfil `test`, mas não pôde ser executada nesta máquina porque só há Java 8 instalado e o projeto exige Java 17.

## Pendências de ambiente e limites conhecidos

- Não foi feito envio SMTP real: faltam servidor e credenciais do provedor. O fluxo está configurado para MailHog local por padrão.
- Não foi feita integração com MySQL, Python/MediaPipe ou Gemini: dependem dos respectivos serviços, runtime Python e, no caso do Gemini, de uma chave válida.
- Os endpoints legados de comunidade e treinos ainda recebem IDs em parâmetros de algumas operações. Eles agora exigem sessão pelo filtro global, mas devem receber uma revisão específica de regras de propriedade antes de uma exposição pública ampla.
- O endpoint de pagamento continua uma simulação e não substitui a validação de pagamento por um provedor real.
