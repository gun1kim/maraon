package com.example.maraon.service;

import com.example.maraon.dto.UserRequestDTO;
import com.example.maraon.dto.UserResponseDTO;
import com.example.maraon.entity.user.User;
import com.example.maraon.entity.user.UserRoleType;
import com.example.maraon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 유저 한 명 생성
    @Transactional
    public void createOneUser(UserRequestDTO dto) {
        String email = dto.getEmail();
        String password = dto.getPassword();
        String username = dto.getUsername();

        // 동일한 email로 유저 생성 불가능
        if (userRepository.existsByEmail(email)) {
            return;
        }

        // 유저에 대한 Entity 생성: DTO -> Entity 및 추가 정보 설정
        User entity = new User();
        entity.setEmail(email);
        entity.setPassword(bCryptPasswordEncoder.encode(password));
        entity.setUsername(username);
        entity.setRole(UserRoleType.USER);

        userRepository.save(entity);
    }

    // 유저 한 명 조회
    @Transactional(readOnly = true)
    public UserResponseDTO readOneUser(String email) {
        User entity = userRepository.findByEmail(email).orElseThrow();

        UserResponseDTO dto = new UserResponseDTO();
        dto.setEmail(entity.getEmail());
        dto.setUsername(entity.getUsername());
        dto.setRole(entity.getRole().toString());

        return dto;
    }

    // 유저 모두 조회
    @Transactional(readOnly = true)
    public List<UserResponseDTO> readAllUsers() {
        List<User> list = userRepository.findAll();

        List<UserResponseDTO> dtos = new ArrayList<>();
        for (User user : list) {
            UserResponseDTO dto = new UserResponseDTO();
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
            dto.setRole(user.getRole().toString());

            dtos.add(dto);
        }

        return dtos;
    }

    // 유저 로그인 (로그인 같은 경우 읽기지만, 시큐리티 형식으로 맞춰야 함)
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User entity = userRepository.findByEmail(email).orElseThrow();

        return org.springframework.security.core.userdetails.User.builder()
                .username(entity.getEmail())
                .password(entity.getPassword())
                .roles(entity.getRole().toString())
                .build();
    }

    // 유저 한 명 수정
    @Transactional
    public void updateOneUser(UserRequestDTO dto, String email) {
        // 기존 유저 정보 읽기
        User entity = userRepository.findByEmail(email).orElseThrow();

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            entity.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        }

        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            entity.setUsername(dto.getUsername());
        }

        userRepository.save(entity);
    }

    // 유저 한 명 삭제
    @Transactional
    public void deleteOneUser(String email) {
        userRepository.deleteByEmail(email);
    }

    // 유저 접근 권한 체크
    public Boolean isAccess(String email) {

        // 현재 로그인되어 있는 유저의 email
        String sessionUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        // 현재 로그인 되어 있는 유저의 role
        String sessionRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();

        // 수직적으로 ADMIN이면 무조건 접근 가능
        if ("ROLE_ADMIN".equals(sessionRole)) {
            return true;
        }

        // 수평적으로 특정 행위를 수행할 email에 대해 세션(현재 로그인한) email과 같은지
        if (email.equals(sessionUserEmail)) {
            return true;
        }

        // 나머지 다 불가
        return false;

    }
}
