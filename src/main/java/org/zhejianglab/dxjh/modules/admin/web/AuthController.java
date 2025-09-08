package org.zhejianglab.dxjh.modules.admin.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

import org.zhejianglab.dxjh.modules.admin.dto.*;
import org.zhejianglab.dxjh.modules.admin.entity.AdmRole;
import org.zhejianglab.dxjh.modules.admin.entity.AdmUser;
import org.zhejianglab.dxjh.modules.admin.service.AdminUserService;
import org.zhejianglab.dxjh.common.utils.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AdminUserService userSvc;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest req){
        AdmUser u = userSvc.loadUserWithRoles(req.getUsername());   // 换这个

        if (!Boolean.TRUE.equals(u.getIsEnabled())) {
            throw new IllegalStateException("账号已禁用");
        }
        if (!encoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        Set<String> roles = u.getRoles().stream().map(AdmRole::getCode).collect(Collectors.toSet());
        String token = jwt.generateToken(u.getUsername(), roles);
        return ResponseEntity.ok(new LoginResponse(token, u.getUsername(), u.getDisplayName(), roles));
    }


}
