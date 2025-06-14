package application.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "draw_id", nullable = false)
    private Draw draw;

    private String numbers;

    public Ticket() {}

    public Ticket(Draw draw, List<Integer> numbersList) {
        if (draw == null || numbersList == null) {
            throw new IllegalArgumentException("Draw and numbers cannot be null");
        }
        this.draw = draw;
        this.numbers = numbersList.toString()
                .replace("[", "")
                .replace("]", "")
                .replace(" ", "");
    }

    // Геттеры и сеттеры остаются без изменений
    public Long getId() {
        return id;
    }

    public Draw getDraw() {
        return draw;
    }

    public void setDraw(Draw draw) {
        this.draw = draw;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }
}
