package com.example.tumi_log.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import java.util.List;

import com.example.tumi_log.dto.ActivityDto;
import com.example.tumi_log.service.ActivityService;
import com.example.tumi_log.service.CustomUserDetails;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping
    public ResponseEntity<List<ActivityDto>> getActivities(@AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "false") boolean includeArchived) {
        Long userId = principal.getId();
        // 窓口は1つだが、中身のロジックをここで切り替える
        List<ActivityDto> foundActivities = includeArchived
                ? activityService.getAllActivities(userId) // includeArchived=true の場合、非表示のアクティビティも含める
                : activityService.getActiveActivities(userId); // includeArchived=false
                                                               // の場合、非表示のアクティビティは除外する
        return ResponseEntity.status(HttpStatus.OK).body(foundActivities);
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<ActivityDto> updateArchiveStatus(@PathVariable Long id, @RequestParam boolean status) {
        ActivityDto updatedDto = activityService.toggleArchive(id, status);
        return ResponseEntity.ok(updatedDto);
    }

}
