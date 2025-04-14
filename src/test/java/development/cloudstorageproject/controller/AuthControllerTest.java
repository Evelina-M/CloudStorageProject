package development.cloudstorageproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import development.cloudstorageproject.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final String LOGIN_PATH = "/login";
    private final String LOGOUT_PATH = "/logout";
    private final String LOGIN = "Nikola";
    private final String BAD_LOGIN = "login";
    private final String PASSWORD = "user3";
    private final String validToken = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJOaWtvbGEiLCJpYXQiOjE3ND" +
            "MwOTUwMzgsImV4cCI6MTc0MzEwMzY3OH0.97ZaEnTxqkP6k7pW2WUJBUg3koQnQcgwnlhBUYJtdRKVIFfIK1kgkvxt3wWXVbq2";

    @Test
    void loginSuccess() throws Exception {
        UserDto loginRequest = new UserDto(LOGIN, PASSWORD);
        mockMvc.perform(post(LOGIN_PATH)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void loginBadCredentials() throws Exception {
        UserDto loginRequest = new UserDto(BAD_LOGIN, PASSWORD);
        mockMvc.perform(post(LOGIN_PATH)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logoutSuccess() throws Exception {
        mockMvc.perform(post(LOGOUT_PATH)
                        .header("auth-token", validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(redirectedUrl("/login?logout"))
                .andExpect(status().is3xxRedirection());
    }
}
