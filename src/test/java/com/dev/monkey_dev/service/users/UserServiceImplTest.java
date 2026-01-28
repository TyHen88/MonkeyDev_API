package com.dev.monkey_dev.service.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.dev.monkey_dev.common.api.StatusCode;
import com.dev.monkey_dev.domain.entity.Address;
import com.dev.monkey_dev.domain.entity.Role;
import com.dev.monkey_dev.domain.entity.Users;
import com.dev.monkey_dev.domain.respository.AddressRepository;
import com.dev.monkey_dev.domain.respository.RoleRepository;
import com.dev.monkey_dev.domain.respository.UserRepository;
import com.dev.monkey_dev.dto.mapper.UserMapper;
import com.dev.monkey_dev.dto.request.CriteriaFilter;
import com.dev.monkey_dev.dto.request.UserRequestDto;
import com.dev.monkey_dev.dto.response.UserResponseDto;
import com.dev.monkey_dev.enums.AddressType;
import com.dev.monkey_dev.exception.BusinessException;
import com.dev.monkey_dev.helper.AuthHelper;

/**
 * Unit Tests for UserServiceImpl class
 * 
 * This test class demonstrates advanced unit testing concepts:
 * 
 * 1. MOCKING DEPENDENCIES:
 * - @Mock: Creates a mock object for dependencies (repositories, mappers)
 * - @InjectMocks: Injects mocks into the class under test
 * - @ExtendWith(MockitoExtension.class): Enables Mockito annotations
 * 
 * 2. STUBBING (when().thenReturn()):
 * - Define what mock methods should return when called
 * - Allows us to control dependencies' behavior
 * 
 * 3. VERIFICATION (verify()):
 * - Verify that methods were called with expected parameters
 * - Verify how many times methods were called
 * - Ensures correct interaction with dependencies
 * 
 * 4. EXCEPTION TESTING:
 * - assertThrows(): Verify that exceptions are thrown correctly
 * - Check exception type and status codes
 * 
 * 5. STATIC METHOD MOCKING:
 * - MockedStatic: For mocking static methods (like AuthHelper.getUserId())
 * - Use try-with-resources to scope the mock
 * 
 * 6. TEST DATA SETUP:
 * - Create test objects using builders
 * - Keep test data setup clear and readable
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

        // Mock dependencies - these are fake objects that simulate real dependencies
        @Mock
        private UserRepository userRepository;

        @Mock
        private UserMapper userMapper;

        @Mock
        private AddressRepository addressRepository;

        @Mock
        private RoleRepository roleRepository;

        // InjectMocks creates an instance of UserServiceImpl and injects the mocks
        // above
        @InjectMocks
        private UserServiceImpl userService;

        // ============================================
        // Testing createUser() method
        // ============================================

        @Test
        @DisplayName("createUser should successfully create user when email and username are unique")
        void createUser_shouldSuccessfullyCreateUser_whenEmailAndUsernameAreUnique() {
                // Arrange: Set up test data and define mock behavior
                UserRequestDto requestDto = UserRequestDto.builder()
                                .fullName("John Doe")
                                .username("johndoe")
                                .email("john@example.com")
                                .password("password123")
                                .build();

                Users userEntity = Users.builder()
                                .id(1L)
                                .fullName("John Doe")
                                .username("johndoe")
                                .email("john@example.com")
                                .build();

                Users savedUser = Users.builder()
                                .id(1L)
                                .fullName("John Doe")
                                .username("johndoe")
                                .email("john@example.com")
                                .build();

                UserResponseDto expectedResponse = UserResponseDto.builder()
                                .id(1L)
                                .fullName("John Doe")
                                .username("johndoe")
                                .email("john@example.com")
                                .build();

                // Stub: Define what mocks should return
                when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
                when(userRepository.findByUsername("johndoe")).thenReturn(new ArrayList<>());
                when(userMapper.toUserEntity(requestDto)).thenReturn(userEntity);
                when(roleRepository.findAllByNameIn(eq(java.util.Set.of("USER"))))
                                .thenReturn(List.of(Role.builder().id(1L).name("USER").build()));
                when(userRepository.save(userEntity)).thenReturn(savedUser);
                when(userMapper.toUserResponseDto(savedUser)).thenReturn(expectedResponse);

                // Act: Execute the method being tested
                UserResponseDto result = userService.createUser(requestDto);

                // Assert: Verify the result and interactions
                assertNotNull(result, "Result should not be null");
                assertEquals(expectedResponse.getId(), result.getId());
                assertEquals(expectedResponse.getEmail(), result.getEmail());
                assertEquals(expectedResponse.getUsername(), result.getUsername());

                // Verify: Check that repository methods were called correctly
                verify(userRepository, times(1)).findByEmail("john@example.com");
                verify(userRepository, times(1)).findByUsername("johndoe");
                verify(userRepository, times(1)).save(userEntity);
                verify(userMapper, times(1)).toUserEntity(requestDto);
                verify(userMapper, times(1)).toUserResponseDto(savedUser);
        }

        @Test
        @DisplayName("createUser should throw exception when email already exists")
        void createUser_shouldThrowException_whenEmailAlreadyExists() {
                // Arrange
                UserRequestDto requestDto = UserRequestDto.builder()
                                .email("existing@email.com")
                                .username("newuser")
                                .build();

                Users existingUser = Users.builder()
                                .id(1L)
                                .email("existing@email.com")
                                .build();

                when(userRepository.findByEmail("existing@email.com"))
                                .thenReturn(Optional.of(existingUser));

                // Act & Assert: Verify exception is thrown
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> userService.createUser(requestDto));

                assertEquals(StatusCode.USER_ID_ALREADY_EXISTS, exception.getStatusCode());

                // Verify: Ensure save was never called (transaction should not complete)
                verify(userRepository, never()).save(any(Users.class));
                verify(userMapper, never()).toUserEntity(any(UserRequestDto.class));
        }

        @Test
        @DisplayName("createUser should throw exception when username already exists")
        void createUser_shouldThrowException_whenUsernameAlreadyExists() {
                // Arrange
                UserRequestDto requestDto = UserRequestDto.builder()
                                .email("new@email.com")
                                .username("existinguser")
                                .build();

                Users existingUser = Users.builder()
                                .id(1L)
                                .username("existinguser")
                                .build();

                when(userRepository.findByEmail("new@email.com")).thenReturn(Optional.empty());
                when(userRepository.findByUsername("existinguser"))
                                .thenReturn(List.of(existingUser));

                // Act & Assert
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> userService.createUser(requestDto));

                assertEquals(StatusCode.USER_ID_ALREADY_EXISTS, exception.getStatusCode());
                verify(userRepository, never()).save(any(Users.class));
        }

        // ============================================
        // Testing getUserProfile() method
        // ============================================

        @Test
        @DisplayName("getUserProfile should return user profile with addresses when user exists")
        void getUserProfile_shouldReturnUserProfileWithAddresses_whenUserExists() {
                // Arrange
                Long userId = 1L;
                Users user = Users.builder()
                                .id(userId)
                                .fullName("John Doe")
                                .username("johndoe")
                                .email("john@example.com")
                                .build();

                Address address1 = Address.builder()
                                .id(1L)
                                .type(AddressType.SHIPPING)
                                .addressLine1("123 Main St")
                                .city("New York")
                                .build();

                Address address2 = Address.builder()
                                .id(2L)
                                .type(AddressType.BILLING)
                                .addressLine1("456 Office Ave")
                                .city("New York")
                                .build();

                List<Address> addresses = List.of(address1, address2);

                UserResponseDto expectedResponse = UserResponseDto.builder()
                                .id(userId)
                                .fullName("John Doe")
                                .username("johndoe")
                                .email("john@example.com")
                                .build();

                // Mock static method using MockedStatic
                try (MockedStatic<AuthHelper> mockedAuthHelper = mockStatic(AuthHelper.class)) {
                        mockedAuthHelper.when(AuthHelper::getUserId).thenReturn(userId);

                        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
                        when(addressRepository.findByUserId(userId)).thenReturn(addresses);
                        when(userMapper.toUsersResponseDto(user, addresses)).thenReturn(expectedResponse);

                        // Act
                        UserResponseDto result = userService.getUserProfile();

                        // Assert
                        assertNotNull(result);
                        assertEquals(expectedResponse.getId(), result.getId());
                        assertEquals(expectedResponse.getEmail(), result.getEmail());

                        // Verify
                        verify(userRepository, times(1)).findById(userId);
                        verify(addressRepository, times(1)).findByUserId(userId);
                        verify(userMapper, times(1)).toUsersResponseDto(user, addresses);
                }
        }

        @Test
        @DisplayName("getUserProfile should throw exception when user not found")
        void getUserProfile_shouldThrowException_whenUserNotFound() {
                // Arrange
                Long userId = 999L;

                try (MockedStatic<AuthHelper> mockedAuthHelper = mockStatic(AuthHelper.class)) {
                        mockedAuthHelper.when(AuthHelper::getUserId).thenReturn(userId);
                        when(userRepository.findById(userId)).thenReturn(Optional.empty());

                        // Act & Assert
                        BusinessException exception = assertThrows(BusinessException.class,
                                        () -> userService.getUserProfile());

                        assertEquals(StatusCode.USER_NOT_FOUND, exception.getStatusCode());
                        verify(addressRepository, never()).findByUserId(anyLong());
                }
        }

        // ============================================
        // Testing getUserByEmailOrUsername() method
        // ============================================

        @Test
        @DisplayName("getUserByEmailOrUsername should return user when found by email")
        void getUserByEmailOrUsername_shouldReturnUser_whenFoundByEmail() {
                // Arrange
                String email = "john@example.com";
                Users user = Users.builder()
                                .id(1L)
                                .email(email)
                                .username("johndoe")
                                .build();

                UserResponseDto expectedResponse = UserResponseDto.builder()
                                .id(1L)
                                .email(email)
                                .build();

                when(userRepository.findByEmailOrUsername(email, null))
                                .thenReturn(Optional.of(user));
                when(userMapper.toUserResponseDto(user)).thenReturn(expectedResponse);

                // Act
                UserResponseDto result = userService.getUserByEmailOrUsername(email, null);

                // Assert
                assertNotNull(result);
                assertEquals(expectedResponse.getId(), result.getId());
                verify(userRepository, times(1)).findByEmailOrUsername(email, null);
        }

        @Test
        @DisplayName("getUserByEmailOrUsername should return user when found by username")
        void getUserByEmailOrUsername_shouldReturnUser_whenFoundByUsername() {
                // Arrange
                String username = "johndoe";
                Users user = Users.builder()
                                .id(1L)
                                .username(username)
                                .build();

                UserResponseDto expectedResponse = UserResponseDto.builder()
                                .id(1L)
                                .username(username)
                                .build();

                when(userRepository.findByEmailOrUsername(null, username))
                                .thenReturn(Optional.of(user));
                when(userMapper.toUserResponseDto(user)).thenReturn(expectedResponse);

                // Act
                UserResponseDto result = userService.getUserByEmailOrUsername(null, username);

                // Assert
                assertNotNull(result);
                assertEquals(expectedResponse.getUsername(), result.getUsername());
        }

        @Test
        @DisplayName("getUserByEmailOrUsername should throw exception when both email and username are null")
        void getUserByEmailOrUsername_shouldThrowException_whenBothEmailAndUsernameAreNull() {
                // Act & Assert
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> userService.getUserByEmailOrUsername(null, null));

                assertEquals(StatusCode.EMAIL_REQUIRED, exception.getStatusCode());
                verify(userRepository, never()).findByEmailOrUsername(any(), any());
        }

        @Test
        @DisplayName("getUserByEmailOrUsername should throw exception when user not found")
        void getUserByEmailOrUsername_shouldThrowException_whenUserNotFound() {
                // Arrange
                String email = "nonexistent@example.com";
                when(userRepository.findByEmailOrUsername(email, null))
                                .thenReturn(Optional.empty());

                // Act & Assert
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> userService.getUserByEmailOrUsername(email, null));

                assertEquals(StatusCode.USER_NOT_FOUND, exception.getStatusCode());
        }

        // ============================================
        // Testing updateUser() method
        // ============================================

        @Test
        @DisplayName("updateUser should successfully update user when no conflicts")
        void updateUser_shouldSuccessfullyUpdateUser_whenNoConflicts() {
                // Arrange
                Long userId = 1L;
                UserRequestDto requestDto = UserRequestDto.builder()
                                .fullName("John Updated")
                                .username("johndoe")
                                .email("john@example.com")
                                .build();

                Users existingUser = Users.builder()
                                .id(userId)
                                .fullName("John Doe")
                                .username("johndoe")
                                .email("john@example.com")
                                .build();

                Users updatedUser = Users.builder()
                                .id(userId)
                                .fullName("John Updated")
                                .username("johndoe")
                                .email("john@example.com")
                                .build();

                UserResponseDto expectedResponse = UserResponseDto.builder()
                                .id(userId)
                                .fullName("John Updated")
                                .build();

                try (MockedStatic<AuthHelper> mockedAuthHelper = mockStatic(AuthHelper.class)) {
                        mockedAuthHelper.when(AuthHelper::getUserId).thenReturn(userId);

                        when(userRepository.findUserById(userId)).thenReturn(Optional.of(existingUser));
                        when(userRepository.save(any(Users.class))).thenReturn(updatedUser);
                        when(userMapper.toUserResponseDto(updatedUser)).thenReturn(expectedResponse);

                        // Act
                        UserResponseDto result = userService.updateUser(requestDto);

                        // Assert
                        assertNotNull(result);
                        assertEquals("John Updated", result.getFullName());
                        verify(userRepository, times(1)).save(any(Users.class));
                }
        }

        @Test
        @DisplayName("updateUser should throw exception when email is taken by another user")
        void updateUser_shouldThrowException_whenEmailIsTakenByAnotherUser() {
                // Arrange
                Long userId = 1L;
                UserRequestDto requestDto = UserRequestDto.builder()
                                .email("taken@example.com")
                                .username("johndoe")
                                .build();

                Users existingUser = Users.builder()
                                .id(userId)
                                .email("john@example.com")
                                .username("johndoe")
                                .build();

                Users userWithEmail = Users.builder()
                                .id(2L) // Different user ID
                                .email("taken@example.com")
                                .build();

                try (MockedStatic<AuthHelper> mockedAuthHelper = mockStatic(AuthHelper.class)) {
                        mockedAuthHelper.when(AuthHelper::getUserId).thenReturn(userId);

                        when(userRepository.findUserById(userId)).thenReturn(Optional.of(existingUser));
                        when(userRepository.findByEmail("taken@example.com"))
                                        .thenReturn(Optional.of(userWithEmail));

                        // Act & Assert
                        BusinessException exception = assertThrows(BusinessException.class,
                                        () -> userService.updateUser(requestDto));

                        assertEquals(StatusCode.USER_ID_ALREADY_EXISTS, exception.getStatusCode());
                        verify(userRepository, never()).save(any(Users.class));
                }
        }

        @Test
        @DisplayName("updateUser should throw exception when username is taken by another user")
        void updateUser_shouldThrowException_whenUsernameIsTakenByAnotherUser() {
                // Arrange
                Long userId = 1L;
                UserRequestDto requestDto = UserRequestDto.builder()
                                .email("john@example.com")
                                .username("takenuser")
                                .build();

                Users existingUser = Users.builder()
                                .id(userId)
                                .email("john@example.com")
                                .username("johndoe")
                                .build();

                Users userWithUsername = Users.builder()
                                .id(2L) // Different user ID
                                .username("takenuser")
                                .build();

                try (MockedStatic<AuthHelper> mockedAuthHelper = mockStatic(AuthHelper.class)) {
                        mockedAuthHelper.when(AuthHelper::getUserId).thenReturn(userId);

                        when(userRepository.findUserById(userId)).thenReturn(Optional.of(existingUser));
                        when(userRepository.findByUsername("takenuser"))
                                        .thenReturn(List.of(userWithUsername));

                        // Act & Assert
                        BusinessException exception = assertThrows(BusinessException.class,
                                        () -> userService.updateUser(requestDto));

                        assertEquals(StatusCode.USER_ID_ALREADY_EXISTS, exception.getStatusCode());
                }
        }

        @Test
        @DisplayName("updateUser should allow updating email to same email")
        void updateUser_shouldAllowUpdatingEmailToSameEmail() {
                // Arrange
                Long userId = 1L;
                UserRequestDto requestDto = UserRequestDto.builder()
                                .email("john@example.com") // Same email
                                .username("johndoe")
                                .fullName("John Updated")
                                .build();

                Users existingUser = Users.builder()
                                .id(userId)
                                .email("john@example.com")
                                .username("johndoe")
                                .fullName("John Doe")
                                .build();

                Users updatedUser = Users.builder()
                                .id(userId)
                                .email("john@example.com")
                                .username("johndoe")
                                .fullName("John Updated")
                                .build();

                UserResponseDto expectedResponse = UserResponseDto.builder()
                                .id(userId)
                                .fullName("John Updated")
                                .build();

                try (MockedStatic<AuthHelper> mockedAuthHelper = mockStatic(AuthHelper.class)) {
                        mockedAuthHelper.when(AuthHelper::getUserId).thenReturn(userId);

                        when(userRepository.findUserById(userId)).thenReturn(Optional.of(existingUser));
                        when(userRepository.save(any(Users.class))).thenReturn(updatedUser);
                        when(userMapper.toUserResponseDto(updatedUser)).thenReturn(expectedResponse);

                        // Act
                        UserResponseDto result = userService.updateUser(requestDto);

                        // Assert
                        assertNotNull(result);
                        // Verify that findByEmail was not called (since email didn't change)
                        verify(userRepository, never()).findByEmail(anyString());
                        verify(userRepository, times(1)).save(any(Users.class));
                }
        }

        // ============================================
        // Testing updateUserStatus() method
        // ============================================

        @Test
        @DisplayName("updateUserStatus should activate user when isActive is true")
        void updateUserStatus_shouldActivateUser_whenIsActiveIsTrue() {
                // Arrange
                Long userId = 1L;
                Users user = Users.builder()
                                .id(userId)
                                .active(false)
                                .build();

                Users activatedUser = Users.builder()
                                .id(userId)
                                .active(true)
                                .build();

                when(userRepository.findById(userId)).thenReturn(Optional.of(user));
                when(userRepository.save(any(Users.class))).thenReturn(activatedUser);

                // Act
                userService.updateUserStatus(userId, true);

                // Assert
                verify(userRepository, times(1)).findById(userId);
                verify(userRepository, times(1)).save(user);
                assertTrue(user.isActive(), "User should be activated");
        }

        @Test
        @DisplayName("updateUserStatus should deactivate user when isActive is false")
        void updateUserStatus_shouldDeactivateUser_whenIsActiveIsFalse() {
                // Arrange
                Long userId = 1L;
                Users user = Users.builder()
                                .id(userId)
                                .active(true)
                                .build();

                when(userRepository.findById(userId)).thenReturn(Optional.of(user));
                when(userRepository.save(any(Users.class))).thenReturn(user);

                // Act
                userService.updateUserStatus(userId, false);

                // Assert
                verify(userRepository, times(1)).save(user);
                assertFalse(user.isActive(), "User should be deactivated");
        }

        @Test
        @DisplayName("updateUserStatus should throw exception when isActive is null")
        void updateUserStatus_shouldThrowException_whenIsActiveIsNull() {
                // Arrange
                Long userId = 1L;
                Users user = Users.builder()
                                .id(userId)
                                .build();

                when(userRepository.findById(userId)).thenReturn(Optional.of(user));

                // Act & Assert
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> userService.updateUserStatus(userId, null));

                assertEquals(StatusCode.IS_ACTIVE_REQUIRED, exception.getStatusCode());
                verify(userRepository, never()).save(any(Users.class));
        }

        @Test
        @DisplayName("updateUserStatus should throw exception when user not found")
        void updateUserStatus_shouldThrowException_whenUserNotFound() {
                // Arrange
                Long userId = 999L;
                when(userRepository.findById(userId)).thenReturn(Optional.empty());

                // Act & Assert
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> userService.updateUserStatus(userId, true));

                assertEquals(StatusCode.USER_NOT_FOUND, exception.getStatusCode());
                verify(userRepository, never()).save(any(Users.class));
        }

        // ============================================
        // Testing getAllUsers() method
        // ============================================

        @Test
        @DisplayName("getAllUsers should return paginated users with addresses")
        void getAllUsers_shouldReturnPaginatedUsersWithAddresses() {
                // Arrange
                Boolean isActive = true;
                CriteriaFilter criteriaFilter = null;

                Users user1 = Users.builder()
                                .id(1L)
                                .fullName("John Doe")
                                .email("john@example.com")
                                .active(true)
                                .build();

                Users user2 = Users.builder()
                                .id(2L)
                                .fullName("Jane Smith")
                                .email("jane@example.com")
                                .active(true)
                                .build();

                List<Users> usersList = List.of(user1, user2);
                Page<Users> usersPage = new PageImpl<>(usersList, PageRequest.of(0, 10), 2);

                Address address1 = Address.builder()
                                .id(1L)
                                .addressLine1("123 Main St")
                                .build();

                when(userRepository.findAllUsersWithFilters(eq(isActive), isNull(), any(Pageable.class)))
                                .thenReturn(usersPage);
                when(addressRepository.findByUserId(1L)).thenReturn(List.of(address1));
                when(addressRepository.findByUserId(2L)).thenReturn(new ArrayList<>());
                when(userMapper.toUsersResponseDto(eq(user1), anyList()))
                                .thenReturn(UserResponseDto.builder().id(1L).build());
                when(userMapper.toUsersResponseDto(eq(user2), anyList()))
                                .thenReturn(UserResponseDto.builder().id(2L).build());

                // Act
                Page<UserResponseDto> result = userService.getAllUsers(isActive, criteriaFilter);

                // Assert
                assertNotNull(result);
                assertEquals(2, result.getContent().size());
                verify(userRepository, times(1)).findAllUsersWithFilters(eq(isActive), isNull(), any(Pageable.class));
                verify(addressRepository, times(1)).findByUserId(1L);
                verify(addressRepository, times(1)).findByUserId(2L);
        }
}
