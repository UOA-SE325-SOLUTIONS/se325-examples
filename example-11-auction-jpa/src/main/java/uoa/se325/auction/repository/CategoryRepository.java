package uoa.se325.auction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uoa.se325.auction.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
