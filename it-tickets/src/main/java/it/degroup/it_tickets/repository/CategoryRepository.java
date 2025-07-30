package it.degroup.it_tickets.repository;

import it.degroup.it_tickets.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
