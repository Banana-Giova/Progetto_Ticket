package it.degroup.it_tickets.service.Category;

import it.degroup.it_tickets.entity.Category;
import it.degroup.it_tickets.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    CategoryRepository repository;

    public List<Category> getAllCategories() {
        return repository.findAll();
    }
}
