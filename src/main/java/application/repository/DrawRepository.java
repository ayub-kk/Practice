package application.repository;

import application.model.Draw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface DrawRepository extends JpaRepository<Draw, Long> {
    Optional<Draw> findByStatus(String status);
}
