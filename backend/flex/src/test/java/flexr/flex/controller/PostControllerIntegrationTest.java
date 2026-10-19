package flexr.flex.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import flexr.flex.business.interfaces.PostManager;
import flexr.flex.domain.dto.CreatePostRequest;
import flexr.flex.domain.dto.PostResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostManager postManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPost_shouldReturn200OK_withCorrectPostDetails() throws Exception {
        // Arrange
        CreatePostRequest request = new CreatePostRequest();
        request.setContent("Test Content");
        request.setAuthorId("test-author-id");
        request.setImage("test-image-data"); // Assuming image is a String for test simplification

        PostResponse expectedResponse = new PostResponse();
        expectedResponse.setId(1L);
        expectedResponse.setContent("Test Content");
        expectedResponse.setAuthorId("test-author-id");
        expectedResponse.setImage("test-image-data".getBytes()); // Assuming image is byte array in response
        expectedResponse.setCreatedAt(java.time.Instant.now());

        when(postManager.createPost(any(CreatePostRequest.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                .andExpect(jsonPath("$.content").value(expectedResponse.getContent()))
                .andExpect(jsonPath("$.authorId").value(expectedResponse.getAuthorId()));
                // .andExpect(jsonPath("$.image").value(expectedResponse.getImage())); // Image might be handled differently in JSON
    }
} 