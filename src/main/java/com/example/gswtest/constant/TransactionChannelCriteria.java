package com.example.gswtest.constant;

import com.example.gswtest.exception.InvalidInputException;

public enum TransactionChannelCriteria {
    CLIENT("client"),
    ATM("atm"),
    INTERNAL("internal");

    private final String channel;

    TransactionChannelCriteria(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

    public static TransactionChannelCriteria getByChannel(String channel) throws InvalidInputException, NullPointerException{
        for(TransactionChannelCriteria channelCriteria: TransactionChannelCriteria.values()){
            if(channelCriteria.getChannel().equals(channel.toLowerCase())) {
                return channelCriteria;
            }
        }
        throw new InvalidInputException(String.format("Provided channel criteria '%s' does not exist", channel));
    }
}
