package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User,Long> {
    @Modifying
    @Query("DELETE FROM User u WHERE u.id = ?1")
    void deleteByUserId(Long userId);

    boolean existsById(Long userId);
}
