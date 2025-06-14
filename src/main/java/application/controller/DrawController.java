package application.controller;

import application.model.Draw;
import application.model.Ticket;
import application.repository.DrawRepository;
import application.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/draws")
public class DrawController {

    @Autowired
    private DrawRepository drawRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // Создать новый тираж
    @PostMapping()
    public ResponseEntity<?> createDraw() {
        try {
            Optional<Draw> activeDrawOpt = drawRepository.findByStatus("ACTIVE");
            if (activeDrawOpt.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Уже есть активный тираж"));
            }

            Draw newDraw = new Draw();
            drawRepository.save(newDraw);

            return ResponseEntity.ok(Map.of(
                    "draw_id", newDraw.getId(),
                    "status", newDraw.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Ошибка при создании тиража"));
        }
    }

    // Завершить тираж и определить победителей
    @PostMapping("/{drawId}/close")
    public ResponseEntity<?> closeDraw(@PathVariable Long drawId) {
        try {
            Optional<Draw> drawOpt = drawRepository.findById(drawId);
            if (drawOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Draw draw = drawOpt.get();
            if ("CLOSED".equals(draw.getStatus())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Тираж уже закрыт"));
            }

            // Генерация 5 уникальных чисел от 1 до 36
            List<Integer> winningNumbers = new Random()
                    .ints(1, 37)
                    .distinct()
                    .limit(5)
                    .boxed()
                    .sorted()
                    .collect(Collectors.toList());

            // Сохранение выигрышной комбинации
            String winningNumbersStr = winningNumbers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            draw.setWinningNumbers(winningNumbersStr);
            draw.setStatus("CLOSED");
            drawRepository.save(draw);

            // Поиск победителей
            List<Ticket> tickets = ticketRepository.findByDraw(draw);
            List<Long> winners = tickets.stream()
                    .filter(ticket -> {
                        Set<String> ticketNumbers = new HashSet<>(
                                Arrays.asList(ticket.getNumbers().split(","))
                        );
                        return ticketNumbers.containsAll(
                                winningNumbers.stream()
                                        .map(String::valueOf)
                                        .collect(Collectors.toSet())
                        );
                    })
                    .map(Ticket::getId)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "draw_id", draw.getId(),
                    "winning_numbers", winningNumbers,
                    "winners_count", winners.size(),
                    "winners_ids", winners
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Ошибка при закрытии тиража"));
        }
    }

    // Получить результаты тиража
    @GetMapping("/{drawId}/results")
    public ResponseEntity<?> getDrawResults(@PathVariable Long drawId) {
        try {
            Optional<Draw> drawOpt = drawRepository.findById(drawId);
            if (drawOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Draw draw = drawOpt.get();
            List<Ticket> tickets = ticketRepository.findByDraw(draw);

            return ResponseEntity.ok(Map.of(
                    "draw_id", draw.getId(),
                    "status", draw.getStatus(),
                    "winning_numbers", draw.getWinningNumbers() != null ?
                            Arrays.asList(draw.getWinningNumbers().split(",")) :
                            Collections.emptyList(),
                    "tickets", tickets.stream()
                            .map(t -> Map.of(
                                    "ticket_id", t.getId(),
                                    "numbers", Arrays.asList(t.getNumbers().split(","))
                            ))
                            .collect(Collectors.toList())
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Ошибка при получении результатов"));
        }
    }
}
