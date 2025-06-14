package application.controller;

import application.model.Draw;
import application.model.Ticket;
import application.repository.DrawRepository;
import application.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private DrawRepository drawRepository;

    @PostMapping
    public ResponseEntity<?> buyTicket(@RequestBody Map<String, Object> payload) {
        try {
            Object numbersObj = payload.get("numbers");
            Object drawIdObj = payload.get("draw_id");

            if (!(numbersObj instanceof List<?>) || !(drawIdObj instanceof Number)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Некорректные данные"));
            }

            @SuppressWarnings("unchecked")
            List<Integer> numbersList = (List<Integer>) numbersObj;

            if (numbersList == null || numbersList.size() != 5) {
                return ResponseEntity.badRequest().body(Map.of("error", "Должно быть ровно 5 чисел"));
            }

            Set<Integer> uniqueNums = new HashSet<>(numbersList);
            if (uniqueNums.size() != 5) {
                return ResponseEntity.badRequest().body(Map.of("error", "Числа должны быть уникальными"));
            }

            for (Integer num : numbersList) {
                if (num == null || num < 1 || num > 36) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Числа должны быть в диапазоне от 1 до 36"));
                }
            }

            Long drawId = ((Number) drawIdObj).longValue();
            Optional<Draw> drawOpt = drawRepository.findById(drawId);
            if (!drawOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Тираж не найден"));
            }

            Draw draw = drawOpt.get();
            if ("CLOSED".equals(draw.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Тираж уже закрыт"));
            }

            Ticket ticket = new Ticket(draw, numbersList);
            ticketRepository.save(ticket);

            return ResponseEntity.ok(Map.of(
                    "ticket_id", ticket.getId(),
                    "numbers", numbersList,
                    "draw_id", draw.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Ошибка при создании билета"));
        }
    }
}