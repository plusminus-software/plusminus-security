package software.plusminus.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenant", type = "string"))
@Filter(name = "tenantFilter", condition = "tenant = :tenant")
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "username", "tenant" }),
    @UniqueConstraint(columnNames = { "email", "tenant" }),
    @UniqueConstraint(columnNames = { "phone", "tenant" }),
})
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String displayName;

    @Email
    private String email;

    private String phone;

    @Size(min = 60, max = 60)
    @Column(nullable = false, length = 60)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @ElementCollection
    private Set<String> roles;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private String tenant;

}
