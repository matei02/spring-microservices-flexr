package flexr.flex.business.managers;

import flexr.flex.business.converters.LikeDtoConverter;
import flexr.flex.business.converters.LikeEntityConverter;
import flexr.flex.domain.classes.Like;
import flexr.flex.domain.dto.LikePostRequest;
import flexr.flex.domain.dto.LikeResponse;
import flexr.flex.persistence.entities.LikeEntity;
import flexr.flex.persistence.entities.PostEntity;
import flexr.flex.persistence.interfaces.LikeRepository;
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
class LikeManagerImplTest {

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private LikeManagerImpl likeManager;

    private LikePostRequest testLikePostRequest;
    private Like testLikeDomain;
    private LikeEntity testLikeEntity;
    private LikeResponse testLikeResponse;
    private PostEntity testPostEntity;

    @BeforeEach
    void setUp() {
        testLikePostRequest = new LikePostRequest();
        testLikePostRequest.setUserId("user123");
        testLikePostRequest.setPostId(1L);

        testLikeDomain = new Like();
        testLikeDomain.setId(1L);
        testLikeDomain.setUserId("user123");
        testLikeDomain.setPostId(1L);
        testLikeDomain.setCreatedAt(Instant.now());

        testLikeEntity = new LikeEntity();
        testLikeEntity.setId(1L);
        testLikeEntity.setUserId("user123");
        testLikeEntity.setCreatedAt(Instant.now());

        testLikeResponse = new LikeResponse();
        testLikeResponse.setId(1L);
        testLikeResponse.setUserId("user123");
        testLikeResponse.setPostId(1L);
        testLikeResponse.setCreatedAt(Instant.now());

        testPostEntity = new PostEntity();
        testPostEntity.setId(1L);
    }

    @Test
    void createLike_shouldCreateLikeSuccessfully() {
        try (MockedStatic<LikeDtoConverter> mockedDtoConverter = Mockito.mockStatic(LikeDtoConverter.class);
             MockedStatic<LikeEntityConverter> mockedEntityConverter = Mockito.mockStatic(LikeEntityConverter.class)) {

            // Given
            mockedDtoConverter.when(() -> LikeDtoConverter.toDomain(testLikePostRequest))
                    .thenReturn(testLikeDomain);
            when(postRepository.findById(testLikeDomain.getPostId()))
                    .thenReturn(Optional.of(testPostEntity));
            mockedEntityConverter.when(() -> LikeEntityConverter.toEntity(any(Like.class), any(PostEntity.class)))
                    .thenReturn(testLikeEntity);
            when(likeRepository.save(testLikeEntity))
                    .thenReturn(testLikeEntity);
            mockedEntityConverter.when(() -> LikeEntityConverter.toDomain(testLikeEntity))
                    .thenReturn(testLikeDomain);
            mockedDtoConverter.when(() -> LikeDtoConverter.toResponse(testLikeDomain))
                    .thenReturn(testLikeResponse);

            // When
            LikeResponse result = likeManager.createLike(testLikePostRequest);

            // Then
            assertThat(result).isEqualTo(testLikeResponse);
            mockedDtoConverter.verify(() -> LikeDtoConverter.toDomain(testLikePostRequest), times(1));
            verify(postRepository, times(1)).findById(testLikeDomain.getPostId());
            mockedEntityConverter.verify(() -> LikeEntityConverter.toEntity(any(Like.class), eq(testPostEntity)), times(1));
            verify(likeRepository, times(1)).save(testLikeEntity);
            mockedEntityConverter.verify(() -> LikeEntityConverter.toDomain(testLikeEntity), times(1));
            mockedDtoConverter.verify(() -> LikeDtoConverter.toResponse(testLikeDomain), times(1));
        }
    }

