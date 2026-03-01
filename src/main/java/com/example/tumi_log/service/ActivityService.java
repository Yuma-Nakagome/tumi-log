package com.example.tumi_log.service;

import java.util.stream.Collectors;
import java.util.List;
import org.springframework.stereotype.Service;

import com.example.tumi_log.dto.ActivityDto;
import com.example.tumi_log.entity.Activity;
import com.example.tumi_log.entity.User;
import com.example.tumi_log.repository.ActivityRepository;
import com.example.tumi_log.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityService {
    // 【インスタンス】: ActivityRepositoryという「設計図」から作られた「実体」への参照を保持するフィールド
    // SpringがこのフィールドにActivityRepositoryのインスタンスを注入する
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    /**
     * 【コンストラクタ】: ActivityServiceクラスのインスタンスを「構築・初期化」するための【メソッド】ブロック全体
     * 
     * @param activityRepository 外部から注入されるActivityRepositoryの【インスタンス】（実体）
     */
    public ActivityService(ActivityRepository activityRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    /**
     * 【メソッド】: ActivityServiceのインスタンスが持つ、特定の処理（アクティビティ作成）を実行する通常の関数ブロック全体
     */
    @Transactional
    public ActivityDto createActivity(ActivityDto activityDto) {
        // 【コンストラクタの呼び出し】: new Activity() により、Activityクラスのコンストラクタが呼ばれ、インスタンスが生まれる
        // 【インスタンス】: Activityクラスの「設計図」から作られた「実体」（新しいアクティビティデータ）
        // ユーザーIDを取得
        User defaultUser = userRepository.findById(activityDto.getUserId())
                .orElseThrow(() -> new IllegalStateException("デフォルトユーザーが見つかりません。先にユーザー登録が必要です。"));
        Activity newActivity = convertToEntity(activityDto, defaultUser);// 2. DTOからエンティティへの変換
        newActivity.setArchive(false);
        // 3. データベースへの保存
        Activity savedActivity = activityRepository.save(newActivity);
        // 3. EntityからDTOへの変換（Controllerへの戻り値）
        return convertToDto(savedActivity); // DTOを返却
    }
    // 役割：メソッド内のDB操作を一連の作業として扱い、成功でコミット、失敗でロールバック（取り消し）する

    @Transactional(readOnly = true)
    public List<ActivityDto> getAllActivities(Long userId) {
        // 1. RepositoryからEntityのリストを取得 (ActivityDtoは使わず、userIdを直接渡す)
        List<Activity> foundAllActivies = activityRepository.findByUserId(userId);
        // 2. EntityのリストをDTOのリストに変換 (Stream APIを使用)
        return foundAllActivies.stream()
                // activity はリスト内の Activity エンティティ1つ1つ
                // プロの現場でよく見る書き方（メソッド参照）
                .map(this::convertToDto)
                .collect(Collectors.toList());
        // リストが空なら、空のList<ActivityDto>を返す
    }

    @Transactional(readOnly = true)
    public List<ActivityDto> getActiveActivities(Long userId) {
        // 1. RepositoryからEntityのリストを取得 (ActivityDtoは使わず、userIdを直接渡す)
        List<Activity> foundAllActivies = activityRepository.findByUserIdAndArchiveFalse(userId);
        // 2. EntityのリストをDTOのリストに変換 (Stream APIを使用)
        return foundAllActivies.stream()
                // activity はリスト内の Activity エンティティ1つ1つ
                .map(this::convertToDto)
                .collect(Collectors.toList());
        // リストが空なら、空のList<ActivityDto>を返す
    }

    @Transactional
    public ActivityDto updateActivity(ActivityDto activityDto, Long id) {

        Activity updatedActivity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("アクティビティが見つかりません。ID: " + id));

        updatedActivity.setTitle(activityDto.getTitle());
        updatedActivity.setDisplayStyle(activityDto.getDisplayStyle());
        // 3. データベースへの保存
        Activity savedActivity = activityRepository.save(updatedActivity);
        return convertToDto(savedActivity); // DTOを返却
    }

    @Transactional
    public ActivityDto toggleArchive(Long id, boolean isArchive) {
        Activity activity = activityRepository.findById(id).orElseThrow();
        activity.setArchive(isArchive); // 状態を書き換える（自動保存の対象になる）
        // repository.save(activity); // 書いても書かなくても、メソッド終了時に更新される
        return convertToDto(activity);
    }

    private ActivityDto convertToDto(Activity activity) {
        ActivityDto dto = new ActivityDto();
        dto.setId(activity.getId());
        dto.setTitle(activity.getTitle());
        dto.setDisplayStyle(activity.getDisplayStyle());
        dto.setUserId(activity.getUser().getId());
        dto.setArchive(activity.isArchive());
        return dto;
    }

    private Activity convertToEntity(ActivityDto activityDto, User defaultUser) {
        Activity activity = new Activity();
        activity.setTitle(activityDto.getTitle());
        activity.setDisplayStyle(activityDto.getDisplayStyle());
        activity.setUser(defaultUser);
        activity.setArchive(activityDto.isArchive());
        return activity;
    }
}
