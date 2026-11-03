package com.wingprojkt.permitservice;

import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthorizeControllerTest {

    private AuthorizeController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        controller = new AuthorizeController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void adminCanDelete() {
        var res = controller.authorize(new AuthorizeController.AuthRequest("ADMIN", "DELETE"));
        assertTrue(res.allowed());
    }

    @Test
    void viewerCannotWrite() {
        var res = controller.authorize(new AuthorizeController.AuthRequest("VIEWER", "WRITE"));
        assertFalse(res.allowed());
    }

    @Test
    void editorCanWriteButNotDelete() {
        assertTrue(controller.authorize(new AuthorizeController.AuthRequest("EDITOR", "WRITE")).allowed());
        assertFalse(controller.authorize(new AuthorizeController.AuthRequest("EDITOR", "DELETE")).allowed());
    }

    @Test
    void unknownRoleIsDeniedByDefault() {
        var res = controller.authorize(new AuthorizeController.AuthRequest("HACKER", "READ"));
        assertFalse(res.allowed());
    }

    @Test
    void missingFieldsAreDeniedNotCrashed() {
        var res = controller.authorize(new AuthorizeController.AuthRequest(null, null));
        assertFalse(res.allowed());
    }

    @Test
    void httpEndpointReturnsDenyForViewerWrite() throws Exception {
        mockMvc.perform(post("/authorize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"VIEWER\",\"action\":\"WRITE\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.allowed").value(false))
            .andExpect(jsonPath("$.reason").value("denied by default"));
    }
}