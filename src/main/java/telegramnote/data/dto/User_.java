package telegramnote.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User_ {
    private Long chatId;
    private String name;

    public User_ setChatId(Long chatId) {
        this.chatId = chatId;
        return this;
    }
}
