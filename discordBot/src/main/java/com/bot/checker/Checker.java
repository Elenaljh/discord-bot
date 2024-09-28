package com.bot.checker;

import com.bot.DiscordBot;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.logging.Logger;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;

public class Checker {

  private static final Logger logger = Logger.getLogger(Checker.class.getName());

  public boolean checkForImage(Message message) {
    if (message.getAttachments().isEmpty()) {
      logger.info("첨부파일 없음");
      return false;
    }

    List<Attachment> attachments = message.getAttachments();

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
}
