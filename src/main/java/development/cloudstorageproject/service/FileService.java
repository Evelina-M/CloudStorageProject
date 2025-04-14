package development.cloudstorageproject.service;

import development.cloudstorageproject.entity.FileEntity;
import development.cloudstorageproject.exception.FileNotFoundException;
import development.cloudstorageproject.exception.GettingFileListException;
import development.cloudstorageproject.exception.InvalidInputDataException;
import development.cloudstorageproject.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    public void uploadFile(String owner, String fileName, MultipartFile file) throws IOException {
        checkingFileName(fileName);
        checkingFileName(String.valueOf(file));

        byte[] bytes = file.getBytes();
        FileEntity uploadFile = new FileEntity();
        uploadFile.setOwner(owner);
        uploadFile.setFileName(fileName);
        uploadFile.setData(bytes);

        fileRepository.save(uploadFile);
    }

    public void deleteFile(String fileName, String owner) {
        FileEntity file = fileRepository.findByFileNameAndOwner(fileName, owner);
        checkingFileName(fileName);
        fileAvailability(owner, fileName);
        fileRepository.delete(file);
    }

    public FileEntity downloadFile(String fileName, String owner) {
        checkingFileName(fileName);
        fileAvailability(owner, fileName);
        return fileRepository.findByFileNameAndOwner(fileName, owner);
    }

    public void renameFile(String owner, String fileName, String fileNameRequest) {
        checkingFileName(fileName);
        checkingFileName(fileNameRequest);

        FileEntity file = fileRepository.findByFileNameAndOwner(fileName, owner);
        fileAvailability(owner, fileName);
        file.setFileName(fileNameRequest);
        fileRepository.save(file);
    }

    private void fileAvailability(String owner, String fileName) {
        FileEntity file = fileRepository.findByFileNameAndOwner(fileName, owner);
        if (file == null) {
            throw new FileNotFoundException("Файл с именем '" + fileName + "' не найден для владельца '" + owner + "'");
        }
    }

    private void checkingFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new InvalidInputDataException("Необходимо указать имя файла");
        }
    }

    public List<FileEntity> getFiles(String owner, int limit) {
        try {
            List<FileEntity> fileList = fileRepository.findAllByOwner(owner);
            return fileList.stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (RuntimeException ex) {
            throw new GettingFileListException("Ошибка при получении списка файлов");
        }
    }
}
