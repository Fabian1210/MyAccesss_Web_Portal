package com.myaccess.myaccesswebportal.controller;

import com.myaccess.myaccesswebportal.repository.UserRepository;
import com.myaccess.myaccesswebportal.repository.DepartmentRepository;
import com.myaccess.myaccesswebportal.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeController.class)
@AutoConfigureMockMvc(addFilters = false) // disables Spring Security filters for this test
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock out constructor dependencies of HomeController
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DepartmentRepository departmentRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @Test
    @DisplayName("GET / should return the home view")
    void homeReturnsHomeView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }
}