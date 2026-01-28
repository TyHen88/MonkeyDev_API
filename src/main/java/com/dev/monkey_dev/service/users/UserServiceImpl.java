package com.dev.monkey_dev.service.users;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dev.monkey_dev.common.api.StatusCode;
import com.dev.monkey_dev.domain.entity.Address;
import com.dev.monkey_dev.domain.entity.Users;
import com.dev.monkey_dev.domain.respository.AddressRepository;
import com.dev.monkey_dev.domain.respository.RoleRepository;
import com.dev.monkey_dev.domain.respository.UserRepository;
import com.dev.monkey_dev.dto.mapper.UserMapper;
import com.dev.monkey_dev.dto.request.CriteriaFilter;
import com.dev.monkey_dev.dto.request.UserRequestDto;
import com.dev.monkey_dev.dto.response.UserResponseDto;
import com.dev.monkey_dev.exception.BusinessException;
import com.dev.monkey_dev.helper.AuthHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AddressRepository addressRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new BusinessException(StatusCode.USER_ID_ALREADY_EXISTS);
        }
        if (!userRepository.findByUsername(userRequestDto.getUsername()).isEmpty()) {
            throw new BusinessException(StatusCode.USER_ID_ALREADY_EXISTS);
        }
        Users user = userMapper.toUserEntity(userRequestDto);
        var roles = roleRepository.findAllByNameIn(Set.of("USER"));
        if (roles.isEmpty()) {
            throw new BusinessException(StatusCode.BAD_REQUEST, "Default role not configured");
        }
        user.setRoles(roles.stream().collect(Collectors.toSet()));
        Users savedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserProfile() {
        Long userId = AuthHelper.getUserId();
        Users user = userRepository
                .findById(userId)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));
        List<Address> addresses = addressRepository.findByUserId(userId);
        return userMapper.toUsersResponseDto(user, addresses);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public UserResponseDto getUserByEmailOrUsername(String email, String username) {
        if (email == null && username == null) {
            throw new BusinessException(StatusCode.EMAIL_REQUIRED);
        }
        Users user = userRepository.findByEmailOrUsername(email, username)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));
        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponseDto updateUser(UserRequestDto userRequestDto) {
        Long id = AuthHelper.getUserId();
        var user = userRepository.findUserById(id).orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));

        if (!user.getEmail().equals(userRequestDto.getEmail())) {
            if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
                throw new BusinessException(StatusCode.USER_ID_ALREADY_EXISTS);
            }
        }
        if (!user.getUsername().equals(userRequestDto.getUsername())) {
            var usersWithUsername = userRepository.findByUsername(userRequestDto.getUsername());
            boolean usernameTakenByAnother = usersWithUsername.stream()
                    .anyMatch(existing -> existing.getId() != null && !existing.getId().equals(user.getId()));
            if (usernameTakenByAnother) {
                throw new BusinessException(StatusCode.USER_ID_ALREADY_EXISTS);
            }
        }

        user.setFullName(userRequestDto.getFullName());
        user.setUsername(userRequestDto.getUsername());
        user.setEmail(userRequestDto.getEmail());
        var savedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long id, Boolean isActive) {
        var user = userRepository.findById(id).orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));
        if (isActive != null && isActive) {
            user.activate();
        } else if (isActive != null && !isActive) {
            user.deactivate();
        } else {
            throw new BusinessException(StatusCode.IS_ACTIVE_REQUIRED);
        }
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<UserResponseDto> getAllUsers(Boolean isActive, CriteriaFilter criteriaFilter) {
        // Use default sort by createdAt descending if no sort is specified
        Pageable pageable = criteriaFilter != null
                ? criteriaFilter.toPageable("createdAt", Sort.Direction.DESC)
                : PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Get search term from criteria filter
        String search = criteriaFilter != null ? criteriaFilter.getSearch() : null;

        // Fetch paginated users from repository
        Page<Users> usersPage = userRepository.findAllUsersWithFilters(isActive, search, pageable);

        // Map Users entities to UserResponseDto with addresses
        return usersPage.map(user -> {
            List<Address> addresses = addressRepository.findByUserId(user.getId());
            return userMapper.toUsersResponseDto(user, addresses);
        });
    }

}
