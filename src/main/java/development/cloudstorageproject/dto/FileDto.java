package development.cloudstorageproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class FileDto {
    private String fileName;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FileDto fileDto = (FileDto) o;
        return Objects.equals(fileName, fileDto.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fileName);
    }
}