package ru.faust.cloudstorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.faust.cloudstorage.config.WebSecurityConfig;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SignInTest {

    @Autowired
    private MockMvc mvc;


    @WithMockUser(username = "spring", roles = "USER")
    @Test
    public void givenAuthRequest_shouldSucceedWith200() throws Exception {
        mvc.perform(get("/home").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenAuthRequest_shouldRedirect() throws Exception {
        mvc.perform(get("/home").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    // TODO: at the end add correct endpoints to security config and uncomment this test!
//    @Test
//    public void givenInvalidEndpoint_shouldReturn404() throws Exception {
//        mvc.perform(getFile("/invalid-endpoint").contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
}
