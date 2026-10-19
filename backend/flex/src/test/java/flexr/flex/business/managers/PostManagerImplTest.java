package flexr.flex.business.managers;

import flexr.flex.business.converters.PostDtoConverter;
import flexr.flex.business.converters.PostEntityConverter;
import flexr.flex.domain.classes.Post;
import flexr.flex.domain.dto.CreatePostRequest;
import flexr.flex.domain.dto.PostResponse;
import flexr.flex.persistence.entities.PostEntity;
import flexr.flex.persistence.interfaces.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostManagerImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostManagerImpl postManager;

    private CreatePostRequest testCreatePostRequest;
    private Post testPostDomain;
    private PostEntity testPostEntity;
    private PostResponse testPostResponse;
    private byte[] testImageBytes;
    private String testImageBase64;

    @BeforeEach
    void setUp() {
        testImageBytes = new byte[]{1, 2, 3, 4};
        testImageBase64 = Base64.getEncoder().encodeToString(testImageBytes);

        testCreatePostRequest = new CreatePostRequest();
        testCreatePostRequest.setContent("Test Post Content");
        testCreatePostRequest.setAuthorId("author123");
        testCreatePostRequest.setImage(testImageBase64);

        testPostDomain = new Post();
        testPostDomain.setId(1L);
        testPostDomain.setContent("Test Post Content");
        testPostDomain.setAuthorId("author123");
        testPostDomain.setImage(testImageBytes);
        testPostDomain.setCreatedAt(Instant.now());

        testPostEntity = new PostEntity();
        testPostEntity.setId(1L);
        testPostEntity.setContent("Test Post Content");
        testPostEntity.setAuthorId("author123");
        testPostEntity.setImage(testImageBytes);
        testPostEntity.setCreatedAt(Instant.now());

        testPostResponse = new PostResponse();
        testPostResponse.setId(1L);
        testPostResponse.setContent("Test Post Content");
        testPostResponse.setAuthorId("author123");
        testPostResponse.setImage(testImageBytes);
        testPostResponse.setCreatedAt(Instant.now());
    }

    @Test
    void createPost_shouldCreatePostSuccessfully() {
        try (MockedStatic<PostDtoConverter> mockedDtoConverter = Mockito.mockStatic(PostDtoConverter.class);
             MockedStatic<PostEntityConverter> mockedEntityConverter = Mockito.mockStatic(PostEntityConverter.class)) {

            // Given
            mockedDtoConverter.when(() -> PostDtoConverter.toDomain(testCreatePostRequest))
                    .thenReturn(testPostDomain);
            mockedEntityConverter.when(() -> PostEntityConverter.toEntity(any(Post.class)))
                    .thenReturn(testPostEntity);
            when(postRepository.save(testPostEntity))
                    .thenReturn(testPostEntity);
            mockedEntityConverter.when(() -> PostEntityConverter.toDomain(testPostEntity))
                    .thenReturn(testPostDomain);
            mockedDtoConverter.when(() -> PostDtoConverter.toResponse(testPostDomain))
                    .thenReturn(testPostResponse);

            // When
            PostResponse result = postManager.createPost(testCreatePostRequest);

            // Then
            assertThat(result).isEqualTo(testPostResponse);
            mockedDtoConverter.verify(() -> PostDtoConverter.toDomain(testCreatePostRequest), times(1));
            verify(postRepository, times(1)).save(testPostEntity);
            mockedEntityConverter.verify(() -> PostEntityConverter.toEntity(any(Post.class)), times(1));
            mockedEntityConverter.verify(() -> PostEntityConverter.toDomain(testPostEntity), times(1));
            mockedDtoConverter.verify(() -> PostDtoConverter.toResponse(testPostDomain), times(1));
        }
    }

    @Test
    void getPostById_shouldReturnPost_whenPostExists() {
        try (MockedStatic<PostEntityConverter> mockedEntityConverter = Mockito.mockStatic(PostEntityConverter.class);
             MockedStatic<PostDtoConverter> mockedDtoConverter = Mockito.mockStatic(PostDtoConverter.class)) {

            // Given
            when(postRepository.findById(testPostEntity.getId()))
                    .thenReturn(Optional.of(testPostEntity));
            mockedEntityConverter.when(() -> PostEntityConverter.toDomain(testPostEntity))
                    .thenReturn(testPostDomain);
            mockedDtoConverter.when(() -> PostDtoConverter.toResponse(testPostDomain))
                    .thenReturn(testPostResponse);

            // When
            PostResponse result = postManager.getPostById(testPostEntity.getId());

            // Then
            assertThat(result).isEqualTo(testPostResponse);
            verify(postRepository, times(1)).findById(testPostEntity.getId());
            mockedEntityConverter.verify(() -> PostEntityConverter.toDomain(testPostEntity), times(1));
            mockedDtoConverter.verify(() -> PostDtoConverter.toResponse(testPostDomain), times(1));
        }
    }

    @Test
    void getPostById_shouldThrowNotFoundException_whenPostDoesNotExist() {
        // Given
        Long postId = 99L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When / Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                postManager.getPostById(postId)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Post not found");
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void getAllPosts_shouldReturnEmptyList_whenNoPostsExist() {
        try (MockedStatic<PostEntityConverter> mockedEntityConverter = Mockito.mockStatic(PostEntityConverter.class);
             MockedStatic<PostDtoConverter> mockedDtoConverter = Mockito.mockStatic(PostDtoConverter.class)) {
            // Given
            when(postRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<PostResponse> result = postManager.getAllPosts();

            // Then
            assertThat(result).isNotNull().isEmpty();
            verify(postRepository, times(1)).findAll();
        }
    }

    @Test
    void getAllPosts_shouldReturnListOfPosts_whenPostsExist() {
        try (MockedStatic<PostEntityConverter> mockedEntityConverter = Mockito.mockStatic(PostEntityConverter.class);
             MockedStatic<PostDtoConverter> mockedDtoConverter = Mockito.mockStatic(PostDtoConverter.class)) {
            // Given
            List<PostEntity> postEntities = Arrays.asList(testPostEntity, new PostEntity());
            when(postRepository.findAll()).thenReturn(postEntities);
            mockedEntityConverter.when(() -> PostEntityConverter.toDomain(any(PostEntity.class)))
                    .thenReturn(testPostDomain);
            mockedDtoConverter.when(() -> PostDtoConverter.toResponse(any(Post.class)))
                    .thenReturn(testPostResponse);

            // When
            List<PostResponse> result = postManager.getAllPosts();

            // Then
            assertThat(result).isNotNull().hasSize(2);
            assertThat(result.get(0)).isEqualTo(testPostResponse);
            verify(postRepository, times(1)).findAll();
            mockedEntityConverter.verify(() -> PostEntityConverter.toDomain(any(PostEntity.class)), times(2));
            mockedDtoConverter.verify(() -> PostDtoConverter.toResponse(any(Post.class)), times(2));
        }
    }

    @Test
    void updatePost_shouldUpdatePostSuccessfully() {
        try (MockedStatic<PostEntityConverter> mockedEntityConverter = Mockito.mockStatic(PostEntityConverter.class);
             MockedStatic<PostDtoConverter> mockedDtoConverter = Mockito.mockStatic(PostDtoConverter.class)) {

            // Given
            Long postId = testPostEntity.getId();
            CreatePostRequest updateRequest = new CreatePostRequest();
            updateRequest.setContent("Updated Content");
            updateRequest.setAuthorId("updatedAuthor");
            updateRequest.setImage(testImageBase64);

            PostEntity updatedPostEntity = new PostEntity();
            updatedPostEntity.setId(postId);
            updatedPostEntity.setContent(updateRequest.getContent());
            updatedPostEntity.setAuthorId(updateRequest.getAuthorId());
            updatedPostEntity.setImage(testImageBytes);
            updatedPostEntity.setCreatedAt(testPostEntity.getCreatedAt());

            Post updatedPostDomain = new Post();
            updatedPostDomain.setId(postId);
            updatedPostDomain.setContent(updateRequest.getContent());
            updatedPostDomain.setAuthorId(updateRequest.getAuthorId());
            updatedPostDomain.setImage(testImageBytes);
            updatedPostDomain.setCreatedAt(testPostDomain.getCreatedAt());

            PostResponse updatedPostResponse = new PostResponse();
            updatedPostResponse.setId(postId);
            updatedPostResponse.setContent(updateRequest.getContent());
            updatedPostResponse.setAuthorId(updateRequest.getAuthorId());
            updatedPostResponse.setImage(testImageBytes);
            updatedPostResponse.setCreatedAt(testPostResponse.getCreatedAt());

            when(postRepository.findById(postId)).thenReturn(Optional.of(testPostEntity));
            when(postRepository.save(any(PostEntity.class))).thenReturn(updatedPostEntity);
            mockedEntityConverter.when(() -> PostEntityConverter.toDomain(updatedPostEntity))
                    .thenReturn(updatedPostDomain);
            mockedDtoConverter.when(() -> PostDtoConverter.toResponse(updatedPostDomain))
                    .thenReturn(updatedPostResponse);

            // When
            PostResponse result = postManager.updatePost(postId, updateRequest);

            // Then
            assertThat(result).isEqualTo(updatedPostResponse);
            verify(postRepository, times(1)).findById(postId);
            verify(postRepository, times(1)).save(testPostEntity);
            assertThat(testPostEntity.getContent()).isEqualTo(updateRequest.getContent());
            assertThat(testPostEntity.getAuthorId()).isEqualTo(updateRequest.getAuthorId());
            assertThat(testPostEntity.getImage()).isEqualTo(testImageBytes);
            mockedEntityConverter.verify(() -> PostEntityConverter.toDomain(updatedPostEntity), times(1));
            mockedDtoConverter.verify(() -> PostDtoConverter.toResponse(updatedPostDomain), times(1));
        }
    }

    @Test
    void updatePost_shouldThrowNotFoundException_whenPostDoesNotExist() {
        // Given
        Long postId = 99L;
        CreatePostRequest updateRequest = new CreatePostRequest();
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When / Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                postManager.updatePost(postId, updateRequest)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Post not found");
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).save(any(PostEntity.class));
    }

    @Test
    void deletePost_shouldDeletePostSuccessfully() {
        // Given
        Long postId = 1L;
        when(postRepository.existsById(postId)).thenReturn(true);

        // When
        postManager.deletePost(postId);

        // Then
        verify(postRepository, times(1)).existsById(postId);
        verify(postRepository, times(1)).deleteById(postId);
    }

    @Test
    void deletePost_shouldThrowNotFoundException_whenPostDoesNotExist() {
        // Given
        Long postId = 99L;
        when(postRepository.existsById(postId)).thenReturn(false);

        // When / Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                postManager.deletePost(postId)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Post not found");
        verify(postRepository, times(1)).existsById(postId);
        verify(postRepository, never()).deleteById(any(Long.class));
    }
} 