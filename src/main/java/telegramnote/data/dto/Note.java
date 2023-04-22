package telegramnote.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Note {
    private Long id;
    private String label;
    private User_ user;
    private Long chatId;
    private String text;
}
