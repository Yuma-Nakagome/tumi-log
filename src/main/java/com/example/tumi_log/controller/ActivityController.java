package com.example.tumi_log.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import com.example.tumi_log.dto.ActivityDto;
import com.example.tumi_log.service.ActivityService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    public final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    // @Validated: この引数（DTO）の入力値に対してバリデーション（検証）を実行するようSpringに指示する。
    // UserRegistrationDto: ユーザーがフォームなどから入力したデータを受け取るためのオブジェクト（DTO）。
    // BindingResult: 直前の @Validated で実行された検証の結果（エラーの有無、エラーの詳細）を格納するオブジェクト。
    // このオブジェクトでエラーをチェックし、エラーがあればユーザーに入力画面を再表示するなどの処理を行う。
    // Model: コントローラで処理した結果（データ）を、最終的にユーザーに見せるビュー（HTMLテンプレートなど）に渡すためのオブジェクト。
    // addAttribute() メソッドを使ってデータをビューに渡す。
    @PostMapping
    public ResponseEntity<ActivityDto> registerActivity(@RequestBody @Validated ActivityDto activityDto) {
        ActivityDto registerActivity = activityService.createActivity(activityDto);
        // HTTPステータスコード 201 Created と共に、作成されたオブジェクトをJSONで返却
        return ResponseEntity.status(HttpStatus.CREATED).body(registerActivity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityDto> putActivity(@RequestBody @Validated ActivityDto activityDto,
            @PathVariable Long id) {
        // idを引数に追加することも忘れないように（どのリソースを更新するか特定するため）
        ActivityDto putActivity = activityService.updateActivity(activityDto, id);
        return ResponseEntity.ok(putActivity);
    }

    @GetMapping("/user/{userid}")
    // @PathVaria URLパスの一部を変数として取り出す
    public ResponseEntity<List<ActivityDto>> getActivitiesForUser(@PathVariable Long userid) {
        List<ActivityDto> foundActivities = activityService.getActivitiesByUserId(userid);
        return ResponseEntity.status(HttpStatus.OK).body(foundActivities);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deletedActivity(id);
        return ResponseEntity.noContent().build();
    }

}
