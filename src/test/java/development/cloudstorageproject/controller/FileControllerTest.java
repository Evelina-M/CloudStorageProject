package development.cloudstorageproject.controller;

import development.cloudstorageproject.dto.FileDto;
import development.cloudstorageproject.entity.FileEntity;
import development.cloudstorageproject.security.JwtToken;
import development.cloudstorageproject.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FileControllerTest {
    private FileController fileController;
    @Mock
    private FileService fileService;

    @Mock
    private JwtToken jwtToken;

    private final String AUTH_TOKEN = "Bearer test-token";
    private final String FILE_NAME = "test.txt";
    private final String OWNER = "testOwner";
    private FileEntity fileEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileController = new FileController(fileService, jwtToken);
        fileEntity = new FileEntity();
        fileEntity.setFileName(FILE_NAME);
    }

    @Test
    void addFile_Success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(FILE_NAME);
        when(jwtToken.getLoginFromAuthToken(any())).thenReturn(OWNER);

        ResponseEntity<Void> response = fileController.addFile(AUTH_TOKEN, FILE_NAME, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(fileService, times(1)).uploadFile(OWNER, FILE_NAME, file);
    }

    @Test
    void deleteFile_Success() {
        when(jwtToken.getLoginFromAuthToken(any())).thenReturn(OWNER);

        ResponseEntity<Void> response = fileController.deleteFile(AUTH_TOKEN, FILE_NAME);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(fileService, times(1)).deleteFile(FILE_NAME, OWNER);
    }

    @Test
    void getFile_Success() {
        when(jwtToken.getLoginFromAuthToken(any())).thenReturn(OWNER);
        when(fileService.downloadFile(FILE_NAME, OWNER)).thenReturn(fileEntity);

        ResponseEntity<FileEntity> response = fileController.getFile(AUTH_TOKEN, FILE_NAME);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fileEntity, response.getBody());
        verify(fileService, times(1)).downloadFile(FILE_NAME, OWNER);
    }

    @Test
    void putFile_Success() {
        FileDto fileDto = new FileDto("newName.txt");
        when(jwtToken.getLoginFromAuthToken(any())).thenReturn(OWNER);

        ResponseEntity<Void> response = fileController.putFile(AUTH_TOKEN, FILE_NAME, fileDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(fileService, times(1)).renameFile(OWNER, FILE_NAME, "newName.txt");
    }

    @Test
    void getFiles_Success() {
        when(jwtToken.getLoginFromAuthToken(any())).thenReturn(OWNER);
        when(fileService.getFiles(OWNER, 10)).thenReturn(Collections.singletonList(fileEntity));

        ResponseEntity<List<FileDto>> response = fileController.getFiles(AUTH_TOKEN, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<FileDto> expectedFiles = Collections.singletonList(new FileDto(FILE_NAME));
        assertEquals(expectedFiles, response.getBody());
    }

    @Test
    void addFile_ShouldThrowIOException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(FILE_NAME);
        when(jwtToken.getLoginFromAuthToken(any())).thenReturn(OWNER);
        doThrow(new IOException()).when(fileService).uploadFile(any(), any(), any());

        assertThrows(IOException.class, () -> fileController.addFile(AUTH_TOKEN, FILE_NAME, file));
    }
}

