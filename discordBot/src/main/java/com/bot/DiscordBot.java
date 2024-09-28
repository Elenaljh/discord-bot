package com.bot;

import com.bot.checker.AttendanceChecker;
import com.bot.checker.AttendanceCheckerImpl;
import com.bot.googleSheets.GoogleSheetsHelper;
import com.bot.handler.MessageHandler;
import com.bot.handler.MessageHandlerImpl;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.util.logging.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

public class DiscordBot extends ListenerAdapter {
  private static final String TOKEN = Dotenv.load().get("DISCORD_TOKEN");
  private static final Logger logger = Logger.getLogger(DiscordBot.class.getName());

  private final MessageHandler messageHandler;

  public DiscordBot(MessageHandler messageHandler) {
    this.messageHandler = messageHandler;
  }

  public static void main(String[] args) {
    try {
      AttendanceChecker attendanceChecker = new AttendanceCheckerImpl(new GoogleSheetsHelper());
      MessageHandler messageHandler = new MessageHandlerImpl(attendanceChecker);

      JDA build = JDABuilder.createDefault(TOKEN)
          .addEventListeners(new DiscordBot(messageHandler))
          .enableIntents(GatewayIntent.MESSAGE_CONTENT)
          .build();
    } catch (IOException e) {
      logger.severe("디스코드봇 초기화 오류 발생: " + e.getMessage());
    }

  }

  @Override
  public void onMessageReceived(@NotNull MessageReceivedEvent event) {
    logger.info("메세지 도착");
    messageHandler.handleMessage(event.getMessage());
  }

}
