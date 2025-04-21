package com.users.app.service.implementation;

import com.users.app.entity.Users;
import com.users.app.repository.UsersRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsersDetailServiceImplementationTest {

    @InjectMocks
    private UsersDetailServiceImplementation usersDetailService;

    @Mock
    private UsersRepository usersRepository;

    private Users mockUser;

    @Before
    public void setUp() {
        mockUser = new Users();
        mockUser.setUsername("pipin");
        mockUser.setPassword("securePassword123");
    }

    @Test
    public void testLoadUserByUsername_UserFound() {
        when(usersRepository.findByUsername("pipin")).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = usersDetailService.loadUserByUsername("pipin");

        assertNotNull(userDetails);
        assertEquals("pipin", userDetails.getUsername());
        assertEquals("securePassword123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsername_UserNotFound() {
        when(usersRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        usersDetailService.loadUserByUsername("unknown"); // should throw exception
    }
}
