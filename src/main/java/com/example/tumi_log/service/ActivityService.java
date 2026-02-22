package com.example.tumi_log.service;

import java.util.stream.Collectors;
import java.util.List;
import org.springframework.stereotype.Service;

import com.example.tumi_log.dto.ActivityDto;
import com.example.tumi_log.entity.Activity;
import com.example.tumi_log.entity.User;
import com.example.tumi_log.repository.ActivityRepository;
import com.example.tumi_log.repository.UserRepository;

import jakarta.transaction.Transactional;

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
        // 2. DTOからエンティティへの変換
        Activity newActivity = new Activity();
        newActivity.setTitle(activityDto.getTitle());
        newActivity.setDisplayStyle(activityDto.getDisplayStyle());
        newActivity.setArchive(false);
        // ユーザーIDを取得
        User defaultUser = userRepository.findById(activityDto.getUserId())
                .orElseThrow(() -> new IllegalStateException("デフォルトユーザーが見つかりません。先にユーザー登録が必要です。"));
        newActivity.setUser(defaultUser);
        // 3. データベースへの保存
        Activity savedActivity = activityRepository.save(newActivity);
        // 3. EntityからDTOへの変換（Controllerへの戻り値）
        ActivityDto createdActivityDto = new ActivityDto();
        createdActivityDto.setId(savedActivity.getId());
        createdActivityDto.setUserId(savedActivity.getUser().getId());
        createdActivityDto.setTitle(savedActivity.getTitle());
        createdActivityDto.setDisplayStyle(savedActivity.getDisplayStyle());
        createdActivityDto.setArchive(savedActivity.isArchive());

        return createdActivityDto; // DTOを返却
    }
    // 役割：メソッド内のDB操作を一連の作業として扱い、成功でコミット、失敗でロールバック（取り消し）する

    @Transactional
    public List<ActivityDto> getActivitiesByUserId(Long userId) {
        // 1. RepositoryからEntityのリストを取得 (ActivityDtoは使わず、userIdを直接渡す)
        List<Activity> foundAllActivies = activityRepository.findByUserIdAndArchiveFalse(userId);
        // 2. EntityのリストをDTOのリストに変換 (Stream APIを使用)
        return foundAllActivies.stream()
                // activity はリスト内の Activity エンティティ1つ1つ
                .map(activity -> {
                    // 2-1. 新しいDTOを作成
                    ActivityDto dto = new ActivityDto();
                    // 2-2. EntityからDTOへ値をコピー
                    dto.setId(activity.getId());
                    dto.setTitle(activity.getTitle());
                    dto.setDisplayStyle(activity.getDisplayStyle());
                    // UserエンティティからIDを取り出してセット
                    dto.setUserId(activity.getUser().getId());
                    dto.setArchive(activity.isArchive());
                    return dto;
                })
                .collect(Collectors.toList());
        // リストが空なら、空のList<ActivityDto>を返す
    }

    @Transactional
    public List<ActivityDto> getAllActivitiesIncludingArchived(Long userId) {
        // 1. RepositoryからEntityのリストを取得 (ActivityDtoは使わず、userIdを直接渡す)
        List<Activity> foundAllActivies = activityRepository.findByUserId(userId);
        // 2. EntityのリストをDTOのリストに変換 (Stream APIを使用)
        return foundAllActivies.stream()
                // activity はリスト内の Activity エンティティ1つ1つ
                .map(activity -> {
                    // 2-1. 新しいDTOを作成
                    ActivityDto dto = new ActivityDto();
                    // 2-2. EntityからDTOへ値をコピー
                    dto.setId(activity.getId());
                    dto.setTitle(activity.getTitle());
                    dto.setDisplayStyle(activity.getDisplayStyle());
                    // UserエンティティからIDを取り出してセット
                    dto.setUserId(activity.getUser().getId());
                    dto.setArchive(activity.isArchive());
                    return dto;
                })
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
        // 3. EntityからDTOへの変換（Controllerへの戻り値）
        ActivityDto updatedActivityDto = new ActivityDto();
        updatedActivityDto.setId(savedActivity.getId());
        updatedActivityDto.setUserId(savedActivity.getUser().getId());
        updatedActivityDto.setTitle(savedActivity.getTitle());
        updatedActivityDto.setDisplayStyle(savedActivity.getDisplayStyle());

        return updatedActivityDto; // DTOを返却
    }

    @Transactional
    public void deletedActivity(Long id) {
        activityRepository.deleteById(id);
    }

}
