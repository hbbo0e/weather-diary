package zerobase.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

@RestController
public class DiaryController {
  private final DiaryService diaryService;

  public DiaryController(DiaryService diaryService) {
    this.diaryService = diaryService;
  }

  @ApiOperation(value = "일기 텍스트와 날씨를 이용해 DB에 일기 저장", notes = "notes")
  @PostMapping("/create/diary")
  void creatDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date, @RequestBody String text){
    diaryService.createDiary(date, text);
  }

  @ApiOperation(value = "선택한 날짜의 모든 일기 데이터 조회")
  @GetMapping("/read/diary")
  List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date){
    return diaryService.readDiary(date);
  }

  @ApiOperation(value = "선택한 기간 중의 모든 일기 데이터 조회")
  @GetMapping("/read/diaries")
  List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "조회할 기간의 첫 날", example = "2025-09-06") LocalDate startDate,
                          @RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "조회할 기간의 마지막 날", example = "2025-09-06") LocalDate endDate){
    return diaryService.readDiaries(startDate, endDate);
  }

  @ApiOperation(value = "선택한 날짜의 일기 중 첫 항목 수정")
  @PutMapping("/update/diary")
  void updateDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date, @RequestBody String text){
    diaryService.updateDiary(date, text);
  }

  @ApiOperation(value = "선택한 날짜의 일기 삭제")
  @DeleteMapping("/delete/diary")
  void deleteDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date){
    diaryService.deleteDiary(date);
  }
}
