package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ru.yandex.practicum.filmorate.dto.ErrorMessage;
import ru.yandex.practicum.filmorate.model.Film;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class FilmControllerTest {

    private static final String MOCK_FILM_NAME = "Eraserhead";
    private static final LocalDate MOCK_FILM_RELEASE_DATE = LocalDate.of(1977, 3, 19);
    private static final String MOCK_FILM_DESCRIPTION = "Eraserhead is a 1977 American "
        + "independent surrealist body horror film written, directed, produced, and edited by "
        + "David Lynch.";
    private static final int MOCK_FILM_DURATION = 89;
    @Autowired
    @SuppressWarnings("unused")
    private MockMvc mockMvc;
    @Autowired
    @SuppressWarnings("unused")
    private ObjectMapper mapper;

    @Test
    void shouldReturn200ForGetRequest() throws Exception {
        log.info("Testing: shouldReturn200ForGetRequest");
        Film film = new Film();
        film.setName(MOCK_FILM_NAME);
        film.setReleaseDate(MOCK_FILM_RELEASE_DATE);
        film.setDescription(MOCK_FILM_DESCRIPTION);
        film.setDuration(MOCK_FILM_DURATION);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/films").content(mapper.writeValueAsString(film))
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/films"))
            .andExpect(status().isOk()).andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        List<Film> actualResponse = mapper.readValue(jsonResponse, new TypeReference<>() {
        });
        assertEquals(MOCK_FILM_NAME, actualResponse.getLast().getName());
        assertEquals(MOCK_FILM_RELEASE_DATE, actualResponse.getLast().getReleaseDate());
        assertEquals(MOCK_FILM_DESCRIPTION, actualResponse.getLast().getDescription());
        assertEquals(MOCK_FILM_DURATION, actualResponse.getLast().getDuration());
    }

    @Test
    void shouldReturn400ForInvalidPostRequest() throws Exception {
        log.info("Testing: shouldReturn400ForInvalidPostRequest");
        Film film = new Film();
        film.setName("");
        film.setReleaseDate(MOCK_FILM_RELEASE_DATE);
        film.setDescription(MOCK_FILM_DESCRIPTION);
        film.setDuration(MOCK_FILM_DURATION);
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/films").content(mapper.writeValueAsString(film))
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ErrorMessage actualResponse = mapper.readValue(jsonResponse, ErrorMessage.class);
        assertEquals("Error when creating new film", actualResponse.getMessage());
        assertEquals("Invalid Film data: Film name must not be blank.",
            actualResponse.getDescription());
    }

    @Test
    void shouldReturn201ForValidPostRequest() throws Exception {
        log.info("Testing: shouldReturn201ForValidPostRequest");
        Film film = new Film();
        film.setName(MOCK_FILM_NAME);
        film.setReleaseDate(MOCK_FILM_RELEASE_DATE);
        film.setDescription(MOCK_FILM_DESCRIPTION);
        film.setDuration(MOCK_FILM_DURATION);
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/films").content(mapper.writeValueAsString(film))
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Film actualResponse = mapper.readValue(jsonResponse, Film.class);
        assertEquals(MOCK_FILM_NAME, actualResponse.getName());
        assertEquals(MOCK_FILM_RELEASE_DATE, actualResponse.getReleaseDate());
        assertEquals(MOCK_FILM_DESCRIPTION, actualResponse.getDescription());
        assertEquals(MOCK_FILM_DURATION, actualResponse.getDuration());
    }

    @Test
    void shouldReturn404ForPutRequestIfObjectDoesNotExist() throws Exception {
        log.info("Testing: shouldReturn404ForPutRequestIfObjectDoesNotExist");
        Film film = new Film();
        film.setName(MOCK_FILM_NAME);
        film.setReleaseDate(MOCK_FILM_RELEASE_DATE);
        film.setDescription(MOCK_FILM_DESCRIPTION);
        film.setDuration(MOCK_FILM_DURATION);
        film.setId(-42L);
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.put("/films").content(mapper.writeValueAsString(film))
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
            .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ErrorMessage actualResponse = mapper.readValue(jsonResponse, ErrorMessage.class);
        assertEquals("Error when updating film", actualResponse.getMessage());
        assertEquals("Film with ID -42 not found", actualResponse.getDescription());
    }

    @Test
    void shouldReturn400ForPutRequestIfObjectExistsButDataIsInvalid() throws Exception {
        log.info("Testing: shouldReturn400ForPutRequestIfObjectExistsButDataIsInvalid");
        Film film = new Film();
        film.setName(MOCK_FILM_NAME);
        film.setReleaseDate(MOCK_FILM_RELEASE_DATE);
        film.setDescription(MOCK_FILM_DESCRIPTION);
        film.setDuration(MOCK_FILM_DURATION);
        MvcResult resultPost = mockMvc.perform(
                MockMvcRequestBuilders.post("/films").content(mapper.writeValueAsString(film))
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andReturn();
        String jsonResponse = resultPost.getResponse().getContentAsString();
        Film createdFilm = mapper.readValue(jsonResponse, Film.class);
        Film newFilm = new Film();
        newFilm.setName("");
        newFilm.setDuration(MOCK_FILM_DURATION);
        newFilm.setId(createdFilm.getId());
        MvcResult resultPut = mockMvc.perform(
                MockMvcRequestBuilders.put("/films").content(mapper.writeValueAsString(newFilm))
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
            .andReturn();
        jsonResponse = resultPut.getResponse().getContentAsString();
        ErrorMessage actualResponse = mapper.readValue(jsonResponse, ErrorMessage.class);
        assertEquals("Error when updating film", actualResponse.getMessage());
        assertEquals("Invalid Film data: Film name must not be blank.",
            actualResponse.getDescription());
    }

    @Test
    void shouldReturn200ForValidPutRequest() throws Exception {
        log.info("Testing: shouldReturn200ForValidPutRequest");
        Film film = new Film();
        film.setName(MOCK_FILM_NAME);
        film.setReleaseDate(MOCK_FILM_RELEASE_DATE);
        film.setDescription(MOCK_FILM_DESCRIPTION);
        film.setDuration(MOCK_FILM_DURATION);
        MvcResult resultPost = mockMvc.perform(
                MockMvcRequestBuilders.post("/films").content(mapper.writeValueAsString(film))
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andReturn();

        String jsonResponsePost = resultPost.getResponse().getContentAsString();
        Film createdFilm = mapper.readValue(jsonResponsePost, Film.class);
        Film newFilm = new Film();
        newFilm.setName("new name");
        newFilm.setReleaseDate(LocalDate.of(2001, 9, 11));
        newFilm.setDescription("new description");
        newFilm.setDuration(42);
        newFilm.setId(createdFilm.getId());
        MvcResult resultPut = mockMvc.perform(
                MockMvcRequestBuilders.put("/films").content(mapper.writeValueAsString(newFilm))
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();
        String jsonResponsePut = resultPut.getResponse().getContentAsString();
        Film updatedFilm = mapper.readValue(jsonResponsePut, Film.class);
        assertEquals(newFilm.getName(), updatedFilm.getName());
        assertEquals(newFilm.getDuration(), updatedFilm.getDuration());
        assertEquals(newFilm.getDescription(), updatedFilm.getDescription());
        assertEquals(newFilm.getReleaseDate(), updatedFilm.getReleaseDate());
    }

    @Test
    void shouldDeleteFilm() throws Exception {
        Film film = new Film();
        film.setName(MOCK_FILM_NAME);
        film.setReleaseDate(MOCK_FILM_RELEASE_DATE);
        film.setDescription(MOCK_FILM_DESCRIPTION);
        film.setDuration(MOCK_FILM_DURATION);
        MvcResult resultPost = mockMvc.perform(
                post("/films").content(mapper.writeValueAsString(film))
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
            .andReturn();
        String jsonResponse = resultPost.getResponse().getContentAsString();
        Film createdFilm = mapper.readValue(jsonResponse, Film.class);

        mockMvc.perform(delete("/films/" + createdFilm.getId())).andExpect(status().isNoContent());

        mockMvc.perform(get("/films/" + createdFilm.getId())).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForDeleteRequestIfObjectDoesNotExist() throws Exception {
        mockMvc.perform(delete("/films/999999")).andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("Error when deleting film"))
            .andExpect(jsonPath("$.description").value("Film with ID 999999 not found"));
    }
}