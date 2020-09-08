package win.hgfdodo.jhipster.app1.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * A DTO for the {@link win.hgfdodo.jhipster.app1.domain.Role} entity.
 */
public class RoleDTO implements Serializable {
    
    private Long id;

    @NotNull
    private String roleName;

    private String additional;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoleDTO)) {
            return false;
        }

        return id != null && id.equals(((RoleDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoleDTO{" +
            "id=" + getId() +
            ", roleName='" + getRoleName() + "'" +
            ", additional='" + getAdditional() + "'" +
            "}";
    }
}
