package telegramnote.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User_ {
    private Long chatId;
    private String name;
}
