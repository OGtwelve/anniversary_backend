package org.zhejianglab.dxjh.modules.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import org.zhejianglab.dxjh.modules.admin.entity.*;
import org.zhejianglab.dxjh.modules.admin.repository.*;

@Service @RequiredArgsConstructor
public class AdminUserService {
    private final AdmUserRepository userRepo;
    private final AdmRoleRepository roleRepo;
    private final PasswordEncoder encoder;

    @Transactional
    public AdmUser createUser(String username, String displayName, String rawPassword, Set<String> roleCodes){
        if (userRepo.existsByUsername(username)) throw new IllegalStateException("用户名已存在");
        AdmUser u = new AdmUser();
        u.setUsername(username);
        u.setDisplayName(displayName);
        u.setPasswordHash(encoder.encode(rawPassword));
        u.setIsEnabled(true);
        if (roleCodes != null && !roleCodes.isEmpty()){
            Set<AdmRole> rs = roleCodes.stream()
                    .map(code -> roleRepo.findByCode(code).orElseThrow(() -> new IllegalArgumentException("角色不存在:"+code)))
                    .collect(Collectors.toSet());
            u.setRoles(rs);
        }
        return userRepo.save(u);
    }

    @Transactional
    public AdmUser updateUser(Long id, String displayName, Boolean enabled, Set<String> roleCodes){
        AdmUser u = userRepo.findById(id).orElseThrow(() -> new NoSuchElementException("用户不存在"));
        if (displayName != null) u.setDisplayName(displayName);
        if (enabled != null) u.setIsEnabled(enabled);
        if (roleCodes != null){
            Set<AdmRole> rs = roleCodes.stream()
                    .map(code -> roleRepo.findByCode(code).orElseThrow(() -> new IllegalArgumentException("角色不存在:"+code)))
                    .collect(Collectors.toSet());
            u.setRoles(rs);
        }
        return userRepo.save(u);
    }

    @Transactional
    public void changePassword(Long id, String newPassword){
        AdmUser u = userRepo.findById(id).orElseThrow(() -> new NoSuchElementException("用户不存在"));
        u.setPasswordHash(encoder.encode(newPassword));
        userRepo.save(u);
    }

    @Transactional(readOnly = true)
    public AdmUser loadUserWithRoles(String username){
        // 若用 EntityGraph 的那个方法，就直接 repo.findByUsername(...)
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));
    }

    public Optional<AdmUser> findByUsername(String username){ return userRepo.findByUsername(username); }
    public List<AdmUser> list(){ return userRepo.findAll(); }
    public void delete(Long id){ userRepo.deleteById(id); }
}
