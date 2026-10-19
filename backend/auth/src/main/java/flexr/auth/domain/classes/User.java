package flexr.auth.domain.classes;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
    private Long id;
    private String auth0Id;
    private String fullName;
    private String handle;
    private String email;
    private Enum<UserRole> role;
}
