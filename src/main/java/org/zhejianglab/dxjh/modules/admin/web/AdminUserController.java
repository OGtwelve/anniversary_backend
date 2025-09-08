package org.zhejianglab.dxjh.modules.admin.web;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import org.zhejianglab.dxjh.modules.admin.dto.*;
import org.zhejianglab.dxjh.modules.admin.entity.AdmRole;
import org.zhejianglab.dxjh.modules.admin.entity.AdmUser;
import org.zhejianglab.dxjh.modules.admin.service.AdminUserService;

/**
 * @author :og-twelve
 * @date : 2025/9/8
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService svc;

    @GetMapping
    public List<Map<String,Object>> list(){
        List<AdmUser> users = svc.list();
        List<Map<String,Object>> vo = new ArrayList<>();
        for (AdmUser u : users){
            Map<String,Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("displayName", u.getDisplayName());
            m.put("enabled", u.getIsEnabled());
            m.put("roles", u.getRoles().stream().map(AdmRole::getCode).collect(Collectors.toList()));
            vo.add(m);
        }
        return vo;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateUserRequest req){
        AdmUser u = svc.createUser(req.getUsername(), req.getDisplayName(), req.getPassword(), req.getRoleCodes());
        return ResponseEntity.ok(Collections.singletonMap("id", u.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest req){
        AdmUser u = svc.updateUser(id, req.getDisplayName(), req.getEnabled(), req.getRoleCodes());
        return ResponseEntity.ok(Collections.singletonMap("ok", true));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePwd(@PathVariable Long id, @RequestBody @Valid ChangePasswordRequest req){
        svc.changePassword(id, req.getNewPassword());
        return ResponseEntity.ok(Collections.singletonMap("ok", true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        svc.delete(id);
        return ResponseEntity.ok(Collections.singletonMap("ok", true));
    }
}