    @Test
    void createLike_shouldThrowNotFoundException_whenPostDoesNotExist() {
        try (MockedStatic<LikeDtoConverter> mockedDtoConverter = Mockito.mockStatic(LikeDtoConverter.class)) {
            // Given
            mockedDtoConverter.when(() -> LikeDtoConverter.toDomain(testLikePostRequest))
                    .thenReturn(testLikeDomain);
            when(postRepository.findById(testLikeDomain.getPostId()))
                    .thenReturn(Optional.empty());

            // When / Then
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    likeManager.createLike(testLikePostRequest)
            );

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(exception.getReason()).isEqualTo("Post not found");
            mockedDtoConverter.verify(() -> LikeDtoConverter.toDomain(testLikePostRequest), times(1));
            verify(postRepository, times(1)).findById(testLikeDomain.getPostId());
            verifyNoInteractions(likeRepository);
        }
    }

    @Test
    void getLikeById_shouldReturnLike_whenLikeExists() {
        try (MockedStatic<LikeEntityConverter> mockedEntityConverter = Mockito.mockStatic(LikeEntityConverter.class);
             MockedStatic<LikeDtoConverter> mockedDtoConverter = Mockito.mockStatic(LikeDtoConverter.class)) {

            // Given
            when(likeRepository.findById(testLikeEntity.getId()))
                    .thenReturn(Optional.of(testLikeEntity));
            mockedEntityConverter.when(() -> LikeEntityConverter.toDomain(testLikeEntity))
                    .thenReturn(testLikeDomain);
            mockedDtoConverter.when(() -> LikeDtoConverter.toResponse(testLikeDomain))
                    .thenReturn(testLikeResponse);

            // When
            LikeResponse result = likeManager.getLikeById(testLikeEntity.getId());

            // Then
            assertThat(result).isEqualTo(testLikeResponse);
            verify(likeRepository, times(1)).findById(testLikeEntity.getId());
            mockedEntityConverter.verify(() -> LikeEntityConverter.toDomain(testLikeEntity), times(1));
            mockedDtoConverter.verify(() -> LikeDtoConverter.toResponse(testLikeDomain), times(1));
        }
    }

    @Test
    void getLikeById_shouldThrowNotFoundException_whenLikeDoesNotExist() {
        // Given
        Long likeId = 99L;
        when(likeRepository.findById(likeId)).thenReturn(Optional.empty());

        // When / Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                likeManager.getLikeById(likeId)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Like not found");
        verify(likeRepository, times(1)).findById(likeId);
    }

    @Test
    void getAllLikes_shouldReturnEmptyList_whenNoLikesExist() {
        try (MockedStatic<LikeEntityConverter> mockedEntityConverter = Mockito.mockStatic(LikeEntityConverter.class);
             MockedStatic<LikeDtoConverter> mockedDtoConverter = Mockito.mockStatic(LikeDtoConverter.class)) {
            // Given
            when(likeRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<LikeResponse> result = likeManager.getAllLikes();

            // Then
            assertThat(result).isNotNull().isEmpty();
            verify(likeRepository, times(1)).findAll();
        }
    }

    @Test
    void getAllLikes_shouldReturnListOfLikes_whenLikesExist() {
        try (MockedStatic<LikeEntityConverter> mockedEntityConverter = Mockito.mockStatic(LikeEntityConverter.class);
             MockedStatic<LikeDtoConverter> mockedDtoConverter = Mockito.mockStatic(LikeDtoConverter.class)) {
            // Given
            List<LikeEntity> likeEntities = Arrays.asList(testLikeEntity, new LikeEntity());
            when(likeRepository.findAll()).thenReturn(likeEntities);
            mockedEntityConverter.when(() -> LikeEntityConverter.toDomain(any(LikeEntity.class)))
                    .thenReturn(testLikeDomain);
            mockedDtoConverter.when(() -> LikeDtoConverter.toResponse(any(Like.class)))
                    .thenReturn(testLikeResponse);

            // When
            List<LikeResponse> result = likeManager.getAllLikes();

            // Then
            assertThat(result).isNotNull().hasSize(2);
            assertThat(result.get(0)).isEqualTo(testLikeResponse);
            verify(likeRepository, times(1)).findAll();
            mockedEntityConverter.verify(() -> LikeEntityConverter.toDomain(any(LikeEntity.class)), times(2));
            mockedDtoConverter.verify(() -> LikeDtoConverter.toResponse(any(Like.class)), times(2));
        }
    }

    @Test
    void deleteLike_shouldDeleteLikeSuccessfully() {
        // Given
        Long likeId = 1L;
        when(likeRepository.existsById(likeId)).thenReturn(true);

        // When
        likeManager.deleteLike(likeId);

        // Then
        verify(likeRepository, times(1)).existsById(likeId);
        verify(likeRepository, times(1)).deleteById(likeId);
    }

    @Test
    void deleteLike_shouldThrowNotFoundException_whenLikeDoesNotExist() {
        // Given
        Long likeId = 99L;
        when(likeRepository.existsById(likeId)).thenReturn(false);

        // When / Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                likeManager.deleteLike(likeId)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Like not found");
        verify(likeRepository, times(1)).existsById(likeId);
        verify(likeRepository, never()).deleteById(any(Long.class));
    }
} 