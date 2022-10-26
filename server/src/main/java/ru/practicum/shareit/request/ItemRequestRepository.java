package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Modifying
    @Query("SELECT r FROM ItemRequest r WHERE r.authorId=?1 ORDER BY r.created DESC")
    List<ItemRequest> findAllByAuthorId(Long userId);
}
