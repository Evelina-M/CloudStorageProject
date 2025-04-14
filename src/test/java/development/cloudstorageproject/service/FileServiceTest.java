package development.cloudstorageproject.service;

import development.cloudstorageproject.entity.FileEntity;
import development.cloudstorageproject.exception.FileNotFoundException;
import development.cloudstorageproject.exception.GettingFileListException;
import development.cloudstorageproject.exception.InvalidInputDataException;
import development.cloudstorageproject.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FileServiceTest {
    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    private static final String OWNER = "owner";
    private static final String FILE_NAME = "file.txt";
    private static final String NEW_FILE_NAME = "new_file.txt";
    private static final MultipartFile FILE = new MockMultipartFile(FILE_NAME, "Hello World".getBytes());
    private static FileEntity fileEntity;

    @BeforeEach
    void setUp() {
        fileEntity = new FileEntity();
        fileEntity.setOwner(OWNER);
        fileEntity.setFileName(FILE_NAME);
        fileEntity.setData("Hello World".getBytes());

        when(fileRepository.findByFileNameAndOwner(FILE_NAME, OWNER))
                .thenReturn(fileEntity);
    }

    @Test
    void uploadFile_success() throws IOException {
        fileService.uploadFile(OWNER, FILE_NAME, FILE);
        verify(fileRepository).save(any(FileEntity.class));
    }

    @Test
    void uploadFile_throws() {
        assertThrows(InvalidInputDataException.class, () -> fileService.uploadFile(OWNER, null, null));
        verify(fileRepository, never()).save(any(FileEntity.class));
    }

    @Test
    void deleteFile_success() {
        fileService.deleteFile(FILE_NAME, OWNER);
        verify(fileRepository, times(2)).findByFileNameAndOwner(FILE_NAME, OWNER);
    }

    @Test
    void deleteFile_fileNotFound() {
        assertThrows(InvalidInputDataException.class, () -> fileService.deleteFile(null, OWNER));
        verify(fileRepository, never()).findByFileNameAndOwner(anyString(), anyString());
    }

    @Test
    void downloadFile_success() {
        when(fileRepository.findByFileNameAndOwner(FILE_NAME, OWNER)).thenReturn(fileEntity);
        FileEntity downloadedFile = fileService.downloadFile(FILE_NAME, OWNER);
        assertEquals(fileEntity, downloadedFile);
    }

    @Test
    void downloadFile_fileNotFound() {
        when(fileRepository.findByFileNameAndOwner(FILE_NAME, OWNER)).thenReturn(null);
        assertThrows(FileNotFoundException.class, () -> fileService.downloadFile(FILE_NAME, OWNER));
    }

    @Test
    void renameFile_success() {
        when(fileRepository.findByFileNameAndOwner(FILE_NAME, OWNER)).thenReturn(fileEntity);
        fileService.renameFile(OWNER, FILE_NAME, NEW_FILE_NAME);
        assertEquals(NEW_FILE_NAME, fileEntity.getFileName());
        verify(fileRepository).save(fileEntity);
    }

    @Test
    void renameFile_fileNotFound() {
        when(fileRepository.findByFileNameAndOwner(FILE_NAME, OWNER)).thenReturn(null);
        assertThrows(FileNotFoundException.class, () -> fileService.renameFile(OWNER, FILE_NAME, NEW_FILE_NAME));
    }

    @Test
    void getFiles_success() {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setOwner(OWNER);
        fileEntity.setFileName(FILE_NAME);
        fileEntity.setData("Hello World".getBytes());

        when(fileRepository.findAllByOwner(OWNER)).thenReturn(Collections.singletonList(fileEntity));

        List<FileEntity> files = fileService.getFiles(OWNER, 10);
        assertEquals(1, files.size());
        assertEquals(FILE_NAME, files.get(0).getFileName());
    }

    @Test
    void getFiles_emptyList() {
        when(fileRepository.findAllByOwner(OWNER)).thenReturn(Collections.emptyList());

        List<FileEntity> files = fileService.getFiles(OWNER, 10);
        assertTrue(files.isEmpty());
    }

    @Test
    void getFiles_gettingFileListException() {
        when(fileRepository.findAllByOwner(anyString()))
                .thenThrow(new GettingFileListException("Ошибка при получении списка файлов"));
        assertThrows(GettingFileListException.class, () -> fileService.getFiles(OWNER, 10));
        verify(fileRepository).findAllByOwner(OWNER);
    }
}


