package development.cloudstorageproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    @JsonProperty("login")
    private String login;

    @JsonProperty("password")
    private String password;
}
