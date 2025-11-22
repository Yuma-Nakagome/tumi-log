# tumi-log

このリポジトリは `tumi-log`（Spring Boot）アプリケーションです。

## 開発環境（推奨）
- Java: 21 (LTS)
- Maven: プロジェクト付属の `./mvnw` を利用

### macOS (Homebrew) での JDK21 インストール
```bash
brew update
brew install openjdk@21
# 推奨: シェル設定に追加（zsh の場合）
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
echo 'export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"' >> ~/.zshrc
source ~/.zshrc
java -version
```

## ビルドとテスト
プロジェクトルートで（ラッパーを利用）:
```bash
./mvnw -DskipTests package   # パッケージ作成（テストをスキップ）
./mvnw test                  # テスト実行
```

## GitHub と CI
- このリポジトリは Java 21 をターゲットにしています。GitHub Actions のワークフローを追加済みです（`.github/workflows/ci.yml`）。
- リモートリポジトリ（GitHub）へ push したい場合は、新しいリポジトリを GitHub 上で作成して URL を教えてください。こちらで `git remote add` と `git push -u origin upgrade/java-21` を行います。

## 追加メモ
- CI のランナーも Java 21 を使用するように設定済みです。

