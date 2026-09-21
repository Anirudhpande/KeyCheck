package src.main.service;

import src.main.models.Message;

import java.util.List;

public interface TokenEstimator {

    long estimate(
            String model,
            List<Message> messages
    ) throws Exception;
}