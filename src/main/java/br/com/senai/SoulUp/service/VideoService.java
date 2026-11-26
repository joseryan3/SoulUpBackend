    package br.com.senai.SoulUp.service;

    import br.com.senai.SoulUp.model.Usuario;
    import br.com.senai.SoulUp.model.Video;
    import br.com.senai.SoulUp.repository.VideoRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;
    import org.springframework.util.StringUtils;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.nio.file.StandardCopyOption;
    import java.util.UUID;
    @Service
    public class VideoService {

        @Value("${app.upload.dir}")
        private String uploadDir;

        @Autowired
        private AnalysisService analysisService;

        @Autowired
        private VideoRepository videoRepository; // Injeção necessária

        public Video storeVideo(MultipartFile file, Usuario usuario) throws IOException {
            // 1. Validação de formato (RGN08)
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("video/mp4") && !contentType.equals("video/quicktime"))) {
                throw new RuntimeException("Apenas formatos MP4 e MOV são permitidos.");
            }

            // 2. Validação de Tamanho (RGN08) - MOVIDO PARA CÁ
            // Verificamos ANTES de salvar qualquer coisa
            if (file.getSize() > 500 * 1024 * 1024) {
                throw new RuntimeException("Erro RGN08: O arquivo excede o limite de 500 MB.");
            }

            // 3. Criar diretório se não existir
            Path copyLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(copyLocation);

            // 4. Definir nome e caminho do arquivo
            String fileName = StringUtils.cleanPath(UUID.randomUUID().toString() + "_" + file.getOriginalFilename());
            Path targetLocation = copyLocation.resolve(fileName);

            // 5. Salva o arquivo físico no disco
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 6. Instancia a entidade e preenche os dados
            Video video = new Video();
            video.setFileName(fileName);
            video.setContentType(contentType);
            video.setSize(file.getSize());
            video.setFilePath(targetLocation.toString());
            video.setUsuario(usuario);
            video.setStatus("PENDENTE"); // Importante para a RGN09

            // 7. Salva no banco de dados
            Video savedVideo = videoRepository.save(video);

            // 8. Dispara a análise automática (RGN09/10)
            analysisService.processVideo(savedVideo);

            return savedVideo;
        }
    }