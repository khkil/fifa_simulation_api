package com.simulation.fifa.api.price.entity;

import com.simulation.fifa.api.player.entity.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"player_id", "upgradeValue", "date"}
        )
})
public class PlayerPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private Long price;

    private Integer upgradeValue;

    private LocalDate date;

    @CreatedDate
    private LocalDateTime createAt;

    @ManyToOne
    Player player;
}
