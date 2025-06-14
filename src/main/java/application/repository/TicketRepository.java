package application.repository;

import application.model.Draw;
import application.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByDraw(Draw draw);
}
