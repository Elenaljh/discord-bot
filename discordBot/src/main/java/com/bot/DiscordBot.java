package com.bot;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

public class DiscordBot extends ListenerAdapter {
  private static final String TOKEN = Dotenv.load().get("DISCORD_TOKEN");
  private static final Logger logger = Logger.getLogger(DiscordBot.class.getName());

  private GoogleSheetsHelper sheetsHelper;

  public DiscordBot() throws IOException {
    this.sheetsHelper = new GoogleSheetsHelper();
  }

  public static void main(String[] args) {
    try {
      JDA build = JDABuilder.createDefault(TOKEN)
          .addEventListeners(new DiscordBot())
          .enableIntents(GatewayIntent.MESSAGE_CONTENT)
          .build();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void onMessageReceived(@NotNull MessageReceivedEvent event) {
    logger.info("메세지 받음");
    Message message = event.getMessage();
    String nickname = message.getMember() != null ? message.getMember().getEffectiveName() : "Unknown User";


    //이미지 유무 검사
    if (!checkForImage(message)) return;
    logger.info("이미지 유무 검사 통과");

    //시간대 검사
//    if (!checkForTime(message)) return;
//    logger.info("시간대 검사 통과");

    //행열 검색
    try {
      logger.info("닉네임으로 열 찾아보자 -> nickname: " + nickname);
      String column = sheetsHelper.findColumnByNickname(nickname);
      logger.info("날짜로 행 찾아보자");
      int row = sheetsHelper.findRowByDate(getDate(message));
      if (column != null && row != -1) {
        String cell = column + row;
        sheetsHelper.write(cell, true);
        logger.info("출석부 등록 완료");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }


  }

  public boolean checkForImage(Message message) {
    if (message.getAttachments().isEmpty()) {
      logger.info("첨부파일 없음");
      return false;
    }

    List<Message.Attachment> attachments = message.getAttachments();

    // 일단 이미지 유무 여부만 검사한다.
    boolean isImage = false;
    for (Message.Attachment item : attachments) {
      if (item.isImage()) {
        isImage = true;
      }
    }
    return isImage;
  }

  public boolean checkForTime(Message message) {
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    ZonedDateTime yesterday = now.minusDays(1L);
    logger.info("now: "+now+", yesterday: "+yesterday);

    OffsetDateTime timeCreated = message.getTimeCreated();
    ZonedDateTime transformedTime = timeCreated.atZoneSameInstant(ZoneId.of("Asia/Seoul"));
    logger.info("createdTime: "+transformedTime);

    return !transformedTime.isBefore(yesterday) && !transformedTime.isAfter(now);
  }

  public String getDate(Message message) {
    ZonedDateTime createdTime = message.getTimeCreated().atZoneSameInstant(ZoneId.of("Asia/Seoul"));
    ZonedDateTime aDayBefore = createdTime.minusDays(1L);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. M. d");
    int hour = createdTime.getHour();
    if (hour > 3) {
      return createdTime.format(formatter);
    } else {
      return aDayBefore.format(formatter);
    }
  }


}
