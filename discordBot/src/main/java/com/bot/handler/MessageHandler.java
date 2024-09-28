package com.bot.handler;

import net.dv8tion.jda.api.entities.Message;

public interface MessageHandler {
  void handleMessage(Message message);
}
