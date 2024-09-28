package com.bot.handler;

import com.bot.DiscordBot;
import com.bot.checker.AttendanceChecker;
import com.bot.checker.Checker;
import com.bot.checker.TimeUtil;
import java.util.logging.Logger;
import net.dv8tion.jda.api.entities.Message;

public class MessageHandlerImpl implements MessageHandler{

  private final AttendanceChecker attendanceChecker;
  private final Checker checker;
  private final TimeUtil timeUtil;
  private static final Logger logger = Logger.getLogger(MessageHandlerImpl.class.getName());


  public MessageHandlerImpl(AttendanceChecker attendanceChecker) {
    this.attendanceChecker = attendanceChecker;
    this.checker = new Checker();
    this.timeUtil = new TimeUtil();
  }


  @Override
  public void handleMessage(Message message) {
    String nickname = message.getMember() != null ? message.getMember().getEffectiveName() : "Unknown User";

    if (!checker.checkForImage(message)) {
      logger.info("이미지 체크 실패");
      return;
    }

    attendanceChecker.checkAttendance(nickname, timeUtil.getDate(message));

  }
}
