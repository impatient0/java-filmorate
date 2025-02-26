package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.dto.ErrorMessage;
import ru.yandex.practicum.filmorate.model.User;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class UserControllerTest {

    private static final String MOCK_USER_EMAIL = "john.doe@example.com";
    private static final String MOCK_USER_LOGIN = "john_doe";
    private static final String MOCK_USER_NAME = "John Doe";
    private static final LocalDate MOCK_USER_BIRTHDAY = LocalDate.of(1990, 5, 15);

    @Autowired
    @SuppressWarnings("unused")
    private MockMvc mockMvc;
    @Autowired
    @SuppressWarnings("unused")
    private ObjectMapper mapper;


    @Test
    void shouldReturn200ForGetRequest() throws Exception {
        log.info("Testing: shouldReturn200ForGetRequest");
        User user = new User();
        user.setEmail(MOCK_USER_EMAIL);
        user.setLogin(MOCK_USER_LOGIN);
        user.setName(MOCK_USER_NAME);
        user.setBirthday(MOCK_USER_BIRTHDAY);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                    .content(mapper.writeValueAsString(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users"))
            .andExpect(status().isOk()).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        List<User> actualResponse = mapper.readValue(jsonResponse, new TypeReference<>() {
        });
        assertEquals(MOCK_USER_NAME, actualResponse.getLast().getName());
        assertEquals(MOCK_USER_LOGIN, actualResponse.getLast().getLogin());
        assertEquals(MOCK_USER_BIRTHDAY, actualResponse.getLast().getBirthday());
        assertEquals(MOCK_USER_EMAIL, actualResponse.getLast().getEmail());

    }

    @Test
    void shouldReturn400ForInvalidPostRequest() throws Exception {
        log.info("Testing: shouldReturn400ForInvalidPostRequest");
        User user = new User();
        user.setEmail("invalid");
        user.setLogin(MOCK_USER_LOGIN);
        user.setName(MOCK_USER_NAME);
        user.setBirthday(MOCK_USER_BIRTHDAY);
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                    .content(mapper.writeValueAsString(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ErrorMessage actualResponse = mapper.readValue(jsonResponse, ErrorMessage.class);
        assertEquals("Error when creating new user", actualResponse.getMessage());
        assertEquals("Invalid user data: E-mail must be valid.", actualResponse.getDescription());
    }

    @Test
    void shouldReturn201ForValidPostRequest() throws Exception {
        log.info("Testing: shouldReturn201ForValidPostRequest");
        User user = new User();
        user.setEmail(MOCK_USER_EMAIL);
        user.setLogin(MOCK_USER_LOGIN);
        user.setName(MOCK_USER_NAME);
        user.setBirthday(MOCK_USER_BIRTHDAY);
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                    .content(mapper.writeValueAsString(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        User actualResponse = mapper.readValue(jsonResponse, User.class);
        assertEquals(MOCK_USER_NAME, actualResponse.getName());
        assertEquals(MOCK_USER_LOGIN, actualResponse.getLogin());
        assertEquals(MOCK_USER_BIRTHDAY, actualResponse.getBirthday());
        assertEquals(MOCK_USER_EMAIL, actualResponse.getEmail());
    }

    @Test
    void shouldReturn201ForValidPostRequestWithNoName() throws Exception {
        log.info("Testing: shouldReturn201ForValidPostRequestWithNoName");
        User user = new User();
        user.setEmail(MOCK_USER_EMAIL);
        user.setLogin(MOCK_USER_LOGIN);
        user.setBirthday(MOCK_USER_BIRTHDAY);
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                    .content(mapper.writeValueAsString(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        User actualResponse = mapper.readValue(jsonResponse, User.class);
        assertEquals(MOCK_USER_LOGIN, actualResponse.getName());
        assertEquals(MOCK_USER_LOGIN, actualResponse.getLogin());
        assertEquals(MOCK_USER_BIRTHDAY, actualResponse.getBirthday());
        assertEquals(MOCK_USER_EMAIL, actualResponse.getEmail());
    }


    @Test
    void shouldReturn404ForPutRequestIfObjectDoesNotExist() throws Exception {
        log.info("Testing: shouldReturn404ForPutRequestIfObjectDoesNotExist");
        User user = new User();
        user.setEmail(MOCK_USER_EMAIL);
        user.setLogin(MOCK_USER_LOGIN);
        user.setName(MOCK_USER_NAME);
        user.setBirthday(MOCK_USER_BIRTHDAY);
        user.setId(-42L);
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.put("/users")
                    .content(mapper.writeValueAsString(user))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ErrorMessage actualResponse = mapper.readValue(jsonResponse, ErrorMessage.class);
        assertEquals("Error when updating user", actualResponse.getMessage());
        assertEquals("User with ID -42 not found", actualResponse.getDescription());
    }

    @Test
    void shouldReturn400ForPutRequestIfObjectExistsButDataIsInvalid() throws Exception {
        log.info("Testing: shouldReturn400ForPutRequestIfObjectExistsButDataIsInvalid");
        User user = new User();
        user.setEmail(MOCK_USER_EMAIL);
        user.setLogin(MOCK_USER_LOGIN);
        user.setName(MOCK_USER_NAME);
        user.setBirthday(MOCK_USER_BIRTHDAY);
        MvcResult resultPost = mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                    .content(mapper.writeValueAsString(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn();

        String jsonResponsePost = resultPost.getResponse().getContentAsString();
        User createdUser = mapper.readValue(jsonResponsePost, User.class);
        User newUser = new User();
        newUser.setEmail("invalid");
        newUser.setId(createdUser.getId());
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.put("/users")
                    .content(mapper.writeValueAsString(newUser))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest()).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        ErrorMessage actualResponse = mapper.readValue(jsonResponse, ErrorMessage.class);
        assertEquals("Error when updating user", actualResponse.getMessage());
        assertEquals("Invalid user data: E-mail must be valid.", actualResponse.getDescription());
    }

    @Test
    void shouldReturn200ForValidPutRequest() throws Exception {
        log.info("Testing: shouldReturn200ForValidPutRequest");
        User user = new User();
        user.setEmail(MOCK_USER_EMAIL);
        user.setLogin(MOCK_USER_LOGIN);
        user.setName(MOCK_USER_NAME);
        user.setBirthday(MOCK_USER_BIRTHDAY);

        MvcResult resultPost = mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                    .content(mapper.writeValueAsString(user))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn();

        String jsonResponsePost = resultPost.getResponse().getContentAsString();
        User createdUser = mapper.readValue(jsonResponsePost, User.class);

        User newUser = new User();
        newUser.setEmail("new.email@example.com");
        newUser.setLogin("new_login");
        newUser.setName("new name");
        newUser.setBirthday(LocalDate.of(1999, 1, 1));
        newUser.setId(createdUser.getId());
        MvcResult resultPut = mockMvc.perform(
                MockMvcRequestBuilders.put("/users")
                    .content(mapper.writeValueAsString(newUser))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();
        String jsonResponsePut = resultPut.getResponse().getContentAsString();
        User actualResponse = mapper.readValue(jsonResponsePut, User.class);
        assertEquals(newUser.getEmail(), actualResponse.getEmail());
        assertEquals(newUser.getLogin(), actualResponse.getLogin());
        assertEquals(newUser.getName(), actualResponse.getName());
        assertEquals(newUser.getBirthday(), actualResponse.getBirthday());
    }
}