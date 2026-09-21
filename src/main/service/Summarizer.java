package src.main.service;

import src.main.models.Message;
import java.util.List;

public interface Summarizer {

    String summarize(
            String existingSummary,
            List<Message> messages
    ) throws Exception;

}
