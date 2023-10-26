package com.simulation.fifa.api.user.dto.match;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserMatchRequestDto {
    private Integer matchType;
    private Integer offset;
    private Integer limit;
    private Integer page;
}
