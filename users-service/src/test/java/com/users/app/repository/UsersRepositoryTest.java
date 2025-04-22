package com.users.app.repository;

import com.users.app.entity.Users;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UsersRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsersRepository usersRepository;

    private Users createSampleUser() {
        return Users.builder()
                .fullName("Budi Santoso")
                .email("budi@example.com")
                .password("secret123")
                .phone("+6281234567890")
                .username("budi123")
                .age(30)
                .salary(8000000.0)
                .role("USER")
                .build();
    }

    @Test
    public void testFindByUsername() {
        Users user = createSampleUser();
        entityManager.persistAndFlush(user);

        Optional<Users> found = usersRepository.findByUsername("budi123");

        assertTrue(found.isPresent());
        assertEquals("budi@example.com", found.get().getEmail());
    }

    @Test
    public void testFindByPhone() {
        Users user = createSampleUser();
        entityManager.persistAndFlush(user);

        Optional<Users> found = usersRepository.findByPhone("+6281234567890");

        assertTrue(found.isPresent());
        assertEquals("budi123", found.get().getUsername());
    }

    @Test
    public void testFindByEmail() {
        Users user = createSampleUser();
        entityManager.persistAndFlush(user);

        Optional<Users> found = usersRepository.findByEmail("budi@example.com");

        assertTrue(found.isPresent());
        assertEquals("+6281234567890", found.get().getPhone());
    }

    @Test
    public void testUserNotFound() {
        Optional<Users> notFound = usersRepository.findByUsername("notexist");
        assertFalse(notFound.isPresent());
    }
}
