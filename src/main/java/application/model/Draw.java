package application.model;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "draws")
public class Draw {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Статус: активный или закрыт
    @Column(nullable = false)
    private String status; // "ACTIVE" или "CLOSED"

    // Победные номера, хранятся как строка через запятую
    private String winningNumbers;

    // Конструкторы, геттеры и сеттеры

    public Draw() {
        this.status = "ACTIVE";
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWinningNumbers() {
        return winningNumbers;
    }

    public void setWinningNumbers(String winningNumbers) {
        this.winningNumbers = winningNumbers;
    }
}
