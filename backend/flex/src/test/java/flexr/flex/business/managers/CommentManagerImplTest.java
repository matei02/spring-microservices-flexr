package flexr.flex.business.managers;

import flexr.flex.business.converters.CommentDtoConverter;
import flexr.flex.business.converters.CommentEntityConverter;
import flexr.flex.domain.classes.Comment;
import flexr.flex.domain.dto.CommentPostRequest;
import flexr.flex.domain.dto.CommentResponse;
import flexr.flex.persistence.entities.CommentEntity;
import flexr.flex.persistence.entities.PostEntity;
import flexr.flex.persistence.interfaces.CommentRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentManagerImplTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentManagerImpl commentManager;

    private CommentPostRequest testCommentPostRequest;
    private Comment testCommentDomain;
    private CommentEntity testCommentEntity;
    private CommentResponse testCommentResponse;
    private PostEntity testPostEntity;

    @BeforeEach
    void setUp() {
        testCommentPostRequest = new CommentPostRequest();
        testCommentPostRequest.setText("Test Comment Text");
        testCommentPostRequest.setAuthorId("author123");
        testCommentPostRequest.setPostId(1L);

        testCommentDomain = new Comment();
        testCommentDomain.setId(1L);
        testCommentDomain.setText("Test Comment Text");
        testCommentDomain.setAuthorId("author123");
        testCommentDomain.setPostId(1L);
        testCommentDomain.setCreatedAt(Instant.now());

        testCommentEntity = new CommentEntity();
        testCommentEntity.setId(1L);
        testCommentEntity.setText("Test Comment Text");
        testCommentEntity.setAuthorId("author123");
        testCommentEntity.setCreatedAt(Instant.now());

        testCommentResponse = new CommentResponse();
        testCommentResponse.setId(1L);
        testCommentResponse.setText("Test Comment Text");
        testCommentResponse.setAuthorId("author123");
        testCommentResponse.setPostId(1L);
        testCommentResponse.setCreatedAt(Instant.now());

        testPostEntity = new PostEntity();
        testPostEntity.setId(1L);
    }

    @Test
    void createComment_shouldCreateCommentSuccessfully() {
        try (MockedStatic<CommentDtoConverter> mockedDtoConverter = Mockito.mockStatic(CommentDtoConverter.class);
             MockedStatic<CommentEntityConverter> mockedEntityConverter = Mockito.mockStatic(CommentEntityConverter.class)) {

            // Given
            mockedDtoConverter.when(() -> CommentDtoConverter.toDomain(testCommentPostRequest))
                    .thenReturn(testCommentDomain);
            when(postRepository.findById(testCommentDomain.getPostId()))
                    .thenReturn(Optional.of(testPostEntity));
            mockedEntityConverter.when(() -> CommentEntityConverter.toEntity(any(Comment.class), any(PostEntity.class)))
                    .thenReturn(testCommentEntity);
            when(commentRepository.save(testCommentEntity))
                    .thenReturn(testCommentEntity);
            mockedEntityConverter.when(() -> CommentEntityConverter.toDomain(testCommentEntity))
                    .thenReturn(testCommentDomain);
            mockedDtoConverter.when(() -> CommentDtoConverter.toResponse(testCommentDomain))
                    .thenReturn(testCommentResponse);

            // When
            CommentResponse result = commentManager.createComment(testCommentPostRequest);

            // Then
            assertThat(result).isEqualTo(testCommentResponse);
            mockedDtoConverter.verify(() -> CommentDtoConverter.toDomain(testCommentPostRequest), times(1));
            verify(postRepository, times(1)).findById(testCommentDomain.getPostId());
            mockedEntityConverter.verify(() -> CommentEntityConverter.toEntity(any(Comment.class), eq(testPostEntity)), times(1));
            verify(commentRepository, times(1)).save(testCommentEntity);
            mockedEntityConverter.verify(() -> CommentEntityConverter.toDomain(testCommentEntity), times(1));
            mockedDtoConverter.verify(() -> CommentDtoConverter.toResponse(testCommentDomain), times(1));
        }
    }

    @Test
    void createComment_shouldThrowNotFoundException_whenPostDoesNotExist() {
        try (MockedStatic<CommentDtoConverter> mockedDtoConverter = Mockito.mockStatic(CommentDtoConverter.class)) {
            // Given
            mockedDtoConverter.when(() -> CommentDtoConverter.toDomain(testCommentPostRequest))
                    .thenReturn(testCommentDomain);
            when(postRepository.findById(testCommentDomain.getPostId()))
                    .thenReturn(Optional.empty());

            // When / Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    commentManager.createComment(testCommentPostRequest)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(exception.getReason()).isEqualTo("Post not found");
            mockedDtoConverter.verify(() -> CommentDtoConverter.toDomain(testCommentPostRequest), times(1));
            verify(postRepository, times(1)).findById(testCommentDomain.getPostId());
            verifyNoInteractions(commentRepository);
        }
    }

    @Test
    void getCommentById_shouldReturnComment_whenCommentExists() {
        try (MockedStatic<CommentEntityConverter> mockedEntityConverter = Mockito.mockStatic(CommentEntityConverter.class);
             MockedStatic<CommentDtoConverter> mockedDtoConverter = Mockito.mockStatic(CommentDtoConverter.class)) {

            // Given
            when(commentRepository.findById(testCommentEntity.getId()))
                    .thenReturn(Optional.of(testCommentEntity));
            mockedEntityConverter.when(() -> CommentEntityConverter.toDomain(testCommentEntity))
                    .thenReturn(testCommentDomain);
            mockedDtoConverter.when(() -> CommentDtoConverter.toResponse(testCommentDomain))
                    .thenReturn(testCommentResponse);

            // When
            CommentResponse result = commentManager.getCommentById(testCommentEntity.getId());

            // Then
            assertThat(result).isEqualTo(testCommentResponse);
            verify(commentRepository, times(1)).findById(testCommentEntity.getId());
            mockedEntityConverter.verify(() -> CommentEntityConverter.toDomain(testCommentEntity), times(1));
            mockedDtoConverter.verify(() -> CommentDtoConverter.toResponse(testCommentDomain), times(1));
        }
    }

    @Test
    void getCommentById_shouldThrowNotFoundException_whenCommentDoesNotExist() {
        // Given
        Long commentId = 99L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // When / Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                commentManager.getCommentById(commentId)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Comment not found");
        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void getAllComments_shouldReturnEmptyList_whenNoCommentsExist() {
        try (MockedStatic<CommentEntityConverter> mockedEntityConverter = Mockito.mockStatic(CommentEntityConverter.class);
             MockedStatic<CommentDtoConverter> mockedDtoConverter = Mockito.mockStatic(CommentDtoConverter.class)) {
            // Given
            when(commentRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<CommentResponse> result = commentManager.getAllComments();

            // Then
            assertThat(result).isNotNull().isEmpty();
            verify(commentRepository, times(1)).findAll();
        }
    }

    @Test
    void getAllComments_shouldReturnListOfComments_whenCommentsExist() {
        try (MockedStatic<CommentEntityConverter> mockedEntityConverter = Mockito.mockStatic(CommentEntityConverter.class);
             MockedStatic<CommentDtoConverter> mockedDtoConverter = Mockito.mockStatic(CommentDtoConverter.class)) {
            // Given
            List<CommentEntity> commentEntities = Arrays.asList(testCommentEntity, new CommentEntity());
            when(commentRepository.findAll()).thenReturn(commentEntities);
            mockedEntityConverter.when(() -> CommentEntityConverter.toDomain(any(CommentEntity.class)))
                    .thenReturn(testCommentDomain);
            mockedDtoConverter.when(() -> CommentDtoConverter.toResponse(any(Comment.class)))
                    .thenReturn(testCommentResponse);

            // When
            List<CommentResponse> result = commentManager.getAllComments();

            // Then
            assertThat(result).isNotNull().hasSize(2);
            assertThat(result.get(0)).isEqualTo(testCommentResponse);
            verify(commentRepository, times(1)).findAll();
            mockedEntityConverter.verify(() -> CommentEntityConverter.toDomain(any(CommentEntity.class)), times(2));
            mockedDtoConverter.verify(() -> CommentDtoConverter.toResponse(any(Comment.class)), times(2));
        }
    }

    @Test
    void updateComment_shouldUpdateCommentSuccessfully() {
        try (MockedStatic<CommentEntityConverter> mockedEntityConverter = Mockito.mockStatic(CommentEntityConverter.class);
             MockedStatic<CommentDtoConverter> mockedDtoConverter = Mockito.mockStatic(CommentDtoConverter.class)) {

            // Given
            Long commentId = testCommentEntity.getId();
            CommentPostRequest updateRequest = new CommentPostRequest();
            updateRequest.setText("Updated Text");
            updateRequest.setAuthorId("updatedAuthor");

            CommentEntity updatedCommentEntity = new CommentEntity();
            updatedCommentEntity.setId(commentId);
            updatedCommentEntity.setText(updateRequest.getText());
            updatedCommentEntity.setAuthorId(updateRequest.getAuthorId());
            updatedCommentEntity.setCreatedAt(testCommentEntity.getCreatedAt()); // Preserve original creation time

            Comment updatedCommentDomain = new Comment();
            updatedCommentDomain.setId(commentId);
            updatedCommentDomain.setText(updateRequest.getText());
            updatedCommentDomain.setAuthorId(updateRequest.getAuthorId());
            updatedCommentDomain.setCreatedAt(testCommentDomain.getCreatedAt());

            CommentResponse updatedCommentResponse = new CommentResponse();
            updatedCommentResponse.setId(commentId);
            updatedCommentResponse.setText(updateRequest.getText());
            updatedCommentResponse.setAuthorId(updateRequest.getAuthorId());
            updatedCommentResponse.setCreatedAt(testCommentResponse.getCreatedAt());

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(testCommentEntity));
            when(commentRepository.save(any(CommentEntity.class))).thenReturn(updatedCommentEntity);
            mockedEntityConverter.when(() -> CommentEntityConverter.toDomain(updatedCommentEntity))
                    .thenReturn(updatedCommentDomain);
            mockedDtoConverter.when(() -> CommentDtoConverter.toResponse(updatedCommentDomain))
                    .thenReturn(updatedCommentResponse);

            // When
            CommentResponse result = commentManager.updateComment(commentId, updateRequest);

            // Then
            assertThat(result).isEqualTo(updatedCommentResponse);
            verify(commentRepository, times(1)).findById(commentId);
            verify(commentRepository, times(1)).save(testCommentEntity);
            assertThat(testCommentEntity.getText()).isEqualTo(updateRequest.getText()); // Verify entity was updated
            assertThat(testCommentEntity.getAuthorId()).isEqualTo(updateRequest.getAuthorId());
            mockedEntityConverter.verify(() -> CommentEntityConverter.toDomain(updatedCommentEntity), times(1));
            mockedDtoConverter.verify(() -> CommentDtoConverter.toResponse(updatedCommentDomain), times(1));
        }
    }

    @Test
    void updateComment_shouldThrowNotFoundException_whenCommentDoesNotExist() {
        // Given
        Long commentId = 99L;
        CommentPostRequest updateRequest = new CommentPostRequest();
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // When / Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                commentManager.updateComment(commentId, updateRequest)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Comment not found");
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).save(any(CommentEntity.class));
    }

    @Test
    void deleteComment_shouldDeleteCommentSuccessfully() {
        // Given
        Long commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(true);

        // When
        commentManager.deleteComment(commentId);

        // Then
        verify(commentRepository, times(1)).existsById(commentId);
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void deleteComment_shouldThrowNotFoundException_whenCommentDoesNotExist() {
        // Given
        Long commentId = 99L;
        when(commentRepository.existsById(commentId)).thenReturn(false);

        // When / Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                commentManager.deleteComment(commentId)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Comment not found");
        verify(commentRepository, times(1)).existsById(commentId);
        verify(commentRepository, never()).deleteById(any(Long.class));
    }
} 