package br.com.senai.SoulUp.controller;

import br.com.senai.SoulUp.DTO.VideoResponseDTO;
import br.com.senai.SoulUp.model.AnalysisResult;
import br.com.senai.SoulUp.model.Video;
import br.com.senai.SoulUp.model.Usuario;
import br.com.senai.SoulUp.repository.AnalysisResultRepository;
import br.com.senai.SoulUp.repository.VideoRepository;
import br.com.senai.SoulUp.service.VideoService;
import br.com.senai.SoulUp.service.AuthenticatedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private AnalysisResultRepository analysisResultRepository;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            Video video = videoService.storeVideo(file, currentUser(authentication));
            return ResponseEntity.ok(toResponse(video));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/detalhes")
    public ResponseEntity<VideoResponseDTO> getFullDetails(@PathVariable Long id, Authentication authentication) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video nao encontrado"));
        requireOwner(video, authentication);

        AnalysisResult result = analysisResultRepository.findByVideoId(id);
        return ResponseEntity.ok(toResponse(video, result));
    }

    @GetMapping("/stream/{fileName}")
    public ResponseEntity<Resource> streamVideo(@PathVariable String fileName, Authentication authentication) throws IOException {
        Video video = videoRepository.findByFileName(fileName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireOwner(video, authentication);
        Path path = Paths.get(video.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(video.getContentType()))
                .body(resource);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadVideo(@PathVariable Long id, Authentication authentication) throws IOException {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video nao encontrado"));
        requireOwner(video, authentication);

        if (video.getUsuario() == null || !video.getUsuario().isPremium()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Path path = Paths.get(video.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + video.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/historico")
    public ResponseEntity<List<VideoResponseDTO>> listarHistorico(Authentication authentication) {
        Long usuarioLogadoId = currentUser(authentication).getId().longValue();
        List<AnalysisResult> resultados = analysisResultRepository.findByVideoUsuarioIdOrderByProcessedAtDesc(usuarioLogadoId);

        List<VideoResponseDTO> historico = resultados.stream()
                .map(res -> toResponse(res.getVideo(), res))
                .collect(Collectors.toList());

        return ResponseEntity.ok(historico);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id, Authentication authentication) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireOwner(video, authentication);
        videoRepository.delete(video);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Video>> searchVideos(@RequestParam("tipo") String tipo, Authentication authentication) {
        Usuario usuario = currentUser(authentication);
        List<Video> videos = videoRepository.findByTipoExercicioIgnoreCase(tipo).stream()
                .filter(video -> video.getUsuario() != null && video.getUsuario().getId().equals(usuario.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(videos);
    }

    private Usuario currentUser(Authentication authentication) {
        return authenticatedUserService.getRequiredUser(authentication);
    }

    private void requireOwner(Video video, Authentication authentication) {
        Usuario usuario = currentUser(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && (video.getUsuario() == null || !video.getUsuario().getId().equals(usuario.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private VideoResponseDTO toResponse(Video video) {
        AnalysisResult result = analysisResultRepository.findByVideoId(video.getId());
        return toResponse(video, result);
    }

    private VideoResponseDTO toResponse(Video video, AnalysisResult result) {
        VideoResponseDTO response = new VideoResponseDTO();
        response.setId(video.getId());
        response.setFileName(video.getFileName());
        response.setVideoUrl("/api/videos/stream/" + video.getFileName());

        response.setStatus(video.getStatus());

        if (result != null) {
            response.setSuggestions(result.getSuggestions());
            response.setAccuracyScore(result.getAccuracyScore());
            response.setRepetitions(result.getRepetitions());
        } else {
            response.setSuggestions("Video recebido. A analise ainda esta em processamento.");
            response.setAccuracyScore(video.getScore());
        }

        return response;
    }
}