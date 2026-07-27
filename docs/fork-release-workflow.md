# Fork 的 GitHub Release 發布流程

本 fork 有兩套彼此獨立的自動發布流程。兩套流程都會建置並簽署主程式與所有
plugin APK，再把整理、重新命名後的 APK 同時存入 GitHub Release 與 GitHub
Actions Artifact。Artifact 可供建置失敗調查或比對使用。

## Development Release

每次 commit push（包含 pull request merge）到預設分支 `master` 時，
`dev-release.yml` 都會執行。也可以在 GitHub 的 **Actions → Development Release →
Run workflow** 手動執行。

流程會把 `dev-latest` tag 強制移到該次的 `master` commit，並建立或更新唯一一筆
標題為 **Development Build** 的 prerelease。舊 APK assets 會先移除再由新建置取代，
所以不會因每次 push 而累積 development releases。Release notes 會記錄 commit SHA、
commit message 與 UTC build time。這是自動產生的測試版，不保證穩定。

## 正式 Tagged Release

正式版只接受以 `v` 開頭的既有 Git tag。以下範例會建立並推送第一個 tag：

```bash
git switch master
git pull --ff-only origin master
git tag v0.1.3-zhuyin.1
git push origin v0.1.3-zhuyin.1
```

推送後，`tagged-release.yml` 會以 tag 名稱作為 Release title，建立非 prerelease 的
正式 GitHub Release，並要求 GitHub 自動產生 release notes。也可以手動執行 workflow
並輸入一個已經推送到 GitHub 的 `v*` tag。若該 tag 的 Release 已存在，流程會明確
失敗，不會覆蓋既有正式版內容。

`dev-release.yml` 只監聽 `master` branch push；`tagged-release.yml` 只監聽 `v*` tag
push。因此推送正式版 tag 不會更新 `dev-latest`，而 workflow 推送 `dev-latest` 也不會
觸發任何一套發布流程。

## 建立簽章 keystore

請在安全的本機環境使用 JDK 的 `keytool` 建立專屬於此 fork 的 keystore。下列名稱均為
範例；執行時工具會安全地提示輸入密碼，請勿把密碼直接寫入 shell 指令或提交至 Git：

```bash
keytool -genkeypair \
  -keystore fcitx5-android-fork.jks \
  -alias fcitx5-android-fork \
  -keyalg RSA \
  -keysize 4096 \
  -validity 10000
```

妥善離線備份 keystore 與密碼；遺失後將無法以相同簽章更新已安裝的 fork APK。

### 轉換成單行 Base64

Linux 使用：

```bash
base64 -w 0 fcitx5-android-fork.jks > fcitx5-android-fork.jks.base64
```

macOS 使用：

```bash
base64 < fcitx5-android-fork.jks | tr -d '\n' > fcitx5-android-fork.jks.base64
```

請勿提交 `.jks`、`.base64` 或其內容。設定 secret 後，也應刪除不再需要的 Base64
暫存檔。

## 設定 GitHub Actions secrets

在 repository 的 **Settings → Secrets and variables → Actions → New repository secret**
新增以下三個 repository secrets：

| Secret | 內容 |
| --- | --- |
| `SIGN_KEY_BASE64` | keystore 檔案的完整單行 Base64 內容 |
| `SIGN_KEY_PWD` | keystore 密碼（同時作為 key password） |
| `SIGN_KEY_ALIAS` | 建立 keystore 時指定的 alias |

Workflow 使用 GitHub 內建的 `GITHUB_TOKEN` 建立 tag 與 Release，不需要 Personal Access
Token。請在 **Settings → Actions → General → Workflow permissions** 選擇 **Read and write
permissions**；workflow 本身也已宣告 `contents: write`。若組織 policy 強制唯讀，管理員
必須允許此 repository 的 Actions 寫入 repository contents。

## 安裝與簽章相容性

Android 只允許簽章相同的 APK直接更新既有應用程式。自行簽署的 fork APK 無法直接
更新官方簽署的 Fcitx5 Android；通常必須先解除安裝官方版（請先備份所需資料），或在
程式碼中另行修改 application ID，讓兩者成為不同的應用程式。
