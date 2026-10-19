package flexr.flex.persistence;

import flexr.flex.persistence.entities.PostEntity;
import flexr.flex.persistence.interfaces.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    @Test
    void savePost_shouldPersistAndRetrievePost() {
        // Arrange
        PostEntity post = PostEntity.builder()
                .content("Test Content for Post")
                .authorId("test-author-123")
                .image("test-image-data".getBytes())
                .createdAt(Instant.now())
                .build();

        // Act
        PostEntity savedPost = postRepository.save(post);
        entityManager.flush(); // Ensure data is written to the H2 database
        entityManager.clear(); // Clear the persistence context to ensure retrieval from DB

        // Assert
        assertThat(savedPost).isNotNull();
        assertThat(savedPost.getId()).isNotNull();

        Optional<PostEntity> foundPost = postRepository.findById(savedPost.getId());

        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getContent()).isEqualTo(post.getContent());
        assertThat(foundPost.get().getAuthorId()).isEqualTo(post.getAuthorId());
        assertThat(foundPost.get().getImage()).isEqualTo(post.getImage());
        assertThat(foundPost.get().getCreatedAt().truncatedTo(java.time.temporal.ChronoUnit.MILLIS)).isEqualTo(post.getCreatedAt().truncatedTo(java.time.temporal.ChronoUnit.MILLIS));
    }
} 