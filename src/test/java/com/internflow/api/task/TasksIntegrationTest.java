package com.internflow.api.task;

import com.internflow.api.internship.InternshipRepository;
import com.internflow.api.internship.InternshipResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TasksIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void cleanDatabase() {
        taskRepository.deleteAll();
        internshipRepository.deleteAll();
    }

    @Test
    void createTasksShouldPersistTask() throws Exception {
        String requestBody = """
                        {
                          "title": "Write tests",
                          "description": "Add integration test for task creation"
                        }
                """;
        String requestBody1 = internshipRequestJson("Open Internship", "BMW", 6);
        String responseBody1 = internshipResponseBody(requestBody1);

        InternshipResponse created = objectMapper.readValue(responseBody1, InternshipResponse.class);

        mockMvc.perform(post("/internships/" + created.id() + "/tasks").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.internshipId").value(created.id()));

    }

    @Test
    void createTasksShouldReturnNotFoundWhenInternshipIsNotFound() throws Exception {
        String requestBody = """
                        {
                          "title": "Write tests",
                          "description": "Add integration test for task creation"
                        }
                """;

        mockMvc.perform(post("/internships/99/tasks").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTasksShouldReturnTasksToTheirInternship() throws Exception {
        String requestBody1 = internshipRequestJson("Open Internship", "BMW", 6);
        String responseBody1 = internshipResponseBody(requestBody1);

        InternshipResponse created = objectMapper.readValue(responseBody1, InternshipResponse.class);

        mockMvc.perform(get("/internships/" + created.id() + "/tasks").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getTasksShouldReturnNotFoundWhenInternshipIsNotFound() throws Exception {
        mockMvc.perform(get("/internships/99/tasks"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTaskCompletedShouldReturnUpdatedTask() throws Exception {
        String requestBody = """
                        {
                          "title": "Write tests",
                          "description": "Add integration test for task creation"
                        }
                """;

        String updatedBody = """
                            {
                                "completed": true
                            }
                            """;

        String responseBody1 = taskResponseBody(requestBody);

        TaskResponse created = objectMapper.readValue(responseBody1, TaskResponse.class);

        mockMvc.perform(patch("/tasks/" + created.id() + "/completed").contentType(MediaType.APPLICATION_JSON).content(updatedBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void updateTaskCompletedShouldReturnNotFoundWhenTaskIsNotFound() throws Exception {
        String updatedBody = """
                            {
                                "completed": true
                            }
                            """;

        mockMvc.perform(patch("/tasks/99/completed").contentType(MediaType.APPLICATION_JSON).content(updatedBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.errors.resource").exists());
    }

    private String internshipResponseBody(String requestBody) throws Exception {
        return mockMvc.perform(post("/internships").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    private String taskResponseBody(String requestBody) throws Exception {
        String requestBody1 = internshipRequestJson("Open Internship", "BMW", 6);
        String responseBody1 = internshipResponseBody(requestBody1);

        InternshipResponse created = objectMapper.readValue(responseBody1, InternshipResponse.class);

        return mockMvc.perform(post("/internships/" + created.id() + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString();

    }

    private String internshipRequestJson(
            String title,
            String company,
            Integer durationInMonths
    ) {
        return """
                {
                    "title": "%s",
                    "company": "%s",
                    "durationInMonths": %d
                }
                """.formatted(title, company, durationInMonths);
    }

}
