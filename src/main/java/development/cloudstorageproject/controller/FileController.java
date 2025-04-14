package development.cloudstorageproject.controller;

import development.cloudstorageproject.dto.FileDto;
import development.cloudstorageproject.entity.FileEntity;
import development.cloudstorageproject.security.JwtToken;
import development.cloudstorageproject.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cloud")
public class FileController {
    private final FileService fileService;
    private final JwtToken jwtToken;

    @PostMapping("/file")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity addFile(@RequestHeader("auth-token") String authToken,
                                  @RequestParam("filename") String fileName,
                                  @RequestBody MultipartFile file) throws IOException {
        fileService.uploadFile(getOwner(authToken), fileName, file);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/file")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity deleteFile(@RequestHeader("auth-token") String authToken,
                                     @RequestParam("filename") String fileName) {
        fileService.deleteFile(fileName, getOwner(authToken));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/file")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity getFile(@RequestHeader("auth-token") String authToken,
                                  @RequestParam("filename") String fileName) {
        return ResponseEntity.ok(fileService.downloadFile(fileName, getOwner(authToken)));
    }

    @PutMapping("/file")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity putFile(@RequestHeader("auth-token") String authToken,
                                  @RequestParam("filename") String fileName,
                                  @RequestBody FileDto fileNameRequest) {
        fileService.renameFile(getOwner(authToken), fileName, fileNameRequest.getFileName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity getFiles(@RequestHeader("auth-token") String authToken,
                                   @RequestParam("limit") int limit) {
        List<FileEntity> files = fileService.getFiles(getOwner(authToken), limit);
        List<FileDto> fileResponses = files.stream()
                .map(file -> new FileDto(file.getFileName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(fileResponses);
    }

    private String getCleanToken(String authToken) {
        return authToken.substring(7);
    }

    private String getOwner(String authToken) {
        return jwtToken.getLoginFromAuthToken(getCleanToken(authToken));
    }
}