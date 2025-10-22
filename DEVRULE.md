ChatNCHU 開發規範
===

git 提交規範
---

### summary

summary 第一個字符使用 [git emoji](https://gitmoji.dev/)，盡可能挑選與本次提交的變更符合之 emoji，例：

1. :sparkles: 新增功能
2. :bug: 修正 bug
3. :recycle: 程式碼重構
4. :fire: 刪除檔案
5. :zap: 性能優化
6. :construction: 正在施工
7. :page_facing_up: 文件修改

其餘符號請自行查閱 [git emoji](https://gitmoji.dev/) 網站。除 emoji 外，後續簡短說明本次提交對程式碼的變更內容，emoji 與說明之間需空一格。

簡短說明部分限制使用英文。

### description

description 為非必寫項目，若 summary 無法精確表達本次變更時需要填寫 description，格式無要求但需有意義，不限中英文但建議中文。

### 提交注意事項

每次提交一個版本盡量只提交一個功能變更並填寫其對應的 summary 及 description，若同時有許多功能因相依性需一起提交，請在 description 中說明每個提交的功能。

### Branch

前端、後端、crawler、LLM 可自建一個 Branch 用於各自的版本控管，但非必要無需使用額外分支，否則只會造成版本混亂，需確保 main 分支永遠為可正常執行的狀態。

product會有另外的 Branch 方案。

版本號規範
---

使用 [Semanitc Versioning](https://semver.org/) 版本號規範，格式定義如下：

```
Major.Minor.Patch
```

* Major 變更代表與舊版本不相容，具有 breaking change，如 api 路由變更，原路由會無法使用。
* Minor 變更代表可與舊版相容的變更，不具有 breaking change，如新增一個 api 路由，不影響舊版路由的使用。
* Patch 變更代表僅做修正的變更，如發現某功能有問題，對其進行修正時。

Coding Style
---

暫無規定、但要確保能執行

API Document
---

統一使用 [apifox](https://apifox.com/)