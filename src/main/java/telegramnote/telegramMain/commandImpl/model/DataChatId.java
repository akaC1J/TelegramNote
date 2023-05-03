package telegramnote.telegramMain.commandImpl.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataChatId<T> {
    private Map<Long, T> dataMap = new ConcurrentHashMap<>();
    private final T DEFAULT_VALUE;

    public DataChatId(T defaultValue) {
        DEFAULT_VALUE = defaultValue;
    }

    public DataChatId() {
        DEFAULT_VALUE = null;
    }

    public void setData(Long chatId, T data) {
        if (data == null) {
            dataMap.remove(chatId);
            return;
        }
        dataMap.put(chatId, data);
    }


    public T getData(Long chatId) {
        T value = dataMap.get(chatId);
        return value == null ? DEFAULT_VALUE : value;
    }
}
