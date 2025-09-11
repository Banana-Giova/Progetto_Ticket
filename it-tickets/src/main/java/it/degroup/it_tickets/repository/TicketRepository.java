package it.degroup.it_tickets.repository;

import it.degroup.it_tickets.entity.Status;
import it.degroup.it_tickets.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByUserId(
            Long userId,
            Pageable pageable);

    Optional<Ticket> findById(Long id);

    Page<Ticket> findByUserIdAndTitleContainingIgnoreCaseOrUserIdAndDescriptionContainingIgnoreCase(
            Long userId, String keyword,
            Long userId1, String keyword1,
            Pageable pageable);

    Page<Ticket> findByUserIdAndCategoryName(
            Long userId,
            String categoryName,
            Pageable pageable);

    Page<Ticket> findByUserIdAndStatus(
            Long userId,
            Status status,
            Pageable pageable);

    Page<Ticket> findByUserIdAndCategoryNameAndStatus(
            Long userId,
            String categoryName,
            Status status,
            Pageable pageable);

    Page<Ticket> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String keyword,
            String keyword1,
            Pageable pageable);

    Page<Ticket> findByStatus(
            Status status,
            Pageable pageable);

    Page<Ticket> findByCategoryName(
            String categoryName,
            Pageable pageable);

    Page<Ticket> findByCategoryNameAndStatus(
            String categoryName,
            Status status,
            Pageable pageable);
}

