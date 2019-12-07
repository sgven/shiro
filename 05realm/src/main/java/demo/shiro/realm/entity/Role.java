package demo.shiro.realm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role implements Serializable {

    private Long id;
    private String role; // 角色标识 程序中判断使用,如"admin"
    private String description; // 角色描述
    private Boolean available = Boolean.FALSE; // 是否可用,如果不可用将不会添加给用户

    public Role(String role, String description, Boolean available) {
        this.role = role;
        this.description = description;
        this.available = available;
    }
}
