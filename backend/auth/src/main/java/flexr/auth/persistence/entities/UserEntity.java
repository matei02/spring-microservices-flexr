package flexr.auth.persistence.entities;

import flexr.auth.domain.classes.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "auth0_id")
    private String auth0Id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "handle")
    private String handle;

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    private String role;
}
