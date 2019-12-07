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
public class User implements Serializable {

    private Long id;
    private String username;
    private String password;
    private String salt;
    // 是否锁定，用于封禁用户使用，其实最好使用 Enum 字段存储，可以实现更复杂的用户状态实现
    private Boolean locked = Boolean.FALSE;

    public String getCredentialsSalt() {
        return username + salt;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
